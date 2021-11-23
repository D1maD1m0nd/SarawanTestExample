package com.example.sarawan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.ActivityMainBinding
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.NetworkStatus
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), FabChanger {

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    private lateinit var binding: ActivityMainBinding

    private var isBackShown = false
    private var lastTimeBackPressed: Long = 0
    private val totalPrice: BehaviorSubject<Float> = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        observeOnlineStatus()
        initFAB()
    }

    private fun initFAB() {
        binding.fabPrice.setOnClickListener {
            navController.navigate(R.id.basketFragment)
        }

        totalPrice.subscribe { price ->
            if (price > 0) {
                "${price.toInt()} ₽".also { binding.fabPrice.text = it }
                binding.fabPrice.show()
            } else binding.fabPrice.hide()
        }
    }

    private fun initNavigation() {
        val navView: BottomNavigationView = binding.bottomNavigationView
        navController = findNavController(R.id.nav_fragment)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {
            if (it.itemId == R.id.profileFragment) {
                Toast.makeText(this, "Switched to profile", Toast.LENGTH_SHORT).show()
                // тут можно вызвать попап при проверке сохранненго номера телефона или токена вместо навигации
                navController.navigate(it.itemId)
                // тут можно возвращать false чтобы не выделять отмеченный элемент
                return@setOnItemSelectedListener true
            }
            navController.navigate(it.itemId)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.basketFragment) binding.fabPrice.hide()
            else if (totalPrice.value != 0f) binding.fabPrice.show()
        }
    }

    private fun observeOnlineStatus() {
        networkStatus
            .isOnline()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe { isOnline ->
                val message = if (isOnline) "You now Online" else "You are Offline!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mainFragment) {
            checkExit()
        } else {
            super.onBackPressed()
        }
        lastTimeBackPressed = System.currentTimeMillis()
    }

    private fun checkExit() {
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()
        if (System.currentTimeMillis() - lastTimeBackPressed < BACK_BUTTON_EXIT_DELAY && isBackShown) {
            exitProcess(0)
        } else isBackShown = false
        isBackShown = true
    }

    override fun putPrice(price: Float) {
        totalPrice.onNext(price)
    }

    override fun changePrice(price: Float) {
        totalPrice.onNext(totalPrice.value + price)
    }

    companion object {

        private const val BACK_BUTTON_EXIT_DELAY = 3000
    }
}

interface FabChanger {
    fun putPrice(price: Float)
    fun changePrice(price: Float)
}