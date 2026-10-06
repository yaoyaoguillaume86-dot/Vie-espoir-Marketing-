package com.vieespoir.marketing

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "VIE ESPOIR MARKETING"
        title.textSize = 28f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        layout.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Bienvenue dans votre espace"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        layout.addView(subtitle)

        val produits = Button(this)
        produits.text = "🛒 Produits"
        layout.addView(produits)

        val stock = Button(this)
        stock.text = "📦 Stock"
        layout.addView(stock)

        val clients = Button(this)
        clients.text = "👥 Clients"
        layout.addView(clients)

        val ventes = Button(this)
        ventes.text = "💰 Ventes"
        layout.addView(ventes)

        val finances = Button(this)
        finances.text = "📊 Finances"
        layout.addView(finances)

        val notifications = Button(this)
        notifications.text = "🔔 Notifications"
        layout.addView(notifications)

        val administration = Button(this)
        administration.text = "⚙️ Administration"
        layout.addView(administration)

        setContentView(layout)
    }
}