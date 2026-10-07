package com.vieespoir.marketing

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 40, 30, 30)
        layout.gravity = Gravity.CENTER_HORIZONTAL
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "VIE ESPOIR MARKETING"
        title.textSize = 26f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER
        title.setPadding(0, 0, 0, 20)

        val subtitle = TextView(this)
        subtitle.text = "Bienvenue dans votre espace"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 0, 0, 30)

        layout.addView(title)
        layout.addView(subtitle)

        ajouterBouton(layout, "PRODUITS") {
            Toast.makeText(this, "Espace Produits", Toast.LENGTH_SHORT).show()
        }

        ajouterBouton(layout, "COMMANDES") {
            Toast.makeText(this, "Espace Commandes", Toast.LENGTH_SHORT).show()
        }

        ajouterBouton(layout, "STOCK") {
            Toast.makeText(this, "Gestion du Stock", Toast.LENGTH_SHORT).show()
        }

        ajouterBouton(layout, "VENTES") {
            Toast.makeText(this, "Espace Ventes", Toast.LENGTH_SHORT).show()
        }

        ajouterBouton(layout, "CLIENTS") {
            Toast.makeText(this, "Espace Clients", Toast.LENGTH_SHORT).show()
        }

        ajouterBouton(layout, "COMMISSIONS") {
            Toast.makeText(this, "Espace Commissions", Toast.LENGTH_SHORT).show()
        }

        setContentView(layout)
    }

    private fun ajouterBouton(
        layout: LinearLayout,
        texte: String,
        action: () -> Unit
    ) {
        val bouton = Button(this)
        bouton.text = texte
        bouton.textSize = 16f

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 8, 0, 8)

        bouton.layoutParams = params
        bouton.setOnClickListener {
            action()
        }

        layout.addView(bouton)
    }
}