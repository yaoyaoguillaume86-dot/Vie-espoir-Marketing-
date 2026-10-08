package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {

    data class Produit(
        var nom: String,
        var prix: Int,
        var stock: Int,
        var imageUri: String = "",
        var vendeur: String = "Vie Espoir",
        var actif: Boolean = true
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<Produit>()
    private val commandes = ArrayList<String>()
    private val clients = ArrayList<String>()

    private lateinit var contenu: LinearLayout
    private var imageProduitChoisie = ""

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

        produits.add(
            Produit(
                "Super7",
                20000,
                20
            )
        )

        produits.add(
            Produit(
                "M7",
                15000,
                15
            )
        )

        produits.add(
            Produit(
                "Women 7",
                15000,
                10
            )
        )

        produits.add(
            Produit(
                "Timoc",
                10000,
                12
            )
        )

        produits.add(
            Produit(
                "Café Royal",
                5000,
                25
            )
        )
    }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil(
        recherche: String = ""
    ) {

        val principal = LinearLayout(this)

        principal.orientation =
            LinearLayout.VERTICAL

        principal.setBackgroundColor(
            Color.WHITE
        )

        // HEADER

        val header = LinearLayout(this)

        header.orientation =
            LinearLayout.HORIZONTAL

        header.gravity =
            Gravity.CENTER_VERTICAL

        header.setPadding(
            12,
            8,
            8,
            8
        )

        // MENU

        val menu = TextView(this)

        menu.text = "☰"

        menu.textSize = 30f

        menu.gravity =
            Gravity.CENTER

        menu.setPadding(
            8,
            4,
            14,
            4
        )

        menu.setOnClickListener {
            afficherMenu()
        }

        // TITRE

        val blocTitre =
            LinearLayout(this)

        blocTitre.orientation =
            LinearLayout.VERTICAL

        val titre =
            TextView(this)

        titre.text =
            "VIE ESPOIR"

        titre.textSize = 21f

        titre.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        titre.setTextColor(
            Color.rgb(0, 110, 75)
        )

        val sousTitre =
            TextView(this)

        sousTitre.text =
            "Votre plateforme de vente"

        sousTitre.textSize =
            13f

        sousTitre.setTextColor(
            Color.DKGRAY
        )

        blocTitre.addView(titre)
        blocTitre.addView(sousTitre)

        header.addView(menu)

        header.addView(
            blocTitre,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        // COMPTE

        val compte =
            TextView(this)

        compte.text = "👤"

        compte.textSize = 27f

        compte.gravity =
            Gravity.CENTER

        compte.setPadding(
            8,
            4,
            8,
            4
        )

        compte.setOnClickListener {
            afficherCompte()
        }

        header.addView(compte)

        principal.addView(header)

        // SCROLL

        val scroll =
            ScrollView(this)

        contenu =
            LinearLayout(this)

        contenu.orientation =
            LinearLayout.VERTICAL

        contenu.setPadding(
            12,
            4,
            12,
            14
        )

        scroll.addView(contenu)

        principal.addView(
            scroll,
            LinearLayout.LayoutParams(
                0,
                0,
                1f
            )
        )

        // NAVIGATION BAS

        val navigation =
            LinearLayout(this)

        navigation.orientation =
            LinearLayout.HORIZONTAL

        navigation.setBackgroundColor(
            Color.rgb(245, 245, 245)
        )

        navigation.setPadding(
            2,
            2,
            2,
            2
        )

        val accueil =
            boutonBas("🏠\nAccueil")

        val produitsBtn =
            boutonBas("🛍️\nProduits")

        val panierBtn =
            boutonBas("🛒\nPanier")

        val compteBtn =
            boutonBas("👤\nCompte")

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

        navigation.addView(
            accueil,
            poidsNavigation()
        )

        navigation.addView(
            produitsBtn,
            poidsNavigation()
        )

        navigation.addView(
            panierBtn,
            poidsNavigation()
        )

        navigation.addView(
            compteBtn,
            poidsNavigation()
        )

        principal.addView(
            navigation
        )

        setContentView(principal)

        afficherAccueilProduits(
            recherche
        )
    }

    // =========================================================
    // PRODUITS SUR L'ACCUEIL
    // =========================================================

    private fun afficherAccueilProduits(
        recherche: String = ""
    ) {

        contenu.removeAllViews()

        val rechercheBox =
            EditText(this)

        rechercheBox.hint =
            "🔎 Rechercher un produit..."

        rechercheBox.textSize =
            15f

        rechercheBox.setSingleLine(true)

        rechercheBox.inputType =
            InputType.TYPE_CLASS_TEXT

        rechercheBox.setPadding(
            16,
            8,
            16,
            8
        )

        if (recherche.isNotEmpty()) {

            rechercheBox.setText(
                recherche
            )
        }

        contenu.addView(
            rechercheBox
        )

        val rechercher =
            boutonPrincipal(
                "🔎 Rechercher"
            )

        rechercher.setOnClickListener {

            afficherAccueil(
                rechercheBox.text
                    .toString()
                    .trim()
            )
        }

        contenu.addView(
            rechercher
        )

        ajouterEspace(8)

        val titre =
            TextView(this)

        titre.text =
            "Nos produits"

        titre.textSize =
            22f

        titre.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        titre.setTextColor(
            Color.rgb(0, 110, 75)
        )

        titre.setPadding(
            4,
            8,
            4,
            10
        )

        contenu.addView(titre)

        val terme =
            recherche.lowercase(
                Locale.getDefault()
            )

        var trouve = false

        produits.forEach { produit ->

            if (!produit.actif)
                return@forEach

            if (produit.stock <= 0)
                return@forEach

            if (
                terme.isNotEmpty() &&
                !produit.nom
                    .lowercase(
                        Locale.getDefault()
                    )
                    .contains(terme)
            ) {
                return@forEach
            }

            trouve = true

            ajouterCarteProduitAccueil(
                produit
            )
        }

        if (!trouve) {

            val vide =
                TextView(this)

            vide.text =
                "Aucun produit disponible."

            vide.textSize =
                18f

            vide.gravity =
                Gravity.CENTER

            vide.setPadding(
                10,
                45,
                10,
                45
            )

            contenu.addView(vide)
        }
    }

    // =========================================================
    // CARTE PRODUIT
    // =========================================================

    private fun ajouterCarteProduitAccueil(
        produit: Produit
    ) {

        val carte =
            LinearLayout(this)

        carte.orientation =
            LinearLayout.VERTICAL

        carte.setPadding(
            12,
            12,
            12,
            12
        )

        carte.setBackgroundColor(
            Color.rgb(248, 249, 249)
        )

        // IMAGE

        val image =
            ImageView(this)

        image.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                220
            )

        image.scaleType =
            ImageView.ScaleType.CENTER_INSIDE

        if (
            produit.imageUri.isNotEmpty()
        ) {

            try {

                image.setImageURI(
                    Uri.parse(
                        produit.imageUri
                    )
                )

            } catch (_: Exception) {

                image.setImageResource(
                    android.R.drawable
                        .ic_menu_gallery
                )
            }

        } else {

            image.setImageResource(
                android.R.drawable
                    .ic_menu_gallery
            )
        }

        // NOM

        val nom =
            TextView(this)

        nom.text =
            produit.nom

        nom.textSize =
            19f

        nom.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        nom.setTextColor(
            Color.rgb(0, 105, 75)
        )

        nom.setPadding(
            4,
            8,
            4,
            2
        )

        // PRIX

        val prix =
            TextView(this)

        prix.text =
            "${produit.prix} FCFA"

        prix.textSize =
            20f

        prix.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        prix.setTextColor(
            Color.rgb(30, 30, 30)
        )

        // INFORMATIONS

        val infos =
            TextView(this)

        infos.text =
            "📦 Stock : ${produit.stock}   •   🏪 ${produit.vendeur}"

        infos.textSize =
            14f

        infos.setTextColor(
            Color.DKGRAY
        )

        // BOUT