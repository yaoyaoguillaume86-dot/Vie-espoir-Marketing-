package com.vieespoir.marketing

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.io.Serializable

class MainActivity : Activity() {

    private val produits = ArrayList<Produit>()
    private var imageProduit: Bitmap? = null

    data class Produit(
        val nom: String,
        val prix: String,
        val image: Bitmap? = null
    ) : Serializable

    companion object {
        const val REQUEST_CAMERA = 100
        const val REQUEST_GALLERY = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Produits de départ
        if (produits.isEmpty()) {
            produits.add(Produit("Super7", "20000"))
            produits.add(Produit("M7", "15000"))
            produits.add(Produit("Women 7", "15000"))
            produits.add(Produit("Timoc", "10000"))
            produits.add(Produit("Café Royal", "5000"))
        }

        afficherAccueil()
    }

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "VIE ESPOIR MARKETING"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER
        layout.addView(titre)

        val btnProduits = Button(this)
        btnProduits.text = "📦 Produits"
        layout.addView(btnProduits)

        val btnAjouter = Button(this)
        btnAjouter.text = "➕ Ajouter un produit"
        layout.addView(btnAjouter)

        btnProduits.setOnClickListener {
            afficherProduits()
        }

        btnAjouter.setOnClickListener {
            afficherAjoutProduit()
        }

        setContentView(layout)
    }

    private fun afficherAjoutProduit() {

        imageProduit = null

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(25, 25, 25, 25)

        val titre = TextView(this)
        titre.text = "Ajouter un produit"
        titre.textSize = 24f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        val nom = EditText(this)
        nom.hint = "Nom du produit"
        layout.addView(nom)

        val prix = EditText(this)
        prix.hint = "Prix"
        prix.inputType = 2
        layout.addView(prix)

        val image = ImageView(this)
        image.setImageResource(android.R.drawable.ic_menu_camera)

        val imageParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )

        imageParams.setMargins(0, 20, 0, 20)
        layout.addView(image, imageParams)

        // Bouton caméra
        val btnCamera = Button(this)
        btnCamera.text = "📷 Prendre une photo"
        layout.addView(btnCamera)

        // Bouton galerie
        val btnGalerie = Button(this)
        btnGalerie.text = "🖼️ Choisir dans la galerie"
        layout.addView(btnGalerie)

        // Enregistrer
        val btnEnregistrer = Button(this)
        btnEnregistrer.text = "✅ Enregistrer le produit"
        layout.addView(btnEnregistrer)

        btnCamera.setOnClickListener {
            ouvrirCamera()
        }

        btnGalerie.setOnClickListener {
            ouvrirGalerie()
        }

        btnEnregistrer.setOnClickListener {

            if (nom.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    "Entrez le nom du produit",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (prix.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    "Entrez le prix",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            produits.add(
                Produit(
                    nom.text.toString(),
                    prix.text.toString(),
                    imageProduit
                )
            )

            Toast.makeText(
                this,
                "Produit enregistré avec succès",
                Toast.LENGTH_SHORT
            ).show()

            afficherProduits()
        }

        setContentView(layout)
    }

    private fun ouvrirCamera() {

        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Impossible d'ouvrir la caméra",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun ouvrirGalerie() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(intent, REQUEST_GALLERY)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {

            REQUEST_CAMERA -> {

                val bitmap =
                    data?.extras?.get("data") as? Bitmap

                if (bitmap != null) {
                    imageProduit = bitmap

                    Toast.makeText(
                        this,
                        "Photo prise avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            REQUEST_GALLERY -> {

                val uri: Uri? = data?.data

                if (uri != null) {

                    try {

                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            uri
                        )

                        imageProduit = bitmap

                        Toast.makeText(
                            this,
                            "Photo sélectionnée",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Erreur lors du chargement de la photo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        val titre = TextView(this)
        titre.text = "NOS PRODUITS"
        titre.textSize = 25f
        titre.gravity = Gravity.CENTER

        layout.addView(titre)

        for (produit in produits) {

            val bloc = LinearLayout(this)
            bloc.orientation = LinearLayout.VERTICAL
            bloc.setPadding(10, 20, 10, 20)

            val image = ImageView(this)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                350
            )

            if (produit.image != null) {
                image.setImageBitmap(produit.image)
            } else {
                image.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }

            bloc.addView(image, params)

            val nom = TextView(this)
            nom.text = produit.nom
            nom.textSize = 21f
            nom.gravity = Gravity.CENTER

            bloc.addView(nom)

            val prix = TextView(this)
            prix.text = produit.prix + " FCFA"
            prix.textSize = 18f
            prix.gravity = Gravity.CENTER

            bloc.addView(prix)

            layout.addView(bloc)
        }

        val btnRetour = Button(this)
        btnRetour.text = "⬅️ Retour"
        layout.addView(btnRetour)

        btnRetour.setOnClickListener {
            afficherAccueil()
        }

        setContentView(layout)
    }
}