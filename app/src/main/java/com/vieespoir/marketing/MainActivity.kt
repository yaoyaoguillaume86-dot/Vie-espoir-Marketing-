package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList

class MainActivity : Activity() {

    private val produits = ArrayList<String>()
    private val commandes = ArrayList<String>()
    private val clients = ArrayList<String>()

    private lateinit var contenu: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afficherAccueil()
    }

    // =========================
    // OUTILS
    // =========================

    private fun baseLayout(): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)
        layout.setBackgroundColor(Color.WHITE)
        return layout
    }

    private fun titre(texte: String): TextView {
        val tv = TextView(this)
        tv.text = texte
        tv.textSize = 25f
        tv.setTextColor(Color.rgb(0, 110, 70))
        tv.gravity = Gravity.CENTER
        tv.setPadding(10, 20, 10, 20)
        return tv
    }

    private fun bouton(texte: String): Button {
        val b = Button(this)
        b.text = texte
        b.textSize = 18f
        b.setTextColor(Color.DKGRAY)
        b.setBackgroundColor(Color.rgb(220, 222, 222))

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            65
        )
        params.setMargins(0, 8, 0, 8)
        b.layoutParams = params

        return b
    }

    private fun retour(layout: LinearLayout) {
        val b = bouton("⬅ RETOUR À L'ACCUEIL")
        b.setOnClickListener {
            afficherAccueil()
        }
        layout.addView(b)
    }

    // =========================
    // ACCUEIL
    // =========================

    private fun afficherAccueil() {

        val layout = baseLayout()

        val logo = TextView(this)
        logo.text = "🦁"
        logo.textSize = 70f
        logo.gravity = Gravity.CENTER
        layout.addView(logo)

        layout.addView(titre("VIE ESPOIR MARKETING"))

        val sousTitre = TextView(this)
        sousTitre.text = "Votre plateforme de vente"
        sousTitre.textSize = 20f
        sousTitre.gravity = Gravity.CENTER
        sousTitre.setPadding(0, 0, 0, 20)
        layout.addView(sousTitre)

        val abonnement = bouton("⭐ ABONNEMENT VENDEUR")
        abonnement.setOnClickListener {
            afficherAbonnement()
        }
        layout.addView(abonnement)

        val produitsBtn = bouton("📦 PRODUITS")
        produitsBtn.setOnClickListener {
            afficherProduits()
        }
        layout.addView(produitsBtn)

        val gestionProduits = bouton("⚙️ GESTION DES PRODUITS")
        gestionProduits.setOnClickListener {
            afficherGestionProduits()
        }
        layout.addView(gestionProduits)

        val ventes = bouton("💰 VENTES")
        ventes.setOnClickListener {
            afficherVentes()
        }
        layout.addView(ventes)

        val commandesBtn = bouton("📋 COMMANDES")
        commandesBtn.setOnClickListener {
            afficherCommandes()
        }
        layout.addView(commandesBtn)

        val stock = bouton("📦 GESTION DU STOCK")
        stock.setOnClickListener {
            afficherStock()
        }
        layout.addView(stock)

        val clientsBtn = bouton("👥 CLIENTS")
        clientsBtn.setOnClickListener {
            afficherClients()
        }
        layout.addView(clientsBtn)

        val paiement = bouton("💳 MOYENS DE PAIEMENT")
        paiement.setOnClickListener {
            afficherPaiements()
        }
        layout.addView(paiement)

        val livraison = bouton("🚚 LIVRAISON")
        livraison.setOnClickListener {
            afficherLivraison()
        }
        layout.addView(livraison)

        val commissions = bouton("💰 COMMISSIONS")
        commissions.setOnClickListener {
            afficherCommissions()
        }
        layout.addView(commissions)

        val aide = bouton("🆘 AIDE / ASSISTANCE")
        aide.setOnClickListener {
            afficherAide()
        }
        layout.addView(aide)

        setContentView(layout)
    }

    // =========================
    // ABONNEMENT VENDEUR
    // =========================

    private fun afficherAbonnement() {

        val layout = baseLayout()
        layout.addView(titre("⭐ ABONNEMENT VENDEUR"))

        val info = TextView(this)
        info.text =
            "Choisissez un pack vendeur pour publier vos produits et vendre sur Vie Espoir Marketing."
        info.textSize = 18f
        info.setPadding(10, 10, 10, 20)
        layout.addView(info)

        val packs = arrayOf(
            "PACK STARTER - 10 000 FCFA",
            "PACK STANDARD - 25 000 FCFA",
            "PACK PRO - 50 000 FCFA",
            "PACK PREMIUM - 100 000 FCFA"
        )

        for (pack in packs) {
            val b = bouton(pack)
            b.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Abonnement vendeur")
                    .setMessage(
                        "$pack\n\n" +
                        "Votre demande d'abonnement a été sélectionnée.\n\n" +
                        "Choisissez ensuite votre moyen de paiement."
                    )
                    .setPositiveButton("CONTINUER", null)
                    .setNegativeButton("ANNULER", null)
                    .show()
            }
            layout.addView(b)
        }

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // PRODUITS
    // =========================

    private fun afficherProduits() {

        val layout = baseLayout()
        layout.addView(titre("📦 PRODUITS"))

        if (produits.isEmpty()) {

            val vide = TextView(this)
            vide.text = "Aucun produit enregistré."
            vide.textSize = 18f
            vide.gravity = Gravity.CENTER
            vide.setPadding(10, 30, 10, 30)
            layout.addView(vide)

        } else {

            for (produit in produits) {
                val tv = TextView(this)
                tv.text = "📦 $produit"
                tv.textSize = 18f
                tv.setPadding(15, 15, 15, 15)
                layout.addView(tv)
            }
        }

        val ajouter = bouton("➕ AJOUTER UN PRODUIT")
        ajouter.setOnClickListener {
            ajouterProduit()
        }
        layout.addView(ajouter)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // AJOUT PRODUIT
    // =========================

    private fun ajouterProduit() {

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(20, 10, 20, 10)

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix du produit"
        prix.inputType = 2

        val quantite = EditText(this)
        quantite.hint = "Quantité"
        quantite.inputType = 2

        container.addView(nom)
        container.addView(prix)
        container.addView(quantite)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un produit")
            .setView(container)
            .setPositiveButton("ENREGISTRER") { _, _ ->

                val nomProduit = nom.text.toString().trim()
                val prixProduit = prix.text.toString().trim()
                val qte = quantite.text.toString().trim()

                if (nomProduit.isNotEmpty()) {

                    produits.add(
                        "$nomProduit | Prix : $prixProduit FCFA | Stock : $qte"
                    )

                    Toast.makeText(
                        this,
                        "Produit ajouté avec succès",
                        Toast.LENGTH_SHORT
                    ).show()

                    afficherProduits()
                }
            }
            .setNegativeButton("ANNULER", null)
            .show()
    }

    // =========================
    // GESTION PRODUITS
    // =========================

    private fun afficherGestionProduits() {

        val layout = baseLayout()
        layout.addView(titre("⚙️ GESTION DES PRODUITS"))

        val ajouter = bouton("➕ AJOUTER UN PRODUIT")
        ajouter.setOnClickListener {
            ajouterProduit()
        }
        layout.addView(ajouter)

        val modifier = bouton("✏️ MODIFIER UN PRODUIT")
        modifier.setOnClickListener {
            Toast.makeText(
                this,
                "Sélectionnez un produit à modifier.",
                Toast.LENGTH_SHORT
            ).show()
        }
        layout.addView(modifier)

        val supprimer = bouton("🗑️ SUPPRIMER UN PRODUIT")
        supprimer.setOnClickListener {

            if (produits.isEmpty()) {
                Toast.makeText(
                    this,
                    "Aucun produit à supprimer.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val choix = produits.toTypedArray()

                AlertDialog.Builder(this)
                    .setTitle("Supprimer un produit")
                    .setItems(choix) { _, position ->

                        produits.removeAt(position)

                        Toast.makeText(
                            this,
                            "Produit supprimé.",
                            Toast.LENGTH_SHORT
                        ).show()

                        afficherGestionProduits()
                    }
                    .show()
            }
        }
        layout.addView(supprimer)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // VENTES
    // =========================

    private fun afficherVentes() {

        val layout = baseLayout()
        layout.addView(titre("💰 VENTES"))

        val chiffre = TextView(this)
        chiffre.text = "Chiffre d'affaires\n\n0 FCFA"
        chiffre.textSize = 25f
        chiffre.gravity = Gravity.CENTER
        chiffre.setPadding(10, 40, 10, 40)

        layout.addView(chiffre)

        val nouvelleVente = bouton("➕ ENREGISTRER UNE VENTE")
        nouvelleVente.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Nouvelle vente")
                .setMessage("Fonction d'enregistrement de vente.")
                .setPositiveButton("OK", null)
                .show()
        }

        layout.addView(nouvelleVente)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // COMMANDES
    // =========================

    private fun afficherCommandes() {

        val layout = baseLayout()
        layout.addView(titre("📋 COMMANDES"))

        if (commandes.isEmpty()) {

            val tv = TextView(this)
            tv.text = "Aucune commande pour le moment."
            tv.textSize = 18f
            tv.gravity = Gravity.CENTER
            tv.setPadding(10, 30, 10, 30)

            layout.addView(tv)

        } else {

            for (commande in commandes) {

                val tv = TextView(this)
                tv.text = "📋 $commande"
                tv.textSize = 18f
                tv.setPadding(10, 15, 10, 15)

                layout.addView(tv)
            }
        }

        val ajouter = bouton("➕ NOUVELLE COMMANDE")

        ajouter.setOnClickListener {

            val input = EditText(this)
            input.hint = "Nom du client / produit"

            AlertDialog.Builder(this)
                .setTitle("Nouvelle commande")
                .setView(input)
                .setPositiveButton("ENREGISTRER") { _, _ ->

                    if (input.text.toString().isNotEmpty()) {

                        commandes.add(input.text.toString())

                        Toast.makeText(
                            this,
                            "Commande enregistrée.",
                            Toast.LENGTH_SHORT
                        ).show()

                        afficherCommandes()
                    }
                }
                .setNegativeButton("ANNULER", null)
                .show()
        }

        layout.addView(ajouter)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // STOCK
    // =========================

    private fun afficherStock() {

        val layout = baseLayout()
        layout.addView(titre("📦 GESTION DU STOCK"))

        if (produits.isEmpty()) {

            val tv = TextView(this)
            tv.text = "Aucun produit dans le stock."
            tv.textSize = 18f
            tv.gravity = Gravity.CENTER
            tv.setPadding(10, 30, 10, 30)

            layout.addView(tv)

        } else {

            for (produit in produits) {

                val tv = TextView(this)
                tv.text = "📦 $produit"
                tv.textSize = 17f
                tv.setPadding(10, 15, 10, 15)

                layout.addView(tv)
            }
        }

        val entrée = bouton("➕ ENTRÉE DE STOCK")
        entrée.setOnClickListener {

            Toast.makeText(
                this,
                "Entrée de stock sélectionnée.",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(entrée)

        val sortie = bouton("➖ SORTIE DE STOCK")
        sortie.setOnClickListener {

            Toast.makeText(
                this,
                "Sortie de stock sélectionnée.",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(sortie)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // CLIENTS
    // =========================

    private fun afficherClients() {

        val layout = baseLayout()
        layout.addView(titre("👥 CLIENTS"))

        if (clients.isEmpty()) {

            val tv = TextView(this)
            tv.text = "Aucun client enregistré."
            tv.textSize = 18f
            tv.gravity = Gravity.CENTER
            tv.setPadding(10, 30, 10, 30)

            layout.addView(tv)

        } else {

            for (client in clients) {

                val tv = TextView(this)
                tv.text = "👤 $client"
                tv.textSize = 18f
                tv.setPadding(10, 15, 10, 15)

                layout.addView(tv)
            }
        }

        val ajouter = bouton("➕ AJOUTER UN CLIENT")

        ajouter.setOnClickListener {

            val input = EditText(this)
            input.hint = "Nom du client"

            AlertDialog.Builder(this)
                .setTitle("Ajouter un client")
                .setView(input)
                .setPositiveButton("ENREGISTRER") { _, _ ->

                    if (input.text.toString().isNotEmpty()) {

                        clients.add(input.text.toString())

                        Toast.makeText(
                            this,
                            "Client ajouté.",
                            Toast.LENGTH_SHORT
                        ).show()

                        afficherClients()
                    }
                }
                .setNegativeButton("ANNULER", null)
                .show()
        }

        layout.addView(ajouter)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // PAIEMENTS
    // =========================

    private fun afficherPaiements() {

        val layout = baseLayout()
        layout.addView(titre("💳 MOYENS DE PAIEMENT"))

        val moyens = arrayOf(
            "💵 Espèces",
            "📱 Orange Money",
            "📱 MTN Mobile Money",
            "📱 Moov Money",
            "📱 Wave",
            "💳 Carte bancaire",
            "🌐 Paiement en ligne"
        )

        for (moyen in moyens) {

            val b = bouton(moyen)

            b.setOnClickListener {

                Toast.makeText(
                    this,
                    "$moyen sélectionné",
                    Toast.LENGTH_SHORT
                ).show()
            }

            layout.addView(b)
        }

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // LIVRAISON
    // =========================

    private fun afficherLivraison() {

        val layout = baseLayout()
        layout.addView(titre("🚚 LIVRAISON"))

        val nouvelle = bouton("➕ NOUVELLE LIVRAISON")

        nouvelle.setOnClickListener {

            val adresse = EditText(this)
            adresse.hint = "Adresse de livraison"

            AlertDialog.Builder(this)
                .setTitle("Nouvelle livraison")
                .setView(adresse)
                .setPositiveButton("ENREGISTRER") { _, _ ->

                    Toast.makeText(
                        this,
                        "Livraison enregistrée.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("ANNULER", null)
                .show()
        }

        layout.addView(nouvelle)

        val suivie = bouton("📍 SUIVRE UNE LIVRAISON")

        suivie.setOnClickListener {

            Toast.makeText(
                this,
                "Suivi de livraison.",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(suivie)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // COMMISSIONS
    // =========================

    private fun afficherCommissions() {

        val layout = baseLayout()
        layout.addView(titre("💰 COMMISSIONS"))

        val montant = TextView(this)
        montant.text =
            "COMMISSION DISPONIBLE\n\n0 FCFA"
        montant.textSize = 25f
        montant.gravity = Gravity.CENTER
        montant.setPadding(10, 40, 10, 40)

        layout.addView(montant)

        val historique = bouton("📊 HISTORIQUE DES COMMISSIONS")

        historique.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Historique")
                .setMessage("Aucune commission enregistrée.")
                .setPositiveButton("OK", null)
                .show()
        }

        layout.addView(historique)

        val retrait = bouton("💵 DEMANDER UN RETRAIT")

        retrait.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Demande de retrait")
                .setMessage(
                    "Votre demande de retrait sera traitée par l'administration."
                )
                .setPositiveButton("CONFIRMER", null)
                .setNegativeButton("ANNULER", null)
                .show()
        }

        layout.addView(retrait)

        retour(layout)
        setContentView(layout)
    }

    // =========================
    // AIDE
    // =========================

    private fun afficherAide() {

        val layout = baseLayout()
        layout.addView(titre("🆘 AIDE / ASSISTANCE"))

        val texte = TextView(this)

        texte.text =
            """
            Bienvenue dans Vie Espoir Marketing.
            
            Besoin d'aide ?
            
            • Problème avec votre abonnement
            • Problème avec un produit
            • Problème avec une commande
            • Problème de paiement
            • Problème de livraison
            
            Contactez l'administration pour obtenir de l'assistance.
            """.trimIndent()

        texte.textSize = 18f
        texte.setPadding(15, 20, 15, 30)

        layout.addView(texte)

        val contact = bouton("📞 CONTACTER L'ADMINISTRATION")

        contact.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Assistance")
                .setMessage(
                    "Service d'assistance Vie Espoir Marketing"
                )
                .setPositiveButton("OK", null)
                .show()
        }

        layout.addView(contact)

        retour(layout)
        setContentView(layout)
    }
}