package com.vijaysn.locatio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class LocationHandler {

    //region Variables
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var listener: LocationHandlerCallBacks
    lateinit var activity:Activity
    //endregion

    //region Location Permission
    fun checkPermissions(): Boolean {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false

    }
    fun requestPermissions() {

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )

    }
    fun isLocationEnabled(): Boolean {

        var locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }
    //endregion

    //region Location Invoking
    fun initiateLocationUpdates(activity: Activity,listener:LocationHandlerCallBacks)  {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        this.listener = listener
        this.activity = activity

        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(activity) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData(activity)
                    } else {
                        Log.d("Location","Enabled")
                    }
                }
            } else {
                Toast.makeText(activity.applicationContext, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }
    fun getCurrentLocation(callback: (Location) -> Unit)  {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData(activity)
                    }  else {
                        callback(location)
                    }
                }
            } else {
                Toast.makeText(activity.applicationContext, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivity(intent)
            }
        } else {
            requestPermissions()
        }


    }
    fun requestNewLocationData(activity: Activity) {

        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }
    val mLocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            listener.onNewLocation(mLastLocation)
        }

    }
    //endregion

}

//region Location Handler Callback - Interface
interface LocationHandlerCallBacks {
    fun onNewLocation(location: Location)
}
//endregion