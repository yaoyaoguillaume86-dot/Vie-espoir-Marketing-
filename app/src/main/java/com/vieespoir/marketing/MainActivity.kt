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
    private lateinit var categorie: Spinner

    private var imageProduit = ""
    private var imagePreview: ImageView? = null

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
            setOnClickListener { action() }

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
        }
    }

    private fun titre(texte: String): TextView {
        return TextView(this).apply {
            text = texte
            textSize = 22f
            setTextColor(Color.rgb(20, 90, 45))
            gravity = Gravity.CENTER
            setPadding(10, 20, 10, 20)
        }
    }

    private fun baseLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(couleurFond())
            setPadding(12, 10, 12, 10)
        }
    }

    private fun scroll(layout: LinearLayout): ScrollView {
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
            setTextColor(Color.rgb(20, 100, 50))
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
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

        recherche = EditText(this)
        recherche.hint = "🔎 Rechercher un produit..."
        recherche.textSize = 16f
        recherche.setSingleLine(true)
        recherche.setPadding(20, 10, 20, 10)

        root.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        recherche.setOnEditorActionListener { _, _, _ ->
            afficherProduits()
            true
        }

        val btnRecherche = bouton("🔎 Rechercher") {
            afficherProduits()
        }

        root.addView(btnRecherche)

        val categories = arrayOf(
            "Toutes",
            "Beauté",
            "Santé",
            "Alimentation",
            "Maison",
            "Autres"
        )

        categorie = Spinner(this)
        categorie.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        root.addView(categorie)

        zoneProduits = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(zoneProduits)

        val navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val accueil = bouton("🏠 Accueil") {
            afficherAccueil()
        }

        val produitsBtn = bouton("🛍 Produits") {
            afficherProduits()
        }

        val panierBtn = bouton("🛒 Panier") {
            afficherPanier()
        }

        val compteBtn = bouton("👤 Compte") {
            afficherCompte()
        }

        navigation.addView(accueil)
        navigation.addView(produitsBtn)
        navigation.addView(panierBtn)
        navigation.addView(compteBtn)

        root.addView(navigation)

        setContentView(scroll(root))

        afficherProduitsDansAccueil()
    }

    private fun afficherProduitsDansAccueil() {

        if (!::zoneProduits.isInitialized) return

        zoneProduits.removeAllViews()

        val titreProduits = TextView(this).apply {
            text = "🛍 Produits disponibles"
            textSize = 21f
            setTextColor(Color.rgb(20, 90, 45))
            setPadding(5, 20, 5, 10)
        }

        zoneProduits.addView(titreProduits)

        if (produits.isEmpty()) {

            val vide = TextView(this).apply {
                text = "Aucun produit pour le moment.\nAjoutez votre premier produit."
                textSize = 17f
                gravity = Gravity.CENTER
                setPadding(10, 30, 10, 30)
            }

            zoneProduits.addView(vide)

            zoneProduits.addView(
                bouton("➕ Ajouter un produit") {
                    afficherAjouterProduit()
                }
            )

            return
        }

        for (produit in produits) {
            ajouterCarteProduit(zoneProduits, produit)
        }
    }

    private fun ajouterCarteProduit(
        parent: LinearLayout,
        produit: Produit
    ) {

        val carte = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.WHITE)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(5, 8, 5, 8)
            }
        }

        val nom = TextView(this).apply {
            text = produit.nom
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.rgb(25, 25, 25))
        }

        val prix = TextView(this).apply {
            text = "Prix : ${produit.prix} FCFA"
            textSize = 17f
            setTextColor(Color.rgb(20, 100, 50))
        }

        val cat = TextView(this).apply {
            text = "Catégorie : ${produit.categorie}"
            textSize = 15f
        }

        carte.addView(nom)
        carte.addView(prix)
        carte.addView(cat)

        if (produit.image.isNotEmpty()) {
            try {
                val uri = Uri.parse(produit.image)
                val image = ImageView(this)

                image.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )

                image.scaleType = ImageView.ScaleType.CENTER_CROP
                image.setImageURI(uri)

                carte.addView(image, 0)

            } catch (_: Exception) {
            }
        }

        val ajouter = bouton("🛒 Ajouter au panier") {
            panier.add(produit)
            sauvegarderPanier()

            Toast.makeText(
                this,
                "${produit.nom} ajouté au panier",
                Toast.LENGTH_SHORT
            ).show()
        }

        carte.addView(ajouter)

        parent.addView(carte)
    }

    private fun afficherProduits() {

        val root = baseLayout()

        root.addView(titre("🛍 Tous les produits"))

        val retour = bouton("⬅ Retour") {
            afficherAccueil()
        }

        root.addView(retour)

        val rechercheLocale = EditText(this).apply {
            hint = "🔎 Rechercher..."
            setSingleLine(true)
        }

        root.addView(rechercheLocale)

        val liste = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(liste)

        fun actualiser() {

            liste.removeAllViews()

            val mot = rechercheLocale.text.toString()
                .trim()
                .lowercase(Locale.getDefault())

            for (produit in produits) {

                val ok = mot.isEmpty() ||
                        produit.nom.lowercase(Locale.getDefault()).contains(mot) ||
                        produit.categorie.lowercase(Locale.getDefault()).contains(mot)

                if (ok) {
                    ajouterCarteProduit(liste, produit)
                }
            }

            if (liste.childCount == 0) {
                liste.addView(
                    TextView(this).apply {
                        text = "Aucun produit trouvé."
                        textSize = 18f
                        gravity = Gravity.CENTER
                        setPadding(10, 30, 10, 30)
                    }
                )
            }
        }

        rechercheLocale.setOnEditorActionListener { _, _, _ ->
            actualiser()
            true
        }

        root.addView(
            bouton("🔎 Rechercher") {
                actualiser()
            }
        )

        root.addView(
            bouton("➕ Ajouter un produit") {
                afficherAjouterProduit()
            }
        )

        actualiser()

        setContentView(scroll(root))
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