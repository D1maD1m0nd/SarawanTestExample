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
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    private lateinit var binding: ActivityMainBinding

    private var isBackShown = false
    private var lastTimeBackPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        observeOnlineStatus()
    }

    private fun initNavigation() {
        val navView: BottomNavigationView = binding.bottomNavigationView
        navController = findNavController(R.id.nav_fragment)
        navView.setupWithNavController(navController)
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

    companion object {

        private const val BACK_BUTTON_EXIT_DELAY = 3000
    }
}
