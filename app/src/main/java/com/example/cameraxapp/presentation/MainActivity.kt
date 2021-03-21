package com.example.cameraxapp.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraxapp.R
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Request camera permissions
        if (allPermissionsGranted(baseContext)) {
            viewModel.startAnalyze(this, viewFinder)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        compositeDisposable.add(
            viewModel.getResultFlow().subscribe() { result ->
                when (result) {
                    "Ni" -> image.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.second_image
                        )
                    )
                    "Hu" -> image.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.third_image
                        )
                    )
                    "Ya" -> Toast.makeText(this, "Идеально!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    private fun allPermissionsGranted(baseContext: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(baseContext)) {
                viewModel.startAnalyze(this, viewFinder)
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}