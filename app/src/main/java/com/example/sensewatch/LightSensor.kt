package com.example.sensewatch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.facebook.stetho.Stetho
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LightSensor : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightValue: TextView
    private lateinit var db: AppDatabase
    private lateinit var lightSensorDao: LightSensorDao
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_sensor)
        Stetho.initializeWithDefaults(this)

        lightValue = findViewById(R.id.tv_lightText)
        db = AppDatabase.getDatabase(applicationContext)
        lightSensorDao = db.lightSensorDao()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        btnStart = findViewById(R.id.btn_start_light)

        onPause()
        var isStarted = true

        btnStart.setOnClickListener {
            if(!isStarted){
                isStarted = true
                lightValue.visibility = View.VISIBLE
                onResume()
                btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                btnStart.text = "Stop Sensor"
            }
            else {
                isStarted = false
                lightValue.visibility = View.GONE
                btnStart.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                onPause()
                btnStart.text = "Start Sensor"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]
            lightValue.text = "Light Sensor Value: $light"
            val data = LightSensorData(System.currentTimeMillis(), light)
            if(light <= 2){
                Log.i("From Light Sensor", "Light is being obstructed by an object")
            }
            CoroutineScope(Dispatchers.IO).launch {
                lightSensorDao.insert(data)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something when the accuracy of the sensor changes
    }
}