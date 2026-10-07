package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.Serializable

class MainActivity : Activity() {

    // =========================
    // DONNÉES
    // =========================

    data class Produit(
        val id: Int,
        var nom: String,
        var prix: Int,
        var categorie: String,
        var description: String = "",
        var stock: Int = 0,
        var image: Bitmap? = null
    ) : Serializable

    data class PanierItem(
        val produit: Produit,
        var quantite: Int
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<PanierItem>()

    private var prochainId = 1
    private var imageProduit: Bitmap? = null

    private var recherche = ""
    private var categorieSelectionnee = "Tous"

    companion object {
        const val REQUEST_CAMERA = 100
        const val REQUEST_GALLERY = 101
    }

    // =========================
    // COULEURS
    // =========================

    private val vert = Color.rgb(20, 120, 70)
    private val vertClair = Color.rgb(235, 248, 240)
    private val gris = Color.rgb(245, 245, 245)
    private val noir = Color.rgb(30, 30, 30)

    // =========================
    // INITIALISATION
    // =========================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerProduitsDepart()
        afficherAccueil()
    }

    private fun chargerProduitsDepart() {

        if (produits.isNotEmpty()) return

        ajouterProduitDepart(
            "Super7",
            20000,
            "Santé",
            "Produit Super7",
            20
        )

        ajouterProduitDepart(
            "M7",
            15000,
            "Santé",
            "Produit M7",
            20
        )

        ajouterProduitDepart(
            "Women 7",
            15000,
            "Femme",
            "Produit Women 7",
            20
        )

        ajouterProduitDepart(
            "Timoc",
            10000,
            "Alimentation",
            "Produit Timoc",
            20
        )

        ajouterProduitDepart(
            "Café Royal",
            5000,
            "Alimentation",
            "Café Royal",
            20
        )
    }

    private fun ajouterProduitDepart(
        nom: String,
        prix: Int,
        categorie: String,
        description: String,
        stock: Int
    ) {
        produits.add(
            Produit(
                prochainId++,
                nom,
                prix,
                categorie,
                description,
                stock
            )
        )
    }

    // =========================
    // ACCUEIL
    // =========================

    private fun afficherAccueil() {

        val principal = creerEcranPrincipal()

        // EN-TÊTE
        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(10, 10, 10, 10)

        val menu = TextView(this)
        menu.text = "☰"
        menu.textSize = 30f
        menu.gravity = Gravity.CENTER
        menu.setTextColor(vert)

        header.addView(
            menu,
            LinearLayout.LayoutParams(55, 65)
        )

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 21f
        titre.setTextColor(vert)
        titre.setTypeface(null, android.graphics.Typeface.BOLD)
        titre.gravity = Gravity.CENTER

        header.addView(
            titre,
            LinearLayout.LayoutParams(
                0,
                65,
                1f
            )
        )

        val panier = TextView(this)
        panier.text = "🛒"
        panier.textSize = 27f
        panier.gravity = Gravity.CENTER

        header.addView(
            panier,
            LinearLayout.LayoutParams(60, 65)
        )

        principal.addView(header)

        menu.setOnClickListener {
            ouvrirMenu()
        }

        panier.setOnClickListener {
            afficherPanier()
        }

        // =========================
        // LOGO LION
        // =========================

        val logo = TextView(this)
        logo.text = "🦁"
        logo.textSize = 65f
        logo.gravity = Gravity.CENTER

        principal.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100
            )
        )

        // =========================
        // RECHERCHE
        // =========================

        val rechercheBox = EditText(this)
        rechercheBox.hint = "🔎 Rechercher un produit..."
        rechercheBox.setSingleLine(true)
        rechercheBox.setPadding(20, 5, 20, 5)

        principal.addView(
            rechercheBox,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            )
        )

        rechercheBox.setText(recherche)

        rechercheBox.setOnEditorActionListener { _, _, _ ->
            recherche = rechercheBox.text.toString()
            afficherProduitsAccueil(principal)
            true
        }

        // =========================
        // CATÉGORIES
        // =========================

        val titreCategorie = TextView(this)
        titreCategorie.text = "Catégories"
        titreCategorie.textSize = 20f
        titreCategorie.setTypeface(null, android.graphics.Typeface.BOLD)
        titreCategorie.setPadding(10, 20, 10, 10)

        principal.addView(titreCategorie)

        val categories = HorizontalScrollView(this)

        val categoriesLayout = LinearLayout(this)
        categoriesLayout.orientation = LinearLayout.HORIZONTAL

        val listeCategories = arrayOf(
            "Tous",
            "Santé",
            "Femme",
            "Alimentation",
            "Cosmétique"
        )

        for (cat in listeCategories) {

            val bouton = Button(this)
            bouton.text = cat

            if (cat == categorieSelectionnee) {
                bouton.setTextColor(Color.WHITE)
                bouton.setBackgroundColor(vert)
            }

            categoriesLayout.addView(
                bouton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    50
                )
            )

            bouton.setOnClickListener {
                categorieSelectionnee = cat
                afficherAccueil()
            }
        }

        categories.addView(categoriesLayout)

        principal.addView(
            categories,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // =========================
        // PRODUITS
        // =========================

        afficherProduitsAccueil(principal)

        // =========================
        // BARRE DU BAS
        // =========================

        ajouterBarreNavigation(principal)

        setContentView(principal)
    }

    // =========================
    // AFFICHAGE PRODUITS
    // =========================

    private fun afficherProduitsAccueil(principal: LinearLayout) {

        val titre = TextView(this)
        titre.text = "Produits"
        titre.textSize = 22f
        titre.setTypeface(null, android.graphics.Typeface.BOLD)
        titre.setPadding(10, 20, 10, 10)

        principal.addView(titre)

        val conteneur = LinearLayout(this)
        conteneur.orientation = LinearLayout.VERTICAL

        for (produit in produits) {

            if (recherche.isNotBlank() &&
                !produit.nom.contains(
                    recherche,
                    ignoreCase = true
                )
            ) {
                continue
            }

            if (categorieSelectionnee != "Tous" &&
                produit.categorie != categorieSelectionnee
            ) {
                continue
            }

            conteneur.addView(creerCarteProduit(produit))
        }

        principal.addView(
            conteneur,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
    }

    private fun creerCarteProduit(produit: Produit): LinearLayout {

        val carte = LinearLayout(this)
        carte.orientation = LinearLayout.VERTICAL
        carte.setPadding(12, 12, 12, 12)
        carte.setBackgroundColor(Color.WHITE)

        // IMAGE
        val image = ImageView(this)

        if (produit.image != null) {
            image.setImageBitmap(produit.image)
        } else {
            image.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        image.scaleType = ImageView.ScaleType.CENTER_CROP

        carte.addView(
            image,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                260
            )
        )

        // NOM
        val nom = TextView(this)
        nom.text = produit.nom
        nom.textSize = 20f
        nom.setTypeface(null, android.graphics.Typeface.BOLD)
        nom.gravity = Gravity.CENTER
        nom.setTextColor(noir)

        carte.addView(nom)

        // PRIX
        val prix = TextView(this)
        prix.text = "${produit.prix} FCFA"
        prix.textSize = 18f
        prix.gravity = Gravity.CENTER
        prix.setTextColor(vert)

        carte.addView(prix)

        // STOCK
        val stock = TextView(this)

        stock.text =
            if (produit.stock > 0)
                "Disponible : ${produit.stock}"
            else
                "Rupture de stock"

        stock.gravity = Gravity.CENTER

        carte.addView(stock)

        // BOUTON
        val bouton = Button(this)
        bouton.text = "Voir le produit"

        carte.addView(bouton)

        bouton.setOnClickListener {
            afficherDetailProduit(produit)
        }

        carte.setOnClickListener {
            afficherDetailProduit(produit)
        }

        val separation = View(this)
        separation.setBackgroundColor(Color.LTGRAY)

        val bloc = LinearLayout(this)
        bloc.orientation = LinearLayout.VERTICAL

        bloc.addView(
            carte,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        bloc.addView(
            separation,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
            )
        )

        return bloc
    }

    // =========================
    // DÉTAIL PRODUIT
    // =========================

    private fun afficherDetailProduit(produit: Produit) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        val retour = Button(this)
        retour.text = "⬅️ Retour"
        layout.addView(retour)

        retour.setOnClickListener {
            afficherAccueil()
        }

        val image = ImageView(this)

        if (produit.image != null) {
            image.setImageBitmap(produit.image)
        } else {
            image.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        image.scaleType = ImageView.ScaleType.CENTER_CROP

        layout.addView(
            image,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                350
            )
        )

        val nom = TextView(this)
        nom.text = produit.nom
        nom.textSize = 26f
        nom.setTypeface(null, android.graphics.Typeface.BOLD)
        nom.gravity = Gravity.CENTER

        layout.addView(nom)

        val prix = TextView(this)
        prix.text = "${produit.prix} FCFA"
        prix.textSize = 22f
        prix.gravity = Gravity.CENTER
        prix.setTextColor(vert)

        layout.addView(prix)

        val description = TextView(this)
        description.text = produit.description
        description.textSize = 17f
        description.setPadding(10, 20, 10, 20)

        layout.addView(description)

        val quantite = EditText(this)
        quantite.hint = "Quantité"
        quantite.inputType = 2
        quantite.setText("1")

        layout.addView(quantite)

        val ajouter = Button(this)
        ajouter.text = "🛒 Ajouter au panier"

        layout.addView(ajouter)

        ajouter.setOnClickListener {

            val qte =
                quantite.text.toString().toIntOrNull() ?: 1

            if (qte <= 0) {
                Toast.makeText(
                    this,
                    "Quantité incorrecte",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (produit.stock < qte) {
                Toast.makeText(
                    this,
                    "Stock insuffisant",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            ajouterAuPanier(produit, qte)

            Toast.makeText(
                this,
                "Produit ajouté au panier",
                Toast.LENGTH_SHORT
            ).show()
        }

        setContentView(layout)
    }

    // =========================
    // PANIER
    // =========================

    private fun ajouterAuPanier(
        produit: Produit,
        quantite: Int
    ) {

        val existant =
            panier.find { it.produit.id == produit.id }

        if (existant != null) {
            existant.quantite += quantite
        } else {
            panier.add(
                PanierItem(
                    produit,
                    quantite
                )
            )
        }
    }

    private fun afficherPanier() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        val titre = TextView(this)
        titre.text = "🛒 Mon panier"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER
        titre.setTypeface(null, android.graphics.Typeface.BOLD)

        layout.addView(titre)

        var total = 0

        for (item in panier) {

            val bloc = LinearLayout(this)
            bloc.orientation = LinearLayout.VERTICAL
            bloc.setPadding(10, 15, 10, 15)

            val nom = TextView(this)
            nom.text =
                "${item.produit.nom} × ${item.quantite}"
            nom.textSize = 19f

            bloc.addView(nom)

            val sousTotal =
                item.produit.prix * item.quantite

            total += sousTotal

            val prix = TextView(this)
            prix.text = "$sousTotal FCFA"
            prix.textSize = 17f
            prix.setTextColor(vert)

            bloc.addView(prix)

            layout.addView(bloc)
        }

        val totalText = TextView(this)
        totalText.text = "TOTAL : $total FCFA"
        totalText.textSize = 22f
        totalText.setTypeface(null, android.graphics.Typeface.BOLD)
        totalText.gravity = Gravity.CENTER
        totalText.setPadding(10, 20, 10, 20)

        layout.addView(totalText)

        val commander = Button(this)
        commander.text = "✅ Commander"

        layout.addView(commander)

        commander.setOnClickListener {

            if (panier.isEmpty()) {
                Toast.makeText(
                    this,
                    "Votre panier est vide",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Commande enregistrée",
                Toast.LENGTH_LONG
            ).show()
        }

        val retour = Button(this)
        retour.text = "⬅️ Continuer mes achats"

        layout.addView(retour)

        retour.setOnClickListener {
            afficherAccueil()
        }

        setContentView(layout)
    }

    // =========================
    // MENU ☰
    // =========================

    private fun ouvrirMenu() {

        val options = arrayOf(
            "👤 Création de compte",
            "🏪 Espace vendeur",
            "💳 Abonnement vendeur",
            "📦 Gestion des produits",
            "📊 Gestion du stock",
            "🧾 Gestion des commandes",
            "👑 Administration",
            "⚙️ Paramètres"
        )

        AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(options) { _, position ->

                when (position) {

                    0 -> afficherCreationCompte()

                    1 -> afficherEspaceVendeur()

                    2 -> afficherAbonnement()

                    3 -> afficherGestionProduits()

                    4 -> afficherStock()

                    5 -> afficherCommandes()

                    6 -> afficherAdministration()

                    7 -> afficherParametres()
                }
            }
            .show()
    }

    // =========================
    // COMPTE
    // =========================

    private fun afficherCreationCompte() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "Créer un compte"
        titre.textSize = 24f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val nom = EditText(this)
        nom.hint = "Nom complet"
        layout.addView(nom)

        val telephone = EditText(this)
        telephone.hint = "Téléphone"
        telephone.inputType = 3
        layout.addView(telephone)

        val email = EditText(this)
        email.hint = "Email"
        layout.addView(email)

        val motDePasse = EditText(this)
        motDePasse.hint = "Mot de passe"
        motDePasse.inputType = 129
        layout.addView(motDePasse)

        val creer = Button(this)
        creer.text = "Créer mon compte"

        layout.addView(creer)

        creer.setOnClickListener {

            Toast.makeText(
                this,
                "Compte créé avec succès",
                Toast.LENGTH_LONG
            ).show()
        }

        val retour = Button(this)
        retour.text = "⬅️ Retour"
        layout.addView(retour)

        retour.setOnClickListener {
            afficherAccueil()
        }

        setContentView(layout)
    }

    // =========================
    // VENDEUR
    // =========================

    private fun afficherEspaceVendeur() {

        val options = arrayOf(
            "➕ Ajouter un produit",
            "📦 Mes produits",
            "🧾 Mes commandes",
            "📊 Mes ventes",
            "💰 Mes revenus"
        )

        AlertDialog.Builder(this)
            .setTitle("Espace vendeur")
            .setItems(options) { _, position ->

                when (position) {

                    0 -> afficherAjoutProduit()

                    1 -> afficherGestionProduits()

                    2 -> afficherCommandes()

                    3 -> afficherVentes()

                    4 -> afficherRevenus()
                }
            }
            .show()
    }

    private fun afficherAbonnement() {

        val packs = arrayOf(
            "Pack vendeur - 5 000 FCFA",
            "Pack vendeur - 10 000 FCFA",
            "Pack vendeur - 25 000 FCFA",
            "Pack vendeur Premium - 50 000 FCFA"
        )

        AlertDialog.Builder(this)
            .setTitle("Abonnement vendeur")
            .setItems(packs) { _, position ->

                Toast.makeText(
                    this,
                    "Pack sélectionné : ${packs[position]}",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    // =========================
    // AJOUT PRODUIT
    // =========================

    private fun afficherAjoutProduit() {

        imageProduit = null

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        val titre = TextView(this)
        titre.text = "Ajouter un produit"
        titre.textSize = 24f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val nom = EditText(this)
        nom.hint = "Nom du produit"
        layout.addView(nom)

        val prix = EditText(this)
        prix.hint = "Prix en FCFA"
        prix.inputType = 2
        layout.addView(prix)

        val categorie = EditText(this)
        categorie.hint = "Catégorie"
        layout.addView(categorie)

        val description = EditText(this)
        description.hint = "Description"
        layout.addView(description)

        val stock = EditText(this)
        stock.hint = "Stock"
        stock.inputType = 2
        layout.addView(stock)

        val image = ImageView(this)
        image.setImageResource(
            android.R.drawable.ic_menu_gallery
        )

        layout.addView(
            image,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                300
            )
        )

        val camera = Button(this)
        camera.text = "📷 Prendre une photo"
        layout.addView(camera)

        camera.setOnClickListener {
            ouvrirCamera()
        }

        val galerie = Button(this)
        galerie.text = "🖼️ Choisir dans la galerie"
        layout.addView(galerie)

        galerie.setOnClickListener {
            ouvrirGalerie()
        }

        val enregistrer = Button(this)
        enregistrer.text = "✅ Ajouter le produit"
        layout.addView(enregistrer)

        enregistrer.setOnClickListener {

            val nomTexte = nom.text.toString().trim()
            val prixTexte =
                prix.text.toString().toIntOrNull()
            val stockTexte =
                stock.text.toString().toIntOrNull()

            if (nomTexte.isEmpty() ||
                prixTexte == null ||
                stockTexte == null
            ) {

                Toast.makeText(
                    this,
                    "Veuillez remplir les informations",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            produits.add(
                Produit(
                    prochainId++,
                    nomTexte,
                    prixTexte,
                    categorie.text.toString(),
                    description.text.toString(),
                    stockTexte,
                    imageProduit
                )
            )

            Toast.makeText(
                this,
                "Produit ajouté avec succès",
                Toast.LENGTH_LONG
            ).show()

            afficherAccueil()
        }

        setContentView(layout)
    }

    // =========================
    // CAMÉRA
    // =========================

    private fun ouvrirCamera() {

        try {

            val intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(
                intent,
                REQUEST_CAMERA
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Impossible d'ouvrir la caméra",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================
    // GALERIE
    // =========================

    private fun ouvrirGalerie() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(
            intent,
            REQUEST_GALLERY
        )
    }

    // =========================
    // RETOUR PHOTO
    // =========================

    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (resultCode != RESULT_OK) return

        when (requestCode) {

            REQUEST_CAMERA -> {

                val bitmap =
                    data?.extras?.get("data") as? Bitmap

                if (bitmap != null) {

                    imageProduit = bitmap

                    Toast.makeText(
                        this,
                        "Photo prise avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            REQUEST_GALLERY -> {

                val uri: Uri? = data?.data

                if (uri != null) {

                    try {

                        imageProduit =
                            MediaStore.Images.Media.getBitmap(
                                contentResolver,
                                uri
                            )

                        Toast.makeText(
                            this,
                            "Photo sélectionnée",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Erreur avec la photo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // =========================
    // GESTION PRODUITS
    // =========================

    private fun afficherGestionProduits() {

        val noms = produits.map {
            "${it.nom} - ${it.prix} FCFA"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Gestion des produits")
            .setItems(noms) { _, position ->

                val produit = produits[position]

                AlertDialog.Builder(this)
                    .setTitle(produit.nom)
                    .setItems(
                        arrayOf(
                            "Modifier",
                            "Supprimer"
                        )
                    ) { _, action ->

                        if (action == 1) {

                            produits.remove(produit)

                            Toast.makeText(
                                this,
                                "Produit supprimé",
                                Toast.LENGTH_SHORT
                            ).show()

                            afficherAccueil()
                        }
                    }
                    .show()
            }
            .setNegativeButton("Fermer", null)
            .show()
    }

    // =========================
    // STOCK
    // =========================

    private fun afficherStock() {

        val message = StringBuilder()

        for (p in produits) {

            message.append(
                "${p.nom} : ${p.stock} unités\n"
            )
        }

        AlertDialog.Builder(this)
            .setTitle("📊 Stock")
            .setMessage(message.toString())
            .setPositiveButton("OK", null)
            .show()
    }

    // =========================
    // COMMANDES
    // =========================

    private fun afficherCommandes() {

        AlertDialog.Builder(this)
            .setTitle("🧾 Commandes")
            .setMessage(
                "Les commandes des clients apparaîtront ici."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun afficherVentes() {

        AlertDialog.Builder(this)
            .setTitle("📊 Mes ventes")
            .setMessage(
                "Les statistiques de ventes apparaîtront ici."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun afficherRevenus() {

        AlertDialog.Builder(this)
            .setTitle("💰 Mes revenus")
            .setMessage(
                "Le tableau des revenus apparaîtra ici."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    // =========================
    // ADMINISTRATION
    // =========================

    private fun afficherAdministration() {

        val options = arrayOf(
            "👥 Utilisateurs",
            "🏪 Vendeurs",
            "📦 Produits",
            "📊 Stock",
            "🧾 Commandes",
            "💳 Abonnements",
            "💰 Paiements",
            "📄 Rapports"
        )

        AlertDialog.Builder(this)
            .setTitle("👑 Administration")
            .setItems(options) { _, position ->

                Toast.makeText(
                    this,
                    "${options[position]} sélectionné",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun afficherParametres() {

        AlertDialog.Builder(this)
            .setTitle("⚙️ Paramètres")
            .setMessage(
                "Paramètres de VIE ESPOIR MARKETING"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    // =========================
    // BARRE DU BAS
    // =========================

    private fun ajouterBarreNavigation(
        principal: LinearLayout
    ) {

        val barre = LinearLayout(this)

        barre.orientation = LinearLayout.HORIZONTAL
        barre.gravity = Gravity.CENTER
        barre.setBackgroundColor(vertClair)

        val accueil = creerBoutonNavigation(
            "🏠\nAccueil"
        )

        val produitsBtn = creerBoutonNavigation(
            "🛍️\nProduits"
        )

        val panierBtn = creerBoutonNavigation(
            "🛒\nPanier"
        )

        val compteBtn = creerBoutonNavigation(
            "👤\nCompte"
        )

        barre.addView(
            accueil,
            LinearLayout.LayoutParams(
                0,
                70,
                1f
            )
        )

        barre.addView(
            produitsBtn,
            LinearLayout.LayoutParams(
                0,
                70,
                1f
            )
        )

        barre.addView(
            panierBtn,
            LinearLayout.LayoutParams(
                0,
                70,
                1f
            )
        )

        barre.addView(
            compteBtn,
            LinearLayout.LayoutParams(
                0,
                70,
                1f
            )
        )

        accueil.setOnClickListener {
            recherche = ""
            categorieSelectionnee = "Tous"
            afficherAccueil()
        }

        produitsBtn.setOnClickListener {
            afficherListeProduits()
        }

        panierBtn.setOnClickListener {
            afficherPanier()
        }

        compteBtn.setOnClickListener {
            afficherCreationCompte()
        }

        principal.addView(barre)
    }

    private fun creerBoutonNavigation(
        texte: String
    ): TextView {

        val bouton = TextView(this)

        bouton.text = texte
        bouton.textSize = 13f
        bouton.gravity = Gravity.CENTER
        bouton.setTextColor(vert)

        return bouton
    }

    // =========================
    // PAGE PRODUITS
    // =========================

    private fun afficherListeProduits() {

        categorieSelectionnee = "Tous"
        recherche = ""

        afficherAccueil()
    }

    // =========================
    // ÉCRAN PRINCIPAL
    // =========================

    private fun creerEcranPrincipal(): LinearLayout {

        val scroll = ScrollView(this)

        val contenu = LinearLayout(this)
        contenu.orientation = LinearLayout.VERTICAL
        contenu.setPadding(10, 5, 10, 5)

        scroll.addView(contenu)

        return contenu
    }
}