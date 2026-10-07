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
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList

data class Produit(
    val nom: String,
    val prix: String,
    val description: String,
    val stock: String,
    val photos: ArrayList<String>
)

data class Commande(
    val produitNom: String,
    val nomClient: String,
    val telephone: String,
    val quantite: String,
    val adresse: String,
    val paiement: String
)

data class Abonnement(
    val pack: String,
    val prix: String,
    val duree: String,
    val actif: Boolean
)

class MainActivity : Activity() {

    private val produits = ArrayList<Produit>()
    private val commandes = ArrayList<Commande>()

    private val photosSelectionnees = ArrayList<String>()

    private var abonnement: Abonnement? = null

    private val CODE_GALERIE = 1001
    private val CODE_CAMERA = 1002
    private val CODE_PERMISSION_CAMERA = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chargerDonnees()
        afficherAccueil()
    }

    // =========================================================
    // ACCUEIL
    // =========================================================

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 40, 30, 30)
        layout.gravity = Gravity.CENTER_HORIZONTAL
        layout.setBackgroundColor(Color.WHITE)

        val logo = TextView(this)
        logo.text = "🦁"
        logo.textSize = 55f
        logo.gravity = Gravity.CENTER

        val title = TextView(this)
        title.text = "VIE ESPOIR MARKETING"
        title.textSize = 26f
        title.setTextColor(Color.rgb(0, 120, 60))
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Votre plateforme de vente"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 10, 0, 20)

        layout.addView(logo)
        layout.addView(title)
        layout.addView(subtitle)

        // ABONNEMENT
        ajouterBouton(layout, "⭐ ABONNEMENT VENDEUR") {
            afficherAbonnement()
        }

        ajouterBouton(layout, "📦 PRODUITS") {
            afficherProduits()
        }

        ajouterBouton(layout, "⚙️ GESTION DES PRODUITS") {
            ouvrirEspace("GESTION DES PRODUITS")
        }

        ajouterBouton(layout, "💰 VENTES") {
            ouvrirEspace("VENTES")
        }

        ajouterBouton(layout, "📋 COMMANDES") {
            afficherCommandes()
        }

        ajouterBouton(layout, "📦 GESTION DU STOCK") {
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

    // =========================================================
    // ABONNEMENT
    // =========================================================

    private fun afficherAbonnement() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 30, 25, 30)
        layout.setBackgroundColor(Color.WHITE)

        val titre = TextView(this)
        titre.text = "⭐ ABONNEMENT VENDEUR"
        titre.textSize = 26f
        titre.setTextColor(Color.rgb(0, 120, 60))
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val info = TextView(this)

        info.text = if (abonnement?.actif == true) {
            "🟢 ABONNEMENT ACTIF\n\n" +
            "Pack : ${abonnement!!.pack}\n" +
            "Prix : ${abonnement!!.prix} FCFA\n" +
            "Durée : ${abonnement!!.duree}"
        } else {
            "🔴 Aucun abonnement actif.\n\n" +
            "Choisissez un pack pour commencer à vendre."
        }

        info.textSize = 18f
        info.gravity = Gravity.CENTER
        info.setPadding(0, 25, 0, 30)

        layout.addView(info)

        ajouterBouton(
            layout,
            "🥉 PACK STARTER — 5 000 FCFA / MOIS"
        ) {
            choisirAbonnement(
                "PACK STARTER",
                "5000",
                "1 mois"
            )
        }

        ajouterBouton(
            layout,
            "🥈 PACK PRO — 10 000 FCFA / 3 MOIS"
        ) {
            choisirAbonnement(
                "PACK PRO",
                "10000",
                "3 mois"
            )
        }

        ajouterBouton(
            layout,
            "🥇 PACK PREMIUM — 30 000 FCFA / AN"
        ) {
            choisirAbonnement(
                "PACK PREMIUM",
                "30000",
                "12 mois"
            )
        }

        if (abonnement?.actif == true) {

            ajouterBouton(
                layout,
                "👤 MON ESPACE VENDEUR"
            ) {
                afficherEspaceVendeur()
            }
        }

        val retour = Button(this)
        retour.text = "🏠 RETOUR À L'ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    private fun choisirAbonnement(
        pack: String,
        prix: String,
        duree: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("⭐ $pack")
            .setMessage(
                "Prix : $prix FCFA\n" +
                "Durée : $duree\n\n" +
                "Après paiement et validation, " +
                "votre espace vendeur sera activé."
            )
            .setNegativeButton(
                "ANNULER",
                null
            )
            .setPositiveButton(
                "CONTINUER"
            ) { _, _ ->

                afficherPaiementAbonnement(
                    pack,
                    prix,
                    duree
                )
            }
            .show()
    }

    // =========================================================
    // PAIEMENT ABONNEMENT
    // =========================================================

    private fun afficherPaiementAbonnement(
        pack: String,
        prix: String,
        duree: String
    ) {

        val moyens = arrayOf(
            "Orange Money",
            "MTN Mobile Money",
            "Moov Money",
            "Wave",
            "Paiement manuel"
        )

        val choix = Spinner(this)

        choix.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            moyens
        )

        val zone = LinearLayout(this)
        zone.orientation = LinearLayout.VERTICAL
        zone.setPadding(30, 10, 30, 10)

        val info = TextView(this)

        info.text =
            "Pack : $pack\n" +
            "Montant : $prix FCFA\n" +
            "Durée : $duree\n\n" +
            "Choisissez votre moyen de paiement :"

        info.textSize = 17f

        zone.addView(info)
        zone.addView(choix)

        AlertDialog.Builder(this)
            .setTitle("💳 PAIEMENT")
            .setView(zone)
            .setNegativeButton(
                "ANNULER",
                null
            )
            .setPositiveButton(
                "J'AI EFFECTUÉ LE PAIEMENT"
            ) { _, _ ->

                // Pour le moment, on enregistre
                // la demande d'abonnement.
                abonnement = Abonnement(
                    pack,
                    prix,
                    duree,
                    true
                )

                sauvegarderDonnees()

                Toast.makeText(
                    this,
                    "Demande d'abonnement enregistrée.",
                    Toast.LENGTH_LONG
                ).show()

                afficherAbonnement()
            }
            .show()
    }

    // =========================================================
    // ESPACE VENDEUR
    // =========================================================

    private fun afficherEspaceVendeur() {

        if (abonnement?.actif != true) {

            Toast.makeText(
                this,
                "Vous devez avoir un abonnement actif.",
                Toast.LENGTH_LONG
            ).show()

            afficherAbonnement()
            return
        }

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "👤 ESPACE VENDEUR"
        titre.textSize = 27f
        titre.gravity = Gravity.CENTER
        titre.setTextColor(Color.rgb(0, 120, 60))

        layout.addView(titre)

        val info = TextView(this)

        info.text =
            "🟢 Compte vendeur actif\n\n" +
            "Pack : ${abonnement!!.pack}\n" +
            "Durée : ${abonnement!!.duree}\n" +
            "Abonnement : ${abonnement!!.prix} FCFA"

        info.textSize = 18f
        info.gravity = Gravity.CENTER
        info.setPadding(0, 25, 0, 30)

        layout.addView(info)

        ajouterBouton(layout, "📦 MES PRODUITS") {
            afficherProduits()
        }

        ajouterBouton(layout, "📋 MES COMMANDES") {
            afficherCommandes()
        }

        ajouterBouton(layout, "💰 MES VENTES") {
            afficherVentes()
        }

        ajouterBouton(layout, "📦 MON STOCK") {
            afficherStock()
        }

        val retour = Button(this)
        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // PRODUITS
    // =========================================================

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

        ajouter.setOnClickListener {
            afficherFenetreAjout()
        }

        layout.addView(ajouter)

        afficherListeProduits(layout)

        val retour = Button(this)

        retour.text = "🏠 RETOUR À L'ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    private fun afficherListeProduits(
        layout: LinearLayout
    ) {

        if (produits.isEmpty()) {

            val vide = TextView(this)

            vide.text =
                "Aucun produit ajouté pour le moment."

            vide.textSize = 17f
            vide.gravity = Gravity.CENTER
            vide.setPadding(0, 30, 0, 30)

            layout.addView(vide)

            return
        }

        for (produit in produits) {

            val ligne = LinearLayout(this)

            ligne.orientation = LinearLayout.VERTICAL
            ligne.setPadding(0, 15, 0, 15)

            if (produit.photos.isNotEmpty()) {

                val photo = ImageView(this)

                photo.setImageURI(
                    Uri.parse(produit.photos[0])
                )

                photo.adjustViewBounds = true
                photo.scaleType =
                    ImageView.ScaleType.CENTER_INSIDE

                photo.layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        400
                    )

                ligne.addView(photo)
            }

            val nom = TextView(this)

            nom.text = "📦 ${produit.nom}"
            nom.textSize = 20f

            ligne.addView(nom)

            val prix = TextView(this)

            prix.text =
                "💰 Prix : ${produit.prix} FCFA"

            prix.textSize = 17f

            ligne.addView(prix)

            val stock = TextView(this)

            stock.text =
                "📦 Stock : ${produit.stock}"

            stock.textSize = 16f

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
        }
    }

    // =========================================================
    // AJOUT PRODUIT
    // =========================================================

    private fun afficherFenetreAjout() {

        photosSelectionnees.clear()

        val zone = LinearLayout(this)

        zone.orientation = LinearLayout.VERTICAL
        zone.setPadding(30, 10, 30, 10)

        val nom = EditText(this)
        nom.hint = "Nom du produit"

        val prix = EditText(this)
        prix.hint = "Prix en FCFA"
        prix.inputType = 2

        val description = EditText(this)
        description.hint = "Description"
        description.minLines = 4
        description.gravity = Gravity.TOP

        val stock = EditText(this)
        stock.hint = "Quantité en stock"
        stock.inputType = 2

        val photosTexte = TextView(this)

        photosTexte.text =
            "📷 0 photo sélectionnée"

        photosTexte.textSize = 16f

        val camera = Button(this)

        camera.text = "📷 PRENDRE UNE PHOTO"

        camera.setOnClickListener {

            if (
                checkSelfPermission(
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    CODE_PERMISSION_CAMERA
                )

            } else {

                ouvrirCamera()
            }
        }

        val galerie = Button(this)

        galerie.text = "🖼️ AJOUTER PLUSIEURS PHOTOS"

        galerie.setOnClickListener {
            choisirPlusieursPhotos()
        }

        zone.addView(nom)
        zone.addView(prix)
        zone.addView(description)
        zone.addView(stock)
        zone.addView(photosTexte)
        zone.addView(camera)
        zone.addView(galerie)

        val dialogue =
            AlertDialog.Builder(this)
                .setTitle("➕ Ajouter un produit")
                .setView(zone)
                .setNegativeButton(
                    "ANNULER",
                    null
                )
                .setPositiveButton(
                    "ENREGISTRER",
                    null
                )
                .create()

        dialogue.setOnShowListener {

            dialogue.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val nomProduit =
                    nom.text.toString().trim()

                val prixProduit =
                    prix.text.toString().trim()

                val descriptionProduit =
                    description.text.toString().trim()

                val stockProduit =
                    stock.text.toString().trim()

                if (nomProduit.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Entrez le nom du produit.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                produits.add(
                    Produit(
                        nomProduit,
                        prixProduit,
                        descriptionProduit,
                        stockProduit,
                        ArrayList(
                            photosSelectionnees
                        )
                    )
                )

                sauvegarderDonnees()

                dialogue.dismiss()

                Toast.makeText(
                    this,
                    "Produit enregistré.",
                    Toast.LENGTH_SHORT
                ).show()

                afficherProduits()
            }
        }

        dialogue.show()
    }

    // =========================================================
    // GALERIE
    // =========================================================

    private fun choisirPlusieursPhotos() {

        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT)

        intent.type = "image/*"

        intent.putExtra(
            Intent.EXTRA_ALLOW_MULTIPLE,
            true
        )

        intent.addCategory(
            Intent.CATEGORY_OPENABLE
        )

        startActivityForResult(
            intent,
            CODE_GALERIE
        )
    }

    // =========================================================
    // CAMERA
    // =========================================================

    private fun ouvrirCamera() {

        try {

            val intent =
                Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )

            startActivityForResult(
                intent,
                CODE_CAMERA
            )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Impossible d'ouvrir la caméra.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================================================
    // RESULTATS PHOTOS
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
            requestCode == CODE_GALERIE &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            val clipData = data.clipData

            if (clipData != null) {

                for (
                    i in 0 until clipData.itemCount
                ) {

                    val uri =
                        clipData.getItemAt(i).uri

                    val copie =
                        copierPhotoDansApplication(uri)

                    if (copie != null) {
                        photosSelectionnees.add(copie)
                    }
                }

            } else {

                data.data?.let {

                    val copie =
                        copierPhotoDansApplication(it)

                    if (copie != null) {
                        photosSelectionnees.add(copie)
                    }
                }
            }

            Toast.makeText(
                this,
                "${photosSelectionnees.size} photo(s) ajoutée(s)",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (
            requestCode == CODE_CAMERA &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            val bitmap =
                data.extras?.get("data") as? Bitmap

            if (bitmap != null) {

                val chemin =
                    sauvegarderPhotoCamera(bitmap)

                if (chemin != null) {

                    photosSelectionnees.add(chemin)

                    Toast.makeText(
                        this,
                        "Photo ajoutée.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // =========================================================
    // SAUVEGARDER PHOTO
    // =========================================================

    private fun copierPhotoDansApplication(
        uri: Uri
    ): String? {

        return try {

            val dossier =
                File(filesDir, "photos_produits")

            if (!dossier.exists()) {
                dossier.mkdirs()
            }

            val fichier =
                File(
                    dossier,
                    "photo_${System.currentTimeMillis()}.jpg"
                )

            val input =
                contentResolver.openInputStream(uri)

            val output =
                FileOutputStream(fichier)

            if (input != null) {

                input.copyTo(output)
                input.close()
            }

            output.close()

            fichier.absolutePath

        } catch (_: Exception) {

            null
        }
    }

    private fun sauvegarderPhotoCamera(
        bitmap: Bitmap
    ): String? {

        return try {

            val dossier =
                File(filesDir, "photos_produits")

            if (!dossier.exists()) {
                dossier.mkdirs()
            }

            val fichier =
                File(
                    dossier,
                    "camera_${System.currentTimeMillis()}.jpg"
                )

            val output =
                FileOutputStream(fichier)

            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                output
            )

            output.flush()
            output.close()

            fichier.absolutePath

        } catch (_: Exception) {

            null
        }
    }

    // =========================================================
    // FICHE PRODUIT
    // =========================================================

    private fun afficherFicheProduit(
        produit: Produit
    ) {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = produit.nom
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        for (chemin in produit.photos) {

            val image = ImageView(this)

            image.setImageURI(
                Uri.parse(chemin)
            )

            image.adjustViewBounds = true
            image.scaleType =
                ImageView.ScaleType.CENTER_INSIDE

            image.layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    400
                )

            layout.addView(image)
        }

        val prix = TextView(this)

        prix.text =
            "💰 ${produit.prix} FCFA"

        prix.textSize = 20f
        prix.gravity = Gravity.CENTER

        layout.addView(prix)

        val stock = TextView(this)

        stock.text =
            "📦 Stock : ${produit.stock}"

        stock.textSize = 17f
        stock.gravity = Gravity.CENTER

        layout.addView(stock)

        val description = TextView(this)

        description.text =
            "📝 DESCRIPTION\n\n" +
            if (produit.description.isEmpty())
                "Aucune description."
            else
                produit.description

        description.textSize = 17f

        layout.addView(description)

        val commander = Button(this)

        commander.text = "🛒 COMMANDER"

        commander.setOnClickListener {
            afficherCommande(produit)
        }

        layout.addView(commander)

        val retour = Button(this)

        retour.text = "⬅ RETOUR"

        retour.setOnClickListener {
            afficherProduits()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // COMMANDER
    // =========================================================

    private fun afficherCommande(
        produit: Produit
    ) {

        val zone = LinearLayout(this)

        zone.orientation =
            LinearLayout.VERTICAL

        zone.setPadding(
            30,
            10,
            30,
            10
        )

        val nom = EditText(this)
        nom.hint = "Nom et prénom du client"

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

        paiement.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                moyens
            )

        zone.addView(nom)
        zone.addView(telephone)
        zone.addView(quantite)
        zone.addView(adresse)
        zone.addView(paiement)

        val dialogue =
            AlertDialog.Builder(this)
                .setTitle(
                    "🛒 Commander : ${produit.nom}"
                )
                .setView(zone)
                .setNegativeButton(
                    "ANNULER",
                    null
                )
                .setPositiveButton(
                    "CONFIRMER",
                    null
                )
                .create()

        dialogue.setOnShowListener {

            dialogue.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val nomClient =
                    nom.text.toString().trim()

                val tel =
                    telephone.text.toString().trim()

                val qte =
                    quantite.text.toString().trim()

                val adr =
                    adresse.text.toString().trim()

                if (
                    nomClient.isEmpty() ||
                    tel.isEmpty() ||
                    qte.isEmpty() ||
                    adr.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Veuillez remplir toutes les informations.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@setOnClickListener
                }

                commandes.add(
                    Commande(
                        produit.nom,
                        nomClient,
                        tel,
                        qte,
                        adr,
                        paiement.selectedItem.toString()
                    )
                )

                sauvegarderDonnees()

                dialogue.dismiss()

                Toast.makeText(
                    this,
                    "Commande enregistrée avec succès.",
                    Toast.LENGTH_LONG
                ).show()

                afficherAccueil()
            }
        }

        dialogue.show()
    }

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun afficherCommandes() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "📋 MES COMMANDES"
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        if (commandes.isEmpty()) {

            val vide = TextView(this)

            vide.text =
                "Aucune commande reçue."

            vide.textSize = 18f
            vide.gravity = Gravity.CENTER

            layout.addView(vide)

        } else {

            for (commande in commandes) {

                val bloc = TextView(this)

                bloc.text =
                    "📦 PRODUIT : ${commande.produitNom}\n" +
                    "👤 Client : ${commande.nomClient}\n" +
                    "📞 Téléphone : ${commande.telephone}\n" +
                    "🔢 Quantité : ${commande.quantite}\n" +
                    "📍 Livraison : ${commande.adresse}\n" +
                    "💳 Paiement : ${commande.paiement}\n" +
                    "──────────────────"

                bloc.textSize = 17f

                bloc.setPadding(
                    0,
                    15,
                    0,
                    15
                )

                layout.addView(bloc)
            }
        }

        val retour = Button(this)

        retour.text = "🏠 RETOUR À L'ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // GESTION PRODUITS
    // =========================================================

    private fun afficherGestionProduits() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "⚙️ GESTION DES PRODUITS"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val info = TextView(this)

        info.text =
            "Nombre de produits : ${produits.size}"

        info.textSize = 18f

        layout.addView(info)

        val ajouter = Button(this)

        ajouter.text = "➕ AJOUTER UN PRODUIT"

        ajouter.setOnClickListener {
            afficherFenetreAjout()
        }

        layout.addView(ajouter)

        for (produit in produits) {

            val texte = TextView(this)

            texte.text =
                "📦 ${produit.nom}\n" +
                "💰 ${produit.prix} FCFA\n" +
                "📦 Stock : ${produit.stock}"

            texte.textSize = 18f

            layout.addView(texte)

            val voir = Button(this)

            voir.text = "👀 VOIR"

            voir.setOnClickListener {
                afficherFicheProduit(produit)
            }

            layout.addView(voir)
        }

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // STOCK
    // =========================================================

    private fun afficherStock() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "📦 GESTION DU STOCK"
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        for (produit in produits) {

            val bloc = LinearLayout(this)

            bloc.orientation =
                LinearLayout.VERTICAL

            val stockActuel =
                produit.stock.toIntOrNull() ?: 0

            val texte = TextView(this)

            texte.text =
                when {
                    stockActuel <= 0 ->
                        "🔴 ${produit.nom} : RUPTURE DE STOCK"

                    stockActuel <= 5 ->
                        "🟠 ${produit.nom} : STOCK FAIBLE ($stockActuel)"

                    else ->
                        "🟢 ${produit.nom} : $stockActuel"
                }

            texte.textSize = 18f

            bloc.addView(texte)

            val ajouter = Button(this)

            ajouter.text = "➕ AJOUTER"

            ajouter.setOnClickListener {
                modifierStock(produit, true)
            }

            bloc.addView(ajouter)

            val retirer = Button(this)

            retirer.text = "➖ RETIRER"

            retirer.setOnClickListener {
                modifierStock(produit, false)
            }

            bloc.addView(retirer)

            layout.addView(bloc)
        }

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    private fun modifierStock(
        produit: Produit,
        ajouter: Boolean
    ) {

        val zone = EditText(this)

        zone.hint = "Quantité"
        zone.inputType = 2

        val dialogue =
            AlertDialog.Builder(this)
                .setTitle(
                    if (ajouter)
                        "➕ Ajouter au stock"
                    else
                        "➖ Retirer du stock"
                )
                .setMessage(
                    "Produit : ${produit.nom}\n" +
                    "Stock actuel : ${produit.stock}"
                )
                .setView(zone)
                .setNegativeButton(
                    "ANNULER",
                    null
                )
                .setPositiveButton(
                    "VALIDER",
                    null
                )
                .create()

        dialogue.setOnShowListener {

            dialogue.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val qte =
                    zone.text.toString().toIntOrNull()

                if (qte == null || qte <= 0) {

                    Toast.makeText(
                        this,
                        "Quantité invalide.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val ancien =
                    produit.stock.toIntOrNull() ?: 0

                if (!ajouter && qte > ancien) {

                    Toast.makeText(
                        this,
                        "Stock insuffisant.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val nouveau =
                    if (ajouter)
                        ancien + qte
                    else
                        ancien - qte

                val index =
                    produits.indexOf(produit)

                if (index >= 0) {

                    produits[index] =
                        Produit(
                            produit.nom,
                            produit.prix,
                            produit.description,
                            nouveau.toString(),
                            ArrayList(produit.photos)
                        )
                }

                sauvegarderDonnees()

                dialogue.dismiss()

                afficherStock()
            }
        }

        dialogue.show()
    }

    // =========================================================
    // VENTES
    // =========================================================

    private fun afficherVentes() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "💰 VENTES"
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        var total = 0

        for (commande in commandes) {

            val produit =
                produits.find {
                    it.nom == commande.produitNom
                }

            val prix =
                produit?.prix?.toIntOrNull() ?: 0

            val qte =
                commande.quantite.toIntOrNull() ?: 0

            total += prix * qte
        }

        val chiffre = TextView(this)

        chiffre.text =
            "💰 CHIFFRE D'AFFAIRES\n\n" +
            "$total FCFA\n\n" +
            "🛒 Commandes : ${commandes.size}"

        chiffre.textSize = 21f
        chiffre.gravity = Gravity.CENTER

        layout.addView(chiffre)

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // CLIENTS
    // =========================================================

    private fun afficherClients() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "👥 CLIENTS"
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val clients =
            commandes.map {
                it.nomClient
            }.distinct()

        if (clients.isEmpty()) {

            val vide = TextView(this)

            vide.text =
                "Aucun client enregistré."

            vide.textSize = 18f

            layout.addView(vide)

        } else {

            for (client in clients) {

                val texte = TextView(this)

                texte.text = "👤 $client"
                texte.textSize = 18f

                texte.setPadding(
                    0,
                    15,
                    0,
                    15
                )

                layout.addView(texte)
            }
        }

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // PAIEMENTS
    // =========================================================

    private fun afficherPaiements() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "💳 MOYENS DE PAIEMENT"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val moyens = arrayOf(
            "💵 Espèces à la livraison",
            "💵 Paiement à la livraison",
            "📱 Orange Money",
            "📱 MTN Mobile Money",
            "📱 Moov Money",
            "📱 Wave",
            "💳 Paiement en ligne"
        )

        for (moyen in moyens) {

            val texte = TextView(this)

            texte.text = "✓ $moyen"
            texte.textSize = 18f

            texte.setPadding(
                0,
                12,
                0,
                12
            )

            layout.addView(texte)
        }

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // LIVRAISON
    // =========================================================

    private fun afficherLivraisons() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "🚚 LIVRAISONS"
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        for (commande in commandes) {

            val texte = TextView(this)

            texte.text =
                "📦 ${commande.produitNom}\n" +
                "👤 ${commande.nomClient}\n" +
                "📞 ${commande.telephone}\n" +
                "📍 ${commande.adresse}\n" +
                "💳 ${commande.paiement}\n" +
                "──────────────"

            texte.textSize = 17f

            texte.setPadding(
                0,
                15,
                0,
                15
            )

            layout.addView(texte)
        }

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // COMMISSIONS
    // =========================================================

    private fun afficherCommissions() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(25, 30, 25, 30)

        val titre = TextView(this)

        titre.text = "💰 COMMISSIONS"
        titre.textSize = 26f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val info = TextView(this)

        info.text =
            "La gestion détaillée des commissions " +
            "sera ajoutée dans une prochaine version."

        info.textSize = 18f
        info.gravity = Gravity.CENTER

        layout.addView(info)

        val retour = Button(this)

        retour.text = "🏠 ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // SAUVEGARDE
    // =========================================================

    private fun sauvegarderDonnees() {

        try {

            val preferences =
                getSharedPreferences(
                    "vie_espoir_donnees",
                    MODE_PRIVATE
                )

            val produitsJson = JSONArray()

            for (produit in produits) {

                val objet = JSONObject()

                objet.put("nom", produit.nom)
                objet.put("prix", produit.prix)
                objet.put(
                    "description",
                    produit.description
                )
                objet.put("stock", produit.stock)

                val photos = JSONArray()

                for (photo in produit.photos) {
                    photos.put(photo)
                }

                objet.put("photos", photos)

                produitsJson.put(objet)
            }

            val commandesJson = JSONArray()

            for (commande in commandes) {

                val objet = JSONObject()

                objet.put(
                    "produit",
                    commande.produitNom
                )

                objet.put(
                    "nomClient",
                    commande.nomClient
                )

                objet.put(
                    "telephone",
                    commande.telephone
                )

                objet.put(
                    "quantite",
                    commande.quantite
                )

                objet.put(
                    "adresse",
                    commande.adresse
                )

                objet.put(
                    "paiement",
                    commande.paiement
                )

                commandesJson.put(objet)
            }

            val abonnementJson =
                JSONObject()

            abonnementJson.put(
                "actif",
                abonnement?.actif ?: false
            )

            abonnementJson.put(
                "pack",
                abonnement?.pack ?: ""
            )

            abonnementJson.put(
                "prix",
                abonnement?.prix ?: ""
            )

            abonnementJson.put(
                "duree",
                abonnement?.duree ?: ""
            )

            preferences.edit()
                .putString(
                    "produits",
                    produitsJson.toString()
                )
                .putString(
                    "commandes",
                    commandesJson.toString()
                )
                .putString(
                    "abonnement",
                    abonnementJson.toString()
                )
                .apply()

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // CHARGEMENT
    // =========================================================

    private fun chargerDonnees() {

        try {

            val preferences =
                getSharedPreferences(
                    "vie_espoir_donnees",
                    MODE_PRIVATE
                )

            produits.clear()
            commandes.clear()

            val produitsTexte =
                preferences.getString(
                    "produits",
                    null
                )

            if (produitsTexte != null) {

                val tableau =
                    JSONArray(produitsTexte)

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(i)

                    val photos =
                        ArrayList<String>()

                    val tableauPhotos =
                        objet.optJSONArray("photos")

                    if (tableauPhotos != null) {

                        for (
                            j in 0 until tableauPhotos.length()
                        ) {

                            photos.add(
                                tableauPhotos.getString(j)
                            )
                        }
                    }

                    produits.add(
                        Produit(
                            objet.optString("nom"),
                            objet.optString("prix"),
                            objet.optString("description"),
                            objet.optString("stock"),
                            photos
                        )
                    )
                }
            }

            val commandesTexte =
                preferences.getString(
                    "commandes",
                    null
                )

            if (commandesTexte != null) {

                val tableau =
                    JSONArray(commandesTexte)

                for (
                    i in 0 until tableau.length()
                ) {

                    val objet =
                        tableau.getJSONObject(i)

                    commandes.add(
                        Commande(
                            objet.optString("produit"),
                            objet.optString("nomClient"),
                            objet.optString("telephone"),
                            objet.optString("quantite"),
                            objet.optString("adresse"),
                            objet.optString("paiement")
                        )
                    )
                }
            }

            val abonnementTexte =
                preferences.getString(
                    "abonnement",
                    null
                )

            if (abonnementTexte != null) {

                val objet =
                    JSONObject(abonnementTexte)

                abonnement =
                    Abonnement(
                        objet.optString("pack"),
                        objet.optString("prix"),
                        objet.optString("duree"),
                        objet.optBoolean("actif")
                    )
            }

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // PARTAGER
    // =========================================================

    private fun partagerProduit(
        produit: Produit
    ) {

        val message =
            "🦁 VIE ESPOIR MARKETING\n\n" +
            "📦 Produit : ${produit.nom}\n" +
            "💰 Prix : ${produit.prix} FCFA\n\n" +
            produit.description

        val intent =
            Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"

        intent.putExtra(
            Intent.EXTRA_TEXT,
            message
        )

        startActivity(
            Intent.createChooser(
                intent,
                "Partager le produit avec"
            )
        )
    }

    // =========================================================
    // AIDE
    // =========================================================

    private fun afficherAide() {

        AlertDialog.Builder(this)
            .setTitle("🆘 BESOIN D'AIDE ?")
            .setMessage(
                "Bienvenue dans Vie Espoir Marketing.\n\n" +
                "Pour vendre vos produits, choisissez " +
                "un abonnement vendeur puis utilisez " +
                "votre espace vendeur."
            )
            .setPositiveButton(
                "FERMER",
                null
            )
            .show()
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private fun ouvrirEspace(
        nom: String
    ) {

        when (nom) {

            "GESTION DES PRODUITS" ->
                afficherGestionProduits()

            "VENTES" ->
                afficherVentes()

            "GESTION DU STOCK" ->
                afficherStock()

            "CLIENTS" ->
                afficherClients()

            "MOYENS DE PAIEMENT" ->
                afficherPaiements()

            "LIVRAISON" ->
                afficherLivraisons()

            "COMMISSIONS" ->
                afficherCommissions()

            else ->
                afficherAccueil()
        }
    }

    // =========================================================
    // BOUTONS
    // =========================================================

    private fun ajouterBouton(
        layout: LinearLayout,
        texte: String,
        action: () -> Unit
    ) {

        val bouton = Button(this)

        bouton.text = texte
        bouton.textSize = 16f

        bouton.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        bouton.setOnClickListener {
            action()
        }

        layout.addView(bouton)
    }

    // =========================================================
    // PERMISSION CAMERA
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
            CODE_PERMISSION_CAMERA
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
}