package com.example.cameraxapp.presentation

import android.content.Context
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.cameraxapp.domain.UseCases
import com.example.cameraxapp.framework.ImageAnalyzer
import io.reactivex.disposables.CompositeDisposable

class MainViewModel : ViewModel() {

    var useCases: UseCases = UseCases()
    private val compositeDisposable by lazy { CompositeDisposable() }

    var imageAnalyzer: ImageAnalyzer? = null

    init {
        useCases.getRecognitionResultFlow().subscribe() { result ->
            when (result) {
                "Ni" -> Log.i("*#NI*#", "Success Ni")
                "Hu" -> Log.i("*#Hu*#", "Success Hu")
                "Ya" -> Log.i("*#Ya*#", "Success Ya")
            }
        }
    }

    fun imageAnalyzerInit(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        viewFinder: PreviewView
    ) {
        imageAnalyzer = ImageAnalyzer(lifecycleOwner, context, viewFinder)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        imageAnalyzer?.stopCamera()
        imageAnalyzer = null
    }
}