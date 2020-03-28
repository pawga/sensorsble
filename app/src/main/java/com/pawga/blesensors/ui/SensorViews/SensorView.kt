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
    private val type: ImageView
    private val value: TextView
    private val unit: ImageView

    init {
        LinearLayout.inflate(context, R.layout.sensor_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorView)

        type = findViewById(R.id.imageTypeView)
        value = findViewById(R.id.valueTextView)
        unit = findViewById(R.id.unitImageView)

        try {
            type.setImageDrawable(attributes.getDrawable(R.styleable.SensorView_smallType))
            value.text = attributes.getString(R.styleable.SensorView_smallValue)
            unit.setImageDrawable(attributes.getDrawable(R.styleable.SensorView_smallUnit))
        } finally {
            attributes.recycle()
        }
    }
}