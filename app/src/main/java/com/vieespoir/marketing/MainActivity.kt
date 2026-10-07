package com.vieespoir.marketing

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private val clients = ArrayList<String>()
    private val produits = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Produits de départ
        if (produits.isEmpty()) {
            produits.add("Super7")
            produits.add("M7")
            produits.add("Women 7")
            produits.add("Timoc")
            produits.add("Café Royal")
        }

        afficherAccueil()
    }

    private fun creerBouton(texte: String): Button {
        return Button(this).apply {
            text = texte
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(30, 120, 60))
            setPadding(20, 15, 20, 15)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 10, 10, 10)
            }
        }
    }

    private fun creerTitre(texte: String): TextView {
        return TextView(this).apply {
            text = texte
            textSize = 24f
            setTextColor(Color.rgb(20, 100, 50))
            gravity = Gravity.CENTER
            setPadding(10, 25, 10, 25)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun afficherAccueil() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        layout.addView(creerTitre("VIE ESPOIR MARKETING"))

        val bienvenue = TextView(this).apply {
            text = "Bienvenue dans votre application"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(10, 10, 10, 30)
        }

        layout.addView(bienvenue)

        layout.addView(
            creerBouton("📦 Produits").apply {
                setOnClickListener {
                    afficherProduits()
                }
            }
        )

        layout.addView(
            creerBouton("👥 Clients").apply {
                setOnClickListener {
                    afficherClients()
                }
            }
        )

        layout.addView(
            creerBouton("➕ Ajouter un client").apply {
                setOnClickListener {
                    ajouterClient()
                }
            }
        )

        setContentView(layout)
    }

    private fun afficherProduits() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        layout.addView(creerTitre("LISTE DES PRODUITS"))

        if (produits.isEmpty()) {

            layout.addView(
                TextView(this).apply {
                    text = "Aucun produit disponible"
                    textSize = 18f
                    setPadding(10, 20, 10, 20)
                }
            )

        } else {

            for ((index, produit) in produits.withIndex()) {

                layout.addView(
                    TextView(this).apply {
                        text = "${index + 1}. $produit"
                        textSize = 18f
                        setPadding(10, 10, 10, 10)
                    }
                )
            }
        }

        layout.addView(
            creerBouton("➕ Ajouter un produit").apply {
                setOnClickListener {
                    ajouterProduit()
                }
            }
        )

        layout.addView(
            creerBouton("Retour").apply {
                setOnClickListener {
                    afficherAccueil()
                }
            }
        )

        setContentView(layout)
    }

    private fun ajouterProduit() {

        val champ = EditText(this)
        champ.hint = "Nom du produit"

        AlertDialog.Builder(this)
            .setTitle("Ajouter un produit")
            .setView(champ)
            .setPositiveButton("Ajouter") { _, _ ->

                val nom = champ.text.toString().trim()

                if (nom.isNotEmpty()) {
                    produits.add(nom)
                    afficherProduits()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun afficherClients() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        layout.addView(creerTitre("LISTE DES CLIENTS"))

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
            creerBouton("Retour").apply {
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

    override fun onBackPressed() {
        afficherAccueil()
    }
}