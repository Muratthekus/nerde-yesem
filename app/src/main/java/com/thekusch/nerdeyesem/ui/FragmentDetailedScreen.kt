package com.thekusch.nerdeyesem.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import com.thekusch.nerdeyesem.R
import com.thekusch.nerdeyesem.TakeScreenshot
import com.thekusch.nerdeyesem.data.Status
import com.thekusch.nerdeyesem.data.Status.*
import com.thekusch.nerdeyesem.data.model.detailed.DetailedResult
import com.thekusch.nerdeyesem.databinding.FragmentDetailedRestaurantBinding
import com.thekusch.nerdeyesem.viewmodel.NetworkDataClass
import com.thekusch.nerdeyesem.viewmodel.NetworkViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException

class FragmentDetailedScreen:Fragment() {
    private lateinit var binding: FragmentDetailedRestaurantBinding

    private lateinit var networkViewModel: NetworkViewModel
    private var resID:Int? = 0

    //Threads
    private val handler = Handler()
    private val waitInternetConnectionRunn = Runnable { waitInternetConnection() }

    //SharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailedRestaurantBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        networkViewModel = ViewModelProviders.of(this).get(NetworkViewModel::class.java)

        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        editor = pref.edit()
        resID = pref.getInt("resID",0)

        FirebaseApp.initializeApp(requireContext())

        binding.shareRest.setOnClickListener{
            val bitmap = TakeScreenshot.takeScreenshotOfRootView(it)
            saveImage2InternalStrg(bitmap)
        }
        handler.post(waitInternetConnectionRunn)
    }
    private fun waitInternetConnection(){
        if(isInternetConnectionOpen()){
            handler.removeCallbacks(waitInternetConnectionRunn)
            sendRequest()
        }
        else{
            binding.pleaseWaitText.text = getString(R.string.internet_connection_required)
            handler.postDelayed(waitInternetConnectionRunn,100)
        }
    }
    private fun isInternetConnectionOpen():Boolean{
        val connectivityManager = requireActivity()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED
    }
    private fun saveImage2InternalStrg(bitmap: Bitmap){
        try{
            val path = File(requireContext().cacheDir,"images")
            path.mkdirs()
            val stream = FileOutputStream("$path/image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
            stream.close()
            sharePhoto()
        }
        catch (e:IOException){
            FirebaseCrashlytics.getInstance()
                .recordException(
                    IOException("Error when save image to internal storage. Error message -> $e"))
            showToast(getString(R.string.cant_share_photo))
        }
    }
    private fun sharePhoto(){
        val imagePath = File(requireContext().cacheDir, "images");
        val newFile = File(imagePath, "image.png");
        val contentUri = FileProvider.getUriForFile(requireContext(), "com.thekusch.nerdeyesem.fileprovider", newFile);

        if (contentUri != null) {
            val shareIntent = Intent();
            shareIntent.action = Intent.ACTION_SEND;
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, requireActivity().contentResolver.getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with_your_friends)));
        }
        else{
            FirebaseCrashlytics.getInstance()
                .recordException(
                    RuntimeException("Image successfully saved to internal storage but can not get it"))
        }
    }
    private fun sendRequest(){
        networkViewModel.detailedInfoApiConnection(resID)
        networkViewModel.detailedResultObservable.observe(this, Observer {
            when(it.status){
                SUCCESS ->
                {
                    binding.loadingComponent.visibility = View.GONE
                    binding.pleaseWaitText.visibility = View.GONE
                    displayData(it)
                }
                ERROR ->
                {
                    showToast(getString(R.string.request_error_try_later_will_fix_it))

                    FirebaseCrashlytics.getInstance()
                        .recordException(
                            RuntimeException("Error when try to fetch detailed info of the restaurant. Error message -> ${it.msg}"))
                }
                LOADING -> binding.pleaseWaitText.text = getString(R.string.try_to_connect_server)
            }
            editor.clear()
        })
    }
    private fun displayData(item:NetworkDataClass<DetailedResult>){
        binding.restName.text = item.data?.name
        binding.restCuisines.text = item.data?.cuisines
        binding.restLocation.text = item.data?.location?.address
        binding.restTiming.text = item.data?.timings
        val votes = "${item.data?.userRating?.aggregate_rating} - ${item.data?.userRating?.votes}"
        binding.restVotes.text = votes
        binding.restAvgCost.text = item.data?.averageCostForTwo
        binding.restPhone.text = item.data?.phoneNumbers

        var index = 0
        //Fetch first 6 highlights
        item.data?.highlights?.forEach {
            val textView = TextView(context)
            if(index<6){
                textView.text = it
                textView.gravity = Gravity.CENTER
                textView.textSize = 14F
                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(0,5,0,5)
                textView.layoutParams = layoutParams
                if(index<3){
                    binding.restHighlightFRow.gravity = Gravity.CENTER
                    binding.restHighlightFRow.addView(textView)
                }
                else{
                    binding.restHighlightSRow.gravity = Gravity.CENTER
                    binding.restHighlightSRow.addView(textView)
                }
                index++
            }
        }
        editRestVoteColor(item.data?.userRating?.aggregate_rating?.toDouble())
        binding.detailedCardView.visibility = View.VISIBLE
    }
    private fun editRestVoteColor(rate:Double?){
        if (rate != null) {
            if(rate<1.0){
                binding.userRatingText.text = getString(R.string.worse)
                binding.userRatingText.setBackgroundColor(resources.getColor(R.color.red))
            }
            else if(1.0<rate && rate<2.0){
                binding.userRatingText.text = getString(R.string.bad)
                binding.userRatingText.setBackgroundColor(resources.getColor(R.color.yellow))
            }
            else if(2.0<rate && rate<3.0){
                binding.userRatingText.text = getString(R.string.average)
                binding.userRatingText.setBackgroundColor(resources.getColor(R.color.orange))
            }
            else if(3.0<rate && rate<4.0){
                binding.userRatingText.text = getString(R.string.good)
                binding.userRatingText.setBackgroundColor(resources.getColor(R.color.green))
            }
            else{
                binding.userRatingText.text = getString(R.string.excellent)
                binding.userRatingText.setBackgroundColor(resources.getColor(R.color.dark_green))
            }
        }
    }
    private fun showToast(msg:String){
        val toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }
}