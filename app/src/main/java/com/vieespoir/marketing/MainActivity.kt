package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {

    data class Produit(
        var nom: String,
        var prix: Int,
        var stock: Int
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<Produit>()
    private val commandes = ArrayList<String>()
    private val clients = ArrayList<String>()

    private lateinit var contenu: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerProduits()
        afficherAccueil()
    }

    // =========================================================
    // PRODUITS DE DÉPART
    // =========================================================

    private fun chargerProduits() {
        if (produits.isNotEmpty()) return

        produits.add(Produit("Super7", 20000, 20))
        produits.add(Produit("M7", 15000, 15))
        produits.add(Produit("Women 7", 15000, 10))
        produits.add(Produit("Timoc", 10000, 12))
        produits.add(Produit("Café Royal", 5000, 25))
    }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil() {

        val principal = LinearLayout(this)
        principal.orientation = LinearLayout.VERTICAL
        principal.setBackgroundColor(Color.WHITE)

        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.gravity = Gravity.CENTER
        header.setPadding(15, 15, 15, 10)

        val logo = TextView(this)
        logo.text = "🦁"
        logo.textSize = 50f
        logo.gravity = Gravity.CENTER

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 24f
        titre.setTextColor(Color.rgb(0, 110, 75))
        titre.gravity = Gravity.CENTER

        val sousTitre = TextView(this)
        sousTitre.text = "Votre plateforme de vente"
        sousTitre.textSize = 16f
        sousTitre.setTextColor(Color.DKGRAY)
        sousTitre.gravity = Gravity.CENTER

        header.addView(logo)
        header.addView(titre)
        header.addView(sousTitre)

        principal.addView(header)

        val scroll = ScrollView(this)

        contenu = LinearLayout(this)
        contenu.orientation = LinearLayout.VERTICAL
        contenu.setPadding(16, 8, 16, 16)

        scroll.addView(contenu)

        principal.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val navigation = LinearLayout(this)
        navigation.orientation = LinearLayout.HORIZONTAL
        navigation.setBackgroundColor(Color.rgb(245, 245, 245))
        navigation.setPadding(3, 3, 3, 3)

        val accueil = boutonBas("🏠\nAccueil")
        val produitsBtn = boutonBas("🛍️\nProduits")
        val panierBtn = boutonBas("🛒\nPanier")
        val compteBtn = boutonBas("👤\nCompte")

        accueil.setOnClickListener {
            afficherAccueil()
        }

        produitsBtn.setOnClickListener {
            afficherProduits()
        }

        panierBtn.setOnClickListener {
            afficherPanier()
        }

        compteBtn.setOnClickListener {
            afficherCompte()
        }

        navigation.addView(accueil, poidsNavigation())
        navigation.addView(produitsBtn, poidsNavigation())
        navigation.addView(panierBtn, poidsNavigation())
        navigation.addView(compteBtn, poidsNavigation())

        principal.addView(navigation)

        setContentView(principal)

        afficherMenuAccueil()
    }

    private fun afficherMenuAccueil() {

        contenu.removeAllViews()

        val recherche = EditText(this)
        recherche.hint = "🔎 Rechercher un produit..."
        recherche.textSize = 16f
        recherche.setSingleLine(true)
        recherche.inputType = InputType.TYPE_CLASS_TEXT
        recherche.setPadding(18, 10, 18, 10)

        contenu.addView(recherche)

        ajouterEspace(10)

        val rechercher = boutonPrincipal("🔎 Rechercher")

        rechercher.setOnClickListener {
            afficherProduits(recherche.text.toString().trim())
        }

        contenu.addView(rechercher)

        ajouterEspace(15)

        ajouterTitre("Bienvenue sur Vie Espoir Marketing")

        ajouterCarte(
            "🛍️",
            "PRODUITS",
            "Découvrir les produits disponibles"
        ) {
            afficherProduits()
        }

        ajouterCarte(
            "🏪",
            "ESPACE VENDEUR",
            "Gérer votre boutique"
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
            "Voir et modifier le stock"
        ) {
            afficherStock()
        }

        ajouterCarte(
            "👥",
            "CLIENTS",
            "Gérer les clients"
        ) {
            afficherClients()
        }

        ajouterCarte(
            "💳",
            "PAIEMENTS",
            "Moyens de paiement"
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
            "Consulter les commissions"
        ) {
            afficherCommissions()
        }

        ajouterCarte(
            "👑",
            "ADMINISTRATION",
            "Gestion de la plateforme"
        ) {
            afficherAdministration()
        }
    }

    // =========================================================
    // PRODUITS
    // =========================================================

    private fun afficherProduits(recherche: String = "") {

        afficherPage("🛍️ Produits")

        val ajouter = boutonPrincipal("➕ Ajouter un produit")

        ajouter.setOnClickListener {
            ajouterProduit()
        }

        contenu.addView(ajouter)

        ajouterEspace(10)

        val terme = recherche.lowercase(Locale.getDefault())

        var trouve = false

        for (produit in produits) {

            if (
                terme.isNotEmpty() &&
                !produit.nom.lowercase(Locale.getDefault())
                    .contains(terme)
            ) {
                continue
            }

            trouve = true

            val carte = LinearLayout(this)
            carte.orientation = LinearLayout.VERTICAL
            carte.setPadding(18, 15, 18, 15)
            carte.setBackgroundColor(Color.rgb(242, 244, 244))

            val nom = TextView(this)
            nom.text = "📦 ${produit.nom}"
            nom.textSize = 20f
            nom.setTextColor(Color.rgb(0, 105, 75))

            val prix = TextView(this)
            prix.text = "Prix : ${produit.prix} FCFA"
            prix.textSize = 17f

            val stock = TextView(this)
            stock.text = "Stock : ${produit.stock}"
            stock.textSize = 16f

            val actions = LinearLayout(this)
            actions.orientation = LinearLayout.HORIZONTAL

            val panierBtn = boutonPrincipal("🛒 Ajouter")

            panierBtn.setOnClickListener {
                ajouterAuPanier(produit)
            }

            val modifier = boutonSecondaire("✏️ Modifier")

            modifier.setOnClickListener {
                modifierProduit(produit)
            }

            actions.addView(
                panierBtn,
                LinearLayout.LayoutParams(
                    0,
                    60,
                    1f
                )
            )

            actions.addView(
                modifier,
                LinearLayout.LayoutParams(
                    0,
                    60,
                    1f
                )
            )

            carte.addView(nom)
            carte.addView(prix)
            carte.addView(stock)
            carte.addView(actions)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 0, 0, 12)

            contenu.addView(carte, params)
        }

        if (!trouve) {

            val vide = TextView(this)
            vide.text = "Aucun produit trouvé."
            vide.textSize = 18f
            vide.gravity = Gravity.CENTER
            vide.setPadding(10, 40, 10, 40)

            contenu.addView(vide)
        }
    }

    private fun ajouterProduit() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 5, 25, 5)

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix en FCFA"
        prix.inputType = InputType.TYPE_CLASS_NUMBER

        val stock = EditText(this)
        stock.hint = "Quantité en stock"
        stock.inputType = InputType.TYPE_CLASS_NUMBER

        layout.addView(nom)
        layout.addView(prix)
        layout.addView(stock)

        AlertDialog.Builder(this)
            .setTitle("➕ Ajouter un produit")
            .setView(layout)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Ajouter") { _, _ ->

                val nomProduit = nom.text.toString().trim()
                val prixProduit =
                    prix.text.toString().toIntOrNull()
                val stockProduit =
                    stock.text.toString().toIntOrNull()

                if (
                    nomProduit.isEmpty() ||
                    prixProduit == null ||
                    prixProduit <= 0 ||
                    stockProduit == null ||
                    stockProduit < 0
                ) {

                    Toast.makeText(
                        this,
                        "Informations invalides",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

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
            }
            .show()
    }

    private fun modifierProduit(produit: Produit) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 5, 25, 5)

        val nom = EditText(this)
        nom.setText(produit.nom)

        val prix = EditText(this)
        prix.setText(produit.prix.toString())
        prix.inputType = InputType.TYPE_CLASS_NUMBER

        val stock = EditText(this)
        stock.setText(produit.stock.toString())
        stock.inputType = InputType.TYPE_CLASS_NUMBER

        layout.addView(nom)
        layout.addView(prix)
        layout.addView(stock)

        AlertDialog.Builder(this)
            .setTitle("✏️ Modifier le produit")
            .setView(layout)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Enregistrer") { _, _ ->

                val nouveauNom =
                    nom.text.toString().trim()

                val nouveauPrix =
                    prix.text.toString().toIntOrNull()

                val nouveauStock =
                    stock.text.toString().toIntOrNull()

                if (
                    nouveauNom.isEmpty() ||
                    nouveauPrix == null ||
                    nouveauPrix <= 0 ||
                    nouveauStock == null ||
                    nouveauStock < 0
                ) {

                    Toast.makeText(
                        this,
                        "Informations invalides",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                produit.nom = nouveauNom
                produit.prix = nouveauPrix
                produit.stock = nouveauStock

                Toast.makeText(
                    this,
                    "Produit modifié",
                    Toast.LENGTH_SHORT
                ).show()

                afficherProduits()
            }
            .show()
    }

    // =========================================================
    // PANIER
    // =========================================================

    private fun ajouterAuPanier(produit: Produit) {

        if (produit.stock <= 0) {

            Toast.makeText(
                this,
                "Produit en rupture de stock",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        panier.add(produit)
        produit.stock--

        Toast.makeText(
            this,
            "${produit.nom} ajouté au panier",
            Toast.LENGTH_SHORT
        ).show()
    }

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

        panier.forEachIndexed { index, produit ->

            val ligne = LinearLayout(this)
            ligne.orientation = LinearLayout.HORIZONTAL
            ligne.gravity = Gravity.CENTER_VERTICAL

            val texte = TextView(this)
            texte.text =
                "${produit.nom}\n${produit.prix} FCFA"

            texte.textSize = 17f

            val supprimer =
                boutonSecondaire("🗑️")

            supprimer.setOnClickListener {

                panier.removeAt(index)
                produit.stock++

                afficherPanier()
            }

            ligne.addView(
                texte,
                LinearLayout.LayoutParams(
                    0,
                    80,
                    1f
                )
            )

            ligne.addView(supprimer)

            contenu.addView(ligne)

            total += produit.prix
        }

        ajouterEspace(10)

        val totalText = TextView(this)
        totalText.text = "TOTAL : $total FCFA"
        totalText.textSize = 23f
        totalText.setTextColor(
            Color.rgb(0, 110, 75)
        )
        totalText.gravity = Gravity.CENTER

        contenu.addView(totalText)

        ajouterEspace(15)

        val commander =
            boutonPrincipal("✅ PASSER LA COMMANDE")

        commander.setOnClickListener {
            passerCommande(total)
        }

        contenu.addView(commander)

        val vider =
            boutonSecondaire("🗑️ Vider le panier")

        vider.setOnClickListener {

            panier.forEach {
                it.stock++
            }

            panier.clear()

            afficherPanier()
        }

        contenu.addView(vider)
    }

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun passerCommande(total: Int) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 5, 25, 5)

        val nom = EditText(this)
        nom.hint = "Nom du client"

        val telephone = EditText(this)
        telephone.hint = "Numéro de téléphone"
        telephone.inputType =
            InputType.TYPE_CLASS_PHONE
        telephone.setSingleLine(true)

        val adresse = EditText(this)
        adresse.hint = "Adresse de livraison"

        val paiement = Spinner(this)

        val moyens = arrayOf(
            "Espèces à la livraison",
            "Orange Money",
            "MTN Money",
            "Moov Money",
            "Wave"
        )

        paiement.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            moyens
        )

        val montant = TextView(this)
        montant.text =
            "Montant à payer : $total FCFA"
        montant.textSize = 20f
        montant.setTextColor(
            Color.rgb(0, 110, 75)
        )

        layout.addView(nom)
        layout.addView(telephone)
        layout.addView(adresse)
        layout.addView(paiement)
        layout.addView(montant)

        val dialog = AlertDialog.Builder(this)
            .setTitle("💳 Finaliser la commande")
            .setView(layout)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Continuer", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val nomClient =
                    nom.text.toString().trim()

                val numero =
                    telephone.text.toString().trim()

                val adresseClient =
                    adresse.text.toString().trim()

                val moyen =
                    paiement.selectedItem.toString()

                if (nomClient.isEmpty()) {
                    nom.error = "Entrez le nom"
                    return@setOnClickListener
                }

                if (!numeroValide(numero)) {
                    telephone.error =
                        "Numéro invalide"
                    return@setOnClickListener
                }

                if (adresseClient.isEmpty()) {
                    adresse.error =
                        "Entrez l'adresse"
                    return@setOnClickListener
                }

                enregistrerCommande(
                    total,
                    nomClient,
                    numero,
                    adresseClient,
                    moyen
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun numeroValide(numero: String): Boolean {

        val chiffres =
            numero.filter { it.isDigit() }

        return chiffres.length in 8..15
    }

    private fun enregistrerCommande(
        total: Int,
        nom: String,
        telephone: String,
        adresse: String,
        paiement: String
    ) {

        val numeroCommande =
            commandes.size + 1

        commandes.add(
            "Commande #$numeroCommande\n" +
            "Client : $nom\n" +
            "Téléphone : $telephone\n" +
            "Adresse : $adresse\n" +
            "Paiement : $paiement\n" +
            "Montant : $total FCFA\n" +
            "Statut : En préparation"
        )

        if (!clients.contains(telephone)) {
            clients.add(telephone)
        }

        panier.clear()

        Toast.makeText(
            this,
            "Commande enregistrée avec succès",
            Toast.LENGTH_LONG
        ).show()

        afficherCommandes()
    }

    private fun afficherCommandes() {

        afficherPage("📦 Commandes")

        if (commandes.isEmpty()) {

            val texte = TextView(this)
            texte.text =
                "Aucune commande pour le moment."
            texte.textSize = 19f
            texte.gravity = Gravity.CENTER
            texte.setPadding(10, 40, 10, 40)

            contenu.addView(texte)

            return
        }

        commandes.forEach {

            val titreCommande =
                it.substringBefore("\n")

            val details =
                it.substringAfter("\n")

            ajouterCarte(
                "📦",
                titreCommande,
                details
            ) {}
        }
    }

    // =========================================================
    // ESPACE VENDEUR
    // =========================================================

    private fun afficherVendeur() {

        afficherPage("🏪 Espace vendeur")

        ajouterCarte(
            "💳",
            "ABONNEMENT VENDEUR",
            "Choisir un pack"
        ) {
            afficherAbonnement()
        }

        ajouterCarte(
            "➕",
            "MES PRODUITS",
            "Ajouter et gérer les produits"
        ) {
            afficherProduits()
        }

        ajouterCarte(
            "📦",
            "MES COMMANDES",
            "Voir les commandes"
        ) {
            afficherCommandes()
        }

        ajouterCarte(
            "📊",
            "MON TABLEAU DE BORD",
            "Voir mes statistiques"
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
            "Plus de produits"
        ) {
            confirmerAbonnement("PACK BUSINESS")
        }

        ajouterCarte(
            "🥇",
            "PACK PREMIUM",
            "Accès complet"
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
            .setNegativeButton(
                "Annuler",
                null
            )
            .setPositiveButton(
                "Continuer"
            ) { _, _ ->

                Toast.makeText(
                    this,
                    "Abonnement sélectionné : $pack",
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

        produits.forEach { produit ->

            total += produit.stock

            ajouterCarte(
                "📦",
                produit.nom,
                "Stock : ${produit.stock}"
            ) {
                modifierProduit(produit)
            }
        }

        ajouterEspace(10)

        val totalStock = TextView(this)
        totalStock.text =
            "TOTAL ARTICLES EN STOCK : $total"
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
            "${clients.size} client(s)"
        ) {
            afficherListeClients()
        }
    }

    private fun ajouterClient() {

        val layout = LinearLayout(this)
        layout.orientation =
            LinearLayout.VERTICAL
        layout.setPadding(25, 5, 25, 5)

        val nom = EditText(this)
        nom.hint = "Nom du client"

        val telephone = EditText(this)
        telephone.hint = "Téléphone"
        telephone.inputType =
            InputType.TYPE_CLASS_PHONE
        telephone.setSingleLine(true)

        layout.addView(nom)
        layout.addView(telephone)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un client")
            .setView(layout)
            .setNegativeButton(
                "Annuler",
                null
            )
            .setPositiveButton(
                "Enregistrer"
            ) { _, _ ->

                val numero =
                    telephone.text.toString().trim()

                if (!numeroValide(numero)) {

                    Toast.makeText(
                        this,
                        "Numéro invalide",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                clients.add(numero)

                Toast.makeText(
                    this,
                    "Client enregistré",
                    Toast.LENGTH_SHORT
                ).show()

                afficherClients()
            }
            .show()
    }

    private fun afficherListeClients() {

        afficherPage("📋 Liste des clients")

        if (clients.isEmpty()) {

            val texte = TextView(this)
            texte.text =
                "Aucun client enregistré."
            texte.textSize = 18f
            texte.gravity = Gravity.CENTER

            contenu.addView(texte)

            return
        }

        clients.forEachIndexed { index, numero ->

            ajouterCarte(
                "👤",
                "Client ${index + 1}",
                numero
            ) {}
        }
    }

    // =========================================================
    // PAIEMENTS
    // =========================================================

    private fun afficherPaiements() {

        afficherPage("💳 Paiements")

        ajouterCarte(
            "📱",
            "ORANGE MONEY",
            "Paiement Mobile Money"
        ) {
            formulairePaiement("Orange Money")
        }

        ajouterCarte(
            "📱",
            "MTN MONEY",
            "Paiement Mobile Money"
        ) {
            formulairePaiement("MTN Money")
        }

        ajouterCarte(
            "📱",
            "MOOV MONEY",
            "Paiement Mobile Money"
        ) {
            formulairePaiement("Moov Money")
        }

        ajouterCarte(
            "🌊",
            "WAVE",
            "Paiement Mobile Money"
        ) {
            formulairePaiement("Wave")
        }

        ajouterCarte(
            "💵",
            "ESPÈCES",
            "Paiement à la livraison"
        ) {
            Toast.makeText(
                this,
                "Paiement à la livraison",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun formulairePaiement(
        operateur: String
    ) {

        val numero = EditText(this)
        numero.hint = "Numéro $operateur"
        numero.inputType =
            InputType.TYPE_CLASS_PHONE
        numero.setSingleLine(true)

        val montant = EditText(this)
        montant.hint = "Montant en FCFA"
        montant.inputType =
            InputType.TYPE_CLASS_NUMBER

        val layout = LinearLayout(this)
        layout.orientation =
            LinearLayout.VERTICAL
        layout.setPadding(25, 5, 25, 5)

        layout.addView(numero)
        layout.addView(montant)

        AlertDialog.Builder(this)
            .setTitle("💳 $operateur")
            .setView(layout)
            .setNegativeButton(
                "Annuler",
                null
            )
            .setPositiveButton(
                "Continuer"
            ) { _, _ ->

                val num =
                    numero.text.toString().trim()

                val valeur =
                    montant.text.toString()
                        .toIntOrNull()

                if (
                    !numeroValide(num) ||
                    valeur == null ||
                    valeur <= 0
                ) {

                    Toast.makeText(
                        this,
                        "Numéro ou montant invalide",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                Toast.makeText(
                    this,
                    "Demande préparée : $valeur FCFA",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
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
        ) {}

        ajouterCarte(
            "📈",
            "VENTES",
            "Voir les ventes"
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
            "${clients.size} client(s)"
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
            "Gérer les produits"
        ) {
            afficherProduits()
        }

        ajouterCarte(
            "📦",
            "COMMANDES",
            "Gérer les commandes"
        ) {
            afficherCommandes()
        }

        ajouterCarte(
            "💳",
            "ABONNEMENTS",
            "Gérer les abonnements"
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
            "Voir les notifications"
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
            .setNegativeButton(
                "Fermer",
                null
            )
            .setPositiveButton(
                "Enregistrer"
            ) { _, _ ->

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

        val retour =
            boutonSecondaire("← Accueil")

        retour.setOnClickListener {
            afficherAccueil()
        }

        contenu.addView(retour)

        ajouterEspace(8)

        ajouterTitre(titrePage)
    }

    private fun ajouterTitre(texte: String) {

        val titre = TextView(this)

        titre.text = texte
        titre.textSize = 23f
        titre.setTextColor(
            Color.rgb(0, 110, 75)
        )
        titre.setPadding(
            5,
            12,
            5,
            18
        )

        contenu.addView(titre)
    }

    private fun ajouterCarte(
        icone: String,
        titre: String,
        description: String,
        action: () -> Unit
    ) {

        val carte = LinearLayout(this)

        carte.orientation =
            LinearLayout.VERTICAL

        carte.setPadding(
            18,
            16,
            18,
            16
        )

        carte.setBackgroundColor(
            Color.rgb(242, 244, 244)
        )

        carte.isClickable = true

        carte.setOnClickListener {
            action()
        }

        val titreView = TextView(this)

        titreView.text =
            "$icone  $titre"

        titreView.textSize = 19f

        titreView.setTextColor(
            Color.rgb(0, 100, 70)
        )

        val descriptionView =
            TextView(this)

        descriptionView.text =
            description

        descriptionView.textSize = 15f

        descriptionView.setTextColor(
            Color.DKGRAY
        )

        descriptionView.setPadding(
            0,
            8,
            0,
            2
        )

        carte.addView(titreView)
        carte.addView(descriptionView)

        val params =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            0,
            0,
            10
        )

        contenu.addView(
            carte,
            params
        )
    }

    private fun boutonPrincipal(
        texte: String
    ): Button {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 16f
        bouton.setTextColor(Color.WHITE)
        bouton.setBackgroundColor(
            Color.rgb(0, 120, 80)
        )
        bouton.gravity = Gravity.CENTER
        bouton.minHeight = 60

        bouton.setPadding(
            12,
            8,
            12,
            8
        )

        return bouton
    }

    private fun boutonSecondaire(
        texte: String
    ): Button {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 15f
        bouton.gravity = Gravity.CENTER
        bouton.minHeight = 55

        bouton.setPadding(
            12,
            8,
            12,
            8
        )

        return bouton
    }

    private fun boutonBas(
        texte: String
    ): Button {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 12f
        bouton.gravity = Gravity.CENTER

        bouton.setPadding(
            2,
            2,
            2,
            2
        )

        return bouton
    }

    private fun poidsNavigation():
        LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )
    }

    private fun ajouterEspace(
        taille: Int
    ) {

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