package com.thekusch.nerdeyesem.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekusch.nerdeyesem.data.Resource
import com.thekusch.nerdeyesem.data.Status.*
import com.thekusch.nerdeyesem.data.model.detailed.DetailedResult
import com.thekusch.nerdeyesem.data.model.nearby.NearbyResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NetworkViewModel: ViewModel(){
    private val apiDataSource = ApiDataSource()

    //To observe status of request
    var nearbyResultObservable : MutableLiveData<NetworkDataClass<NearbyResult>> = MutableLiveData()
    var detailedResultObservable : MutableLiveData<NetworkDataClass<DetailedResult>> = MutableLiveData()

    @SuppressLint("CheckResult")
    fun nearbyApiConnection(lat:Double,lon:Double){
        apiDataSource
            .fetchNearbyRestaurant(lat,lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when(it.status){
                    SUCCESS ->nearbyResultObservable.value = NetworkDataClass(SUCCESS,it.data,"")
                    ERROR -> nearbyResultObservable.value = NetworkDataClass(ERROR,null,it.message)
                    LOADING -> nearbyResultObservable.value = NetworkDataClass(LOADING,null,"")
                }
            }
    }

    @SuppressLint("CheckResult")
    fun detailedInfoApiConnection(res_id:Int){
        apiDataSource
            .fetchDetailedRestaurant(res_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when(it.status){
                    SUCCESS -> detailedResultObservable.value = NetworkDataClass(SUCCESS,it.data,"")
                    ERROR -> detailedResultObservable.value = NetworkDataClass(ERROR,null,it.message)
                    LOADING -> detailedResultObservable.value = NetworkDataClass(LOADING,null,"")
                }
            }
    }
}