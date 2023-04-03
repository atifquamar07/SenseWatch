package com.example.sensewatch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GeomagneticRotation : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var geomagneticSensor: Sensor
    private lateinit var tvXCoordinate: TextView
    private lateinit var tvYCoordinate: TextView
    private lateinit var tvZCoordinate: TextView
    private var rotationVectorSensor: Sensor? = null
    private val filterFactor = 0.2f // Filter factor
    private var smoothedRotationVector = FloatArray(4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geomagnetic_rotation)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        geomagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        tvXCoordinate = findViewById(R.id.tv_X_val)
        tvYCoordinate = findViewById(R.id.tv_Y_val)
        tvZCoordinate = findViewById(R.id.tv_Z_val)
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Do nothing
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val x = orientation[0] * 180 / Math.PI
                val y = orientation[1] * 180 / Math.PI
                val z = orientation[2] * 180 / Math.PI
                tvXCoordinate.text = "$x"
                tvYCoordinate.text = "$y"
                tvZCoordinate.text = "$z"

            }
        }
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, geomagneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
}