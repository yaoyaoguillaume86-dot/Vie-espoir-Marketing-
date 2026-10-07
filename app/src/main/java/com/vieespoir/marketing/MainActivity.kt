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

class MainActivity : Activity() {

    private val produits = ArrayList<Produit>()
    private val commandes = ArrayList<Commande>()

    private val photosSelectionnees = ArrayList<String>()

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
            afficherCommandes()
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
        ajouter.textSize = 16f

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

    // =========================================================
    // LISTE PRODUITS
    // =========================================================

    private fun afficherListeProduits(layout: LinearLayout) {

        if (produits.isEmpty()) {

            val vide = TextView(this)
            vide.text = "Aucun produit ajouté pour le moment."
            vide.textSize = 17f
            vide.setTextColor(Color.DKGRAY)
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

                try {
                    photo.setImageURI(
                        Uri.parse(produit.photos[0])
                    )
                } catch (_: Exception) {
                }

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
        prix.hint = "Prix du produit en FCFA"
        prix.inputType = 2

        val description = EditText(this)
        description.hint =
            "Description du produit"
        description.minLines = 4
        description.gravity = Gravity.TOP

        val stock = EditText(this)
        stock.hint = "Quantité en stock"
        stock.inputType = 2

        val photosTexte = TextView(this)
        photosTexte.text =
            "📷 0 photo sélectionnée"
        photosTexte.textSize = 16f
        photosTexte.setPadding(0, 15, 0, 15)

        val camera = Button(this)
        camera.text = "📷 PRENDRE UNE PHOTO"

        camera.setOnClickListener {

            if (
                checkSelfPermission(
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CODE_PERMISSION_CAMERA
                )

            } else {

                ouvrirCamera()
            }
        }

        val galerie = Button(this)
        galerie.text =
            "🖼️ AJOUTER PLUSIEURS PHOTOS"

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
                        "Veuillez entrer le nom du produit",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val produit = Produit(
                    nomProduit,
                    prixProduit,
                    descriptionProduit,
                    stockProduit,
                    ArrayList(photosSelectionnees)
                )

                produits.add(produit)

                sauvegarderDonnees()

                Toast.makeText(
                    this,
                    "Produit enregistré définitivement",
                    Toast.LENGTH_SHORT
                ).show()

                dialogue.dismiss()

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
    // CAMÉRA
    // =========================================================

    private fun ouvrirCamera() {

        try {

            val intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(
                intent,
                CODE_CAMERA
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Impossible d'ouvrir la caméra",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================================================
    // RÉSULTATS PHOTO
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

                for (i in 0 until clipData.itemCount) {

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

                    photosSelectionnees.add(
                        chemin
                    )

                    Toast.makeText(
                        this,
                        "Photo ajoutée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // =========================================================
    // COPIER PHOTO GALERIE
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
                    "photo_${System.currentTimeMillis()}_${photosSelectionnees.size}.jpg"
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

        } catch (e: Exception) {

            null
        }
    }

    // =========================================================
    // SAUVEGARDER PHOTO CAMÉRA
    // =========================================================

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

        } catch (e: Exception) {

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

        layout.setPadding(
            25,
            30,
            25,
            30
        )

        layout.setBackgroundColor(
            Color.WHITE
        )

        val titre = TextView(this)

        titre.text = produit.nom
        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        if (produit.photos.isNotEmpty()) {

            for (chemin in produit.photos) {

                val image =
                    ImageView(this)

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

        } else {

            val aucunePhoto =
                TextView(this)

            aucunePhoto.text =
                "📷 Aucune photo"

            aucunePhoto.textSize = 17f
            aucunePhoto.gravity = Gravity.CENTER

            layout.addView(aucunePhoto)
        }

        val prix = TextView(this)

        prix.text =
            "💰 ${produit.prix} FCFA"

        prix.textSize = 20f
        prix.gravity = Gravity.CENTER

        layout.addView(prix)

        val stock = TextView(this)

        stock.text =
            "📦 Stock disponible : ${produit.stock}"

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

        description.setPadding(
            0,
            20,
            0,
            20
        )

        layout.addView(description)

        val commander = Button(this)

        commander.text =
            "🛒 COMMANDER"

        commander.setOnClickListener {
            afficherCommande(produit)
        }

        layout.addView(commander)

        val retour = Button(this)

        retour.text =
            "⬅ RETOUR AUX PRODUITS"

        retour.setOnClickListener {
            afficherProduits()
        }

        layout.addView(retour)

        val accueil = Button(this)

        accueil.text =
            "🏠 RETOUR À L'ACCUEIL"

        accueil.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(accueil)

        setContentView(layout)
    }

    // =========================================================
    // COMMANDER
    // =========================================================

    private fun afficherCommande(
        produit: Produit
    ) {

        val zone =
            LinearLayout(this)

        zone.orientation =
            LinearLayout.VERTICAL

        zone.setPadding(
            30,
            10,
            30,
            10
        )

        if (produit.photos.isNotEmpty()) {

            val image =
                ImageView(this)

            image.setImageURI(
                Uri.parse(produit.photos[0])
            )

            image.adjustViewBounds = true

            image.scaleType =
                ImageView.ScaleType.CENTER_INSIDE

            image.layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )

            zone.addView(image)
        }

        val produitNom =
            TextView(this)

        produitNom.text =
            "📦 Produit : ${produit.nom}"

        produitNom.textSize = 19f
        produitNom.setTextColor(Color.BLACK)

        zone.addView(produitNom)

        val nom = EditText(this)
        nom.hint =
            "Nom et prénom du client"

        val telephone = EditText(this)
        telephone.hint =
            "Numéro de téléphone"
        telephone.inputType = 3

        val quantite = EditText(this)
        quantite.hint = "Quantité"
        quantite.inputType = 2

        val adresse = EditText(this)
        adresse.hint =
            "Lieu / adresse de livraison"

        val paiement =
            Spinner(this)

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
                        "Veuillez remplir toutes les informations",
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

                Toast.makeText(
                    this,
                    "Commande enregistrée avec succès",
                    Toast.LENGTH_LONG
                ).show()

                dialogue.dismiss()

                afficherAccueil()
            }
        }

        dialogue.show()
    }

    // =========================================================
    // COMMANDES
    // =========================================================

    private fun afficherCommandes() {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            25,
            30,
            25,
            30
        )

        layout.setBackgroundColor(
            Color.WHITE
        )

        val titre =
            TextView(this)

        titre.text =
            "📋 MES COMMANDES"

        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        if (commandes.isEmpty()) {

            val vide =
                TextView(this)

            vide.text =
                "Aucune commande reçue pour le moment."

            vide.textSize = 18f
            vide.gravity = Gravity.CENTER

            layout.addView(vide)

        } else {

            for (commande in commandes) {

                val bloc =
                    LinearLayout(this)

                bloc.orientation =
                    LinearLayout.VERTICAL

                bloc.setPadding(
                    0,
                    20,
                    0,
                    20
                )

                val produit =
                    TextView(this)

                produit.text =
                    "📦 PRODUIT : ${commande.produitNom}"

                produit.textSize = 21f

                bloc.addView(produit)

                val client =
                    TextView(this)

                client.text =
                    "👤 Client : ${commande.nomClient}"

                client.textSize = 17f

                bloc.addView(client)

                val telephone =
                    TextView(this)

                telephone.text =
                    "📞 Téléphone : ${commande.telephone}"

                telephone.textSize = 17f

                bloc.addView(telephone)

                val quantite =
                    TextView(this)

                quantite.text =
                    "🔢 Quantité : ${commande.quantite}"

                quantite.textSize = 17f

                bloc.addView(quantite)

                val adresse =
                    TextView(this)

                adresse.text =
                    "📍 Livraison : ${commande.adresse}"

                adresse.textSize = 17f

                bloc.addView(adresse)

                val paiement =
                    TextView(this)

                paiement.text =
                    "💳 Paiement : ${commande.paiement}"

                paiement.textSize = 17f

                bloc.addView(paiement)

                layout.addView(bloc)
            }
        }

        val retour =
            Button(this)

        retour.text =
            "🏠 RETOUR À L'ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // SAUVEGARDE PERMANENTE
    // =========================================================

    private fun sauvegarderDonnees() {

        try {

            val preferences =
                getSharedPreferences(
                    "vie_espoir_donnees",
                    MODE_PRIVATE
                )

            val produitsJson =
                JSONArray()

            for (produit in produits) {

                val objet =
                    JSONObject()

                objet.put(
                    "nom",
                    produit.nom
                )

                objet.put(
                    "prix",
                    produit.prix
                )

                objet.put(
                    "description",
                    produit.description
                )

                objet.put(
                    "stock",
                    produit.stock
                )

                val photos =
                    JSONArray()

                for (photo in produit.photos) {
                    photos.put(photo)
                }

                objet.put(
                    "photos",
                    photos
                )

                produitsJson.put(objet)
            }

            val commandesJson =
                JSONArray()

            for (commande in commandes) {

                val objet =
                    JSONObject()

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

            preferences.edit()
                .putString(
                    "produits",
                    produitsJson.toString()
                )
                .putString(
                    "commandes",
                    commandesJson.toString()
                )
                .apply()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Erreur lors de la sauvegarde",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // CHARGEMENT PERMANENT
    // =========================================================

    private fun chargerDonnees() {

        try {

            val preferences =
                getSharedPreferences(
                    "vie_espoir_donnees",
                    MODE_PRIVATE
                )

            val produitsTexte =
                preferences.getString(
                    "produits",
                    null
                )

            if (produitsTexte != null) {

                val tableau =
                    JSONArray(produitsTexte)

                for (i in 0 until tableau.length()) {

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

                for (i in 0 until tableau.length()) {

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

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Impossible de charger les données",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================================================
    // PARTAGER
    // =========================================================

    private fun partagerProduit(
        produit: Produit
    ) {

        val message =
            "📦 VIE ESPOIR MARKETING\n\n" +
            "Produit : ${produit.nom}\n" +
            "Prix : ${produit.prix} FCFA\n\n" +
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
                    "L'autorisation caméra est nécessaire.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // =========================================================
    // AIDE
    // =========================================================

    private fun afficherAide() {

        AlertDialog.Builder(this)
            .setTitle(
                "🆘 BESOIN D'AIDE ?"
            )
            .setMessage(
                "Vous ne comprenez pas comment utiliser " +
                "l'application ou commander un produit ?"
            )
            .setPositiveButton(
                "FERMER",
                null
            )
            .show()
    }

    // =========================================================
    // AUTRES ESPACES
    // =========================================================

    private fun ouvrirEspace(
        nom: String
    ) {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            30,
            40,
            30,
            30
        )

        layout.gravity =
            Gravity.CENTER_HORIZONTAL

        val titre =
            TextView(this)

        titre.text = nom
        titre.textSize = 26f
        titre.setTextColor(Color.BLACK)
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val message =
            TextView(this)

        message.text =
            "Bienvenue dans l'espace $nom"

        message.textSize = 18f
        message.gravity = Gravity.CENTER
        message.setPadding(
            0,
            30,
            0,
            30
        )

        layout.addView(message)

        val retour =
            Button(this)

        retour.text =
            "🏠 RETOUR À L'ACCUEIL"

        retour.setOnClickListener {
            afficherAccueil()
        }

        layout.addView(retour)

        setContentView(layout)
    }

    // =========================================================
    // BOUTONS
    // =========================================================

    private fun ajouterBouton(
        layout: LinearLayout,
        texte: String,
        action: () -> Unit
    ) {

        val bouton =
            Button(this)

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
}