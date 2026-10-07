package com.vieespoir.marketing

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        afficherAccueil()
    }

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(40, 40, 40, 40)
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

        val button = Button(this)
        button.text = "COMMENCER"

        button.setOnClickListener {
            afficherMenu()
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(button)

        setContentView(layout)
    }

    private fun afficherMenu() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "ESPACE VIE ESPOIR MARKETING"
        title.textSize = 24f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        val produits = Button(this)
        produits.text = "NOS PRODUITS"

        val commandes = Button(this)
        commandes.text = "COMMANDES"

        val stock = Button(this)
        stock.text = "GESTION DU STOCK"

        val ventes = Button(this)
        ventes.text = "MES VENTES"

        layout.addView(title)
        layout.addView(produits)
        layout.addView(commandes)
        layout.addView(stock)
        layout.addView(ventes)

        setContentView(layout)
    }
}