package com.thekusch.nerdeyesem.viewmodel

import com.thekusch.nerdeyesem.BuildConfig
import com.thekusch.nerdeyesem.data.Resource
import com.thekusch.nerdeyesem.data.RetrofitProvider
import com.thekusch.nerdeyesem.data.model.detailed.DetailedResult
import com.thekusch.nerdeyesem.data.model.nearby.NearbyResult
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class ApiDataSource {

    private val retrofitProvider = RetrofitProvider()
    fun fetchNearbyRestaurant(lat:Double,lon:Double):Observable<Resource<NearbyResult>>{
        return Observable.create { emitter ->
            emitter.onNext(Resource.loading())

            retrofitProvider.
            API.
            nearbyRestaurants(BuildConfig.API_KEY,lat,lon).
            subscribeOn(Schedulers.io()).
            subscribe(
                {
                    emitter.onNext(Resource.success(it))
                    emitter.onComplete()
                },
                {
                    //If an error occur, app must display error and shouldn't crash
                    //To do this, instead of using emitter.onError(), use emitter.onNext() so ap can go work
                    emitter.onNext(Resource.error(it.message?:""))
                    emitter.onComplete()
                })

        }
    }
    fun fetchDetailedRestaurant(res_id:Int):Observable<Resource<DetailedResult>>{
        return Observable.create { emitter ->
            emitter.onNext(Resource.loading())

            retrofitProvider
                .API
                .getRestaurant(BuildConfig.API_KEY,res_id)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        emitter.onNext(Resource.success(it))
                        emitter.onComplete()
                    },
                    {
                        emitter.onNext(Resource.error(it.message?:""))
                        emitter.onComplete()
                    })
        }
    }
}