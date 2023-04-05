package com.example.sensewatch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProximitySensor : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var proximitySensor: Sensor
    private lateinit var proximityValue: TextView
    private lateinit var db: AppDatabase
    private lateinit var proximitySensorDao: ProximitySensorDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximity_sensor)
        db = AppDatabase.getDatabase(applicationContext)
        proximitySensorDao = db.proximitySensorDao()
        proximityValue = findViewById(R.id.tv_proximityText)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            proximityValue.text = "Proximity Sensor Value: $distance"
            val data = ProximitySensorData(System.currentTimeMillis(), distance)
            if(distance <= 2){
                Log.i("From Proximity Sensor", "An object is close to the device")
            }
            CoroutineScope(Dispatchers.IO).launch {
                proximitySensorDao.insert(data)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something when the accuracy of the sensor changes
    }
}