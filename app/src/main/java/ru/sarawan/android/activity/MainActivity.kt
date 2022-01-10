package ru.sarawan.android.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.sarawan.android.R
import ru.sarawan.android.app.App.Companion.navController
import ru.sarawan.android.databinding.ActivityMainBinding
import ru.sarawan.android.framework.ui.profile.phone_fragment.ProfilePhoneFragment
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.UNREGISTERED
import ru.sarawan.android.utils.exstentions.token
import ru.sarawan.android.utils.exstentions.userId
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), ru.sarawan.android.activity.FabChanger {

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

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var isOnline = false
    private var isBackShown = false
    private var lastTimeBackPressed: Long = 0
    private val totalPrice: BehaviorSubject<Float> = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        initFAB()
        initNetwork()
        viewModel.getStateLiveData().observe(this) { appState: AppState<*> ->
            updateFab(appState)
        }
    }

    private fun initNetwork() {
        networkStatus
            .isOnline()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe { isOnline = it }
    }

    private fun updateFab(appState: AppState<*>) {
        if (appState is AppState.Success<*>) {
            val data = appState.data as List<Float?>
            putPrice(data.first() ?: 0f)
        }
    }

    private fun initFAB() {
        binding.fabPrice.setOnClickListener { navController.navigate(R.id.basketFragment) }

        totalPrice.subscribe { price ->
            if (price.toInt() > 0) {
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
                else -> {
                    if (!navController.popBackStack(it.itemId, false))
                        navController.navigate(it.itemId)
                    true
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.basketFragment -> binding.fabPrice.hide()
                R.id.orderFragment -> binding.fabPrice.hide()
                else -> if (isOnline) viewModel.getBasket(!sharedPreferences.token.isNullOrEmpty())
            }
        }
    }

    private fun showProfile(): Boolean =
        if (sharedPreferences.userId == UNREGISTERED) {
            ProfilePhoneFragment.newInstance { navigateToProfile() }
                .show(supportFragmentManager, null)
            false
        } else {
            navigateToProfile()
            true
        }

    private fun navigateToProfile() {
        navController.navigate(R.id.profileFragment)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mainFragment) checkExit()
        else super.onBackPressed()
        lastTimeBackPressed = System.currentTimeMillis()
    }

    private fun checkExit() {
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT)
            .show()
        if (System.currentTimeMillis() - lastTimeBackPressed < ru.sarawan.android.activity.MainActivity.Companion.BACK_BUTTON_EXIT_DELAY && isBackShown) {
            exitProcess(0)
        } else isBackShown = false
        isBackShown = true
    }

    override fun putPrice(price: Float) = totalPrice.onNext(price)

    override fun changePrice(price: Float) = totalPrice.onNext((totalPrice.value ?: 0f) + price)

    override fun changeState() {
        if (isOnline) viewModel.getBasket(!sharedPreferences.token.isNullOrEmpty())
        else _binding?.fabPrice?.hide()
    }

    companion object {
        private const val BACK_BUTTON_EXIT_DELAY = 3000
    }
}

interface FabChanger {
    fun putPrice(price: Float)
    fun changePrice(price: Float)
    fun changeState()
}