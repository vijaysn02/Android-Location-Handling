package com.vijaysn.locatio

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //region Variables
    val locationHandler = LocationHandler()
    //endregion

    //region Activity Life Cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleLocation()
    }
    //endregion

    //region Location Handling
    fun handleLocation() {

        //Initialization
        val locationListener = object : LocationHandlerCallBacks {
            override fun onNewLocation(location: Location) {
                processNewLocationUpdate(location)
            }
        }
        locationHandler.initiateLocationUpdates(this,listener = locationListener)

        //Get the current Location - on demand
        locationBtn.setOnClickListener {
            val currentUserLocation = locationHandler.getCurrentLocation { location ->
                Toast.makeText(this,location.latitude.toString(), Toast.LENGTH_SHORT).show()
                //TODO: - Process Location data
            }
        }

    }
    fun processNewLocationUpdate(location:Location) {
        Log.d("New Location", location.latitude.toString())
        //TODO: - Process New Location data
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == 42) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this,"Granted", Toast.LENGTH_SHORT).show()
                //TODO: - Handle Permission - Positive Case
            } else {
                Toast.makeText(this,"Rejected", Toast.LENGTH_SHORT).show()
                //TODO: - Handle Permission - Negative Case
            }
        }

    }
    //endregion
}
