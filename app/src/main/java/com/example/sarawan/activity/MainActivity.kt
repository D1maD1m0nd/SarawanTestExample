package com.example.sarawan.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.ActivityMainBinding
import com.example.sarawan.framework.ui.profile.phone_fragment.ProfilePhoneFragment
import com.example.sarawan.model.data.AppState
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.NetworkStatus
import com.example.sarawan.utils.exstentions.userId
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), FabChanger {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ActivityViewModel by lazy {
        viewModelFactory.create(ActivityViewModel::class.java)
    }

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
        initFAB()
        viewModel.getStateLiveData().observe(this) { appState: AppState<*> ->
            updateFab(appState)
        }
    }

    private fun updateFab(appState: AppState<*>) {
        if (appState is AppState.Success<*>) {
            val data = appState.data as List<Float?>
            putPrice(data.firstOrNull() ?: 0f)
        }
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
        val navView = binding.bottomNavigationView
        navController = findNavController(R.id.nav_fragment)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profileFragment -> return@setOnItemSelectedListener showProfile()
                R.id.basketFragment -> return@setOnItemSelectedListener showBasket()
            }
            navController.navigate(it.itemId)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.basketFragment) binding.fabPrice.hide()
            if (destination.id == R.id.orderFragment) binding.fabPrice.hide()
            else viewModel.getBasket()
        }
    }

    private fun showProfile(): Boolean =
        if (sharedPreferences.userId == -1L) {
            ProfilePhoneFragment.newInstance { navigateToProfile() }
                .show(supportFragmentManager, null)
            false
        } else {
            navigateToProfile()
            true
        }

    private fun showBasket(): Boolean =
        if (sharedPreferences.userId == -1L) {
            ProfilePhoneFragment.newInstance { navigateToProfile() }
                .show(supportFragmentManager, null)
            false
        } else {
            navigateToBasket()
            true
        }

    private fun navigateToProfile() {
        navController.navigate(R.id.profileFragment)
    }

    private fun navigateToBasket() {
        navController.navigate(R.id.basketFragment)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mainFragment) checkExit()
        else super.onBackPressed()
        lastTimeBackPressed = System.currentTimeMillis()
    }

    private fun checkExit() {
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show()
        if (System.currentTimeMillis() - lastTimeBackPressed < BACK_BUTTON_EXIT_DELAY && isBackShown) {
            exitProcess(0)
        } else isBackShown = false
        isBackShown = true
    }

    override fun putPrice(price: Float) {
        totalPrice.onNext(price)
    }

    override fun changePrice(price: Float) {
        totalPrice.onNext((totalPrice.value ?: 0f) + price)
    }

    companion object {
        private const val BACK_BUTTON_EXIT_DELAY = 3000
    }
}

interface FabChanger {
    fun putPrice(price: Float)
    fun changePrice(price: Float)
}