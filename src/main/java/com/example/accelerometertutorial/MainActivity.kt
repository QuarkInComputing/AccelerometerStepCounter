package com.example.accelerometertutorial

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import com.example.accelerometertutorial.ui.theme.AccelerometerTutorialTheme
import kotlin.math.sqrt

private val counter = StepCounter()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AccelerometerTutorialTheme {
                AcceleromterApp()
            }
        }
    }
}

@Composable
fun AcceleromterApp() {
    //Get sensor manager from system service and connect to UI
    val context = LocalContext.current
    val sensorManager = remember {
        getSystemService(context, SensorManager::class.java)
    }

    //Store sensor readings as state
    var x by remember { mutableFloatStateOf(0f)}
    var y by remember { mutableFloatStateOf(0f)}
    var z by remember { mutableFloatStateOf(0f)}

    var steps by remember { mutableIntStateOf(0)}
    var mag = 0f

    var currentTime : Long = 0

    //Registering listener to detect changes
    DisposableEffect(Unit) {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                x = event.values[0]
                y = event.values[1]
                z = event.values[2]
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    mag = (sqrt((x*x)+(y*y)+(z*z)))-9.8f
    currentTime = System.currentTimeMillis()

    steps = counter.StepChecker(mag, currentTime)

    AccelerometerScreen(x,y,z,steps)
}

@Composable
fun AccelerometerScreen(x: Float, y: Float, z: Float, steps: Int) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background //Sets colour scheme according to device theme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Accelerometer Data",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("X = %.2f".format(x), fontSize = 18.sp)
            Text("Y = %.2f".format(y), fontSize = 18.sp)
            Text("Z = %.2f".format(z), fontSize = 18.sp)
            Text("Steps = $steps", fontSize = 18.sp)
        }
    }
}

@Preview
@Composable
fun AcceleromterPreview(){
    AccelerometerScreen(0.67f, 0.63f, -0.21f, 512)
}