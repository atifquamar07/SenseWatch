package com.example.sensewatch

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "light_sensor_data")
data class LightSensorData(
    @PrimaryKey(autoGenerate = true)
    val timestamp: Long,
    val value: Float,
)
@Entity(tableName = "proximity_sensor_data")
data class ProximitySensorData(
    @PrimaryKey(autoGenerate = true)
    val timestamp: Long,
    val value: Float,
)
@Entity(tableName = "geomagnetic_sensor_data")
data class GeomagneticSensorData(
    @PrimaryKey(autoGenerate = true)
    val timestamp: Long,
    val XCoordinate: Float,
    val YCoordinate: Float,
    val ZCoordinate: Float,
)
@Dao
interface LightSensorDao {
    @Insert
    fun insert(vararg entities: LightSensorData)
    @Query("SELECT * FROM light_sensor_data")
    fun getAllData(): Flow<List<LightSensorData>>
}
@Dao
interface ProximitySensorDao {
    @Insert
    fun insert(vararg entities: ProximitySensorData)
    @Query("SELECT * FROM proximity_sensor_data")
    fun getAllData(): Flow<List<ProximitySensorData>>
}
@Dao
interface GeomagneticSensorDao {
    @Insert
    fun insert(vararg entities: GeomagneticSensorData)
    @Query("SELECT * FROM geomagnetic_sensor_data")
    fun getAllData(): Flow<List<GeomagneticSensorData>>
}

@Database(entities = [LightSensorData::class, ProximitySensorData::class, GeomagneticSensorData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lightSensorDao(): LightSensorDao
    abstract fun proximitySensorDao(): ProximitySensorDao
    abstract fun geomagneticSensorDao(): GeomagneticSensorDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database2"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }

}



class MainActivity : AppCompatActivity() {

    private lateinit var btnRotation: Button
    private lateinit var btnAlignPhone: Button
    private lateinit var btnProximitySensor: Button
    private lateinit var btnLightSensor: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRotation = findViewById(R.id.btn_rotation)
        btnRotation.setOnClickListener {
            val intent = Intent(this, CurrentCoordinates::class.java)
            startActivity(intent)
        }
        btnAlignPhone = findViewById(R.id.btn_alignPhone)
        btnAlignPhone.setOnClickListener {
            val intent = Intent(this, GeomagneticRotation::class.java)
            startActivity(intent)
        }
        btnProximitySensor = findViewById(R.id.btn_proxySensor)
        btnProximitySensor.setOnClickListener {
            val intent = Intent(this, ProximitySensor::class.java)
            startActivity(intent)
        }
        btnLightSensor = findViewById(R.id.btn_lightSensor)
        btnLightSensor.setOnClickListener {
            val intent = Intent(this, LightSensor::class.java)
            startActivity(intent)
        }
    }

}