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
    // PRODUIT
    // =========================

    data class Produit(
        val id: Int,
        var nom: String,
        var prix: Int,
        var categorie: String,
        var description: String,
        var stock: Int,
        var image: Bitmap? = null
    ) : Serializable

    data class PanierItem(
        val produitId: Int,
        var quantite: Int
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<PanierItem>()

    private var prochainId = 1
    private var imageProduit: Bitmap? = null

    private val vert = Color.rgb(20, 120, 70)
    private val grisClair = Color.rgb(245, 245, 245)

    companion object {
        const val REQUEST_CAMERA = 100
        const val REQUEST_GALLERY = 101
    }

    // =========================
    // DÉMARRAGE
    // =========================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerProduits()

        afficherAccueil()
    }

    // =========================
    // PRODUITS DE DÉPART
    // =========================

    private fun chargerProduits() {

        if (produits.isNotEmpty()) return

        ajouterProduit(
            "Super7",
            20000,
            "Santé",
            "Produit Super7",
            20
        )

        ajouterProduit(
            "M7",
            15000,
            "Santé",
            "Produit M7",
            20
        )

        ajouterProduit(
            "Women 7",
            15000,
            "Femme",
            "Produit Women 7",
            20
        )

        ajouterProduit(
            "Timoc",
            10000,
            "Alimentation",
            "Produit Timoc",
            20
        )

        ajouterProduit(
            "Café Royal",
            5000,
            "Alimentation",
            "Café Royal",
            20
        )
    }

    private fun ajouterProduit(
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

        val racine = LinearLayout(this)
        racine.orientation = LinearLayout.VERTICAL
        racine.setBackgroundColor(Color.WHITE)

        // ---------------------------------
        // BARRE DU HAUT
        // ---------------------------------

        val haut = LinearLayout(this)
        haut.orientation = LinearLayout.HORIZONTAL
        haut.gravity = Gravity.CENTER_VERTICAL
        haut.setPadding(8, 8, 8, 8)

        val menu = TextView(this)
        menu.text = "☰"
        menu.textSize = 30f
        menu.gravity = Gravity.CENTER
        menu.setTextColor(vert)

        haut.addView(
            menu,
            LinearLayout.LayoutParams(55, 60)
        )

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 20f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(vert)
        titre.setTypeface(null, android.graphics.Typeface.BOLD)

        haut.addView(
            titre,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        val panierButton = TextView(this)
        panierButton.text = "🛒"
        panierButton.textSize = 28f
        panierButton.gravity = Gravity.CENTER

        haut.addView(
            panierButton,
            LinearLayout.LayoutParams(60, 60)
        )

        racine.addView(haut)

        menu.setOnClickListener {
            ouvrirMenu()
        }

        panierButton.setOnClickListener {
            afficherPanier()
        }

        // ---------------------------------
        // LOGO LION
        // ---------------------------------

        val logo = TextView(this)
        logo.text = "🦁"
        logo.textSize = 65f
        logo.gravity = Gravity.CENTER

        racine.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                105
            )
        )

        // ---------------------------------
        // CONTENU DÉFILABLE
        // ---------------------------------

        val scroll = ScrollView(this)

        val contenu = LinearLayout(this)
        contenu.orientation = LinearLayout.VERTICAL
        contenu.setPadding(12, 5, 12, 10)

        scroll.addView(contenu)

        // RECHERCHE

        val recherche = EditText(this)
        recherche.hint = "🔎 Rechercher un produit"
        recherche.setSingleLine(true)
        recherche.setPadding(15, 0, 15, 0)

        contenu.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            )
        )

        // CATÉGORIES

        val titreCategorie = TextView(this)
        titreCategorie.text = "Catégories"
        titreCategorie.textSize = 20f
        titreCategorie.setTypeface(null, android.graphics.Typeface.BOLD)
        titreCategorie.setPadding(5, 20, 5, 10)

        contenu.addView(titreCategorie)

        val categories = HorizontalScrollView(this)

        val categoriesLayout = LinearLayout(this)
        categoriesLayout.orientation = LinearLayout.HORIZONTAL

        val nomsCategories = arrayOf(
            "Tous",
            "Santé",
            "Femme",
            "Alimentation",
            "Cosmétique"
        )

        for (categorie in nomsCategories) {

            val bouton = Button(this)
            bouton.text = categorie

            categoriesLayout.addView(
                bouton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    50
                )
            )

            bouton.setOnClickListener {

                afficherProduits(
                    contenu,
                    recherche.text.toString(),
                    categorie
                )
            }
        }

        categories.addView(categoriesLayout)

        contenu.addView(
            categories,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )

        val titreProduits = TextView(this)
        titreProduits.text = "Nos produits"
        titreProduits.textSize = 22f
        titreProduits.setTypeface(null, android.graphics.Typeface.BOLD)
        titreProduits.setPadding(5, 20, 5, 10)

        contenu.addView(titreProduits)

        val liste = LinearLayout(this)
        liste.orientation = LinearLayout.VERTICAL

        contenu.addView(liste)

        afficherProduitsDansListe(
            liste,
            "",
            "Tous"
        )

        recherche.setOnEditorActionListener { _, _, _ ->

            afficherProduitsDansListe(
                liste,
                recherche.text.toString(),
                "Tous"
            )

            true
        }

        racine.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // ---------------------------------
        // BARRE DU BAS
        // ---------------------------------

        racine.addView(
            creerBarreBas(),
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                70
            )
        )

        setContentView(racine)
    }

    // =========================
    // AFFICHER PRODUITS
    // =========================

    private fun afficherProduits(
        contenu: LinearLayout,
        recherche: String,
        categorie: String
    ) {

        if (contenu.childCount == 0) return

        val liste = LinearLayout(this)
        liste.orientation = LinearLayout.VERTICAL

        afficherProduitsDansListe(
            liste,
            recherche,
            categorie
        )

        contenu.addView(liste)
    }

    private fun afficherProduitsDansListe(
        liste: LinearLayout,
        recherche: String,
        categorie: String
    ) {

        liste.removeAllViews()

        for (produit in produits) {

            if (
                recherche.isNotEmpty() &&
                !produit.nom.contains(
                    recherche,
                    ignoreCase = true
                )
            ) {
                continue
            }

            if (
                categorie != "Tous" &&
                produit.categorie != categorie
            ) {
                continue
            }

            liste.addView(
                creerCarteProduit(produit)
            )
        }
    }

    // =========================
    // CARTE PRODUIT
    // =========================

    private fun creerCarteProduit(
        produit: Produit
    ): LinearLayout {

        val carte = LinearLayout(this)
        carte.orientation = LinearLayout.VERTICAL
        carte.setPadding(10, 10, 10, 10)
        carte.setBackgroundColor(grisClair)

        val image = ImageView(this)

        if (produit.image != null) {
            image.setImageBitmap(produit.image)
        } else {
            image.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        carte.addView(
            image,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                260
            )
        )

        val nom = TextView(this)
        nom.text = produit.nom
        nom.textSize = 21f
        nom.gravity = Gravity.CENTER
        nom.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        carte.addView(nom)

        val prix = TextView(this)
        prix.text = "${produit.prix} FCFA"
        prix.textSize = 18f
        prix.gravity = Gravity.CENTER
        prix.setTextColor(vert)

        carte.addView(prix)

        val bouton = Button(this)
        bouton.text = "Voir le produit"

        carte.addView(bouton)

        bouton.setOnClickListener {
            afficherDetailProduit(produit)
        }

        carte.setOnClickListener {
            afficherDetailProduit(produit)
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 8, 0, 8)

        carte.layoutParams = params

        return carte
    }

    // =========================
    // DÉTAIL PRODUIT
    // =========================

    private fun afficherDetailProduit(
        produit: Produit
    ) {

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

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

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
        nom.gravity = Gravity.CENTER
        nom.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

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
        description.setPadding(5, 20, 5, 20)

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
                quantite.text.toString()
                    .toIntOrNull() ?: 1

            if (qte <= 0) {
                Toast.makeText(
                    this,
                    "Quantité incorrecte",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (qte > produit.stock) {
                Toast.makeText(
                    this,
                    "Stock insuffisant",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val existant =
                panier.find {
                    it.produitId == produit.id
                }

            if (existant == null) {
                panier.add(
                    PanierItem(
                        produit.id,
                        qte
                    )
                )
            } else {
                existant.quantite += qte
            }

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

    private fun afficherPanier() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        val titre = TextView(this)
        titre.text = "🛒 PANIER"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER
        titre.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        layout.addView(titre)

        var total = 0

        for (item in panier) {

            val produit =
                produits.find {
                    it.id == item.produitId
                } ?: continue

            val ligne = TextView(this)

            val sousTotal =
                produit.prix * item.quantite

            total += sousTotal

            ligne.text =
                "${produit.nom} x ${item.quantite} = $sousTotal FCFA"

            ligne.textSize = 18f
            ligne.setPadding(5, 15, 5, 15)

            layout.addView(ligne)
        }

        val totalText = TextView(this)
        totalText.text = "TOTAL : $total FCFA"
        totalText.textSize = 22f
        totalText.gravity = Gravity.CENTER
        totalText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        layout.addView(totalText)

        val commander = Button(this)
        commander.text = "✅ Commander"

        layout.addView(commander)

        commander.setOnClickListener {

            if (panier.isEmpty()) {

                Toast.makeText(
                    this,
                    "Le panier est vide",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Commande enregistrée",
                    Toast.LENGTH_LONG
                ).show()
            }
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
    // MENU
    // =========================

    private fun ouvrirMenu() {

        val options = arrayOf(
            "👤 Création de compte",
            "🏪 Espace vendeur",
            "💳 Abonnement vendeur",
            "📦 Gestion des produits",
            "📊 Stock",
            "🧾 Commandes",
            "👑 Administration",
            "⚙️ Paramètres"
        )

        AlertDialog.Builder(this)
            .setTitle("VIE ESPOIR MARKETING")
            .setItems(options) { _, position ->

                when (position) {

                    0 -> afficherCompte()

                    1 -> afficherVendeur()

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

    private fun afficherCompte() {

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
        layout.addView(telephone)

        val email = EditText(this)
        email.hint = "Email"
        layout.addView(email)

        val bouton = Button(this)
        bouton.text = "Créer le compte"

        layout.addView(bouton)

        bouton.setOnClickListener {

            Toast.makeText(
                this,
                "Compte créé",
                Toast.LENGTH_SHORT
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

    private fun afficherVendeur() {

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

    // =========================
    // ABONNEMENT
    // =========================

    private fun afficherAbonnement() {

        val packs = arrayOf(
            "Pack 5 000 FCFA",
            "Pack 10 000 FCFA",
            "Pack 25 000 FCFA",
            "Pack Premium 50 000 FCFA"
        )

        AlertDialog.Builder(this)
            .setTitle("Abonnement vendeur")
            .setItems(packs) { _, position ->

                Toast.makeText(
                    this,
                    packs[position],
                    Toast.LENGTH_SHORT
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
        prix.hint = "Prix"
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
                280
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
        enregistrer.text = "✅ Enregistrer le produit"
        layout.addView(enregistrer)

        enregistrer.setOnClickListener {

            val nomTexte =
                nom.text.toString().trim()

            val prixTexte =
                prix.text.toString().toIntOrNull()

            val stockTexte =
                stock.text.toString().toIntOrNull()

            if (
                nomTexte.isEmpty() ||
                prixTexte == null ||
                stockTexte == null
            ) {

                Toast.makeText(
                    this,
                    "Remplissez les informations",
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
                "Caméra indisponible",
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
    // RETOUR IMAGE
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
                    data?.extras?.get("data")
                        as? Bitmap

                if (bitmap != null) {

                    imageProduit = bitmap

                    Toast.makeText(
                        this,
                        "Photo prise",
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
                            "Impossible de charger la photo",
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
                            "Supprimer"
                        )
                    ) { _, _ ->

                        produits.remove(produit)

                        Toast.makeText(
                            this,
                            "Produit supprimé",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .show()
            }
            .show()
    }

    // =========================
    // STOCK
    // =========================

    private fun afficherStock() {

        val texte = StringBuilder()

        for (produit in produits) {

            texte.append(
                "${produit.nom} : ${produit.stock}\n"
            )
        }

        AlertDialog.Builder(this)
            .setTitle("📊 Stock")
            .setMessage(texte.toString())
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
                "Les commandes apparaîtront ici."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun afficherVentes() {

        AlertDialog.Builder(this)
            .setTitle("📊 Ventes")
            .setMessage(
                "Les ventes apparaîtront ici."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun afficherRevenus() {

        AlertDialog.Builder(this)
            .setTitle("💰 Revenus")
            .setMessage(
                "Les revenus apparaîtront ici."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    // =========================
    // ADMINISTRATION
    // =========================

    private fun afficherAdministration() {

        val options = arrayOf(
            "Utilisateurs",
            "Vendeurs",
            "Produits",
            "Stock",
            "Commandes",
            "Abonnements",
            "Paiements",
            "Rapports"
        )

        AlertDialog.Builder(this)
            .setTitle("👑 Administration")
            .setItems(options) { _, position ->

                Toast.makeText(
                    this,
                    options[position],
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

    private fun creerBarreBas(): LinearLayout {

        val barre = LinearLayout(this)
        barre.orientation = LinearLayout.HORIZONTAL
        barre.gravity = Gravity.CENTER
        barre.setBackgroundColor(Color.rgb(235, 248, 240))

        val accueil = boutonBas("🏠\nAccueil")
        val produits = boutonBas("🛍️\nProduits")
        val panier = boutonBas("🛒\nPanier")
        val compte = boutonBas("👤\nCompte")

        barre.addView(
            accueil,
            LinearLayout.LayoutParams(0, 70, 1f)
        )

        barre.addView(
            produits,
            LinearLayout.LayoutParams(0, 70, 1f)
        )

        barre.addView(
            panier,
            LinearLayout.LayoutParams(0, 70, 1f)
        )

        barre.addView(
            compte,
            LinearLayout.LayoutParams(0, 70, 1f)
        )

        accueil.setOnClickListener {
            afficherAccueil()
        }

        produits.setOnClickListener {
            afficherAccueil()
        }

        panier.setOnClickListener {
            afficherPanier()
        }

        compte.setOnClickListener {
            afficherCompte()
        }

        return barre
    }

    private fun boutonBas(
        texte: String
    ): TextView {

        val bouton = TextView(this)

        bouton.text = texte
        bouton.textSize = 13f
        bouton.gravity = Gravity.CENTER
        bouton.setTextColor(vert)

        return bouton
    }
}