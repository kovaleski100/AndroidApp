package com.example.skysys

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skysys.dataClass.missao
import com.example.skysys.databinding.ActivityMainBinding
import com.example.skysys.map.Map
import com.example.skysys.screen.CustomAdapter
import com.example.skysys.screen.Message
import com.example.skysys.utils.GPS
import com.example.skysys.utils.realmIOSky
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import io.realm.kotlin.query.RealmResults
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var GPS: GPS
    private lateinit var mapConfig : Map
    private var mapIndex = 0
    private var updateCameraflag = true

    private val navigationProvider = NavigationLocationProvider()
    private lateinit var mapboxNavigation: MapboxNavigation
    private val locationObserver = object :
        LocationObserver {
        override fun onNewRawLocation(rawLocation: Location) {
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            navigationProvider.changePosition(
                enhancedLocation,
                locationMatcherResult.keyPoints,
            )
            if(updateCameraflag)
            {
                updateCamera(enhancedLocation)
                updateCameraflag = false
            }
        }
    }

    private fun updateCamera(location: Location) {
        binding.mapView.camera.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(location.longitude, location.latitude))
                .zoom(14.0)
                .padding(EdgeInsets(500.0, 0.0, 0.0, 0.0))
                .build()
        )
    }

    private fun updateUI(messages : MutableList<com.example.skysys.screen.Message>)
    {
        binding.rvMessage.adapter = CustomAdapter(messages)
        binding.rvMessage.setHasFixedSize(true)
        binding.rvMessage.itemAnimator = DefaultItemAnimator()
        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        binding.rvMessage.isVisible = true
    }

    @SuppressLint("MissingPermission")
    private fun initNavigation() {
        mapboxNavigation = MapboxNavigation(
            NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .build()
        ).apply {
            startTripSession()
            registerLocationObserver(locationObserver)
        }
    }

    private fun initActivity()
    {
        binding.lancar.isVisible = false
        binding.newPath.isVisible = false
        binding.rvMessage.isVisible = false

        GPS = GPS(WeakReference(this))



        mapConfig = Map(binding.mapView, this)
        GPS.checkPermissions {
            mapConfig.onMapReady()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()

        initActivity()

        binding.mapView.location.apply {
            setLocationProvider(navigationProvider)

            locationPuck = LocationPuck2D(bearingImage = ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.mark_location
            ))
            enabled = true
        }

        binding.mapStyle.setOnClickListener{
            if(mapIndex == 0)
            {
                mapConfig.setMapStyle(Style.SATELLITE_STREETS)
                mapIndex = 1
            }
            else
            {
                mapConfig.setMapStyle(Style.MAPBOX_STREETS)
                mapIndex = 0
            }
            mapConfig.onMapReady()
        }
        initNavigation()

        binding.mapLocation.setOnClickListener{
            updateCameraflag = true
        }

        binding.menu.setOnClickListener{
            binding.rvMessage.isVisible = false
            val popupMenu = PopupMenu(this, binding.menu);

            popupMenu.menuInflater.inflate(R.menu.menu_main,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    item ->
                when(item.itemId)
                {
                    R.id.poligono ->
                    {
                        mapConfig.missao.setPointController(1)
                        mapConfig.missao.setDrawController(1)
                        binding.lancar.isVisible = true
                    }
                    R.id.caminho ->
                    {
                        mapConfig.missao.setPointController(1)
                        binding.lancar.isVisible = true
                        binding.newPath.isVisible = true
                    }
                    R.id.missao ->
                    {
                        val bd = realmIOSky()
                        val listOfMissions = bd.readByName("Matheus")
                        if(!listOfMissions.isEmpty())
                        {
                            var messages = mutableListOf<com.example.skysys.screen.Message>()

                            for(i in 0..listOfMissions.size-1)
                            {
                                messages.add(com.example.skysys.screen.Message(listOfMissions[i].nome!!))
                            }

                            Log.i("teste",messages.toString())
                            Log.i("teste",listOfMissions.size.toString())
                            updateUI(messages)
                        }
                    }
                }
                true
            })

            mapConfig.missao.poligon.observe(this, Observer { valor ->
                if(valor != 0)
                {
                    binding.newPath.isVisible = true
                }
            })

            binding.newPath.setOnClickListener{
                mapConfig.missao.addList()
                val numPath = mapConfig.missao.getCountList()
                mapConfig.missao.setCountList(numPath+1)
                binding.lancar.isVisible = true
            }

            binding.lancar.setOnClickListener{
                mapConfig.missao.setDrawController(0)
                mapConfig.missao.setPointController(0)
                binding.lancar.isVisible = false
                var distance = 0.0
                for (i in 0..mapConfig.missao.getCountList())
                {
                    distance += mapConfig.missao.calcDistance(i)
                }
                val intent = Intent(this, MissaoActivity::class.java)
                intent.putExtra("distancia", distance)
                startActivity(intent)
                initActivity()
            }
            popupMenu.show()
        }

        //val udp = UDP_server()
        //udp.start()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.gestures.removeOnMoveListener(mapConfig.onMoveListener)
        binding.mapView?.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate : MenuInflater = menuInflater
        inflate.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        GPS.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}