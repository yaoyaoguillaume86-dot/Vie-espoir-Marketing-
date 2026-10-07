package com.vieespoir.marketing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList

data class Produit(
    val nom: String,
    val prix: String,
    val description: String,
    val stock: String,
    val photos: ArrayList<Uri>
)

class MainActivity : Activity() {

    private val produits = ArrayList<Produit>()

    private var zoneAjout: LinearLayout? = null
    private var photosSelectionnees = ArrayList<Uri>()

    private val CODE_PHOTOS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afficherAccueil()
    }

    // =========================
    // ACCUEIL
    // =========================

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 40, 30, 30)
        layout.gravity = Gravity.CENTER_HORIZONTAL
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "VIE ESPOIR MARKETING"
        title.textSize = 26f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Bienvenue dans votre espace"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 10, 0, 20)

        layout.addView(title)
        layout.addView(subtitle)

        ajouterBouton(layout, "📦 PRODUITS") {
            afficherProduits()
        }

        ajouterBouton(layout, "⚙️ GESTION DES PRODUITS") {
            ouvrirEspace("GESTION DES PRODUITS")
        }

        ajouterBouton(layout, "🛒 VENTES") {
            ouvrirEspace("VENTES")
        }

        ajouterBouton(layout, "📋 COMMANDES") {
            ouvrirEspace("COMMANDES")
        }

        ajouterBouton(layout, "📦 STOCK") {
            ouvrirEspace("GESTION DU STOCK")
        }

        ajouterBouton(layout, "👥 CLIENTS") {
            ouvrirEspace("CLIENTS")
        }

        ajouterBouton(layout, "💳 MOYENS DE PAIEMENT") {
            ouvrirEspace("MOYENS DE PAIEMENT")
        }

        ajouterBouton(layout, "🚚 LIVRAISON") {
            ouvrirEspace("LIVRAISON")
        }

        ajouterBouton(layout, "💰 COMMISSIONS") {
            ouvrirEspace("COMMISSIONS")
        }

        ajouterBouton(layout, "🆘 AIDE / ASSISTANCE") {
            afficherAide()
        }

        setContentView(layout)
    }

    // =========================
    // PRODUITS
    // =========================

    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 30, 25, 30)
        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)
        titre.text = "📦 MES PRODUITS"
        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER
        titre.setPadding(0, 0, 0, 20)

        layout.addView(titre)

        val ajouter = Button(this)
        ajouter.text = "➕ AJOUTER UN PRODUIT"
        ajouter.textSize = 16f

        ajouter.setOnClickListener {
            afficherFenetreAjout()
        }

        layout.addView(ajouter)

        afficherListeProduits(layout)

        val aide = Button(this)
        aide.text = "🆘 BESOIN D'AIDE ?"

        aide.setOnClickListener {
            afficherAide()
        }

        layout.addView(aide)

        val retour = Button(this)
        retour.text = "⬅ RETOUR"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================
    // LISTE DES PRODUITS
    // =========================

    private fun afficherListeProduits(layout: LinearLayout) {

        if (produits.isEmpty()) {

            val vide = TextView(this)
            vide.text = "Aucun produit ajouté pour le moment."
            vide.textSize = 17f
            vide.setTextColor(Color.DKGRAY)
            vide.gravity = Gravity.CENTER
            vide.setPadding(0, 30, 0, 30)

            layout.addView(vide)

        } else {

            for (produit in produits) {

                val ligne = LinearLayout(this)
                ligne.orientation = LinearLayout.VERTICAL
                ligne.setPadding(0, 15, 0, 15)

                val nom = TextView(this)
                nom.text = "📦 ${produit.nom}"
                nom.textSize = 20f
                nom.setTextColor(Color.BLACK)

                val prix = TextView(this)
                prix.text = "💰 Prix : ${produit.prix} FCFA"
                prix.textSize = 17f
                prix.setTextColor(Color.DKGRAY)

                val stock = TextView(this)
                stock.text = "📦 Stock : ${produit.stock}"
                stock.textSize = 16f
                stock.setTextColor(Color.DKGRAY)

                ligne.addView(nom)
                ligne.addView(prix)
                ligne.addView(stock)

                val voir = Button(this)
                voir.text = "👀 VOIR LE PRODUIT"

                voir.setOnClickListener {
                    afficherFicheProduit(produit)
                }

                ligne.addView(voir)

                val partager = Button(this)
                partager.text = "📤 PARTAGER"

                partager.setOnClickListener {
                    partagerProduit(produit)
                }

                ligne.addView(partager)

                layout.addView(ligne)

                val separateur = TextView(this)
                separateur.text = "────────────────────"
                separateur.gravity = Gravity.CENTER

                layout.addView(separateur)
            }
        }
    }

    // =========================
    // AJOUTER PRODUIT
    // =========================

    private fun afficherFenetreAjout() {

        photosSelectionnees.clear()

        val zone = LinearLayout(this)
        zone.orientation = LinearLayout.VERTICAL
        zone.setPadding(30, 10, 30, 10)

        zoneAjout = zone

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix du produit en FCFA"
        prix.inputType = 2

        val description = EditText(this)
        description.hint = "Description du produit : ce qu'il fait, utilisation, détails..."
        description.minLines = 4
        description.gravity = Gravity.TOP

        val stock = EditText(this)
        stock.hint = "Quantité en stock"
        stock.inputType = 2

        val photosTexte = TextView(this)
        photosTexte.text = "📷 Aucune photo sélectionnée"
        photosTexte.textSize = 16f
        photosTexte.setPadding(0, 15, 0, 15)

        val choisirPhotos = Button(this)
        choisirPhotos.text = "📷 AJOUTER PLUSIEURS PHOTOS"

        choisirPhotos.setOnClickListener {
            choisirPlusieursPhotos(photosTexte)
        }

        zone.addView(nom)
        zone.addView(prix)
        zone.addView(description)
        zone.addView(stock)
        zone.addView(photosTexte)
        zone.addView(choisirPhotos)

        AlertDialog.Builder(this)
            .setTitle("➕ Ajouter un produit")
            .setView(zone)
            .setNegativeButton("ANNULER", null)
            .setPositiveButton("ENREGISTRER") { _, _ ->

                val nomProduit = nom.text.toString().trim()
                val prixProduit = prix.text.toString().trim()
                val descriptionProduit = description.text.toString().trim()
                val stockProduit = stock.text.toString().trim()

                if (nomProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Veuillez entrer le nom du produit",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val produit = Produit(
                    nomProduit,
                    prixProduit,
                    descriptionProduit,
                    stockProduit,
                    ArrayList(photosSelectionnees)
                )

                produits.add(produit)

                Toast.makeText(
                    this,
                    "Produit ajouté avec succès",
                    Toast.LENGTH_SHORT
                ).show()

                afficherProduits()
            }
            .show()
    }

    // =========================
    // CHOISIR PLUSIEURS PHOTOS
    // =========================

    private fun choisirPlusieursPhotos(texte: TextView) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        intent.type = "image/*"

        intent.putExtra(
            Intent.EXTRA_ALLOW_MULTIPLE,
            true
        )

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        startActivityForResult(
            intent,
            CODE_PHOTOS
        )
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
            requestCode == CODE_PHOTOS &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            photosSelectionnees.clear()

            val clipData = data.clipData

            if (clipData != null) {

                for (i in 0 until clipData.itemCount) {

                    photosSelectionnees.add(
                        clipData.getItemAt(i).uri
                    )
                }

            } else {

                data.data?.let {
                    photosSelectionnees.add(it)
                }
            }

            Toast.makeText(
                this,
                "${photosSelectionnees.size} photo(s) sélectionnée(s)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // FICHE PRODUIT
    // =========================

    private fun afficherFicheProduit(produit: Produit) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 30, 25, 30)
        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)
        titre.text = produit.nom
        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val photos = TextView(this)
        photos.text =
            if (produit.photos.isEmpty()) {
                "📷 Aucune photo"
            } else {
                "📷 ${produit.photos.size} photo(s) disponible(s)"
            }

        photos.textSize = 17f
        photos.gravity = Gravity.CENTER
        photos.setPadding(0, 20, 0, 20)

        layout.addView(photos)

        val prix = TextView(this)
        prix.text = "💰 ${produit.prix} FCFA"
        prix.textSize = 20f
        prix.gravity = Gravity.CENTER

        layout.addView(prix)

        val stock = TextView(this)
        stock.text = "📦 Stock disponible : ${produit.stock}"
        stock.textSize = 17f
        stock.gravity = Gravity.CENTER

        layout.addView(stock)

        val description = TextView(this)
        description.text =
            if (produit.description.isEmpty()) {
                "Aucune description disponible."
            } else {
                "📝 DESCRIPTION\n\n${produit.description}"
            }

        description.textSize = 17f
        description.setPadding(0, 20, 0, 20)

        layout.addView(description)

        val commander = Button(this)
        commander.text = "🛒 COMMANDER"

        commander.setOnClickListener {
            afficherCommande(produit)
        }

        layout.addView(commander)

        val aide = Button(this)
        aide.text = "🆘 BESOIN D'AIDE ?"

        aide.setOnClickListener {
            afficherAide()
        }

        layout.addView(aide)

        val retour = Button(this)
        retour.text = "⬅ RETOUR"

        retour.setOnClickListener {
            afficherProduits()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================
    // COMMANDER
    // =========================

    private fun afficherCommande(produit: Produit) {

        val zone = LinearLayout(this)
        zone.orientation = LinearLayout.VERTICAL
        zone.setPadding(30, 10, 30, 10)

        val nom = EditText(this)
        nom.hint = "Nom et prénom"

        val telephone = EditText(this)
        telephone.hint = "Numéro de téléphone"
        telephone.inputType = 3

        val quantite = EditText(this)
        quantite.hint = "Quantité"
        quantite.inputType = 2

        val adresse = EditText(this)
        adresse.hint = "Lieu / adresse de livraison"

        val paiement = Spinner(this)

        val moyens = arrayOf(
            "Espèces à la livraison",
            "Paiement à la livraison",
            "Mobile Money",
            "Paiement en ligne"
        )

        paiement.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            moyens
        )

        zone.addView(nom)
        zone.addView(telephone)
        zone.addView(quantite)
        zone.addView(adresse)
        zone.addView(paiement)

        AlertDialog.Builder(this)
            .setTitle("🛒 Commander : ${produit.nom}")
            .setView(zone)
            .setNegativeButton("ANNULER", null)
            .setPositiveButton("CONFIRMER") { _, _ ->

                if (
                    nom.text.toString().trim().isEmpty() ||
                    telephone.text.toString().trim().isEmpty() ||
                    quantite.text.toString().trim().isEmpty() ||
                    adresse.text.toString().trim().isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Veuillez remplir toutes les informations",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setPositiveButton
                }

                Toast.makeText(
                    this,
                    "Commande enregistrée avec succès",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    // =========================
    // PARTAGER
    // =========================

    private fun partagerProduit(produit: Produit) {

        val message =
            "📦 VIE ESPOIR MARKETING\n\n" +
            "Produit : ${produit.nom}\n" +
            "Prix : ${produit.prix} FCFA\n\n" +
            "${produit.description}\n\n" +
            "Pour commander, contactez-nous."

        val intent = Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"

        intent.putExtra(
            Intent.EXTRA_TEXT,
            message
        )

        try {

            startActivity(
                Intent.createChooser(
                    intent,
                    "Partager le produit avec"
                )
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Aucune application de partage disponible",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // AIDE
    // =========================

    private fun afficherAide() {

        AlertDialog.Builder(this)
            .setTitle("🆘 BESOIN D'AIDE ?")
            .setMessage(
                "Vous ne comprenez pas comment utiliser " +
                "l'application ou commander un produit ?\n\n" +
                "Contactez notre assistance."
            )
            .setPositiveButton("💬 WHATSAPP") { _, _ ->

                Toast.makeText(
                    this,
                    "Nous allons configurer ton WhatsApp ici.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNeutralButton("🔵 FACEBOOK") { _, _ ->

                Toast.makeText(
                    this,
                    "Nous allons configurer ton Facebook ici.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("FERMER", null)
            .show()
    }

    // =========================
    // AUTRES ESPACES
    // =========================

    private fun ouvrirEspace(nom: String) {

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(30, 40, 30, 30)

        layout.gravity = Gravity.CENTER_HORIZONTAL

        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)

        titre.text = nom

        titre.textSize = 26f

        titre.setTextColor(Color.BLACK)

        titre.gravity = Gravity.CENTER

        titre.setPadding(0, 0, 0, 30)

        layout.addView(titre)

        val message = TextView(this)

        message.text =
            "Bienvenue dans l'espace $nom"

        message.textSize = 18f

        message.setTextColor(Color.DKGRAY)

        message.gravity = Gravity.CENTER

        message.setPadding(0, 0, 0, 30)

        layout.addView(message)

        val retour = Button(this)

        retour.text = "⬅ RETOUR"

        retour.setOnClickListener {

            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================
    // BOUTONS
    // =========================

    private fun ajouterBouton(
        layout: LinearLayout,
        texte: String,
        action: () -> Unit
    ) {

        val bouton = Button(this)

        bouton.text = texte

        bouton.textSize = 16f

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            0,
            6,
            0,
            6
        )

        bouton.layoutParams = params

        bouton.setOnClickListener {

            action()
        }

        layout.addView(bouton)
    }
}