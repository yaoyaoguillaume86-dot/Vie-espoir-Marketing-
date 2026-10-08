package com.vieespoir.marketing

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class MainActivity : Activity() {

    data class Produit(
        val nom: String,
        val prix: String,
        val categorie: String,
        val image: String = ""
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<Produit>()
    private val clients = ArrayList<String>()

    private lateinit var zoneProduits: LinearLayout
    private lateinit var imagePreview: ImageView

    private var imageProduit: String = ""

    private val PREFS = "vie_espoir_data"
    private val PRODUCTS_KEY = "products"
    private val CART_KEY = "cart"
    private val CLIENTS_KEY = "clients"
    private val SELLER_NAME = "seller_name"
    private val SELLER_EMAIL = "seller_email"

    private val CAMERA_REQUEST = 100
    private val GALLERY_REQUEST = 101

    private val prefs by lazy {
        getSharedPreferences(PREFS, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerDonnees()
        afficherAccueil()
    }

    private fun layoutBase(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
            setBackgroundColor(
                Color.rgb(248, 249, 247)
            )
        }
    }

    private fun scroll(
        contenu: LinearLayout
    ): ScrollView {
        return ScrollView(this).apply {
            addView(contenu)
        }
    }

    private fun titre(
        texte: String
    ): TextView {
        return TextView(this).apply {
            text = texte
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(
                Color.rgb(20, 100, 50)
            )
            setPadding(10, 20, 10, 20)
        }
    }

    private fun bouton(
        texte: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = texte
            textSize = 15f

            setOnClickListener {
                action()
            }

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(5, 5, 5, 5)
                }
        }
    }

    private fun afficherAccueil() {

        val root = layoutBase()

        val header =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.HORIZONTAL
                gravity =
                    Gravity.CENTER_VERTICAL
            }

        val logo =
            TextView(this).apply {
                text = "🦁"
                textSize = 40f
            }

        val nom =
            TextView(this).apply {
                text = "VIE ESPOIR"
                textSize = 25f
                setTextColor(
                    Color.rgb(20, 100, 50)
                )
                setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        val menu =
            Button(this).apply {
                text = "☰"
                textSize = 22f

                setOnClickListener {
                    afficherMenu()
                }
            }

        header.addView(logo)
        header.addView(nom)
        header.addView(menu)

        root.addView(header)

        val recherche =
            EditText(this).apply {
                hint =
                    "🔎 Rechercher un produit"
                textSize = 16f
                setSingleLine(true)
                setPadding(
                    15,
                    10,
                    15,
                    10
                )
            }

        root.addView(recherche)

        root.addView(
            bouton("🔎 Rechercher") {
                afficherProduits(
                    recherche.text
                        .toString()
                        .trim()
                )
            }
        )

        root.addView(
            bouton("🛍 Voir les produits") {
                afficherProduits("")
            }
        )

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        zoneProduits =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
            }

        root.addView(zoneProduits)

        root.addView(titre("Navigation"))

        val nav =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.HORIZONTAL
                gravity =
                    Gravity.CENTER
            }

        nav.addView(
            bouton("🏠 Accueil") {
                afficherAccueil()
            }
        )

        nav.addView(
            bouton("🛍 Produits") {
                afficherProduits("")
            }
        )

        nav.addView(
            bouton("🛒 Panier") {
                afficherPanier()
            }
        )

        nav.addView(
            bouton("👤 Compte") {
                afficherCompte()
            }
        )

        root.addView(nav)

        setContentView(
            scroll(root)
        )

        afficherProduitsAccueil()
    }

    private fun afficherProduitsAccueil() {

        zoneProduits.removeAllViews()

        zoneProduits.addView(
            titre("🛍 Produits disponibles")
        )

        if (produits.isEmpty()) {

            zoneProduits.addView(
                TextView(this).apply {
                    text =
                        "Aucun produit disponible."
                    textSize = 18f
                    gravity =
                        Gravity.CENTER
                    setPadding(
                        10,
                        30,
                        10,
                        30
                    )
                }
            )

            return
        }

        for (produit in produits) {
            ajouterCarteProduit(
                zoneProduits,
                produit
            )
        }
    }

    private fun ajouterCarteProduit(
        parent: LinearLayout,
        produit: Produit
    ) {

        val carte =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
                setPadding(
                    15,
                    15,
                    15,
                    15
                )
                setBackgroundColor(
                    Color.WHITE
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(
                            5,
                            8,
                            5,
                            8
                        )
                    }
            }

        if (produit.image.isNotEmpty()) {

            try {

                val image =
                    ImageView(this).apply {

                        layoutParams =
                            LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                280
                            )

                        scaleType =
                            ImageView.ScaleType.CENTER_CROP

                        setImageURI(
                            Uri.parse(
                                produit.image
                            )
                        )
                    }

                carte.addView(image)

            } catch (_: Exception) {
            }
        }

        carte.addView(
            TextView(this).apply {
                text = produit.nom
                textSize = 20f
                setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
                )
            }
        )

        carte.addView(
            TextView(this).apply {
                text =
                    "${produit.prix} FCFA"
                textSize = 18f
                setTextColor(
                    Color.rgb(20, 100, 50)
                )
            }
        )

        carte.addView(
            TextView(this).apply {
                text =
                    "Catégorie : ${produit.categorie}"
                textSize = 15f
            }
        )

        carte.addView(
            bouton("🛒 Ajouter au panier") {

                panier.add(produit)

                sauvegarderPanier()

                Toast.makeText(
                    this,
                    "Produit ajouté au panier",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        parent.addView(carte)
    }    private fun afficherProduits(
        rechercheTexte: String
    ) {

        val root = layoutBase()

        root.addView(
            titre("🛍 Tous les produits")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val recherche =
            EditText(this).apply {
                hint =
                    "🔎 Rechercher..."
                setSingleLine(true)
                setText(rechercheTexte)
            }

        root.addView(recherche)

        val liste =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
            }

        root.addView(liste)

        root.addView(
            bouton("🔎 Rechercher") {

                val mot =
                    recherche.text
                        .toString()
                        .trim()

                afficherProduits(mot)
            }
        )

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        val mot =
            rechercheTexte
                .lowercase(Locale.getDefault())

        var nombre = 0

        for (produit in produits) {

            val nom =
                produit.nom
                    .lowercase(Locale.getDefault())

            val cat =
                produit.categorie
                    .lowercase(Locale.getDefault())

            if (
                mot.isEmpty() ||
                nom.contains(mot) ||
                cat.contains(mot)
            ) {

                ajouterCarteProduit(
                    liste,
                    produit
                )

                nombre++
            }
        }

        if (nombre == 0) {

            liste.addView(
                TextView(this).apply {
                    text =
                        "Aucun produit trouvé."
                    textSize = 18f
                    gravity =
                        Gravity.CENTER
                    setPadding(
                        10,
                        30,
                        10,
                        30
                    )
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }

    private fun afficherMenu() {

        val choix = arrayOf(
            "🏠 Accueil",
            "🛍 Produits",
            "➕ Ajouter un produit",
            "👨‍💼 Espace vendeur",
            "🛒 Panier",
            "👥 Clients",
            "📦 Stock",
            "👤 Compte"
        )

        AlertDialog.Builder(this)
            .setTitle(
                "☰ Menu VIE ESPOIR"
            )
            .setItems(
                choix
            ) { _, position ->

                when (position) {

                    0 ->
                        afficherAccueil()

                    1 ->
                        afficherProduits("")

                    2 ->
                        afficherAjouterProduit()

                    3 ->
                        afficherEspaceVendeur()

                    4 ->
                        afficherPanier()

                    5 ->
                        afficherClients()

                    6 ->
                        afficherStock()

                    7 ->
                        afficherCompte()
                }
            }
            .show()
    }

    private fun afficherAjouterProduit() {

        val root = layoutBase()

        root.addView(
            titre("➕ Ajouter un produit")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val nom =
            EditText(this).apply {
                hint =
                    "Nom du produit"
                textSize = 16f
            }

        val prix =
            EditText(this).apply {
                hint =
                    "Prix en FCFA"
                textSize = 16f
                inputType =
                    android.text.InputType
                        .TYPE_CLASS_NUMBER
            }

        val categorie =
            EditText(this).apply {
                hint =
                    "Catégorie"
                textSize = 16f
            }

        root.addView(nom)
        root.addView(prix)
        root.addView(categorie)

        imagePreview =
            ImageView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        300
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_CROP

                visibility =
                    View.GONE
            }

        root.addView(imagePreview)

        root.addView(
            bouton("📷 Prendre une photo") {
                ouvrirCamera()
            }
        )

        root.addView(
            bouton("🖼 Choisir dans la galerie") {
                ouvrirGalerie()
            }
        )

        root.addView(
            bouton("💾 Enregistrer le produit") {

                val nomProduit =
                    nom.text
                        .toString()
                        .trim()

                val prixProduit =
                    prix.text
                        .toString()
                        .trim()

                val categorieProduit =
                    categorie.text
                        .toString()
                        .trim()

                if (
                    nomProduit.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Entrez le nom du produit",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                if (
                    prixProduit.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Entrez le prix",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                val produit =
                    Produit(
                        nom =
                            nomProduit,
                        prix =
                            prixProduit,
                        categorie =
                            if (
                                categorieProduit.isEmpty()
                            ) {
                                "Autres"
                            } else {
                                categorieProduit
                            },
                        image =
                            imageProduit
                    )

                produits.add(produit)

                sauvegarderProduits()

                imageProduit = ""

                Toast.makeText(
                    this,
                    "Produit ajouté avec succès",
                    Toast.LENGTH_LONG
                ).show()

                afficherAccueil()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    private fun ouvrirCamera() {

        if (
            android.os.Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ),
                CAMERA_REQUEST
            )

            return
        }

        try {

            val intent =
                Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )

            startActivityForResult(
                intent,
                CAMERA_REQUEST
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Impossible d'ouvrir la caméra",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun ouvrirGalerie() {

        try {

            val intent =
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )

            startActivityForResult(
                intent,
                GALLERY_REQUEST
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Impossible d'ouvrir la galerie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == CAMERA_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {

            ouvrirCamera()
        }
    }

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
            resultCode != RESULT_OK ||
            data == null
        ) {
            return
        }

        if (
            requestCode == GALLERY_REQUEST
        ) {

            val uri =
                data.data

            if (uri != null) {

                imageProduit =
                    uri.toString()

                imagePreview.visibility =
                    View.VISIBLE

                imagePreview.setImageURI(
                    uri
                )
            }
        }

        if (
            requestCode == CAMERA_REQUEST
        ) {

            val bitmap =
                data.extras
                    ?.get("data") as? Bitmap

            if (bitmap != null) {

                imagePreview.visibility =
                    View.VISIBLE

                imagePreview.setImageBitmap(
                    bitmap
                )

                Toast.makeText(
                    this,
                    "Photo prise avec succès",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }    private fun afficherPanier() {

        val root = layoutBase()

        root.addView(
            titre("🛒 Mon panier")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        if (panier.isEmpty()) {

            root.addView(
                TextView(this).apply {
                    text = "Votre panier est vide."
                    textSize = 18f
                    gravity = Gravity.CENTER
                    setPadding(
                        10,
                        40,
                        10,
                        40
                    )
                }
            )

        } else {

            var total = 0

            for (produit in panier) {

                val bloc =
                    LinearLayout(this).apply {
                        orientation =
                            LinearLayout.VERTICAL
                        setPadding(
                            15,
                            15,
                            15,
                            15
                        )
                        setBackgroundColor(
                            Color.WHITE
                        )
                    }

                bloc.addView(
                    TextView(this).apply {
                        text =
                            produit.nom
                        textSize = 19f
                        setTypeface(
                            null,
                            android.graphics.Typeface.BOLD
                        )
                    }
                )

                bloc.addView(
                    TextView(this).apply {
                        text =
                            "${produit.prix} FCFA"
                        textSize = 17f
                    }
                )

                bloc.addView(
                    bouton("❌ Retirer") {

                        panier.remove(produit)

                        sauvegarderPanier()

                        afficherPanier()
                    }
                )

                root.addView(bloc)

                try {

                    total +=
                        produit.prix
                            .replace(" ", "")
                            .toInt()

                } catch (_: Exception) {
                }
            }

            root.addView(
                TextView(this).apply {

                    text =
                        "TOTAL : $total FCFA"

                    textSize = 22f

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    gravity = Gravity.CENTER

                    setTextColor(
                        Color.rgb(20, 100, 50)
                    )

                    setPadding(
                        10,
                        20,
                        10,
                        20
                    )
                }
            )

            root.addView(
                bouton("✅ Passer la commande") {

                    AlertDialog.Builder(this)
                        .setTitle(
                            "Confirmer la commande"
                        )
                        .setMessage(
                            "Voulez-vous confirmer cette commande ?"
                        )
                        .setNegativeButton(
                            "Annuler",
                            null
                        )
                        .setPositiveButton(
                            "Confirmer"
                        ) { _, _ ->

                            panier.clear()

                            sauvegarderPanier()

                            Toast.makeText(
                                this,
                                "Commande enregistrée avec succès",
                                Toast.LENGTH_LONG
                            ).show()

                            afficherAccueil()
                        }
                        .show()
                }
            )

            root.addView(
                bouton("🗑 Vider le panier") {

                    panier.clear()

                    sauvegarderPanier()

                    afficherPanier()
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }

    private fun afficherCompte() {

        val root = layoutBase()

        root.addView(
            titre("👤 Mon compte")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val nom =
            prefs.getString(
                SELLER_NAME,
                ""
            ) ?: ""

        val email =
            prefs.getString(
                SELLER_EMAIL,
                ""
            ) ?: ""

        if (nom.isEmpty()) {

            root.addView(
                TextView(this).apply {

                    text =
                        "Vous n'avez pas encore de compte vendeur."

                    textSize = 17f

                    gravity = Gravity.CENTER

                    setPadding(
                        10,
                        20,
                        10,
                        20
                    )
                }
            )

            root.addView(
                bouton("➕ Créer un compte") {
                    afficherCreationCompte()
                }
            )

            root.addView(
                bouton("🔐 Se connecter") {
                    afficherConnexion()
                }
            )

        } else {

            root.addView(
                TextView(this).apply {

                    text =
                        "Bienvenue $nom 👋"

                    textSize = 21f

                    gravity = Gravity.CENTER

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    setPadding(
                        10,
                        20,
                        10,
                        10
                    )
                }
            )

            root.addView(
                TextView(this).apply {

                    text =
                        "📧 $email"

                    textSize = 16f

                    gravity = Gravity.CENTER
                }
            )

            root.addView(
                bouton("👨‍💼 Espace vendeur") {
                    afficherEspaceVendeur()
                }
            )

            root.addView(
                bouton("➕ Ajouter un produit") {
                    afficherAjouterProduit()
                }
            )

            root.addView(
                bouton("📦 Mon stock") {
                    afficherStock()
                }
            )

            root.addView(
                bouton("🚪 Se déconnecter") {

                    prefs.edit()
                        .remove(SELLER_NAME)
                        .remove(SELLER_EMAIL)
                        .apply()

                    Toast.makeText(
                        this,
                        "Déconnexion réussie",
                        Toast.LENGTH_SHORT
                    ).show()

                    afficherCompte()
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }

    private fun afficherCreationCompte() {

        val root = layoutBase()

        root.addView(
            titre("➕ Créer mon compte vendeur")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherCompte()
            }
        )

        val nom =
            EditText(this).apply {
                hint =
                    "Nom complet"
                textSize = 16f
            }

        val email =
            EditText(this).apply {
                hint =
                    "Adresse e-mail"
                textSize = 16f
                inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

        root.addView(nom)
        root.addView(email)

        root.addView(
            bouton("✅ Créer le compte") {

                val nomTexte =
                    nom.text
                        .toString()
                        .trim()

                val emailTexte =
                    email.text
                        .toString()
                        .trim()

                if (nomTexte.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez votre nom",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                if (emailTexte.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez votre e-mail",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                prefs.edit()
                    .putString(
                        SELLER_NAME,
                        nomTexte
                    )
                    .putString(
                        SELLER_EMAIL,
                        emailTexte
                    )
                    .apply()

                Toast.makeText(
                    this,
                    "Compte créé avec succès",
                    Toast.LENGTH_LONG
                ).show()

                afficherCompte()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    private fun afficherConnexion() {

        val root = layoutBase()

        root.addView(
            titre("🔐 Connexion vendeur")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherCompte()
            }
        )

        val email =
            EditText(this).apply {
                hint =
                    "Votre e-mail"
                textSize = 16f
                inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

        root.addView(email)

        root.addView(
            bouton("🔓 Se connecter") {

                val emailEntre =
                    email.text
                        .toString()
                        .trim()

                val emailEnregistre =
                    prefs.getString(
                        SELLER_EMAIL,
                        ""
                    ) ?: ""

                if (emailEntre.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez votre e-mail",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                if (
                    emailEnregistre.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Aucun compte trouvé. Créez d'abord un compte.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@bouton
                }

                if (
                    emailEntre != emailEnregistre
                ) {

                    Toast.makeText(
                        this,
                        "E-mail incorrect",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                Toast.makeText(
                    this,
                    "Connexion réussie",
                    Toast.LENGTH_SHORT
                ).show()

                afficherEspaceVendeur()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    private fun afficherEspaceVendeur() {

        val root = layoutBase()

        root.addView(
            titre("👨‍💼 Espace vendeur")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val nom =
            prefs.getString(
                SELLER_NAME,
                ""
            ) ?: ""

        if (nom.isEmpty()) {

            root.addView(
                TextView(this).apply {

                    text =
                        "Vous devez créer un compte vendeur."

                    textSize = 17f

                    gravity = Gravity.CENTER

                    setPadding(
                        10,
                        20,
                        10,
                        20
                    )
                }
            )

            root.addView(
                bouton("👤 Créer mon compte") {
                    afficherCreationCompte()
                }
            )

        } else {

            root.addView(
                TextView(this).apply {

                    text =
                        "Bonjour $nom 👋"

                    textSize = 21f

                    gravity = Gravity.CENTER

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    setPadding(
                        10,
                        20,
                        10,
                        20
                    )
                }
            )

            root.addView(
                bouton("➕ Ajouter un produit") {
                    afficherAjouterProduit()
                }
            )

            root.addView(
                bouton("🛍 Mes produits") {
                    afficherProduits("")
                }
            )

            root.addView(
                bouton("📦 Mon stock") {
                    afficherStock()
                }
            )

            root.addView(
                bouton("👥 Mes clients") {
                    afficherClients()
                }
            )

            root.addView(
                bouton("🛒 Mes commandes") {

                    AlertDialog.Builder(this)
                        .setTitle("🛒 Commandes")
                        .setMessage(
                            "Aucune commande en attente."
                        )
                        .setPositiveButton(
                            "OK",
                            null
                        )
                        .show()
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }    private fun afficherClients() {

        val root = layoutBase()

        root.addView(
            titre("👥 Mes clients")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        if (clients.isEmpty()) {

            root.addView(
                TextView(this).apply {
                    text =
                        "Aucun client enregistré."
                    textSize = 18f
                    gravity = Gravity.CENTER
                    setPadding(
                        10,
                        30,
                        10,
                        30
                    )
                }
            )

        } else {

            for (client in clients) {

                root.addView(
                    TextView(this).apply {
                        text =
                            "👤 $client"
                        textSize = 17f
                        setPadding(
                            15,
                            15,
                            15,
                            15
                        )
                        setBackgroundColor(
                            Color.WHITE
                        )
                    }
                )
            }
        }

        root.addView(
            bouton("➕ Ajouter un client") {

                val champ =
                    EditText(this).apply {
                        hint =
                            "Nom du client"
                    }

                AlertDialog.Builder(this)
                    .setTitle(
                        "Ajouter un client"
                    )
                    .setView(champ)
                    .setNegativeButton(
                        "Annuler",
                        null
                    )
                    .setPositiveButton(
                        "Ajouter"
                    ) { _, _ ->

                        val nom =
                            champ.text
                                .toString()
                                .trim()

                        if (nom.isNotEmpty()) {

                            clients.add(nom)

                            sauvegarderClients()

                            afficherClients()
                        }
                    }
                    .show()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    private fun afficherStock() {

        val root = layoutBase()

        root.addView(
            titre("📦 Gestion du stock")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        root.addView(
            TextView(this).apply {

                text =
                    "Nombre de produits : ${produits.size}"

                textSize = 18f

                setPadding(
                    10,
                    20,
                    10,
                    20
                )
            }
        )

        if (produits.isEmpty()) {

            root.addView(
                TextView(this).apply {

                    text =
                        "Le stock est vide."

                    textSize = 17f

                    gravity =
                        Gravity.CENTER

                    setPadding(
                        10,
                        30,
                        10,
                        30
                    )
                }
            )

        } else {

            for (produit in produits) {

                val ligne =
                    LinearLayout(this).apply {

                        orientation =
                            LinearLayout.HORIZONTAL

                        gravity =
                            Gravity.CENTER_VERTICAL

                        setPadding(
                            10,
                            15,
                            10,
                            15
                        )

                        setBackgroundColor(
                            Color.WHITE
                        )
                    }

                val information =
                    TextView(this).apply {

                        text =
                            "${produit.nom}\n${produit.prix} FCFA"

                        textSize = 16f

                        layoutParams =
                            LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                    }

                ligne.addView(information)

                ligne.addView(
                    bouton("❌ Supprimer") {

                        AlertDialog.Builder(this)
                            .setTitle(
                                "Supprimer le produit"
                            )
                            .setMessage(
                                "Supprimer ${produit.nom} ?"
                            )
                            .setNegativeButton(
                                "Annuler",
                                null
                            )
                            .setPositiveButton(
                                "Supprimer"
                            ) { _, _ ->

                                produits.remove(
                                    produit
                                )

                                sauvegarderProduits()

                                afficherStock()
                            }
                            .show()
                    }
                )

                root.addView(ligne)
            }
        }

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    private fun sauvegarderProduits() {

        val tableau =
            JSONArray()

        for (produit in produits) {

            val objet =
                JSONObject()

            objet.put(
                "nom",
                produit.nom
            )

            objet.put(
                "prix",
                produit.prix
            )

            objet.put(
                "categorie",
                produit.categorie
            )

            objet.put(
                "image",
                produit.image
            )

            tableau.put(objet)
        }

        prefs.edit()
            .putString(
                PRODUCTS_KEY,
                tableau.toString()
            )
            .apply()
    }

    private fun sauvegarderPanier() {

        val tableau =
            JSONArray()

        for (produit in panier) {

            val objet =
                JSONObject()

            objet.put(
                "nom",
                produit.nom
            )

            objet.put(
                "prix",
                produit.prix
            )

            objet.put(
                "categorie",
                produit.categorie
            )

            objet.put(
                "image",
                produit.image
            )

            tableau.put(objet)
        }

        prefs.edit()
            .putString(
                CART_KEY,
                tableau.toString()
            )
            .apply()
    }

    private fun sauvegarderClients() {

        val tableau =
            JSONArray()

        for (client in clients) {
            tableau.put(client)
        }

        prefs.edit()
            .putString(
                CLIENTS_KEY,
                tableau.toString()
            )
            .apply()
    }

    private fun chargerDonnees() {

        produits.clear()
        panier.clear()
        clients.clear()

        val produitsJson =
            prefs.getString(
                PRODUCTS_KEY,
                null
            )

        if (
            !produitsJson.isNullOrEmpty()
        ) {

            try {

                val tableau =
                    JSONArray(produitsJson)

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(i)

                    produits.add(
                        Produit(
                            nom =
                                objet.optString(
                                    "nom"
                                ),
                            prix =
                                objet.optString(
                                    "prix"
                                ),
                            categorie =
                                objet.optString(
                                    "categorie"
                                ),
                            image =
                                objet.optString(
                                    "image"
                                )
                        )
                    )
                }

            } catch (_: Exception) {
            }
        }

        val panierJson =
            prefs.getString(
                CART_KEY,
                null
            )

        if (
            !panierJson.isNullOrEmpty()
        ) {

            try {

                val tableau =
                    JSONArray(panierJson)

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(i)

                    panier.add(
                        Produit(
                            nom =
                                objet.optString(
                                    "nom"
                                ),
                            prix =
                                objet.optString(
                                    "prix"
                                ),
                            categorie =
                                objet.optString(
                                    "categorie"
                                ),
                            image =
                                objet.optString(
                                    "image"
                                )
                        )
                    )
                }

            } catch (_: Exception) {
            }
        }

        val clientsJson =
            prefs.getString(
                CLIENTS_KEY,
                null
            )

        if (
            !clientsJson.isNullOrEmpty()
        ) {

            try {

                val tableau =
                    JSONArray(clientsJson)

                for (
                    i in 0 until tableau.length()
                ) {

                    clients.add(
                        tableau.getString(i)
                    )
                }

            } catch (_: Exception) {
            }
        }
    }
}