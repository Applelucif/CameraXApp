package com.example.cameraxapp.framework

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.cameraxapp.data.LocalDataSource
import com.example.cameraxapp.domain.BodyPart
import com.example.cameraxapp.domain.BodyPartCoord
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageAnalyzer(
    private var lifecycleOwner: LifecycleOwner,
    context: Context,
    private var viewFinder: PreviewView
) :
    ImageAnalysis.Analyzer {
    private var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val preview by lazy { Preview.Builder().build() }

    init {
        getCameraPreview()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, this)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    private fun getCameraPreview(): Preview = preview

    fun stopCamera() {
        cameraExecutor.shutdown()
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API

            poseDetector.process(image)
                .addOnSuccessListener { results ->
                    // Task completed successfully

                    if (results.allPoseLandmarks.size != 0) {
                        Log.i("#SuccessPoseDetect#", "Pose detect is success")

                        val leftShoulderE = results.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                        val leftShoulder = BodyPartCoord(
                            BodyPart.LEFT_SHOULDER,
                            leftShoulderE.position.x,
                            leftShoulderE.position.y
                        )
                        val leftElbowA = results.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
                        val leftElbow = BodyPartCoord(
                            BodyPart.LEFT_ELBOW,
                            leftElbowA.position.x,
                            leftElbowA.position.y
                        )
                        val rightElbowB = results.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
                        val rightElbow = BodyPartCoord(
                            BodyPart.RIGHT_ELBOW,
                            rightElbowB.position.x,
                            rightElbowB.position.y
                        )
                        val leftWristC = results.getPoseLandmark(PoseLandmark.LEFT_WRIST)
                        val leftWrist = BodyPartCoord(
                            BodyPart.LEFT_WRIST,
                            leftWristC.position.x,
                            leftWristC.position.y
                        )
                        val rightWristD = results.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
                        val rightWrist = BodyPartCoord(
                            BodyPart.RIGHT_WRIST,
                            rightWristD.position.x,
                            rightWristD.position.y
                        )

                        val list: List<BodyPartCoord> =
                            listOf(leftShoulder, leftElbow, rightElbow, leftWrist, rightWrist)

                        val localData = LocalDataSource()
                        localData.addCoords(list)
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    Log.i("#FailurePoseDetect#", "Pose detect is failure")
                }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
    }
}
