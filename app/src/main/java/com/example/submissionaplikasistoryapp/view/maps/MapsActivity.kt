package com.example.submissionaplikasistoryapp.view.maps

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionaplikasistoryapp.R
import com.example.submissionaplikasistoryapp.databinding.ActivityMapsBinding
import com.example.submissionaplikasistoryapp.model.UserPreferences
import com.example.submissionaplikasistoryapp.response.ListStoryItem
import com.example.submissionaplikasistoryapp.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupView()
        setupViewModel()
        storyMarker()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupViewModel(){
        mapsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreferences.getInstance(dataStore))
        )[MapsViewModel::class.java]
    }

    private val boundsBuilder = LatLngBounds.Builder()
    private val listStory = ArrayList<ListStoryItem>()
    private fun storyMarker(){
        mapsViewModel.getUser().observe(this){
            val token = it.token
            mapsViewModel.setStoryMap(1, 100, 1, "Bearer $token")
        }
        mapsViewModel.storyMaps.observe(this){
            for (story in it) {
                listStory.add(story)
            }
            listStory.forEach { story ->
                val latLng = story.lat?.let { it1 -> story.lon?.let { it2 -> LatLng(it1, it2) } }
                latLng?.let { it1 -> MarkerOptions().position(it1).title(story.name) }
                    ?.let { it2 -> mMap.addMarker(it2) }
                latLng?.let { it1 -> boundsBuilder.include(it1) }

                val bounds: LatLngBounds = boundsBuilder.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        300
                    )
                )
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted: Boolean ->
            if (isGranted){
                getMyLocation()
            }
        }

    private fun getMyLocation(){
        if(ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
        } else{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle(){
        try{
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (exception: Resources.NotFoundException){
            Log.e(TAG, "Error: ", exception)
        }
    }
}