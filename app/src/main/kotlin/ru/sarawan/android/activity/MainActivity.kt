package ru.sarawan.android.activity

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.sarawan.android.MobileNavigationDirections
import ru.sarawan.android.R
import ru.sarawan.android.databinding.ActivityMainBinding
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Product
import ru.sarawan.android.model.data.ProductsItem
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.UNREGISTERED
import ru.sarawan.android.utils.exstentions.token
import ru.sarawan.android.utils.exstentions.userId
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), FabChanger, BasketSaver {

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

    private var isBackShown = false
    private var lastTimeBackPressed: Long = 0
    private val totalPrice: BehaviorSubject<Float> = BehaviorSubject.create()
    private var data: List<ProductsItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultSplashScreen()
        initNavigation()
        initFAB()
        initNetwork()
        viewModel.getStateLiveData().observe(this) { appState: AppState<*> -> updateFab(appState) }
    }

    private fun initNetwork() {
        networkStatus
            .isOnline()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe { viewModel.getBasket(!sharedPreferences.token.isNullOrEmpty()) }
    }

    private fun updateFab(appState: AppState<*>) {
        if (appState is AppState.Success<*>) {
            if (appState.data is List<*>) {
                if (appState.data.isNotEmpty()) {
                    if (appState.data.first() is ProductsItem) {
                        @Suppress("UNCHECKED_CAST")
                        data = appState.data as List<ProductsItem>
                        val price = data.sumOf {
                            var result = 0.0
                            it.quantity?.let { quantity ->
                                it.basketProduct?.price?.let { price ->
                                    result += (price.toDouble() * quantity)
                                }
                            }
                            result
                        }.toFloat()
                        putPrice(price)
                    } else throw RuntimeException("Wrong List type ${appState.data}")
                }
            } else throw RuntimeException("Wrong AppState type $appState")
        }
    }

    private fun initFAB() = with(binding) {
        fabPrice.setOnClickListener {
            findNavController(R.id.nav_fragment)
                .navigate(MobileNavigationDirections.actionGlobalToBasketFragment())
        }

        totalPrice.subscribe { price ->
            if (price.toInt() > 0) {
                "${price.toInt()} ₽".also { fabPrice.text = it }
                fabPrice.show()
            } else fabPrice.hide()
        }
    }

    private fun initNavigation() {
        val navView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_fragment)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {
            if (it.itemId == R.id.profileFragment) return@setOnItemSelectedListener showProfile()
            else {
                val action = when (it.itemId) {
                    R.id.mainFragment -> MobileNavigationDirections.actionGlobalToMainFragment()
                    R.id.catalogFragment -> MobileNavigationDirections.actionGlobalToCatalogFragment()
                    R.id.basketFragment -> MobileNavigationDirections.actionGlobalToBasketFragment()
                    R.id.infoFragment -> MobileNavigationDirections.actionGlobalToInfoFragment()
                    else -> MobileNavigationDirections.actionGlobalToMainFragment()
                }
                navController.navigate(action)
                true
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.basketFragment -> binding.fabPrice.hide()
                R.id.orderFragment -> binding.fabPrice.hide()
                else -> viewModel.getBasket(!sharedPreferences.token.isNullOrEmpty())
            }
        }
    }

    private fun showProfile(): Boolean =
        if (sharedPreferences.userId == UNREGISTERED) {
            val action = MobileNavigationDirections.actionGlobalToProfilePhoneDialogFragment()
            findNavController(R.id.nav_fragment).navigate(action)
            false
        } else {
            navigateToProfile()
            true
        }

    private fun navigateToProfile() = findNavController(R.id.nav_fragment)
        .navigate(MobileNavigationDirections.actionGlobalToProfileFragment())

    override fun onBackPressed() = with(findNavController(R.id.nav_fragment)) {
        when (currentDestination?.id) {
            R.id.mainFragment -> checkExit()
            R.id.productCardFragment -> super.onBackPressed()
            else -> {
                val destination = previousBackStackEntry?.destination?.id
                when {
                    destination == null -> {
                        super.onBackPressed()
                        return
                    }
                    binding.bottomNavigationView.menu.findItem(destination) != null -> {
                        binding.bottomNavigationView.selectedItemId = destination
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }

    private fun checkExit() {
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT)
            .show()
        if (System.currentTimeMillis() - lastTimeBackPressed < BACK_BUTTON_EXIT_DELAY && isBackShown) {
            exitProcess(0)
        } else isBackShown = false
        isBackShown = true
        lastTimeBackPressed = System.currentTimeMillis()
    }

    private fun setDefaultSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setSplashScreenHideAnimation()
        }

        setSplashScreenDuration()
    }

    @RequiresApi(31)
    private fun setSplashScreenHideAnimation() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideLeft = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_X,
                0f,
                splashScreenView.height.toFloat()
            )
            //slideLeft.interpolator = AnticipateInterpolator()
            slideLeft.duration = SLIDE_LEFT_DURATION

            slideLeft.doOnEnd { splashScreenView.remove() }
            slideLeft.start()
        }
    }

    private fun setSplashScreenDuration() {
        var isHideSplashScreen = false

        object : CountDownTimer(COUNTDOWN_DURATION, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isHideSplashScreen = true
            }
        }.start()

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isHideSplashScreen) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    override fun putPrice(price: Float) = totalPrice.onNext(price)

    override fun changePrice(price: Float) = totalPrice.onNext((totalPrice.value ?: 0f) + price)

    override fun changeState() = viewModel.getBasket(!sharedPreferences.token.isNullOrEmpty())

    override fun saveBasket() = if (sharedPreferences.userId != UNREGISTERED && data.isNotEmpty()) {
        val products = data.map { item ->
            Product(id = item.basketProduct?.basketProduct?.id, quantity = item.quantity ?: 0)
        }
        viewModel.saveData(products, true)
    } else Unit

    companion object {
        private const val BACK_BUTTON_EXIT_DELAY = 3000
        private const val SLIDE_LEFT_DURATION = 500L
        private const val COUNTDOWN_DURATION = 1150L
        private const val COUNTDOWN_INTERVAL = 750L
    }
}

interface FabChanger {
    fun putPrice(price: Float)
    fun changePrice(price: Float)
    fun changeState()
}

interface BasketSaver {
    fun saveBasket()
}