package com.pawga.blesensors.ui.SensorViews

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.pawga.blesensors.R

/**
 * Created by sivannikov
 */
class SensorBigView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    init {
        LinearLayout.inflate(context, R.layout.sensor_big_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorBigView)
        try {
            val imageView: ImageView = findViewById(R.id.image)
            val textView: TextView = findViewById(R.id.caption)

            val text = attributes.getString(R.styleable.SensorBigView_text)
            val drawableId = attributes.getResourceId(R.styleable.SensorBigView_image, 0)
            if (drawableId != 0) {
                val drawable = AppCompatResources.getDrawable(context, drawableId)
                image_thumb.setImageDrawable(drawable)
            }
            text_title.text = text
        } finally {
            attributes.recycle()
        }

        val imageView: ImageView = findViewById(R.id.image)
        val textView: TextView = findViewById(R.id.caption)

        val attributeqs = context.obtainStyledAttributes(attrs, R.styleable.BenefitView)
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.BenefitView_image))
        textView.text = attributes.getString(R.styleable.BenefitView_text)
        attributes.recycle()

    }
}