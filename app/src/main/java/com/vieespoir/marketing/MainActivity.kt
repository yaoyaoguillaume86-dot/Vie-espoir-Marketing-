package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList

class MainActivity : Activity() {

    // Liste des produits
    private val produits = ArrayList<String>()

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

        val subtitle = TextView(this)
        subtitle.text = "Bienvenue dans votre espace"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 10, 0, 20)

        layout.addView(title)
        layout.addView(subtitle)

        ajouterBouton(layout, "📦 PRODUITS") {
            afficherProduits()
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

        ajouterBouton(layout, "💰 COMMISSIONS") {
            ouvrirEspace("COMMISSIONS")
        }

        setContentView(layout)
    }

    // Écran PRODUITS
    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 30, 25, 30)
        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)
        titre.text = "📦 MES PRODUITS"
        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER
        titre.setPadding(0, 0, 0, 20)

        layout.addView(titre)

        // Bouton ajouter
        val ajouter = Button(this)
        ajouter.text = "➕ AJOUTER UN PRODUIT"
        ajouter.textSize = 16f

        ajouter.setOnClickListener {
            afficherFenetreAjout()
        }

        layout.addView(ajouter)

        // Liste des produits
        afficherListeProduits(layout)

        // Bouton retour
        val retour = Button(this)
        retour.text = "⬅ RETOUR"
        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // Afficher la liste des produits
    private fun afficherListeProduits(layout: LinearLayout) {

        if (produits.isEmpty()) {

            val vide = TextView(this)
            vide.text = "Aucun produit ajouté pour le moment."
            vide.textSize = 17f
            vide.setTextColor(Color.DKGRAY)
            vide.gravity = Gravity.CENTER
            vide.setPadding(0, 30, 0, 30)

            layout.addView(vide)

        } else {

            for (produit in produits) {

                val ligne = LinearLayout(this)
                ligne.orientation = LinearLayout.HORIZONTAL
                ligne.gravity = Gravity.CENTER_VERTICAL
                ligne.setPadding(0, 10, 0, 10)

                val nom = TextView(this)
                nom.text = "📦 $produit"
                nom.textSize = 17f
                nom.setTextColor(Color.BLACK)

                val paramsNom = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )

                nom.layoutParams = paramsNom

                val partager = Button(this)
                partager.text = "📤 PARTAGER"

                partager.setOnClickListener {
                    partagerProduit(produit)
                }

                ligne.addView(nom)
                ligne.addView(partager)

                layout.addView(ligne)
            }
        }
    }

    // Fenêtre pour ajouter un produit
    private fun afficherFenetreAjout() {

        val zone = LinearLayout(this)
        zone.orientation = LinearLayout.VERTICAL
        zone.setPadding(40, 10, 40, 10)

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix du produit"
        prix.inputType = 2

        zone.addView(nom)
        zone.addView(prix)

        AlertDialog.Builder(this)
            .setTitle("➕ Ajouter un produit")
            .setView(zone)
            .setNegativeButton("ANNULER", null)
            .setPositiveButton("AJOUTER") { _, _ ->

                val nomProduit = nom.text.toString().trim()
                val prixProduit = prix.text.toString().trim()

                if (nomProduit.isNotEmpty()) {

                    val produit = if (prixProduit.isNotEmpty()) {
                        "$nomProduit — $prixProduit FCFA"
                    } else {
                        nomProduit
                    }

                    produits.add(produit)

                    Toast.makeText(
                        this,
                        "Produit ajouté avec succès",
                        Toast.LENGTH_SHORT
                    ).show()

                    afficherProduits()

                } else {

                    Toast.makeText(
                        this,
                        "Veuillez entrer le nom du produit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // Partager un produit
    private fun partagerProduit(produit: String) {

        val message =
            "📦 VIE ESPOIR MARKETING\n\n" +
            "Découvrez notre produit :\n" +
            "$produit\n\n" +
            "Contactez-nous pour plus d'informations."

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(
            Intent.createChooser(
                intent,
                "Partager le produit avec"
            )
        )
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