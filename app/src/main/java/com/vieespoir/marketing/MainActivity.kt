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

        // BOUTON

        val bouton =
            boutonPrincipal(
                "🛒 Ajouter au panier"
            )

        bouton.setOnClickListener {

            ajouterAuPanier(
                produit
            )
        }

        carte.addView(image)
        carte.addView(nom)
        carte.addView(prix)
        carte.addView(infos)
        carte.addView(bouton)

        val params =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            0,
            0,
            12
        )

        contenu.addView(
            carte,
            params
        )
    }

    // =========================================================
    // MENU
    // =========================================================

    private fun afficherMenu() {

        val items =
            arrayOf(
                "🏪 Espace vendeur",
                "📦 Commandes",
                "📊 Gestion du stock",
                "👥 Clients",
                "💳 Paiements",
                "🚚 Livraison",
                "💰 Commissions",
                "👑 Administration"
            )

        AlertDialog.Builder(this)
            .setTitle("☰ Menu")
            .setItems(items) { _, position ->

                when (position) {

                    0 ->
                        afficherVendeur()

                    1 ->
                        afficherCommandes()

                    2 ->
                        afficherStock()

                    3 ->
                        afficherClients()

                    4 ->
                        afficherPaiements()

                    5 ->
                        afficherLivraison()

                    6 ->
                        afficherCommissions()

                    7 ->
                        afficherAdministration()
                }
            }
            .show()
    }

    // =========================================================
    // PAGE PRODUITS
    // =========================================================

    private fun afficherProduits(
        recherche: String = ""
    ) {

        afficherPage(
            "🛍️ Produits"
        )

        val ajouter =
            boutonPrincipal(
                "➕ Ajouter un produit"
            )

        ajouter.setOnClickListener {
            ajouterProduit()
        }

        contenu.addView(
            ajouter
        )

        ajouterEspace(8)

        val terme =
            recherche.lowercase(
                Locale.getDefault()
            )

        var trouve = false

        produits.forEach { produit ->

            if (!produit.actif)
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

            ajouterCarte(
                "📦",
                produit.nom,
                "${produit.prix} FCFA • Stock : ${produit.stock}\nVendeur : ${produit.vendeur}"
            ) {

                modifierProduit(
                    produit
                )
            }
        }

        if (!trouve) {

            val vide =
                TextView(this)

            vide.text =
                "Aucun produit trouvé."

            vide.textSize =
                18f

            vide.gravity =
                Gravity.CENTER

            vide.setPadding(
                10,
                40,
                10,
                40
            )

            contenu.addView(
                vide
            )
        }
    }

    // =========================================================
    // AJOUTER PRODUIT
    // =========================================================

    private fun ajouterProduit() {

        imageProduitChoisie = ""

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            20,
            5,
            20,
            5
        )

        val image =
            ImageView(this)

        image.setImageResource(
            android.R.drawable
                .ic_menu_gallery
        )

        image.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                180
            )

        image.scaleType =
            ImageView.ScaleType.CENTER_INSIDE

        val photo =
            boutonSecondaire(
                "📷 Choisir la photo du produit"
            )

        photo.setOnClickListener {

            val intent =
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media
                        .EXTERNAL_CONTENT_URI
                )

            startActivityForResult(
                intent,
                1001
            )
        }

        val nom =
            EditText(this)

        nom.hint =
            "Nom du produit"

        val prix =
            EditText(this)

        prix.hint =
            "Prix en FCFA"

        prix.inputType =
            InputType.TYPE_CLASS_NUMBER

        val stock =
            EditText(this)

        stock.hint =
            "Quantité en stock"

        stock.inputType =
            InputType.TYPE_CLASS_NUMBER

        layout.addView(image)
        layout.addView(photo)
        layout.addView(nom)
        layout.addView(prix)
        layout.addView(stock)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "➕ Ajouter un produit"
                )
                .setView(layout)
                .setNegativeButton(
                    "Annuler",
                    null
                )
                .setPositiveButton(
                    "Ajouter",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val n =
                    nom.text
                        .toString()
                        .trim()

                val p =
                    prix.text
                        .toString()
                        .toIntOrNull()

                val s =
                    stock.text
                        .toString()
                        .toIntOrNull()

                if (
                    n.isEmpty() ||
                    p == null ||
                    p <= 0 ||
                    s == null ||
                    s < 0
                ) {

                    Toast.makeText(
                        this,
                        "Informations invalides",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                produits.add(
                    Produit(
                        n,
                        p,
                        s,
                        imageProduitChoisie,
                        "Mon compte vendeur",
                        true
                    )
                )

                Toast.makeText(
                    this,
                    "Produit publié sur la plateforme",
                    Toast.LENGTH_LONG
                ).show()

                dialog.dismiss()

                afficherProduits()
            }
        }

        dialog.show()
    }

    // =========================================================
    // PHOTO
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
            requestCode == 1001 &&
            resultCode == RESULT_OK &&
            data?.data != null
        ) {

            imageProduitChoisie =
                data.data.toString()

            Toast.makeText(
                this,
                "Photo sélectionnée",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // MODIFIER PRODUIT
    // =========================================================

    private fun modifierProduit(
        produit: Produit
    ) {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            20,
            5,
            20,
            5
        )

        val nom =
            EditText(this)

        nom.setText(
            produit.nom
        )

        val prix =
            EditText(this)

        prix.setText(
            produit.prix.toString()
        )

        prix.inputType =
            InputType.TYPE_CLASS_NUMBER

        val stock =
            EditText(this)

        stock.setText(
            produit.stock.toString()
        )

        stock.inputType =
            InputType.TYPE_CLASS_NUMBER

        layout.addView(nom)
        layout.addView(prix)
        layout.addView(stock)

        AlertDialog.Builder(this)
            .setTitle(
                "✏️ Modifier le produit"
            )
            .setView(layout)
            .setNegativeButton(
                "Annuler",
                null
            )
            .setNeutralButton(
                "🗑️ Masquer"
            ) { _, _ ->

                produit.actif =
                    false

                afficherProduits()
            }
            .setPositiveButton(
                "Enregistrer"
            ) { _, _ ->

                val p =
                    prix.text
                        .toString()
                        .toIntOrNull()

                val s =
                    stock.text
                        .toString()
                        .toIntOrNull()

                val n =
                    nom.text
                        .toString()
                        .trim()

                if (
                    n.isNotEmpty() &&
                    p != null &&
                    p > 0 &&
                    s != null &&
                    s >= 0
                ) {

                    produit.nom = n
                    produit.prix = p
                    produit.stock = s

                    afficherProduits()
                }
            }
            .show()
    }

    // =========================================================
    // ESPACE VENDEUR
    // =========================================================

    private fun afficherVendeur() {

        afficherPage(
            "🏪 Espace vendeur"
        )

        ajouterCarte(
            "👤",
            "MON COMPTE VENDEUR",
            "Gérer mon profil vendeur"
        ) {

            afficherCompte()
        }

        ajouterCarte(
            "💳",
            "ABONNEMENT",
            "Choisir ou renouveler mon abonnement"
        ) {

            afficherAbonnement()
        }

        ajouterCarte(
            "➕",
            "AJOUTER UN PRODUIT",
            "Photo, nom, prix et stock"
        ) {

            ajouterProduit()
        }

        ajouterCarte(
            "🛍️",
            "MES PRODUITS",
            "Gérer les produits publiés"
        ) {

            afficherProduits()
        }

        ajouterCarte(
            "📊",
            "TABLEAU DE BORD",
            "${produits.size} produit(s) • ${commandes.size} commande(s)"
        ) {

            afficherStatistiques()
        }
    }

    // =========================================================
    // ABONNEMENT
    // =========================================================

    private fun afficherAbonnement() {

        afficherPage(
            "💳 Abonnement vendeur"
        )

        ajouterCarte(
            "🥉",
            "PACK 1 MOIS",
            "Votre boutique reste visible pendant 1 mois"
        ) {

            confirmerAbonnement(
                "PACK 1 MOIS"
            )
        }

        ajouterCarte(
            "🥈",
            "PACK 6 MOIS",
            "Votre boutique reste visible pendant 6 mois"
        ) {

            confirmerAbonnement(
                "PACK 6 MOIS"
            )
        }

        ajouterCarte(
            "🥇",
            "PACK 12 MOIS",
            "Votre boutique reste visible pendant 12 mois"
        ) {

            confirmerAbonnement(
                "PACK 12 MOIS"
            )
        }
    }

    private fun confirmerAbonnement(
        pack: String
    ) {

        AlertDialog.Builder(this)
            .setTitle(pack)
            .setMessage(
                "L'abonnement permettra de publier les produits du vendeur sur la grande plateforme."
            )
            .setNegativeButton(
                "Annuler",
                null
            )
            .setPositiveButton(
                "Continuer"
            ) { _, _ ->

                Toast.makeText(
                    this,
                    "$pack sélectionné",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    // =========================================================
    // PANIER
    // =========================================================

    private fun ajouterAuPanier(
        produit: Produit
    ) {

        if (
            produit.stock <= 0
        ) {

            Toast.makeText(
                this,
                "Produit en rupture de stock",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        panier.add(
            produit
        )

        produit.stock--

        Toast.makeText(
            this,
            "${produit.nom} ajouté au panier",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun afficherPanier() {

        afficherPage(
            "🛒 Mon panier"
        )

        if (
            panier.isEmpty()
        ) {

            val v =
                TextView(this)

            v.text =
                "Votre panier est vide."

            v.textSize =
                20f

            v.gravity =
                Gravity.CENTER

            v.setPadding(
                10,
                50,
                10,
                50
            )

            contenu.addView(v)

            return
        }

        var total = 0

        panier.forEachIndexed {
                index,
                produit ->

            ajouterCarte(
                "🛍️",
                produit.nom,
                "${produit.prix} FCFA"
            ) {

                if (
                    index < panier.size
                ) {

                    panier.removeAt(
                        index
                    )

                    produit.stock++

                    afficherPanier()
                }
            }

            total += produit.prix
        }

        val t =
            TextView(this)

        t.text =
            "TOTAL : $total FCFA"

        t.textSize =
            22f

        t.gravity =
            Gravity.CENTER

        t.setTextColor(
            Color.rgb(0, 110, 75)
        )

        contenu.addView(t)

        ajouterEspace(10)

        val commander =
            boutonPrincipal(
                "✅ PASSER LA COMMANDE"
            )

        commander.setOnClickListener {

            passerCommande(
                total
            )
        }

        contenu.addView(
            commander
        )
    }

    // =========================================================
    // COMMANDE
    // =========================================================

    private fun passerCommande(
        total: Int
    ) {

        val nom =
            EditText(this)

        nom.hint =
            "Nom du client"

        val telephone =
            EditText(this)

        telephone.hint =
            "Téléphone"

        telephone.inputType =
            InputType.TYPE_CLASS_PHONE

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            20,
            5,
            20,
            5
        )

        layout.addView(nom)
        layout.addView(telephone)

        AlertDialog.Builder(this)
            .setTitle(
                "💳 Finaliser la commande"
            )
            .setView(layout)
            .setNegativeButton(
                "Annuler",
                null
            )
            .setPositiveButton(
                "Commander"
            ) { _, _ ->

                if (
                    nom.text
                        .toString()
                        .trim()
                        .isEmpty()
                ) {

                    return@setPositiveButton
                }

                commandes.add(
                    "Commande #${commandes.size + 1}\n" +
                    "Client : ${nom.text}\n" +
                    "Téléphone : ${telephone.text}\n" +
                    "Montant : $total FCFA\n" +
                    "Statut : En préparation"
                )

                if (
                    !clients.contains(
                        telephone.text.toString()
                    )
                ) {

                    clients.add(
                        telephone.text.toString()
                    )
                }

                panier.clear()

                Toast.makeText(
                    this,
                    "Commande enregistrée",
                    Toast.LENGTH_LONG
                ).show()

                afficherCommandes()
            }
            .show()
    }

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun afficherCommandes() {

        afficherPage(
            "📦 Commandes"
        )

        if (
            commandes.isEmpty()
        ) {

            ajouterCarte(
                "📦",
                "Aucune commande",
                "Aucune commande pour le moment"
            ) {}

            return
        }

        commandes.forEach {

            ajouterCarte(
                "📦",
                it.substringBefore(
                    "\n"
                ),
                it.substringAfter(
                    "\n"
                )
            ) {}
        }
    }

    // =========================================================
    // STOCK
    // =========================================================

    private fun afficherStock() {

        afficherPage(
            "📊 Gestion du stock"
        )

        produits.forEach {

            ajouterCarte(
                "📦",
                it.nom,
                "Stock : ${it.stock}"
            ) {

                modifierProduit(it)
            }
        }
    }

    // =========================================================
    // CLIENTS
    // =========================================================

    private fun afficherClients() {

        afficherPage(
            "👥 Clients"
        )

        ajouterCarte(
            "👥",
            "CLIENTS ENREGISTRÉS",
            "${clients.size} client(s)"
        ) {}
    }

    // =========================================================
    // PAIEMENTS
    // =========================================================

    private fun afficherPaiements() {

        afficherPage(
            "💳 Paiements"
        )

        arrayOf(
            "Orange Money",
            "MTN Money",
            "Moov Money",
            "Wave",
            "Espèces"
        ).forEach {

            ajouterCarte(
                "💳",
                it,
                "Moyen de paiement"
            ) {

                Toast.makeText(
                    this,
                    "$it sélectionné",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // =========================================================
    // LIVRAISON
    // =========================================================

    private fun afficherLivraison() {

        afficherPage(
            "🚚 Livraison"
        )

        ajouterCarte(
            "📦",
            "EN PRÉPARATION",
            "Commandes en préparation"
        ) {}

        ajouterCarte(
            "🚚",
            "EN LIVRAISON",
            "Commandes en livraison"
        ) {}

        ajouterCarte(
            "✅",
            "LIVRÉES",
            "Commandes terminées"
        ) {}
    }

    // =========================================================
    // COMMISSIONS
    // =========================================================

    private fun afficherCommissions() {

        afficherPage(
            "💰 Commissions"
        )

        ajouterCarte(
            "💵",
            "COMMISSIONS GAGNÉES",
            "0 FCFA"
        ) {}
    }

    // =========================================================
    // STATISTIQUES
    // =========================================================

    private fun afficherStatistiques() {

        afficherPage(
            "📊 Tableau de bord"
        )

        ajouterCarte(
            "🛍️",
            "PRODUITS",
            "${produits.size} produit(s)"
        ) {}

        ajouterCarte(
            "📦",
            "COMMANDES",
            "${commandes.size} commande(s)"
        ) {}

        ajouterCarte(
            "👥",
            "CLIENTS",
            "${clients.size} client(s)"
        ) {}

        ajouterCarte(
            "💰",
            "COMMISSIONS",
            "0 FCFA"
        ) {}
    }

    // =========================================================
    // ADMINISTRATION
    // =========================================================

    private fun afficherAdministration() {

        afficherPage(
            "👑 Administration"
        )

        ajouterCarte(
            "👥",
            "VENDEURS",
            "Gérer les vendeurs"
        ) {}

        ajouterCarte(
            "🛍️",
            "PRODUITS",
            "Gérer les produits"
        ) {

            afficherProduits()
        }

        ajouterCarte(
            "📦",
            "COMMANDES",
            "Gérer les commandes"
        ) {

            afficherCommandes()
        }

        ajouterCarte(
            "💳",
            "ABONNEMENTS",
            "Gérer les abonnements"
        ) {

            afficherAbonnement()
        }
    }

    // =========================================================
    // COMPTE
    // =========================================================

    private fun afficherCompte() {

        afficherPage(
            "👤 Mon compte"
        )

        ajouterCarte(
            "👤",
            "MON PROFIL",
            "Yao Guillaume"
        ) {

            Toast.makeText(
                this,
                "Profil vendeur",
                Toast.LENGTH_SHORT
            ).show()
        }

        ajouterCarte(
            "🏪",
            "ESPACE VENDEUR",
            "Gérer ma boutique"
        ) {

            afficherVendeur()
        }

        ajouterCarte(
            "🔔",
            "NOTIFICATIONS",
            "Aucune nouvelle notification"
        ) {}
    }

    // =========================================================
    // AFFICHAGE
    // =========================================================

    private fun afficherPage(
        titrePage: String
    ) {

        contenu.removeAllViews()

        val retour =
            boutonSecondaire(
                "← Accueil"
            )

        retour.setOnClickListener {
            afficherAccueil()
        }

        contenu.addView(
            retour
        )

        ajouterTitre(
            titrePage
        )
    }

    private fun ajouterTitre(
        texte: String
    ) {

        val titre =
            TextView(this)

        titre.text =
            texte

        titre.textSize =
            23f

        titre.setTextColor(
            Color.rgb(0, 110, 75)
        )

        titre.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        titre.setPadding(
            5,
            12,
            5,
            18
        )

        contenu.addView(
            titre
        )
    }

    private fun ajouterCarte(
        icone: String,
        titre: String,
        description: String,
        action: () -> Unit
    ) {

        val carte =
            LinearLayout(this)

        carte.orientation =
            LinearLayout.VERTICAL

        carte.setPadding(
            18,
            16,
            18,
            16
        )

        carte.setBackgroundColor(
            Color.rgb(242, 244, 244)
        )

        carte.isClickable =
            true

        carte.setOnClickListener {
            action()
        }

        val t =
            TextView(this)

        t.text =
            "$icone  $titre"

        t.textSize =
            19f

        t.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        t.setTextColor(
            Color.rgb(0, 100, 70)
        )

        val d =
            TextView(this)

        d.text =
            description

        d.textSize =
            15f

        d.setTextColor(
            Color.DKGRAY
        )

        d.setPadding(
            0,
            8,
            0,
            2
        )

        carte.addView(t)
        carte.addView(d)

        val p =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        p.setMargins(
            0,
            0,
            0,
            10
        )

        contenu.addView(
            carte,
            p
        )
    }

    // =========================================================
    // BOUTONS
    // =========================================================

    private fun boutonPrincipal(
        texte: String
    ): Button {

        val b =
            Button(this)

        b.text =
            texte

        b.textSize =
            15f

        b.setTextColor(
            Color.WHITE
        )

        b.setBackgroundColor(
            Color.rgb(0, 120, 80)
        )

        b.gravity =
            Gravity.CENTER

        b.minHeight =
            56

        return b
    }

    private fun boutonSecondaire(
        texte: String
    ): Button {

        val b =
            Button(this)

        b.text =
            texte

        b.textSize =
            15f

        b.gravity =
            Gravity.CENTER

        b.minHeight =
            52

        return b
    }

    private fun boutonBas(
        texte: String
    ): Button {

        val b =
            Button(this)

        b.text =
            texte

        b.textSize =
            12f

        b.gravity =
            Gravity.CENTER

        b.setPadding(
            2,
            2,
            2,
            2
        )

        return b
    }

    private fun poidsNavigation():
        LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )
    }

    private fun ajouterEspace(
        taille: Int
    ) {

        contenu.addView(
            Space(this),
            LinearLayout.LayoutParams(
                1,
                taille
            )
        )
    }
}