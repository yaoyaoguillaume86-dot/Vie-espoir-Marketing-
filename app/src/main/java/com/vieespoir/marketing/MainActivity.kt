package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {

    // =========================
    // DONNÉES
    // =========================

    data class Produit(
        var nom: String,
        var prix: String,
        var description: String,
        var categorie: String,
        var stock: Int,
        var imageUri: String = "",
        var vendeur: String = "Vie Espoir Marketing"
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<Produit>()

    private var logoUri: String = ""
    private var entreprise = "Vie Espoir Marketing"
    private var nomVendeur = ""
    private var telephone = ""

    private lateinit var contenu: LinearLayout
    private lateinit var titre: TextView

    // =========================
    // COULEURS
    // =========================

    private val vert = Color.rgb(20, 125, 78)
    private val vertClair = Color.rgb(232, 248, 239)
    private val gris = Color.rgb(245, 245, 245)
    private val grisTexte = Color.rgb(95, 95, 95)

    // =========================
    // INITIALISATION
    // =========================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerDonnees()
        construireInterface()
        afficherAccueil()
    }

    // =========================
    // INTERFACE PRINCIPALE
    // =========================

    private fun construireInterface() {

        val principal = LinearLayout(this)
        principal.orientation = LinearLayout.VERTICAL
        principal.setBackgroundColor(Color.WHITE)

        // -------- BARRE DU HAUT --------

        val barreHaut = LinearLayout(this)
        barreHaut.orientation = LinearLayout.HORIZONTAL
        barreHaut.gravity = Gravity.CENTER_VERTICAL
        barreHaut.setPadding(12, 8, 12, 8)

        val menu = TextView(this)
        menu.text = "☰"
        menu.textSize = 32f
        menu.setTextColor(vert)
        menu.gravity = Gravity.CENTER
        menu.setOnClickListener {
            ouvrirMenu()
        }

        barreHaut.addView(
            menu,
            LinearLayout.LayoutParams(55, 60)
        )

        titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 22f
        titre.setTextColor(vert)
        titre.setTypeface(null, android.graphics.Typeface.BOLD)
        titre.gravity = Gravity.CENTER

        barreHaut.addView(
            titre,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        val panierHaut = TextView(this)
        panierHaut.text = "🛒"
        panierHaut.textSize = 27f
        panierHaut.gravity = Gravity.CENTER
        panierHaut.setOnClickListener {
            afficherPanier()
        }

        barreHaut.addView(
            panierHaut,
            LinearLayout.LayoutParams(55, 60)
        )

        principal.addView(barreHaut)

        // -------- CONTENU --------

        val scroll = ScrollView(this)

        contenu = LinearLayout(this)
        contenu.orientation = LinearLayout.VERTICAL
        contenu.setPadding(16, 5, 16, 20)

        scroll.addView(contenu)

        principal.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // -------- MENU BAS --------

        val menuBas = LinearLayout(this)
        menuBas.orientation = LinearLayout.HORIZONTAL
        menuBas.gravity = Gravity.CENTER
        menuBas.setBackgroundColor(vertClair)

        menuBas.addView(
            boutonBas("🏠", "Accueil") {
                afficherAccueil()
            },
            poids()
        )

        menuBas.addView(
            boutonBas("🛍️", "Produits") {
                afficherProduits()
            },
            poids()
        )

        menuBas.addView(
            boutonBas("🛒", "Panier") {
                afficherPanier()
            },
            poids()
        )

        menuBas.addView(
            boutonBas("👤", "Compte") {
                afficherCompte()
            },
            poids()
        )

        principal.addView(
            menuBas,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                72
            )
        )

        setContentView(principal)
    }

    private fun poids(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1f
        )
    }

    private fun boutonBas(
        icone: String,
        nom: String,
        action: () -> Unit
    ): LinearLayout {

        val bloc = LinearLayout(this)
        bloc.orientation = LinearLayout.VERTICAL
        bloc.gravity = Gravity.CENTER
        bloc.setOnClickListener {
            action()
        }

        val ic = TextView(this)
        ic.text = icone
        ic.textSize = 24f
        ic.gravity = Gravity.CENTER

        val txt = TextView(this)
        txt.text = nom
        txt.textSize = 14f
        txt.setTextColor(vert)
        txt.gravity = Gravity.CENTER

        bloc.addView(ic)
        bloc.addView(txt)

        return bloc
    }

    // =========================
    // ACCUEIL
    // =========================

    private fun afficherAccueil() {

        vider()

        titre.text = "VIE ESPOIR MARKETING"

        val logo = ImageView(this)

        if (logoUri.isNotEmpty()) {
            try {
                logo.setImageURI(Uri.parse(logoUri))
            } catch (_: Exception) {
                logo.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            logo.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        logo.scaleType = ImageView.ScaleType.CENTER_INSIDE

        contenu.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100
            )
        )

        val recherche = EditText(this)
        recherche.hint = "🔎 Rechercher un produit"
        recherche.textSize = 18f
        recherche.quelqueChose.setSingleLine(true)

        contenu.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )

        val catTitre = texte("Catégories", 26f, Color.DKGRAY)
        catTitre.setTypeface(null, android.graphics.Typeface.BOLD)

        contenu.addView(catTitre)

        val categories = LinearLayout(this)
        categories.orientation = LinearLayout.HORIZONTAL

        val noms = arrayOf(
            "Tous",
            "Santé",
            "Beauté",
            "Alimentation"
        )

        for (nom in noms) {

            val b = Button(this)
            b.text = nom
            b.textSize = 12f

            b.setOnClickListener {
                afficherProduits(nom)
            }

            categories.addView(
                b,
                LinearLayout.LayoutParams(
                    0,
                    55,
                    1f
                )
            )
        }

        contenu.addView(categories)

        val produitsTitre = texte(
            "Nos produits",
            28f,
            Color.DKGRAY
        )

        produitsTitre.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        contenu.addView(produitsTitre)

        val boutonAjouter = Button(this)
        boutonAjouter.text = "＋ AJOUTER UN PRODUIT"
        boutonAjouter.textSize = 16f
        boutonAjouter.setTextColor(Color.WHITE)
        boutonAjouter.setBackgroundColor(vert)

        boutonAjouter.setOnClickListener {
            ajouterProduit()
        }

        contenu.addView(
            boutonAjouter,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                58
            )
        )

        afficherListeProduits()
    }

    // =========================
    // PRODUITS
    // =========================

    private fun afficherProduits(
        categorie: String = "Tous"
    ) {

        vider()

        titre.text = "PRODUITS"

        val recherche = EditText(this)
        recherche.hint = "🔎 Rechercher un produit"
        recherche.quelqueChose.setSingleLine(true)

        contenu.addView(recherche)

        val ajouter = Button(this)
        ajouter.text = "＋ AJOUTER UN PRODUIT"
        ajouter.setTextColor(Color.WHITE)
        ajouter.setBackgroundColor(vert)

        ajouter.setOnClickListener {
            ajouterProduit()
        }

        contenu.addView(ajouter)

        val titreProduits = texte(
            if (categorie == "Tous")
                "Nos produits"
            else
                categorie,
            27f,
            Color.DKGRAY
        )

        titreProduits.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        contenu.addView(titreProduits)

        afficherListeProduits(categorie)
    }

    private fun afficherListeProduits(
        categorie: String = "Tous"
    ) {

        if (produits.isEmpty()) {

            val vide = texte(
                "Aucun produit publié pour le moment.",
                18f,
                grisTexte
            )

            vide.gravity = Gravity.CENTER
            vide.setPadding(10, 40, 10, 40)

            contenu.addView(vide)

            return
        }

        for (produit in produits) {

            if (
                categorie != "Tous" &&
                produit.categorie != categorie
            ) {
                continue
            }

            afficherCarteProduit(produit)
        }
    }

    private fun afficherCarteProduit(produit: Produit) {

        val carte = LinearLayout(this)
        carte.orientation = LinearLayout.VERTICAL
        carte.setBackgroundColor(Color.rgb(248, 248, 248))
        carte.setPadding(8, 8, 8, 8)

        val image = ImageView(this)

        if (produit.imageUri.isNotEmpty()) {

            try {
                image.setImageURI(
                    Uri.parse(produit.imageUri)
                )
            } catch (_: Exception) {
                image.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }

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
                250
            )
        )

        val nom = texte(
            produit.nom,
            23f,
            Color.DKGRAY
        )

        nom.gravity = Gravity.CENTER
        nom.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        carte.addView(nom)

        val prix = texte(
            "${produit.prix} FCFA",
            21f,
            vert
        )

        prix.gravity = Gravity.CENTER
        carte.addView(prix)

        val stock = texte(
            "Stock : ${produit.stock}",
            15f,
            grisTexte
        )

        stock.gravity = Gravity.CENTER
        carte.addView(stock)

        val vendeur = texte(
            "Vendeur : ${produit.vendeur}",
            14f,
            grisTexte
        )

        vendeur.gravity = Gravity.CENTER
        carte.addView(vendeur)

        val voir = Button(this)
        voir.text = "VOIR LE PRODUIT"

        voir.setOnClickListener {
            detailProduit(produit)
        }

        carte.addView(voir)

        val partager = Button(this)
        partager.text = "↗ PARTAGER"

        partager.setOnClickListener {
            partagerProduit(produit)
        }

        carte.addView(partager)

        val espace = Space(this)

        contenu.addView(carte)

        contenu.addView(
            espace,
            LinearLayout.LayoutParams(
                1,
                12
            )
        )
    }

    // =========================
    // AJOUTER PRODUIT
    // =========================

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
        stock.hint = "Stock disponible"
        stock.inputType = 2

        val description = EditText(this)
        description.hint = "Description du produit"
        description.minLines = 3

        val categorie = EditText(this)
        categorie.hint =
            "Catégorie : Santé, Beauté..."

        val image = Button(this)
        image.text = "📷 Choisir la photo du produit"

        var imageChoisie = ""

        image.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_OPEN_DOCUMENT
            )

            intent.type = "image/*"
            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            )

            startActivityForResult(
                intent,
                1001
            )
        }

        layout.addView(nom)
        layout.addView(prix)
        layout.addView(stock)
        layout.addView(categorie)
        layout.addView(description)
        layout.addView(image)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un produit")
            .setView(layout)
            .setPositiveButton("PUBLIER") { _, _ ->

                if (nom.text.toString().trim().isEmpty()) {
                    toast("Veuillez entrer le nom du produit.")
                    return@setPositiveButton
                }

                val produit = Produit(
                    nom = nom.text.toString(),
                    prix = prix.text.toString()
                        .ifEmpty { "0" },
                    description =
                        description.text.toString(),
                    categorie =
                        categorie.text.toString()
                            .ifEmpty { "Autres" },
                    stock =
                        stock.text.toString()
                            .toIntOrNull() ?: 0,
                    imageUri = imageChoisie,
                    vendeur =
                        if (entreprise.isNotEmpty())
                            entreprise
                        else
                            "Vie Espoir Marketing"
                )

                produits.add(0, produit)

                sauvegarderDonnees()

                toast(
                    "Produit publié avec succès !"
                )

                afficherProduits()
            }
            .setNegativeButton("ANNULER", null)
            .show()
    }

    // =========================
    // DÉTAIL PRODUIT
    // =========================

    private fun detailProduit(produit: Produit) {

        val message = """
            ${produit.nom}

            Prix : ${produit.prix} FCFA

            Catégorie :
            ${produit.categorie}

            Stock disponible :
            ${produit.stock}

            Vendeur :
            ${produit.vendeur}

            ${produit.description}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle(produit.nom)
            .setMessage(message)
            .setPositiveButton("AJOUTER AU PANIER") { _, _ ->

                if (produit.stock > 0) {
                    panier.add(produit)
                    toast("Produit ajouté au panier.")
                } else {
                    toast("Produit en rupture de stock.")
                }
            }
            .setNeutralButton("PARTAGER") { _, _ ->
                partagerProduit(produit)
            }
            .setNegativeButton("FERMER", null)
            .show()
    }

    // =========================
    // PANIER
    // =========================

    private fun afficherPanier() {

        vider()

        titre.text = "PANIER"

        if (panier.isEmpty()) {

            contenu.addView(
                texte(
                    "Votre panier est vide.",
                    20f,
                    grisTexte
                )
            )

            return
        }

        var total = 0

        for (produit in panier) {

            val bloc = LinearLayout(this)
            bloc.orientation =
                LinearLayout.VERTICAL

            bloc.setPadding(
                10,
                10,
                10,
                10
            )

            bloc.addView(
                texte(
                    produit.nom,
                    20f,
                    Color.DKGRAY
                )
            )

            bloc.addView(
                texte(
                    "${produit.prix} FCFA",
                    18f,
                    vert
                )
            )

            total += produit.prix
                .replace(" ", "")
                .toIntOrNull() ?: 0

            contenu.addView(bloc)
        }

        contenu.addView(
            texte(
                "TOTAL : $total FCFA",
                23f,
                vert
            )
        )

        val commander = Button(this)
        commander.text = "PASSER LA COMMANDE"

        commander.setOnClickListener {

            toast(
                "Commande enregistrée. Le vendeur pourra vous contacter."
            )
        }

        contenu.addView(commander)
    }

    // =========================
    // COMPTE VENDEUR
    // =========================

    private fun afficherCompte() {

        vider()

        titre.text = "MON COMPTE"

        val logo = ImageView(this)

        if (logoUri.isNotEmpty()) {
            logo.setImageURI(
                Uri.parse(logoUri)
            )
        } else {
            logo.setImageResource(
                android.R.drawable.ic_menu_myplaces
            )
        }

        logo.scaleType =
            ImageView.ScaleType.CENTER_INSIDE

        contenu.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
            )
        )

        contenu.addView(
            texte(
                entreprise,
                26f,
                vert
            )
        )

        contenu.addView(
            texte(
                if (nomVendeur.isEmpty())
                    "Vendeur non renseigné"
                else
                    nomVendeur,
                18f,
                grisTexte
            )
        )

        contenu.addView(
            texte(
                if (telephone.isEmpty())
                    "Téléphone non renseigné"
                else
                    telephone,
                18f,
                grisTexte
            )
        )

        val modifier = Button(this)
        modifier.text = "✏️ MODIFIER MON COMPTE"

        modifier.setOnClickListener {
            modifierCompte()
        }

        contenu.addView(modifier)

        val ajouter = Button(this)
        ajouter.text = "＋ AJOUTER UN PRODUIT"

        ajouter.setOnClickListener {
            ajouterProduit()
        }

        contenu.addView(ajouter)

        val mesProduits = Button(this)
        mesProduits.text = "🛍️ MES PRODUITS"

        mesProduits.setOnClickListener {
            afficherProduits()
        }

        contenu.addView(mesProduits)

        val partagerApp = Button(this)
        partagerApp.text =
            "↗ PARTAGER VIE ESPOIR MARKETING"

        partagerApp.setOnClickListener {
            partagerApplication()
        }

        contenu.addView(partagerApp)
    }

    // =========================
    // MODIFIER COMPTE
    // =========================

    private fun modifierCompte() {

        val layout = LinearLayout(this)
        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            30,
            10,
            30,
            10
        )

        val nom = EditText(this)
        nom.hint = "Nom du vendeur"
        nom.setText(nomVendeur)

        val entrepriseInput = EditText(this)
        entrepriseInput.hint =
            "Nom de l'entreprise"
        entrepriseInput.setText(entreprise)

        val tel = EditText(this)
        tel.hint = "Téléphone"
        tel.setText(telephone)

        val logo = Button(this)
        logo.text = "🖼️ Choisir le logo"

        logo.setOnClickListener {

            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT)

            intent.type = "image/*"
            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            )

            startActivityForResult(
                intent,
                1002
            )
        }

        layout.addView(nom)
        layout.addView(entrepriseInput)
        layout.addView(tel)
        layout.addView(logo)

        AlertDialog.Builder(this)
            .setTitle("Mon compte vendeur")
            .setView(layout)
            .setPositiveButton("ENREGISTRER") { _, _ ->

                nomVendeur =
                    nom.text.toString()

                entreprise =
                    entrepriseInput.text
                        .toString()
                        .ifEmpty {
                            "Vie Espoir Marketing"
                        }

                telephone =
                    tel.text.toString()

                sauvegarderDonnees()

                afficherCompte()

                toast(
                    "Compte mis à jour."
                )
            }
            .setNegativeButton(
                "ANNULER",
                null
            )
            .show()
    }

    // =========================
    // PARTAGE PRODUIT
    // =========================

    private fun partagerProduit(
        produit: Produit
    ) {

        val texte = """
            🛍️ ${produit.nom}

            💰 Prix : ${produit.prix} FCFA

            📦 Disponible

            🏢 Vendeur :
            ${produit.vendeur}

            📝 ${produit.description}

            Découvrez ce produit sur
            Vie Espoir Marketing.
        """.trimIndent()

        val intent =
            Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            texte
        )

        startActivity(
            Intent.createChooser(
                intent,
                "Partager le produit"
            )
        )
    }

    // =========================
    // PARTAGER APPLICATION
    // =========================

    private fun partagerApplication() {

        val texte = """
            🛍️ VIE ESPOIR MARKETING

            Découvrez nos produits,
            commandez et achetez facilement.

            Téléchargez l'application :
            [LIEN DE L'APPLICATION À AJOUTER]
        """.trimIndent()

        val intent =
            Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"

        intent.putExtra(
            Intent.EXTRA_TEXT,
            texte
        )

        startActivity(
            Intent.createChooser(
                intent,
                "Partager l'application"
            )
        )
    }

    // =========================
    // MENU
    // =========================

    private fun ouvrirMenu() {

        val choix = arrayOf(
            "🏠 Accueil",
            "🛍️ Produits",
            "➕ Ajouter un produit",
            "👤 Mon compte",
            "🛒 Mon panier",
            "↗ Partager l'application"
        )

        AlertDialog.Builder(this)
            .setTitle("Vie Espoir Marketing")
            .setItems(choix) { _, position ->

                when (position) {

                    0 -> afficherAccueil()

                    1 -> afficherProduits()

                    2 -> ajouterProduit()

                    3 -> afficherCompte()

                    4 -> afficherPanier()

                    5 -> partagerApplication()
                }
            }
            .show()
    }

    // =========================
    // STOCKAGE
    // =========================

    private fun sauvegarderDonnees() {

        val prefs =
            getSharedPreferences(
                "vie_espoir",
                MODE_PRIVATE
            )

        val editor = prefs.edit()

        editor.putString(
            "entreprise",
            entreprise
        )

        editor.putString(
            "vendeur",
            nomVendeur
        )

        editor.putString(
            "telephone",
            telephone
        )

        editor.putString(
            "logo",
            logoUri
        )

        editor.putInt(
            "nombre_produits",
            produits.size
        )

        for (i in produits.indices) {

            val p = produits[i]

            editor.putString(
                "p_${i}_nom",
                p.nom
            )

            editor.putString(
                "p_${i}_prix",
                p.prix
            )

            editor.putString(
                "p_${i}_description",
                p.description
            )

            editor.putString(
                "p_${i}_categorie",
                p.categorie
            )

            editor.putInt(
                "p_${i}_stock",
                p.stock
            )

            editor.putString(
                "p_${i}_image",
                p.imageUri
            )

            editor.putString(
                "p_${i}_vendeur",
                p.vendeur
            )
        }

        editor.apply()
    }

    private fun chargerDonnees() {

        val prefs =
            getSharedPreferences(
                "vie_espoir",
                MODE_PRIVATE
            )

        entreprise =
            prefs.getString(
                "entreprise",
                "Vie Espoir Marketing"
            ) ?: "Vie Espoir Marketing"

        nomVendeur =
            prefs.getString(
                "vendeur",
                ""
            ) ?: ""

        telephone =
            prefs.getString(
                "telephone",
                ""
            ) ?: ""

        logoUri =
            prefs.getString(
                "logo",
                ""
            ) ?: ""

        produits.clear()

        val nombre =
            prefs.getInt(
                "nombre_produits",
                0
            )

        for (i in 0 until nombre) {

            produits.add(
                Produit(
                    nom =
                        prefs.getString(
                            "p_${i}_nom",
                            ""
                        ) ?: "",

                    prix =
                        prefs.getString(
                            "p_${i}_prix",
                            "0"
                        ) ?: "0",

                    description =
                        prefs.getString(
                            "p_${i}_description",
                            ""
                        ) ?: "",

                    categorie =
                        prefs.getString(
                            "p_${i}_categorie",
                            "Autres"
                        ) ?: "Autres",

                    stock =
                        prefs.getInt(
                            "p_${i}_stock",
                            0
                        ),

                    imageUri =
                        prefs.getString(
                            "p_${i}_image",
                            ""
                        ) ?: "",

                    vendeur =
                        prefs.getString(
                            "p_${i}_vendeur",
                            entreprise
                        ) ?: entreprise
                )
            )
        }

        // Produits de départ
        if (produits.isEmpty()) {

            produits.add(
                Produit(
                    "M7",
                    "15000",
                    "Produit M7",
                    "Santé",
                    10
                )
            )

            produits.add(
                Produit(
                    "Women 7",
                    "15000",
                    "Produit Women 7",
                    "Santé",
                    10
                )
            )

            produits.add(
                Produit(
                    "Super7",
                    "20000",
                    "Produit Super7",
                    "Santé",
                    10
                )
            )

            produits.add(
                Produit(
                    "Timoc",
                    "15000",
                    "Produit Timoc",
                    "Alimentation",
                    10
                )
            )

            sauvegarderDonnees()
        }
    }

    // =========================
    // IMAGE
    // =========================

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

        if (
            resultCode == RESULT_OK &&
            data != null &&
            data.data != null
        ) {

            val uri = data.data!!

            try {

                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

            } catch (_: Exception) {
            }

            if (requestCode == 1002) {

                logoUri =
                    uri.toString()

                sauvegarderDonnees()

                afficherCompte()

                toast(
                    "Logo enregistré."
                )
            }

            if (requestCode == 1001) {

                toast(
                    "Photo du produit sélectionnée. Appuyez sur PUBLIER."
                )

                // La photo sélectionnée est conservée
                // pour le prochain ajout.
                getSharedPreferences(
                    "temp",
                    MODE_PRIVATE
                )
                    .edit()
                    .putString(
                        "photo_produit",
                        uri.toString()
                    )
                    .apply()
            }
        }
    }

    // =========================
    // OUTILS
    // =========================

    private fun vider() {
        contenu.removeAllViews()
    }

    private fun texte(
        valeur: String,
        taille: Float,
        couleur: Int
    ): TextView {

        val t = TextView(this)

        t.text = valeur
        t.textSize = taille
        t.setTextColor(couleur)
        t.setPadding(
            5,
            10,
            5,
            10
        )

        return t
    }

    private fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}