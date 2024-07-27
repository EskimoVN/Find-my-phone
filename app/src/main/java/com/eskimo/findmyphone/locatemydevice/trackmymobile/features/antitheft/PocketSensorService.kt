package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.antitheft

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R

class PocketSensorService : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var mediaPlayer: MediaPlayer? = null
    private var customThreshold: Float = 5.0f
    private var isEnableMedia = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.demo
        ) // Đặt tệp âm thanh của bạn ở thư mục res/raw và thay 'sound' bằng tên tệp của bạn

        if (proximitySensor == null) {
            Toast.makeText(this, "Thiết bị không có cảm biến tiệm cận", Toast.LENGTH_SHORT).show()
            finish() // Kết thúc hoạt động nếu không có cảm biến tiệm cận
        } else {
            // Kiểm tra giá trị tối đa của cảm biến tiệm cận
            val maxRange = proximitySensor!!.maximumRange
            Toast.makeText(
                this,
                "Giá trị tối đa của cảm biến tiệm cận: $maxRange cm",
                Toast.LENGTH_SHORT
            ).show()
            // Bạn có thể điều chỉnh customThreshold dựa trên giá trị tối đa này nếu cần
        }
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.also { proximity ->
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] < customThreshold) {
                isEnableMedia = true
                Toast.makeText(this, "Điện thoại đang ở trong túi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Điện thoại được lấy ra khỏi túi", Toast.LENGTH_SHORT).show()
                if (isEnableMedia) {
                    mediaPlayer?.start()
                    isEnableMedia = false
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Không cần thiết phải làm gì ở đây cho cảm biến này
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}