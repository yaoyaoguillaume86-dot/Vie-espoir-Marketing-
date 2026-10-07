package com.vieespoir.marketing

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afficherAccueil()
    }

    private fun afficherAccueil() {

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
        title.setPadding(0, 0, 0, 15)

        val subtitle = TextView(this)
        subtitle.text = "Bienvenue dans votre espace"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 0, 0, 20)

        layout.addView(title)
        layout.addView(subtitle)

        ajouterBouton(layout, "📦 PRODUITS") {
            ouvrirEspace("PRODUITS")
        }

        ajouterBouton(layout, "⚙️ GESTION DES PRODUITS") {
            ouvrirEspace("GESTION DES PRODUITS")
        }

        ajouterBouton(layout, "🛒 VENTES") {
            ouvrirEspace("VENTES")
        }

        ajouterBouton(layout, "📋 COMMANDES") {
            ouvrirEspace("COMMANDES")
        }

        ajouterBouton(layout, "📦 STOCK") {
            ouvrirEspace("GESTION DU STOCK")
        }

        ajouterBouton(layout, "👥 CLIENTS") {
            ouvrirEspace("CLIENTS")
        }

        ajouterBouton(layout, "💳 MOYENS DE PAIEMENT") {
            ouvrirEspace("MOYENS DE PAIEMENT")
        }

        ajouterBouton(layout, "🚚 LIVRAISON") {
            ouvrirEspace("LIVRAISON")
        }

        ajouterBouton(layout, "📢 PUBLIER / PARTAGER") {
            partagerProduit()
        }

        ajouterBouton(layout, "💰 COMMISSIONS") {
            ouvrirEspace("COMMISSIONS")
        }

        setContentView(layout)
    }

    private fun ouvrirEspace(nom: String) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 40, 30, 30)
        layout.gravity = Gravity.CENTER_HORIZONTAL
        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)
        titre.text = nom
        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER
        titre.setPadding(0, 0, 0, 30)

        layout.addView(titre)

        val message = TextView(this)
        message.text = "Bienvenue dans l'espace $nom"
        message.textSize = 18f
        message.setTextColor(Color.DKGRAY)
        message.gravity = Gravity.CENTER
        message.setPadding(0, 0, 0, 30)

        layout.addView(message)

        val retour = Button(this)
        retour.text = "⬅ RETOUR"
        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    private fun partagerProduit() {

        val message = "Découvrez nos produits sur Vie Espoir Marketing."

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(intent, "Partager avec"))
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Aucune application de partage disponible",
                Toast.LENGTH_SHORT
            ).show()
        }
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

        params.setMargins(0, 6, 0, 6)

        bouton.layoutParams = params

        bouton.setOnClickListener {
            action()
        }

        layout.addView(bouton)
    }
}