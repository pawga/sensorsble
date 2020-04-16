package com.pawga.blesensors.ui.SensorViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.pawga.blesensors.R

/**
 * Created by sivannikov
 */

class SensorBigView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val type: TextView
    private val valueText: TextView
    private val unit: TextView
    private val colorView: View
    private val colorTextView: TextView
    private val sensorAlarm: ImageView
    private val format: String

    private var _value: Double = 0.0
    var value: Double?
        get() = _value
        set(value) {
            _value = value ?: 0.0
            valueText.text = format.format(_value)
            setIndicatorColor()
        }

    init {
        LinearLayout.inflate(context, R.layout.sensor_big_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorBigView)

        type = findViewById(R.id.sensorTypeTextView)
        valueText = findViewById(R.id.sensorValueTextView)
        unit = findViewById(R.id.sensorUnitTextView)
        colorView = findViewById(R.id.sensorColorView)
        colorTextView = findViewById(R.id.sensorColorTextView)
        sensorAlarm = findViewById(R.id.sensorAlarmImageView)

        try {
            type.text = attributes.getString(R.styleable.SensorBigView_bigType)
            valueText.text = attributes.getString(R.styleable.SensorBigView_bigValue)
            unit.text  = attributes.getString(R.styleable.SensorBigView_bigUnit)
            format = attributes.getString(R.styleable.SensorBigView_bigUnitFormatString) ?: "%.0f"
        } finally {
            attributes.recycle()
        }
    }

    private fun setIndicatorColor() = when(value?.toInt() ?: 0) {
        in 0..1000 -> {
            colorView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen))
            colorTextView.text = resources.getText(R.string.good)
        }
        in 1001..2000 -> {
            colorView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorYellow))
            colorTextView.text = resources.getText(R.string.attention)
        }
        in 2001..5000 -> {
            colorView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed))
            colorTextView.text = resources.getText(R.string.dangerous)
        }
        else -> {
            colorView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBrown))
            colorTextView.text = resources.getText(R.string.very_dangerous)
        }
    }
}