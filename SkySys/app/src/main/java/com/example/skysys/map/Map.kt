package com.example.skysys.map

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.example.skysys.R
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

open class Map constructor(mapView: MapView, context: Context){

    private var mapView = mapView
    private var context = context
    private var mapStyle = Style.SATELLITE_STREETS
    var missao = Controller()

    fun setMapStyle( mapStyle : String)
    {
        this.mapStyle = mapStyle
    }

//    var onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
//        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
//    }

//    var onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
//        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
//        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
//    }
    fun onMapReady() {

        missao.setContext(context)
        missao.setMapView(mapView)

        mapView.getMapboxMap().setCamera(CameraOptions.Builder()
            .zoom(14.0)
            .build())
        mapView.getMapboxMap().loadStyleUri(
            mapStyle
        ) {
            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
        mapView.gestures.addOnMapClickListener(missao.mapClick)
    }

    val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(context, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
//        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}