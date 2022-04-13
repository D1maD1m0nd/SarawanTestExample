package ru.sarawan.android.framework.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.BuildConfig
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentMapBinding
import ru.sarawan.android.framework.ui.map.viewModel.MapViewModel
import ru.sarawan.android.model.data.AppState
import javax.inject.Inject


class MapFragment : Fragment(), CameraListener, UserLocationObjectListener {

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>
    private val viewModel: MapViewModel by lazy {
        viewModelFactory.get().create(MapViewModel::class.java)
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var searchManager: SearchManager? = null
    private var userLocationLayer: UserLocationLayer? = null
    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {

        } else {
            Toast.makeText(context, getString(R.string.not_permission), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() {
        permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkPermission() {
        activity?.let {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        AndroidSupportInjection.inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapKitFactory.setApiKey(BuildConfig.MAP_API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        viewModel.getStateLiveData()
            .observe(viewLifecycleOwner) { appState: AppState<*> -> setState(appState) }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun moveCameraToPosition(target: Point?) {
        binding.mapview.map.move(
            CameraPosition(target!!, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2F), null
        )
    }

    private fun initView() = with(binding) {

        mapview.map.isRotateGesturesEnabled = false
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapview.mapWindow)
        userLocationLayer?.let {
            it.isVisible = true
            it.isHeadingEnabled = true

            it.setObjectListener(this@MapFragment)
        }

        fabPin.setOnClickListener {
            //moveCameraToPosition(userLocationLayer!!.cameraPosition()?.target)
            userLocationLayer?.let { userLocationLayer ->
                userLocationLayer.cameraPosition()?.let { camera ->
                    val lat = camera.target.latitude
                    val lon = camera.target.longitude
                    viewModel.getCoordinated(lat, lon)
                }
            }
        }

        mapview.map.move(
            CameraPosition(Point(55.7537090, 37.6198133), 14.0f, 0.0f, 0.0f)
        )

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onDestroyView() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onDestroyView()
    }


    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                when (val item = appState.data) {

                    else -> throw RuntimeException("Wrong AppState type $appState")
                }
            }

            is AppState.Error -> {
                val error = appState.error
                if (error is HttpException) {
                    Toast.makeText(context, error.response().toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else -> throw RuntimeException("Wrong AppState type $appState")
        }
    }


    override fun onObjectAdded(userLocationView: UserLocationView) = with(binding) {

        userLocationLayer?.setAnchor(
            PointF((mapview.width * 0.5).toFloat(), (mapview.height * 0.5).toFloat()),
            PointF((mapview.width * 0.5).toFloat(), (mapview.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context, R.drawable.user_arrow
            )
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(context, R.drawable.icon),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(context, R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        p3: Boolean
    ) {

    }
}


