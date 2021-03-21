package com.example.cameraxapp.presentation

import android.util.Log
import androidx.camera.view.PreviewView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.cameraxapp.domain.UseCases
import com.example.cameraxapp.framework.ImageAnalyzer
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor

class MainViewModel @ViewModelInject constructor(
    useCases: UseCases,
    private val imageAnalyzer: ImageAnalyzer
) : ViewModel() {
    private val compositeDisposable by lazy { CompositeDisposable() }
    private val resultAddProcessor = BehaviorProcessor.create<String>()
    private var poseCount = 1

    init {
        compositeDisposable.add(
            useCases.getRecognitionResultFlow().subscribe() { result ->
                if (result == "Ni" && poseCount == 1) {
                    resultAddProcessor.onNext("Ni")
                    poseCount = 2
                }
                if (result == "Hu" && poseCount == 2) {
                    resultAddProcessor.onNext("Hu")
                    poseCount = 3
                }
                if (result == "Ya" && poseCount == 3) {
                    resultAddProcessor.onNext("Ya")
                    poseCount = 4
                }
                if (result == "nothing") Log.i ("#*RecognitionResult", "Pose not found")
            })
    }

    fun getResultFlow(): Flowable<String> {
        return resultAddProcessor
    }

    fun startAnalyze(
        lifecycleOwner: LifecycleOwner,
        viewFinder: PreviewView
    ) {
        imageAnalyzer.startAnalyze(lifecycleOwner, viewFinder)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        imageAnalyzer.stopCamera()
    }
}