package com.wilderpereira.pointer

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val TAG = "MainActivity"
    private lateinit var mSensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var accelerometerValues: FloatArray? = null
    private var pressing = false
    private var drawingMode = "point"

    private lateinit var presenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter()
        presenter.signInUser()

        initializeSensors()
        handleDrawingModeChanges()

        handlePointButtonInteraction()
    }

    private fun initializeSensors() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun handleDrawingModeChanges() {
        btnFocusMode.setOnClickListener {
            changeButtonFocusColor(it, btnPointMode)
            drawingMode = "focus"
        }

        btnPointMode.setOnClickListener {
            changeButtonFocusColor(it, btnFocusMode)
            drawingMode = "point"
        }
    }

    private fun changeButtonFocusColor(focusedView: View, unfocusedView: View) {
        focusedView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        unfocusedView.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
    }


    private fun handlePointButtonInteraction() {
        btnPoint.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pressing = true
                MotionEvent.ACTION_UP -> {
                    pausePointer()
                }
            }
            false
        }
    }

    private fun pausePointer() {
        pressing = false
        presenter.pauseCoordinates()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accelerometer ->
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (pressing) {
            updateAccelerometerInfo(sensorEvent)
        }
    }

    private fun updateAccelerometerInfo(sensorEvent: SensorEvent?) {
        accelerometerValues = lowPassFilter(sensorEvent!!.values, accelerometerValues)

        val x = accelerometerValues!![0]
        val y = accelerometerValues!![1]
        val z = accelerometerValues!![2]

        presenter.updateAccelerometerInfo(drawingMode, x, y, z)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
