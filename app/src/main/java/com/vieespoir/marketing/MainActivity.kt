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

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<Produit>()
    private val commandes = ArrayList<String>()

    private lateinit var contenu: LinearLayout

    data class Produit(
        var nom: String,
        var prix: Int,
        var stock: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Produits de démonstration
        produits.add(Produit("Super7", 20000, 20))
        produits.add(Produit("M7", 15000, 15))
        produits.add(Produit("Women 7", 15000, 10))
        produits.add(Produit("Timoc", 10000, 12))
        produits.add(Produit("Café Royal", 5000, 25))

        afficherAccueil()
    }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil() {

        val principal = LinearLayout(this)
        principal.orientation = LinearLayout.VERTICAL
        principal.setBackgroundColor(Color.WHITE)

        // En-tête
        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.gravity = Gravity.CENTER
        header.setPadding(20, 25, 20, 20)
        header.setBackgroundColor(Color.WHITE)

        val logo = TextView(this)
        logo.text = "🦁"
        logo.textSize = 55f
        logo.gravity = Gravity.CENTER

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 25f
        titre.setTextColor(Color.rgb(0, 110, 75))
        titre.gravity = Gravity.CENTER
        titre.setPadding(0, 5, 0, 5)

        val sousTitre = TextView(this)
        sousTitre.text = "Votre plateforme de vente"
        sousTitre.textSize = 17f
        sousTitre.setTextColor(Color.DKGRAY)
        sousTitre.gravity = Gravity.CENTER

        header.addView(logo)
        header.addView(titre)
        header.addView(sousTitre)

        principal.addView(header)

        // Zone centrale
        val scroll = ScrollView(this)

        contenu = LinearLayout(this)
        contenu.orientation = LinearLayout.VERTICAL
        contenu.setPadding(18, 10, 18, 20)

        scroll.addView(contenu)
        principal.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // Navigation du bas
        val navigation = LinearLayout(this)
        navigation.orientation = LinearLayout.HORIZONTAL
        navigation.gravity = Gravity.CENTER
        navigation.setPadding(5, 8, 5, 8)
        navigation.setBackgroundColor(Color.rgb(245, 245, 245))

        val accueil = boutonBas("🏠\nAccueil")
        val produitsBtn = boutonBas("🛍️\nProduits")
        val panierBtn = boutonBas("🛒\nPanier")
        val compte = boutonBas("👤\nCompte")

        accueil.setOnClickListener {
            afficherAccueil()
        }

        produitsBtn.setOnClickListener {
            afficherProduits()
        }

        panierBtn.setOnClickListener {
            afficherPanier()
        }

        compte.setOnClickListener {
            afficherCompte()
        }

        navigation.addView(accueil, poidsNavigation())
        navigation.addView(produitsBtn, poidsNavigation())
        navigation.addView(panierBtn, poidsNavigation())
        navigation.addView(compte, poidsNavigation())

        principal.addView(navigation)

        setContentView(principal)

        afficherMenuAccueil()
    }

    // =========================================================
    // MENU ACCUEIL
    // =========================================================

    private fun afficherMenuAccueil() {

        contenu.removeAllViews()

        val recherche = EditText(this)
        recherche.hint = "🔎 Rechercher un produit..."
        recherche.setPadding(20, 15, 20, 15)
        contenu.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )

        ajouterEspace(12)

        ajouterTitre("Bienvenue sur Vie Espoir Marketing")

        ajouterCarte(
            "🛍️",
            "PRODUITS",
            "Découvrez tous les produits disponibles"
        ) {
            afficherProduits()
        }

        ajouterCarte(
            "🏪",
            "ESPACE VENDEUR",
            "Gérer vos produits et vos ventes"
        ) {
            afficherVendeur()
        }

        ajouterCarte(
            "📦",
            "COMMANDES",
            "Consulter et suivre les commandes"
        ) {
            afficherCommandes()
        }

        ajouterCarte(
            "📊",
            "GESTION DU STOCK",
            "Voir et gérer votre stock"
        ) {
            afficherStock()
        }

        ajouterCarte(
            "👥",
            "CLIENTS",
            "Gérer vos clients"
        ) {
            afficherClients()
        }

        ajouterCarte(
            "💳",
            "PAIEMENTS",
            "Gérer les paiements"
        ) {
            afficherPaiements()
        }

        ajouterCarte(
            "🚚",
            "LIVRAISON",
            "Suivre les livraisons"
        ) {
            afficherLivraison()
        }

        ajouterCarte(
            "💰",
            "COMMISSIONS",
            "Consulter vos commissions"
        ) {
            afficherCommissions()
        }

        ajouterCarte(
            "👑",
            "ADMINISTRATION",
            "Administration de la plateforme"
        ) {
            afficherAdministration()
        }
    }

    // =========================================================
    // PRODUITS
    // =========================================================

    private fun afficherProduits() {

        afficherPage("🛍️ Produits")

        val recherche = EditText(this)
        recherche.hint = "Rechercher..."
        contenu.addView(recherche)

        ajouterEspace(10)

        val ajouter = boutonPrincipal("➕ Ajouter un produit")

        ajouter.setOnClickListener {
            ajouterProduit()
        }

        contenu.addView(ajouter)

        ajouterEspace(10)

        for (produit in produits) {

            val carte = LinearLayout(this)
            carte.orientation = LinearLayout.VERTICAL
            carte.setPadding(20, 15, 20, 15)
            carte.setBackgroundColor(Color.rgb(245, 245, 245))

            val nom = TextView(this)
            nom.text = "📦 ${produit.nom}"
            nom.textSize = 20f
            nom.setTextColor(Color.rgb(0, 100, 70))

            val prix = TextView(this)
            prix.text = "Prix : ${produit.prix} FCFA"
            prix.textSize = 17f

            val stock = TextView(this)
            stock.text = "Stock : ${produit.stock}"
            stock.textSize = 16f

            val acheter = boutonPrincipal("🛒 Ajouter au panier")

            acheter.setOnClickListener {
                if (produit.stock > 0) {
                    panier.add(produit)
                    produit.stock--
                    Toast.makeText(
                        this,
                        "${produit.nom} ajouté au panier",
                        Toast.LENGTH_SHORT
                    ).show()
                    afficherProduits()
                } else {
                    Toast.makeText(
                        this,
                        "Produit en rupture de stock",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            carte.addView(nom)
            carte.addView(prix)
            carte.addView(stock)
            carte.addView(acheter)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 0, 0, 15)

            contenu.addView(carte, params)
        }
    }

    // =========================================================
    // AJOUTER PRODUIT
    // =========================================================

    private fun ajouterProduit() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 10)

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix en FCFA"
        prix.inputType = 2

        val stock = EditText(this)
        stock.hint = "Quantité en stock"
        stock.inputType = 2

        layout.addView(nom)
        layout.addView(prix)
        layout.addView(stock)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un produit")
            .setView(layout)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Ajouter") { _, _ ->

                val nomProduit = nom.text.toString()
                val prixProduit = prix.text.toString().toIntOrNull() ?: 0
                val stockProduit = stock.text.toString().toIntOrNull() ?: 0

                if (nomProduit.isNotEmpty() && prixProduit > 0) {

                    produits.add(
                        Produit(
                            nomProduit,
                            prixProduit,
                            stockProduit
                        )
                    )

                    Toast.makeText(
                        this,
                        "Produit ajouté avec succès",
                        Toast.LENGTH_SHORT
                    ).show()

                    afficherProduits()

                } else {

                    Toast.makeText(
                        this,
                        "Veuillez remplir correctement les informations",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // =========================================================
    // PANIER
    // =========================================================

    private fun afficherPanier() {

        afficherPage("🛒 Mon panier")

        if (panier.isEmpty()) {

            val vide = TextView(this)
            vide.text = "Votre panier est vide."
            vide.textSize = 20f
            vide.gravity = Gravity.CENTER
            vide.setPadding(10, 50, 10, 50)

            contenu.addView(vide)

            return
        }

        var total = 0

        for (produit in panier) {

            val ligne = TextView(this)

            ligne.text =
                "📦 ${produit.nom}     ${produit.prix} FCFA"

            ligne.textSize = 18f
            ligne.setPadding(15, 20, 15, 20)

            contenu.addView(ligne)

            total += produit.prix
        }

        ajouterEspace(10)

        val totalText = TextView(this)

        totalText.text =
            "TOTAL : $total FCFA"

        totalText.textSize = 22f
        totalText.setTextColor(Color.rgb(0, 110, 75))
        totalText.gravity = Gravity.CENTER

        contenu.addView(totalText)

        ajouterEspace(15)

        val commander = boutonPrincipal("✅ PASSER LA COMMANDE")

        commander.setOnClickListener {
            passerCommande(total)
        }

        contenu.addView(commander)

        val vider = boutonSecondaire("🗑️ Vider le panier")

        vider.setOnClickListener {
            panier.clear()
            afficherPanier()
        }

        contenu.addView(vider)
    }

    // =========================================================
    // COMMANDE
    // =========================================================

    private fun passerCommande(total: Int) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 10)

        val nom = EditText(this)
        nom.hint = "Nom du client"

        val telephone = EditText(this)
        telephone.hint = "Téléphone"
        telephone.inputType = 2

        val adresse = EditText(this)
        adresse.hint = "Adresse de livraison"

        val paiement = Spinner(this)

        val moyens = arrayOf(
            "Espèces à la livraison",
            "Mobile Money",
            "Paiement en ligne"
        )

        paiement.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                moyens
            )

        layout.addView(nom)
        layout.addView(telephone)
        layout.addView(adresse)
        layout.addView(paiement)

        AlertDialog.Builder(this)
            .setTitle("Confirmer la commande")
            .setMessage("Total : $total FCFA")
            .setView(layout)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Commander") { _, _ ->

                commandes.add(
                    "Commande #${commandes.size + 1} - $total FCFA"
                )

                panier.clear()

                Toast.makeText(
                    this,
                    "Commande enregistrée avec succès",
                    Toast.LENGTH_LONG
                ).show()

                afficherCommandes()
            }
            .show()
    }

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun afficherCommandes() {

        afficherPage("📦 Mes commandes")

        if (commandes.isEmpty()) {

            val texte = TextView(this)
            texte.text = "Aucune commande pour le moment."
            texte.textSize = 19f
            texte.gravity = Gravity.CENTER

            contenu.addView(texte)

            return
        }

        for (commande in commandes) {

            ajouterCarte(
                "📦",
                commande,
                "Statut : En préparation"
            ) {
                Toast.makeText(
                    this,
                    "Commande sélectionnée",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // =========================================================
    // VENDEUR
    // =========================================================

    private fun afficherVendeur() {

        afficherPage("🏪 Espace vendeur")

        ajouterCarte(
            "💳",
            "ABONNEMENT VENDEUR",
            "Choisir un pack et activer votre boutique"
        ) {
            afficherAbonnement()
        }

        ajouterCarte(
            "➕",
            "MES PRODUITS",
            "Ajouter et gérer vos produits"
        ) {
            afficherProduits()
        }

        ajouterCarte(
            "📦",
            "MES COMMANDES",
            "Voir les commandes reçues"
        ) {
            afficherCommandes()
        }

        ajouterCarte(
            "📊",
            "MON CHIFFRE D'AFFAIRES",
            "Voir les statistiques de vente"
        ) {
            afficherStatistiques()
        }

        ajouterCarte(
            "💰",
            "MES COMMISSIONS",
            "Consulter les commissions"
        ) {
            afficherCommissions()
        }
    }

    // =========================================================
    // ABONNEMENT
    // =========================================================

    private fun afficherAbonnement() {

        afficherPage("💳 Abonnement vendeur")

        ajouterCarte(
            "🥉",
            "PACK STARTER",
            "Accès vendeur de base"
        ) {
            confirmerAbonnement("PACK STARTER")
        }

        ajouterCarte(
            "🥈",
            "PACK BUSINESS",
            "Plus de produits et plus de fonctionnalités"
        ) {
            confirmerAbonnement("PACK BUSINESS")
        }

        ajouterCarte(
            "🥇",
            "PACK PREMIUM",
            "Accès complet aux fonctionnalités"
        ) {
            confirmerAbonnement("PACK PREMIUM")
        }
    }

    private fun confirmerAbonnement(pack: String) {

        AlertDialog.Builder(this)
            .setTitle(pack)
            .setMessage(
                "Voulez-vous souscrire à $pack ?"
            )
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Continuer") { _, _ ->

                Toast.makeText(
                    this,
                    "Redirection vers le paiement",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    // =========================================================
    // STOCK
    // =========================================================

    private fun afficherStock() {

        afficherPage("📊 Gestion du stock")

        var total = 0

        for (produit in produits) {

            total += produit.stock

            ajouterCarte(
                "📦",
                produit.nom,
                "Stock disponible : ${produit.stock}"
            ) {

                AlertDialog.Builder(this)
                    .setTitle(produit.nom)
                    .setMessage(
                        "Stock actuel : ${produit.stock}"
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        ajouterEspace(10)

        val totalStock = TextView(this)

        totalStock.text =
            "TOTAL DES ARTICLES EN STOCK : $total"

        totalStock.textSize = 19f
        totalStock.gravity = Gravity.CENTER

        contenu.addView(totalStock)
    }

    // =========================================================
    // CLIENTS
    // =========================================================

    private fun afficherClients() {

        afficherPage("👥 Clients")

        ajouterCarte(
            "👤",
            "NOUVEAU CLIENT",
            "Ajouter un client"
        ) {
            ajouterClient()
        }

        ajouterCarte(
            "📋",
            "LISTE DES CLIENTS",
            "Voir vos clients"
        ) {
            Toast.makeText(
                this,
                "La liste des clients sera disponible ici",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun ajouterClient() {

        val nom = EditText(this)
        nom.hint = "Nom du client"

        AlertDialog.Builder(this)
            .setTitle("Ajouter un client")
            .setView(nom)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Enregistrer") { _, _ ->

                Toast.makeText(
                    this,
                    "Client enregistré",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    // =========================================================
    // PAIEMENTS
    // =========================================================

    private fun afficherPaiements() {

        afficherPage("💳 Paiements")

        ajouterCarte(
            "📱",
            "MOBILE MONEY",
            "Gérer les paiements Mobile Money"
        ) {
            Toast.makeText(
                this,
                "Module Mobile Money",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "💵",
            "ESPÈCES",
            "Paiement à la livraison"
        ) {
            Toast.makeText(
                this,
                "Paiement en espèces",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "💳",
            "PAIEMENT EN LIGNE",
            "Paiement électronique"
        ) {
            Toast.makeText(
                this,
                "Module paiement en ligne",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // LIVRAISON
    // =========================================================

    private fun afficherLivraison() {

        afficherPage("🚚 Livraison")

        ajouterCarte(
            "📦",
            "EN PRÉPARATION",
            "Commandes en préparation"
        ) {
            Toast.makeText(
                this,
                "Commandes en préparation",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "🚚",
            "EN LIVRAISON",
            "Commandes actuellement livrées"
        ) {
            Toast.makeText(
                this,
                "Commandes en livraison",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "✅",
            "LIVRÉES",
            "Commandes terminées"
        ) {
            Toast.makeText(
                this,
                "Commandes livrées",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // COMMISSIONS
    // =========================================================

    private fun afficherCommissions() {

        afficherPage("💰 Commissions")

        ajouterCarte(
            "💵",
            "COMMISSIONS GAGNÉES",
            "0 FCFA"
        ) {
            Toast.makeText(
                this,
                "Aucune commission enregistrée",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "📈",
            "VENTES",
            "Voir les ventes générant des commissions"
        ) {
            afficherStatistiques()
        }
    }

    // =========================================================
    // STATISTIQUES
    // =========================================================

    private fun afficherStatistiques() {

        afficherPage("📊 Tableau de bord")

        ajouterCarte(
            "💰",
            "CHIFFRE D'AFFAIRES",
            "0 FCFA"
        ) {}

        ajouterCarte(
            "🛒",
            "COMMANDES",
            "${commandes.size} commande(s)"
        ) {}

        ajouterCarte(
            "📦",
            "PRODUITS",
            "${produits.size} produit(s)"
        ) {}

        ajouterCarte(
            "👥",
            "CLIENTS",
            "0 client"
        ) {}

        ajouterCarte(
            "💵",
            "COMMISSIONS",
            "0 FCFA"
        ) {}
    }

    // =========================================================
    // ADMINISTRATION
    // =========================================================

    private fun afficherAdministration() {

        afficherPage("👑 Administration")

        ajouterCarte(
            "👥",
            "VENDEURS",
            "Gérer les vendeurs"
        ) {
            Toast.makeText(
                this,
                "Gestion des vendeurs",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "🛍️",
            "PRODUITS",
            "Gérer tous les produits"
        ) {
            afficherProduits()
        }

        ajouterCarte(
            "📦",
            "COMMANDES",
            "Gérer toutes les commandes"
        ) {
            afficherCommandes()
        }

        ajouterCarte(
            "💳",
            "ABONNEMENTS",
            "Gérer les abonnements vendeurs"
        ) {
            afficherAbonnement()
        }

        ajouterCarte(
            "📊",
            "STATISTIQUES",
            "Statistiques générales"
        ) {
            afficherStatistiques()
        }
    }

    // =========================================================
    // COMPTE
    // =========================================================

    private fun afficherCompte() {

        afficherPage("👤 Mon compte")

        ajouterCarte(
            "👤",
            "MON PROFIL",
            "Yao Guillaume"
        ) {
            modifierProfil()
        }

        ajouterCarte(
            "🏪",
            "MON ESPACE VENDEUR",
            "Gérer ma boutique"
        ) {
            afficherVendeur()
        }

        ajouterCarte(
            "🔔",
            "NOTIFICATIONS",
            "Voir mes notifications"
        ) {
            Toast.makeText(
                this,
                "Aucune nouvelle notification",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "⚙️",
            "PARAMÈTRES",
            "Paramètres de l'application"
        ) {
            Toast.makeText(
                this,
                "Paramètres",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun modifierProfil() {

        val nom = EditText(this)
        nom.setText("Yao Guillaume")

        AlertDialog.Builder(this)
            .setTitle("Mon profil")
            .setView(nom)
            .setNegativeButton("Fermer", null)
            .setPositiveButton("Enregistrer") { _, _ ->
                Toast.makeText(
                    this,
                    "Profil enregistré",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    // =========================================================
    // OUTILS D'AFFICHAGE
    // =========================================================

    private fun afficherPage(titrePage: String) {

        contenu.removeAllViews()

        val retour = boutonSecondaire("← Retour à l'accueil")

        retour.setOnClickListener {
            afficherAccueil()
        }

        contenu.addView(retour)

        ajouterEspace(10)

        ajouterTitre(titrePage)
    }

    private fun ajouterTitre(texte: String) {

        val titre = TextView(this)

        titre.text = texte
        titre.textSize = 23f
        titre.setTextColor(Color.rgb(0, 110, 75))
        titre.setPadding(5, 15, 5, 20)

        contenu.addView(titre)
    }

    private fun ajouterCarte(
        icone: String,
        titre: String,
        description: String,
        action: () -> Unit
    ) {

        val carte = LinearLayout(this)

        carte.orientation = LinearLayout.VERTICAL
        carte.setPadding(20, 18, 20, 18)
        carte.setBackgroundColor(Color.rgb(242, 244, 244))

        val titreView = TextView(this)

        titreView.text = "$icone  $titre"
        titreView.textSize = 19f
        titreView.setTextColor(Color.rgb(0, 100, 70))

        val descriptionView = TextView(this)

        descriptionView.text = description
        descriptionView.textSize = 15f
        descriptionView.setTextColor(Color.DKGRAY)
        descriptionView.setPadding(0, 8, 0, 5)

        carte.addView(titreView)
        carte.addView(descriptionView)

        carte.setOnClickListener {
            action()
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 0, 0, 12)

        contenu.addView(carte, params)
    }

    private fun boutonPrincipal(texte: String): Button {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 16f
        bouton.setTextColor(Color.WHITE)
        bouton.setBackgroundColor(Color.rgb(0, 120, 80))
        bouton.gravity = Gravity.CENTER

        bouton.minHeight = 60
        bouton.setPadding(15, 10, 15, 10)

        return bouton
    }

    private fun boutonSecondaire(texte: String): Button {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 16f
        bouton.gravity = Gravity.CENTER

        bouton.minHeight = 55
        bouton.setPadding(15, 10, 15, 10)

        return bouton
    }

    private fun boutonBas(texte: String): Button {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 12f
        bouton.gravity = Gravity.CENTER
        bouton.setPadding(2, 2, 2, 2)

        return bouton
    }

    private fun poidsNavigation(): LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )
    }

    private fun ajouterEspace(taille: Int) {

        val espace = Space(this)

        contenu.addView(
            espace,
            LinearLayout.LayoutParams(
                1,
                taille
            )
        )
    }
}