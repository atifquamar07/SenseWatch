package com.example.sensewatch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs

class GeomagneticRotation : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor
    private lateinit var targetOrientation: FloatArray
    private lateinit var currentOrientation: FloatArray
    private lateinit var tvDirections: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geomagnetic_rotation)

        // Initialize the sensor manager and rotation vector sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        // Initialize the target orientation and current orientation arrays
        targetOrientation = FloatArray(3)
        currentOrientation = FloatArray(3)

        // Initialize the button
//        btnSetTarget = findViewById(R.id.btn_setTarget)
        tvDirections = findViewById(R.id.tv_directions)

//        // Set a click listener for the button
//        btnSetTarget.setOnClickListener {
//            // Set the current orientation as the target orientation
//            targetOrientation = currentOrientation
//            // Provide feedback to the user
//            Toast.makeText(this, "Target orientation set!", Toast.LENGTH_SHORT).show()
//        }
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
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert the rotation vector to a rotation matrix
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            // Convert the rotation matrix to an orientation
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            // Convert the orientation from radians to degrees
            currentOrientation[0] = Math.toDegrees(orientation[0].toDouble()).toFloat()
            currentOrientation[1] = Math.toDegrees(orientation[1].toDouble()).toFloat()
            currentOrientation[2] = Math.toDegrees(orientation[2].toDouble()).toFloat()

            // Calculate the difference between the current orientation and the target orientation
            val diffX = abs(currentOrientation[0] - targetOrientation[0])
            val diffY = abs(currentOrientation[1] - targetOrientation[1])
            val diffZ = abs(currentOrientation[2] - targetOrientation[2])

            // Provide feedback to the user about how much rotation is needed
            val feedback = "To align with earth's frame of reference\n\nRotate $diffX degrees around the X-axis.\nRotate $diffY degrees around the Y-axis.\nRotate $diffZ degrees around the Z-axis."
            tvDirections.text = feedback

            // Check if the device is aligned with earth's frame of reference
            if (diffX <= 1 && diffY <= 1 && diffZ <= 1) {
                // Provide feedback to the user that the device is aligned
                tvDirections.text = "Success! The device is now aligned with earth's frame of reference."
                Toast.makeText(this, "Success! The device is now aligned with earth's frame of reference.", Toast.LENGTH_SHORT).show()
                onPause()
            }
        }
    }
}