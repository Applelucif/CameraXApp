package com.example.cameraxapp.data

import com.example.cameraxapp.domain.BodyPartCoord
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor() {

    private val coordsAddProcessor = BehaviorProcessor.create<List<BodyPartCoord>>()

    fun getCoordsFlow(): Flowable<List<BodyPartCoord>> {
        return coordsAddProcessor
    }

    fun addCoords(listCoords: List<BodyPartCoord>) {
        coordsAddProcessor.onNext(listCoords)
    }
}