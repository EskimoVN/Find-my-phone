package com.eskimo.findmyphone.locatemydevice.trackmymobile

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ActivityMainBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.sdptracking.DSPDetectService
import com.eskimo.findmyphone.locatemydevice.trackmymobile.servicestracking.AudioDetectService
import com.musicg.fingerprint.FingerprintSimilarity
import com.musicg.wave.Wave
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissionRecord()
    }

    private fun requestPermissionRecord() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionRecord.launch(android.Manifest.permission.RECORD_AUDIO)
        } else {
            Log.d("LucTV", "Access record success ")
            startAudioService()
        }
    }

    private fun startAudioService() {
        lifecycleScope.launch {
            delay(300)
            val intent = Intent(this@MainActivity, DSPDetectService::class.java)
            startService(intent)
        }
    }

    private val requestPermissionRecord =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("LucTV", "Access record success ")
                startAudioService()
            } else {
                Log.d("LucTV", "Access record fail ")
            }
        }

    override fun onDestroy() {
        super.onDestroy()
    }
}