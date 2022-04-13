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
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
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
import ru.sarawan.android.BuildConfig
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentMapBinding


class MapFragment : Fragment(), Session.SearchListener, CameraListener, UserLocationObjectListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
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
        binding.mapview.map.cameraPosition.target.latitude
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
            moveCameraToPosition(userLocationLayer!!.cameraPosition()?.target)

        }




        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEdit.text.toString())
            }
            false
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

    private fun submitQuery(query: String) {
//        searchSession = searchManager!!.submit(
//            "Пихтовая 12",
//            VisibleRegionUtils.toPolygon(binding.mapview.map.visibleRegion),
//            SearchOptions().apply {
//                 searchTypes = SearchType.GEO.value
//                 geometry = true
//            },
//            this
//        )
        val lat = userLocationLayer!!.cameraPosition()!!.target.latitude
        val lon = userLocationLayer!!.cameraPosition()!!.target.longitude
        searchSession = searchManager!!.submit(
            Point(lat, lon),
            1000,
            SearchOptions(),
            this
        )
    }

    override fun onSearchResponse(response: Response) {

        val mapObjects: MapObjectCollection = binding.mapview.map.mapObjects
        mapObjects.clear()
        response.collection.children.firstOrNull()?.obj?.metadataContainer?.getItem(
            ToponymObjectMetadata::class.java
        )
            ?.address
        for (searchResult in response.collection.children) {
            searchResult.obj?.let { it ->
                it.metadataContainer.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
                    ?.name

                val resultLocation = it.geometry[0].point
                if (resultLocation != null) {
                    mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(context, R.drawable.search_result)
                    )
                }
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        errorMessage = when (error) {
            is RemoteError -> {
                getString(R.string.remote_error_message)
            }
            is NetworkError -> {
                getString(R.string.network_error_message)
            }
            else -> errorMessage
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {

        if (finished) {
            submitQuery(binding.searchEdit.text.toString())
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
}


