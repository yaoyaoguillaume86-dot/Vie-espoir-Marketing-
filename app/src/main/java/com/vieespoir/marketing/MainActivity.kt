package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
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

    private fun creerBouton(texte: String): Button {
        val bouton = Button(this)
        bouton.text = texte
        bouton.textSize = 17f
        bouton.isAllCaps = false

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 8, 0, 8)
        bouton.layoutParams = params

        return bouton
    }

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(Color.rgb(0, 120, 60))
        titre.setPadding(0, 20, 0, 30)

        layout.addView(titre)

        val sousTitre = TextView(this)
        sousTitre.text = "Bienvenue dans votre application"
        sousTitre.textSize = 18f
        sousTitre.gravity = Gravity.CENTER
        sousTitre.setPadding(0, 0, 0, 20)

        layout.addView(sousTitre)

        val produitsBtn = creerBouton("📦 Produits")
        produitsBtn.setOnClickListener {
            afficherProduits()
        }
        layout.addView(produitsBtn)

        val commandesBtn = creerBouton("🛒 Commander")
        commandesBtn.setOnClickListener {
            ajouterCommande()
        }
        layout.addView(commandesBtn)

        val listeCommandesBtn = creerBouton("📋 Mes commandes")
        listeCommandesBtn.setOnClickListener {
            afficherCommandes()
        }
        layout.addView(listeCommandesBtn)

        val clientsBtn = creerBouton("👥 Clients")
        clientsBtn.setOnClickListener {
            afficherClients()
        }
        layout.addView(clientsBtn)

        val ajouterBtn = creerBouton("➕ Ajouter un produit")
        ajouterBtn.setOnClickListener {
            ajouterProduit()
        }
        layout.addView(ajouterBtn)

        val infosBtn = creerBouton("ℹ️ Informations")
        infosBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vie Espoir Marketing")
                .setMessage(
                    "Application de gestion des produits, " +
                    "clients, commandes et paiements."
                )
                .setPositiveButton("OK", null)
                .show()
        }
        layout.addView(infosBtn)

        setContentView(layout)
    }

    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "📦 PRODUITS"
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
        prix.hint = "Prix en FCFA"
        prix.inputType = InputType.TYPE_CLASS_NUMBER

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

                    val produit =
                        if (prixProduit.isNotEmpty()) {
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

    private fun ajouterCommande() {

        val client = EditText(this)
        client.hint = "Nom du client"

        val telephone = EditText(this)
        telephone.hint = "Numéro de téléphone"
        telephone.inputType = InputType.TYPE_CLASS_PHONE

        val produit = EditText(this)
        produit.hint = "Produit commandé"

        val quantite = EditText(this)
        quantite.hint = "Quantité"
        quantite.inputType = InputType.TYPE_CLASS_NUMBER

        val adresse = EditText(this)
        adresse.hint = "Lieu / adresse de livraison"

        val paiement = Spinner(this)

        val moyensPaiement = arrayOf(
            "Choisir le mode de paiement",
            "Espèces à la livraison",
            "Paiement à la livraison",
            "Orange Money",
            "MTN Money",
            "Moov Money",
            "Wave",
            "Paiement en ligne"
        )

        paiement.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            moyensPaiement
        )

        val conteneur = LinearLayout(this)
        conteneur.orientation = LinearLayout.VERTICAL
        conteneur.setPadding(25, 10, 25, 10)

        conteneur.addView(client)
        conteneur.addView(telephone)
        conteneur.addView(produit)
        conteneur.addView(quantite)
        conteneur.addView(adresse)

        val titrePaiement = TextView(this)
        titrePaiement.text = "💳 Mode de paiement"
        titrePaiement.textSize = 17f
        titrePaiement.setPadding(0, 15, 0, 5)

        conteneur.addView(titrePaiement)
        conteneur.addView(paiement)

        AlertDialog.Builder(this)
            .setTitle("🛒 Nouvelle commande")
            .setView(conteneur)
            .setPositiveButton("Confirmer la commande") { _, _ ->

                val nomClient = client.text.toString().trim()
                val numero = telephone.text.toString().trim()
                val nomProduit = produit.text.toString().trim()
                val qte = quantite.text.toString().trim()
                val lieu = adresse.text.toString().trim()

                val modePaiement =
                    paiement.selectedItem.toString()

                if (nomClient.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Veuillez entrer le nom du client",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                if (numero.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Veuillez entrer le numéro de téléphone",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                if (nomProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Veuillez entrer le produit",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                if (qte.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Veuillez entrer la quantité",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                if (lieu.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Veuillez entrer l'adresse de livraison",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                if (modePaiement == "Choisir le mode de paiement") {

                    Toast.makeText(
                        this,
                        "Veuillez choisir un mode de paiement",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                val commande =
                    "Client : $nomClient\n" +
                    "Téléphone : $numero\n" +
                    "Produit : $nomProduit\n" +
                    "Quantité : $qte\n" +
                    "Livraison : $lieu\n" +
                    "Paiement : $modePaiement\n" +
                    "Statut : En attente"

                commandes.add(commande)

                if (!clients.contains(nomClient)) {
                    clients.add(nomClient)
                }

                Toast.makeText(
                    this,
                    "Commande enregistrée avec succès",
                    Toast.LENGTH_LONG
                ).show()

                if (
                    modePaiement == "Orange Money" ||
                    modePaiement == "MTN Money" ||
                    modePaiement == "Moov Money" ||
                    modePaiement == "Wave" ||
                    modePaiement == "Paiement en ligne"
                ) {

                    AlertDialog.Builder(this)
                        .setTitle("💳 Paiement")
                        .setMessage(
                            "Mode : $modePaiement\n\n" +
                            "Numéro utilisé : $numero\n\n" +
                            "La commande est enregistrée.\n" +
                            "Le paiement réel sera effectué après " +
                            "connexion du service de paiement."
                        )
                        .setPositiveButton("OK", null)
                        .show()
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
        titre.text = "📋 COMMANDES"
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

            for ((index, commande) in commandes.withIndex()) {

                val ligne = TextView(this)

                ligne.text =
                    "Commande ${index + 1}\n\n$commande"

                ligne.textSize = 16f
                ligne.setPadding(15, 20, 15, 20)

                layout.addView(ligne)

                val separateur = View(this)
                layout.addView(
                    separateur,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        2
                    )
                )
            }
        }

        val nouvelle = creerBouton("➕ Nouvelle commande")
        nouvelle.setOnClickListener {
            ajouterCommande()
        }

        layout.addView(nouvelle)

        val retour = creerBouton("⬅️ Retour")
        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
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