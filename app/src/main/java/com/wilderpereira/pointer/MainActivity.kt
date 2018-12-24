package com.wilderpereira.pointer

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), SensorEventListener {

    private val TAG = "MainActivity"
    private lateinit var mSensorManager: SensorManager
    private val mPaint = Paint()
    private var mCanvas: Canvas? = null
    private lateinit var mBitmap: Bitmap
    private var accelerometer: Sensor? = null
    private var accelerometerValues: FloatArray? = null

    private lateinit var presenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter()
        presenter.signInUser()

        initializeSensors()
    }

    private fun initializeSensors() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }


    private fun displayMaxHeightAndWidth() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        maxX.text = "Max x: $width"
        maxY.text = "Max y: $height"
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accelerometer ->
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
        }

        canvasView.post {
            mPaint.color = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
            mBitmap = Bitmap.createBitmap(canvasView.width, canvasView.height, Bitmap.Config.ARGB_8888)
            canvasView.setImageBitmap(mBitmap)
            mCanvas = Canvas(mBitmap)
            displayMaxHeightAndWidth()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        accelerometerValues = lowPassFilter(sensorEvent!!.values, accelerometerValues)

        val x = accelerometerValues!![0]
        val y = accelerometerValues!![1]
        val z = accelerometerValues!![2]
        xTv.text = "X: $x"
        yTv.text = "Y: $y"
        zTv.text = "Z: $z"

        presenter.updateAccelerometerInfo(x, y, z)

        if (mCanvas != null) {
            mCanvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            mCanvas!!.drawCircle(((canvasView.width/2) - (x*canvasView.width/16)).roundToInt().toFloat(),
                    ((canvasView.height/2) - (y*canvasView.height/16)).roundToInt().toFloat(), 30f, mPaint)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
