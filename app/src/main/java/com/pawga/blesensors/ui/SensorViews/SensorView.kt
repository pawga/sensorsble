package com.pawga.blesensors.ui.SensorViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pawga.blesensors.R

/**
 * Created by sivannikov
 */

class SensorView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val type: TextView
    private val value: TextView
    private val unit: TextView
    private val colorView: View
    private val colorTextView: TextView
    private val sensorAlarm: ImageView

    init {
        LinearLayout.inflate(context, R.layout.sensor_big_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorBigView)

        type = findViewById(R.id.sensorTypeTextView)
        value = findViewById(R.id.sensorValueTextView)
        unit = findViewById(R.id.sensorUnitTextView)
        colorView = findViewById(R.id.sensorColorView)
        colorTextView = findViewById(R.id.sensorColorTextView)
        sensorAlarm = findViewById(R.id.sensorAlarmImageView)

        try {
            val typeText = attributes.getString(R.styleable.SensorBigView_type)
            val valueText = attributes.getString(R.styleable.SensorBigView_value)
            val unitText = attributes.getString(R.styleable.SensorBigView_unit)
            type.text = typeText
            value.text = valueText
            unit.text = unitText
        } finally {
            attributes.recycle()
        }
    }
}