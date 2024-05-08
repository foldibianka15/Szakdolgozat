package com.example.projektmunka.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.projektmunka.R
import com.example.projektmunka.routeUtil.displayCircularRoute
import com.example.projektmunka.databinding.FragmentMapBinding
import com.example.projektmunka.routeUtil.addMilestones
import com.example.projektmunka.routeUtil.drawRoute2
import com.example.projektmunka.viewModel.RouteOnMapViewModel
import com.example.projektmunka.viewModel.SessionViewModel
import com.example.projektmunka.viewModel.routeGeneratorViewmodel.CircularDiffRouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

@AndroidEntryPoint
class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap: MapView
    private lateinit var controller: IMapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    private val sessionViewModel: SessionViewModel by viewModels()
    private val circularDiffRouteViewModel: CircularDiffRouteViewModel by viewModels()
    private val routeOnMapViewModel: RouteOnMapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayMap()
        //drawCircularRouteOnMap()
        drawRouteOnMap()
    }

    private fun displayMap() {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        )

        mMap = binding.mapView
        mMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            setMultiTouchControls(true)
            setBuiltInZoomControls(true)
        }

        controller = mMap.controller
        controller.setZoom(15.0)

        if(ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
            ){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            handleLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleLocationUpdates()
        }
    }

    private fun handleLocationUpdates() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mMap)
        myLocationOverlay.enableMyLocation()
        mMap.overlays.add(myLocationOverlay)

        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.lastLocation.collect { location ->
                location?.let {
                    mMap.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                }
            }
        }
    }

    private fun drawRouteOnMap() {
        viewLifecycleOwner.lifecycleScope.launch {
            routeOnMapViewModel.generatedRoute.collect { route ->
                if (route != null) {
                    drawRoute2(mMap, route)
                }
            }
        }
    }

    private fun drawCircularRouteOnMap() {
        viewLifecycleOwner.lifecycleScope.launch {
            val combinedFlow = circularDiffRouteViewModel.generatedRoute.zip(circularDiffRouteViewModel.generatedRoutePois) {
                route, poi ->
                Pair(route, poi)
            }
            combinedFlow.collect { (route, poi) ->
                if (route != null && poi != null) {
                    displayCircularRoute(mMap, poi, route)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myLocationOverlay.disableMyLocation()
        mMap.onDetach()
    }
}