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
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class MainActivity : Activity() {

    private var pageAccueilVisible = false

    data class Produit(
        val nom: String,
        val prix: String,
        val categorie: String,
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

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<Produit>()
    private val clients = ArrayList<String>()
    private val commandes = ArrayList<Commande>()

    private lateinit var zoneProduits: LinearLayout
    private lateinit var imagePreview: ImageView

    private var imageProduit = ""

    private val PREFS = "vie_espoir_data"

    private val PRODUCTS_KEY = "products"
    private val CART_KEY = "cart"
    private val CLIENTS_KEY = "clients"
    private val ORDERS_KEY = "orders"

    private val SELLER_NAME = "seller_name"
    private val SELLER_EMAIL = "seller_email"
    private val SELLER_PHONE = "seller_phone"
    private val SELLER_WHATSAPP = "seller_whatsapp"
    private val SELLER_VALIDATED = "seller_validated"

    private val SUBSCRIPTION_ACTIVE = "subscription_active"
    private val SUBSCRIPTION_DATE = "subscription_date"

    private val SUBSCRIPTION_AMOUNT = 5000

    private val CAMERA_REQUEST = 100
    private val GALLERY_REQUEST = 101

    private val prefs by lazy {
        getSharedPreferences(PREFS, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            chargerDonnees()
        } catch (_: Exception) {
            produits.clear()
            panier.clear()
            clients.clear()
            commandes.clear()
        }

        afficherAccueil()
    }

    // =========================================================
    // OUTILS D'AFFICHAGE
    // =========================================================

    private fun layoutBase() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(12, 12, 12, 12)
        setBackgroundColor(Color.rgb(248, 249, 247))
    }

    private fun scroll(v: LinearLayout) =
        ScrollView(this).apply {
            addView(v)
        }

    private fun titre(t: String) =
        TextView(this).apply {
            text = t
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(20, 100, 50))
            setPadding(10, 20, 10, 20)
        }

    private fun bouton(
        t: String,
        action: () -> Unit
    ) =
        Button(this).apply {
            text = t
            textSize = 15f

            setOnClickListener {
                try {
                    action()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Une erreur est survenue.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(5, 5, 5, 5)
                }
        }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil() {

        pageAccueilVisible = true

        val root = layoutBase()

        val header =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

        header.addView(
            TextView(this).apply {
                text = "🦁"
                textSize = 40f
            }
        )

        header.addView(
            TextView(this).apply {
                text = "VIE ESPOIR MARKETING"
                textSize = 22f
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
                        -2,
                        1f
                    )
            }
        )

        header.addView(
            Button(this).apply {
                text = "☰"
                textSize = 22f

                setOnClickListener {
                    afficherMenu()
                }
            }
        )

        root.addView(header)

        val recherche =
            EditText(this).apply {
                hint = "🔎 Rechercher un produit"
                textSize = 16f
                setSingleLine(true)
            }

        root.addView(recherche)

        root.addView(
            bouton("🔎 Rechercher") {
                afficherProduits(
                    recherche.text.toString().trim()
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
                orientation = LinearLayout.VERTICAL
            }

        root.addView(zoneProduits)

        setContentView(scroll(root))

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
                    text = "Aucun produit disponible."
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

            return
        }

        produits.forEach {
            ajouterCarteProduit(
                zoneProduits,
                it
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
                    15,
                    15,
                    15,
                    15
                )

                setBackgroundColor(Color.WHITE)

                layoutParams =
                    LinearLayout.LayoutParams(
                        -1,
                        -2
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
                    ImageView(this).apply {

                        layoutParams =
                            LinearLayout.LayoutParams(
                                -1,
                                280
                            )

                        scaleType =
                            ImageView.ScaleType.CENTER_CROP
                    }

                imageView.setImageURI(
                    Uri.parse(produit.image)
                )

                carte.addView(imageView)

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
                text = "${produit.prix} FCFA"
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
                    this@MainActivity,
                    "Produit ajouté au panier",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        carte.addView(
            bouton("📤 Partager en ligne") {
                partagerProduit(produit)
            }
        )

        parent.addView(carte)
    }

    // =========================================================
    // PARTAGER PRODUIT
    // =========================================================

    private fun partagerProduit(
        produit: Produit
    ) {

        val texte =
            "🛍️ ${produit.nom}\n" +
            "💰 Prix : ${produit.prix} FCFA\n" +
            "🏷️ Catégorie : ${produit.categorie}\n\n" +
            "Découvrez ce produit sur VIE ESPOIR MARKETING."

        try {

            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            texte
                        )
                    },
                    "Partager le produit"
                )
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Impossible de partager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // PRODUITS
    // =========================================================

    private fun afficherProduits(
        rechercheTexte: String
    ) {

        pageAccueilVisible = false

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
                hint = "🔎 Rechercher..."
                setSingleLine(true)
                setText(rechercheTexte)
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

        val liste =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
            }

        root.addView(liste)

        val mot =
            rechercheTexte.lowercase(
                Locale.getDefault()
            )

        var nombre = 0

        produits.forEach {

            val nom =
                it.nom.lowercase(
                    Locale.getDefault()
                )

            val cat =
                it.categorie.lowercase(
                    Locale.getDefault()
                )

            if (
                mot.isEmpty() ||
                nom.contains(mot) ||
                cat.contains(mot)
            ) {

                ajouterCarteProduit(
                    liste,
                    it
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

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        setContentView(scroll(root))
    }

    // =========================================================
    // MENU
    // =========================================================

    private fun afficherMenu() {

        pageAccueilVisible = false

        val choix = arrayOf(
            "🏠 Accueil",
            "🛍 Produits",
            "➕ Ajouter un produit",
            "👨‍💼 Espace vendeur",
            "🛒 Panier",
            "📦 Mes commandes",
            "👥 Clients",
            "📦 Stock",
            "👤 Mon compte",
            "💎 Abonnement vendeur",
            "📤 Partager",
            "📞 Contact / WhatsApp",
            "🚪 Quitter l'application"
        )

        AlertDialog.Builder(this)
            .setTitle(
                "☰ VIE ESPOIR MARKETING"
            )
            .setItems(choix) { _, p ->

                when (p) {

                    0 -> afficherAccueil()

                    1 -> afficherProduits("")

                    2 -> afficherAjouterProduit()

                    3 -> afficherEspaceVendeur()

                    4 -> afficherPanier()

                    5 -> afficherCommandes()

                    6 -> afficherClients()

                    7 -> afficherStock()

                    8 -> afficherCompte()

                    9 -> afficherAbonnement()

                    10 -> partagerApplication()

                    11 -> afficherContact()

                    12 -> confirmerQuitter()
                }
            }
            .show()
    }

    // =========================================================
    // QUITTER
    // =========================================================

    private fun confirmerQuitter() {

        AlertDialog.Builder(this)
            .setTitle(
                "🚪 Quitter l'application"
            )
            .setMessage(
                "Voulez-vous vraiment quitter Vie Espoir Marketing ?"
            )
            .setNegativeButton(
                "ANNULER",
                null
            )
            .setPositiveButton(
                "QUITTER"
            ) { _, _ ->
                finishAffinity()
            }
            .show()
    }

    // =========================================================
    // PARTAGER APPLICATION
    // =========================================================

    private fun partagerApplication() {

        pageAccueilVisible = false

        try {

            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {

                        type = "text/plain"

                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Découvrez VIE ESPOIR MARKETING : produits, commandes et vente en ligne."
                        )
                    },
                    "Partager VIE ESPOIR MARKETING"
                )
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Impossible de partager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // AJOUTER PRODUIT
    // =========================================================

    private fun afficherAjouterProduit() {

        pageAccueilVisible = false

        imageProduit = ""

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
                hint = "Nom du produit"
                textSize = 16f
            }

        val prix =
            EditText(this).apply {
                hint = "Prix en FCFA"
                textSize = 16f
                inputType =
                    android.text.InputType.TYPE_CLASS_NUMBER
            }

        val categorie =
            EditText(this).apply {
                hint = "Catégorie"
                textSize = 16f
            }

        root.addView(nom)
        root.addView(prix)
        root.addView(categorie)

        imagePreview =
            ImageView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        -1,
                        300
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_CROP

                visibility = View.GONE
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

                val n =
                    nom.text
                        .toString()
                        .trim()

                val p =
                    prix.text
                        .toString()
                        .trim()

                val c =
                    categorie.text
                        .toString()
                        .trim()

                if (
                    n.isEmpty() ||
                    p.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Nom et prix obligatoires",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
                }

                produits.add(
                    Produit(
                        nom = n,
                        prix = p,
                        categorie =
                            if (c.isEmpty())
                                "Autres"
                            else
                                c,
                        image = imageProduit
                    )
                )

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

        setContentView(scroll(root))
    }

    // =========================================================
    // CAMERA
    // =========================================================

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

    // =========================================================
    // GALERIE
    // =========================================================

    private fun ouvrirGalerie() {

        try {

            startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ),
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

    // =========================================================
    // PERMISSION CAMERA
    // =========================================================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            results
        )

        if (
            requestCode == CAMERA_REQUEST &&
            results.isNotEmpty() &&
            results[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {

            ouvrirCamera()
        }
    }

    // =========================================================
    // RESULTAT CAMERA / GALERIE
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

        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == GALLERY_REQUEST) {

            val uri = data?.data

            if (uri != null) {

                imageProduit =
                    uri.toString()

                imagePreview.visibility =
                    View.VISIBLE

                imagePreview.setImageURI(uri)
            }

            return
        }

        if (requestCode == CAMERA_REQUEST) {

            val bitmap =
                data?.extras?.get(
                    "data"
                ) as? Bitmap

            if (bitmap != null) {

                imagePreview.visibility =
                    View.VISIBLE

                imagePreview.setImageBitmap(
                    bitmap
                )

                val chemin =
                    sauvegarderPhotoCamera(
                        bitmap
                    )

                if (chemin != null) {

                    imageProduit =
                        chemin

                    Toast.makeText(
                        this,
                        "Photo prise et enregistrée",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Photo prise, mais impossible de la sauvegarder",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // =========================================================
    // SAUVEGARDE PHOTO CAMERA
    // =========================================================

    private fun sauvegarderPhotoCamera(
        bitmap: Bitmap
    ): String? {

        return try {

            val nom =
                "produit_${System.currentTimeMillis()}.jpg"

            val fichier =
                File(
                    filesDir,
                    nom
                )

            val sortie =
                FileOutputStream(fichier)

            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                sortie
            )

            sortie.flush()
            sortie.close()

            Uri.fromFile(fichier).toString()

        } catch (_: Exception) {

            null
        }
    }

    // =========================================================
    // PANIER
    // =========================================================

    private fun afficherPanier() {

        pageAccueilVisible = false

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
                    text =
                        "Votre panier est vide."
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

            panier.forEach { produit ->

                root.addView(
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

                        addView(
                            TextView(
                                this@MainActivity
                            ).apply {
                                text =
                                    produit.nom
                                textSize = 19f
                            }
                        )

                        addView(
                            TextView(
                                this@MainActivity
                            ).apply {
                                text =
                                    "${produit.prix} FCFA"
                                textSize = 17f
                            }
                        )

                        addView(
                            bouton("❌ Retirer") {

                                panier.remove(
                                    produit
                                )

                                sauvegarderPanier()

                                afficherPanier()
                            }
                        )
                    }
                )

                total +=
                    produit.prix
                        .replace(" ", "")
                        .replace(".", "")
                        .toIntOrNull()
                        ?: 0
            }

            root.addView(
                TextView(this).apply {

                    text =
                        "TOTAL : $total FCFA"

                    textSize = 22f

                    gravity =
                        Gravity.CENTER

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    setTextColor(
                        Color.rgb(
                            20,
                            100,
                            50
                        )
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
                bouton(
                    "✅ Passer la commande"
                ) {
                    demanderInfosCommande(
                        total
                    )
                }
            )

            root.addView(
                bouton(
                    "🗑 Vider le panier"
                ) {

                    panier.clear()

                    sauvegarderPanier()

                    afficherPanier()
                }
            )
        }

        setContentView(scroll(root))
    }

    // =========================================================
    // COMMANDE
    // =========================================================

    private fun demanderInfosCommande(
        total: Int
    ) {

        pageAccueilVisible = false

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
                    android.text.InputType.TYPE_CLASS_PHONE
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

                if (
                    client.text
                        .toString()
                        .trim()
                        .isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Nom du client obligatoire",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val nomProduit =
                    panier.joinToString(
                        ", "
                    ) {
                        it.nom
                    }

                commandes.add(
                    Commande(
                        id =
                            System.currentTimeMillis()
                                .toString()
                                .takeLast(6),

                        client =
                            client.text
                                .toString()
                                .trim(),

                        telephone =
                            tel.text
                                .toString()
                                .trim(),

                        produit =
                            nomProduit,

                        montant =
                            total.toString(),

                        statut =
                            "Nouvelle"
                    )
                )

                sauvegarderCommandes()

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

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun afficherCommandes() {

        pageAccueilVisible = false

        val root = layoutBase()

        root.addView(
            titre("📦 Mes commandes")
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        if (commandes.isEmpty()) {

            root.addView(
                TextView(this).apply {
                    text =
                        "Aucune commande en attente."
                    textSize = 18f
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

            commandes.forEach { commande ->

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
                            "📦 Commande #${commande.id}\n" +
                            "👤 ${commande.client}\n" +
                            "📞 ${commande.telephone}\n" +
                            "🛍 ${commande.produit}\n" +
                            "💰 ${commande.montant} FCFA\n" +
                            "📌 Statut : ${commande.statut}"

                        textSize = 16f
                    }
                )

                bloc.addView(
                    bouton(
                        "✅ Accepter / Préparer"
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
                        "❌ Refuser / Annuler"
                    ) {

                        commande.statut =
                            "Annulée"

                        sauvegarderCommandes()

                        afficherCommandes()
                    }
                )

                root.addView(bloc)
            }
        }

        setContentView(scroll(root))
    }

    // =========================================================
    // COMPTE
    // =========================================================

    private fun afficherCompte() {

        pageAccueilVisible = false

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

        val valide =
            prefs.getBoolean(
                SELLER_VALIDATED,
                false
            )

        if (nom.isEmpty()) {

            root.addView(
                TextView(this).apply {
                    text =
                        "Vous n'avez pas encore de compte vendeur."
                    textSize = 17f
                    gravity =
                        Gravity.CENTER
                }
            )

            root.addView(
                bouton(
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

            root.addView(
                TextView(this).apply {

                    text =
                        "Bienvenue $nom 👋\n" +
                        if (valide)
                            "✅ Compte validé"
                        else
                            "⏳ Compte en attente de validation"

                    textSize = 20f

                    gravity =
                        Gravity.CENTER

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    setPadding(
                        10,
                        20,
                        10,
                        15
                    )
                }
            )

            root.addView(
                TextView(this).apply {

                    text =
                        "📧 $email\n" +
                        "📞 $phone\n" +
                        "💬 WhatsApp : $whatsapp"

                    textSize = 16f

                    gravity =
                        Gravity.CENTER
                }
            )

            root.addView(
                bouton(
                    "👨‍💼 Espace vendeur"
                ) {
                    afficherEspaceVendeur()
                }
            )

            root.addView(
                bouton(
                    "💎 Mon abonnement"
                ) {
                    afficherAbonnement()
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
                    "📞 Contact / WhatsApp"
                ) {
                    afficherContact()
                }
            )

            root.addView(
                bouton(
                    "🚪 Se déconnecter"
                ) {

                    prefs.edit()
                        .remove(SELLER_NAME)
                        .remove(SELLER_EMAIL)
                        .remove(SELLER_PHONE)
                        .remove(SELLER_WHATSAPP)
                        .remove(SELLER_VALIDATED)
                        .apply()

                    afficherCompte()
                }
            )
        }

        setContentView(scroll(root))
    }

    // =========================================================
    // CREATION COMPTE
    // =========================================================

    private fun afficherCreationCompte() {

        pageAccueilVisible = false

        val root = layoutBase()

        root.addView(
            titre(
                "➕ Créer mon compte vendeur"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherCompte()
            }
        )

        val nom =
            EditText(this).apply {
                hint = "Nom complet"
            }

        val email =
            EditText(this).apply {
                hint = "Adresse e-mail"
                inputType = 33
            }

        val phone =
            EditText(this).apply {
                hint =
                    "Numéro de téléphone"
                inputType = 3
            }

        val whatsapp =
            EditText(this).apply {
                hint =
                    "Numéro WhatsApp"
            }

        root.addView(nom)
        root.addView(email)
        root.addView(phone)
        root.addView(whatsapp)

        root.addView(
            bouton(
                "✅ Valider mon compte"
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

                    Toast.makeText(
                        this,
                        "Nom, e-mail et téléphone sont obligatoires",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@bouton
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

                Toast.makeText(
                    this,
                    "Compte validé et enregistré",
                    Toast.LENGTH_LONG
                ).show()

                afficherCompte()
            }
        )

        setContentView(scroll(root))
    }

    // =========================================================
    // CONNEXION
    // =========================================================

    private fun afficherConnexion() {

        pageAccueilVisible = false

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
                hint = "Votre e-mail"
                inputType = 33
            }

        root.addView(email)

        root.addView(
            bouton("🔓 Se connecter") {

                val enregistre =
                    prefs.getString(
                        SELLER_EMAIL,
                        ""
                    ) ?: ""

                if (enregistre.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Aucun compte trouvé.",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (
                    email.text
                        .toString()
                        .trim() ==
                    enregistre
                ) {

                    Toast.makeText(
                        this,
                        "Connexion réussie",
                        Toast.LENGTH_SHORT
                    ).show()

                    afficherEspaceVendeur()

                } else {

                    Toast.makeText(
                        this,
                        "E-mail incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        setContentView(scroll(root))
    }

    // =========================================================
    // ESPACE VENDEUR
    // =========================================================

    private fun afficherEspaceVendeur() {

        pageAccueilVisible = false

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

        val valide =
            prefs.getBoolean(
                SELLER_VALIDATED,
                false
            )

        if (nom.isEmpty()) {

            root.addView(
                TextView(this).apply {
                    text =
                        "Vous devez créer un compte vendeur."
                    textSize = 17f
                    gravity =
                        Gravity.CENTER
                }
            )

            root.addView(
                bouton(
                    "👤 Créer mon compte"
                ) {
                    afficherCreationCompte()
                }
            )

        } else {

            root.addView(
                TextView(this).apply {

                    text =
                        "Bonjour $nom 👋\n" +
                        if (valide)
                            "✅ Compte validé"
                        else
                            "⏳ En attente"

                    textSize = 21f

                    gravity =
                        Gravity.CENTER

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )
                }
            )

            root.addView(
                bouton(
                    "💎 Abonnement vendeur"
                ) {
                    afficherAbonnement()
                }
            )

            root.addView(
                bouton(
                    "➕ Ajouter un produit"
                ) {
                    afficherAjouterProduit()
                }
            )

            root.addView(
                bouton(
                    "🛍 Mes produits"
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
                    "📦 Mon stock"
                ) {
                    afficherStock()
                }
            )

            root.addView(
                bouton(
                    "👥 Mes clients"
                ) {
                    afficherClients()
                }
            )

            root.addView(
                bouton(
                    "📞 Contact / WhatsApp"
                ) {
                    afficherContact()
                }
            )
        }

        setContentView(scroll(root))
    }

    // =========================================================
    // ABONNEMENT VENDEUR
    // =========================================================

    private fun afficherAbonnement() {

        pageAccueilVisible = false

        val root = layoutBase()

        root.addView(
            titre(
                "💎 ABONNEMENT VENDEUR"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherEspaceVendeur()
            }
        )

        val actif =
            prefs.getBoolean(
                SUBSCRIPTION_ACTIVE,
                false
            )

        if (actif) {

            val date =
                prefs.getString(
                    SUBSCRIPTION_DATE,
                    ""
                ) ?: ""

            root.addView(
                TextView(this).apply {

                    text =
                        "✅ ABONNEMENT ACTIF\n\n" +
                        "Abonnement mensuel\n" +
                        "5 000 FCFA / mois\n\n" +
                        if (date.isNotEmpty())
                            "Activé le : $date"
                        else
                            ""

                    textSize = 19f

                    gravity =
                        Gravity.CENTER

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    setPadding(
                        10,
                        30,
                        10,
                        30
                    )
                }
            )

        } else {

            root.addView(
                TextView(this).apply {

                    text =
                        "💎 Abonnement mensuel\n\n" +
                        "5 000 FCFA / mois\n\n" +
                        "Cet abonnement permet au vendeur " +
                        "d'utiliser les fonctions de vente " +
                        "de la plateforme."

                    textSize = 19f

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

            root.addView(
                TextView(this).apply {

                    text =
                        "Choisissez votre mode de paiement :"

                    textSize = 17f

                    gravity =
                        Gravity.CENTER

                    setPadding(
                        10,
                        20,
                        10,
                        10
                    )
                }
            )

            val paiement =
                Spinner(this)

            val moyens =
                arrayOf(
                    "Orange Money",
                    "MTN Money",
                    "Moov Money",
                    "Wave"
                )

            paiement.adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    moyens
                )

            root.addView(paiement)

            root.addView(
                bouton(
                    "✅ Activer mon abonnement"
                ) {

                    AlertDialog.Builder(this)
                        .setTitle(
                            "💎 Abonnement mensuel"
                        )
                        .setMessage(
                            "Montant : 5 000 FCFA / mois\n\n" +
                            "Moyen choisi : ${paiement.selectedItem}\n\n" +
                            "Pour le moment, l'application enregistre " +
                            "la demande localement. Aucun paiement réel " +
                            "n'est effectué automatiquement."
                        )
                        .setNegativeButton(
                            "ANNULER",
                            null
                        )
                        .setPositiveButton(
                            "ENREGISTRER"
                        ) { _, _ ->

                            val date =
                                java.text.SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm",
                                    Locale.getDefault()
                                ).format(
                                    java.util.Date()
                                )

                            prefs.edit()
                                .putBoolean(
                                    SUBSCRIPTION_ACTIVE,
                                    true
                                )
                                .putString(
                                    SUBSCRIPTION_DATE,
                                    date
                                )
                                .apply()

                            Toast.makeText(
                                this,
                                "Abonnement enregistré.",
                                Toast.LENGTH_LONG
                            ).show()

                            afficherAbonnement()
                        }
                        .show()
                }
            )
        }

        setContentView(scroll(root))
    }

    // =========================================================
    // CONTACT
    // =========================================================

    private fun afficherContact() {

        pageAccueilVisible = false

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

        val root = layoutBase()

        root.addView(
            titre(
                "📞 Contact VIE ESPOIR MARKETING"
            )
        )

        root.addView(
            bouton("⬅ Retour") {
                afficherAccueil()
            }
        )

        root.addView(
            TextView(this).apply {

                text =
                    "📞 Téléphone : $phone\n" +
                    "💬 WhatsApp : $whatsapp"

                textSize = 18f

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

        root.addView(
            bouton("📞 Appeler") {

                if (phone.isNotEmpty()) {

                    try {

                        startActivity(
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse(
                                    "tel:$phone"
                                )
                            )
                        )

                    } catch (_: Exception) {

                        Toast.makeText(
                            this,
                            "Impossible d'ouvrir le téléphone.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Numéro de téléphone non configuré",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        root.addView(
            bouton("💬 WhatsApp") {

                val numero =
                    whatsapp
                        .replace("+", "")
                        .replace(" ", "")

                if (numero.isNotEmpty()) {

                    try {

                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://wa.me/$numero"
                                )
                            )
                        )

                    } catch (_: Exception) {

                        Toast.makeText(
                            this,
                            "WhatsApp n'est pas disponible",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Numéro WhatsApp non configuré",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        setContentView(scroll(root))
    }

    // =========================================================
    // CLIENTS
    // =========================================================

    private fun afficherClients() {

        pageAccueilVisible = false

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

        clients.forEach {

            root.addView(
                TextView(this).apply {

                    text = "👤 $it"

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

        root.addView(
            bouton(
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
                    .setView(champ)
                    .setNegativeButton(
                        "Annuler",
                        null
                    )
                    .setPositiveButton(
                        "Ajouter"
                    ) { _, _ ->

                        val nomClient =
                            champ.text
                                .toString()
                                .trim()

                        if (
                            nomClient.isNotEmpty()
                        ) {

                            clients.add(
                                nomClient
                            )

                            sauvegarderClients()

                            afficherClients()
                        }
                    }
                    .show()
            }
        )

        setContentView(scroll(root))
    }

    // =========================================================
    // STOCK
    // =========================================================

    private fun afficherStock() {

        pageAccueilVisible = false

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
                        "Aucun produit dans le stock."

                    textSize = 17f

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
        }

        produits.forEach { produit ->

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

            ligne.addView(
                TextView(this).apply {

                    text =
                        "${produit.nom}\n${produit.prix} FCFA"

                    textSize = 16f

                    layoutParams =
                        LinearLayout.LayoutParams(
                            0,
                            -2,
                            1f
                        )
                }
            )

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

        root.addView(
            bouton(
                "➕ Ajouter un produit"
            ) {
                afficherAjouterProduit()
            }
        )

        setContentView(scroll(root))
    }

    // =========================================================
    // BOUTON RETOUR ANDROID
    // =========================================================

    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (!pageAccueilVisible) {

            afficherAccueil()

            return
        }

        confirmerQuitter()
    }

    // =========================================================
    // SAUVEGARDE PRODUITS
    // =========================================================

    private fun sauvegarderProduits() {

        try {

            val a = JSONArray()

            produits.forEach {

                a.put(
                    JSONObject().apply {

                        put(
                            "nom",
                            it.nom
                        )

                        put(
                            "prix",
                            it.prix
                        )

                        put(
                            "categorie",
                            it.categorie
                        )

                        put(
                            "image",
                            it.image
                        )
                    }
                )
            }

            prefs.edit()
                .putString(
                    PRODUCTS_KEY,
                    a.toString()
                )
                .apply()

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // SAUVEGARDE PANIER
    // =========================================================

    private fun sauvegarderPanier() {

        try {

            val a = JSONArray()

            panier.forEach {

                a.put(
                    JSONObject().apply {

                        put(
                            "nom",
                            it.nom
                        )

                        put(
                            "prix",
                            it.prix
                        )

                        put(
                            "categorie",
                            it.categorie
                        )

                        put(
                            "image",
                            it.image
                        )
                    }
                )
            }

            prefs.edit()
                .putString(
                    CART_KEY,
                    a.toString()
                )
                .apply()

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // SAUVEGARDE CLIENTS
    // =========================================================

    private fun sauvegarderClients() {

        try {

            val a = JSONArray()

            clients.forEach {
                a.put(it)
            }

            prefs.edit()
                .putString(
                    CLIENTS_KEY,
                    a.toString()
                )
                .apply()

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // SAUVEGARDE COMMANDES
    // =========================================================

    private fun sauvegarderCommandes() {

        try {

            val a = JSONArray()

            commandes.forEach {

                a.put(
                    JSONObject().apply {

                        put(
                            "id",
                            it.id
                        )

                        put(
                            "client",
                            it.client
                        )

                        put(
                            "telephone",
                            it.telephone
                        )

                        put(
                            "produit",
                            it.produit
                        )

                        put(
                            "montant",
                            it.montant
                        )

                        put(
                            "statut",
                            it.statut
                        )
                    }
                )
            }

            prefs.edit()
                .putString(
                    ORDERS_KEY,
                    a.toString()
                )
                .apply()

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // CHARGEMENT DES DONNÉES
    // =========================================================

    private fun chargerDonnees() {

        produits.clear()
        panier.clear()
        clients.clear()
        commandes.clear()

        // -----------------------------------------------------
        // PRODUITS / PANIER
        // -----------------------------------------------------

        fun lireProduits(
            key: String,
            cible: ArrayList<Produit>
        ) {

            val json =
                prefs.getString(
                    key,
                    null
                ) ?: return

            try {

                val a =
                    JSONArray(json)

                for (
                    i in 0 until a.length()
                ) {

                    val o =
                        a.optJSONObject(i)
                            ?: continue

                    val nom =
                        o.optString(
                            "nom",
                            ""
                        )

                    val prix =
                        o.optString(
                            "prix",
                            ""
                        )

                    val categorie =
                        o.optString(
                            "categorie",
                            "Autres"
                        )

                    val image =
                        o.optString(
                            "image",
                            ""
                        )

                    if (
                        nom.isNotEmpty() &&
                        prix.isNotEmpty()
                    ) {

                        cible.add(
                            Produit(
                                nom,
                                prix,
                                categorie,
                                image
                            )
                        )
                    }
                }

            } catch (_: Exception) {

                // Si les anciennes données
                // sont corrompues, on ignore
                // simplement ces données.
            }
        }

        lireProduits(
            PRODUCTS_KEY,
            produits
        )

        lireProduits(
            CART_KEY,
            panier
        )

        // -----------------------------------------------------
        // CLIENTS
        // -----------------------------------------------------

        try {

            val json =
                prefs.getString(
                    CLIENTS_KEY,
                    "[]"
                ) ?: "[]"

            val a =
                JSONArray(json)

            for (
                i in 0 until a.length()
            ) {

                val client =
                    a.optString(
                        i,
                        ""
                    )

                if (
                    client.isNotEmpty()
                ) {
                    clients.add(client)
                }
            }

        } catch (_: Exception) {
        }

        // -----------------------------------------------------
        // COMMANDES
        // -----------------------------------------------------

        try {

            val json =
                prefs.getString(
                    ORDERS_KEY,
                    "[]"
                ) ?: "[]"

            val a =
                JSONArray(json)

            for (
                i in 0 until a.length()
            ) {

                val o =
                    a.optJSONObject(i)
                        ?: continue

                commandes.add(
                    Commande(

                        id =
                            o.optString(
                                "id",
                                ""
                            ),

                        client =
                            o.optString(
                                "client",
                                ""
                            ),

                        telephone =
                            o.optString(
                                "telephone",
                                ""
                            ),

                        produit =
                            o.optString(
                                "produit",
                                ""
                            ),

                        montant =
                            o.optString(
                                "montant",
                                "0"
                            ),

                        statut =
                            o.optString(
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