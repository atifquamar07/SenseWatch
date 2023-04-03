package com.example.sensewatch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GeomagneticRotation : AppCompatActivity(), SensorEventListener  {

    private lateinit var tvXCoordinate: TextView
    private lateinit var tvYCoordinate: TextView
    private lateinit var tvZCoordinate: TextView
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null

    private val filterFactor = 0.9f // Filter factor
    private var smoothedRotationVector = FloatArray(4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geomagnetic_rotation)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        tvXCoordinate = findViewById(R.id.tv_X_val)
        tvYCoordinate = findViewById(R.id.tv_Y_val)
        tvZCoordinate = findViewById(R.id.tv_Z_val)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            // Apply low-pass filter to smooth the sensor data
            smoothedRotationVector[0] += filterFactor * (event.values[0] - smoothedRotationVector[0])
            smoothedRotationVector[1] += filterFactor * (event.values[1] - smoothedRotationVector[1])
            smoothedRotationVector[2] += filterFactor * (event.values[2] - smoothedRotationVector[2])
            smoothedRotationVector[3] += filterFactor * (event.values[3] - smoothedRotationVector[3])

            // Update the TextViews with the smoothed sensor data
            tvXCoordinate.text = String.format("%.9f", smoothedRotationVector[0])
            tvYCoordinate.text = String.format("%.9f", smoothedRotationVector[1])
            tvZCoordinate.text = String.format("%.9f", smoothedRotationVector[2])
        }
    }

    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}