package com.thekusch.nerdeyesem.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.marginLeft
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
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
import kotlinx.android.synthetic.main.fragment_detailed_restaurant.view.*


class FragmentHomeScreen : Fragment(), RestaurantListAdapter.ItemClickListener {

    private val PERMISSION_ID = 35
    private lateinit var mService:LocationListenerService
    private var isProviderEnable : Boolean = true

    private lateinit var binding: FragmentHomeScreenBinding

    //view model
    private lateinit var networkViewModel:NetworkViewModel

    //Thread
    private val handler = Handler()
    private var waitServiceConnectionRunn:Runnable = Runnable {waitServiceConnection()}

    //Recyclerview
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter:RestaurantListAdapter

    //SharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
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

        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        editor = pref.edit()

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setOrientation(LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recyclerview_divider))
        recyclerView.addItemDecoration(dividerItemDecoration)

        networkViewModel = ViewModelProviders.of(this).get(NetworkViewModel::class.java)

        handler.post(waitServiceConnectionRunn)
    }
    private fun waitServiceConnection(){
        if(this::mService.isInitialized && mService.getLocation().first!=0.0){
            handler.removeCallbacks(waitServiceConnectionRunn)
            networkProcess(mService.getLocation().second,mService.getLocation().first)
        }
        else{
            handler.postDelayed(waitServiceConnectionRunn,100)
        }

    }
    //Observe the request status
    private fun networkProcess(lat:Double,lon:Double){
        networkViewModel.nearbyApiConnection(lat,lon)
        networkViewModel.nearbyResultObservable.observe(this, Observer {
            when(it.status){
                SUCCESS ->
                {
                    binding.loadingComponent.visibility = View.GONE
                    binding.pleaseWaitText.visibility = View.GONE
                    binding.headerTopCuisines.visibility = View.VISIBLE
                    binding.restaurantRecycler.visibility = View.VISIBLE
                    it.data?.nearbyRestaurants?.let { it1 -> adapter.setData(it1) }
                    it.data?.popularity?.top_cuisines?.forEach {
                            cuisines -> addTopCuisines2Header(cuisines)
                    }
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
    private fun addTopCuisines2Header(cuisines:String){
        val text = TextView(context)
        text.text = cuisines
        text.gravity = Gravity.CENTER
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(10,0,5,0)
        text.layoutParams = layoutParams
        text.textSize = 16F
        text.setTextColor(resources.getColor(R.color.white))
        binding.topCuisinesLayout.addView(text)
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
                mService = binder.getService()
                binding.pleaseWaitText.text = getString(R.string.location_information_fetch)
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
                binding.pleaseWaitText.text = getString(R.string.waiting_for_gps_connection)
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
        res_id?.let { editor.putInt("resID", it) }
        editor.apply()

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.mainActivity_FrameLayout,FragmentDetailedScreen())
            ?.commit()

    }

}
