package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private val clients = ArrayList<String>()

    private data class Produit(
        val nom: String,
        val prix: String,
        val categorie: String
    )

    private val produits = ArrayList<Produit>()

    private lateinit var zoneProduits: GridLayout
    private lateinit var recherche: EditText

    private var categorieSelectionnee = "Tous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Produits de départ
        if (produits.isEmpty()) {
            produits.add(Produit("Super7", "20 000 FCFA", "Santé"))
            produits.add(Produit("M7", "15 000 FCFA", "Santé"))
            produits.add(Produit("Women 7", "15 000 FCFA", "Beauté"))
            produits.add(Produit("Timoc", "10 000 FCFA", "Santé"))
            produits.add(Produit("Café Royal", "5 000 FCFA", "Alimentation"))
        }

        afficherAccueil()
    }

    // ============================================================
    // ACCUEIL
    // ============================================================

    private fun afficherAccueil() {

        val principal = LinearLayout(this)
        principal.orientation = LinearLayout.VERTICAL
        principal.setBackgroundColor(Color.WHITE)

        // --------------------------------------------------------
        // BARRE DU HAUT
        // --------------------------------------------------------

        val barreHaut = LinearLayout(this)
        barreHaut.orientation = LinearLayout.HORIZONTAL
        barreHaut.gravity = Gravity.CENTER_VERTICAL
        barreHaut.setPadding(10, 8, 10, 8)

        val menu = Button(this)
        menu.text = "☰"
        menu.textSize = 25f
        menu.setTextColor(Color.rgb(20, 110, 75))
        menu.setBackgroundColor(Color.TRANSPARENT)

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 19f
        titre.setTextColor(Color.rgb(20, 110, 75))
        titre.gravity = Gravity.CENTER

        val panier = Button(this)
        panier.text = "🛒"
        panier.textSize = 22f
        panier.setTextColor(Color.rgb(20, 110, 75))
        panier.setBackgroundColor(Color.TRANSPARENT)

        barreHaut.addView(
            menu,
            LinearLayout.LayoutParams(55, 60)
        )

        barreHaut.addView(
            titre,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        barreHaut.addView(
            panier,
            LinearLayout.LayoutParams(55, 60)
        )

        principal.addView(barreHaut)

        // Menu caché
        menu.setOnClickListener {
            afficherMenu()
        }

        panier.setOnClickListener {
            afficherPanier()
        }

        // --------------------------------------------------------
        // RECHERCHE
        // --------------------------------------------------------

        recherche = EditText(this)

        recherche.hint = "🔍 Rechercher un produit..."
        recherche.textSize = 17f
        recherche.setSingleLine(true)
        recherche.setPadding(20, 5, 20, 5)

        principal.addView(
            recherche,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                setMargins(15, 5, 15, 5)
            }
        )

        recherche.addTextChangedListener(object : TextWatcher {

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

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // --------------------------------------------------------
        // CATÉGORIES
        // --------------------------------------------------------

        val titreCategorie = TextView(this)
        titreCategorie.text = "Catégories"
        titreCategorie.textSize = 19f
        titreCategorie.setTextColor(Color.rgb(20, 110, 75))
        titreCategorie.setPadding(15, 10, 10, 5)

        principal.addView(titreCategorie)

        val categoriesScroll = HorizontalScrollView(this)

        val categories = LinearLayout(this)
        categories.orientation = LinearLayout.HORIZONTAL
        categories.setPadding(10, 5, 10, 5)

        val listeCategories = arrayOf(
            "Tous",
            "Santé",
            "Beauté",
            "Alimentation",
            "Mode",
            "Autres"
        )

        for (categorie in listeCategories) {

            val bouton = Button(this)
            bouton.text = categorie
            bouton.textSize = 14f
            bouton.setTextColor(Color.WHITE)
            bouton.setBackgroundColor(Color.rgb(20, 110, 75))

            bouton.setOnClickListener {

                categorieSelectionnee = categorie

                afficherListeProduits()
            }

            categories.addView(
                bouton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    50
                ).apply {
                    setMargins(5, 3, 5, 3)
                }
            )
        }

        categoriesScroll.addView(categories)

        principal.addView(
            categoriesScroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // --------------------------------------------------------
        // TITRE PRODUITS
        // --------------------------------------------------------

        val titreProduits = TextView(this)
        titreProduits.text = "Produits"
        titreProduits.textSize = 21f
        titreProduits.setTextColor(Color.rgb(20, 110, 75))
        titreProduits.setPadding(15, 10, 10, 5)

        principal.addView(titreProduits)

        // --------------------------------------------------------
        // ZONE DES PRODUITS
        // --------------------------------------------------------

        val scrollProduits = ScrollView(this)

        zoneProduits = GridLayout(this)
        zoneProduits.columnCount = 2
        zoneProduits.setPadding(10, 5, 10, 20)

        scrollProduits.addView(zoneProduits)

        principal.addView(
            scrollProduits,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // --------------------------------------------------------
        // MENU DU BAS
        // --------------------------------------------------------

        val menuBas = LinearLayout(this)
        menuBas.orientation = LinearLayout.HORIZONTAL
        menuBas.setBackgroundColor(Color.rgb(235, 235, 235))

        val accueil = creerBoutonBas("🏠\nACCUEIL")
        val produitsBtn = creerBoutonBas("🛍️\nPRODUITS")
        val panierBtn = creerBoutonBas("🛒\nPANIER")
        val compteBtn = creerBoutonBas("👤\nCOMPTE")

        menuBas.addView(
            accueil,
            LinearLayout.LayoutParams(0, 75, 1f)
        )

        menuBas.addView(
            produitsBtn,
            LinearLayout.LayoutParams(0, 75, 1f)
        )

        menuBas.addView(
            panierBtn,
            LinearLayout.LayoutParams(0, 75, 1f)
        )

        menuBas.addView(
            compteBtn,
            LinearLayout.LayoutParams(0, 75, 1f)
        )

        produitsBtn.setOnClickListener {
            afficherProduits()
        }

        panierBtn.setOnClickListener {
            afficherPanier()
        }

        compteBtn.setOnClickListener {
            afficherCompte()
        }

        principal.addView(menuBas)

        setContentView(principal)

        afficherListeProduits()
    }

    // ============================================================
    // AFFICHAGE DES PRODUITS
    // ============================================================

    private fun afficherListeProduits() {

        if (!::zoneProduits.isInitialized) return

        zoneProduits.removeAllViews()

        val rechercheTexte =
            if (::recherche.isInitialized)
                recherche.text.toString().trim().lowercase()
            else
                ""

        var nombre = 0

        for (produit in produits) {

            val correspondRecherche =
                rechercheTexte.isEmpty() ||
                        produit.nom.lowercase().contains(rechercheTexte)

            val correspondCategorie =
                categorieSelectionnee == "Tous" ||
                        produit.categorie == categorieSelectionnee

            if (correspondRecherche && correspondCategorie) {

                zoneProduits.addView(
                    creerCarteProduit(produit)
                )

                nombre++
            }
        }

        if (nombre == 0) {

            val aucun = TextView(this)
            aucun.text = "Aucun produit trouvé"
            aucun.textSize = 18f
            aucun.gravity = Gravity.CENTER
            aucun.setPadding(20, 40, 20, 40)

            zoneProduits.addView(
                aucun,
                GridLayout.LayoutParams().apply {
                    columnSpec = GridLayout.spec(0, 2)
                }
            )
        }
    }

    // ============================================================
    // CARTE PRODUIT
    // ============================================================

    private fun creerCarteProduit(produit: Produit): LinearLayout {

        val carte = LinearLayout(this)
        carte.orientation = LinearLayout.VERTICAL
        carte.gravity = Gravity.CENTER
        carte.setPadding(10, 10, 10, 10)
        carte.setBackgroundColor(Color.rgb(245, 247, 246))

        // Zone photo
        val photo = ImageView(this)

        // Image temporaire.
        // Plus tard, on pourra mettre la vraie photo du produit.
        photo.setImageResource(android.R.drawable.ic_menu_gallery)

        photo.setBackgroundColor(Color.rgb(225, 235, 230))
        photo.scaleType = ImageView.ScaleType.CENTER_INSIDE

        carte.addView(
            photo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
            )
        )

        // Nom
        val nom = TextView(this)
        nom.text = produit.nom
        nom.textSize = 18f
        nom.setTextColor(Color.rgb(20, 100, 70))
        nom.gravity = Gravity.CENTER
        nom.setPadding(5, 8, 5, 3)

        carte.addView(nom)

        // Prix
        val prix = TextView(this)
        prix.text = produit.prix
        prix.textSize = 16f
        prix.setTextColor(Color.DKGRAY)
        prix.gravity = Gravity.CENTER

        carte.addView(prix)

        // Catégorie
        val categorie = TextView(this)
        categorie.text = produit.categorie
        categorie.textSize = 13f
        categorie.setTextColor(Color.GRAY)
        categorie.gravity = Gravity.CENTER

        carte.addView(categorie)

        // Bouton acheter
        val acheter = Button(this)
        acheter.text = "🛒 Acheter"
        acheter.setTextColor(Color.WHITE)
        acheter.setBackgroundColor(Color.rgb(20, 110, 75))

        acheter.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(produit.nom)
                .setMessage(
                    "Prix : ${produit.prix}\n\n" +
                            "Catégorie : ${produit.categorie}"
                )
                .setPositiveButton("Ajouter au panier") { _, _ ->
                    Toast.makeText(
                        this,
                        "${produit.nom} ajouté au panier",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Fermer", null)
                .show()
        }

        carte.addView(acheter)

        val params = GridLayout.LayoutParams()

        params.width = 0
        params.columnSpec = GridLayout.spec(
            GridLayout.UNDEFINED,
            1f
        )

        params.setMargins(6, 6, 6, 6)

        carte.layoutParams = params

        return carte
    }

    // ============================================================
    // AJOUTER UN PRODUIT
    // ============================================================

    private fun ajouterProduit() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 10)

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix (ex : 10 000 FCFA)"

        val categorie = EditText(this)
        categorie.hint = "Catégorie (ex : Santé)"

        layout.addView(nom)
        layout.addView(prix)
        layout.addView(categorie)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un produit")
            .setView(layout)
            .setPositiveButton("Ajouter") { _, _ ->

                val nomProduit = nom.text.toString().trim()
                val prixProduit = prix.text.toString().trim()
                val categorieProduit =
                    categorie.text.toString().trim()

                if (nomProduit.isNotEmpty()) {

                    produits.add(
                        Produit(
                            nomProduit,
                            if (prixProduit.isEmpty())
                                "Prix à définir"
                            else
                                prixProduit,
                            if (categorieProduit.isEmpty())
                                "Autres"
                            else
                                categorieProduit
                        )
                    )

                    afficherAccueil()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // ============================================================
    // PAGE PRODUITS
    // ============================================================

    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        val titre = TextView(this)
        titre.text = "GESTION DES PRODUITS"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(Color.rgb(20, 110, 75))

        layout.addView(titre)

        layout.addView(
            creerBouton("➕ Ajouter un produit").apply {
                setOnClickListener {
                    ajouterProduit()
                }
            }
        )

        for (produit in produits) {

            val ligne = TextView(this)

            ligne.text =
                "📦 ${produit.nom}\n" +
                        "💰 ${produit.prix}\n" +
                        "📂 ${produit.categorie}"

            ligne.textSize = 17f
            ligne.setPadding(15, 15, 15, 15)

            layout.addView(ligne)
        }

        layout.addView(
            creerBouton("← Retour à l'accueil").apply {
                setOnClickListener {
                    afficherAccueil()
                }
            }
        )

        setContentView(layout)
    }

    // ============================================================
    // MENU CACHÉ
    // ============================================================

    private fun afficherMenu() {

        val choix = arrayOf(
            "🛍️ Produits",
            "👥 Clients",
            "📦 Commandes",
            "📊 Gestion du stock",
            "🏪 Espace vendeur",
            "➕ Ajouter un produit"
        )

        AlertDialog.Builder(this)
            .setTitle("☰ Menu Vie Espoir")
            .setItems(choix) { _, position ->

                when (position) {

                    0 -> afficherProduits()

                    1 -> afficherClients()

                    2 -> afficherCommandes()

                    3 -> afficherStock()

                    4 -> afficherEspaceVendeur()

                    5 -> ajouterProduit()
                }
            }
            .show()
    }

    // ============================================================
    // PANIER
    // ============================================================

    private fun afficherPanier() {

        AlertDialog.Builder(this)
            .setTitle("🛒 Mon panier")
            .setMessage(
                "Votre panier est actuellement vide.\n\n" +
                        "Ajoutez des produits pour commencer."
            )
            .setPositiveButton("Fermer", null)
            .show()
    }

    // ============================================================
    // COMPTE
    // ============================================================

    private fun afficherCompte() {

        AlertDialog.Builder(this)
            .setTitle("👤 Mon compte")
            .setItems(
                arrayOf(
                    "Connexion",
                    "Créer un compte",
                    "Espace vendeur",
                    "Paramètres"
                )
            ) { _, position ->

                when (position) {

                    0 -> Toast.makeText(
                        this,
                        "Connexion à venir",
                        Toast.LENGTH_SHORT
                    ).show()

                    1 -> Toast.makeText(
                        this,
                        "Création de compte à venir",
                        Toast.LENGTH_SHORT
                    ).show()

                    2 -> afficherEspaceVendeur()

                    3 -> Toast.makeText(
                        this,
                        "Paramètres à venir",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // ============================================================
    // CLIENTS
    // ============================================================

    private fun afficherClients() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        layout.addView(
            creerTitre("LISTE DES CLIENTS")
        )

        if (clients.isEmpty()) {

            layout.addView(
                TextView(this).apply {
                    text = "Aucun client enregistré"
                    textSize = 18f
                    gravity = Gravity.CENTER
                    setPadding(10, 20, 10, 20)
                }
            )

        } else {

            for ((index, client) in clients.withIndex()) {

                layout.addView(
                    TextView(this).apply {
                        text = "${index + 1}. $client"
                        textSize = 18f
                        setPadding(10, 10, 10, 10)
                    }
                )
            }
        }

        layout.addView(
            creerBouton("➕ Ajouter un client").apply {
                setOnClickListener {
                    ajouterClient()
                }
            }
        )

        layout.addView(
            creerBouton("← Retour").apply {
                setOnClickListener {
                    afficherAccueil()
                }
            }
        )

        setContentView(layout)
    }

    private fun ajouterClient() {

        val champ = EditText(this)
        champ.hint = "Nom du client"

        AlertDialog.Builder(this)
            .setTitle("Ajouter un client")
            .setView(champ)
            .setPositiveButton("Ajouter") { _, _ ->

                val nom = champ.text.toString().trim()

                if (nom.isNotEmpty()) {

                    clients.add(nom)

                    afficherClients()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // ============================================================
    // COMMANDES
    // ============================================================

    private fun afficherCommandes() {

        AlertDialog.Builder(this)
            .setTitle("📦 Commandes")
            .setMessage(
                "Aucune commande pour le moment."
            )
            .setPositiveButton("Fermer", null)
            .show()
    }

    // ============================================================
    // STOCK
    // ============================================================

    private fun afficherStock() {

        AlertDialog.Builder(this)
            .setTitle("📊 Gestion du stock")
            .setMessage(
                "La gestion détaillée du stock sera disponible ici.\n\n" +
                        "Produits enregistrés : ${produits.size}"
            )
            .setPositiveButton("Fermer", null)
            .show()
    }

    // ============================================================
    // ESPACE VENDEUR
    // ============================================================

    private fun afficherEspaceVendeur() {

        AlertDialog.Builder(this)
            .setTitle("🏪 Espace vendeur")
            .setItems(
                arrayOf(
                    "➕ Ajouter un produit",
                    "📦 Mes produits",
                    "📊 Mon stock",
                    "💰 Mes ventes"
                )
            ) { _, position ->

                when (position) {

                    0 -> ajouterProduit()

                    1 -> afficherProduits()

                    2 -> afficherStock()

                    3 -> Toast.makeText(
                        this,
                        "Vos ventes seront affichées ici.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // ============================================================
    // BOUTONS
    // ============================================================

    private fun creerBouton(texte: String): Button {

        return Button(this).apply {

            text = texte
            textSize = 16f

            setTextColor(Color.WHITE)

            setBackgroundColor(
                Color.rgb(20, 110, 75)
            )

            setPadding(
                20,
                15,
                20,
                15
            )

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(10, 10, 10, 10)
                }
        }
    }

    private fun creerBoutonBas(texte: String): Button {

        return Button(this).apply {

            text = texte
            textSize = 13f

            setTextColor(Color.DKGRAY)

            setBackgroundColor(
                Color.rgb(235, 235, 235)
            )

            gravity = Gravity.CENTER

            setPadding(2, 2, 2, 2)
        }
    }

    private fun creerTitre(texte: String): TextView {

        return TextView(this).apply {

            text = texte
            textSize = 24f

            setTextColor(
                Color.rgb(20, 100, 50)
            )

            gravity = Gravity.CENTER

            setPadding(
                10,
                25,
                10,
                25
            )

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }
    }

    // ============================================================
    // BOUTON RETOUR ANDROID
    // ============================================================

    override fun onBackPressed() {

        afficherAccueil()
    }
}