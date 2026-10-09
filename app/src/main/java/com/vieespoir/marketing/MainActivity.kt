package com.vieespoir.marketing

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
// ============================================================
// ÉCRAN DE BIENVENUE
// ============================================================

class SplashActivity : Activity() {

override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  

    window.statusBarColor = Color.WHITE  
    window.navigationBarColor = Color.WHITE  

    val layout = LinearLayout(this).apply {  
        orientation = LinearLayout.VERTICAL  
        gravity = Gravity.CENTER  
        setBackgroundColor(Color.WHITE)  
        setPadding(30, 30, 30, 30)  
    }  

    val bienvenue = TextView(this).apply {  
        text = "BIENVENUE\nVIE ESPOIR MARKETING"  
        textSize = 22f  
        setTextColor(Color.rgb(20, 100, 50))  
        gravity = Gravity.CENTER  
        typeface = Typeface.DEFAULT_BOLD  
    }  

    layout.addView(bienvenue)  

    super.setContentView(layout)  

    Handler(Looper.getMainLooper()).postDelayed({  
        try {  
            startActivity(  
                Intent(  
                    this@SplashActivity,  
                    MainActivity::class.java  
                )  
            )  
            finish()  
        } catch (_: Exception) {  
        }  
    }, 10000)  
}

}

// ============================================================
// APPLICATION PRINCIPALE
// ============================================================

class MainActivity : Activity() {
private val db = FirebaseFirestore.getInstance()
// ========================================================  
// MODÈLES  
// ========================================================  

data class Produit(  
    val id: String = System.currentTimeMillis().toString(),  
    var nom: String,  
    var prix: String,  
    var categorie: String,  
    var stock: Int = 0,  
    var description: String = "",  
    var images: MutableList<String> = mutableListOf(),  
    var vendeur: String = "",  
    var entreprise: String = ""  
)  

data class PanierItem(  
    val produitId: String,  
    val nom: String,  
    val prix: Int,  
    var quantite: Int,  
    val images: MutableList<String> = mutableListOf()  
)  

data class Commande(  
    val id: String,  
    val client: String,  
    val telephone: String,  
    val produit: String,  
    val montant: String,  
    val paiement: String,  
    var statut: String = "Nouvelle"  
)  

data class Compte(  
    val id: String,  
    var nom: String,  
    var entreprise: String,  
    var email: String,  
    var telephone: String,  
    var adresse: String,  
    var motDePasse: String,  
    var type: String,  
    var dateCreation: Long,  
    var valide: Boolean = false  
)  

// ========================================================  
// DONNÉES  
// ========================================================  

private val produits = ArrayList<Produit>()  
private val panier = ArrayList<PanierItem>()  
private val commandes = ArrayList<Commande>()  
private val comptes = ArrayList<Compte>()  
private val evenementsAdmin = ArrayList<String>()  

private var compteConnecteId = ""  
private var imageProduit = ""  
private val imagesProduit = ArrayList<String>()  

private lateinit var zoneProduits: LinearLayout  
private lateinit var galeriePhotos: LinearLayout  

// ========================================================  
// HISTORIQUE DES ÉCRANS  
// ========================================================  

/*  
 * IMPORTANT :  
 * Chaque fois qu'un nouvel écran est affiché avec  
 * setContentView(), il est ajouté à l'historique.  
 *  
 * Ainsi le bouton RETOUR du téléphone permet de revenir  
 * progressivement aux écrans précédents.  
 */  

private val historiqueEcrans = ArrayList<View>()  

private var restaurationEcran = false  

override fun setContentView(view: View) {  

    if (!restaurationEcran) {  
        historiqueEcrans.add(view)  
    }  

    super.setContentView(view)  
}  

private fun afficherEcranPrecedent(): Boolean {  

    if (historiqueEcrans.size <= 1) {  
        return false  
    }  

    historiqueEcrans.removeAt(  
        historiqueEcrans.lastIndex  
    )  

    val precedent =  
        historiqueEcrans.lastOrNull()  
            ?: return false  

    restaurationEcran = true  

    try {  
        super.setContentView(precedent)  
    } finally {  
        restaurationEcran = false  
    }  

    return true  
}  

private fun viderHistoriqueEtAfficher(view: View) {  

    historiqueEcrans.clear()  

    historiqueEcrans.add(view)  

    restaurationEcran = true  

    try {  
        super.setContentView(view)  
    } finally {  
        restaurationEcran = false  
    }  
}  

// ========================================================  
// STOCKAGE  
// ========================================================  

private val PREFS = "vie_espoir_data"  

private val PRODUCTS_KEY = "products"  
private val CART_KEY = "cart"  
private val ORDERS_KEY = "orders"  
private val ACCOUNTS_KEY = "accounts"  
private val EVENTS_KEY = "events"  

private val CONNECTED_ACCOUNT = "connected_account"  

private val CAMERA_REQUEST = 100  
private val MULTI_GALLERY_REQUEST = 102  

// ========================================================  
// ADMINISTRATEUR  
// ========================================================  

private val ADMIN_EMAIL = "admin@vieespoir.com"  
private val ADMIN_PASSWORD = "VieEspoirAdmin2026"  

private var adminConnecte = false  

// ========================================================  
// CONTACT / PAIEMENT  
// ========================================================  

private val ORANGE_WAVE = "0710405688"  
private val MTN = "0546420566"  

// ========================================================  
// COULEURS  
// ========================================================  

private val VERT = Color.rgb(20, 100, 50)  
private val VERT_FONCE = Color.rgb(8, 65, 32)  
private val OR = Color.rgb(218, 165, 32)  
private val BLANC = Color.WHITE  
private val FOND = Color.rgb(247, 249, 246)  
private val GRIS = Color.rgb(235, 237, 235)  
private val TEXTE = Color.rgb(35, 35, 35)  

private val prefs by lazy {  
    getSharedPreferences(PREFS, MODE_PRIVATE)  
}  

// ========================================================  
// DÉMARRAGE  
// ========================================================  

override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  

    window.statusBarColor = VERT_FONCE  
    window.navigationBarColor = Color.WHITE  

    chargerDonnees()  

    compteConnecteId =  
        prefs.getString(  
            CONNECTED_ACCOUNT,  
            ""  
        ) ?: ""  

    afficherAccueil()
chargerProduitsFirebase()
}

// ========================================================  
// INTERFACE DE BASE  
// ========================================================  

private fun layoutBase(): LinearLayout {  
    return LinearLayout(this).apply {  
        orientation = LinearLayout.VERTICAL  
        setPadding(12, 8, 12, 25)  
        setBackgroundColor(FOND)  
    }  
}  

private fun scroll(  
    contenu: LinearLayout  
): ScrollView {  
    return ScrollView(this).apply {  
        isFillViewport = true  
        addView(contenu)  
    }  
}  

private fun titre(  
    texte: String  
): TextView {  
    return TextView(this).apply {  
        text = texte  
        textSize = 23f  
        gravity = Gravity.CENTER  
        setTextColor(VERT)  
        typeface = Typeface.DEFAULT_BOLD  
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
        setPadding(8, 7, 8, 7)  
    }  
}  

private fun bouton(  
    texte: String,  
    action: () -> Unit  
): Button {  

    return Button(this).apply {  

        text = texte  
        textSize = 15f  
        setTextColor(TEXTE)  
        setBackgroundColor(GRIS)  
        typeface = Typeface.DEFAULT_BOLD  

        setOnClickListener {  
            action()  
        }  

        layoutParams =  
            LinearLayout.LayoutParams(  
                ViewGroup.LayoutParams.MATCH_PARENT,  
                ViewGroup.LayoutParams.WRAP_CONTENT  
            ).apply {  
                setMargins(4, 5, 4, 5)  
            }  
    }  
}  

private fun boutonVert(  
    texte: String,  
    action: () -> Unit  
): Button {  

    return Button(this).apply {  

        text = texte  
        textSize = 15f  
        setTextColor(Color.WHITE)  
        setBackgroundColor(VERT)  
        typeface = Typeface.DEFAULT_BOLD  

        setOnClickListener {  
            action()  
        }  

        layoutParams =  
            LinearLayout.LayoutParams(  
                ViewGroup.LayoutParams.MATCH_PARENT,  
                ViewGroup.LayoutParams.WRAP_CONTENT  
            ).apply {  
                setMargins(4, 5, 4, 5)  
            }  
    }  
}  

private fun toast(  
    message: String  
) {  
    Toast.makeText(  
        this,  
        message,  
        Toast.LENGTH_SHORT  
    ).show()  
}  

// ========================================================  
// ACCUEIL  
// ========================================================  

private fun afficherAccueil() {  

    val root = layoutBase()  

    val header =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.HORIZONTAL  

            gravity =  
                Gravity.CENTER_VERTICAL  

            setPadding(  
                8,  
                10,  
                8,  
                10  
            )  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  

    /*  
     * PAS DE FICHIER LOGO.  
     *  
     * Le lion est simplement un caractère texte.  
     * Aucun drawable n'est nécessaire.  
     */  

    val logo =  
        TextView(this).apply {  

            text = "🦁"  
            textSize = 42f  
            gravity = Gravity.CENTER  
        }  

    val nom =  
        TextView(this).apply {  

            text =  
                "VIE ESPOIR\nMARKETING"  

            textSize = 20f  

            setTextColor(  
                VERT  
            )  

            typeface =  
                Typeface.DEFAULT_BOLD  

            gravity =  
                Gravity.CENTER_VERTICAL  

            layoutParams =  
                LinearLayout.LayoutParams(  
                    0,  
                    ViewGroup.LayoutParams.WRAP_CONTENT,  
                    1f  
                )  
        }  

    val menu =  
        Button(this).apply {  

            text = "☰"  
            textSize = 25f  

            setTextColor(  
                VERT  
            )  

            setBackgroundColor(  
                GRIS  
            )  

            setOnClickListener {  
                afficherMenu()  
            }  
        }  

    header.addView(logo)  
    header.addView(nom)  
    header.addView(menu)  

    root.addView(header)  

    root.addView(  
        texte(  
            "Ensemble pour un meilleur avenir",  
            14f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            typeface =  
                Typeface.create(  
                    Typeface.DEFAULT,  
                    Typeface.ITALIC  
                )  
        }  
    )  

    if (compteConnecteId.isNotEmpty()) {  

        val compte =  
            compteActuel()  

        if (compte != null) {  

            root.addView(  
                texte(  
                    "👋 Bienvenue ${compte.nom}",  
                    17f,  
                    VERT  
                ).apply {  

                    gravity =  
                        Gravity.CENTER  

                    typeface =  
                        Typeface.DEFAULT_BOLD  
                }  
            )  
        }  
    }  

    val recherche =  
        EditText(this).apply {  

            hint =  
                "🔎 Rechercher un produit"  

            textSize = 16f  

            setSingleLine(true)  

            setPadding(  
                16,  
                12,  
                16,  
                12  
            )  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  

    root.addView(recherche)  

    root.addView(  
        boutonVert(  
            "🔎 Rechercher"  
        ) {  

            afficherProduits(  
                recherche.text  
                    .toString()  
                    .trim()  
            )  
        }  
    )  

    root.addView(  
        titre(  
            "🛍️ Produits disponibles"  
        )  
    )  

    zoneProduits =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.VERTICAL  
        }  

    root.addView(  
        zoneProduits  
    )  

    root.addView(  
        bouton(  
            "🛒 Mon panier (${panier.size})"  
        ) {  

            afficherPanier()  
        }  
    )  

    root.addView(  
        texte(  
            "VIE ESPOIR MARKETING\nProduits • Commandes • Vente • Entreprises",  
            13f,  
            Color.GRAY  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            setPadding(  
                8,  
                25,  
                8,  
                20  
            )  
        }  
    )  

    viderHistoriqueEtAfficher(  
        scroll(root)  
    )  

    afficherProduitsAccueil()  
}  

// ========================================================  
// PRODUITS ACCUEIL  
//// ========================================================
// CHARGER LES PRODUITS DEPUIS FIREBASE
// ========================================================

private fun chargerProduitsFirebase() {
    db.collection("produits")
        .get()
        .addOnSuccessListener { result ->

            produits.clear()

            for (document in result.documents) {
                val nom = document.getString("nom") ?: continue
                val prixValeur = document.get("prix")

                val prix = when (prixValeur) {
                    is Number -> prixValeur.toInt().toString()
                    is String -> prixValeur
                    else -> "0"
                }

                val categorie =
                    document.getString("categorie") ?: "Autres"

                val stock =
                    (document.getLong("stock") ?: 0L).toInt()

                val description =
                    document.getString("description") ?: ""

                produits.add(
                    Produit(
                        id = document.id,
                        nom = nom,
                        prix = prix,
                        categorie = categorie,
                        stock = stock,
                        description = description,
                        images = mutableListOf(),
                        vendeur = document.getString("vendeur") ?: "",
                        entreprise = document.getString("entreprise") ?: ""
                    )
                )
            }

            sauvegarderProduits()
            afficherProduitsAccueil()

            toast("Produits chargés depuis Firebase")
        }
        .addOnFailureListener {
            toast("Impossible de charger les produits Firebase")
            afficherProduitsAccueil()
        }
}

private fun afficherProduitsAccueil() {  

    if (!::zoneProduits.isInitialized) {  
        return  
    }  

    zoneProduits.removeAllViews()  

    if (produits.isEmpty()) {  

        zoneProduits.addView(  
            texte(  
                "Aucun produit disponible pour le moment.",  
                17f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  

                setPadding(  
                    10,  
                    30,  
                    10,  
                    30  
                )  
            }  
        )  

        return  
    }  

    for (produit in produits) {  

        ajouterCarteProduit(  
            zoneProduits,  
            produit  
        )  
    }  
}  

// ========================================================  
// CARTE PRODUIT  
// ========================================================  

private fun ajouterCarteProduit(  
    parent: LinearLayout,  
    produit: Produit  
) {  

    val carte =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.VERTICAL  

            setPadding(  
                12,  
                12,  
                12,  
                12  
            )  

            setBackgroundColor(  
                Color.WHITE  
            )  

            layoutParams =  
                LinearLayout.LayoutParams(  
                    ViewGroup.LayoutParams.MATCH_PARENT,  
                    ViewGroup.LayoutParams.WRAP_CONTENT  
                ).apply {  

                    setMargins(  
                        3,  
                        8,  
                        3,  
                        8  
                    )  
                }  
        }  

    // ====================================================  
    // IMAGE PRINCIPALE  
    // ====================================================  

    val imagePrincipale =  
        if (produit.images.isNotEmpty()) {  
            produit.images[0]  
        } else {  
            ""  
        }  

    if (imagePrincipale.isNotEmpty()) {  

        try {  

            val image =  
                ImageView(this).apply {  

                    layoutParams =  
                        LinearLayout.LayoutParams(  
                            ViewGroup.LayoutParams.MATCH_PARENT,  
                            250  
                        )  

                    scaleType =  
                        ImageView.ScaleType.CENTER_CROP  

                    setImageURI(  
                        Uri.parse(  
                            imagePrincipale  
                        )  
                    )  
                }  

            carte.addView(image)  

        } catch (_: Exception) {  
        }  
    }  

    // ====================================================  
    // MINIATURES  
    // ====================================================  

    if (produit.images.size > 1) {  

        val miniatures =  
            LinearLayout(this).apply {  

                orientation =  
                    LinearLayout.HORIZONTAL  

                gravity =  
                    Gravity.CENTER  

                setPadding(  
                    0,  
                    5,  
                    0,  
                    5  
                )  
            }  

        for (chemin in produit.images) {  

            try {  

                val miniature =  
                    ImageView(this).apply {  

                        layoutParams =  
                            LinearLayout.LayoutParams(  
                                65,  
                                65  
                            ).apply {  

                                setMargins(  
                                    3,  
                                    3,  
                                    3,  
                                    3  
                                )  
                            }  

                        scaleType =  
                            ImageView.ScaleType.CENTER_CROP  

                        setImageURI(  
                            Uri.parse(  
                                chemin  
                            )  
                        )  
                    }  

                miniatures.addView(  
                    miniature  
                )  

            } catch (_: Exception) {  
            }  
        }  

        carte.addView(  
            miniatures  
        )  
    }  

    carte.addView(  
        texte(  
            produit.nom,  
            21f,  
            VERT  
        ).apply {  

            typeface =  
                Typeface.DEFAULT_BOLD  
        }  
    )  

    val prix =  
        produit.prix  
            .replace(" ", "")  
            .toIntOrNull()  
            ?: 0  

    carte.addView(  
        texte(  
            "${formaterNombre(prix)} FCFA",  
            19f,  
            VERT  
        ).apply {  

            typeface =  
                Typeface.DEFAULT_BOLD  
        }  
    )  

    if (produit.entreprise.isNotEmpty()) {  

        carte.addView(  
            texte(  
                "🏢 ${produit.entreprise}",  
                14f,  
                OR  
            )  
        )  
    }  

    carte.addView(  
        texte(  
            "🏷️ ${produit.categorie}",  
            14f,  
            Color.GRAY  
        )  
    )  

    carte.addView(  
        texte(  
            if (produit.stock > 0) {  
                "📦 Stock : ${produit.stock}"  
            } else {  
                "⚠️ Produit indisponible"  
            },  
            14f,  
            if (produit.stock > 0) {  
                VERT  
            } else {  
                Color.RED  
            }  
        )  
    )  

    if (produit.description.isNotEmpty()) {  

        carte.addView(  
            texte(  
                produit.description,  
                14f,  
                Color.DKGRAY  
            )  
        )  
    }  

    carte.addView(  
        boutonVert(  
            "🛒 Ajouter au panier"  
        ) {  

            if (produit.stock <= 0) {  

                toast(  
                    "Produit indisponible"  
                )  

                return@boutonVert  
            }  

            ajouterAuPanier(  
                produit  
            )  
        }  
    )  

    carte.addView(  
        bouton(  
            "👁️ Voir le produit"  
        ) {  

            afficherDetailProduit(  
                produit  
            )  
        }  
    )  

    carte.addView(  
        bouton(  
            "📤 Partager"  
        ) {  

            partagerProduit(  
                produit  
            )  
        }  
    )  

    val compte =  
        compteActuel()  

    if (  
        compte != null &&  
        compte.type == "VENDEUR" &&  
        compte.valide  
    ) {  

        val appartient =  
            produit.vendeur == compte.nom ||  
            produit.entreprise == compte.entreprise  

        if (appartient) {  

            carte.addView(  
                bouton(  
                    "✏️ Modifier / gérer"  
                ) {  

                    afficherModifierProduit(  
                        produit  
                    )  
                }  
            )  
        }  
    }  

    parent.addView(carte)  
}  

// ========================================================  
// DETAIL PRODUIT  
// ========================================================  

private fun afficherDetailProduit(  
    produit: Produit  
) {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "🛍️ ${produit.nom}"  
        )  
    )  

    if (produit.images.isNotEmpty()) {  

        val galerie =  
            LinearLayout(this).apply {  

                orientation =  
                    LinearLayout.HORIZONTAL  

                gravity =  
                    Gravity.CENTER  
            }  

        for (chemin in produit.images) {  

            try {  

                val image =  
                    ImageView(this).apply {  

                        layoutParams =  
                            LinearLayout.LayoutParams(  
                                90,  
                                90  
                            ).apply {  

                                setMargins(  
                                    4,  
                                    4,  
                                    4,  
                                    4  
                                )  
                            }  

                        scaleType =  
                            ImageView.ScaleType.CENTER_CROP  

                        setImageURI(  
                            Uri.parse(  
                                chemin  
                            )  
                        )  

                        setOnClickListener {  

                            val grand =  
                                ImageView(  
                                    this@MainActivity  
                                )  

                            grand.setImageURI(  
                                Uri.parse(  
                                    chemin  
                                )  
                            )  

                            grand.adjustViewBounds =  
                                true  

                            AlertDialog.Builder(  
                                this@MainActivity  
                            )  
                                .setView(grand)  
                                .setPositiveButton(  
                                    "Fermer",  
                                    null  
                                )  
                                .show()  
                        }  
                    }  

                galerie.addView(  
                    image  
                )  

            } catch (_: Exception) {  
            }  
        }  

        root.addView(  
            galerie  
        )  
    }  

    root.addView(  
        texte(  
            "💰 ${  
                formaterNombre(  
                    produit.prix.toIntOrNull()  
                        ?: 0  
                )  
            } FCFA",  
            21f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            typeface =  
                Typeface.DEFAULT_BOLD  
        }  
    )  

    root.addView(  
        texte(  
            "🏢 ${  
                produit.entreprise.ifEmpty {  
                    "Vendeur"  
                }  
            }",  
            16f,  
            OR  
        ).apply {  

            gravity =  
                Gravity.CENTER  
        }  
    )  

    root.addView(  
        texte(  
            produit.description.ifEmpty {  
                "Aucune description."  
            },  
            16f  
        )  
    )  

    root.addView(  
        texte(  
            if (produit.stock > 0) {  
                "📦 Stock disponible : ${produit.stock}"  
            } else {  
                "⚠️ Produit indisponible"  
            },  
            16f,  
            if (produit.stock > 0) {  
                VERT  
            } else {  
                Color.RED  
            }  
        ).apply {  
            gravity = Gravity.CENTER  
        }  
    )  

    root.addView(  
        boutonVert(  
            "🛒 Ajouter au panier"  
        ) {  

            if (produit.stock <= 0) {  

                toast(  
                    "Produit indisponible"  
                )  

            } else {  

                ajouterAuPanier(  
                    produit  
                )  
            }  
        }  
    )  

    root.addView(  
        bouton(  
            "📤 Partager"  
        ) {  

            partagerProduit(  
                produit  
            )  
        }  
    )  

    root.addView(  
        bouton(  
            "⬅️ Retour"  
        ) {  

            onBackPressed()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// PANIER  
// ========================================================  

private fun ajouterAuPanier(  
    produit: Produit  
) {  

    val existant =  
        panier.find {  
            it.produitId ==  
                produit.id  
        }  

    if (existant != null) {  

        if (  
            existant.quantite <  
            produit.stock  
        ) {  

            existant.quantite++  

        } else {  

            toast(  
                "Stock maximum atteint"  
            )  

            return  
        }  

    } else {  

        panier.add(  
            PanierItem(  
                produitId =  
                    produit.id,  

                nom =  
                    produit.nom,  

                prix =  
                    produit.prix  
                        .replace(" ", "")  
                        .toIntOrNull()  
                        ?: 0,  

                quantite = 1,  

                images =  
                    produit.images  
                        .toMutableList()  
            )  
        )  
    }  

    sauvegarderPanier()  

    toast(  
        "Produit ajouté au panier"  
    )  
}  

private fun afficherPanier() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "🛒 Mon panier"  
        )  
    )  

    if (panier.isEmpty()) {  

        root.addView(  
            texte(  
                "Votre panier est vide.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  

                setPadding(  
                    10,  
                    40,  
                    10,  
                    40  
                )  
            }  
        )  

    } else {  

        var total = 0  

        for (item in panier) {  

            val bloc =  
                LinearLayout(this).apply {  

                    orientation =  
                        LinearLayout.VERTICAL  

                    setPadding(  
                        12,  
                        12,  
                        12,  
                        12  
                    )  

                    setBackgroundColor(  
                        Color.WHITE  
                    )  

                    layoutParams =  
                        LinearLayout.LayoutParams(  
                            ViewGroup.LayoutParams.MATCH_PARENT,  
                            ViewGroup.LayoutParams.WRAP_CONTENT  
                        ).apply {  
                            setMargins(  
                                0,  
                                5,  
                                0,  
                                5  
                            )  
                        }  
                }  

            bloc.addView(  
                texte(  
                    item.nom,  
                    19f,  
                    VERT  
                ).apply {  

                    typeface =  
                        Typeface.DEFAULT_BOLD  
                }  
            )  

            bloc.addView(  
                texte(  
                    "${formaterNombre(item.prix)} FCFA",  
                    16f  
                )  
            )  

            bloc.addView(  
                texte(  
                    "Quantité : ${item.quantite}",  
                    16f  
                )  
            )  

            bloc.addView(  
                bouton(  
                    "➕ Ajouter 1"  
                ) {  

                    val produit =  
                        produits.find {  
                            it.id ==  
                                item.produitId  
                        }  

                    if (  
                        produit != null &&  
                        item.quantite <  
                        produit.stock  
                    ) {  

                        item.quantite++  

                        sauvegarderPanier()  

                        afficherPanier()  

                    } else {  

                        toast(  
                            "Stock maximum atteint"  
                        )  
                    }  
                }  
            )  

            bloc.addView(  
                bouton(  
                    "➖ Retirer 1"  
                ) {  

                    if (  
                        item.quantite > 1  
                    ) {  

                        item.quantite--  

                    } else {  

                        panier.remove(  
                            item  
                        )  
                    }  

                    sauvegarderPanier()  

                    afficherPanier()  
                }  
            )  

            root.addView(  
                bloc  
            )  

            total +=  
                item.prix *  
                item.quantite  
        }  

        root.addView(  
            texte(  
                "TOTAL : ${formaterNombre(total)} FCFA",  
                22f,  
                VERT  
            ).apply {  

                gravity =  
                    Gravity.CENTER  

                typeface =  
                    Typeface.DEFAULT_BOLD  

                setPadding(  
                    10,  
                    25,  
                    10,  
                    25  
                )  
            }  
        )  

        root.addView(  
            boutonVert(  
                "✅ Continuer la commande"  
            ) {  

                demanderInfosCommande(  
                    total  
                )  
            }  
        )  

        root.addView(  
            bouton(  
                "🗑️ Vider le panier"  
            ) {  

                panier.clear()  

                sauvegarderPanier()  

                afficherPanier()  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// INFORMATIONS COMMANDE  
// ========================================================  

private fun demanderInfosCommande(  
    total: Int  
) {  

    val box =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.VERTICAL  

            setPadding(  
                20,  
                5,  
                20,  
                5  
            )  
        }  

    val client =  
        EditText(this).apply {  
            hint = "Nom du client"  
        }  

    val tel =  
        EditText(this).apply {  

            hint = "Téléphone"  

            inputType =  
                InputType.TYPE_CLASS_PHONE  
        }  

    val adresse =  
        EditText(this).apply {  

            hint =  
                "Adresse de livraison"  

            minLines = 2  
        }  

    box.addView(client)  
    box.addView(tel)  
    box.addView(adresse)  

    val dialogue =  
        AlertDialog.Builder(this)  
            .setTitle(  
                "📦 Informations de commande"  
            )  
            .setView(box)  
            .setNegativeButton(  
                "Annuler",  
                null  
            )  
            .setPositiveButton(  
                "Continuer",  
                null  
            )  
            .create()  

    dialogue.setOnShowListener {  

        dialogue.getButton(  
            AlertDialog.BUTTON_POSITIVE  
        ).setOnClickListener {  

            val nomClient =  
                client.text  
                    .toString()  
                    .trim()  

            val telephone =  
                tel.text  
                    .toString()  
                    .trim()  

            val adresseTexte =  
                adresse.text  
                    .toString()  
                    .trim()  

            if (  
                nomClient.isEmpty()  
            ) {  

                toast(  
                    "Nom du client obligatoire"  
                )  

                return@setOnClickListener  
            }  

            if (  
                telephone.isEmpty()  
            ) {  

                toast(  
                    "Téléphone obligatoire"  
                )  

                return@setOnClickListener  
            }  

            if (  
                adresseTexte.isEmpty()  
            ) {  

                toast(  
                    "Adresse obligatoire"  
                )  

                return@setOnClickListener  
            }  

            dialogue.dismiss()  

            choisirPaiementCommande(  
                nomClient,  
                telephone,  
                total  
            )  
        }  
    }  

    dialogue.show()  
}  

// ========================================================  
// PAIEMENT  
// ========================================================  

private fun choisirPaiementCommande(  
    client: String,  
    telephone: String,  
    total: Int  
) {  

    val choix =  
        arrayOf(  
            "💵 Paiement en espèces",  
            "💳 Paiement en ligne",  
            "🚚 Paiement à la livraison"  
        )  

    AlertDialog.Builder(this)  
        .setTitle(  
            "💳 Choisissez le mode de paiement"  
        )  
        .setItems(  
            choix  
        ) { _, position ->  

            val paiement =  
                when (position) {  

                    0 ->  
                        "Espèces"  

                    1 ->  
                        "Paiement en ligne"  

                    2 ->  
                        "Paiement à la livraison"  

                    else ->  
                        ""  
                }  

            if (  
                paiement.isEmpty()  
            ) {  
                return@setItems  
            }  

            confirmerCommande(  
                client,  
                telephone,  
                total,  
                paiement  
            )  
        }  
        .setNegativeButton(  
            "Annuler",  
            null  
        )  
        .show()  
}  

// ========================================================  
// CONFIRMATION COMMANDE  
// ========================================================  

private fun confirmerCommande(  
    client: String,  
    telephone: String,  
    total: Int,  
    paiement: String  
) {  

    val produitsCommande =  
        panier.joinToString(", ") {  

            "${it.nom} x${it.quantite}"  
        }  

    AlertDialog.Builder(this)  
        .setTitle(  
            "📋 Récapitulatif"  
        )  
        .setMessage(  
            """  
            Client : $client  
              
            Produits :  
            $produitsCommande  
              
            Total :  
            ${formaterNombre(total)} FCFA  
              
            Paiement :  
            $paiement  
            """.trimIndent()  
        )  
        .setNegativeButton(  
            "Modifier",  
            null  
        )  
        .setPositiveButton(  
            "Confirmer la commande"  
        ) { _, _ ->  

            val commande =  
                Commande(  
                    id =  
                        System.currentTimeMillis()  
                            .toString()  
                            .takeLast(6),  

                    client =  
                        client,  

                    telephone =  
                        telephone,  

                    produit =  
                        produitsCommande,  

                    montant =  
                        total.toString(),  

                    paiement =  
                        paiement,  

                    statut =  
                        "Nouvelle"  
                )  

            commandes.add(  
                commande  
            )  

            // Déduction du stock  
            for (item in panier) {  

                val produit =  
                    produits.find {  
                        it.id ==  
                            item.produitId  
                    }  

                if (produit != null) {  

                    produit.stock =  
                        (  
                            produit.stock -  
                            item.quantite  
                        ).coerceAtLeast(0)  
                }  
            }  

            sauvegarderProduits()  
            sauvegarderCommandes()  

            enregistrerEvenement(  
                "Nouvelle commande #${commande.id} — $paiement — ${formaterNombre(total)} FCFA"  
            )  

            panier.clear()  

            sauvegarderPanier()  

            toast(  
                "✅ Commande enregistrée"  
            )  

            afficherAccueil()  
        }  
        .show()  
}  

// ========================================================  
// RECHERCHE  
// ========================================================  

private fun afficherProduits(  
    rechercheTexte: String  
) {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "🛍️ Produits"  
        )  
    )  

    val recherche =  
        EditText(this).apply {  

            hint =  
                "🔎 Rechercher..."  

            setSingleLine(true)  

            setText(  
                rechercheTexte  
            )  
        }  

    root.addView(  
        recherche  
    )  

    root.addView(  
        boutonVert(  
            "🔎 Rechercher"  
        ) {  

            afficherProduits(  
                recherche.text  
                    .toString()  
                    .trim()  
            )  
        }  
    )  

    val liste =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.VERTICAL  
        }  

    root.addView(  
        liste  
    )  

    val mot =  
        rechercheTexte.lowercase(  
            Locale.getDefault()  
        )  

    var nombre = 0  

    for (produit in produits) {  

        val nom =  
            produit.nom.lowercase(  
                Locale.getDefault()  
            )  

        val categorie =  
            produit.categorie.lowercase(  
                Locale.getDefault()  
            )  

        if (  
            mot.isEmpty() ||  
            nom.contains(mot) ||  
            categorie.contains(mot)  
        ) {  

            ajouterCarteProduit(  
                liste,  
                produit  
            )  

            nombre++  
        }  
    }  

    if (nombre == 0) {  

        liste.addView(  
            texte(  
                "Aucun produit trouvé.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  

                setPadding(  
                    10,  
                    35,  
                    10,  
                    35  
                )  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// AJOUT PRODUIT  
// ========================================================  

private fun afficherAjouterProduit() {  

    val compte =  
        compteActuel()  

    if (  
        compte == null ||  
        compte.type != "VENDEUR"  
    ) {  

        toast(  
            "Connectez-vous comme vendeur."  
        )  

        afficherConnexion()  

        return  
    }  

    if (!compte.valide) {  

        toast(  
            "Votre compte vendeur n'est pas validé."  
        )  

        return  
    }  

    if (!vendeurPeutUtiliser()) {  

        toast(  
            "Votre période gratuite est terminée. Veuillez prendre un abonnement."  
        )  

        afficherAbonnement()  

        return  
    }  

    imageProduit = ""  

    imagesProduit.clear()  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "➕ Ajouter un produit"  
        )  
    )  

    val nom =  
        EditText(this).apply {  

            hint =  
                "Nom du produit"  

            textSize = 16f  
        }  

    val prix =  
        EditText(this).apply {  

            hint =  
                "Prix en FCFA"  

            inputType =  
                InputType.TYPE_CLASS_NUMBER  
        }  

    val categorie =  
        EditText(this).apply {  

            hint =  
                "Catégorie"  
        }  

    val stock =  
        EditText(this).apply {  

            hint =  
                "Quantité en stock"  

            inputType =  
                InputType.TYPE_CLASS_NUMBER  
        }  

    val description =  
        EditText(this).apply {  

            hint =  
                "Description du produit"  

            minLines = 3  

            gravity =  
                Gravity.TOP  
        }  

    root.addView(nom)  
    root.addView(prix)  
    root.addView(categorie)  
    root.addView(stock)  
    root.addView(description)  

    galeriePhotos =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.HORIZONTAL  

            gravity =  
                Gravity.CENTER  
        }  

    root.addView(  
        texte(  
            "📸 Photos du produit",  
            17f,  
            VERT  
        )  
    )  

    root.addView(  
        galeriePhotos  
    )  

    root.addView(  
        bouton(  
            "📷 Prendre une photo"  
        ) {  

            ouvrirCamera()  
        }  
    )  

    root.addView(  
        bouton(  
            "🖼️ Ajouter des photos"  
        ) {  

            ouvrirGalerieMultiple()  
        }  
    )  

    root.addView(  
        boutonVert(  
            "💾 Enregistrer le produit"  
        ) {  

            val nomProduit =  
                nom.text  
                    .toString()  
                    .trim()  

            val prixProduit =  
                prix.text  
                    .toString()  
                    .trim()  

            val categorieProduit =  
                categorie.text  
                    .toString()  
                    .trim()  

            val stockProduit =  
                stock.text  
                    .toString()  
                    .toIntOrNull()  
                    ?: 0  

            val descriptionProduit =  
                description.text  
                    .toString()  
                    .trim()  

            if (  
                nomProduit.isEmpty()  
            ) {  

                toast(  
                    "Entrez le nom du produit"  
                )  

                return@boutonVert  
            }  

            if (  
                prixProduit.isEmpty()  
            ) {  

                toast(  
                    "Entrez le prix"  
                )  

                return@boutonVert  
            }  

            if (  
                prixProduit  
                    .toIntOrNull() == null  
            ) {  

                toast(  
                    "Le prix doit être un nombre"  
                )  

                return@boutonVert  
            }  

            val compteVendeur =  
                compteActuel()  

            produits.add(  
                Produit(  
                    nom =  
                        nomProduit,  

                    prix =  
                        prixProduit,  

                    categorie =  
                        categorieProduit  
                            .ifEmpty {  
                                "Autres"  
                            },  

                    stock =  
                        stockProduit,  

                    description =  
                        descriptionProduit,  

                    images =  
                        imagesProduit  
                            .toMutableList(),  

                    vendeur =  
                        compteVendeur  
                            ?.nom  
                            ?: "",  

                    entreprise =  
                        compteVendeur  
                            ?.entreprise  
                            ?: ""  
                )  
            )  

            sauvegarderProduits()  

            enregistrerEvenement(  
                "Nouveau produit ajouté : $nomProduit"  
            )  

            imagesProduit.clear()  

            imageProduit = ""  

            toast(  
                "Produit ajouté avec succès"  
            )  

            afficherAccueil()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// MODIFIER PRODUIT  
// ========================================================  

private fun afficherModifierProduit(  
    produit: Produit  
) {  

    val compte =  
        compteActuel()  

    if (  
        compte == null ||  
        compte.type != "VENDEUR"  
    ) {  

        toast(  
            "Accès vendeur requis"  
        )  

        return  
    }  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "✏️ Modifier le produit"  
        )  
    )  

    val nom =  
        EditText(this).apply {  

            hint =  
                "Nom"  

            setText(  
                produit.nom  
            )  
        }  

    val prix =  
        EditText(this).apply {  

            hint =  
                "Prix"  

            setText(  
                produit.prix  
            )  

            inputType =  
                InputType.TYPE_CLASS_NUMBER  
        }  

    val categorie =  
        EditText(this).apply {  

            hint =  
                "Catégorie"  

            setText(  
                produit.categorie  
            )  
        }  

    val stock =  
        EditText(this).apply {  

            hint =  
                "Stock"  

            setText(  
                produit.stock.toString()  
            )  

            inputType =  
                InputType.TYPE_CLASS_NUMBER  
        }  

    val description =  
        EditText(this).apply {  

            hint =  
                "Description"  

            setText(  
                produit.description  
            )  

            minLines = 3  

            gravity =  
                Gravity.TOP  
        }  

    root.addView(nom)  
    root.addView(prix)  
    root.addView(categorie)  
    root.addView(stock)  
    root.addView(description)  

    root.addView(  
        boutonVert(  
            "💾 Enregistrer"  
        ) {  

            val nouveauNom =  
                nom.text  
                    .toString()  
                    .trim()  

            val nouveauPrix =  
                prix.text  
                    .toString()  
                    .trim()  

            if (  
                nouveauNom.isEmpty()  
            ) {  

                toast(  
                    "Le nom est obligatoire"  
                )  

                return@boutonVert  
            }  

            if (  
                nouveauPrix.toIntOrNull() == null  
            ) {  

                toast(  
                    "Prix invalide"  
                )  

                return@boutonVert  
            }  

            produit.nom =  
                nouveauNom  

            produit.prix =  
                nouveauPrix  

            produit.categorie =  
                categorie.text  
                    .toString()  
                    .trim()  
                    .ifEmpty {  
                        "Autres"  
                    }  

            produit.stock =  
                stock.text  
                    .toString()  
                    .toIntOrNull()  
                    ?: 0  

            produit.description =  
                description.text  
                    .toString()  
                    .trim()  

            sauvegarderProduits()  

            enregistrerEvenement(  
                "Produit modifié : ${produit.nom}"  
            )  

            toast(  
                "Produit modifié"  
            )  

            afficherAccueil()  
        }  
    )  

    root.addView(  
        bouton(  
            "❌ Supprimer le produit"  
        ) {  

            AlertDialog.Builder(this)  
                .setTitle(  
                    "Supprimer"  
                )  
                .setMessage(  
                    "Supprimer ${produit.nom} ?"  
                )  
                .setNegativeButton(  
                    "Annuler",  
                    null  
                )  
                .setPositiveButton(  
                    "Supprimer"  
                ) { _, _ ->  

                    produits.remove(  
                        produit  
                    )  

                    sauvegarderProduits()  

                    enregistrerEvenement(  
                        "Produit supprimé : ${produit.nom}"  
                    )  

                    afficherAccueil()  
                }  
                .show()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// CAMERA  
// ========================================================  

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
            CAMERA_REQUEST  
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
            CAMERA_REQUEST  
        )  

    } catch (_: Exception) {  

        toast(  
            "Impossible d'ouvrir la caméra"  
        )  
    }  
}  

// ========================================================  
// GALERIE  
// ========================================================  

private fun ouvrirGalerieMultiple() {  

    try {  

        val intent =  
            Intent(  
                Intent.ACTION_OPEN_DOCUMENT  
            ).apply {  

                type =  
                    "image/*"  

                putExtra(  
                    Intent.EXTRA_ALLOW_MULTIPLE,  
                    true  
                )  

                addCategory(  
                    Intent.CATEGORY_OPENABLE  
                )  
            }  

        startActivityForResult(  
            intent,  
            MULTI_GALLERY_REQUEST  
        )  

    } catch (_: Exception) {  

        toast(  
            "Impossible d'ouvrir la galerie"  
        )  
    }  
}  

// ========================================================  
// PERMISSION  
// ========================================================  

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
        CAMERA_REQUEST &&  
        grantResults.isNotEmpty() &&  
        grantResults[0] ==  
        PackageManager.PERMISSION_GRANTED  
    ) {  

        ouvrirCamera()  
    }  
}  

// ========================================================  
// RESULTAT PHOTO  
// ========================================================  

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
        resultCode != RESULT_OK  
    ) {  
        return  
    }  

    if (  
        requestCode ==  
        MULTI_GALLERY_REQUEST  
    ) {  

        val clipData =  
            data?.clipData  

        if (clipData != null) {  

            for (  
                i in 0 until clipData.itemCount  
            ) {  

                val uri =  
                    clipData  
                        .getItemAt(i)  
                        .uri  

                ajouterImageProduit(  
                    uri.toString()  
                )  
            }  

        } else {  

            val uri =  
                data?.data  

            if (uri != null) {  

                ajouterImageProduit(  
                    uri.toString()  
                )  
            }  
        }  

        toast(  
            "Photo(s) ajoutée(s)"  
        )  
    }  

    if (  
        requestCode ==  
        CAMERA_REQUEST  
    ) {  

        val bitmap =  
            data?.extras?.get("data")  
                as? Bitmap  

        if (bitmap != null) {  

            try {  

                val chemin =  
                    sauvegarderBitmap(  
                        bitmap  
                    )  

                ajouterImageProduit(  
                    chemin  
                )  

                toast(  
                    "Photo ajoutée"  
                )  

            } catch (_: Exception) {  

                toast(  
                    "Impossible d'enregistrer la photo"  
                )  
            }  
        }  
    }  
}  

private fun ajouterImageProduit(  
    chemin: String  
) {  

    if (  
        !imagesProduit.contains(  
            chemin  
        )  
    ) {  

        imagesProduit.add(  
            chemin  
        )  
    }  

    if (  
        ::galeriePhotos.isInitialized  
    ) {  

        val image =  
            ImageView(this).apply {  

                layoutParams =  
                    LinearLayout.LayoutParams(  
                        75,  
                        75  
                    ).apply {  

                        setMargins(  
                            4,  
                            4,  
                            4,  
                            4  
                        )  
                    }  

                scaleType =  
                    ImageView.ScaleType.CENTER_CROP  

                setImageURI(  
                    Uri.parse(  
                        chemin  
                    )  
                )  
            }  

        galeriePhotos.addView(  
            image  
        )  
    }  
}  

private fun sauvegarderBitmap(  
    bitmap: Bitmap  
): String {  

    val fichier =  
        File(  
            filesDir,  
            "produit_${System.currentTimeMillis()}.jpg"  
        )  

    FileOutputStream(  
        fichier  
    ).use { sortie ->  

        bitmap.compress(  
            Bitmap.CompressFormat.JPEG,  
            90,  
            sortie  
        )  
    }  

    return Uri.fromFile(  
        fichier  
    ).toString()  
}  

// ========================================================  
// MENU  
// ========================================================  

private fun afficherMenu() {  

    val compte =  
        compteActuel()  

    val choix =  
        ArrayList<String>()  

    choix.add(  
        "🏠 Accueil"  
    )  

    choix.add(  
        "🛍️ Produits"  
    )  

    choix.add(  
        "🛒 Mon panier"  
    )  

    if (  
        compte == null ||  
        compte.type != "VENDEUR"  
    ) {  

        choix.add(  
            "👤 Créer un compte"  
        )  

        choix.add(  
            "🔐 Se connecter"  
        )  
    }  

    if (  
        compte != null &&  
        compte.type == "VENDEUR"  
    ) {  

        choix.add(  
            "👨‍💼 Espace vendeur"  
        )  

        choix.add(  
            "➕ Ajouter un produit"  
        )  

        choix.add(  
            "📦 Mes commandes"  
        )  

        choix.add(  
            "📊 Tableau de bord"  
        )  

        choix.add(  
            "📦 Gestion du stock"  
        )  

        choix.add(  
            "💎 Abonnement"  
        )  
    }  

    choix.add(  
        "👤 Mon compte"  
    )  

    choix.add(  
        "📞 Contact / WhatsApp"  
    )  

    choix.add(  
        "📤 Partager l'application"  
    )  

    choix.add(  
        "🔐 Administration"  
    )  

    choix.add(  
        "🚪 Quitter"  
    )  

    AlertDialog.Builder(this)  
        .setTitle(  
            "☰ VIE ESPOIR MARKETING"  
        )  
        .setItems(  
            choix.toTypedArray()  
        ) { _, position ->  

            when (  
                choix[position]  
            ) {  

                "🏠 Accueil" ->  
                    afficherAccueil()  

                "🛍️ Produits" ->  
                    afficherProduits("")  

                "🛒 Mon panier" ->  
                    afficherPanier()  

                "👤 Créer un compte" ->  
                    afficherCreationCompte()  

                "🔐 Se connecter" ->  
                    afficherConnexion()  

                "👨‍💼 Espace vendeur" ->  
                    afficherEspaceVendeur()  

                "➕ Ajouter un produit" ->  
                    afficherAjouterProduit()  

                "📦 Mes commandes" ->  
                    afficherCommandes()  

                "📊 Tableau de bord" ->  
                    afficherTableauDeBord()  

                "📦 Gestion du stock" ->  
                    afficherStock()  

                "💎 Abonnement" ->  
                    afficherAbonnement()  

                "👤 Mon compte" ->  
                    afficherCompte()  

                "📞 Contact / WhatsApp" ->  
                    afficherContact()  

                "📤 Partager l'application" ->  
                    partagerApplication()  

                "🔐 Administration" ->  
                    afficherConnexionAdmin()  

                "🚪 Quitter" ->  
                    confirmerQuitter()  
            }  
        }  
        .show()  
}  

// ========================================================  
// CREATION COMPTE  
// ========================================================  

private fun afficherCreationCompte() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "➕ Créer mon compte"  
        )  
    )  

    val nom =  
        EditText(this).apply {  
            hint =  
                "Nom complet"  
        }  

    val entreprise =  
        EditText(this).apply {  
            hint =  
                "Nom de l'entreprise / boutique"  
        }  

    val email =  
        EditText(this).apply {  

            hint =  
                "Adresse e-mail"  

            inputType =  
                InputType.TYPE_CLASS_TEXT or  
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS  
        }  

    val phone =  
        EditText(this).apply {  

            hint =  
                "Téléphone"  

            inputType =  
                InputType.TYPE_CLASS_PHONE  
        }  

    val adresse =  
        EditText(this).apply {  

            hint =  
                "Adresse"  
        }  

    val password =  
        EditText(this).apply {  

            hint =  
                "Mot de passe"  

            inputType =  
                InputType.TYPE_CLASS_TEXT or  
                InputType.TYPE_TEXT_VARIATION_PASSWORD  
        }  

    root.addView(nom)  
    root.addView(entreprise)  
    root.addView(email)  
    root.addView(phone)  
    root.addView(adresse)  
    root.addView(password)  

    root.addView(  
        texte(  
            "🎁 Nouveau vendeur : 7 jours gratuits.",  
            16f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  
        }  
    )  

    root.addView(  
        boutonVert(  
            "✅ Créer mon compte"  
        ) {  

            val n =  
                nom.text  
                    .toString()  
                    .trim()  

            val ent =  
                entreprise.text  
                    .toString()  
                    .trim()  

            val e =  
                email.text  
                    .toString()  
                    .trim()  

            val p =  
                phone.text  
                    .toString()  
                    .trim()  

            val a =  
                adresse.text  
                    .toString()  
                    .trim()  

            val mdp =  
                password.text  
                    .toString()  

            if (  
                n.isEmpty() ||  
                ent.isEmpty() ||  
                e.isEmpty() ||  
                p.isEmpty() ||  
                a.isEmpty() ||  
                mdp.isEmpty()  
            ) {  

                toast(  
                    "Tous les champs sont obligatoires"  
                )  

                return@boutonVert  
            }  

            if (  
                !android.util.Patterns.EMAIL_ADDRESS  
                    .matcher(e)  
                    .matches()  
            ) {  

                toast(  
                    "Adresse e-mail invalide"  
                )  

                return@boutonVert  
            }  

            if (  
                comptes.any {  
                    it.email.equals(  
                        e,  
                        ignoreCase = true  
                    )  
                }  
            ) {  

                toast(  
                    "Cet e-mail est déjà utilisé"  
                )  

                return@boutonVert  
            }  

            val compte =  
                Compte(  
                    id =  
                        System.currentTimeMillis()  
                            .toString(),  

                    nom =  
                        n,  

                    entreprise =  
                        ent,  

                    email =  
                        e,  

                    telephone =  
                        p,  

                    adresse =  
                        a,  

                    motDePasse =  
                        mdp,  

                    type =  
                        "VENDEUR",  

                    dateCreation =  
                        System.currentTimeMillis(),  

                    valide =  
                        true  
                )  

            comptes.add(  
                compte  
            )  

            compteConnecteId =  
                compte.id  

            prefs.edit()  
                .putString(  
                    CONNECTED_ACCOUNT,  
                    compte.id  
                )  
                .apply()  

            sauvegarderComptes()  

            enregistrerEvenement(  
                "Nouveau vendeur inscrit : $n — $ent"  
            )  

            toast(  
                "Compte créé. Vous avez 7 jours gratuits."  
            )  

            afficherEspaceVendeur()  
        }  
    )  

    root.addView(  
        bouton(  
            "🔐 J'ai déjà un compte"  
        ) {  

            afficherConnexion()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// CONNEXION  
// ========================================================  

private fun afficherConnexion() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "🔐 Connexion"  
        )  
    )  

    val email =  
        EditText(this).apply {  

            hint =  
                "E-mail"  

            inputType =  
                InputType.TYPE_CLASS_TEXT or  
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS  
        }  

    val password =  
        EditText(this).apply {  

            hint =  
                "Mot de passe"  

            inputType =  
                InputType.TYPE_CLASS_TEXT or  
                InputType.TYPE_TEXT_VARIATION_PASSWORD  
        }  

    root.addView(email)  
    root.addView(password)  

    root.addView(  
        boutonVert(  
            "🔓 Se connecter"  
        ) {  

            val e =  
                email.text  
                    .toString()  
                    .trim()  

            val mdp =  
                password.text  
                    .toString()  

            val compte =  
                comptes.find {  

                    it.email.equals(  
                        e,  
                        ignoreCase = true  
                    ) &&  
                    it.motDePasse == mdp  
                }  

            if (compte == null) {  

                toast(  
                    "E-mail ou mot de passe incorrect"  
                )  

                return@boutonVert  
            }  

            compteConnecteId =  
                compte.id  

            prefs.edit()  
                .putString(  
                    CONNECTED_ACCOUNT,  
                    compte.id  
                )  
                .apply()  

            toast(  
                "Connexion réussie"  
            )  

            afficherEspaceVendeur()  
        }  
    )  

    root.addView(  
        bouton(  
            "🔑 Mot de passe oublié ?"  
        ) {  

            afficherMotDePasseOublie()  
        }  
    )  

    root.addView(  
        bouton(  
            "➕ Créer un nouveau compte"  
        ) {  

            afficherCreationCompte()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// MOT DE PASSE OUBLIE  
// ========================================================  

private fun afficherMotDePasseOublie() {  

    val champ =  
        EditText(this).apply {  

            hint =  
                "Votre adresse e-mail"  

            inputType =  
                InputType.TYPE_CLASS_TEXT or  
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS  
        }  

    AlertDialog.Builder(this)  
        .setTitle(  
            "🔑 Mot de passe oublié"  
        )  
        .setMessage(  
            "Dans cette version locale, la récupération automatique n'est pas encore connectée à un serveur."  
        )  
        .setView(champ)  
        .setNegativeButton(  
            "Annuler",  
            null  
        )  
        .setPositiveButton(  
            "Continuer"  
        ) { _, _ ->  

            val email =  
                champ.text  
                    .toString()  
                    .trim()  

            val compte =  
                comptes.find {  

                    it.email.equals(  
                        email,  
                        ignoreCase = true  
                    )  
                }  

            if (compte == null) {  

                toast(  
                    "Compte introuvable"  
                )  

            } else {  

                toast(  
                    "Contactez l'administrateur pour réinitialiser le mot de passe."  
                )  
            }  
        }  
        .show()  
}  

// ========================================================  
// ESPACE VENDEUR  
// ========================================================  

private fun afficherEspaceVendeur() {  

    val compte =  
        compteActuel()  

    if (compte == null) {  

        afficherConnexion()  

        return  
    }  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "👨‍💼 Espace vendeur"  
        )  
    )  

    root.addView(  
        texte(  
            """  
            👋 ${compte.nom}  
              
            🏢 ${compte.entreprise}  
              
            📧 ${compte.email}  
              
            📞 ${compte.telephone}  
            """.trimIndent(),  
            17f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            typeface =  
                Typeface.DEFAULT_BOLD  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  
    )  

    val jours =  
        joursGratuitsRestants(  
            compte  
        )  

    if (jours > 0) {  

        root.addView(  
            texte(  
                "🎁 Il vous reste $jours jour(s) gratuit(s).",  
                17f,  
                VERT  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  

    } else {  

        root.addView(  
            texte(  
                "⚠️ Votre période gratuite est terminée.",  
                17f,  
                Color.RED  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  
    }  

    root.addView(  
        boutonVert(  
            "➕ Ajouter un produit"  
        ) {  

            afficherAjouterProduit()  
        }  
    )  

    root.addView(  
        bouton(  
            "🛍️ Mes produits"  
        ) {  

            afficherMesProduitsVendeur()  
        }  
    )  

    root.addView(  
        bouton(  
            "📦 Mes commandes"  
        ) {  

            afficherCommandes()  
        }  
    )  

    root.addView(  
        bouton(  
            "📊 Mon tableau de bord"  
        ) {  

            afficherTableauDeBord()  
        }  
    )  

    root.addView(  
        bouton(  
            "📦 Gestion du stock"  
        ) {  

            afficherStock()  
        }  
    )  

    root.addView(  
        bouton(  
            "💎 Mon abonnement"  
        ) {  

            afficherAbonnement()  
        }  
    )  

    root.addView(  
        bouton(  
            "🚪 Se déconnecter"  
        ) {  

            deconnecter()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// MES PRODUITS VENDEUR  
// ========================================================  

private fun afficherMesProduitsVendeur() {  

    val compte =  
        compteActuel()  

    if (compte == null) {  

        afficherConnexion()  

        return  
    }  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "🛍️ Mes produits"  
        )  
    )  

    val liste =  
        LinearLayout(this).apply {  
            orientation =  
                LinearLayout.VERTICAL  
        }  

    root.addView(  
        liste  
    )  

    var nombre = 0  

    for (produit in produits) {  

        val appartient =  
            produit.vendeur == compte.nom ||  
            produit.entreprise == compte.entreprise  

        if (appartient) {  

            ajouterCarteProduit(  
                liste,  
                produit  
            )  

            nombre++  
        }  
    }  

    if (nombre == 0) {  

        liste.addView(  
            texte(  
                "Vous n'avez encore aucun produit.",  
                17f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  

                setPadding(  
                    10,  
                    35,  
                    10,  
                    35  
                )  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// COMPTE  
// ========================================================  

private fun afficherCompte() {  

    val compte =  
        compteActuel()  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "👤 Mon compte"  
        )  
    )  

    if (compte == null) {  

        root.addView(  
            texte(  
                "Vous n'êtes pas connecté.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  

        root.addView(  
            boutonVert(  
                "🔐 Se connecter"  
            ) {  

                afficherConnexion()  
            }  
        )  

        root.addView(  
            bouton(  
                "➕ Créer un compte"  
            ) {  

                afficherCreationCompte()  
            }  
        )  

    } else {  

        root.addView(  
            texte(  
                """  
                👤 ${compte.nom}  
                  
                🏢 ${compte.entreprise}  
                  
                📧 ${compte.email}  
                  
                📞 ${compte.telephone}  
                  
                📍 ${compte.adresse}  
                  
                👤 Type : ${compte.type}  
                """.trimIndent(),  
                17f,  
                VERT  
            ).apply {  

                gravity =  
                    Gravity.CENTER  

                setBackgroundColor(  
                    Color.WHITE  
                )  
            }  
        )  

        root.addView(  
            boutonVert(  
                "👨‍💼 Espace vendeur"  
            ) {  

                afficherEspaceVendeur()  
            }  
        )  

        root.addView(  
            bouton(  
                "🚪 Se déconnecter"  
            ) {  

                deconnecter()  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

private fun deconnecter() {  

    compteConnecteId = ""  

    prefs.edit()  
        .remove(  
            CONNECTED_ACCOUNT  
        )  
        .apply()  

    toast(  
        "Déconnexion effectuée"  
    )  

    afficherAccueil()  
}  

// ========================================================  
// ABONNEMENT  
// ========================================================  

private fun afficherAbonnement() {  

    val compte =  
        compteActuel()  

    if (compte == null) {  

        afficherConnexion()  

        return  
    }  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "💎 Abonnement vendeur"  
        )  
    )  

    val jours =  
        joursGratuitsRestants(  
            compte  
        )  

    if (jours > 0) {  

        root.addView(  
            texte(  
                "🎁 Essai gratuit : $jours jour(s) restant(s).",  
                18f,  
                VERT  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  
    }  

    root.addView(  
        texte(  
            "Après l'essai gratuit, l'abonnement commence à 5 000 FCFA par mois.",  
            16f  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  
    )  

    root.addView(  
        boutonVert(  
            "💎 5 000 FCFA / mois"  
        ) {  

            afficherPaiementAbonnement()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

private fun afficherPaiementAbonnement() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "💳 Paiement abonnement"  
        )  
    )  

    root.addView(  
        texte(  
            """  
            Abonnement :  
            5 000 FCFA / mois  
              
            Choisissez votre moyen de paiement.  
            """.trimIndent(),  
            18f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  
    )  

    root.addView(  
        boutonVert(  
            "🟠 Orange Money / Wave"  
        ) {  

            appelerNumero(  
                ORANGE_WAVE  
            )  

            enregistrerEvenement(  
                "Demande de paiement abonnement — Orange/Wave"  
            )  
        }  
    )  

    root.addView(  
        boutonVert(  
            "🟡 MTN Mobile Money"  
        ) {  

            appelerNumero(  
                MTN  
            )  

            enregistrerEvenement(  
                "Demande de paiement abonnement — MTN"  
            )  
        }  
    )  

    root.addView(  
        bouton(  
            "💬 WhatsApp"  
        ) {  

            ouvrirWhatsApp(  
                ORANGE_WAVE  
            )  
        }  
    )  

    root.addView(  
        texte(  
            "Après paiement, l'administrateur doit confirmer l'activation.",  
            14f,  
            Color.GRAY  
        ).apply {  

            gravity =  
                Gravity.CENTER  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// COMMANDES  
// ========================================================  

private fun afficherCommandes() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "📦 Commandes"  
        )  
    )  

    if (commandes.isEmpty()) {  

        root.addView(  
            texte(  
                "Aucune commande.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  

    } else {  

        for (commande in commandes) {  

            val bloc =  
                LinearLayout(this).apply {  

                    orientation =  
                        LinearLayout.VERTICAL  

                    setPadding(  
                        12,  
                        12,  
                        12,  
                        12  
                    )  

                    setBackgroundColor(  
                        Color.WHITE  
                    )  

                    layoutParams =  
                        LinearLayout.LayoutParams(  
                            ViewGroup.LayoutParams.MATCH_PARENT,  
                            ViewGroup.LayoutParams.WRAP_CONTENT  
                        ).apply {  

                            setMargins(  
                                0,  
                                5,  
                                0,  
                                5  
                            )  
                        }  
                }  

            bloc.addView(  
                texte(  
                    """  
                    📦 Commande #${commande.id}  
                      
                    👤 ${commande.client}  
                      
                    📞 ${commande.telephone}  
                      
                    🛍️ ${commande.produit}  
                      
                    💰 ${  
                        formaterNombre(  
                            commande.montant  
                                .toIntOrNull()  
                                ?: 0  
                        )  
                    } FCFA  
                      
                    💳 Paiement : ${commande.paiement}  
                      
                    📌 ${commande.statut}  
                    """.trimIndent(),  
                    16f  
                )  
            )  

            bloc.addView(  
                boutonVert(  
                    "✅ Confirmer"  
                ) {  

                    commande.statut =  
                        "Confirmée"  

                    sauvegarderCommandes()  

                    enregistrerEvenement(  
                        "Commande #${commande.id} confirmée"  
                    )  

                    afficherCommandes()  
                }  
            )  

            bloc.addView(  
                bouton(  
                    "📦 Préparer"  
                ) {  

                    commande.statut =  
                        "En préparation"  

                    sauvegarderCommandes()  

                    afficherCommandes()  
                }  
            )  

            bloc.addView(  
                bouton(  
                    "🚚 En livraison"  
                ) {  

                    commande.statut =  
                        "En livraison"  

                    sauvegarderCommandes()  

                    afficherCommandes()  
                }  
            )  

            bloc.addView(  
                bouton(  
                    "✅ Livrée"  
                ) {  

                    commande.statut =  
                        "Livrée"  

                    sauvegarderCommandes()  

                    afficherCommandes()  
                }  
            )  

            bloc.addView(  
                bouton(  
                    "❌ Annuler"  
                ) {  

                    commande.statut =  
                        "Annulée"  

                    sauvegarderCommandes()  

                    afficherCommandes()  
                }  
            )  

            root.addView(  
                bloc  
            )  
        }  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// TABLEAU DE BORD  
// ========================================================  

private fun afficherTableauDeBord() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "📊 Tableau de bord"  
        )  
    )  

    var montant = 0  

    for (commande in commandes) {  

        if (  
            commande.statut !=  
            "Annulée"  
        ) {  

            montant +=  
                commande.montant  
                    .toIntOrNull()  
                    ?: 0  
        }  
    }  

    root.addView(  
        texte(  
            """  
            🛍️ Produits : ${produits.size}  
              
            📦 Commandes : ${commandes.size}  
              
            👥 Comptes : ${comptes.size}  
              
            💰 Chiffre d'affaires :  
            ${formaterNombre(montant)} FCFA  
            """.trimIndent(),  
            19f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            typeface =  
                Typeface.DEFAULT_BOLD  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// STOCK  
// ========================================================  

private fun afficherStock() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "📦 Gestion du stock"  
        )  
    )  

    for (produit in produits) {  

        val bloc =  
            LinearLayout(this).apply {  

                orientation =  
                    LinearLayout.VERTICAL  

                setPadding(  
                    12,  
                    12,  
                    12,  
                    12  
                )  

                setBackgroundColor(  
                    Color.WHITE  
                )  
            }  

        bloc.addView(  
            texte(  
                produit.nom,  
                18f,  
                VERT  
            ).apply {  

                typeface =  
                    Typeface.DEFAULT_BOLD  
            }  
        )  

        bloc.addView(  
            texte(  
                "Stock : ${produit.stock}",  
                16f,  
                if (produit.stock <= 5) {  
                    Color.RED  
                } else {  
                    VERT  
                }  
            )  
        )  

        bloc.addView(  
            bouton(  
                "➕ Ajouter du stock"  
            ) {  

                val champ =  
                    EditText(this).apply {  

                        hint =  
                            "Quantité"  

                        inputType =  
                            InputType.TYPE_CLASS_NUMBER  
                    }  

                AlertDialog.Builder(this)  
                    .setTitle(  
                        "Ajouter du stock"  
                    )  
                    .setView(  
                        champ  
                    )  
                    .setNegativeButton(  
                        "Annuler",  
                        null  
                    )  
                    .setPositiveButton(  
                        "Ajouter"  
                    ) { _, _ ->  

                        val q =  
                            champ.text  
                                .toString()  
                                .toIntOrNull()  
                                ?: 0  

                        if (q <= 0) {  

                            toast(  
                                "Quantité invalide"  
                            )  

                            return@setPositiveButton  
                        }  

                        produit.stock +=  
                            q  

                        sauvegarderProduits()  

                        afficherStock()  
                    }  
                    .show()  
            }  
        )  

        root.addView(  
            bloc  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// ADMINISTRATION  
// ========================================================  

private fun afficherConnexionAdmin() {  

    val email =  
        EditText(this).apply {  

            hint =  
                "Email administrateur"  
        }  

    val password =  
        EditText(this).apply {  

            hint =  
                "Mot de passe administrateur"  

            inputType =  
                InputType.TYPE_CLASS_TEXT or  
                InputType.TYPE_TEXT_VARIATION_PASSWORD  
        }  

    val box =  
        LinearLayout(this).apply {  

            orientation =  
                LinearLayout.VERTICAL  

            setPadding(  
                20,  
                5,  
                20,  
                5  
            )  

            addView(email)  
            addView(password)  
        }  

    AlertDialog.Builder(this)  
        .setTitle(  
            "🔐 Administration privée"  
        )  
        .setView(  
            box  
        )  
        .setNegativeButton(  
            "Annuler",  
            null  
        )  
        .setPositiveButton(  
            "Connexion"  
        ) { _, _ ->  

            if (  
                email.text  
                    .toString()  
                    .trim()  
                    ==  
                ADMIN_EMAIL &&  
                password.text  
                    .toString()  
                    ==  
                ADMIN_PASSWORD  
            ) {  

                adminConnecte =  
                    true  

                afficherAdministration()  

            } else {  

                toast(  
                    "Accès administrateur refusé"  
                )  
            }  
        }  
        .show()  
}  

private fun afficherAdministration() {  

    if (!adminConnecte) {  

        afficherConnexionAdmin()  

        return  
    }  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "👑 Administration"  
        )  
    )  

    root.addView(  
        texte(  
            "🔐 Espace réservé à l'administrateur",  
            17f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            typeface =  
                Typeface.DEFAULT_BOLD  
        }  
    )  

    root.addView(  
        texte(  
            """  
            👥 Comptes : ${comptes.size}  
              
            🛍️ Produits : ${produits.size}  
              
            📦 Commandes : ${commandes.size}  
              
            🔔 Événements : ${evenementsAdmin.size}  
            """.trimIndent(),  
            18f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  
    )  

    root.addView(  
        boutonVert(  
            "👥 Voir les vendeurs"  
        ) {  

            afficherVendeursAdmin()  
        }  
    )  

    root.addView(  
        boutonVert(  
            "📦 Voir les commandes"  
        ) {  

            afficherCommandesAdmin()  
        }  
    )  

    root.addView(  
        boutonVert(  
            "🔔 Voir les notifications"  
        ) {  

            afficherEvenementsAdmin()  
        }  
    )  

    root.addView(  
        bouton(  
            "🚪 Fermer l'administration"  
        ) {  

            adminConnecte =  
                false  

            afficherAccueil()  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// ADMIN VENDEURS  
// ========================================================  

private fun afficherVendeursAdmin() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "👥 Vendeurs"  
        )  
    )  

    var nombre = 0  

    for (compte in comptes) {  

        if (  
            compte.type !=  
            "VENDEUR"  
        ) {  
            continue  
        }  

        nombre++  

        val bloc =  
            LinearLayout(this).apply {  

                orientation =  
                    LinearLayout.VERTICAL  

                setPadding(  
                    12,  
                    12,  
                    12,  
                    12  
                )  

                setBackgroundColor(  
                    Color.WHITE  
                )  

                layoutParams =  
                    LinearLayout.LayoutParams(  
                        ViewGroup.LayoutParams.MATCH_PARENT,  
                        ViewGroup.LayoutParams.WRAP_CONTENT  
                    ).apply {  

                        setMargins(  
                            0,  
                            5,  
                            0,  
                            5  
                        )  
                    }  
            }  

        bloc.addView(  
            texte(  
                """  
                👤 ${compte.nom}  
                  
                🏢 ${compte.entreprise}  
                  
                📧 ${compte.email}  
                  
                📞 ${compte.telephone}  
                  
                📍 ${compte.adresse}  
                  
                Statut : ${  
                    if (compte.valide)  
                        "ACTIF"  
                    else  
                        "NON ACTIF"  
                }  
                """.trimIndent(),  
                16f,  
                VERT  
            )  
        )  

        bloc.addView(  
            boutonVert(  
                if (compte.valide)  
                    "⛔ Désactiver"  
                else  
                    "✅ Activer"  
            ) {  

                compte.valide =  
                    !compte.valide  

                sauvegarderComptes()  

                enregistrerEvenement(  
                    if (compte.valide) {  
                        "Vendeur activé : ${compte.entreprise}"  
                    } else {  
                        "Vendeur désactivé : ${compte.entreprise}"  
                    }  
                )  

                afficherVendeursAdmin()  
            }  
        )  

        root.addView(  
            bloc  
        )  
    }  

    if (nombre == 0) {  

        root.addView(  
            texte(  
                "Aucun vendeur enregistré.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// ADMIN COMMANDES  
// ========================================================  

private fun afficherCommandesAdmin() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "📦 Toutes les commandes"  
        )  
    )  

    if (commandes.isEmpty()) {  

        root.addView(  
            texte(  
                "Aucune commande.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  
    }  

    for (commande in commandes) {  

        root.addView(  
            texte(  
                """  
                📦 #${commande.id}  
                  
                👤 ${commande.client}  
                  
                📞 ${commande.telephone}  
                  
                🛍️ ${commande.produit}  
                  
                💰 ${  
                    formaterNombre(  
                        commande.montant  
                            .toIntOrNull()  
                            ?: 0  
                    )  
                } FCFA  
                  
                💳 ${commande.paiement}  
                  
                📌 ${commande.statut}  
                """.trimIndent(),  
                16f,  
                TEXTE  
            ).apply {  

                setBackgroundColor(  
                    Color.WHITE  
                )  

                setPadding(  
                    12,  
                    12,  
                    12,  
                    12  
                )  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// ADMIN EVENEMENTS  
// ========================================================  

private fun afficherEvenementsAdmin() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "🔔 Notifications administrateur"  
        )  
    )  

    if (  
        evenementsAdmin.isEmpty()  
    ) {  

        root.addView(  
            texte(  
                "Aucun événement.",  
                18f  
            ).apply {  

                gravity =  
                    Gravity.CENTER  
            }  
        )  
    }  

    for (  
        evenement in  
        evenementsAdmin.asReversed()  
    ) {  

        root.addView(  
            texte(  
                "🔔 $evenement",  
                16f  
            ).apply {  

                setBackgroundColor(  
                    Color.WHITE  
                )  

                setPadding(  
                    12,  
                    12,  
                    12,  
                    12  
                )  
            }  
        )  
    }  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// CONTACT  
// ========================================================  

private fun afficherContact() {  

    val root =  
        layoutBase()  

    root.addView(  
        titre(  
            "📞 Contact Vie Espoir Marketing"  
        )  
    )  

    root.addView(  
        texte(  
            """  
            🟠 Orange / Wave  
            $ORANGE_WAVE  
              
            🟡 MTN  
            $MTN  
            """.trimIndent(),  
            19f,  
            VERT  
        ).apply {  

            gravity =  
                Gravity.CENTER  

            typeface =  
                Typeface.DEFAULT_BOLD  

            setBackgroundColor(  
                Color.WHITE  
            )  
        }  
    )  

    root.addView(  
        boutonVert(  
            "📞 Appeler Orange / Wave"  
        ) {  

            appelerNumero(  
                ORANGE_WAVE  
            )  
        }  
    )  

    root.addView(  
        boutonVert(  
            "📞 Appeler MTN"  
        ) {  

            appelerNumero(  
                MTN  
            )  
        }  
    )  

    root.addView(  
        bouton(  
            "💬 WhatsApp"  
        ) {  

            ouvrirWhatsApp(  
                ORANGE_WAVE  
            )  
        }  
    )  

    setContentView(  
        scroll(root)  
    )  
}  

// ========================================================  
// APPEL  
// ========================================================  

private fun appelerNumero(  
    numero: String  
) {  

    try {  

        startActivity(  
            Intent(  
                Intent.ACTION_DIAL,  
                Uri.parse(  
                    "tel:$numero"  
                )  
            )  
        )  

    } catch (_: Exception) {  

        toast(  
            "Impossible d'ouvrir le téléphone"  
        )  
    }  
}  

// ========================================================  
// WHATSAPP  
// ========================================================  

private fun ouvrirWhatsApp(  
    numero: String  
) {  

    val propre =  
        numero  
            .replace(  
                "+",  
                ""  
            )  
            .replace(  
                " ",  
                ""  
            )  

    val international =  
        if (  
            propre.startsWith("0")  
        ) {  
            "225$propre"  
        } else {  
            propre  
        }  

    try {  

        startActivity(  
            Intent(  
                Intent.ACTION_VIEW,  
                Uri.parse(  
                    "https://wa.me/$international"  
                )  
            )  
        )  

    } catch (_: Exception) {  

        toast(  
            "WhatsApp n'est pas disponible"  
        )  
    }  
}  

// ========================================================  
// PARTAGE PRODUIT  
// ========================================================  

private fun partagerProduit(  
    produit: Produit  
) {  

    val prix =  
        produit.prix  
            .toIntOrNull()  
            ?: 0  

    val textePartage =  
        """  
        🛍️ VIE ESPOIR MARKETING  
          
        🏢 ${produit.entreprise.ifEmpty {  
            "Boutique"  
        }}  
          
        ${produit.nom}  
          
        💰 Prix :  
        ${formaterNombre(prix)} FCFA  
          
        🏷️ ${produit.categorie}  
          
        ${produit.description}  
          
        📲 Ouvrez Vie Espoir Marketing pour voir le produit et commander.  
          
        📞 $ORANGE_WAVE  
        """.trimIndent()  

    val intent =  
        Intent(  
            Intent.ACTION_SEND  
        ).apply {  

            type =  
                "text/plain"  

            putExtra(  
                Intent.EXTRA_TEXT,  
                textePartage  
            )  
        }  

    startActivity(  
        Intent.createChooser(  
            intent,  
            "Partager le produit"  
        )  
    )  
}  

// ========================================================  
// PARTAGE APPLICATION  
// ========================================================  

private fun partagerApplication() {  

    val textePartage =  
        """  
        🦁 VIE ESPOIR MARKETING  
          
        Découvrez notre plateforme de vente.  
          
        🏢 Entreprises  
        🛍️ Produits  
        🛒 Commandes  
        💳 Paiements  
        🚚 Livraison  
          
        Téléchargez Vie Espoir Marketing  
        pour découvrir les produits et commander.  
        """.trimIndent()  

    val intent =  
        Intent(  
            Intent.ACTION_SEND  
        ).apply {  

            type =  
                "text/plain"  

            putExtra(  
                Intent.EXTRA_TEXT,  
                textePartage  
            )  
        }  

    startActivity(  
        Intent.createChooser(  
            intent,  
            "Partager l'application"  
        )  
    )  
}  

// ========================================================  
// BOUTON RETOUR DU TÉLÉPHONE  
// ========================================================  

@Suppress("DEPRECATION")  
override fun onBackPressed() {  

    /*  
     * NOUVELLE NAVIGATION :  
     *  
     * 1. Si un écran précédent existe :  
     *    on revient à cet écran.  
     *  
     * 2. Si nous sommes déjà sur l'accueil :  
     *    on demande confirmation avant de quitter.  
     */  

    if (  
        afficherEcranPrecedent()  
    ) {  
        return  
    }  

    AlertDialog.Builder(this)  
        .setTitle(  
            "🚪 Quitter l'application"  
        )  
        .setMessage(  
            "Voulez-vous vraiment quitter Vie Espoir Marketing ?"  
        )  
        .setNegativeButton(  
            "Annuler",  
            null  
        )  
        .setPositiveButton(  
            "Quitter"  
        ) { _, _ ->  

            finishAffinity()  
        }  
        .show()  
}  

// ========================================================  
// QUITTER  
// ========================================================  

private fun confirmerQuitter() {  

    AlertDialog.Builder(this)  
        .setTitle(  
            "🚪 Quitter"  
        )  
        .setMessage(  
            "Voulez-vous vraiment quitter Vie Espoir Marketing ?"  
        )  
        .setNegativeButton(  
            "Annuler",  
            null  
        )  
        .setPositiveButton(  
            "Quitter"  
        ) { _, _ ->  

            finishAffinity()  
        }  
        .show()  
}  

// ========================================================  
// COMPTE ACTUEL  
// ========================================================  

private fun compteActuel(): Compte? {  

    if (  
        compteConnecteId.isEmpty()  
    ) {  
        return null  
    }  

    return comptes.find {  
        it.id ==  
            compteConnecteId  
    }  
}  

// ========================================================  
// 7 JOURS GRATUITS  
// ========================================================  

private fun joursGratuitsRestants(  
    compte: Compte  
): Long {  

    val septJours =  
        7L *  
        24L *  
        60L *  
        60L *  
        1000L  

    val fin =  
        compte.dateCreation +  
        septJours  

    val restant =  
        fin -  
        System.currentTimeMillis()  

    if (restant <= 0) {  
        return 0  
    }  

    return (  
        restant /  
        (  
            24L *  
            60L *  
            60L *  
            1000L  
        )  
    ) + 1  
}  

private fun vendeurPeutUtiliser(): Boolean {  

    val compte =  
        compteActuel()  
            ?: return false  

    if (!compte.valide) {  
        return false  
    }  

    /*  
     * Les 7 jours gratuits sont conservés.  
     * Après les 7 jours, l'écran abonnement est proposé.  
     */  
    return joursGratuitsRestants(  
        compte  
    ) > 0  
}  

// ========================================================  
// EVENEMENTS ADMIN  
// ========================================================  

private fun enregistrerEvenement(  
    evenement: String  
) {  

    val ligne =  
        "${System.currentTimeMillis()} — $evenement"  

    evenementsAdmin.add(  
        ligne  
    )  

    prefs.edit()  
        .putString(  
            EVENTS_KEY,  
            JSONArray(  
                evenementsAdmin  
            ).toString()  
        )  
        .apply()  
}  

// ========================================================  
// FORMATAGE  
// ========================================================  

private fun formaterNombre(  
    nombre: Int  
): String {  

    return String.format(  
        Locale.FRANCE,  
        "%,d",  
        nombre  
    ).replace(  
        ',',  
        ' '  
    )  
}  

// ========================================================  
// SAUVEGARDE PRODUITS  
// ========================================================  

private fun sauvegarderProduits() {  

    val tableau =  
        JSONArray()  

    for (produit in produits) {  

        val images =  
            JSONArray()  

        for (  
            image in produit.images  
        ) {  

            images.put(  
                image  
            )  
        }  

        tableau.put(  
            JSONObject().apply {  

                put(  
                    "id",  
                    produit.id  
                )  

                put(  
                    "nom",  
                    produit.nom  
                )  

                put(  
                    "prix",  
                    produit.prix  
                )  

                put(  
                    "categorie",  
                    produit.categorie  
                )  

                put(  
                    "stock",  
                    produit.stock  
                )  

                put(  
                    "description",  
                    produit.description  
                )  

                put(  
                    "images",  
                    images  
                )  

                put(  
                    "vendeur",  
                    produit.vendeur  
                )  

                put(  
                    "entreprise",  
                    produit.entreprise  
                )  
            }  
        )  
    }  

    prefs.edit()  
        .putString(  
            PRODUCTS_KEY,  
            tableau.toString()  
        )  
        .apply()  
}  

// ========================================================  
// SAUVEGARDE PANIER  
// ========================================================  

private fun sauvegarderPanier() {  

    val tableau =  
        JSONArray()  

    for (item in panier) {  

        val images =  
            JSONArray()  

        for (  
            image in item.images  
        ) {  

            images.put(  
                image  
            )  
        }  

        tableau.put(  
            JSONObject().apply {  

                put(  
                    "produitId",  
                    item.produitId  
                )  

                put(  
                    "nom",  
                    item.nom  
                )  

                put(  
                    "prix",  
                    item.prix  
                )  

                put(  
                    "quantite",  
                    item.quantite  
                )  

                put(  
                    "images",  
                    images  
                )  
            }  
        )  
    }  

    prefs.edit()  
        .putString(  
            CART_KEY,  
            tableau.toString()  
        )  
        .apply()  
}  

// ========================================================  
// SAUVEGARDE COMMANDES  
// ========================================================  

private fun sauvegarderCommandes() {  

    val tableau =  
        JSONArray()  

    for (  
        commande in commandes  
    ) {  

        tableau.put(  
            JSONObject().apply {  

                put(  
                    "id",  
                    commande.id  
                )  

                put(  
                    "client",  
                    commande.client  
                )  

                put(  
                    "telephone",  
                    commande.telephone  
                )  

                put(  
                    "produit",  
                    commande.produit  
                )  

                put(  
                    "montant",  
                    commande.montant  
                )  

                put(  
                    "paiement",  
                    commande.paiement  
                )  

                put(  
                    "statut",  
                    commande.statut  
                )  
            }  
        )  
    }  

    prefs.edit()  
        .putString(  
            ORDERS_KEY,  
            tableau.toString()  
        )  
        .apply()  
}  

// ========================================================  
// SAUVEGARDE COMPTES  
// ========================================================  

private fun sauvegarderComptes() {  

    val tableau =  
        JSONArray()  

    for (  
        compte in comptes  
    ) {  

        tableau.put(  
            JSONObject().apply {  

                put(  
                    "id",  
                    compte.id  
                )  

                put(  
                    "nom",  
                    compte.nom  
                )  

                put(  
                    "entreprise",  
                    compte.entreprise  
                )  

                put(  
                    "email",  
                    compte.email  
                )  

                put(  
                    "telephone",  
                    compte.telephone  
                )  

                put(  
                    "adresse",  
                    compte.adresse  
                )  

                put(  
                    "motDePasse",  
                    compte.motDePasse  
                )  

                put(  
                    "type",  
                    compte.type  
                )  

                put(  
                    "dateCreation",  
                    compte.dateCreation  
                )  

                put(  
                    "valide",  
                    compte.valide  
                )  
            }  
        )  
    }  

    prefs.edit()  
        .putString(  
            ACCOUNTS_KEY,  
            tableau.toString()  
        )  
        .apply()  
}  

// ========================================================  
// CHARGEMENT DES DONNÉES  
// ========================================================  

private fun chargerDonnees() {  

    produits.clear()  
    panier.clear()  
    commandes.clear()  
    comptes.clear()  
    evenementsAdmin.clear()  

    // ====================================================  
    // PRODUITS  
    // ====================================================  

    val produitsJson =  
        prefs.getString(  
            PRODUCTS_KEY,  
            null  
        )  

    if (  
        !produitsJson.isNullOrEmpty()  
    ) {  

        try {  

            val tableau =  
                JSONArray(  
                    produitsJson  
                )  

            for (  
                i in 0 until tableau.length()  
            ) {  

                val objet =  
                    tableau  
                        .getJSONObject(i)  

                val images =  
                    mutableListOf<String>()  

                val imagesJson =  
                    objet.optJSONArray(  
                        "images"  
                    )  

                if (  
                    imagesJson != null  
                ) {  

                    for (  
                        j in 0 until  
                            imagesJson.length()  
                    ) {  

                        val image =  
                            imagesJson  
                                .optString(  
                                    j  
                                )  

                        if (  
                            image.isNotEmpty()  
                        ) {  

                            images.add(  
                                image  
                            )  
                        }  
                    }  
                }  

                /*  
                 * COMPATIBILITÉ ANCIEN FORMAT  
                 */  

                if (  
                    images.isEmpty()  
                ) {  

                    val ancienneImage =  
                        objet.optString(  
                            "image",  
                            ""  
                        )  

                    if (  
                        ancienneImage.isNotEmpty()  
                    ) {  

                        images.add(  
                            ancienneImage  
                        )  
                    }  
                }  

                val id =  
                    objet.optString(  
                        "id",  
                        System.currentTimeMillis()  
                            .toString()  
                    )  

                produits.add(  
                    Produit(  
                        id =  
                            id,  

                        nom =  
                            objet.optString(  
                                "nom",  
                                ""  
                            ),  

                        prix =  
                            objet.optString(  
                                "prix",  
                                "0"  
                            ),  

                        categorie =  
                            objet.optString(  
                                "categorie",  
                                "Autres"  
                            ),  

                        stock =  
                            objet.optInt(  
                                "stock",  
                                0  
                            ),  

                        description =  
                            objet.optString(  
                                "description",  
                                ""  
                            ),  

                        images =  
                            images,  

                        vendeur =  
                            objet.optString(  
                                "vendeur",  
                                ""  
                            ),  

                        entreprise =  
                            objet.optString(  
                                "entreprise",  
                                ""  
                            )  
                    )  
                )  
            }  

        } catch (_: Exception) {  
        }  
    }  

    // ====================================================  
    // PANIER  
    // ====================================================  

    val panierJson =  
        prefs.getString(  
            CART_KEY,  
            null  
        )  

    if (  
        !panierJson.isNullOrEmpty()  
    ) {  

        try {  

            val tableau =  
                JSONArray(  
                    panierJson  
                )  

            for (  
                i in 0 until tableau.length()  
            ) {  

                val objet =  
                    tableau  
                        .getJSONObject(i)  

                val images =  
                    mutableListOf<String>()  

                val imagesJson =  
                    objet.optJSONArray(  
                        "images"  
                    )  

                if (  
                    imagesJson != null  
                ) {  

                    for (  
                        j in 0 until  
                            imagesJson.length()  
                    ) {  

                        images.add(  
                            imagesJson.optString(  
                                j  
                            )  
                        )  
                    }  
                }  

                panier.add(  
                    PanierItem(  
                        produitId =  
                            objet.optString(  
                                "produitId"  
                            ),  

                        nom =  
                            objet.optString(  
                                "nom"  
                            ),  

                        prix =  
                            objet.optInt(  
                                "prix",  
                                0  
                            ),  

                        quantite =  
                            objet.optInt(  
                                "quantite",  
                                1  
                            ),  

                        images =  
                            images  
                    )  
                )  
            }  

        } catch (_: Exception) {  
        }  
    }  

    // ====================================================  
    // COMMANDES  
    // ====================================================  

    val commandesJson =  
        prefs.getString(  
            ORDERS_KEY,  
            null  
        )  

    if (  
        !commandesJson.isNullOrEmpty()  
    ) {  

        try {  

            val tableau =  
                JSONArray(  
                    commandesJson  
                )  

            for (  
                i in 0 until  
                    tableau.length()  
            ) {  

                val objet =  
                    tableau  
                        .getJSONObject(i)  

                commandes.add(  
                    Commande(  
                        id =  
                            objet.optString(  
                                "id"  
                            ),  

                        client =  
                            objet.optString(  
                                "client"  
                            ),  

                        telephone =  
                            objet.optString(  
                                "telephone"  
                            ),  

                        produit =  
                            objet.optString(  
                                "produit"  
                            ),  

                        montant =  
                            objet.optString(  
                                "montant"  
                            ),  

                        paiement =  
                            objet.optString(  
                                "paiement",  
                                "Non défini"  
                            ),  

                        statut =  
                            objet.optString(  
                                "statut",  
                                "Nouvelle"  
                            )  
                    )  
                )  
            }  

        } catch (_: Exception) {  
        }  
    }  

    // ====================================================  
    // COMPTES  
    // ====================================================  

    val comptesJson =  
        prefs.getString(  
            ACCOUNTS_KEY,  
            null  
        )  

    if (  
        !comptesJson.isNullOrEmpty()  
    ) {  

        try {  

            val tableau =  
                JSONArray(  
                    comptesJson  
                )  

            for (  
                i in 0 until  
                    tableau.length()  
            ) {  

                val objet =  
                    tableau  
                        .getJSONObject(i)  

                comptes.add(  
                    Compte(  
                        id =  
                            objet.optString(  
                                "id"  
                            ),  

                        nom =  
                            objet.optString(  
                                "nom"  
                            ),  

                        entreprise =  
                            objet.optString(  
                                "entreprise"  
                            ),  

                        email =  
                            objet.optString(  
                                "email"  
                            ),  

                        telephone =  
                            objet.optString(  
                                "telephone"  
                            ),  

                        adresse =  
                            objet.optString(  
                                "adresse"  
                            ),  

                        motDePasse =  
                            objet.optString(  
                                "motDePasse"  
                            ),  

                        type =  
                            objet.optString(  
                                "type",  
                                "VENDEUR"  
                            ),  

                        dateCreation =  
                            objet.optLong(  
                                "dateCreation",  
                                System.currentTimeMillis()  
                            ),  

                        valide =  
                            objet.optBoolean(  
                                "valide",  
                                false  
                            )  
                    )  
                )  
            }  

        } catch (_: Exception) {  
        }  
    }  

    // ====================================================  
    // EVENEMENTS  
    // ====================================================  

    val eventsJson =  
        prefs.getString(  
            EVENTS_KEY,  
            null  
        )  

    if (  
        !eventsJson.isNullOrEmpty()  
    ) {  

        try {  

            val tableau =  
                JSONArray(  
                    eventsJson  
                )  

            for (  
                i in 0 until  
                    tableau.length()  
            ) {  

                evenementsAdmin.add(  
                    tableau.optString(  
                        i  
                    )  
                )  
            }  

        } catch (_: Exception) {  
        }  
    }  
}

}