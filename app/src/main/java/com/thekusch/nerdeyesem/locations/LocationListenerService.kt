package com.thekusch.nerdeyesem.locations

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Pair
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.ajalt.timberkt.Timber

class LocationListenerService : Service(), LocationListener {
    private lateinit var locationManager:LocationManager
    //private lateinit var locationLocationManager: LocationManager
    private val LOCATION_UPDATE_TIME :Long = 3000//millisecond -> 10SN
    private val LOCATION_UPDATE_DISTANCE :Float = 1f

    private val iBinder:IBinder=LocalBinder()

    //lambda function
    //return true if provider enable else false
    private val providerStatus : (LocationProviderStatus) -> Boolean = {it== LocationProviderStatus.ENABLE }

    //Location value
    private var mLongitude:Double= 0.0
    private var mLatitude:Double= 0.0

    override fun onCreate() {
        super.onCreate()
        initLocManager()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }
    inner class LocalBinder: Binder(){
        fun getService(): LocationListenerService {
            return this@LocationListenerService
        }
    }

    //Initialize location manager and request for location updated
    private fun initLocManager(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.d{"Permission not granted, service stop"}
            stopSelf()
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_UPDATE_TIME,LOCATION_UPDATE_DISTANCE,this)
    }
    override fun onLocationChanged(location: Location?) {
        mLongitude = location!!.longitude
        mLatitude = location.latitude
        Timber.d{"Longitude -> $mLongitude || Latitude ->$mLatitude"}
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Timber.d{"Status changed -> $status"}
    }
    override fun onProviderEnabled(provider: String?) {
        sendMessage(LocationProviderStatus.ENABLE)
    }
    override fun onProviderDisabled(provider: String?) {
        sendMessage(LocationProviderStatus.DISABLE)
    }
    //Send message to fragment about provider status
    private fun sendMessage(providerStat: LocationProviderStatus){
        val intent = Intent("LOCATION_SERVICE_PROVIDER_STATUS")
        intent.putExtra("providerEnable",providerStatus.invoke(providerStat))
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
    fun getLocation():Pair<Double,Double>{
        /**
         * Return user longitude and latitude
         * */
        return Pair(mLongitude,mLatitude)
    }
}