package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================
// VIE ESPOIR MARKETING
// Application Android
// ============================================================

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.WHITE)
        }

        root.addView(TextView(this).apply {
            text = "🦁"
            textSize = 65f
            gravity = Gravity.CENTER
        })

        root.addView(TextView(this).apply {
            text = "VIE ESPOIR"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(20, 100, 50))
            typeface = Typeface.DEFAULT_BOLD
        })

        root.addView(TextView(this).apply {
            text = "MARKETING"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(218, 165, 32))
            typeface = Typeface.DEFAULT_BOLD
        })

        setContentView(root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}

class MainActivity : Activity() {

    // ========================================================
    // MODÈLES
    // ========================================================

    data class Produit(
        val id: String,
        var nom: String,
        var prix: Int,
        var categorie: String,
        var description: String,
        var stock: Int,
        var imageUri: String,
        var vendeurId: String,
        var entreprise: String
    )

    data class PanierItem(
        val produitId: String,
        val nom: String,
        val prix: Int,
        var quantite: Int
    )

    data class Commande(
        val id: String,
        val client: String,
        val telephone: String,
        val adresse: String,
        val details: String,
        val montant: Int,
        val paiement: String,
        var statut: String
    )

    data class Compte(
        val id: String,
        var nom: String,
        var entreprise: String,
        var email: String,
        var telephone: String,
        var adresse: String,
        var motDePasse: String,
        var valide: Boolean,
        var dateCreation: Long,
        var abonnementJusqua: Long
    )

    // ========================================================
    // DONNÉES
    // ========================================================

    private val produits = arrayListOf<Produit>()
    private val panier = arrayListOf<PanierItem>()
    private val commandes = arrayListOf<Commande>()
    private val comptes = arrayListOf<Compte>()
    private val evenements = arrayListOf<String>()

    private var compteConnecteId = ""
    private var adminConnecte = false

    private var zoneProduits: LinearLayout? = null
    private var imageEnPreparation = ""

    private val preferences by lazy {
        getSharedPreferences("vie_espoir_marketing", MODE_PRIVATE)
    }

    private val CLE_PRODUITS = "produits"
    private val CLE_PANIER = "panier"
    private val CLE_COMMANDES = "commandes"
    private val CLE_COMPTES = "comptes"
    private val CLE_EVENEMENTS = "evenements"
    private val CLE_COMPTE = "compte_connecte"

    // À modifier si tes coordonnées officielles changent.
    private val TELEPHONE_CONTACT = "0710405688"
    private val TELEPHONE_MTN = "0546420566"

    // Pour une vraie mise en production, ces identifiants
    // ne doivent pas rester codés en dur dans l'application.
    private val ADMIN_EMAIL = "admin@vieespoir.com"
    private val ADMIN_MOT_DE_PASSE = "VieEspoirAdmin2026"

    private val VERT = Color.rgb(20, 100, 50)
    private val VERT_FONCE = Color.rgb(8, 65, 32)
    private val OR = Color.rgb(218, 165, 32)
    private val FOND = Color.rgb(247, 249, 246)
    private val TEXTE = Color.rgb(35, 35, 35)
    private val BLANC = Color.WHITE
    private val GRIS = Color.rgb(235, 237, 235)

    // ========================================================
    // INITIALISATION
    // ========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = VERT_FONCE
        window.navigationBarColor = BLANC

        chargerDonnees()

        compteConnecteId =
            preferences.getString(CLE_COMPTE, "") ?: ""

        afficherAccueil()
    }

    override fun onBackPressed() {
        afficherAccueil()
    }

    // ========================================================
    // OUTILS D'AFFICHAGE
    // ========================================================

    private fun layoutBase(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 10, 12, 24)
            setBackgroundColor(FOND)
        }
    }

    private fun scroll(contenu: View): ScrollView {
        return ScrollView(this).apply {
            isFillViewport = true
            addView(contenu)
        }
    }

    private fun titre(texte: String): TextView {
        return TextView(this).apply {
            text = texte
            textSize = 22f
            setTextColor(VERT)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(8, 18, 8, 18)
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
            setTextColor(couleur)
            setPadding(8, 6, 8, 6)
        }
    }

    private fun bouton(
        libelle: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = libelle
            textSize = 14f
            setTextColor(VERT)
            setBackgroundColor(GRIS)
            setOnClickListener { action() }

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(4, 5, 4, 5)
            }
        }
    }

    private fun boutonVert(
        libelle: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = libelle
            textSize = 14f
            setTextColor(BLANC)
            setBackgroundColor(VERT)
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener { action() }

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(4, 5, 4, 5)
            }
        }
    }

    private fun afficherVue(view: View) {
        setContentView(view)
    }

    private fun afficherRacine(view: View) {
        zoneProduits = null
        setContentView(view)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun formaterPrix(prix: Int): String {
        return String.format(Locale.FRANCE, "%,d", prix)
            .replace(',', ' ') + " FCFA"
    }

    private fun compteActuel(): Compte? {
        return comptes.find { it.id == compteConnecteId }
    }

    private fun enregistrerEvenement(message: String) {
        val date = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.FRANCE
        ).format(Date())

        evenements.add("$date — $message")

        while (evenements.size > 300) {
            evenements.removeAt(0)
        }

        sauvegarderEvenements()
    }

    // ========================================================
    // ACCUEIL MODERNE
    // ========================================================

    private fun afficherAccueil() {

        val root = layoutBase()

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 14, 12, 14)
            setBackgroundColor(BLANC)
        }

        header.addView(TextView(this).apply {
            text = "🦁"
            textSize = 40f
            gravity = Gravity.CENTER
            setPadding(0, 0, 10, 0)
        })

        val identite = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        identite.addView(TextView(this).apply {
            text = "VIE ESPOIR"
            textSize = 22f
            setTextColor(VERT)
            typeface = Typeface.DEFAULT_BOLD
        })

        identite.addView(TextView(this).apply {
            text = "MARKETING"
            textSize = 17f
            setTextColor(OR)
            typeface = Typeface.DEFAULT_BOLD
        })

        header.addView(identite)

        header.addView(bouton("☰") {
            afficherMenu()
        }.apply {
            layoutParams = LinearLayout.LayoutParams(-2, -2)
        })

        root.addView(header)

        val bienvenue = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 24, 16, 24)
            setBackgroundColor(VERT_FONCE)
        }

        bienvenue.addView(TextView(this).apply {
            text = "BIENVENUE CHEZ VIE ESPOIR"
            textSize = 22f
            setTextColor(BLANC)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        })

        bienvenue.addView(TextView(this).apply {
            text = "Découvrez nos produits et commandez facilement."
            textSize = 15f
            setTextColor(BLANC)
            gravity = Gravity.CENTER
            setPadding(4, 10, 4, 4)
        })

        root.addView(bienvenue)

        val compte = compteActuel()

        if (compte != null) {
            root.addView(texte(
                "👋 Bienvenue, ${compte.nom}",
                17f,
                VERT
            ).apply {
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
            })
        }

        root.addView(titre("Que recherchez-vous ?"))

        val recherche = EditText(this).apply {
            hint = "🔎 Nom du produit ou catégorie"
            textSize = 16f
            singleLine = true
            setPadding(16, 12, 16, 12)
            setBackgroundColor(BLANC)
        }

        root.addView(recherche)

        root.addView(boutonVert("RECHERCHER UN PRODUIT") {
            afficherProduits(recherche.text.toString().trim())
        })

        root.addView(titre("Accès rapide"))

        val raccourcis = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        fun raccourci(
            symbole: String,
            libelle: String,
            action: () -> Unit
        ) {
            val bloc = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(4, 12, 4, 12)
                setBackgroundColor(BLANC)
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
                    .apply { setMargins(4, 4, 4, 4) }
                setOnClickListener { action() }
            }

            bloc.addView(TextView(this).apply {
                text = symbole
                textSize = 26f
                gravity = Gravity.CENTER
            })

            bloc.addView(TextView(this).apply {
                text = libelle
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(VERT)
                typeface = Typeface.DEFAULT_BOLD
            })

            raccourcis.addView(bloc)
        }

        raccourci("🛍️", "Produits") { afficherProduits("") }
        raccourci("🛒", "Panier") { afficherPanier() }
        raccourci("👤", "Mon compte") { afficherCompte() }

        root.addView(raccourcis)

        root.addView(titre("🛍️ NOS PRODUITS"))
        root.addView(texte(
            "Découvrez les produits disponibles et leurs prix.",
            14f,
            Color.GRAY
        ))

        zoneProduits = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(zoneProduits)

        root.addView(boutonVert("VOIR TOUS LES PRODUITS") {
            afficherProduits("")
        })

        root.addView(titre("🏪 ESPACE VENDEUR"))

        root.addView(texte(
            "Publiez vos produits et gérez votre commerce sur la plateforme."
        ))

        root.addView(boutonVert("Créer un compte vendeur") {
            afficherInscription()
        })

        root.addView(bouton("Connexion vendeur") {
            afficherConnexion()
        })

        root.addView(bouton("📞 CONTACTER VIE ESPOIR") {
            afficherContact()
        })

        root.addView(bouton("Partager l'application") {
            partagerTexte(
                "Découvrez Vie Espoir Marketing ! Contactez-nous pour plus d'informations."
            )
        })

        root.addView(texte(
            "VIE ESPOIR MARKETING\nProduits • Vente • Commandes",
            13f,
            Color.GRAY
        ).apply {
            gravity = Gravity.CENTER
            setPadding(8, 24, 8, 20)
        })

        afficherRacine(scroll(root))
        afficherProduitsAccueil()
    }

    private fun afficherProduitsAccueil() {
        val zone = zoneProduits ?: return
        zone.removeAllViews()

        val disponibles = produits.filter { it.stock > 0 }

        if (disponibles.isEmpty()) {
            zone.addView(texte(
                "Aucun produit publié pour le moment.",
                15f,
                Color.GRAY
            ).apply {
                gravity = Gravity.CENTER
            })
        } else {
            disponibles.take(6).forEach {
                ajouterCarteProduit(zone, it)
            }
        }
    }

    private fun afficherMenu() {
        val choix = arrayOf(
            "Accueil",
            "Produits",
            "Panier",
            "Commandes",
            "Mon compte",
            "Espace vendeur",
            "Contact",
            "Administration"
        )

        AlertDialog.Builder(this)
            .setTitle("Vie Espoir Marketing")
            .setItems(choix) { _, position ->
                when (position) {
                    0 -> afficherAccueil()
                    1 -> afficherProduits("")
                    2 -> afficherPanier()
                    3 -> afficherCommandes()
                    4 -> afficherCompte()
                    5 -> afficherEspaceVendeur()
                    6 -> afficherContact()
                    7 -> afficherConnexionAdmin()
                }
            }
            .show()
    }

    // ========================================================
    // PRODUITS ET PHOTOS
    // ========================================================

    private fun ajouterCarteProduit(
        parent: LinearLayout,
        p: Produit
    ) {
        val carte = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
            setBackgroundColor(BLANC)
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                setMargins(2, 6, 2, 6)
            }
        }

        if (p.imageUri.isNotBlank()) {
            try {
                val image = ImageView(this)
                image.layoutParams = LinearLayout.LayoutParams(-1, 220)
                image.scaleType = ImageView.ScaleType.CENTER_CROP
                image.setImageURI(Uri.parse(p.imageUri))
                carte.addView(image)
            } catch (_: Exception) {
                carte.addView(texte("🛍️"))
            }
        } else {
            carte.addView(TextView(this).apply {
                text = "🛍️"
                textSize = 35f
                gravity = Gravity.CENTER
                setPadding(8, 12, 8, 12)
            })
        }

        carte.addView(TextView(this).apply {
            text = p.nom
            textSize = 19f
            setTextColor(VERT)
            typeface = Typeface.DEFAULT_BOLD
        })

        carte.addView(texte("Prix : ${formaterPrix(p.prix)}", 18f, OR))
        carte.addView(texte("Catégorie : ${p.categorie}\nStock : ${p.stock}"))

        if (p.description.isNotBlank()) {
            carte.addView(texte(p.description, 14f))
        }

        if (p.entreprise.isNotBlank()) {
            carte.addView(texte("Vendeur : ${p.entreprise}", 13f, VERT))
        }

        carte.addView(boutonVert("Ajouter au panier") {
            ajouterAuPanier(p)
        })

        carte.addView(bouton("Partager ce produit") {
            partagerTexte(
                "${p.nom}\nPrix : ${formaterPrix(p.prix)}\n" +
                    "${p.description}\nVendeur : ${p.entreprise}"
            )
        })

        parent.addView(carte)
    }

    private fun afficherProduits(filtreInitial: String = "") {
        val root = layoutBase()
        root.addView(titre("🛍️ Tous nos produits"))

        val recherche = EditText(this).apply {
            hint = "Rechercher un produit"
            setSingleLine(true)
            setText(filtreInitial)
            setPadding(12, 12, 12, 12)
            setBackgroundColor(BLANC)
        }

        root.addView(recherche)

        val liste = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        fun actualiser() {
            liste.removeAllViews()

            val filtre = recherche.text.toString().trim()

            val resultats = produits.filter { p ->
                p.stock > 0 && (
                    filtre.isBlank() ||
                    p.nom.contains(filtre, true) ||
                    p.categorie.contains(filtre, true) ||
                    p.description.contains(filtre, true)
                )
            }

            if (resultats.isEmpty()) {
                liste.addView(texte("Aucun produit trouvé."))
            } else {
                resultats.forEach { ajouterCarteProduit(liste, it) }
            }
        }

        root.addView(boutonVert("Rechercher") { actualiser() })
        root.addView(liste)
        root.addView(bouton("Retour à l'accueil") { afficherAccueil() })

        afficherVue(scroll(root))
        actualiser()
    }

    // ========================================================
    // PANIER
    // ========================================================

    private fun ajouterAuPanier(p: Produit) {
        if (p.stock <= 0) {
            toast("Ce produit n'est plus disponible.")
            return
        }

        val item = panier.find { it.produitId == p.id }

        if (item != null) {
            if (item.quantite >= p.stock) {
                toast("Stock insuffisant.")
                return
            }
            item.quantite++
        } else {
            panier.add(PanierItem(p.id, p.nom, p.prix, 1))
        }

        sauvegarderPanier()
        toast("${p.nom} ajouté au panier.")
    }

    private fun afficherPanier() {
        val root = layoutBase()
        root.addView(titre("🛒 Mon panier"))

        if (panier.isEmpty()) {
            root.addView(texte("Votre panier est vide."))
        } else {
            panier.toList().forEach { item ->
                root.addView(texte(
                    "${item.nom}\n" +
                        "Prix unitaire : ${formaterPrix(item.prix)}\n" +
                        "Quantité : ${item.quantite}\n" +
                        "Sous-total : ${formaterPrix(item.prix * item.quantite)}"
                ))

                root.addView(bouton("Retirer un exemplaire") {
                    if (item.quantite > 1) {
                        item.quantite--
                    } else {
                        panier.remove(item)
                    }
                    sauvegarderPanier()
                    afficherPanier()
                })
            }

            val total = panier.sumOf { it.prix * it.quantite }

            root.addView(texte(
                "TOTAL : ${formaterPrix(total)}",
                21f,
                VERT
            ))

            root.addView(boutonVert("Passer la commande") {
                formulaireCommande()
            })

            root.addView(bouton("Vider le panier") {
                panier.clear()
                sauvegarderPanier()
                afficherPanier()
            })
        }

        root.addView(bouton("Continuer les achats") { afficherProduits("") })
        afficherVue(scroll(root))
    }

    private fun formulaireCommande() {
        if (panier.isEmpty()) {
            toast("Votre panier est vide.")
            return
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 8, 16, 8)
        }

        val nom = EditText(this).apply { hint = "Votre nom" }
        val telephone = EditText(this).apply {
            hint = "Téléphone"
            inputType = InputType.TYPE_CLASS_PHONE
        }
        val adresse = EditText(this).apply { hint = "Adresse de livraison" }

        val paiement = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(
                    "Paiement à la livraison",
                    "Orange Money / Wave",
                    "MTN Money"
                )
            )
        }

        box.addView(nom)
        box.addView(telephone)
        box.addView(adresse)
        box.addView(paiement)

        AlertDialog.Builder(this)
            .setTitle("Finaliser la commande")
            .setView(box)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Confirmer") { _, _ ->
                val n = nom.text.toString().trim()
                val t = telephone.text.toString().trim()
                val a = adresse.text.toString().trim()

                if (n.isBlank() || t.isBlank() || a.isBlank()) {
                    toast("Remplissez tous les champs.")
                    return@setPositiveButton
                }

                for (item in panier) {
                    val p = produits.find { it.id == item.produitId }
                    if (p == null || p.stock < item.quantite) {
                        toast("Stock insuffisant pour ${item.nom}.")
                        return@setPositiveButton
                    }
                }

                val details = panier.joinToString("\n") {
                    "${it.nom} x${it.quantite}"
                }

                val total = panier.sumOf { it.prix * it.quantite }

                commandes.add(
                    Commande(
                        id = System.currentTimeMillis().toString(),
                        client = n,
                        telephone = t,
                        adresse = a,
                        details = details,
                        montant = total,
                        paiement = paiement.selectedItem.toString(),
                        statut = "Nouvelle"
                    )
                )

                panier.forEach { item ->
                    produits.find { it.id == item.produitId }?.let {
                        it.stock -= item.quantite
                    }
                }

                panier.clear()

                sauvegarderCommandes()
                sauvegarderProduits()
                sauvegarderPanier()

                enregistrerEvenement("Commande enregistrée : $n")

                AlertDialog.Builder(this)
                    .setTitle("Commande enregistrée")
                    .setMessage(
                        "Merci $n !\n\n" +
                            "Montant : ${formaterPrix(total)}\n" +
                            "Votre commande est enregistrée sur cet appareil."
                    )
                    .setPositiveButton("OK") { _, _ -> afficherAccueil() }
                    .show()
            }
            .show()
    }

    private fun afficherCommandes() {
        val root = layoutBase()
        root.addView(titre("📦 Commandes"))

        if (commandes.isEmpty()) {
            root.addView(texte("Aucune commande enregistrée."))
        } else {
            commandes.asReversed().forEach { c ->
                root.addView(texte(
                    "Commande #${c.id}\n" +
                        "Client : ${c.client}\n" +
                        "Téléphone : ${c.telephone}\n" +
                        "Adresse : ${c.adresse}\n" +
                        "${c.details}\n" +
                        "Total : ${formaterPrix(c.montant)}\n" +
                        "Paiement : ${c.paiement}\n" +
                        "Statut : ${c.statut}"
                ))
            }
        }

        root.addView(bouton("Retour") { afficherAccueil() })
        afficherVue(scroll(root))
    }

    // ========================================================
    // INSCRIPTION ET CONNEXION VENDEUR
    // ========================================================

    private fun afficherInscription() {
        val root = layoutBase()
        root.addView(titre("Créer un compte vendeur"))

        val nom = EditText(this).apply { hint = "Nom complet" }
        val entreprise = EditText(this).apply { hint = "Nom du commerce" }
        val email = EditText(this).apply {
            hint = "Adresse e-mail"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val telephone = EditText(this).apply {
            hint = "Téléphone"
            inputType = InputType.TYPE_CLASS_PHONE
        }
        val adresse = EditText(this).apply { hint = "Ville ou adresse" }
        val mdp = EditText(this).apply {
            hint = "Mot de passe"
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        listOf(nom, entreprise, email, telephone, adresse, mdp)
            .forEach { root.addView(it) }

        root.addView(boutonVert("Créer mon compte") {
            val n = nom.text.toString().trim()
            val ent = entreprise.text.toString().trim()
            val e = email.text.toString().trim()
            val t = telephone.text.toString().trim()
            val a = adresse.text.toString().trim()
            val m = mdp.text.toString()

            if (
                n.isBlank() || ent.isBlank() || e.isBlank() ||
                t.isBlank() || a.isBlank() || m.isBlank()
            ) {
                toast("Remplissez tous les champs.")
                return@boutonVert
            }

            if (!e.contains("@")) {
                toast("Adresse e-mail invalide.")
                return@boutonVert
            }

            if (comptes.any { it.email.equals(e, true) }) {
                toast("Cette adresse e-mail est déjà utilisée.")
                return@boutonVert
            }

            val c = Compte(
                id = System.currentTimeMillis().toString(),
                nom = n,
                entreprise = ent,
                email = e,
                telephone = t,
                adresse = a,
                motDePasse = m,
                valide = true,
                dateCreation = System.currentTimeMillis(),
                abonnementJusqua = 0L
            )

            comptes.add(c)
            compteConnecteId = c.id

            preferences.edit().putString(CLE_COMPTE, c.id).apply()

            sauvegarderComptes()
            enregistrerEvenement("Inscription vendeur : $n")

            AlertDialog.Builder(this)
                .setTitle("Compte créé")
                .setMessage(
                    "Bienvenue, $n !\n\n" +
                        "Vous disposez de 7 jours d'essai. " +
                        "L'abonnement coûte ensuite 5 000 FCFA par mois."
                )
                .setPositiveButton("Continuer") { _, _ ->
                    afficherEspaceVendeur()
                }
                .show()
        })

        root.addView(bouton("J'ai déjà un compte") {
            afficherConnexion()
        })

        root.addView(bouton("Retour") { afficherAccueil() })
        afficherVue(scroll(root))
    }

    private fun afficherConnexion() {
        val root = layoutBase()
        root.addView(titre("Connexion vendeur"))

        val email = EditText(this).apply { hint = "Adresse e-mail" }
        val mdp = EditText(this).apply {
            hint = "Mot de passe"
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        root.addView(email)
        root.addView(mdp)

        root.addView(boutonVert("Se connecter") {
            val e = email.text.toString().trim()
            val m = mdp.text.toString()

            val c = comptes.find {
                it.email.equals(e, true) &&
                    it.motDePasse == m &&
                    it.valide
            }

            if (c == null) {
                toast("Identifiants incorrects ou compte bloqué.")
                return@boutonVert
            }

            compteConnecteId = c.id
            preferences.edit().putString(CLE_COMPTE, c.id).apply()
            enregistrerEvenement("Connexion vendeur : ${c.nom}")
            afficherEspaceVendeur()
        })

        root.addView(bouton("Créer un compte") { afficherInscription() })
        root.addView(bouton("Retour") { afficherAccueil() })

        afficherVue(scroll(root))
    }

    private fun afficherCompte() {
        val root = layoutBase()
        root.addView(titre("👤 Mon compte"))

        val c = compteActuel()

        if (c == null) {
            root.addView(texte("Vous n'êtes pas connecté."))

            root.addView(boutonVert("Créer un compte") {
                afficherInscription()
            })

            root.addView(bouton("Connexion") {
                afficherConnexion()
            })
        } else {
            root.addView(texte(
                "Nom : ${c.nom}\n" +
                    "Commerce : ${c.entreprise}\n" +
                    "E-mail : ${c.email}\n" +
                    "Téléphone : ${c.telephone}\n" +
                    "Adresse : ${c.adresse}"
            ))

            root.addView(boutonVert("Espace vendeur") {
                afficherEspaceVendeur()
            })

            root.addView(bouton("Se déconnecter") {
                compteConnecteId = ""
                preferences.edit().remove(CLE_COMPTE).apply()
                afficherAccueil()
            })
        }

        root.addView(bouton("Retour") { afficherAccueil() })
        afficherVue(scroll(root))
    }

    // ========================================================
    // ABONNEMENT
    // ========================================================

    private fun essaiValide(c: Compte): Boolean {
        val duree = 7L * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() < c.dateCreation + duree
    }

    private fun abonnementValide(c: Compte): Boolean {
        return c.abonnementJusqua > System.currentTimeMillis()
    }

    private fun vendeurPeutUtiliser(c: Compte): Boolean {
        return essaiValide(c) || abonnementValide(c)
    }

    private fun afficherAbonnement() {
        val root = layoutBase()
        root.addView(titre("Abonnement vendeur"))

        val c = compteActuel()

        if (c == null) {
            root.addView(boutonVert("Connexion") { afficherConnexion() })
            afficherVue(scroll(root))
            return
        }

        val statut = when {
            abonnementValide(c) -> "Abonnement actif"
            essaiValide(c) -> "Période d'essai en cours"
            else -> "Abonnement nécessaire"
        }

        root.addView(texte(statut, 18f, VERT))

        root.addView(texte(
            "Prix : 5 000 FCFA par mois\n\n" +
                "Contactez l'administrateur pour effectuer le paiement."
        ))

        root.addView(boutonVert("Contacter par WhatsApp") {
            ouvrirWhatsApp(TELEPHONE_CONTACT)
        })

        root.addView(texte(
            "Orange Money / Wave : $TELEPHONE_CONTACT\n" +
                "MTN Money : $TELEPHONE_MTN"
        ))

        root.addView(texte(
            "Le paiement n'est pas vérifié automatiquement. " +
                "L'administrateur doit vérifier la transaction avant d'activer l'abonnement."
        ))

        root.addView(bouton("Retour") { afficherEspaceVendeur() })
        afficherVue(scroll(root))
    }

    private fun afficherEspaceVendeur() {
        val root = layoutBase()
        root.addView(titre("🏪 Espace vendeur"))

        val c = compteActuel()

        if (c == null) {
            root.addView(boutonVert("Connexion") { afficherConnexion() })
            root.addView(bouton("Créer un compte") { afficherInscription() })
            afficherVue(scroll(root))
            return
        }

        root.addView(texte("Bienvenue, ${c.nom}", 20f, VERT))

        root.addView(texte(
            if (abonnementValide(c)) {
                "Votre abonnement est actif."
            } else if (essaiValide(c)) {
                "Votre période d'essai de 7 jours est en cours."
            } else {
                "Votre période d'essai est terminée."
            }
        ))

        root.addView(boutonVert("Mon abonnement") {
            afficherAbonnement()
        })

        root.addView(boutonVert("Ajouter un produit") {
            if (vendeurPeutUtiliser(c)) afficherAjouterProduit()
            else afficherAbonnement()
        })

        root.addView(bouton("Gérer mes produits et mon stock") {
            if (vendeurPeutUtiliser(c)) afficherStockVendeur()
            else afficherAbonnement()
        })

        root.addView(bouton("Mon compte") { afficherCompte() })
        root.addView(bouton("Accueil") { afficherAccueil() })

        afficherVue(scroll(root))
    }

    // ========================================================
    // AJOUT D'UN PRODUIT AVEC PHOTO
    // ========================================================

    private fun afficherAjouterProduit() {
        val c = compteActuel()

        if (c == null || !vendeurPeutUtiliser(c)) {
            afficherAbonnement()
            return
        }

        val root = layoutBase()
        root.addView(titre("Ajouter un produit"))

        val nom = EditText(this).apply { hint = "Nom du produit" }
        val prix = EditText(this).apply {
            hint = "Prix en FCFA"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val categorie = EditText(this).apply { hint = "Catégorie" }
        val stock = EditText(this).apply {
            hint = "Quantité disponible"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val description = EditText(this).apply {
            hint = "Description"
            minLines = 3
            gravity = Gravity.TOP
        }

        val photo = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 200)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(GRIS)
        }

        var uriPhoto = ""

        root.addView(photo)
        root.addView(bouton("Choisir une photo dans la galerie") {
            imageEnPreparation = ""
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, 500)
        })

        root.addView(nom)
        root.addView(prix)
        root.addView(categorie)
        root.addView(stock)
        root.addView(description)

        root.addView(boutonVert("Publier le produit") {
            val n = nom.text.toString().trim()
            val p = prix.text.toString().toIntOrNull()
            val cat = categorie.text.toString().trim()
            val q = stock.text.toString().toIntOrNull()
            val desc = description.text.toString().trim()

            if (
                n.isBlank() || p == null || p <= 0 ||
                cat.isBlank() || q == null || q < 0
            ) {
                toast("Vérifiez le nom, le prix, la catégorie et le stock.")
                return@boutonVert
            }

            val produit = Produit(
                id = System.currentTimeMillis().toString(),
                nom = n,
                prix = p,
                categorie = cat,
                description = desc,
                stock = q,
                imageUri = imageEnPreparation,
                vendeurId = c.id,
                entreprise = c.entreprise
            )

            produits.add(produit)
            sauvegarderProduits()
            enregistrerEvenement("Produit publié : $n")

            imageEnPreparation = ""

            toast("Produit publié avec succès.")
            afficherStockVendeur()
        })

        root.addView(bouton("Annuler") {
            imageEnPreparation = ""
            afficherEspaceVendeur()
        })

        afficherVue(scroll(root))
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 500 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return

            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Certains fournisseurs ne permettent pas de conserver l'autorisation.
            }

            imageEnPreparation = uri.toString()
            toast("Photo sélectionnée. Vous pouvez publier le produit.")
        }
    }

    // ========================================================
    // STOCK VENDEUR
    // ========================================================

    private fun afficherStockVendeur() {
        val root = layoutBase()
        root.addView(titre("📦 Mon stock"))

        val c = compteActuel()

        if (c == null) {
            root.addView(bouton("Connexion") { afficherConnexion() })
            afficherVue(scroll(root))
            return
        }

        val mesProduits = produits.filter { it.vendeurId == c.id }

        if (mesProduits.isEmpty()) {
            root.addView(texte("Vous n'avez pas encore de produits."))
        }

        mesProduits.forEach { p ->
            root.addView(texte(
                "${p.nom}\nPrix : ${formaterPrix(p.prix)}\nStock : ${p.stock}"
            ))

            root.addView(bouton("Modifier le stock") {
                val champ = EditText(this).apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setText(p.stock.toString())
                }

                AlertDialog.Builder(this)
                    .setTitle("Modifier le stock")
                    .setView(champ)
                    .setNegativeButton("Annuler", null)
                    .setPositiveButton("Enregistrer") { _, _ ->
                        val q = champ.text.toString().toIntOrNull()

                        if (q == null || q < 0) {
                            toast("Quantité invalide.")
                        } else {
                            p.stock = q
                            sauvegarderProduits()
                            afficherStockVendeur()
                        }
                    }
                    .show()
            })

            root.addView(bouton("Modifier le prix") {
                val champ = EditText(this).apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setText(p.prix.toString())
                }

                AlertDialog.Builder(this)
                    .setTitle("Modifier le prix")
                    .setView(champ)
                    .setNegativeButton("Annuler", null)
                    .setPositiveButton("Enregistrer") { _, _ ->
                        val prix = champ.text.toString().toIntOrNull()

                        if (prix == null || prix <= 0) {
                            toast("Prix invalide.")
                        } else {
                            p.prix = prix
                            sauvegarderProduits()
                            afficherStockVendeur()
                        }
                    }
                    .show()
            })

            root.addView(bouton("Supprimer le produit") {
                AlertDialog.Builder(this)
                    .setTitle("Supprimer le produit ?")
                    .setMessage(p.nom)
                    .setNegativeButton("Annuler", null)
                    .setPositiveButton("Supprimer") { _, _ ->
                        produits.remove(p)
                        sauvegarderProduits()
                        afficherStockVendeur()
                    }
                    .show()
            })
        }

        root.addView(boutonVert("Ajouter un produit") {
            afficherAjouterProduit()
        })

        root.addView(bouton("Retour") { afficherEspaceVendeur() })
        afficherVue(scroll(root))
    }

    // ========================================================
    // ADMINISTRATION
    // ========================================================

    private fun afficherConnexionAdmin() {
        val root = layoutBase()
        root.addView(titre("Administration"))

        val email = EditText(this).apply { hint = "E-mail administrateur" }
        val mdp = EditText(this).apply {
            hint = "Mot de passe"
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        root.addView(email)
        root.addView(mdp)

        root.addView(boutonVert("Se connecter") {
            if (
                email.text.toString().trim().equals(ADMIN_EMAIL, true) &&
                mdp.text.toString() == ADMIN_MOT_DE_PASSE
            ) {
                adminConnecte = true
                enregistrerEvenement("Connexion administrateur")
                afficherAdministration()
            } else {
                toast("Identifiants incorrects.")
            }
        })

        root.addView(bouton("Retour") { afficherAccueil() })
        afficherVue(scroll(root))
    }

    private fun afficherAdministration() {
        if (!adminConnecte) {
            afficherConnexionAdmin()
            return
        }

        val root = layoutBase()
        root.addView(titre("👑 Administration"))

        root.addView(texte(
            "Produits : ${produits.size}\n" +
                "Vendeurs : ${comptes.size}\n" +
                "Commandes : ${commandes.size}",
            18f,
            VERT
        ))

        root.addView(boutonVert("Gérer les produits") {
            afficherProduitsAdmin()
        })

        root.addView(boutonVert("Gérer les vendeurs") {
            afficherComptesAdmin()
        })

        root.addView(boutonVert("Voir les commandes") {
            afficherCommandesAdmin()
        })

        root.addView(bouton("Journal des activités") {
            afficherEvenementsAdmin()
        })

        root.addView(bouton("Déconnexion administrateur") {
            adminConnecte = false
            afficherAccueil()
        })

        afficherVue(scroll(root))
    }

    private fun afficherProduitsAdmin() {
        val root = layoutBase()
        root.addView(titre("Produits"))

        produits.toList().forEach { p ->
            root.addView(texte(
                "${p.nom}\nPrix : ${formaterPrix(p.prix)}\n" +
                    "Stock : ${p.stock}\nVendeur : ${p.entreprise}"
            ))

            root.addView(bouton("Supprimer") {
                produits.remove(p)
                sauvegarderProduits()
                afficherProduitsAdmin()
            })
        }

        root.addView(bouton("Retour") { afficherAdministration() })
        afficherVue(scroll(root))
    }

    private fun afficherComptesAdmin() {
        val root = layoutBase()
        root.addView(titre("Vendeurs"))

        comptes.forEach { c ->
            root.addView(texte(
                "${c.nom}\n${c.entreprise}\n${c.email}\n${c.telephone}\n" +
                    "Statut : ${if (c.valide) "Actif" else "Bloqué"}"
            ))

            root.addView(bouton(
                if (c.valide) "Bloquer" else "Réactiver"
            ) {
                c.valide = !c.valide
                sauvegarderComptes()
                afficherComptesAdmin()
            })

            root.addView(bouton("Activer un mois d'abonnement") {
                val maintenant = System.currentTimeMillis()

                c.abonnementJusqua =
                    maxOf(maintenant, c.abonnementJusqua) +
                        30L * 24 * 60 * 60 * 1000

                sauvegarderComptes()
                enregistrerEvenement("Abonnement activé : ${c.email}")
                toast("Un mois d'abonnement a été ajouté.")
                afficherComptesAdmin()
            })
        }

        root.addView(bouton("Retour") { afficherAdministration() })
        afficherVue(scroll(root))
    }

    private fun afficherCommandesAdmin() {
        val root = layoutBase()
        root.addView(titre("Commandes"))

        commandes.forEach { c ->
            root.addView(texte(
                "Client : ${c.client}\nTéléphone : ${c.telephone}\n" +
                    "Adresse : ${c.adresse}\n${c.details}\n" +
                    "Montant : ${formaterPrix(c.montant)}\n" +
                    "Statut : ${c.statut}"
            ))

            root.addView(bouton("Marquer comme traitée") {
                c.statut = "Traitée"
                sauvegarderCommandes()
                afficherCommandesAdmin()
            })
        }

        root.addView(bouton("Retour") { afficherAdministration() })
        afficherVue(scroll(root))
    }

    private fun afficherEvenementsAdmin() {
        val root = layoutBase()
        root.addView(titre("Journal des activités"))

        if (evenements.isEmpty()) {
            root.addView(texte("Aucune activité enregistrée."))
        } else {
            evenements.asReversed().forEach {
                root.addView(texte(it, 14f))
            }
        }

        root.addView(bouton("Retour") { afficherAdministration() })
        afficherVue(scroll(root))
    }

    // ========================================================
    // CONTACT ET PARTAGE
    // ========================================================

    private fun afficherContact() {
        val root = layoutBase()
        root.addView(titre("📞 Contact"))

        root.addView(texte(
            "Vie Espoir Marketing\n\n" +
                "Pour vos commandes, vos produits ou votre compte vendeur, " +
                "contactez-nous."
        ))

        root.addView(texte(
            "Orange Money / Wave : $TELEPHONE_CONTACT\n" +
                "MTN Money : $TELEPHONE_MTN"
        ))

        root.addView(boutonVert("Contacter par WhatsApp") {
            ouvrirWhatsApp(TELEPHONE_CONTACT)
        })

        root.addView(bouton("Retour") { afficherAccueil() })
        afficherVue(scroll(root))
    }

    private fun ouvrirWhatsApp(numero: String) {
        val chiffres = numero.filter { it.isDigit() }

        val international = if (chiffres.startsWith("225")) {
            chiffres
        } else {
            "225$chiffres"
        }

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/$international")
                )
            )
        } catch (_: Exception) {
            toast("Impossible d'ouvrir WhatsApp.")
        }
    }

    private fun partagerTexte(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }

        startActivity(
            Intent.createChooser(intent, "Partager Vie Espoir Marketing")
        )
    }

    // ========================================================
    // SAUVEGARDE DES DONNÉES
    // ========================================================

    private fun sauvegarderProduits() {
        val tableau = JSONArray()

        produits.forEach { p ->
            tableau.put(JSONObject().apply {
                put("id", p.id)
                put("nom", p.nom)
                put("prix", p.prix)
                put("categorie", p.categorie)
                put("description", p.description)
                put("stock", p.stock)
                put("imageUri", p.imageUri)
                put("vendeurId", p.vendeurId)
                put("entreprise", p.entreprise)
            })
        }

        preferences.edit()
            .putString(CLE_PRODUITS, tableau.toString())
            .apply()
    }

    private fun sauvegarderPanier() {
        val tableau = JSONArray()

        panier.forEach { p ->
            tableau.put(JSONObject().apply {
                put("produitId", p.produitId)
                put("nom", p.nom)
                put("prix", p.prix)
                put("quantite", p.quantite)
            })
        }

        preferences.edit()
            .putString(CLE_PANIER, tableau.toString())
            .apply()
    }

    private fun sauvegarderCommandes() {
        val tableau = JSONArray()

        commandes.forEach { c ->
            tableau.put(JSONObject().apply {
                put("id", c.id)
                put("client", c.client)
                put("telephone", c.telephone)
                put("adresse", c.adresse)
                put("details", c.details)
                put("montant", c.montant)
                put("paiement", c.paiement)
                put("statut", c.statut)
            })
        }

        preferences.edit()
            .putString(CLE_COMMANDES, tableau.toString())
            .apply()
    }

    private fun sauvegarderComptes() {
        val tableau = JSONArray()

        comptes.forEach { c ->
            tableau.put(JSONObject().apply {
                put("id", c.id)
                put("nom", c.nom)
                put("entreprise", c.entreprise)
                put("email", c.email)
                put("telephone", c.telephone)
                put("adresse", c.adresse)
                put("motDePasse", c.motDePasse)
                put("valide", c.valide)
                put("dateCreation", c.dateCreation)
                put("abonnementJusqua", c.abonnementJusqua)
            })
        }

        preferences.edit()
            .putString(CLE_COMPTES, tableau.toString())
            .apply()
    }

    private fun sauvegarderEvenements() {
        val tableau = JSONArray()

        evenements.forEach { tableau.put(it) }

        preferences.edit()
            .putString(CLE_EVENEMENTS, tableau.toString())
            .apply()
    }

    // ========================================================
    // CHARGEMENT DES DONNÉES
    // ========================================================

    private fun chargerDonnees() {

        try {
            val tableau = JSONArray(
                preferences.getString(CLE_PRODUITS, "[]")
            )

            for (i in 0 until tableau.length()) {
                val o = tableau.optJSONObject(i) ?: continue

                produits.add(
                    Produit(
                        id = o.optString("id", ""),
                        nom = o.optString("nom", ""),
                        prix = o.optInt(
                            "prix",
                            o.optString("prix").toIntOrNull() ?: 0
                        ),
                        categorie = o.optString("categorie", "Autre"),
                        description = o.optString("description", ""),
                        stock = o.optInt("stock", 0),
                        imageUri = o.optString("imageUri", ""),
                        vendeurId = o.optString("vendeurId", ""),
                        entreprise = o.optString("entreprise", "")
                    )
                )
            }
        } catch (_: Exception) {
            produits.clear()
        }

        try {
            val tableau = JSONArray(
                preferences.getString(CLE_PANIER, "[]")
            )

            for (i in 0 until tableau.length()) {
                val o = tableau.optJSONObject(i) ?: continue

                panier.add(
                    PanierItem(
                        produitId = o.optString("produitId", ""),
                        nom = o.optString("nom", ""),
                        prix = o.optInt("prix", 0),
                        quantite = o.optInt("quantite", 1)
                    )
                )
            }
        } catch (_: Exception) {
            panier.clear()
        }

        try {
            val tableau = JSONArray(
                preferences.getString(CLE_COMMANDES, "[]")
            )

            for (i in 0 until tableau.length()) {
                val o = tableau.optJSONObject(i) ?: continue

                commandes.add(
                    Commande(
                        id = o.optString("id", ""),
                        client = o.optString("client", ""),
                        telephone = o.optString("telephone", ""),
                        adresse = o.optString("adresse", ""),
                        details = o.optString(
                            "details",
                            o.optString("produits", "")
                        ),
                        montant = o.optInt(
                            "montant",
                            o.optString("montant").toIntOrNull() ?: 0
                        ),
                        paiement = o.optString("paiement", ""),
                        statut = o.optString("statut", "Nouvelle")
                    )
                )
            }
        } catch (_: Exception) {
            commandes.clear()
        }

        try {
            val tableau = JSONArray(
                preferences.getString(CLE_COMPTES, "[]")
            )

            for (i in 0 until tableau.length()) {
                val o = tableau.optJSONObject(i) ?: continue

                comptes.add(
                    Compte(
                        id = o.optString("id", ""),
                        nom = o.optString("nom", ""),
                        entreprise = o.optString("entreprise", ""),
                        email = o.optString("email", ""),
                        telephone = o.optString("telephone", ""),
                        adresse = o.optString("adresse", ""),
                        motDePasse = o.optString("motDePasse", ""),
                        valide = o.optBoolean("valide", true),
                        dateCreation = o.optLong(
                            "dateCreation",
                            System.currentTimeMillis()
                        ),
                        abonnementJusqua = o.optLong(
                            "abonnementJusqua",
                            0L
                        )
                    )
                )
            }
        } catch (_: Exception) {
            comptes.clear()
        }

        try {
            val tableau = JSONArray(
                preferences.getString(CLE_EVENEMENTS, "[]")
            )

            for (i in 0 until tableau.length()) {
                evenements.add(tableau.optString(i))
            }
        } catch (_: Exception) {
            evenements.clear()
        }
    }
}