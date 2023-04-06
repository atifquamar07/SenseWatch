package com.example.sensewatch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class GeomagneticRotation : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor
    private lateinit var targetOrientation: FloatArray
    private lateinit var currentOrientation: FloatArray
    private lateinit var tvDirections: TextView
    private lateinit var magneticFieldValues: FloatArray
    private lateinit var accelerometerValues: FloatArray
    private lateinit var db: AppDatabase
    private lateinit var geomagneticSensorDao: GeomagneticSensorDao
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geomagnetic_rotation)
        db = AppDatabase.getDatabase(applicationContext)
        geomagneticSensorDao = db.geomagneticSensorDao()

        // Initialize the sensor manager and rotation vector sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)

        // Initialize the target orientation and current orientation arrays
        targetOrientation = FloatArray(3)
        currentOrientation = FloatArray(3)
        magneticFieldValues = FloatArray(3)
        accelerometerValues = FloatArray(3)

        // Initialize the button
        btnStart = findViewById(R.id.btn_start_geomagnetic)
        tvDirections = findViewById(R.id.tv_directions)

        onPause()
        var isStarted = true

        btnStart.setOnClickListener {
            if(!isStarted){
                isStarted = true
                tvDirections.visibility = View.VISIBLE
                onResume()
                btnStart.text = "Stop Sensor"
                btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            }
            else {
                isStarted = false
                tvDirections.visibility = View.GONE
                onPause()
                btnStart.text = "Start Sensor"
                btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            }
        }

    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            // Converting the rotation vector to a rotation matrix
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            // Converting the rotation matrix to an orientation
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            // Converting the orientation from radians to degrees
            currentOrientation[0] = Math.toDegrees(orientation[0].toDouble()).toFloat()
            currentOrientation[1] = Math.toDegrees(orientation[1].toDouble()).toFloat()
            currentOrientation[2] = Math.toDegrees(orientation[2].toDouble()).toFloat()

            val data = GeomagneticSensorData(System.currentTimeMillis(), currentOrientation[0], currentOrientation[1], currentOrientation[2])

            // Calculating the difference between the current orientation and the target orientation
            val diffX = (currentOrientation[0] - targetOrientation[0])
            val diffY = (currentOrientation[1] - targetOrientation[1])
            val diffZ = (currentOrientation[2] - targetOrientation[2])

            // Providing feedback to the user about how much rotation is needed
            val feedback = "To align with earth's frame of reference\n\nRotate $diffX degrees around the X-axis.\nRotate $diffY degrees around the Y-axis.\nRotate $diffZ degrees around the Z-axis."
            tvDirections.text = feedback
            CoroutineScope(Dispatchers.IO).launch {
                geomagneticSensorDao.insert(data)
            }

            // Check if the device is aligned with earth's frame of reference
            if (diffX <= 1 && diffY <= 1 && diffZ <= 1 && diffX >= -1 && diffY >= -1 && diffZ >= -1) {
                // Provide feedback to the user that the device is aligned
                tvDirections.text = "Success! The device is now aligned with earth's frame of reference."
                Toast.makeText(this, "Aligned!", Toast.LENGTH_SHORT).show()
                Log.i("Alignment Status", "Success! The device is now aligned with earth's frame of reference.")
                onPause()
            }
        }
    }
}