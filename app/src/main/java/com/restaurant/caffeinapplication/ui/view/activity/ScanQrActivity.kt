package com.restaurant.caffeinapplication.ui.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.databinding.ActivityScanQrBinding
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import java.util.*

class ScanQrActivity : AppCompatActivity(), DecoratedBarcodeView.TorchListener {

    lateinit var binding : ActivityScanQrBinding
    private var viewfinderView: ViewfinderView? = null
    private var capture: CaptureManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var barcodeScannerView = binding.zxingBarcodeScanner
        barcodeScannerView.setTorchListener(this)
        barcodeScannerView.setStatusText("")
        viewfinderView = findViewById(R.id.zxing_viewfinder_view)
        binding.imgClose.setOnClickListener {
            finish()
        }

        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.setShowMissingCameraPermissionDialog(false)
        capture!!.decode()


    }
    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onTorchOn() {
        Log.d("SCAN-QR", "onTorchOn: YO")
    }

    override fun onTorchOff() {
        Log.d("SCAN-QR", "onTorchOFF: YO")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}