package com.pawga.blesensors.ui.SensorViews

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pawga.blesensors.R

/**
 * Created by sivannikov
 */

class SensorView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val type: ImageView
    private val valueText: TextView
    private val unit: TextView
    private val format: String

    private var _value: Double = 0.0
    var value: Double?
        get() = _value
        set(value) {
            _value = value ?: 0.0
            valueText.text = format.format(_value)
        }

    init {
        LinearLayout.inflate(context, R.layout.sensor_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorView)

        type = findViewById(R.id.imageTypeView)
        valueText = findViewById(R.id.valueTextView)
        unit = findViewById(R.id.unitValueView)

        try {
            type.setImageDrawable(attributes.getDrawable(R.styleable.SensorView_smallType))
            valueText.text = attributes.getString(R.styleable.SensorView_smallValue)
            unit.text = attributes.getString(R.styleable.SensorView_smallUnit)
            format = attributes.getString(R.styleable.SensorView_smallUnitFormatString) ?: "%.2f"
        } finally {
            attributes.recycle()
        }
    }
}
