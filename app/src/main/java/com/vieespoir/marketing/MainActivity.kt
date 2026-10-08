package com.vieespoir.marketing

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

// =========================================================
// ÉCRAN DE BIENVENUE
// =========================================================

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setPadding(30, 30, 30, 30)
        }

        val bienvenue = TextView(this).apply {

            text =
                "BIENVENUE\nVIE ESPOIR MARKETING"

            textSize = 22f

            setTextColor(
                Color.rgb(20, 100, 50)
            )

            gravity = Gravity.CENTER

            typeface =
                Typeface.DEFAULT_BOLD

            setPadding(
                0,
                20,
                0,
                0
            )
        }

        layout.addView(bienvenue)

        setContentView(layout)

        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()

        }, 10000)
    }
}


// =========================================================
// APPLICATION PRINCIPALE
// =========================================================

class MainActivity : Activity() {

    // =========================================================
    // DONNÉES
    // =========================================================

    data class Produit(
        val id: String =
            System.currentTimeMillis().toString(),

        var nom: String,

        var prix: String,

        var categorie: String,

        var stock: Int = 0,

        var description: String = "",

        var image: String = ""
    )

    data class PanierItem(
        val produitId: String,

        val nom: String,

        val prix: Int,

        var quantite: Int,

        val image: String = ""
    )

    data class Commande(
        val id: String,

        val client: String,

        val telephone: String,

        val produit: String,

        val montant: String,

        var statut: String = "Nouvelle"
    )

    private val produits =
        ArrayList<Produit>()

    private val panier =
        ArrayList<PanierItem>()

    private val clients =
        ArrayList<String>()

    private val commandes =
        ArrayList<Commande>()

    private lateinit var zoneProduits: LinearLayout

    private lateinit var imagePreview: ImageView

    private var imageProduit = ""

    // =========================================================
    // STOCKAGE
    // =========================================================

    private val PREFS =
        "vie_espoir_data"

    private val PRODUCTS_KEY =
        "products"

    private val CART_KEY =
        "cart"

    private val CLIENTS_KEY =
        "clients"

    private val ORDERS_KEY =
        "orders"

    private val SELLER_NAME =
        "seller_name"

    private val SELLER_EMAIL =
        "seller_email"

    private val SELLER_PHONE =
        "seller_phone"

    private val SELLER_WHATSAPP =
        "seller_whatsapp"

    private val SELLER_VALIDATED =
        "seller_validated"

    private val SELLER_PACK =
        "seller_pack"

    private val CAMERA_REQUEST = 100

    private val GALLERY_REQUEST = 101

    private val ORANGE_WAVE =
        "0710405688"

    private val MTN =
        "0546420566"

    private val prefs by lazy {

        getSharedPreferences(
            PREFS,
            MODE_PRIVATE
        )
    }

    // =========================================================
    // COULEURS
    // =========================================================

    private val VERT =
        Color.rgb(20, 100, 50)

    private val VERT_FONCE =
        Color.rgb(8, 65, 32)

    private val OR =
        Color.rgb(218, 165, 32)

    private val OR_CLAIR =
        Color.rgb(255, 193, 7)

    private val BLANC =
        Color.WHITE

    private val FOND =
        Color.rgb(247, 249, 246)

    private val GRIS =
        Color.rgb(235, 237, 235)

    private val TEXTE =
        Color.rgb(35, 35, 35)

    // =========================================================
    // DÉMARRAGE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        window.statusBarColor =
            VERT_FONCE

        window.navigationBarColor =
            Color.WHITE

        chargerDonnees()

        afficherAccueil()
    }

    // =========================================================
    // OUTILS INTERFACE
    // =========================================================

    private fun layoutBase():
            LinearLayout {

        return LinearLayout(this).apply {

            orientation =
                LinearLayout.VERTICAL

            setPadding(
                12,
                8,
                12,
                20
            )

            setBackgroundColor(
                FOND
            )
        }
    }

    private fun scroll(
        contenu: LinearLayout
    ): ScrollView {

        return ScrollView(this).apply {

            isFillViewport = true

            addView(contenu)
        }
    }

    private fun titre(
        texte: String
    ): TextView {

        return TextView(this).apply {

            text = texte

            textSize = 23f

            gravity =
                Gravity.CENTER

            setTextColor(
                VERT
            )

            typeface =
                Typeface.DEFAULT_BOLD

            setPadding(
                8,
                18,
                8,
                18
            )
        }
    }

    private fun bouton(
        texte: String,
        action: () -> Unit
    ): Button {

        return Button(this).apply {

            text = texte

            textSize = 15f

            setTextColor(
                TEXTE
            )

            setBackgroundColor(
                GRIS
            )

            typeface =
                Typeface.DEFAULT_BOLD

            setOnClickListener {
                action()
            }

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {

                    setMargins(
                        4,
                        5,
                        4,
                        5
                    )
                }
        }
    }

    private fun boutonVert(
        texte: String,
        action: () -> Unit
    ): Button {

        return Button(this).apply {

            text = texte

            textSize = 15f

            setTextColor(
                Color.WHITE
            )

            setBackgroundColor(
                VERT
            )

            typeface =
                Typeface.DEFAULT_BOLD

            setOnClickListener {
                action()
            }

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {

                    setMargins(
                        4,
                        5,
                        4,
                        5
                    )
                }
        }
    }

    private fun texte(
        contenu: String,
        taille: Float = 16f,
        couleur: Int = TEXTE
    ): TextView {

        return TextView(this).apply {

            text = contenu

            textSize = taille

            setTextColor(
                couleur
            )

            setPadding(
                8,
                6,
                8,
                6
            )
        }
    }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil() {

        val root =
            layoutBase()

        val header =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    8,
                    10,
                    8,
                    10
                )

                setBackgroundColor(
                    Color.WHITE
                )
            }

        val logo =
            TextView(this).apply {

                text = "🦁"

                textSize = 42f

                gravity =
                    Gravity.CENTER
            }

        val nom =
            TextView(this).apply {

                text =
                    "VIE ESPOIR\nMARKETING"

                textSize = 20f

                setTextColor(
                    VERT
                )

                typeface =
                    Typeface.DEFAULT_BOLD

                gravity =
                    Gravity.CENTER_VERTICAL

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

                textSize = 25f

                setTextColor(
                    VERT
                )

                setBackgroundColor(
                    GRIS
                )

                setOnClickListener {
                    afficherMenu()
                }
            }

        header.addView(logo)

        header.addView(nom)

        header.addView(menu)

        root.addView(header)

        root.addView(
            texte(
                "Ensemble pour un meilleur avenir",
                14f,
                VERT
            ).apply {

                gravity =
                    Gravity.CENTER

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.ITALIC
                    )
            }
        )

        val recherche =
            EditText(this).apply {

                hint =
                    "🔎 Rechercher un produit"

                textSize = 16f

                setSingleLine(true)

                setPadding(
                    16,
                    12,
                    16,
                    12
                )

                setBackgroundColor(
                    Color.WHITE
                )
            }

        root.addView(recherche)

        root.addView(
            boutonVert(
                "🔎 Rechercher"
            ) {

                afficherProduits(
                    recherche.text
                        .toString()
                        .trim()
                )
            }
        )

        root.addView(
            titre(
                "🛍️ Produits disponibles"
            )
        )

        zoneProduits =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL
            }

        root.addView(
            zoneProduits
        )

        root.addView(
            bouton(
                "🛒 Mon panier (${panier.size})"
            ) {

                afficherPanier()
            }
        )

        root.addView(
            texte(
                "VIE ESPOIR MARKETING\nProduits • Commandes • Vente",
                13f,
                Color.GRAY
            ).apply {

                gravity =
                    Gravity.CENTER

                setPadding(
                    8,
                    25,
                    8,
                    20
                )
            }
        )

        setContentView(
            scroll(root)
        )

        afficherProduitsAccueil()
    }

    // =========================================================
    // PRODUITS ACCUEIL
    // =========================================================

    private fun afficherProduitsAccueil() {

        zoneProduits.removeAllViews()

        if (produits.isEmpty()) {

            zoneProduits.addView(
                texte(
                    "Aucun produit disponible pour le moment.",
                    17f
                ).apply {

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

    // =========================================================
    // CARTE PRODUIT
    // =========================================================

    private fun ajouterCarteProduit(
        parent: LinearLayout,
        produit: Produit
    ) {

        val carte =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    12,
                    12,
                    12,
                    12
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
                            3,
                            8,
                            3,
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
                                260
                            )

                        scaleType =
                            ImageView.ScaleType.CENTER_CROP

                        setImageURI(
                            Uri.parse(
                                produit.image
                            )
                        )
                    }

                carte.addView(
                    image
                )

            } catch (_: Exception) {
            }
        }

        carte.addView(
            texte(
                produit.nom,
                21f,
                VERT
            ).apply {

                typeface =
                    Typeface.DEFAULT_BOLD
            }
        )

        val prix =
            produit.prix
                .replace(" ", "")
                .toIntOrNull() ?: 0

        carte.addView(
            texte(
                "${formaterNombre(prix)} FCFA",
                19f,
                VERT
            ).apply {

                typeface =
                    Typeface.DEFAULT_BOLD
            }
        )

        carte.addView(
            texte(
                "Catégorie : ${produit.categorie}",
                14f,
                Color.GRAY
            )
        )

        val stockTexte =
            if (produit.stock > 0) {

                "📦 Stock : ${produit.stock}"

            } else {

                "⚠️ Stock non disponible"
            }

        carte.addView(
            texte(
                stockTexte,
                14f,
                if (produit.stock > 0)
                    VERT
                else
                    Color.RED
            )
        )

        if (
            produit.description.isNotEmpty()
        ) {

            carte.addView(
                texte(
                    produit.description,
                    14f,
                    Color.DKGRAY
                )
            )
        }

        carte.addView(
            boutonVert(
                "🛒 Ajouter au panier"
            ) {

                if (produit.stock <= 0) {

                    Toast.makeText(
                        this,
                        "Produit indisponible",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@boutonVert
                }

                ajouterAuPanier(
                    produit
                )
            }
        )

        carte.addView(
            bouton(
                "📤 Partager"
            ) {

                partagerProduit(
                    produit
                )
            }
        )

        carte.addView(
            bouton(
                "✏️ Modifier / gérer"
            ) {

                afficherModifierProduit(
                    produit
                )
            }
        )

        parent.addView(
            carte
        )
    }

    // =========================================================
    // AJOUT PANIER
    // =========================================================

    private fun ajouterAuPanier(
        produit: Produit
    ) {

        val existant =
            panier.find {
                it.produitId ==
                    produit.id
            }

        if (existant != null) {

            if (
                existant.quantite <
                produit.stock
            ) {

                existant.quantite++
            }

        } else {

            panier.add(
                PanierItem(
                    produitId =
                        produit.id,

                    nom =
                        produit.nom,

                    prix =
                        produit.prix
                            .replace(" ", "")
                            .toIntOrNull()
                            ?: 0,

                    quantite = 1,

                    image =
                        produit.image
                )
            )
        }

        sauvegarderPanier()

        Toast.makeText(
            this,
            "Produit ajouté au panier",
            Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // RECHERCHE / PRODUITS
    // =========================================================

    private fun afficherProduits(
        rechercheTexte: String
    ) {

        val root =
            layoutBase()

        root.addView(
            titre("🛍️ Produits")
        )

        root.addView(
            bouton("⬅ Accueil") {
                afficherAccueil()
            }
        )

        val recherche =
            EditText(this).apply {

                hint =
                    "🔎 Rechercher..."

                setSingleLine(true)

                setText(
                    rechercheTexte
                )
            }

        root.addView(
            recherche
        )

        root.addView(
            boutonVert(
                "🔎 Rechercher"
            ) {

                afficherProduits(
                    recherche.text
                        .toString()
                        .trim()
                )
            }
        )

        val liste =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL
            }

        root.addView(
            liste
        )

        val mot =
            rechercheTexte.lowercase(
                Locale.getDefault()
            )

        var nombre = 0

        for (produit in produits) {

            val nom =
                produit.nom.lowercase(
                    Locale.getDefault()
                )

            val categorie =
                produit.categorie.lowercase(
                    Locale.getDefault()
                )

            if (
                mot.isEmpty() ||
                nom.contains(mot) ||
                categorie.contains(mot)
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
                texte(
                    "Aucun produit trouvé.",
                    18f
                ).apply {

                    gravity =
                        Gravity.CENTER

                    setPadding(
                        10,
                        35,
                        10,
                        35
                    )
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // AJOUT PRODUIT
    // =========================================================

    private fun afficherAjouterProduit() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "➕ Ajouter un produit"
            )
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

                inputType =
                    InputType.TYPE_CLASS_NUMBER

                textSize = 16f
            }

        val categorie =
            EditText(this).apply {

                hint =
                    "Catégorie"

                textSize = 16f
            }

        val stock =
            EditText(this).apply {

                hint =
                    "Quantité en stock"

                inputType =
                    InputType.TYPE_CLASS_NUMBER

                textSize = 16f
            }

        val description =
            EditText(this).apply {

                hint =
                    "Description du produit"

                textSize = 16f

                minLines = 3

                gravity =
                    Gravity.TOP
            }

        root.addView(nom)

        root.addView(prix)

        root.addView(categorie)

        root.addView(stock)

        root.addView(description)

        imagePreview =
            ImageView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        280
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_CROP

                visibility =
                    View.GONE
            }

        root.addView(
            imagePreview
        )

        root.addView(
            bouton(
                "📷 Prendre une photo"
            ) {
                ouvrirCamera()
            }
        )

        root.addView(
            bouton(
                "🖼️ Choisir une photo"
            ) {
                ouvrirGalerie()
            }
        )

        root.addView(
            boutonVert(
                "💾 Enregistrer le produit"
            ) {

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

                val stockProduit =
                    stock.text
                        .toString()
                        .trim()
                        .toIntOrNull()
                        ?: 0

                val descriptionProduit =
                    description.text
                        .toString()
                        .trim()

                if (
                    nomProduit.isEmpty()
                ) {

                    toast(
                        "Entrez le nom du produit"
                    )

                    return@boutonVert
                }

                if (
                    prixProduit.isEmpty()
                ) {

                    toast(
                        "Entrez le prix"
                    )

                    return@boutonVert
                }

                produits.add(
                    Produit(
                        nom =
                            nomProduit,

                        prix =
                            prixProduit,

                        categorie =
                            if (
                                categorieProduit.isEmpty()
                            )
                                "Autres"
                            else
                                categorieProduit,

                        stock =
                            stockProduit,

                        description =
                            descriptionProduit,

                        image =
                            imageProduit
                    )
                )

                sauvegarderProduits()

                imageProduit = ""

                toast(
                    "Produit ajouté avec succès"
                )

                afficherAccueil()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // MODIFIER PRODUIT
    // =========================================================

    private fun afficherModifierProduit(
        produit: Produit
    ) {

        val root =
            layoutBase()

        root.addView(
            titre(
                "✏️ Modifier le produit"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val nom =
            EditText(this).apply {

                hint = "Nom"

                setText(
                    produit.nom
                )
            }

        val prix =
            EditText(this).apply {

                hint = "Prix"

                setText(
                    produit.prix
                )

                inputType =
                    InputType.TYPE_CLASS_NUMBER
            }

        val categorie =
            EditText(this).apply {

                hint =
                    "Catégorie"

                setText(
                    produit.categorie
                )
            }

        val stock =
            EditText(this).apply {

                hint = "Stock"

                setText(
                    produit.stock.toString()
                )

                inputType =
                    InputType.TYPE_CLASS_NUMBER
            }

        val description =
            EditText(this).apply {

                hint =
                    "Description"

                setText(
                    produit.description
                )

                minLines = 3

                gravity =
                    Gravity.TOP
            }

        root.addView(nom)

        root.addView(prix)

        root.addView(categorie)

        root.addView(stock)

        root.addView(description)

        root.addView(
            boutonVert(
                "💾 Enregistrer les modifications"
            ) {

                produit.nom =
                    nom.text
                        .toString()
                        .trim()

                produit.prix =
                    prix.text
                        .toString()
                        .trim()

                produit.categorie =
                    categorie.text
                        .toString()
                        .trim()

                produit.stock =
                    stock.text
                        .toString()
                        .toIntOrNull()
                        ?: 0

                produit.description =
                    description.text
                        .toString()
                        .trim()

                sauvegarderProduits()

                toast(
                    "Produit modifié"
                )

                afficherAccueil()
            }
        )

        root.addView(
            bouton(
                "❌ Supprimer le produit"
            ) {

                AlertDialog.Builder(this)
                    .setTitle(
                        "Supprimer"
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

                        toast(
                            "Produit supprimé"
                        )

                        afficherAccueil()
                    }
                    .show()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // CAMERA
    // =========================================================

    private fun ouvrirCamera() {

        if (
            android.os.Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(
                Manifest.permission.CAMERA
            ) !=
            PackageManager.PERMISSION_GRANTED
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

            toast(
                "Impossible d'ouvrir la caméra"
            )
        }
    }

    // =========================================================
    // GALERIE
    // =========================================================

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

            toast(
                "Impossible d'ouvrir la galerie"
            )
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
            requestCode ==
            CAMERA_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {

            ouvrirCamera()
        }
    }

    // =========================================================
    // RÉSULTAT PHOTO
    // =========================================================

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
            resultCode != RESULT_OK
        ) {
            return
        }

        if (
            requestCode ==
            GALLERY_REQUEST
        ) {

            val uri =
                data?.data

            if (uri != null) {

                try {

                    imageProduit =
                        uri.toString()

                    imagePreview.visibility =
                        View.VISIBLE

                    imagePreview.setImageURI(
                        uri
                    )

                } catch (_: Exception) {

                    toast(
                        "Impossible de charger l'image"
                    )
                }
            }
        }

        if (
            requestCode ==
            CAMERA_REQUEST
        ) {

            val bitmap =
                data?.extras?.get("data")
                    as? Bitmap

            if (bitmap != null) {

                try {

                    val chemin =
                        sauvegarderBitmap(
                            bitmap
                        )

                    imageProduit =
                        chemin

                    imagePreview.visibility =
                        View.VISIBLE

                    imagePreview.setImageURI(
                        Uri.parse(
                            chemin
                        )
                    )

                    toast(
                        "Photo enregistrée"
                    )

                } catch (_: Exception) {

                    imagePreview.visibility =
                        View.VISIBLE

                    imagePreview.setImageBitmap(
                        bitmap
                    )

                    toast(
                        "Photo prise"
                    )
                }
            }
        }
    }

    // =========================================================
    // SAUVER PHOTO CAMÉRA
    // =========================================================

    private fun sauvegarderBitmap(
        bitmap: Bitmap
    ): String {

        val fichier =
            File(
                filesDir,
                "produit_${System.currentTimeMillis()}.jpg"
            )

        FileOutputStream(
            fichier
        ).use { sortie ->

            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                sortie
            )
        }

        return Uri.fromFile(
            fichier
        ).toString()
    }

    // =========================================================
    // PANIER
    // =========================================================

    private fun afficherPanier() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "🛒 Mon panier"
            )
        )

        root.addView(
            bouton("⬅ Accueil") {
                afficherAccueil()
            }
        )

        if (panier.isEmpty()) {

            root.addView(
                texte(
                    "Votre panier est vide.",
                    18f
                ).apply {

                    gravity =
                        Gravity.CENTER

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

            for (item in panier) {

                val bloc =
                    LinearLayout(this).apply {

                        orientation =
                            LinearLayout.VERTICAL

                        setPadding(
                            12,
                            12,
                            12,
                            12
                        )

                        setBackgroundColor(
                            Color.WHITE
                        )
                    }

                bloc.addView(
                    texte(
                        item.nom,
                        19f,
                        VERT
                    ).apply {

                        typeface =
                            Typeface.DEFAULT_BOLD
                    }
                )

                bloc.addView(
                    texte(
                        "${formaterNombre(item.prix)} FCFA",
                        16f
                    )
                )

                bloc.addView(
                    texte(
                        "Quantité : ${item.quantite}",
                        16f
                    )
                )

                bloc.addView(
                    bouton(
                        "➕ Ajouter 1"
                    ) {

                        val produit =
                            produits.find {

                                it.id ==
                                    item.produitId
                            }

                        if (
                            produit != null &&
                            item.quantite <
                            produit.stock
                        ) {

                            item.quantite++

                            sauvegarderPanier()

                            afficherPanier()
                        }
                    }
                )

                bloc.addView(
                    bouton(
                        "➖ Retirer 1"
                    ) {

                        if (
                            item.quantite > 1
                        ) {

                            item.quantite--

                        } else {

                            panier.remove(
                                item
                            )
                        }

                        sauvegarderPanier()

                        afficherPanier()
                    }
                )

                root.addView(
                    bloc
                )

                total +=
                    item.prix *
                    item.quantite
            }

            root.addView(
                texte(
                    "TOTAL : ${formaterNombre(total)} FCFA",
                    22f,
                    VERT
                ).apply {

                    gravity =
                        Gravity.CENTER

                    typeface =
                        Typeface.DEFAULT_BOLD

                    setPadding(
                        10,
                        25,
                        10,
                        25
                    )
                }
            )

            root.addView(
                boutonVert(
                    "✅ Passer la commande"
                ) {

                    demanderInfosCommande(
                        total
                    )
                }
            )

            root.addView(
                bouton(
                    "🗑️ Vider le panier"
                ) {

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

    // =========================================================
    // COMMANDE
    // =========================================================

    private fun demanderInfosCommande(
        total: Int
    ) {

        val box =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    20,
                    5,
                    20,
                    5
                )
            }

        val client =
            EditText(this).apply {

                hint =
                    "Nom du client"
            }

        val tel =
            EditText(this).apply {

                hint =
                    "Téléphone"

                inputType =
                    InputType.TYPE_CLASS_PHONE
            }

        box.addView(client)

        box.addView(tel)

        AlertDialog.Builder(this)
            .setTitle(
                "📦 Informations de commande"
            )
            .setView(box)
            .setNegativeButton(
                "Annuler",
                null
            )
            .setPositiveButton(
                "Confirmer"
            ) { _, _ ->

                val nomClient =
                    client.text
                        .toString()
                        .trim()

                val telephone =
                    tel.text
                        .toString()
                        .trim()

                if (
                    nomClient.isEmpty()
                ) {

                    toast(
                        "Nom du client obligatoire"
                    )

                    return@setPositiveButton
                }

                val produitsCommande =
                    panier.joinToString(
                        ", "
                    ) {

                        "${it.nom} x${it.quantite}"
                    }

                commandes.add(
                    Commande(
                        id =
                            System.currentTimeMillis()
                                .toString()
                                .takeLast(6),

                        client =
                            nomClient,

                        telephone =
                            telephone,

                        produit =
                            produitsCommande,

                        montant =
                            total.toString(),

                        statut =
                            "Nouvelle"
                    )
                )

                clients.add(
                    nomClient
                )

                sauvegarderCommandes()

                sauvegarderClients()

                panier.clear()

                sauvegarderPanier()

                toast(
                    "Commande enregistrée avec succès"
                )

                afficherAccueil()
            }
            .show()
    }

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun afficherCommandes() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "📦 Commandes"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        if (
            commandes.isEmpty()
        ) {

            root.addView(
                texte(
                    "Aucune commande.",
                    18f
                ).apply {

                    gravity =
                        Gravity.CENTER

                    setPadding(
                        10,
                        35,
                        10,
                        35
                    )
                }
            )

        } else {

            for (
                commande in commandes
            ) {

                val bloc =
                    LinearLayout(this).apply {

                        orientation =
                            LinearLayout.VERTICAL

                        setPadding(
                            12,
                            12,
                            12,
                            12
                        )

                        setBackgroundColor(
                            Color.WHITE
                        )
                    }

                bloc.addView(
                    texte(
                        """
                        📦 Commande #${commande.id}
                        👤 ${commande.client}
                        📞 ${commande.telephone}
                        🛍 ${commande.produit}
                        💰 ${formaterNombre(
                            commande.montant
                                .toIntOrNull()
                                ?: 0
                        )} FCFA
                        📌 ${commande.statut}
                        """.trimIndent(),
                        16f
                    )
                )

                bloc.addView(
                    boutonVert(
                        "✅ Confirmer"
                    ) {

                        commande.statut =
                            "Confirmée"

                        sauvegarderCommandes()

                        afficherCommandes()
                    }
                )

                bloc.addView(
                    bouton(
                        "📦 Préparer"
                    ) {

                        commande.statut =
                            "En préparation"

                        sauvegarderCommandes()

                        afficherCommandes()
                    }
                )

                bloc.addView(
                    bouton(
                        "🚚 En livraison"
                    ) {

                        commande.statut =
                            "En livraison"

                        sauvegarderCommandes()

                        afficherCommandes()
                    }
                )

                bloc.addView(
                    bouton(
                        "✅ Livrée"
                    ) {

                        commande.statut =
                            "Livrée"

                        sauvegarderCommandes()

                        afficherCommandes()
                    }
                )

                bloc.addView(
                    bouton(
                        "❌ Annuler"
                    ) {

                        commande.statut =
                            "Annulée"

                        sauvegarderCommandes()

                        afficherCommandes()
                    }
                )

                root.addView(
                    bloc
                )
            }
        }

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // MENU PRINCIPAL
    // =========================================================

    private fun afficherMenu() {

        val choix =
            arrayOf(

                "🏠 Accueil",

                "🛍️ Produits",

                "➕ Ajouter un produit",

                "🛒 Mon panier",

                "📦 Commandes",

                "👨‍💼 Espace vendeur",

                "📊 Tableau de bord",

                "📦 Gestion du stock",

                "👥 Clients",

                "💎 Abonnement vendeur",

                "💳 Paiement",

                "👤 Mon compte",

                "📞 Contact / WhatsApp",

                "📤 Partager l'application",

                "🚪 Quitter l'application"
            )

        AlertDialog.Builder(this)
            .setTitle(
                "☰ VIE ESPOIR MARKETING"
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
                        afficherPanier()

                    4 ->
                        afficherCommandes()

                    5 ->
                        afficherEspaceVendeur()

                    6 ->
                        afficherTableauDeBord()

                    7 ->
                        afficherStock()

                    8 ->
                        afficherClients()

                    9 ->
                        afficherAbonnement()

                    10 ->
                        afficherPaiement()

                    11 ->
                        afficherCompte()

                    12 ->
                        afficherContact()

                    13 ->
                        partagerApplication()

                    14 -> {

                        AlertDialog.Builder(this)
                            .setTitle(
                                "🚪 Quitter l'application"
                            )
                            .setMessage(
                                "Voulez-vous vraiment quitter Vie Espoir Marketing ?"
                            )
                            .setNegativeButton(
                                "Annuler",
                                null
                            )
                            .setPositiveButton(
                                "Quitter"
                            ) { _, _ ->

                                finishAffinity()
                            }
                            .show()
                    }
                }
            }
            .show()
    }

    // =========================================================
    // ESPACE VENDEUR
    // =========================================================

    private fun afficherEspaceVendeur() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "👨‍💼 Espace vendeur"
            )
        )

        root.addView(
            bouton("⬅ Accueil") {
                afficherAccueil()
            }
        )

        val nom =
            prefs.getString(
                SELLER_NAME,
                ""
            ) ?: ""

        val valide =
            prefs.getBoolean(
                SELLER_VALIDATED,
                false
            )

        if (
            nom.isEmpty()
        ) {

            root.addView(
                texte(
                    "Vous devez créer votre compte vendeur.",
                    17f
                ).apply {

                    gravity =
                        Gravity.CENTER
                }
            )

            root.addView(
                boutonVert(
                    "👤 Créer mon compte"
                ) {

                    afficherCreationCompte()
                }
            )

            root.addView(
                bouton(
                    "🔐 Se connecter"
                ) {

                    afficherConnexion()
                }
            )

        } else {

            root.addView(
                texte(
                    """
                    Bonjour $nom 👋
                    
                    ${
                        if (valide)
                            "✅ Compte vendeur actif"
                        else
                            "⏳ Compte en attente"
                    }
                    """.trimIndent(),
                    20f,
                    VERT
                ).apply {

                    gravity =
                        Gravity.CENTER

                    typeface =
                        Typeface.DEFAULT_BOLD
                }
            )

            root.addView(
                boutonVert(
                    "➕ Ajouter un produit"
                ) {

                    afficherAjouterProduit()
                }
            )

            root.addView(
                bouton(
                    "🛍️ Mes produits"
                ) {

                    afficherProduits("")
                }
            )

            root.addView(
                bouton(
                    "📦 Mes commandes"
                ) {

                    afficherCommandes()
                }
            )

            root.addView(
                bouton(
                    "📊 Mon tableau de bord"
                ) {

                    afficherTableauDeBord()
                }
            )

            root.addView(
                bouton(
                    "📦 Mon stock"
                ) {

                    afficherStock()
                }
            )

            root.addView(
                bouton(
                    "💎 Mon abonnement"
                ) {

                    afficherAbonnement()
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // COMPTE
    // =========================================================

    private fun afficherCompte() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "👤 Mon compte"
            )
        )

        root.addView(
            bouton("⬅ Accueil") {
                afficherAccueil()
            }
        )

        val nom =
            prefs.getString(
                SELLER_NAME,
                ""
            ) ?: ""

        if (
            nom.isEmpty()
        ) {

            root.addView(
                texte(
                    "Aucun compte vendeur enregistré.",
                    17f
                ).apply {

                    gravity =
                        Gravity.CENTER
                }
            )

            root.addView(
                boutonVert(
                    "➕ Créer un compte"
                ) {

                    afficherCreationCompte()
                }
            )

            root.addView(
                bouton(
                    "🔐 Se connecter"
                ) {

                    afficherConnexion()
                }
            )

        } else {

            val email =
                prefs.getString(
                    SELLER_EMAIL,
                    ""
                ) ?: ""

            val phone =
                prefs.getString(
                    SELLER_PHONE,
                    ""
                ) ?: ""

            val whatsapp =
                prefs.getString(
                    SELLER_WHATSAPP,
                    ""
                ) ?: ""

            val pack =
                prefs.getString(
                    SELLER_PACK,
                    "Aucun"
                ) ?: "Aucun"

            root.addView(
                texte(
                    """
                    👋 Bienvenue $nom
                    
                    📧 $email
                    📞 $phone
                    💬 WhatsApp : $whatsapp
                    💎 Abonnement : $pack
                    """.trimIndent(),
                    17f
                ).apply {

                    gravity =
                        Gravity.CENTER
                }
            )

            root.addView(
                boutonVert(
                    "👨‍💼 Espace vendeur"
                ) {

                    afficherEspaceVendeur()
                }
            )

            root.addView(
                bouton(
                    "💎 Modifier mon abonnement"
                ) {

                    afficherAbonnement()
                }
            )

            root.addView(
                bouton(
                    "🚪 Se déconnecter"
                ) {

                    prefs.edit()
                        .remove(
                            SELLER_NAME
                        )
                        .remove(
                            SELLER_EMAIL
                        )
                        .remove(
                            SELLER_PHONE
                        )
                        .remove(
                            SELLER_WHATSAPP
                        )
                        .remove(
                            SELLER_VALIDATED
                        )
                        .remove(
                            SELLER_PACK
                        )
                        .apply()

                    toast(
                        "Déconnexion effectuée"
                    )

                    afficherCompte()
                }
            )
        }

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // CRÉATION COMPTE
    // =========================================================

    private fun afficherCreationCompte() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "➕ Créer un compte vendeur"
            )
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
            }

        val email =
            EditText(this).apply {

                hint =
                    "Adresse e-mail"

                inputType =
                    InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

        val phone =
            EditText(this).apply {

                hint =
                    "Téléphone"

                inputType =
                    InputType.TYPE_CLASS_PHONE
            }

        val whatsapp =
            EditText(this).apply {

                hint =
                    "WhatsApp"

                inputType =
                    InputType.TYPE_CLASS_PHONE
            }

        root.addView(nom)

        root.addView(email)

        root.addView(phone)

        root.addView(whatsapp)

        root.addView(
            boutonVert(
                "✅ Créer mon compte"
            ) {

                val n =
                    nom.text
                        .toString()
                        .trim()

                val e =
                    email.text
                        .toString()
                        .trim()

                val p =
                    phone.text
                        .toString()
                        .trim()

                val w =
                    whatsapp.text
                        .toString()
                        .trim()

                if (
                    n.isEmpty() ||
                    e.isEmpty() ||
                    p.isEmpty()
                ) {

                    toast(
                        "Nom, e-mail et téléphone obligatoires"
                    )

                    return@boutonVert
                }

                prefs.edit()
                    .putString(
                        SELLER_NAME,
                        n
                    )
                    .putString(
                        SELLER_EMAIL,
                        e
                    )
                    .putString(
                        SELLER_PHONE,
                        p
                    )
                    .putString(
                        SELLER_WHATSAPP,
                        w
                    )
                    .putBoolean(
                        SELLER_VALIDATED,
                        true
                    )
                    .apply()

                toast(
                    "Compte créé avec succès"
                )

                afficherCompte()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // CONNEXION
    // =========================================================

    private fun afficherConnexion() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "🔐 Connexion vendeur"
            )
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

                inputType =
                    InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

        root.addView(
            email
        )

        root.addView(
            boutonVert(
                "🔓 Se connecter"
            ) {

                val entre =
                    email.text
                        .toString()
                        .trim()

                val enregistre =
                    prefs.getString(
                        SELLER_EMAIL,
                        ""
                    ) ?: ""

                if (
                    enregistre.isEmpty()
                ) {

                    toast(
                        "Aucun compte trouvé"
                    )

                } else if (
                    entre == enregistre
                ) {

                    toast(
                        "Connexion réussie"
                    )

                    afficherEspaceVendeur()

                } else {

                    toast(
                        "E-mail incorrect"
                    )
                }
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // TABLEAU DE BORD
    // =========================================================

    private fun afficherTableauDeBord() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "📊 Tableau de bord"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        var ventes = 0

        var montant = 0

        for (
            commande in commandes
        ) {

            if (
                commande.statut !=
                "Annulée"
            ) {

                ventes++

                montant +=
                    commande.montant
                        .toIntOrNull()
                        ?: 0
            }
        }

        root.addView(
            texte(
                """
                🛍️ Produits : ${produits.size}
                
                📦 Commandes : ${commandes.size}
                
                ✅ Ventes actives : $ventes
                
                💰 Chiffre d'affaires : ${formaterNombre(montant)} FCFA
                
                👥 Clients : ${clients.size}
                """.trimIndent(),
                19f,
                VERT
            ).apply {

                gravity =
                    Gravity.CENTER

                setPadding(
                    10,
                    25,
                    10,
                    25
                )

                typeface =
                    Typeface.DEFAULT_BOLD

                setBackgroundColor(
                    Color.WHITE
                )
            }
        )

        root.addView(
            bouton(
                "📦 Voir les commandes"
            ) {

                afficherCommandes()
            }
        )

        root.addView(
            bouton(
                "🛍️ Voir les produits"
            ) {

                afficherProduits("")
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // STOCK
    // =========================================================

    private fun afficherStock() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "📦 Gestion du stock"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        root.addView(
            texte(
                "Nombre de produits : ${produits.size}",
                18f,
                VERT
            )
        )

        for (
            produit in produits
        ) {

            val bloc =
                LinearLayout(this).apply {

                    orientation =
                        LinearLayout.VERTICAL

                    setPadding(
                        12,
                        12,
                        12,
                        12
                    )

                    setBackgroundColor(
                        Color.WHITE
                    )
                }

            bloc.addView(
                texte(
                    produit.nom,
                    18f,
                    VERT
                ).apply {

                    typeface =
                        Typeface.DEFAULT_BOLD
                }
            )

            bloc.addView(
                texte(
                    "Prix : ${formaterNombre(
                        produit.prix
                            .toIntOrNull()
                            ?: 0
                    )} FCFA"
                )
            )

            bloc.addView(
                texte(
                    "Stock : ${produit.stock}",
                    16f,
                    if (
                        produit.stock <= 5
                    )
                        Color.RED
                    else
                        VERT
                )
            )

            bloc.addView(
                bouton(
                    "➕ Ajouter du stock"
                ) {

                    val champ =
                        EditText(this).apply {

                            hint =
                                "Quantité à ajouter"

                            inputType =
                                InputType.TYPE_CLASS_NUMBER
                        }

                    AlertDialog.Builder(this)
                        .setTitle(
                            "Ajouter du stock"
                        )
                        .setView(
                            champ
                        )
                        .setNegativeButton(
                            "Annuler",
                            null
                        )
                        .setPositiveButton(
                            "Ajouter"
                        ) { _, _ ->

                            val q =
                                champ.text
                                    .toString()
                                    .toIntOrNull()
                                    ?: 0

                            produit.stock +=
                                q

                            sauvegarderProduits()

                            afficherStock()
                        }
                        .show()
                }
            )

            bloc.addView(
                bouton(
                    "✏️ Modifier"
                ) {

                    afficherModifierProduit(
                        produit
                    )
                }
            )

            root.addView(
                bloc
            )
        }

        root.addView(
            boutonVert(
                "➕ Ajouter un produit"
            ) {

                afficherAjouterProduit()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // CLIENTS
    // =========================================================

    private fun afficherClients() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "👥 Mes clients"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        if (
            clients.isEmpty()
        ) {

            root.addView(
                texte(
                    "Aucun client enregistré.",
                    18f
                ).apply {

                    gravity =
                        Gravity.CENTER
                }
            )
        }

        for (
            client in clients
        ) {

            root.addView(
                texte(
                    "👤 $client",
                    17f
                ).apply {

                    setBackgroundColor(
                        Color.WHITE
                    )

                    setPadding(
                        15,
                        15,
                        15,
                        15
                    )
                }
            )
        }

        root.addView(
            boutonVert(
                "➕ Ajouter un client"
            ) {

                val champ =
                    EditText(this).apply {

                        hint =
                            "Nom du client"
                    }

                AlertDialog.Builder(this)
                    .setTitle(
                        "Ajouter un client"
                    )
                    .setView(
                        champ
                    )
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

                        if (
                            nom.isNotEmpty()
                        ) {

                            clients.add(
                                nom
                            )

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

    // =========================================================
    // ABONNEMENT — NOUVELLE VERSION
    // =========================================================

    private fun afficherAbonnement() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "💎 Abonnement vendeur"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        val actuel =
            prefs.getString(
                SELLER_PACK,
                "Aucun"
            ) ?: "Aucun"

        root.addView(
            texte(
                "Abonnement actuel : $actuel",
                18f,
                VERT
            ).apply {

                gravity =
                    Gravity.CENTER

                typeface =
                    Typeface.DEFAULT_BOLD

                setPadding(
                    10,
                    15,
                    10,
                    15
                )
            }
        )

        root.addView(
            texte(
                "L'abonnement vendeur commence à 5 000 FCFA par mois.",
                16f,
                TEXTE
            ).apply {

                gravity =
                    Gravity.CENTER

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
        )

        root.addView(
            boutonVert(
                "💎 Choisir — 5 000 FCFA / mois"
            ) {

                val pack =
                    "Abonnement mensuel — 5 000 FCFA / mois"

                prefs.edit()
                    .putString(
                        SELLER_PACK,
                        pack
                    )
                    .apply()

                toast(
                    "Abonnement sélectionné : 5 000 FCFA / mois"
                )

                afficherPaiement()
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // PAIEMENT
    // =========================================================

    private fun afficherPaiement() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "💳 Paiement"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAbonnement()
            }
        )

        root.addView(
            texte(
                """
                Abonnement sélectionné :
                5 000 FCFA / mois
                
                Choisissez votre moyen de paiement :
                
                🟠 Orange Money / Wave
                📞 $ORANGE_WAVE
                
                🟡 MTN Mobile Money
                📞 $MTN
                """.trimIndent(),
                18f
            ).apply {

                gravity =
                    Gravity.CENTER

                setBackgroundColor(
                    Color.WHITE
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
            boutonVert(
                "🟠 Orange Money / Wave"
            ) {

                appelerNumero(
                    ORANGE_WAVE
                )
            }
        )

        root.addView(
            boutonVert(
                "🟡 MTN Mobile Money"
            ) {

                appelerNumero(
                    MTN
                )
            }
        )

        root.addView(
            bouton(
                "💬 Contacter par WhatsApp"
            ) {

                ouvrirWhatsApp(
                    ORANGE_WAVE
                )
            }
        )

        root.addView(
            texte(
                "Après paiement, l'administrateur pourra confirmer l'activation de l'abonnement.",
                14f,
                Color.GRAY
            ).apply {

                gravity =
                    Gravity.CENTER

                setPadding(
                    10,
                    20,
                    10,
                    20
                )
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // CONTACT
    // =========================================================

    private fun afficherContact() {

        val root =
            layoutBase()

        root.addView(
            titre(
                "📞 Contact Vie Espoir Marketing"
            )
        )

        root.addView(
            bouton("⬅ Accueil") {
                afficherAccueil()
            }
        )

        root.addView(
            texte(
                """
                🟠 Orange Money / Wave
                
                $ORANGE_WAVE
                
                🟡 MTN
                
                $MTN
                """.trimIndent(),
                19f,
                VERT
            ).apply {

                gravity =
                    Gravity.CENTER

                typeface =
                    Typeface.DEFAULT_BOLD

                setBackgroundColor(
                    Color.WHITE
                )
            }
        )

        root.addView(
            boutonVert(
                "📞 Appeler Orange / Wave"
            ) {

                appelerNumero(
                    ORANGE_WAVE
                )
            }
        )

        root.addView(
            boutonVert(
                "📞 Appeler MTN"
            ) {

                appelerNumero(
                    MTN
                )
            }
        )

        root.addView(
            bouton(
                "💬 WhatsApp Orange / Wave"
            ) {

                ouvrirWhatsApp(
                    ORANGE_WAVE
                )
            }
        )

        root.addView(
            bouton(
                "💬 WhatsApp MTN"
            ) {

                ouvrirWhatsApp(
                    MTN
                )
            }
        )

        setContentView(
            scroll(root)
        )
    }

    // =========================================================
    // APPEL
    // =========================================================

    private fun appelerNumero(
        numero: String
    ) {

        try {

            startActivity(
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse(
                        "tel:$numero"
                    )
                )
            )

        } catch (_: Exception) {

            toast(
                "Impossible d'ouvrir le téléphone"
            )
        }
    }

    // =========================================================
    // WHATSAPP
    // =========================================================

    private fun ouvrirWhatsApp(
        numero: String
    ) {

        val propre =
            numero
                .replace(
                    "+",
                    ""
                )
                .replace(
                    " ",
                    ""
                )

        val international =
            if (
                propre.startsWith("0")
            )
                "225$propre"
            else
                propre

        try {

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://wa.me/$international"
                    )
                )
            )

        } catch (_: Exception) {

            toast(
                "WhatsApp n'est pas disponible"
            )
        }
    }

    // =========================================================
    // PARTAGE PRODUIT
    // =========================================================

    private fun partagerProduit(
        produit: Produit
    ) {

        val prix =
            produit.prix
                .toIntOrNull()
                ?: 0

        val texte =
            """
            🛍️ VIE ESPOIR MARKETING
            
            ${produit.nom}
            
            💰 Prix : ${formaterNombre(prix)} FCFA
            
            🏷️ Catégorie : ${produit.categorie}
            
            📦 Stock : ${produit.stock}
            
            ${produit.description}
            
            Contact :
            Orange / Wave : $ORANGE_WAVE
            MTN : $MTN
            """.trimIndent()

        val intent =
            Intent(
                Intent.ACTION_SEND
            ).apply {

                type =
                    "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    texte
                )
            }

        startActivity(
            Intent.createChooser(
                intent,
                "Partager le produit"
            )
        )
    }

    // =========================================================
    // PARTAGE APPLICATION
    // =========================================================

    private fun partagerApplication() {

        val texte =
            """
            🦁 VIE ESPOIR MARKETING
            
            Découvrez notre plateforme de vente.
            
            Produits • Commandes • Vendeurs
            
            Ensemble pour un meilleur avenir.
            """.trimIndent()

        val intent =
            Intent(
                Intent.ACTION_SEND
            ).apply {

                type =
                    "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    texte
                )
            }

        startActivity(
            Intent.createChooser(
                intent,
                "Partager l'application"
            )
        )
    }

    // =========================================================
    // FORMATAGE
    // =========================================================

    private fun formaterNombre(
        nombre: Int
    ): String {

        return String.format(
            Locale.FRANCE,
            "%,d",
            nombre
        ).replace(
            ',',
            ' '
        )
    }

    private fun toast(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // RETOUR — QUITTER L'APPLICATION
    // =========================================================

    override fun onBackPressed() {

        finishAffinity()
    }

    // =========================================================
    // SAUVEGARDE PRODUITS
    // =========================================================

    private fun sauvegarderProduits() {

        val tableau =
            JSONArray()

        for (
            produit in produits
        ) {

            tableau.put(
                JSONObject().apply {

                    put(
                        "id",
                        produit.id
                    )

                    put(
                        "nom",
                        produit.nom
                    )

                    put(
                        "prix",
                        produit.prix
                    )

                    put(
                        "categorie",
                        produit.categorie
                    )

                    put(
                        "stock",
                        produit.stock
                    )

                    put(
                        "description",
                        produit.description
                    )

                    put(
                        "image",
                        produit.image
                    )
                }
            )
        }

        prefs.edit()
            .putString(
                PRODUCTS_KEY,
                tableau.toString()
            )
            .apply()
    }

    // =========================================================
    // SAUVEGARDE PANIER
    // =========================================================

    private fun sauvegarderPanier() {

        val tableau =
            JSONArray()

        for (
            item in panier
        ) {

            tableau.put(
                JSONObject().apply {

                    put(
                        "produitId",
                        item.produitId
                    )

                    put(
                        "nom",
                        item.nom
                    )

                    put(
                        "prix",
                        item.prix
                    )

                    put(
                        "quantite",
                        item.quantite
                    )

                    put(
                        "image",
                        item.image
                    )
                }
            )
        }

        prefs.edit()
            .putString(
                CART_KEY,
                tableau.toString()
            )
            .apply()
    }

    // =========================================================
    // SAUVEGARDE CLIENTS
    // =========================================================

    private fun sauvegarderClients() {

        val tableau =
            JSONArray()

        for (
            client in clients
        ) {

            tableau.put(
                client
            )
        }

        prefs.edit()
            .putString(
                CLIENTS_KEY,
                tableau.toString()
            )
            .apply()
    }

    // =========================================================
    // SAUVEGARDE COMMANDES
    // =========================================================

    private fun sauvegarderCommandes() {

        val tableau =
            JSONArray()

        for (
            commande in commandes
        ) {

            tableau.put(
                JSONObject().apply {

                    put(
                        "id",
                        commande.id
                    )

                    put(
                        "client",
                        commande.client
                    )

                    put(
                        "telephone",
                        commande.telephone
                    )

                    put(
                        "produit",
                        commande.produit
                    )

                    put(
                        "montant",
                        commande.montant
                    )

                    put(
                        "statut",
                        commande.statut
                    )
                }
            )
        }

        prefs.edit()
            .putString(
                ORDERS_KEY,
                tableau.toString()
            )
            .apply()
    }

    // =========================================================
    // CHARGEMENT
    // =========================================================

    private fun chargerDonnees() {

        produits.clear()

        panier.clear()

        clients.clear()

        commandes.clear()

        // -----------------------------------------------------
        // PRODUITS
        // -----------------------------------------------------

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
                    JSONArray(
                        produitsJson
                    )

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(
                            i
                        )

                    produits.add(
                        Produit(

                            id =
                                objet.optString(
                                    "id",
                                    System.currentTimeMillis()
                                        .toString()
                                ),

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
                                    "categorie",
                                    "Autres"
                                ),

                            stock =
                                objet.optInt(
                                    "stock",
                                    0
                                ),

                            description =
                                objet.optString(
                                    "description"
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

        // -----------------------------------------------------
        // PANIER
        // -----------------------------------------------------

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
                    JSONArray(
                        panierJson
                    )

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(
                            i
                        )

                    panier.add(
                        PanierItem(

                            produitId =
                                objet.optString(
                                    "produitId"
                                ),

                            nom =
                                objet.optString(
                                    "nom"
                                ),

                            prix =
                                objet.optInt(
                                    "prix",
                                    0
                                ),

                            quantite =
                                objet.optInt(
                                    "quantite",
                                    1
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

        // -----------------------------------------------------
        // CLIENTS
        // -----------------------------------------------------

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
                    JSONArray(
                        clientsJson
                    )

                for (
                    i in 0 until tableau.length()
                ) {

                    clients.add(
                        tableau.getString(
                            i
                        )
                    )
                }

            } catch (_: Exception) {
            }
        }

        // -----------------------------------------------------
        // COMMANDES
        // -----------------------------------------------------

        val commandesJson =
            prefs.getString(
                ORDERS_KEY,
                null
            )

        if (
            !commandesJson.isNullOrEmpty()
        ) {

            try {

                val tableau =
                    JSONArray(
                        commandesJson
                    )

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(
                            i
                        )

                    commandes.add(
                        Commande(

                            id =
                                objet.optString(
                                    "id"
                                ),

                            client =
                                objet.optString(
                                    "client"
                                ),

                            telephone =
                                objet.optString(
                                    "telephone"
                                ),

                            produit =
                                objet.optString(
                                    "produit"
                                ),

                            montant =
                                objet.optString(
                                    "montant"
                                ),

                            statut =
                                objet.optString(
                                    "statut",
                                    "Nouvelle"
                                )
                        )
                    )
                }

            } catch (_: Exception) {
            }
        }
    }
}