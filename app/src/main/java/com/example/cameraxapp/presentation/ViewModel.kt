package com.example.cameraxapp.presentation

import android.util.Log
import androidx.camera.view.PreviewView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.cameraxapp.domain.UseCases
import com.example.cameraxapp.framework.ImageAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
    useCases: UseCases,
    private val imageAnalyzer: ImageAnalyzer
): ViewModel() {
    private val compositeDisposable by lazy { CompositeDisposable() }

    init {
        compositeDisposable.add(
        useCases.getRecognitionResultFlow().subscribe() { result ->
            when (result) {
                "Ni" -> Log.i("*#RecognitionResult*#", "Success Ni")
                "Hu" -> Log.i("*#RecognitionResult*#", "Success Hu")
                "Ya" -> Log.i("*#RecognitionResult*#", "Success Ya")
                "nothing" -> Log.i("*#RecognitionResult*#", "Success Ya")
            }
        })
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