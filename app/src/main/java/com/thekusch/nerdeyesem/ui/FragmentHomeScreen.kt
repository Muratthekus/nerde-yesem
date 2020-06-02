package com.thekusch.nerdeyesem.ui

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.thekusch.nerdeyesem.LocationListenerService
import com.thekusch.nerdeyesem.LocationPermission

import com.thekusch.nerdeyesem.R
import com.thekusch.nerdeyesem.databinding.FragmentHomeScreenBinding


class FragmentHomeScreen : Fragment() {
    private lateinit var locationPermission : LocationPermission
    private val PERMISSION_ID = 35
    private var isServiceConnected = false
    private lateinit var binding: FragmentHomeScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkLocationPermission()
        serviceCommunication()
    }

    //Bind service
    private fun serviceConnection(){
        val serviceConnection : ServiceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                showToast(getString(R.string.location_information_cant_fetch))
            }
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder: LocationListenerService.LocalBinder =
                    service as LocationListenerService.LocalBinder
                isServiceConnected=true
                showToast(getString(R.string.location_information_fetch))
            }
        }
        val intent = Intent(activity,LocationListenerService::class.java)
        activity!!.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)
    }
    //Receiver message from location listener service
    private fun serviceCommunication(){
        val mReceiver : BroadcastReceiver = object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val msg = if(intent!!.getBooleanExtra("providerEnable",true)){
                    getString(R.string.location_information_fetch_start_again)
                }
                else{
                    getString(R.string.location_information_cant_fetch)
                }
                showToast(msg)
            }
        }
        //If receiver intent has this action, then go to the receiver
        LocalBroadcastManager.getInstance(context!!).registerReceiver(mReceiver, IntentFilter("LOCATION_SERVICE_PROVIDER_STATUS"))
    }

    private fun checkLocationPermission(){
        locationPermission = LocationPermission(requireActivity())
        //Permission granted
        if(locationPermission.checkPermission()){
            isLocationEnable()
        }
        //Request permission
        else{
            locationPermission.requestPermission()
        }
    }
    //If location enable, start service
    private fun isLocationEnable(){
        if(locationPermission.isLocationEnable()){
            serviceConnection()
        }
        else{
            showToast(getString(R.string.enable_location_settings))
            val intent = Intent("android.settings.ACTION_LOCATION_SOURCE_SETTINGS")
            startActivity(intent)
        }
    }
    //If permission granted, check whether location enable
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isLocationEnable()
            }
        }
    }

    private fun showToast(msg:String){
        val toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }

}
