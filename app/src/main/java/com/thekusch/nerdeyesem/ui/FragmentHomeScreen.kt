package com.thekusch.nerdeyesem.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekusch.nerdeyesem.locations.LocationListenerService
import com.thekusch.nerdeyesem.R
import com.thekusch.nerdeyesem.data.Status
import com.thekusch.nerdeyesem.databinding.FragmentHomeScreenBinding
import com.thekusch.nerdeyesem.viewmodel.ApiDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.github.ajalt.timberkt.Timber
import com.thekusch.nerdeyesem.adapter.RestaurantListAdapter
import com.thekusch.nerdeyesem.data.Status.*
import com.thekusch.nerdeyesem.data.model.nearby.NearbyRestaurant
import com.thekusch.nerdeyesem.viewmodel.NetworkViewModel


class FragmentHomeScreen : Fragment(), RestaurantListAdapter.ItemClickListener {

    private val PERMISSION_ID = 35
    private var isServiceConnected = false
    private lateinit var mService:LocationListenerService

    private lateinit var binding: FragmentHomeScreenBinding

    //view model
    private lateinit var networkViewModel:NetworkViewModel

    //Thread
    private val handler = Handler()
    private var waitServiceConnectionRunn:Runnable = Runnable {waitServiceConnection()}
    //Recyclerview
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter:RestaurantListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeScreenBinding.inflate(inflater,container,false)

        recyclerView = binding.restaurantRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startFetchLocation()
        serviceCommunication()

        adapter = RestaurantListAdapter()
        recyclerView.adapter = adapter
        adapter.setItemClickListener(this)

        networkViewModel = ViewModelProviders.of(this).get(NetworkViewModel::class.java)
        networkProcess()
    }
    private fun waitServiceConnection(){

    }
    //Observe the request status
    private fun networkProcess(){
        networkViewModel.nearbyApiConnection(38.410481,27.128403)
        networkViewModel.nearbyResultObservable.observe(this, Observer {
            when(it.status){
                SUCCESS ->
                {
                    binding.loadingComponent.visibility = View.GONE
                    binding.pleaseWaitText.visibility = View.GONE
                    binding.headerUserLocation.visibility = View.VISIBLE
                    binding.restaurantRecycler.visibility = View.VISIBLE
                    it.data?.nearbyRestaurants?.let { it1 -> adapter.setData(it1) }
                }
                ERROR ->
                {
                    showToast(getString(R.string.request_error_try_later_will_fix_it))
                    binding.pleaseWaitText.text = it.msg
                }
                LOADING -> binding.pleaseWaitText.text = getString(R.string.try_to_connect_server)
            }
        })
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
                mService = binder.getService()
                showToast(getString(R.string.location_information_fetch))
            }
        }
        val intent = Intent(activity,
            LocationListenerService::class.java)
        activity!!.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)
    }

    //Receive message from location listener service
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

    //Start fetch location process
    private fun startFetchLocation(){
        //Permission granted
        if(checkPermission()){
            locationStatus()
        }
        //Request permission
        else{
            permissionRequest()
        }
    }

    //Check if location permission granted
    private fun checkPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun permissionRequest(){
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID)
    }

    private fun locationStatus(){
        val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
        if(status){
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
                locationStatus()
            }
        }
    }

    private fun showToast(msg:String){
        val toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }

    override fun onItemClick(res_id: Int?) {
        showToast("TIKLANDI KANKA")
    }

}
