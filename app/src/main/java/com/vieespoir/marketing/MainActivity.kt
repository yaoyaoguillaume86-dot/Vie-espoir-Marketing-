package com.vieespoir.marketing

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class MainActivity : Activity() {

    // =========================================================
    // DONNÉES
    // =========================================================

    data class Produit(
        val nom: String,
        val prix: String,
        val categorie: String,
        val image: String = ""
    )

    private val produits = ArrayList<Produit>()
    private val panier = ArrayList<String>()
    private val clients = ArrayList<String>()

    private lateinit var zoneProduits: LinearLayout
    private lateinit var recherche: EditText

    private var categorie = "Tous"
    private var imageProduit: Uri? = null
    private var imagePreview: ImageView? = null

    private val CAMERA = 100
    private val GALERIE = 101
    private val PERMISSION_CAMERA = 102

    private val PREF = "VIE_ESPOIR"
    private val PRODUITS = "produits"
    private val PANIER = "panier"
    private val CLIENTS = "clients"

    private val VENDEUR_NOM = "vendeur_nom"
    private val VENDEUR_TEL = "vendeur_tel"
    private val VENDEUR_MDP = "vendeur_mdp"
    private val VENDEUR_CONNECTE = "vendeur_connecte"

    // =========================================================
    // DÉMARRAGE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerDonnees()
        afficherAccueil()
    }

    private fun prefs() =
        getSharedPreferences(PREF, MODE_PRIVATE)

    // =========================================================
    // SAUVEGARDE
    // =========================================================

    private fun chargerDonnees() {

        produits.clear()
        panier.clear()
        clients.clear()

        val p = prefs().getString(PRODUITS, "")

        if (p.isNullOrEmpty()) {

            produits.add(
                Produit(
                    "Super7",
                    "20 000 FCFA",
                    "Santé"
                )
            )

            produits.add(
                Produit(
                    "M7",
                    "15 000 FCFA",
                    "Santé"
                )
            )

            produits.add(
                Produit(
                    "Women 7",
                    "15 000 FCFA",
                    "Beauté"
                )
            )

            produits.add(
                Produit(
                    "Timoc",
                    "10 000 FCFA",
                    "Santé"
                )
            )

            produits.add(
                Produit(
                    "Café Royal",
                    "5 000 FCFA",
                    "Alimentation"
                )
            )

            sauvegarderProduits()

        } else {

            try {

                val array = JSONArray(p)

                for (i in 0 until array.length()) {

                    val o = array.getJSONObject(i)

                    produits.add(
                        Produit(
                            o.optString("nom"),
                            o.optString("prix"),
                            o.optString("categorie"),
                            o.optString("image")
                        )
                    )
                }

            } catch (_: Exception) {
            }
        }

        val panierJson =
            prefs().getString(PANIER, "[]")

        try {

            val array = JSONArray(panierJson)

            for (i in 0 until array.length()) {
                panier.add(array.getString(i))
            }

        } catch (_: Exception) {
        }

        val clientsJson =
            prefs().getString(CLIENTS, "[]")

        try {

            val array = JSONArray(clientsJson)

            for (i in 0 until array.length()) {
                clients.add(array.getString(i))
            }

        } catch (_: Exception) {
        }
    }

    private fun sauvegarderProduits() {

        val array = JSONArray()

        for (p in produits) {

            array.put(
                JSONObject().apply {

                    put("nom", p.nom)
                    put("prix", p.prix)
                    put("categorie", p.categorie)
                    put("image", p.image)
                }
            )
        }

        prefs()
            .edit()
            .putString(PRODUITS, array.toString())
            .apply()
    }

    private fun sauvegarderPanier() {

        val array = JSONArray()

        panier.forEach {
            array.put(it)
        }

        prefs()
            .edit()
            .putString(PANIER, array.toString())
            .apply()
    }

    private fun sauvegarderClients() {

        val array = JSONArray()

        clients.forEach {
            array.put(it)
        }

        prefs()
            .edit()
            .putString(CLIENTS, array.toString())
            .apply()
    }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil() {

        val principal = LinearLayout(this)

        principal.orientation =
            LinearLayout.VERTICAL

        principal.setBackgroundColor(
            Color.WHITE
        )

        // -------------------------
        // EN-TÊTE
        // -------------------------

        val header = LinearLayout(this)

        header.orientation =
            LinearLayout.HORIZONTAL

        header.gravity =
            Gravity.CENTER_VERTICAL

        header.setPadding(
            8,
            5,
            8,
            5
        )

        val menu = Button(this)

        menu.text = "☰"
        menu.textSize = 27f

        menu.setTextColor(
            Color.rgb(20, 110, 75)
        )

        menu.setBackgroundColor(
            Color.TRANSPARENT
        )

        menu.setOnClickListener {
            afficherMenu()
        }

        val titre = TextView(this)

        titre.text = "🦁 VIE ESPOIR"
        titre.textSize = 21f

        titre.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        titre.setTextColor(
            Color.rgb(20, 110, 75)
        )

        titre.gravity =
            Gravity.CENTER_VERTICAL

        val sousTitre = TextView(this)

        sousTitre.text =
            "Votre plateforme de vente"

        sousTitre.textSize = 13f

        sousTitre.setTextColor(
            Color.DKGRAY
        )

        val blocTitre = LinearLayout(this)

        blocTitre.orientation =
            LinearLayout.VERTICAL

        blocTitre.addView(titre)
        blocTitre.addView(sousTitre)

        val compte = Button(this)

        compte.text = "👤"
        compte.textSize = 23f

        compte.setBackgroundColor(
            Color.TRANSPARENT
        )

        compte.setOnClickListener {
            afficherCompte()
        }

        header.addView(
            menu,
            LinearLayout.LayoutParams(
                55,
                65
            )
        )

        header.addView(
            blocTitre,
            LinearLayout.LayoutParams(
                0,
                65,
                1f
            )
        )

        header.addView(
            compte,
            LinearLayout.LayoutParams(
                55,
                65
            )
        )

        principal.addView(header)

        // -------------------------
        // RECHERCHE
        // -------------------------

        recherche = EditText(this)

        recherche.hint =
            "🔎 Rechercher un produit..."

        recherche.singleLine = true
        recherche.textSize = 16f

        principal.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            ).apply {

                setMargins(
                    12,
                    5,
                    12,
                    5
                )
            }
        )

        recherche.addTextChangedListener(
            object :
                android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    afficherListeProduits()
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )

        // -------------------------
        // CATÉGORIES
        // -------------------------

        val scrollCategories =
            HorizontalScrollView(this)

        val categories =
            LinearLayout(this)

        categories.orientation =
            LinearLayout.HORIZONTAL

        val liste =
            arrayOf(
                "Tous",
                "Santé",
                "Beauté",
                "Alimentation",
                "Mode",
                "Autres"
            )

        for (cat in liste) {

            val bouton =
                petitBouton(cat)

            bouton.setOnClickListener {

                categorie = cat

                afficherListeProduits()
            }

            categories.addView(
                bouton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    50
                ).apply {
                    setMargins(4, 2, 4, 2)
                }
            )
        }

        scrollCategories.addView(
            categories
        )

        principal.addView(
            scrollCategories,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                58
            )
        )

        // -------------------------
        // TITRE PRODUITS
        // -------------------------

        val titreProduits =
            TextView(this)

        titreProduits.text =
            "Produits disponibles"

        titreProduits.textSize = 19f

        titreProduits.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        titreProduits.setTextColor(
            Color.rgb(20, 110, 75)
        )

        titreProduits.setPadding(
            15,
            8,
            10,
            5
        )

        principal.addView(
            titreProduits
        )

        // -------------------------
        // PRODUITS
        // -------------------------

        val scroll =
            ScrollView(this)

        zoneProduits =
            LinearLayout(this)

        zoneProduits.orientation =
            LinearLayout.VERTICAL

        zoneProduits.setPadding(
            10,
            5,
            10,
            10
        )

        scroll.addView(
            zoneProduits
        )

        principal.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // -------------------------
        // BARRE BAS
        // -------------------------

        val bas =
            LinearLayout(this)

        bas.orientation =
            LinearLayout.HORIZONTAL

        bas.setBackgroundColor(
            Color.rgb(235, 235, 235)
        )

        val accueil =
            boutonBas("🏠\nACCUEIL")

        val produitsBtn =
            boutonBas("🛍️\nPRODUITS")

        val panierBtn =
            boutonBas("🛒\nPANIER")

        val compteBtn =
            boutonBas("👤\nCOMPTE")

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

        bas.addView(
            accueil,
            LinearLayout.LayoutParams(
                0,
                72,
                1f
            )
        )

        bas.addView(
            produitsBtn,
            LinearLayout.LayoutParams(
                0,
                72,
                1f
            )
        )

        bas.addView(
            panierBtn,
            LinearLayout.LayoutParams(
                0,
                72,
                1f
            )
        )

        bas.addView(
            compteBtn,
            LinearLayout.LayoutParams(
                0,
                72,
                1f
            )
        )

        principal.addView(
            bas
        )

        setContentView(
            principal
        )

        afficherListeProduits()
    }

    // =========================================================
    // AFFICHER PRODUITS
    // =========================================================

    private fun afficherListeProduits() {

        if (!::zoneProduits.isInitialized)
            return

        zoneProduits.removeAllViews()

        val texte =
            recherche.text.toString()
                .trim()
                .lowercase(
                    Locale.getDefault()
                )

        val liste =
            produits.filter {

                val nomOk =
                    texte.isEmpty() ||
                    it.nom.lowercase(
                        Locale.getDefault()
                    ).contains(texte)

                val catOk =
                    categorie == "Tous" ||
                    it.categorie.equals(
                        categorie,
                        true
                    )

                nomOk && catOk
            }

        if (liste.isEmpty()) {

            val vide =
                TextView(this)

            vide.text =
                "Aucun produit trouvé."

            vide.textSize = 18f
            vide.gravity =
                Gravity.CENTER

            zoneProduits.addView(
                vide
            )

            return
        }

        for (produit in liste) {

            zoneProduits.addView(
                carteProduit(produit)
            )
        }
    }

    // =========================================================
    // CARTE PRODUIT
    // =========================================================

    private fun carteProduit(
        produit: Produit
    ): LinearLayout {

        val carte =
            LinearLayout(this)

        carte.orientation =
            LinearLayout.HORIZONTAL

        carte.setPadding(
            8,
            8,
            8,
            8
        )

        carte.setBackgroundColor(
            Color.rgb(
                245,
                247,
                246
            )
        )

        val image =
            ImageView(this)

        image.setBackgroundColor(
            Color.rgb(
                225,
                235,
                230
            )
        )

        image.scaleType =
            ImageView.ScaleType.CENTER_CROP

        if (produit.image.isNotEmpty()) {

            try {

                image.setImageURI(
                    Uri.parse(
                        produit.image
                    )
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

        carte.addView(
            image,
            LinearLayout.LayoutParams(
                105,
                105
            ).apply {
                setMargins(
                    3,
                    3,
                    10,
                    3
                )
            }
        )

        val infos =
            LinearLayout(this)

        infos.orientation =
            LinearLayout.VERTICAL

        val nom =
            TextView(this)

        nom.text =
            produit.nom

        nom.textSize = 18f

        nom.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        nom.setTextColor(
            Color.rgb(
                20,
                100,
                70
            )
        )

        val prix =
            TextView(this)

        prix.text =
            produit.prix

        prix.textSize = 16f

        val cat =
            TextView(this)

        cat.text =
            produit.categorie

        cat.textSize = 13f

        cat.setTextColor(
            Color.GRAY
        )

        val acheter =
            Button(this)

        acheter.text =
            "🛒 Ajouter au panier"

        acheter.setTextColor(
            Color.WHITE
        )

        acheter.setBackgroundColor(
            Color.rgb(
                20,
                110,
                75
            )
        )

        acheter.setOnClickListener {

            ajouterAuPanier(
                produit
            )
        }

        infos.addView(nom)
        infos.addView(prix)
        infos.addView(cat)
        infos.addView(acheter)

        carte.addView(
            infos,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        return carte
    }

    // =========================================================
    // PAGE PRODUITS
    // =========================================================

    private fun afficherProduits() {

        val page =
            page("🛍️ PRODUITS")

        val ajouter =
            boutonPrincipal(
                "➕ AJOUTER UN PRODUIT"
            )

        ajouter.setOnClickListener {

            ajouterProduit()
        }

        page.addView(
            ajouter
        )

        for (produit in produits) {

            page.addView(
                carteProduit(produit)
            )
        }

        val retour =
            boutonPrincipal(
                "← RETOUR À L'ACCUEIL"
            )

        retour.setOnClickListener {
            afficherAccueil()
        }

        page.addView(
            retour
        )

        setContentView(page)
    }

    // =========================================================
    // AJOUTER PRODUIT
    // =========================================================

    private fun ajouterProduit() {

        if (!vendeurConnecte()) {

            AlertDialog.Builder(this)
                .setTitle(
                    "🏪 Espace vendeur"
                )
                .setMessage(
                    "Créez ou ouvrez votre compte vendeur avant de publier."
                )
                .setPositiveButton(
                    "Créer un compte"
                ) { _, _ ->

                    creerCompteVendeur()
                }
                .setNeutralButton(
                    "Connexion"
                ) { _, _ ->

                    connexionVendeur()
                }
                .setNegativeButton(
                    "Annuler",
                    null
                )
                .show()

            return
        }

        imageProduit = null

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            15,
            5,
            15,
            5
        )

        imagePreview =
            ImageView(this)

        imagePreview!!.setImageResource(
            android.R.drawable.ic_menu_gallery
        )

        imagePreview!!.setBackgroundColor(
            Color.rgb(
                225,
                235,
                230
            )
        )

        imagePreview!!.scaleType =
            ImageView.ScaleType.CENTER_CROP

        layout.addView(
            imagePreview,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                190
            )
        )

        val photos =
            LinearLayout(this)

        photos.orientation =
            LinearLayout.HORIZONTAL

        val camera =
            Button(this)

        camera.text =
            "📷 CAMÉRA"

        camera.setOnClickListener {
            ouvrirCamera()
        }

        val galerie =
            Button(this)

        galerie.text =
            "🖼️ GALERIE"

        galerie.setOnClickListener {
            ouvrirGalerie()
        }

        photos.addView(
            camera,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        photos.addView(
            galerie,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        layout.addView(
            photos
        )

        val nom =
            EditText(this)

        nom.hint =
            "Nom du produit"

        layout.addView(
            nom
        )

        val prix =
            EditText(this)

        prix.hint =
            "Prix"

        layout.addView(
            prix
        )

        val categorie =
            EditText(this)

        categorie.hint =
            "Catégorie"

        layout.addView(
            categorie
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "➕ AJOUTER UN PRODUIT"
                )
                .setView(layout)
                .setPositiveButton(
                    "Publier",
                    null
                )
                .setNegativeButton(
                    "Annuler",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val n =
                    nom.text.toString().trim()

                val p =
                    prix.text.toString().trim()

                val c =
                    categorie.text.toString().trim()

                if (n.isEmpty()) {

                    nom.error =
                        "Entrez le nom"

                    return@setOnClickListener
                }

                produits.add(
                    Produit(
                        n,
                        if (p.isEmpty())
                            "Prix à définir"
                        else
                            p,
                        if (c.isEmpty())
                            "Autres"
                        else
                            c,
                        imageProduit?.toString()
                            ?: ""
                    )
                )

                sauvegarderProduits()

                dialog.dismiss()

                Toast.makeText(
                    this,
                    "Produit ajouté avec succès.",
                    Toast.LENGTH_SHORT
                ).show()

                afficherAccueil()
            }
        }

        dialog.show()
    }

    // =========================================================
    // PANIER
    // =========================================================

    private fun ajouterAuPanier(
        produit: Produit
    ) {

        panier.add(
            produit.nom
        )

        sauvegarderPanier()

        Toast.makeText(
            this,
            "${produit.nom} ajouté au panier.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun afficherPanier() {

        val page =
            page("🛒 MON PANIER")

        if (panier.isEmpty()) {

            page.addView(
                info(
                    "Votre panier est vide."
                )
            )

        } else {

            for (i in panier.indices) {

                val ligne =
                    LinearLayout(this)

                ligne.orientation =
                    LinearLayout.HORIZONTAL

                val nom =
                    TextView(this)

                nom.text =
                    "${i + 1}. ${panier[i]}"

                nom.textSize = 17f

                val supprimer =
                    Button(this)

                supprimer.text =
                    "Supprimer"

                supprimer.setOnClickListener {

                    panier.removeAt(i)

                    sauvegarderPanier()

                    afficherPanier()
                }

                ligne.addView(
                    nom,
                    LinearLayout.LayoutParams(
                        0,
                        60,
                        1f
                    )
                )

                ligne.addView(
                    supprimer
                )

                page.addView(
                    ligne
                )
            }

            val commander =
                boutonPrincipal(
                    "✅ PASSER LA COMMANDE"
                )

            commander.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Commande")
                    .setMessage(
                        "Confirmer votre commande ?"
                    )
                    .setPositiveButton(
                        "Confirmer"
                    ) { _, _ ->

                        panier.clear()

                        sauvegarderPanier()

                        Toast.makeText(
                            this,
                            "Commande enregistrée.",
                            Toast.LENGTH_SHORT
                        ).show()

                        afficherAccueil()
                    }
                    .setNegativeButton(
                        "Annuler",
                        null
                    )
                    .show()
            }

            page.addView(
                commander
            )
        }

        val retour =
            boutonPrincipal(
                "← RETOUR"
            )

        retour.setOnClickListener {
            afficherAccueil()
        }

        page.addView(
            retour
        )

        setContentView(page)
    }

    // =========================================================
    // COMPTE
    // =========================================================

    private fun afficherCompte() {

        if (vendeurConnecte()) {

            AlertDialog.Builder(this)
                .setTitle(
                    "👤 MON COMPTE"
                )
                .setItems(
                    arrayOf(
                        "🏪 Espace vendeur",
                        "➕ Ajouter un produit",
                        "📦 Mes produits",
                        "🚪 Déconnexion"
                    )
                ) { _, choix ->

                    when (choix) {

                        0 ->
                            afficherEspaceVendeur()

                        1 ->
                            ajouterProduit()

                        2 ->
                            afficherProduits()

                        3 -> {

                            prefs()
                                .edit()
                                .putBoolean(
                                    VENDEUR_CONNECTE,
                                    false
                                )
                                .apply()

                            afficherAccueil()
                        }
                    }
                }
                .show()

        } else {

            AlertDialog.Builder(this)
                .setTitle(
                    "👤 COMPTE"
                )
                .setItems(
                    arrayOf(
                        "🔐 Connexion vendeur",
                        "📝 Créer un compte vendeur",
                        "🛍️ Continuer comme client"
                    )
                ) { _, choix ->

                    when (choix) {

                        0 ->
                            connexionVendeur()

                        1 ->
                            creerCompteVendeur()

                        2 ->
                            afficherAccueil()
                    }
                }
                .show()
        }
    }

    // =========================================================
    // COMPTE VENDEUR
    // =========================================================

    private fun vendeurConnecte(): Boolean {

        return prefs().getBoolean(
            VENDEUR_CONNECTE,
            false
        ) &&
                prefs()
                    .getString(
                        VENDEUR_NOM,
                        ""
                    )
                    .orEmpty()
                    .isNotEmpty()
    }

    private fun creerCompteVendeur() {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        val nom =
            EditText(this)

        nom.hint =
            "Nom du vendeur"

        val telephone =
            EditText(this)

        telephone.hint =
            "Téléphone"

        telephone.inputType =
            android.text.InputType.TYPE_CLASS_PHONE

        val mdp =
            EditText(this)

        mdp.hint =
            "Mot de passe"

        mdp.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        layout.addView(nom)
        layout.addView(telephone)
        layout.addView(mdp)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "📝 CRÉER UN COMPTE VENDEUR"
                )
                .setView(layout)
                .setPositiveButton(
                    "Créer",
                    null
                )
                .setNegativeButton(
                    "Annuler",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val n =
                    nom.text.toString().trim()

                val tel =
                    telephone.text.toString().trim()

                val pass =
                    mdp.text.toString()

                if (
                    n.isEmpty() ||
                    tel.isEmpty() ||
                    pass.length < 4
                ) {

                    Toast.makeText(
                        this,
                        "Remplissez tous les champs. Mot de passe minimum 4 caractères.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setOnClickListener
                }

                prefs()
                    .edit()
                    .putString(
                        VENDEUR_NOM,
                        n
                    )
                    .putString(
                        VENDEUR_TEL,
                        tel
                    )
                    .putString(
                        VENDEUR_MDP,
                        pass
                    )
                    .putBoolean(
                        VENDEUR_CONNECTE,
                        true
                    )
                    .apply()

                dialog.dismiss()

                Toast.makeText(
                    this,
                    "Compte vendeur créé.",
                    Toast.LENGTH_SHORT
                ).show()

                afficherEspaceVendeur()
            }
        }

        dialog.show()
    }

    private fun connexionVendeur() {

        val nom =
            prefs()
                .getString(
                    VENDEUR_NOM,
                    ""
                )
                .orEmpty()

        if (nom.isEmpty()) {

            creerCompteVendeur()

            return
        }

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        val tel =
            EditText(this)

        tel.hint =
            "Téléphone"

        val mdp =
            EditText(this)

        mdp.hint =
            "Mot de passe"

        mdp.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        layout.addView(tel)
        layout.addView(mdp)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "🔐 CONNEXION VENDEUR"
                )
                .setView(layout)
                .setPositiveButton(
                    "Connexion",
                    null
                )
                .setNegativeButton(
                    "Annuler",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val bonTel =
                    tel.text.toString().trim() ==
                            prefs().getString(
                                VENDEUR_TEL,
                                ""
                            )

                val bonMdp =
                    mdp.text.toString() ==
                            prefs().getString(
                                VENDEUR_MDP,
                                ""
                            )

                if (!bonTel || !bonMdp) {

                    Toast.makeText(
                        this,
                        "Téléphone ou mot de passe incorrect.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setOnClickListener
                }

                prefs()
                    .edit()
                    .putBoolean(
                        VENDEUR_CONNECTE,
                        true
                    )
                    .apply()

                dialog.dismiss()

                afficherEspaceVendeur()
            }
        }

        dialog.show()
    }

    // =========================================================
    // ESPACE VENDEUR
    // =========================================================

    private fun afficherEspaceVendeur() {

        if (!vendeurConnecte()) {

            connexionVendeur()

            return
        }

        val page =
            page(
                "🏪 ESPACE VENDEUR"
            )

        val nom =
            prefs()
                .getString(
                    VENDEUR_NOM,
                    "Vendeur"
                )

        val tel =
            prefs()
                .getString(
                    VENDEUR_TEL,
                    ""
                )

        page.addView(
            info(
                "👤 $nom\n📱 $tel\n\nVotre espace vendeur est actif."
            )
        )

        val ajouter =
            boutonPrincipal(
                "➕ AJOUTER UN PRODUIT"
            )

        ajouter.setOnClickListener {
            ajouterProduit()
        }

        page.addView(
            ajouter
        )

        val mesProduits =
            boutonPrincipal(
                "📦 MES PRODUITS"
            )

        mesProduits.setOnClickListener {
            afficherProduits()
        }

        page.addView(
            mesProduits
        )

        val stock =
            boutonPrincipal(
                "📊 MON STOCK"
            )

        stock.setOnClickListener {
            afficherStock()
        }

        page.addView(
            stock
        )

        val commandes =
            boutonPrincipal(
                "📦 MES COMMANDES"
            )

        commandes.setOnClickListener {

            Toast.makeText(
                this,
                "Aucune commande pour le moment.",
                Toast.LENGTH_SHORT
            ).show()
        }

        page.addView(
            commandes
        )

        val deconnexion =
            boutonPrincipal(
                "🚪 DÉCONNEXION"
            )

        deconnexion.setOnClickListener {

            prefs()
                .edit()
                .putBoolean(
                    VENDEUR_CONNECTE,
                    false
                )
                .apply()

            afficherAccueil()
        }

        page.addView(
            deconnexion
        )

        setContentView(page)
    }

    // =========================================================
    // MENU
    // =========================================================

    private fun afficherMenu() {

        val choix =
            arrayOf(
                "🛍️ Produits",
                "➕ Ajouter un produit",
                "🏪 Espace vendeur",
                "🛒 Panier",
                "👥 Clients",
                "📊 Stock",
                "👤 Compte"
            )

        AlertDialog.Builder(this)
            .setTitle(
                "☰ MENU VIE ESPOIR"
            )
            .setItems(
                choix
            ) { _, position ->

                when (position) {

                    0 ->
                        afficherProduits()

                    1 ->
                        ajouterProduit()

                    2 ->
                        afficherEspaceVendeur()

                    3 ->
                        afficherPanier()

                    4 ->
                        afficherClients()

                    5 ->
                        afficherStock()

                    6 ->
                        afficherCompte()
                }
            }
            .show()
    }

    // =========================================================
    // CLIENTS
    // =========================================================

    private fun afficherClients() {

        val page =
            page("👥 CLIENTS")

        if (clients.isEmpty()) {

            page.addView(
                info(
                    "Aucun client enregistré."
                )
            )

        } else {

            clients.forEachIndexed {
                    index,
                    client ->

                page.addView(
                    info(
                        "${index + 1}. $client"
                    )
                )
            }
        }

        val ajouter =
            boutonPrincipal(
                "➕ AJOUTER UN CLIENT"
            )

        ajouter.setOnClickListener {
            ajouterClient()
        }

        page.addView(
            ajouter
        )

        val retour =
            boutonPrincipal(
                "← RETOUR"
            )

        retour.setOnClickListener {
            afficherAccueil()
        }

        page.addView(
            retour
        )

        setContentView(page)
    }

    private fun ajouterClient() {

        val champ =
            EditText(this)

        champ.hint =
            "Nom du client"

        AlertDialog.Builder(this)
            .setTitle(
                "Ajouter un client"
            )
            .setView(champ)
            .setPositiveButton(
                "Ajouter"
            ) { _, _ ->

                val nom =
                    champ.text.toString().trim()

                if (nom.isNotEmpty()) {

                    clients.add(nom)

                    sauvegarderClients()

                    afficherClients()
                }
            }
            .setNegativeButton(
                "Annuler",
                null
            )
            .show()
    }

    // =========================================================
    // STOCK
    // =========================================================

    private fun afficherStock() {

        val page =
            page("📊 GESTION DU STOCK")

        page.addView(
            info(
                "Nombre de produits : ${produits.size}"
            )
        )

        produits.forEach {

            page.addView(
                info(
                    "📦 ${it.nom}\n💰 ${it.prix}\n📂 ${it.categorie}"
                )
            )
        }

        val retour =
            boutonPrincipal(
                "← RETOUR"
            )

        retour.setOnClickListener {
            afficherAccueil()
        }

        page.addView(
            retour
        )

        setContentView(page)
    }

    // =========================================================
    // CAMÉRA
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
                PERMISSION_CAMERA
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
                CAMERA
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Caméra indisponible.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // GALERIE
    // =========================================================

    private fun ouvrirGalerie() {

        try {

            val intent =
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                )

            intent.type =
                "image/*"

            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            )

            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )

            startActivityForResult(
                intent,
                GALERIE
            )

        } catch (_: Exception) {

            val intent =
                Intent(
                    Intent.ACTION_PICK
                )

            intent.type =
                "image/*"

            startActivityForResult(
                intent,
                GALERIE
            )
        }
    }

    // =========================================================
    // RÉSULTAT CAMÉRA / GALERIE
    // =========================================================

    @Deprecated("Compatibilité Android")
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
        )
            return

        when (requestCode) {

            CAMERA -> {

                val bitmap =
                    data.extras?.get(
                        "data"
                    ) as? android.graphics.Bitmap

                if (bitmap != null) {

                    imagePreview?.setImageBitmap(
                        bitmap
                    )

                    Toast.makeText(
                        this,
                        "Photo prise.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            GALERIE -> {

                val uri =
                    data.data

                if (uri != null) {

                    imageProduit =
                        uri

                    imagePreview?.setImageURI(
                        uri
                    )

                    try {

                        contentResolver
                            .takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )

                    } catch (_: Exception) {
                    }

                    Toast.makeText(
                        this,
                        "Photo sélectionnée.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // =========================================================
    // PERMISSION CAMÉRA
    // =========================================================

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
            PERMISSION_CAMERA
        ) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                ouvrirCamera()

            } else {

                Toast.makeText(
                    this,
                    "Autorisation caméra refusée.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // =========================================================
    // OUTILS INTERFACE
    // =========================================================

    private fun page(
        titre: String
    ): LinearLayout {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            14,
            14,
            14,
            14
        )

        layout.setBackgroundColor(
            Color.WHITE
        )

        val t =
            TextView(this)

        t.text =
            titre

        t.textSize = 23f

        t.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        t.setTextColor(
            Color.rgb(
                20,
                110,
                75
            )
        )

        t.gravity =
            Gravity.CENTER

        t.setPadding(
            5,
            12,
            5,
            20
        )

        layout.addView(
            t
        )

        return layout
    }

    private fun info(
        texte: String
    ): TextView {

        return TextView(this).apply {

            text = texte

            textSize = 17f

            setTextColor(
                Color.DKGRAY
            )

            setBackgroundColor(
                Color.rgb(
                    245,
                    247,
                    246
                )
            )

            setPadding(
                18,
                16,
                18,
                16
            )

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

    private fun boutonPrincipal(
        texte: String
    ): Button {

        return Button(this).apply {

            text = texte

            textSize = 15f

            setTextColor(
                Color.WHITE
            )

            setBackgroundColor(
                Color.rgb(
                    20,
                    110,
                    75
                )
            )

            setPadding(
                12,
                8,
                12,
                8
            )

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {

                    setMargins(
                        4,
                        6,
                        4,
                        6
                    )
                }
        }
    }

    private fun petitBouton(
        texte: String
    ): Button {

        return Button(this).apply {

            text = texte

            textSize = 13f

            setTextColor(
                Color.WHITE
            )

            setBackgroundColor(
                Color.rgb(
                    20,
                    110,
                    75
                )
            )

            setPadding(
                12,
                2,
                12,
                2
            )
        }
    }

    private fun boutonBas(
        texte: String
    ): Button {

        return Button(this).apply {

            text = texte

            textSize = 12f

            setTextColor(
                Color.DKGRAY
            )

            setBackgroundColor(
                Color.rgb(
                    235,
                    235,
                    235
                )
            )

            gravity =
                Gravity.CENTER

            setPadding(
                1,
                1,
                1,
                1
            )
        }
    }

    // =========================================================
    // RETOUR
    // =========================================================

    override fun onBackPressed() {

        afficherAccueil()
    }
}