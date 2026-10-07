package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList

class MainActivity : Activity() {

    private val produits = ArrayList<String>()
    private val commandes = ArrayList<String>()
    private val clients = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afficherAccueil()
    }

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 26f
        titre.setTextColor(Color.rgb(0, 120, 60))
        titre.gravity = Gravity.CENTER
        titre.setPadding(0, 20, 0, 30)

        layout.addView(
            titre,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val sousTitre = TextView(this)
        sousTitre.text = "Bienvenue dans votre application"
        sousTitre.textSize = 18f
        sousTitre.gravity = Gravity.CENTER
        sousTitre.setPadding(0, 0, 0, 30)

        layout.addView(sousTitre)

        val boutonProduits = creerBouton("📦 Produits")
        boutonProduits.setOnClickListener {
            afficherProduits()
        }
        layout.addView(boutonProduits)

        val boutonCommandes = creerBouton("🛒 Commandes")
        boutonCommandes.setOnClickListener {
            afficherCommandes()
        }
        layout.addView(boutonCommandes)

        val boutonClients = creerBouton("👥 Clients")
        boutonClients.setOnClickListener {
            afficherClients()
        }
        layout.addView(boutonClients)

        val boutonAjouter = creerBouton("➕ Ajouter un produit")
        boutonAjouter.setOnClickListener {
            ajouterProduit()
        }
        layout.addView(boutonAjouter)

        val boutonInfos = creerBouton("ℹ️ Informations")
        boutonInfos.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vie Espoir Marketing")
                .setMessage(
                    "Application de gestion des produits, " +
                    "commandes et clients."
                )
                .setPositiveButton("OK", null)
                .show()
        }
        layout.addView(boutonInfos)

        setContentView(layout)
    }

    private fun creerBouton(texte: String): Button {

        val bouton = Button(this)
        bouton.text = texte
        bouton.textSize = 17f
        bouton.isAllCaps = false

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 10, 0, 10)

        bouton.layoutParams = params

        return bouton
    }

    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "📦 LISTE DES PRODUITS"
        titre.textSize = 24f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(Color.rgb(0, 120, 60))

        layout.addView(titre)

        if (produits.isEmpty()) {

            val message = TextView(this)
            message.text = "Aucun produit enregistré."
            message.textSize = 18f
            message.setPadding(0, 30, 0, 30)

            layout.addView(message)

        } else {

            for (produit in produits) {

                val ligne = TextView(this)
                ligne.text = "• $produit"
                ligne.textSize = 18f
                ligne.setPadding(10, 15, 10, 15)

                layout.addView(ligne)
            }
        }

        val ajouter = creerBouton("➕ Ajouter un produit")
        ajouter.setOnClickListener {
            ajouterProduit()
        }

        layout.addView(ajouter)

        val retour = creerBouton("⬅️ Retour")
        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    private fun ajouterProduit() {

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix du produit"
        prix.inputType = 2

        val conteneur = LinearLayout(this)
        conteneur.orientation = LinearLayout.VERTICAL
        conteneur.setPadding(30, 10, 30, 10)

        conteneur.addView(nom)
        conteneur.addView(prix)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un produit")
            .setView(conteneur)
            .setPositiveButton("Enregistrer") { _, _ ->

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
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun afficherCommandes() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "🛒 COMMANDES"
        titre.textSize = 24f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(Color.rgb(0, 120, 60))

        layout.addView(titre)

        if (commandes.isEmpty()) {

            val message = TextView(this)
            message.text = "Aucune commande enregistrée."
            message.textSize = 18f
            message.setPadding(0, 30, 0, 30)

            layout.addView(message)

        } else {

            for (commande in commandes) {

                val ligne = TextView(this)
                ligne.text = "• $commande"
                ligne.textSize = 18f
                ligne.setPadding(10, 15, 10, 15)

                layout.addView(ligne)
            }
        }

        val nouvelleCommande = creerBouton("➕ Nouvelle commande")

        nouvelleCommande.setOnClickListener {
            ajouterCommande()
        }

        layout.addView(nouvelleCommande)

        val retour = creerBouton("⬅️ Retour")

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    private fun ajouterCommande() {

        val client = EditText(this)
        client.hint = "Nom du client"

        val produit = EditText(this)
        produit.hint = "Produit commandé"

        val quantite = EditText(this)
        quantite.hint = "Quantité"
        quantite.inputType = 2

        val adresse = EditText(this)
        adresse.hint = "Adresse de livraison"

        val conteneur = LinearLayout(this)
        conteneur.orientation = LinearLayout.VERTICAL
        conteneur.setPadding(30, 10, 30, 10)

        conteneur.addView(client)
        conteneur.addView(produit)
        conteneur.addView(quantite)
        conteneur.addView(adresse)

        AlertDialog.Builder(this)
            .setTitle("Nouvelle commande")
            .setView(conteneur)
            .setPositiveButton("Enregistrer") { _, _ ->

                val nomClient = client.text.toString().trim()
                val nomProduit = produit.text.toString().trim()
                val qte = quantite.text.toString().trim()
                val lieu = adresse.text.toString().trim()

                if (
                    nomClient.isNotEmpty() &&
                    nomProduit.isNotEmpty()
                ) {

                    val commande =
                        "Client: $nomClient\n" +
                        "Produit: $nomProduit\n" +
                        "Quantité: $qte\n" +
                        "Livraison: $lieu"

                    commandes.add(commande)

                    if (!clients.contains(nomClient)) {
                        clients.add(nomClient)
                    }

                    Toast.makeText(
                        this,
                        "Commande enregistrée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun afficherClients() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "👥 CLIENTS"
        titre.textSize = 24f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(Color.rgb(0, 120, 60))

        layout.addView(titre)

        if (clients.isEmpty()) {

            val message = TextView(this)
            message.text = "Aucun client enregistré."
            message.textSize = 18f
            message.setPadding(0, 30, 0, 30)

            layout.addView(message)

        } else {

            for (client in clients) {

                val ligne = TextView(this)
                ligne.text = "• $client"
                ligne.textSize = 18f
                ligne.setPadding(10, 15, 10, 15)

                layout.addView(ligne)
            }
        }

        val retour = creerBouton("⬅️ Retour")

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    override fun onBackPressed() {
        afficherAccueil()
    }
}