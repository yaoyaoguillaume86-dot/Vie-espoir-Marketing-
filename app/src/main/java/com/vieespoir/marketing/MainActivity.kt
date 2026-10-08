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
    private lateinit var recherche: EditText

    private var imageProduit = ""

    private val CAMERA_REQUEST = 100
    private val GALLERY_REQUEST = 101

    private val PREFS = "vie_espoir_data"
    private val PRODUCTS_KEY = "products"
    private val CART_KEY = "cart"
    private val CLIENTS_KEY = "clients"
    private val SELLER_NAME = "seller_name"
    private val SELLER_EMAIL = "seller_email"

    private val prefs by lazy {
        getSharedPreferences(PREFS, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerDonnees()
        afficherAccueil()
    }

    private fun couleurFond(): Int {
        return Color.rgb(248, 249, 247)
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

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(6, 6, 6, 6)
            }
        }
    }

    private fun titre(texte: String): TextView {

        return TextView(this).apply {

            text = texte
            textSize = 23f

            setTextColor(
                Color.rgb(20, 100, 50)
            )

            gravity = Gravity.CENTER

            setPadding(
                10,
                20,
                10,
                20
            )
        }
    }

    private fun baseLayout(): LinearLayout {

        return LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setBackgroundColor(
                couleurFond()
            )

            setPadding(
                12,
                10,
                12,
                10
            )
        }
    }

    private fun scroll(
        layout: LinearLayout
    ): ScrollView {

        return ScrollView(this).apply {
            addView(layout)
        }
    }

    private fun afficherAccueil() {

        val root = baseLayout()

        val entete = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL

            gravity = Gravity.CENTER_VERTICAL
        }

        val logo = TextView(this).apply {

            text = "🦁"
            textSize = 38f
        }

        val nom = TextView(this).apply {

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

        val menu = Button(this).apply {

            text = "☰"

            textSize = 24f

            setOnClickListener {
                afficherMenu()
            }
        }

        entete.addView(logo)
        entete.addView(nom)
        entete.addView(menu)

        root.addView(entete)

        recherche = EditText(this).apply {

            hint = "🔎 Rechercher un produit..."

            textSize = 16f

            setSingleLine(true)

            setPadding(
                20,
                10,
                20,
                10
            )
        }

        root.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            bouton("🔎 Rechercher") {
                afficherProduits()
            }
        )

        root.addView(
            bouton("🛍 Voir tous les produits") {
                afficherProduits()
            }
        )

        zoneProduits = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(zoneProduits)

        val navigation = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL

            gravity = Gravity.CENTER
        }

        navigation.addView(
            bouton("🏠 Accueil") {
                afficherAccueil()
            }
        )

        navigation.addView(
            bouton("🛍 Produits") {
                afficherProduits()
            }
        )

        navigation.addView(
            bouton("🛒 Panier") {
                afficherPanier()
            }
        )

        navigation.addView(
            bouton("👤 Compte") {
                afficherCompte()
            }
        )

        root.addView(navigation)

        setContentView(
            scroll(root)
        )

        afficherProduitsDansAccueil()
    }

    private fun afficherProduitsDansAccueil() {

        zoneProduits.removeAllViews()

        zoneProduits.addView(
            titre("🛍 Produits disponibles")
        )

        if (produits.isEmpty()) {

            val message = TextView(this).apply {

                text =
                    "Aucun produit pour le moment."

                textSize = 18f

                gravity = Gravity.CENTER

                setPadding(
                    10,
                    30,
                    10,
                    30
                )
            }

            zoneProduits.addView(message)

            zoneProduits.addView(
                bouton("➕ Ajouter un produit") {
                    afficherAjouterProduit()
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

        val carte = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                16,
                16,
                16,
                16
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

                val imageView =
                    ImageView(this)

                imageView.layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        280
                    )

                imageView.scaleType =
                    ImageView.ScaleType.CENTER_CROP

                imageView.setImageURI(
                    Uri.parse(produit.image)
                )

                carte.addView(
                    imageView
                )

            } catch (_: Exception) {
            }
        }

        val nom = TextView(this).apply {

            text = produit.nom

            textSize = 20f

            setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )
        }

        carte.addView(nom)

        val prix = TextView(this).apply {

            text =
                "Prix : ${produit.prix} FCFA"

            textSize = 17f

            setTextColor(
                Color.rgb(20, 100, 50)
            )
        }

        carte.addView(prix)

        val categorie = TextView(this).apply {

            text =
                "Catégorie : ${produit.categorie}"

            textSize = 15f
        }

        carte.addView(categorie)

        carte.addView(
            bouton("🛒 Ajouter au panier") {

                panier.add(produit)

                sauvegarderPanier()

                Toast.makeText(
                    this,
                    "${produit.nom} ajouté au panier",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        parent.addView(carte)
    }    private fun afficherProduits() {

        val root = baseLayout()

        root.addView(
            titre("🛍 Tous les produits")
        )

        root.addView(
            bouton("⬅ Retour à l'accueil") {
                afficherAccueil()
            }
        )

        val rechercheLocale = EditText(this).apply {

            hint = "🔎 Rechercher un produit"

            textSize = 16f

            setSingleLine(true)

            setPadding(
                15,
                10,
                15,
                10
            )
        }

        root.addView(rechercheLocale)

        val liste = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(liste)

        fun actualiserListe() {

            liste.removeAllViews()

            val mot =
                rechercheLocale.text
                    .toString()
                    .trim()
                    .lowercase(Locale.getDefault())

            for (produit in produits) {

                val correspond =
                    mot.isEmpty() ||
                    produit.nom
                        .lowercase(Locale.getDefault())
                        .contains(mot) ||
                    produit.categorie
                        .lowercase(Locale.getDefault())
                        .contains(mot)

                if (correspond) {

                    ajouterCarteProduit(
                        liste,
                        produit
                    )
                }
            }

            if (liste.childCount == 0) {

                liste.addView(
                    TextView(this).apply {

                        text =
                            "Aucun produit trouvé."

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
            }
        }

        root.addView(
            bouton("🔎 Rechercher") {
                actualiserListe()
            }
        )

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        actualiserListe()

        setContentView(
            scroll(root)
        )
    }

    private fun afficherMenu() {

        val options = arrayOf(
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
            .setTitle("☰ Menu VIE ESPOIR")
            .setItems(options) { _, position ->

                when (position) {

                    0 -> afficherAccueil()

                    1 -> afficherProduits()

                    2 -> afficherAjouterProduit()

                    3 -> afficherEspaceVendeur()

                    4 -> afficherPanier()

                    5 -> afficherClients()

                    6 -> afficherStock()

                    7 -> afficherCompte()
                }
            }
            .show()
    }

    private fun afficherAjouterProduit() {

        val root = baseLayout()

        root.addView(
            titre("➕ Ajouter un produit")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val nom = EditText(this).apply {

            hint = "Nom du produit"

            textSize = 16f
        }

        root.addView(nom)

        val prix = EditText(this).apply {

            hint = "Prix en FCFA"

            textSize = 16f

            inputType =
                android.text.InputType.TYPE_CLASS_NUMBER
        }

        root.addView(prix)

        val categorie = EditText(this).apply {

            hint = "Catégorie"

            textSize = 16f
        }

        root.addView(categorie)

        val apercu = ImageView(this).apply {

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )

            scaleType =
                ImageView.ScaleType.CENTER_CROP

            visibility = View.GONE
        }

        imagePreview = apercu

        root.addView(apercu)

        root.addView(
            bouton("📷 Prendre une photo") {

                ouvrirCamera()
            }
        )

        root.addView(
            bouton("🖼 Choisir une image") {

                ouvrirGalerie()
            }
        )

        root.addView(
            bouton("💾 Enregistrer le produit") {

                val nomProduit =
                    nom.text.toString().trim()

                val prixProduit =
                    prix.text.toString().trim()

                val categorieProduit =
                    categorie.text.toString().trim()

                if (nomProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez le nom du produit",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                if (prixProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez le prix",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                val nouveauProduit =
                    Produit(
                        nom = nomProduit,
                        prix = prixProduit,
                        categorie =
                            if (categorieProduit.isEmpty())
                                "Autres"
                            else
                                categorieProduit,
                        image = imageProduit
                    )

                produits.add(
                    nouveauProduit
                )

                sauvegarderProduits()

                imageProduit = ""

                Toast.makeText(
                    this,
                    "Produit ajouté avec succès",
                    Toast.LENGTH_SHORT
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

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Caméra indisponible",
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

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Galerie indisponible",
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

                imagePreview?.visibility =
                    View.VISIBLE

                imagePreview?.setImageURI(
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

                imagePreview?.visibility =
                    View.VISIBLE

                imagePreview?.setImageBitmap(
                    bitmap
                )

                Toast.makeText(
                    this,
                    "Photo prise. Pour la conserver, utilisez aussi la galerie si nécessaire.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun afficherPanier() {

        val root = baseLayout()

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

            for (index in panier.indices) {

                val produit =
                    panier[index]

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

                        panier.removeAt(index)

                        sauvegarderPanier()

                        afficherPanier()
                    }
                )

                root.addView(bloc)

                try {

                    total +=
                        produit.prix
                            .replace(
                                " ",
                                ""
                            )
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

                    setTextColor(
                        Color.rgb(20, 100, 50)
                    )

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
                bouton("✅ Passer la commande") {

                    AlertDialog.Builder(this)
                        .setTitle("Commande")
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
    }    private fun afficherProduits() {

        val root = baseLayout()

        root.addView(
            titre("🛍 Tous les produits")
        )

        root.addView(
            bouton("⬅ Retour à l'accueil") {
                afficherAccueil()
            }
        )

        val rechercheLocale = EditText(this).apply {

            hint = "🔎 Rechercher un produit"

            textSize = 16f

            setSingleLine(true)

            setPadding(
                15,
                10,
                15,
                10
            )
        }

        root.addView(rechercheLocale)

        val liste = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(liste)

        fun actualiserListe() {

            liste.removeAllViews()

            val mot =
                rechercheLocale.text
                    .toString()
                    .trim()
                    .lowercase(Locale.getDefault())

            for (produit in produits) {

                val correspond =
                    mot.isEmpty() ||
                    produit.nom
                        .lowercase(Locale.getDefault())
                        .contains(mot) ||
                    produit.categorie
                        .lowercase(Locale.getDefault())
                        .contains(mot)

                if (correspond) {

                    ajouterCarteProduit(
                        liste,
                        produit
                    )
                }
            }

            if (liste.childCount == 0) {

                liste.addView(
                    TextView(this).apply {

                        text =
                            "Aucun produit trouvé."

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
            }
        }

        root.addView(
            bouton("🔎 Rechercher") {
                actualiserListe()
            }
        )

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        actualiserListe()

        setContentView(
            scroll(root)
        )
    }

    private fun afficherMenu() {

        val options = arrayOf(
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
            .setTitle("☰ Menu VIE ESPOIR")
            .setItems(options) { _, position ->

                when (position) {

                    0 -> afficherAccueil()

                    1 -> afficherProduits()

                    2 -> afficherAjouterProduit()

                    3 -> afficherEspaceVendeur()

                    4 -> afficherPanier()

                    5 -> afficherClients()

                    6 -> afficherStock()

                    7 -> afficherCompte()
                }
            }
            .show()
    }

    private fun afficherAjouterProduit() {

        val root = baseLayout()

        root.addView(
            titre("➕ Ajouter un produit")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        val nom = EditText(this).apply {

            hint = "Nom du produit"

            textSize = 16f
        }

        root.addView(nom)

        val prix = EditText(this).apply {

            hint = "Prix en FCFA"

            textSize = 16f

            inputType =
                android.text.InputType.TYPE_CLASS_NUMBER
        }

        root.addView(prix)

        val categorie = EditText(this).apply {

            hint = "Catégorie"

            textSize = 16f
        }

        root.addView(categorie)

        val apercu = ImageView(this).apply {

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )

            scaleType =
                ImageView.ScaleType.CENTER_CROP

            visibility = View.GONE
        }

        imagePreview = apercu

        root.addView(apercu)

        root.addView(
            bouton("📷 Prendre une photo") {

                ouvrirCamera()
            }
        )

        root.addView(
            bouton("🖼 Choisir une image") {

                ouvrirGalerie()
            }
        )

        root.addView(
            bouton("💾 Enregistrer le produit") {

                val nomProduit =
                    nom.text.toString().trim()

                val prixProduit =
                    prix.text.toString().trim()

                val categorieProduit =
                    categorie.text.toString().trim()

                if (nomProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez le nom du produit",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                if (prixProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez le prix",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                val nouveauProduit =
                    Produit(
                        nom = nomProduit,
                        prix = prixProduit,
                        categorie =
                            if (categorieProduit.isEmpty())
                                "Autres"
                            else
                                categorieProduit,
                        image = imageProduit
                    )

                produits.add(
                    nouveauProduit
                )

                sauvegarderProduits()

                imageProduit = ""

                Toast.makeText(
                    this,
                    "Produit ajouté avec succès",
                    Toast.LENGTH_SHORT
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

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Caméra indisponible",
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

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Galerie indisponible",
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

                imagePreview?.visibility =
                    View.VISIBLE

                imagePreview?.setImageURI(
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

                imagePreview?.visibility =
                    View.VISIBLE

                imagePreview?.setImageBitmap(
                    bitmap
                )

                Toast.makeText(
                    this,
                    "Photo prise. Pour la conserver, utilisez aussi la galerie si nécessaire.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun afficherPanier() {

        val root = baseLayout()

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

            for (index in panier.indices) {

                val produit =
                    panier[index]

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

                        panier.removeAt(index)

                        sauvegarderPanier()

                        afficherPanier()
                    }
                )

                root.addView(bloc)

                try {

                    total +=
                        produit.prix
                            .replace(
                                " ",
                                ""
                            )
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

                    setTextColor(
                        Color.rgb(20, 100, 50)
                    )

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
                bouton("✅ Passer la commande") {

                    AlertDialog.Builder(this)
                        .setTitle("Commande")
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