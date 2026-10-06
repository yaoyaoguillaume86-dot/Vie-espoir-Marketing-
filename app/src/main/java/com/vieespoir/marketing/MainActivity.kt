package com.example.vieespoirmarketing

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(32, 32, 32, 32)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "VIE ESPOIR MARKETING"
        title.textSize = 28f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Bienvenue dans votre espace"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        layout.addView(title)
        layout.addView(subtitle)

        setContentView(layout)
    }
}