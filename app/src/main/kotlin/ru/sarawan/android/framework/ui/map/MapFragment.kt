package ru.sarawan.android.framework.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.BuildConfig
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentMapBinding
import ru.sarawan.android.framework.ui.map.viewModel.MapViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import javax.inject.Inject


class MapFragment : Fragment(), CameraListener, UserLocationObjectListener, Session.SearchListener,
    GeoObjectTapListener, InputListener {

    private var permissionLocation = false
    private var lastResponseAddress: AddressItem? = null

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>
    private val viewModel: MapViewModel by lazy {
        viewModelFactory.get().create(MapViewModel::class.java)
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private var userLocationLayer: UserLocationLayer? = null

    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            onMapReady()
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
                    onMapReady()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        checkPermission()
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
        mapview.map.move(
            CameraPosition(Point(55.7537090, 37.6198133), 14.0f, 0.0f, 0.0f)
        )
        searchTextInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchTextInput.text.toString())
            }
            false
        }
        mapview.map.addTapListener(this@MapFragment)
        mapview.map.addInputListener(this@MapFragment)
    }

    private fun onMapReady() {
        binding.fabPin.visibility = View.VISIBLE
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer?.let { userLocationLayer ->
            userLocationLayer.isVisible = true
            userLocationLayer.isHeadingEnabled = true
            userLocationLayer.setObjectListener(this)

            binding.mapview.map.addCameraListener(this)
            binding.fabPin.setOnClickListener {
                userLocationLayer.cameraPosition()?.let { camera ->
                    val lat = camera.target.latitude
                    val lon = camera.target.longitude
                    viewModel.getCoordinated(lat, lon)
                    moveCameraToPosition(camera.target)
                }
            }
        }
        permissionLocation = true
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
                    is AddressItem -> {
                        lastResponseAddress = item
                    }
                    else -> throw RuntimeException("Wrong AppState type $appState")
                }
            }
            is AppState.Loading -> {}
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
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            val textSearch = binding.searchTextInput.text.toString()
            if (textSearch.isNotEmpty()) {
                submitQuery(textSearch)
            }
        }
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects: MapObjectCollection = binding.mapview.map.mapObjects
        mapObjects.clear()

        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(context, R.drawable.search_result)
                )

            }
        }
    }


    override fun onSearchError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        when (error) {
            is RemoteError -> {
                errorMessage = getString(R.string.remote_error_message)
            }

            is NetworkError -> {
                errorMessage = getString(R.string.network_error_message)
            }
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun submitQuery(query: String) {
        searchSession = searchManager!!.submit(
            query,
            VisibleRegionUtils.toPolygon(binding.mapview.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        val selectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)

        if (selectionMetadata != null) {
            binding.mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
            geoObjectTapEvent.geoObject.geometry.firstOrNull()?.let {
                it.point?.let { point ->
                    val lon = point.longitude
                    val lat = point.latitude
                    viewModel.getCoordinated(lat, lon)
                }
            }
        }
        return selectionMetadata != null
    }

    override fun onMapTap(map: Map, point: Point) {
        binding.mapview.map.deselectGeoObject()
    }

    override fun onMapLongTap(map: Map, point: Point) {}
}


