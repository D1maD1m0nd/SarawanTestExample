package ru.sarawan.android.framework.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import ru.sarawan.android.BuildConfig
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentMapBinding


class MapFragment : Fragment(), Session.SearchListener, CameraListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null

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

    private fun initView() = with(binding) {
        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEdit.text.toString())
            }
            false
        }
        mapview.map.move(
            CameraPosition(Point(55.7537090, 37.6198133), 14.0f, 0.0f, 0.0f)
        )

        submitQuery(searchEdit.text.toString())
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
        searchSession = searchManager!!.submit(
            query,
            VisibleRegionUtils.toPolygon(binding.mapview.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects: MapObjectCollection = binding.mapview.map.mapObjects
        mapObjects.clear()

        for (searchResult in response.collection.children) {
            searchResult.obj?.let {
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
}


