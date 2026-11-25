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
import com.google.firebase.firestore.ListenerRegistration
import java.net.HttpURLConnection
import java.net.URL
import android.graphics.BitmapFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.content.ClipData
import android.text.TextUtils
import android.util.Log

// ========================================================
// VIE ESPOIR MARKETING
// ECRAN DE BIENVENUE OFFICIEL AVEC LOGO LION
// VERIFICATION INTERNET + OUVERTURE AUTOMATIQUE
// ========================================================

class SplashActivity : android.app.Activity() {

    private val handler =
        android.os.Handler(android.os.Looper.getMainLooper())

    private var verificationEnCours = false
    private var ouvertureEnCours = false
    private var ecranDetruit = false

    private lateinit var message: android.widget.TextView
    private lateinit var boutonReessayer: android.widget.Button

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        val fond = android.widget.FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.WHITE)
        }

        // ====================================================
        // IMAGE DE BIENVENUE : LION VERT ET DORE
        // ====================================================

        val imageBienvenue = android.widget.ImageView(this).apply {
            setImageResource(R.drawable.a_clean_app_splash_screen_welcome_screen_for_vie)
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
        }

        fond.addView(
            imageBienvenue,
            android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // ====================================================
        // ZONE DE CONNEXION INTERNET
        // ====================================================

        val zoneConnexion = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(dp(16), dp(12), dp(16), dp(16))
            setBackgroundColor(
                android.graphics.Color.argb(235, 255, 255, 255)
            )
        }

        val chargement =
            android.widget.ProgressBar(this).apply {
                isIndeterminate = true
                indeterminateTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.rgb(0, 125, 45)
                    )
            }

        zoneConnexion.addView(
            chargement,
            android.widget.LinearLayout.LayoutParams(
                dp(36),
                dp(36)
            )
        )

        message = android.widget.TextView(this).apply {
            text = "Vérification de la connexion Internet..."
            textSize = 15f
            gravity = android.view.Gravity.CENTER
            setTextColor(
                android.graphics.Color.rgb(0, 85, 35)
            )
            setPadding(0, dp(12), 0, dp(8))
        }

        zoneConnexion.addView(message)

        boutonReessayer = android.widget.Button(this).apply {
            text = "Réessayer"
            visibility = android.view.View.GONE
            setTextColor(android.graphics.Color.WHITE)
            backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.rgb(0, 125, 45)
                )

            setOnClickListener {
                visibility = android.view.View.GONE
                message.text =
                    "Vérification de la connexion Internet..."
                verifierInternet()
            }
        }

        zoneConnexion.addView(boutonReessayer)

        fond.addView(
            zoneConnexion,
            android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.BOTTOM
            ).apply {
                bottomMargin = dp(30)
            }
        )

        setContentView(fond)

        // ====================================================
        // AFFICHAGE DE BIENVENUE PENDANT 10 SECONDES
        // ====================================================

        handler.postDelayed({
            if (!ecranDetruit) {
                verifierInternet()
            }
        }, 10000)
    }

    // ========================================================
    // CONVERSION DP
    // ========================================================

    private fun dp(valeur: Int): Int {
        return (
            valeur * resources.displayMetrics.density
        ).toInt()
    }

    // ========================================================
    // VERIFICATION DE LA CONNEXION INTERNET
    // ========================================================

    private fun verifierInternet() {

        if (verificationEnCours || ouvertureEnCours || ecranDetruit) {
            return
        }

        verificationEnCours = true

        Thread {

            val connecte = try {

                val gestionnaire =
                    getSystemService(CONNECTIVITY_SERVICE) as
                        android.net.ConnectivityManager

                val reseau = gestionnaire.activeNetwork

                val capacites =
                    gestionnaire.getNetworkCapabilities(reseau)

                capacites != null &&
                    capacites.hasCapability(
                        android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
                    ) &&
                    capacites.hasCapability(
                        android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )

            } catch (_: Exception) {
                false
            }

            runOnUiThread {

                if (ecranDetruit || isFinishing) {
                    return@runOnUiThread
                }

                verificationEnCours = false

                if (connecte) {

                    if (ouvertureEnCours) {
                        return@runOnUiThread
                    }

                    ouvertureEnCours = true

                    message.text =
                        "Connexion réussie. Ouverture de l'application..."

                    boutonReessayer.visibility =
                        android.view.View.GONE

                    handler.postDelayed({

                        if (!ecranDetruit && !isFinishing) {

                            startActivity(
                                android.content.Intent(
                                    this,
                                    MainActivity::class.java
                                )
                            )

                            finish()
                        }

                    }, 500)

                } else {

                    message.text =
                        "Pas de connexion Internet. Active Internet puis réessaie."

                    boutonReessayer.visibility =
                        android.view.View.VISIBLE

                    handler.postDelayed({

                        if (!ecranDetruit && !ouvertureEnCours) {
                            verifierInternet()
                        }

                    }, 5000)
                }
            }

        }.start()
    }

    // ========================================================
    // FERMETURE PROPRE DE L'ECRAN
    // ========================================================

    override fun onDestroy() {
        ecranDetruit = true
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}

// ========================================================
// FIN ECRAN DE BIENVENUE VIE ESPOIR MARKETING
// ========================================================



// ============================================================
// APPLICATION PRINCIPALE
// ============================================================

class MainActivity : Activity() {
private val db = FirebaseFirestore.getInstance()
// ========================================================  
// MODÈLES  
// ========================================================  


// ========================================================
// VIE ESPOIR MARKETING
// PRODUIT AVEC IDENTIFIANT UNIQUE DU VENDEUR
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
    var entreprise: String = "",

    // Identifiant Firebase du propriétaire
    var vendeurId: String = "",

    // Visibilité du produit dans la boutique
    var actif: Boolean = true
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
    var statut: String = "Nouvelle",
    val vendeurId: String = ""
)

// ========================================================
// ========================================================
// VIE ESPOIR MARKETING
//
// CLASSE COMPTE CLIENT ET VENDEUR
// PROFIL UTILISATEUR FIREBASE
// INFORMATIONS PERSONNELLES
//
// ========================================================
// =======================================================

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
    var valide: Boolean = false,

    // =================================
    // ABONNEMENT VENDEUR
    // =================================

    var abonnementActif: Boolean = false,
    var dateDebutAbonnement: Long = 0L,
    var dateFinAbonnement: Long = 0L,
    var formuleAbonnement: Int = 0
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

private var dernierAppuiRetour = 0L

override fun onBackPressed() {

    // Revenir à l'écran précédent
    if (afficherEcranPrecedent()) {
        dernierAppuiRetour = 0L
        return
    }

    // À l'accueil général : double appui pour quitter
    val maintenant = android.os.SystemClock.uptimeMillis()

    if (maintenant - dernierAppuiRetour < 2000) {
        finish()
    } else {
        dernierAppuiRetour = maintenant

        android.widget.Toast.makeText(
            this,
            "Appuyez encore une fois pour quitter",
            android.widget.Toast.LENGTH_SHORT
        ).show()
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
// PRODUIT PARTAGE EN ATTENTE
// VIE ESPOIR MARKETING
// ========================================================
private var produitIdEnAttente: String? = null
// ========================================================
// DEMARRAGE DE L'APPLICATION
// OUVERTURE DES LIENS PRODUITS
// VIE ESPOIR MARKETING
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

    // Recuperer l'identifiant du produit partage
    produitIdEnAttente =
        intent?.data
            ?.takeIf {
                it.scheme == "vieespoir" &&
                it.host == "produit"
            }
            ?.lastPathSegment

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

    val densite = resources.displayMetrics.density

    fun dp(v: Int): Int =
        (v * densite).toInt()

    // ========================================================
    // 1 - EN-TETE PROFESSIONNEL VIE ESPOIR MARKETING
    // ========================================================

    val header = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(10), dp(10), dp(10), dp(10))
        setBackgroundColor(VERT)
    }

    // Utilise l'icône officielle déjà installée
    // dans le projet Android.

    val logo = ImageView(this).apply {

        setImageResource(
            applicationInfo.icon
        )

        scaleType = ImageView.ScaleType.FIT_CENTER

        layoutParams = LinearLayout.LayoutParams(
            dp(65),
            dp(65)
        )
    }

    val nom = TextView(this).apply {

        text = "VIE ESPOIR\nMARKETING"
        textSize = 19f
        setTextColor(Color.WHITE)
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER_VERTICAL

        layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            marginStart = dp(8)
        }
    }

    val compteBouton = TextView(this).apply {

        text = "👤"
        textSize = 27f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        setPadding(dp(5), dp(5), dp(5), dp(5))

        setOnClickListener {

            if (compteActuel() != null) {
                afficherMenu()
            } else {
                afficherConnexion()
            }
        }
    }

    val panierBouton = TextView(this).apply {

        text = "🛒"
        textSize = 27f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        setPadding(dp(5), dp(5), dp(5), dp(5))

        setOnClickListener {
            afficherPanier()
        }
    }

    val menu = TextView(this).apply {

        text = "☰"
        textSize = 27f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        setPadding(dp(5), dp(5), dp(5), dp(5))

        setOnClickListener {
            afficherMenu()
        }
    }

    header.addView(menu)
    header.addView(logo)
    header.addView(nom)
    header.addView(compteBouton)
    header.addView(panierBouton)

    root.addView(header)

    // ========================================================
    // 2 - MESSAGE DE BIENVENUE
    // ========================================================

    root.addView(
        texte(
            "Ensemble pour un meilleur avenir",
            14f,
            VERT
        ).apply {
            gravity = Gravity.CENTER
            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.ITALIC
            )
        }
    )

    if (compteConnecteId.isNotEmpty()) {

        val compte = compteActuel()

        if (compte != null) {

            root.addView(
                texte(
                    "👋 Bienvenue",
                    17f,
                    VERT
                ).apply {
                    gravity = Gravity.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                }
            )
        }
    }

    // ========================================================
    // 3 - BARRE DE RECHERCHE
    // ========================================================

    val recherche = EditText(this).apply {

        hint = "🔎 Rechercher un produit..."
        textSize = 16f
        setSingleLine(true)

        setPadding(
            dp(14),
            dp(12),
            dp(14),
            dp(12)
        )

        setBackgroundColor(Color.WHITE)
    }

    root.addView(recherche)

    root.addView(
        boutonVert("🔎 Rechercher") {

            afficherProduits(
                recherche.text
                    .toString()
                    .trim()
            )
        }
    )

    // ========================================================
    // 4 - PRODUITS DE LA BOUTIQUE
    // ========================================================

    root.addView(
        titre("🛍️ Nos produits")
    )

    zoneProduits = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    root.addView(zoneProduits)

    // ========================================================
    // 5 - ACCES AU PANIER
    // ========================================================

    root.addView(
        bouton(
            "🛒 Mon panier (${panier.size})"
        ) {
            afficherPanier()
        }
    )

    // ========================================================
    // 6 - PIED DE PAGE
    // ========================================================

    root.addView(
        texte(
            "VIE ESPOIR MARKETING\nProduits • Commandes • Vente • Entreprises",
            13f,
            Color.GRAY
        ).apply {

            gravity = Gravity.CENTER

            setPadding(
                dp(8),
                dp(20),
                dp(8),
                dp(20)
            )
        }
    )

    // ========================================================
    // 7 - AFFICHAGE DE LA PAGE
    // ========================================================

    viderHistoriqueEtAfficher(
        scroll(root)
    )

    // Conserve les deux colonnes existantes
    afficherProduitsAccueil()
}

// ========================================================
// FIN - PAGE D'ACCUEIL VIE ESPOIR MARKETING
// ========================================================


// ========================================================  
// PRODUITS ACCUEIL  
//
// ========================================================
// CHARGEMENT AUTOMATIQUE DES PRODUITS FIREBASE
// VIE ESPOIR MARKETING
// ========================================================

private var ecouteurProduits: ListenerRegistration? = null

private fun chargerProduitsFirebase() {

    ecouteurProduits?.remove()
    ecouteurProduits = null

    android.util.Log.d(
        "FIREBASE_PRODUITS",
        "Démarrage de la synchronisation automatique"
    )

    ecouteurProduits = db.collection("Produits")
        .addSnapshotListener { result, erreur ->

            if (erreur != null) {

                android.util.Log.e(
                    "FIREBASE_PRODUITS",
                    "Erreur de synchronisation",
                    erreur
                )

                toast(
                    "Erreur Firebase : ${erreur.localizedMessage}"
                )

                return@addSnapshotListener
            }

            if (result == null) return@addSnapshotListener

            val produitsRecus = ArrayList<Produit>()

            for (document in result.documents) {

                val donnees = document.data ?: continue

                val nom = (
                    donnees["nom"]
                        ?: donnees["Nom"]
                        ?: donnees["name"]
                )?.toString()?.trim().orEmpty()

                if (nom.isEmpty()) continue

                val prixValeur =
                    donnees["prix"] ?: donnees["Prix"]

                val prix = when (prixValeur) {
                    is Number -> prixValeur.toInt().toString()
                    is String -> prixValeur
                    else -> "0"
                }

                val categorie = (
                    donnees["categorie"]
                        ?: donnees["category"]
                )?.toString() ?: "Autres produits"

                val stock = when (
                    val valeur = donnees["stock"]
                ) {
                    is Number -> valeur.toInt()
                    is String -> valeur.toIntOrNull() ?: 0
                    else -> 0
                }

                // PHOTOS
                val images = mutableListOf<String>()

                val imagesFirebase = donnees["images"]

                if (imagesFirebase is List<*>) {
                    for (image in imagesFirebase) {
                        if (image is String && image.isNotBlank()) {
                            images.add(image)
                        }
                    }
                }

                val ancienneImage =
                    donnees["image"]?.toString()

                if (
                    images.isEmpty() &&
                    !ancienneImage.isNullOrBlank()
                ) {
                    images.add(ancienneImage)
                }

                val description =
                    donnees["description"]?.toString() ?: ""

                val vendeur =
                    donnees["vendeur"]?.toString() ?: ""

                val entreprise =
                    donnees["entreprise"]?.toString() ?: ""

                // IDENTIFIANT DU VENDEUR
                val vendeurId =
                    donnees["vendeurId"]?.toString() ?: ""

                // VISIBILITÉ DU PRODUIT
                // Les anciens produits sans champ actif
                // restent visibles par défaut.
                val actif = when (val valeur = donnees["actif"]) {
                    is Boolean -> valeur
                    is String -> valeur.toBooleanStrictOrNull() ?: true
                    else -> true
                }

                val produit = Produit(
                    id = donnees["id"]?.toString()
                        ?.takeIf { it.isNotBlank() }
                        ?: document.id,

                    nom = nom,
                    prix = prix,
                    categorie = categorie,
                    stock = stock,
                    description = description,
                    images = images,
                    vendeur = vendeur,
                    entreprise = entreprise,
                    vendeurId = vendeurId,
                    actif = actif
                )

                produitsRecus.add(produit)
            }

            // ACTUALISER LES PRODUITS
            produits.clear()
            produits.addAll(produitsRecus)

            android.util.Log.d(
                "FIREBASE_PRODUITS",
                "Produits synchronisés : ${produits.size}"
            )

            // ACTUALISER L'ACCUEIL
            if (
                ::zoneProduits.isInitialized &&
                zoneProduits.isAttachedToWindow
            ) {
                afficherProduitsAccueil()
            }

            // OUVRIR UN PRODUIT PARTAGÉ
            val idAouvrir = produitIdEnAttente

            if (!idAouvrir.isNullOrBlank()) {

                val produitTrouve = produits.find {
                    it.id == idAouvrir
                }

                if (produitTrouve != null) {

                    produitIdEnAttente = null

                    if (produitTrouve.actif) {
                        afficherDetailProduit(produitTrouve)
                    } else {
                        toast("Ce produit n'est plus disponible.")
                    }

                } else {

                    produitIdEnAttente = null

                    toast("Ce produit est introuvable.")
                }
            }
        }
}


override fun onDestroy() {
    ecouteurProduits?.remove()
    ecouteurProduits = null
    super.onDestroy()
}
 // ========================================================
 // AFFICHER LES PRODUITS SUR L'ACCUEIL
 // Présentation en deux colonnes, style boutique en ligne
 // ========================================================


// ========================================================
// VIE ESPOIR MARKETING
// CARROUSEL AUTOMATIQUE - 30 SECONDES
// ========================================================

private val accueilCarrouselHandler =
    Handler(Looper.getMainLooper())

private var accueilCarrouselRunnable: Runnable? = null

private fun arreterCarrouselAccueil() {
    accueilCarrouselRunnable?.let {
        accueilCarrouselHandler.removeCallbacks(it)
    }
    accueilCarrouselRunnable = null
}

private fun ajouterCarrouselAccueil(
    parent: LinearLayout
) {
    arreterCarrouselAccueil()

    if (produits.isEmpty()) return

    val densite = resources.displayMetrics.density

    fun dp(v: Int): Int =
        (v * densite).toInt()

    parent.addView(
        titre("✨ Découvrez nos produits")
    )

    val defilement = HorizontalScrollView(this).apply {
        isHorizontalScrollBarEnabled = false
        isFillViewport = false
    }

    val ligne = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(dp(4), dp(8), dp(4), dp(12))
    }

    defilement.addView(ligne)
    parent.addView(defilement)

    produits.forEach { produit ->

        val conteneur = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            layoutParams = LinearLayout.LayoutParams(
                dp(170),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dp(10)
            }
        }

        ligne.addView(conteneur)

        ajouterCarteProduit(
            parent = conteneur,
            produit = produit,
            compact = true
        )
    }

    if (produits.size <= 1) return

    val largeurEtape = dp(180)
    var position = 0

    val animation = object : Runnable {

        override fun run() {

            if (!defilement.isAttachedToWindow) {
                return
            }

            val maximum = (
                ligne.width - defilement.width
            ).coerceAtLeast(0)

            if (maximum > 0) {

                position += largeurEtape

                if (position > maximum) {
                    position = 0
                }

                defilement.smoothScrollTo(
                    position,
                    0
                )
            }

            accueilCarrouselHandler.postDelayed(
    this,
    5_000L
)
        }
    }

    accueilCarrouselRunnable = animation

accueilCarrouselHandler.postDelayed(
    animation,
   5_000L
)
}

// ========================================================
// FIN CARROUSEL AUTOMATIQUE
// ========================================================

private fun afficherProduitsAccueil() {

    if (!::zoneProduits.isInitialized) return

    zoneProduits.removeAllViews()

    val densite = resources.displayMetrics.density

    fun dp(valeur: Int): Int =
        (valeur * densite).toInt()

    zoneProduits.setPadding(
        dp(10), dp(4), dp(10), dp(16)
    )

    zoneProduits.clipToPadding = false

    // Afficher uniquement les produits actifs
    val produitsDisponibles = produits.filter {
        it.actif
    }

    if (produitsDisponibles.isEmpty()) {

        zoneProduits.addView(
            texte(
                "Aucun produit disponible pour le moment.",
                16f
            ).apply {
                gravity = Gravity.CENTER
                setPadding(
                    dp(12), dp(30), dp(12), dp(30)
                )
            }
        )

        return
    }

    // ========================================================
    // CARROUSEL AUTOMATIQUE
    // ========================================================

    ajouterCarrouselAccueil(zoneProduits)

    // ========================================================
    // REGROUPEMENT PAR CATEGORIE
    // ========================================================

    val groupes = produitsDisponibles.groupBy { produit ->

        produit.categorie.trim()
            .ifEmpty { "Autres produits" }
    }

    for ((categorie, listeProduits) in groupes) {

        val titreCategorie = TextView(this).apply {

            text = categorie.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase()
                else it.toString()
            }

            textSize = 20f
            setTextColor(VERT)
            typeface = Typeface.DEFAULT_BOLD

            setPadding(
                dp(4), dp(20), dp(4), dp(10)
            )
        }

        zoneProduits.addView(
            titreCategorie,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        // Deux produits par rangée
        for (paire in listeProduits.chunked(2)) {

            val rangeeProduits = LinearLayout(this).apply {

                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.TOP
                weightSum = 2f
            }

            for (produit in paire) {

                val carteConteneur = LinearLayout(this).apply {

                    orientation = LinearLayout.VERTICAL

                    setPadding(
                        dp(4), dp(4), dp(4), dp(8)
                    )
                }

                rangeeProduits.addView(
                    carteConteneur,
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                )

                // Carte client existante
                ajouterCarteProduit(
                    parent = carteConteneur,
                    produit = produit,
                    compact = true
                )
            }

            // Garder la deuxième colonne vide
            if (paire.size == 1) {

                rangeeProduits.addView(
                    View(this),
                    LinearLayout.LayoutParams(
                        0,
                        1,
                        1f
                    )
                )
            }

            zoneProduits.addView(
                rangeeProduits,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }
}

// ========================================================
// CARTE PRODUIT — STYLE JUMIA
// VIE ESPOIR MARKETING
// GALERIE PAR GLISSEMENT SANS FLECHES
// ========================================================

private fun ajouterCarteProduit(
    parent: LinearLayout,
    produit: Produit,
    compact: Boolean = false
) {
    val densite = resources.displayMetrics.density

    fun dp(v: Int): Int =
        (v * densite).toInt()

    val photos = produit.images.filter {
        it.isNotBlank()
    }

    // ========================================================
    // CARTE BLANCHE
    // ========================================================

    val carte = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL

        setPadding(
            dp(5), dp(5), dp(5), dp(7)
        )

        background =
            android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(9).toFloat()
                setStroke(dp(1), Color.LTGRAY)
            }

        elevation = dp(2).toFloat()

        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(dp(2), dp(2), dp(2), dp(3))
        }
    }

    // ========================================================
    // PHOTO PRINCIPALE - GRAND FORMAT
    // VIE ESPOIR MARKETING
    // ========================================================

    val image = ImageView(this).apply {

        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(if (compact) 245 else 300)
        )

        scaleType = ImageView.ScaleType.CENTER_CROP

        setBackgroundColor(Color.WHITE)

        contentDescription =
            "Photos de ${produit.nom}"
    }

    // ========================================================
    // AFFICHAGE DES PHOTOS DU PRODUIT
    // ========================================================

    var positionAccueil = 0

    fun afficherPhotoAccueil() {

        if (photos.isNotEmpty()) {

            image.setImageDrawable(null)

            chargerImageProduit(
                image,
                photos[positionAccueil]
            )

        } else {

            image.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }
    }

    afficherPhotoAccueil()
carte.addView(image)
    // ========================================================
    // GRANDE GALERIE SANS FLECHES
    // ========================================================


    fun ouvrirGalerie() {

        if (photos.isEmpty()) {
            toast("Aucune photo disponible")
            return
        }

        val dialogue =
            AlertDialog.Builder(this).create()

        val racine = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(10), dp(10), dp(10), dp(10)
            )
            setBackgroundColor(Color.WHITE)
        }

        val titre = texte(
            produit.nom,
            18f,
            VERT
        ).apply {
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(5), 0, dp(10))
        }

        val grandePhoto = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(320)
            )

            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(Color.WHITE)
            contentDescription = "Galerie de ${produit.nom}"
        }

        val compteur = texte(
            "",
            14f,
            Color.DKGRAY
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }

        var position = positionAccueil

        fun afficherPhoto() {
            compteur.text =
                "Photo ${position + 1} sur ${photos.size}"

            grandePhoto.setImageDrawable(null)

            chargerImageProduit(
                grandePhoto,
                photos[position]
            )
        }

        // ========================================================
        // GLISSEMENT DANS LA GRANDE GALERIE
        // ========================================================

        grandePhoto.setOnTouchListener(
            object : View.OnTouchListener {

                private var debutX = 0f
                private var debutY = 0f

                override fun onTouch(
                    vue: View,
                    evenement: android.view.MotionEvent
                ): Boolean {

                    when (evenement.actionMasked) {

                        android.view.MotionEvent.ACTION_DOWN -> {
                            debutX = evenement.x
                            debutY = evenement.y
                            return true
                        }

                        android.view.MotionEvent.ACTION_UP -> {
                            val dx = evenement.x - debutX
                            val dy = evenement.y - debutY

                            if (
                                kotlin.math.abs(dx) > dp(45) &&
                                kotlin.math.abs(dx) >
                                kotlin.math.abs(dy)
                            ) {
                                position = if (dx < 0) {
                                    (position + 1) % photos.size
                                } else {
                                    (position - 1 + photos.size) % photos.size
                                }

                                afficherPhoto()
                            }

                            return true
                        }

                        android.view.MotionEvent.ACTION_CANCEL -> {
                            return true
                        }
                    }

                    return true
                }
            }
        )

        // ========================================================
        // MINIATURES DANS LA GRANDE GALERIE
        // ========================================================

        val miniatures = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val defilement =
            HorizontalScrollView(this).apply {
                isHorizontalScrollBarEnabled = false
                addView(miniatures)
            }

        for ((index, chemin) in photos.withIndex()) {

            val miniature = ImageView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        dp(55),
                        dp(55)
                    ).apply {
                        setMargins(
                            dp(3), dp(3),
                            dp(3), dp(3)
                        )
                    }

                scaleType =
                    ImageView.ScaleType.CENTER_CROP

                setBackgroundColor(Color.LTGRAY)

                contentDescription =
                    "Photo ${index + 1}"
            }

            chargerImageProduit(
                miniature,
                chemin
            )

            miniature.setOnClickListener {
                position = index
                afficherPhoto()
            }

            miniatures.addView(miniature)
        }

        // ========================================================
        // CONTENU DE LA GALERIE
        // ========================================================

        racine.addView(titre)
        racine.addView(grandePhoto)
        racine.addView(compteur)
        racine.addView(defilement)

        dialogue.setView(racine)

        afficherPhoto()

        dialogue.show()

        dialogue.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // ========================================================
    // GLISSEMENT DIRECTEMENT SUR L'ACCUEIL
    // ========================================================

    image.setOnTouchListener(
        object : View.OnTouchListener {

            private var debutX = 0f
            private var debutY = 0f

            override fun onTouch(
                vue: View,
                evenement: android.view.MotionEvent
            ): Boolean {

                when (evenement.actionMasked) {

                    android.view.MotionEvent.ACTION_DOWN -> {
                        debutX = evenement.x
                        debutY = evenement.y
                        return true
                    }

                    android.view.MotionEvent.ACTION_UP -> {

                        val dx = evenement.x - debutX
                        val dy = evenement.y - debutY

                        if (
                            photos.size > 1 &&
                            kotlin.math.abs(dx) > dp(45) &&
                            kotlin.math.abs(dx) >
                            kotlin.math.abs(dy)
                        ) {
                            positionAccueil = if (dx < 0) {
                                (positionAccueil + 1) % photos.size
                            } else {
                                (positionAccueil - 1 + photos.size) % photos.size
                            }

                            afficherPhotoAccueil()

                        } else if (
                            kotlin.math.abs(dx) < dp(15) &&
                            kotlin.math.abs(dy) < dp(15)
                        ) {
                            ouvrirGalerie()
                        }

                        return true
                    }

                    android.view.MotionEvent.ACTION_CANCEL -> {
                        return true
                    }
                }

                return true
            }
        }
    )





    // ========================================================
    // OUVRIR LES DETAILS DU PRODUIT
    // ========================================================

    carte.setOnClickListener {
        afficherDetailProduit(produit)
    }

    // ========================================================
    // NOM DU PRODUIT
    // ========================================================

    carte.addView(
        texte(
            produit.nom,
            14f,
            Color.BLACK
        ).apply {
            maxLines = 2

            ellipsize =
                android.text.TextUtils.TruncateAt.END

            setPadding(
                0, dp(5), 0, dp(3)
            )
        }
    )

    // ========================================================
    // PRIX DU PRODUIT
    // ========================================================

    val prix = produit.prix
        .replace(" ", "")
        .replace(",", "")
        .toIntOrNull() ?: 0

    carte.addView(
        texte(
            "${formaterNombre(prix)} FCFA",
            17f,
            Color.BLACK
        ).apply {
            typeface = Typeface.DEFAULT_BOLD

            setPadding(
                0, dp(3), 0, dp(4)
            )
        }
    )

    // ========================================================
    // STOCK
    // ========================================================

    carte.addView(
        texte(
            if (produit.stock > 0) {
                "📦 Stock : ${produit.stock}"
            } else {
                "Indisponible"
            },
            11f,
            if (produit.stock > 0) VERT else Color.RED
        )
    )

// ========================================================
// VIE ESPOIR MARKETING
// PETIT PANIER VERT - STYLE JUMIA
// PAGE D'ACCUEIL
// PHOTO GRANDE - PANIER DISCRET EN BAS A DROITE
// ========================================================

val actions = LinearLayout(this).apply {

    orientation = LinearLayout.HORIZONTAL

    gravity = Gravity.END or Gravity.CENTER_VERTICAL

    layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

// ========================================================
// PETIT BOUTON PANIER AVEC SIGNE PLUS
// ========================================================

val petitPanier = TextView(this).apply {

    text = "🛒+"

    textSize = 17f

    gravity = Gravity.CENTER

    setTextColor(VERT)

    background =
        android.graphics.drawable.GradientDrawable().apply {

            setColor(Color.rgb(232, 247, 235))

            cornerRadius = dp(9).toFloat()
        }

    contentDescription =
        "Ajouter ${produit.nom} au panier"

    setOnClickListener {

        if (produit.stock <= 0) {

            toast("Produit indisponible")

        } else {

            ajouterAuPanier(produit)
        }
    }
}

// ========================================================
// POSITION DU PANIER EN BAS A DROITE
// ========================================================

actions.addView(
    petitPanier,
    LinearLayout.LayoutParams(
        dp(45),
        dp(40)
    ).apply {

        setMargins(
            0,
            dp(4),
            dp(3),
            0
        )
    }
)

carte.addView(actions)

// ========================================================
// FIN PETIT PANIER - VIE ESPOIR MARKETING
// ========================================================



    // ========================================================
    // GESTION DU VENDEUR
    // ========================================================

    val compte = compteActuel()

    if (
    compte != null &&
    compte.type == "VENDEUR" &&
    compte.valide &&
    vendeurPeutUtiliser()
) {
       
        val appartient =
            produit.vendeur == compte.nom ||
            produit.entreprise == compte.entreprise

        if (appartient) {

            carte.addView(
                bouton("✏️ Gérer") {
                    afficherModifierProduit(produit)
                }
            )
        }
    }

    // ========================================================
    // AJOUTER LA CARTE A L'ACCUEIL
    // ========================================================

    parent.addView(carte)
}
 

// ========================================================
// FORMATER LES PRIX
// ========================================================

private fun formaterNombre(nombre: Int): String {
    return "%,d".format(java.util.Locale.FRANCE, nombre)
        .replace(',', ' ')
}

 // ========================================================
// DETAIL PRODUIT - GALERIE DE PHOTOS
// VIE ESPOIR MARKETING
// ========================================================

private fun afficherDetailProduit(produit: Produit) {

    val root = layoutBase()

    val densite = resources.displayMetrics.density

    fun dp(valeur: Int): Int {
        return (valeur * densite).toInt()
    }

    root.addView(
        titre("🛍️ ${produit.nom}")
    )

    // ====================================================
    // RECUPERER TOUTES LES PHOTOS
    // ====================================================

    val photos = produit.images.filter {
        it.isNotBlank()
    }

    if (photos.isNotEmpty()) {

        var position = 0

        // GRANDE PHOTO
        val grandePhoto = ImageView(this).apply {

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(300)
            )

            scaleType = ImageView.ScaleType.FIT_CENTER

            setBackgroundColor(Color.WHITE)

            contentDescription = "Photo de ${produit.nom}"
        }

        // COMPTEUR DE PHOTOS
        val compteur = texte(
            "",
            15f,
            VERT
        ).apply {

            gravity = Gravity.CENTER

            typeface = Typeface.DEFAULT_BOLD
        }

        // AFFICHER LA PHOTO SELECTIONNEE
        fun afficherPhoto() {

            compteur.text =
                "📷 Photo ${position + 1} sur ${photos.size}"

            chargerImageProduit(
                grandePhoto,
                photos[position]
            )
        }

        root.addView(grandePhoto)

        root.addView(compteur)

        // ====================================================
        // BOUTONS PRECEDENTE ET SUIVANTE
        // ====================================================

        val navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val boutonPrecedent = bouton("◀ Précédente") {

            position =
                (position - 1 + photos.size) % photos.size

            afficherPhoto()
        }

        val boutonSuivant = bouton("Suivante ▶") {

            position =
                (position + 1) % photos.size

            afficherPhoto()
        }

        navigation.addView(
            boutonPrecedent,
            LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
            )
        )

        navigation.addView(
            boutonSuivant,
            LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
            )
        )

        root.addView(navigation)

        // ====================================================
        // MINIATURES DES PHOTOS
        // ====================================================

        root.addView(
            texte(
                "🖼️ Autres photos du produit",
                16f,
                VERT
            )
        )

        val miniatures = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL

            gravity = Gravity.CENTER_VERTICAL
        }

        for ((index, chemin) in photos.withIndex()) {

            val miniature = ImageView(this).apply {

                layoutParams = LinearLayout.LayoutParams(
                    dp(75),
                    dp(75)
                ).apply {

                    setMargins(
                        dp(4),
                        dp(4),
                        dp(4),
                        dp(4)
                    )
                }

                scaleType = ImageView.ScaleType.CENTER_CROP

                setBackgroundColor(Color.LTGRAY)

                contentDescription =
                    "Photo ${index + 1} de ${produit.nom}"
            }

            chargerImageProduit(
                miniature,
                chemin
            )

            miniature.setOnClickListener {

                position = index

                afficherPhoto()
            }

            miniatures.addView(miniature)
        }

        val defilement = HorizontalScrollView(this).apply {

            isHorizontalScrollBarEnabled = false

            addView(miniatures)
        }

        root.addView(defilement)

        // ====================================================
        // GLISSER LA PHOTO A GAUCHE OU A DROITE
        // ====================================================

        grandePhoto.setOnTouchListener(
            object : View.OnTouchListener {

                private var debutX = 0f
                private var debutY = 0f

                override fun onTouch(
                    vue: View,
                    evenement: android.view.MotionEvent
                ): Boolean {

                    when (evenement.action) {

                        android.view.MotionEvent.ACTION_DOWN -> {

                            debutX = evenement.x
                            debutY = evenement.y

                            return true
                        }

                        android.view.MotionEvent.ACTION_UP -> {

                            val differenceX =
                                evenement.x - debutX

                            val differenceY =
                                evenement.y - debutY

                            if (
                                kotlin.math.abs(differenceX) > dp(45) &&
                                kotlin.math.abs(differenceX) >
                                kotlin.math.abs(differenceY)
                            ) {

                                position = if (differenceX < 0) {

                                    (position + 1) % photos.size

                                } else {

                                    (position - 1 + photos.size) %
                                        photos.size
                                }

                                afficherPhoto()
                            }

                            return true
                        }
                    }

                    return true
                }
            }
        )

        // AFFICHER LA PREMIERE PHOTO
        afficherPhoto()

    } else {

        root.addView(
            texte(
                "📷 Aucune photo disponible pour ce produit.",
                16f,
                Color.GRAY
            )
        )
    }

    // ====================================================
    // PRIX
    // ====================================================

    val prix = produit.prix
        .replace(" ", "")
        .replace(",", "")
        .toIntOrNull() ?: 0

    root.addView(
        texte(
            "💰 ${formaterNombre(prix)} FCFA",
            21f,
            VERT
        ).apply {

            gravity = Gravity.CENTER

            typeface = Typeface.DEFAULT_BOLD
        }
    )

    // ====================================================
    // ENTREPRISE
    // ====================================================

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

            gravity = Gravity.CENTER
        }
    )

    // ====================================================
    // CATEGORIE
    // ====================================================

    root.addView(
        texte(
            "🏷️ ${produit.categorie}",
            15f,
            Color.DKGRAY
        )
    )

    // ====================================================
    // DESCRIPTION
    // ====================================================

    root.addView(
        texte(
            produit.description.ifEmpty {
                "Aucune description disponible."
            },
            16f
        )
    )

    // ====================================================
    // STOCK
    // ====================================================

    root.addView(
        texte(
            if (produit.stock > 0) {
                "📦 Stock disponible : ${produit.stock}"
            } else {
                "⚠️ Produit indisponible"
            },
            16f,
            if (produit.stock > 0) VERT else Color.RED
        )
    )

    // ====================================================
    // AJOUTER AU PANIER
    // ====================================================

    root.addView(
        boutonVert("🛒 Ajouter au panier") {

            if (produit.stock <= 0) {

                toast("Produit indisponible")

            } else {

                ajouterAuPanier(produit)
            }
        }
    )

    // ====================================================
    // PARTAGER LE PRODUIT
    // ====================================================

    root.addView(
        bouton("📤 Partager le produit") {

            partagerProduit(produit)
        }
    )

    // ====================================================
    // RETOUR
    // ====================================================

    root.addView(
        bouton("⬅️ Retour") {

            onBackPressed()
        }
    )

    // AFFICHER L'ECRAN
    setContentView(
        scroll(root)
    )
}
// ========================================================
// ========================================================
// OUVERTURE DIRECTE D'UN PRODUIT
// VIE ESPOIR MARKETING
// LIEN PARTAGE WHATSAPP - FACEBOOK
// ========================================================
// ========================================================

private fun ouvrirProduitParId(
    produitId: String
) {
    val produitTrouve = produits.find {
        it.id.toString() == produitId
    }

    if (produitTrouve != null) {

        afficherDetailProduit(produitTrouve)

    } else {

        toast(
            "Produit introuvable. Actualisez la boutique."
        )
    }
}
 
// ========================================================
// VIE ESPOIR MARKETING
// PANIER COMPLET
// PHOTOS + COMPTEUR + QUANTITES + COMMANDES
// ========================================================

// ========================================================
// 1. COMPTEUR DES ARTICLES DU PANIER
// ========================================================

private fun nombreArticlesPanier(): Int {

    return panier.sumOf { it.quantite }
}

// ========================================================
// 2. AJOUTER UN PRODUIT AU PANIER
// ========================================================

private fun ajouterAuPanier(
    produit: Produit
) {

    val existant = panier.find {
        it.produitId == produit.id
    }

    if (existant != null) {

        if (existant.quantite < produit.stock) {

            existant.quantite++

        } else {

            toast("Stock maximum atteint")
            return
        }

    } else {

        if (produit.stock <= 0) {
            toast("Produit indisponible")
            return
        }

        panier.add(
            PanierItem(
                produitId = produit.id,
                nom = produit.nom,
                prix = produit.prix
                    .replace(" ", "")
                    .toIntOrNull() ?: 0,
                quantite = 1,
                images = produit.images.toMutableList()
            )
        )
    }

    sauvegarderPanier()

    toast(
        "Produit ajouté au panier (${nombreArticlesPanier()})"
    )
}

// ========================================================
// 3. AFFICHER LE PANIER AVEC LES PHOTOS
// ========================================================

private fun afficherPanier() {

    val root = layoutBase()

    root.addView(
        titre(
            "🛒 Mon panier (${nombreArticlesPanier()})"
        )
    )

    // ====================================================
    // PANIER VIDE
    // ====================================================

    if (panier.isEmpty()) {

        root.addView(
            texte(
                "Votre panier est vide.",
                18f
            ).apply {

                gravity = Gravity.CENTER

                setPadding(
                    10, 40, 10, 40
                )
            }
        )

    } else {

        var total = 0

        // ================================================
        // PARCOURIR LES PRODUITS DU PANIER
        // ================================================

        for (item in panier) {

            val bloc = LinearLayout(this).apply {

                orientation = LinearLayout.VERTICAL

                setPadding(
                    12, 12, 12, 12
                )

                setBackgroundColor(Color.WHITE)

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {

                        setMargins(
                            0, 5, 0, 5
                        )
                    }
            }

            // ============================================
            // PHOTO REELLE DU PRODUIT
            // ============================================

            val imagePanier = ImageView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (180 * resources.displayMetrics.density).toInt()
                    )

                scaleType =
                    ImageView.ScaleType.FIT_CENTER

                setBackgroundColor(Color.WHITE)

                contentDescription =
                    "Photo de ${item.nom}"
            }

            val photoProduit =
                item.images.firstOrNull()

            if (!photoProduit.isNullOrBlank()) {

                chargerImageProduit(
                    imagePanier,
                    photoProduit
                )

            } else {

                imagePanier.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }

            bloc.addView(imagePanier)

            // ============================================
            // NOM DU PRODUIT
            // ============================================

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

            // ============================================
            // PRIX DU PRODUIT
            // ============================================

            bloc.addView(
                texte(
                    "${formaterNombre(item.prix)} FCFA",
                    16f
                )
            )

            // ============================================
            // QUANTITE DU PRODUIT
            // ============================================

            bloc.addView(
                texte(
                    "Quantité : ${item.quantite}",
                    16f
                )
            )

   
// ============================================
// SOUS-TOTAL DU PRODUIT
// VIE ESPOIR MARKETING - CORRIGE
// ============================================

val sousTotal =
    item.prix.toLong() *
    item.quantite

bloc.addView(
    texte(
        "Sous-total : ${formaterNombre(sousTotal.toInt())} FCFA",
        16f,
        VERT
    )
)


            // ============================================
            // BOUTON AJOUTER UNE QUANTITE
            // ============================================

            bloc.addView(
                bouton(
                    "➕ Ajouter 1"
                ) {

                    val produit =
                        produits.find {
                            it.id == item.produitId
                        }

                    if (
                        produit != null &&
                        item.quantite < produit.stock
                    ) {

                        item.quantite++

                        sauvegarderPanier()

                        afficherPanier()

                    } else {

                        toast(
                            "Stock maximum atteint ou produit indisponible"
                        )
                    }
                }
            )

            // ============================================
            // BOUTON DIMINUER UNE QUANTITE
            // ============================================

            bloc.addView(
                bouton(
                    "➖ Retirer 1"
                ) {

                    if (item.quantite > 1) {

                        item.quantite--

                    } else {

                        panier.remove(item)
                    }

                    sauvegarderPanier()

                    afficherPanier()
                }
            )

            // ============================================
            // AFFICHER LE BLOC DU PRODUIT
            // ============================================

            root.addView(bloc)

            total += (
                item.prix.toLong() *
                item.quantite
            ).toInt()
        }

        // ================================================
        // TOTAL GENERAL DU PANIER
        // ================================================

        root.addView(
            texte(
                "TOTAL : ${formaterNombre(total)} FCFA",
                22f,
                VERT
            ).apply {

                gravity = Gravity.CENTER

                typeface =
                    Typeface.DEFAULT_BOLD

                setPadding(
                    10, 25, 10, 25
                )
            }
        )

        // ================================================
        // CONTINUER LA COMMANDE
        // ================================================

        root.addView(
            boutonVert(
                "✅ Continuer la commande"
            ) {

                demanderInfosCommande(total)
            }
        )

        // ================================================
        // VIDER LE PANIER
        // ================================================

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

    // ====================================================
    // AFFICHAGE FINAL DU PANIER
    // ====================================================

    setContentView(
        scroll(root)
    )
}

// ========================================================
// FIN PANIER COMPLET
// VIE ESPOIR MARKETING
// ========================================================



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
 // SEPARATION DES COMMANDES PAR ENTREPRISE
 // ========================================================

private fun confirmerCommande(
    client: String,
    telephone: String,
    total: Int,
    paiement: String
) {

    val uidVieEspoir =
        "1kpZWN50IHhvz4y4OxS60MVjckx1"

    // Identifier chaque vendeur
    for (item in panier) {

        val produit = produits.find {
            it.id == item.produitId
        }

        if (produit == null) {
            toast("Produit introuvable : ${item.nom}")
            return
        }

        if (produit.vendeurId.isBlank()) {

            val entreprise = produit.entreprise.trim()

            if (
                entreprise.equals("Vie Espoir Marketing", true) ||
                entreprise.equals("Vie Espoir", true)
            ) {
                produit.vendeurId = uidVieEspoir
            } else {
                toast(
                    "Le vendeur de ${item.nom} n'est pas identifié."
                )
                return
            }
        }
    }

    // Regrouper les produits par vendeur
    val produitsParVendeur = panier.groupBy { item ->

        produits.find {
            it.id == item.produitId
        }!!.vendeurId
    }

    val produitsCommande = panier.joinToString(", ") {
        "${it.nom} x${it.quantite}"
    }

    AlertDialog.Builder(this)
        .setTitle("📋 Récapitulatif")
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
        .setNegativeButton("Modifier", null)
        .setPositiveButton("Confirmer la commande") { _, _ ->

            var numero = 0

            for ((vendeurId, articles) in produitsParVendeur) {

                val produitsDuVendeur =
                    articles.joinToString(", ") {
                        "${it.nom} x${it.quantite}"
                    }

                val montantVendeur =
                    articles.sumOf {
                        it.prix * it.quantite
                    }

                val commande = Commande(
                    id = "${System.currentTimeMillis()}_${numero++}",
                    client = client,
                    telephone = telephone,
                    produit = produitsDuVendeur,
                    montant = montantVendeur.toString(),
                    paiement = paiement,
                    statut = "Nouvelle",
                    vendeurId = vendeurId
                )

                commandes.add(commande)
            }

            // Déduction du stock
            for (item in panier) {

                val produit = produits.find {
                    it.id == item.produitId
                }

                if (produit != null) {
                    produit.stock =
                        (produit.stock - item.quantite)
                            .coerceAtLeast(0)
                }
            }

            sauvegarderProduits()
            sauvegarderCommandes()

            enregistrerEvenement(
                "Nouvelle commande de $client — ${formaterNombre(total)} FCFA"
            )

            panier.clear()
            sauvegarderPanier()

            toast("✅ Commande enregistrée")

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
// VIE ESPOIR MARKETING
// AJOUT PRODUIT - ADMINISTRATEUR ET VENDEUR
// VERSION CORRIGEE
// ========================================================

private fun afficherAjouterProduit() {

    val root = layoutBase()

    root.addView(
        titre("Ajouter un produit")
    )

    // ====================================================
    // NOM DU PRODUIT
    // ====================================================

    val champNom = EditText(this).apply {
        hint = "Nom du produit"
        inputType = InputType.TYPE_CLASS_TEXT
    }

    root.addView(champNom)

    // ====================================================
    // PRIX DU PRODUIT
    // ====================================================

    val champPrix = EditText(this).apply {
        hint = "Prix en F CFA"
        inputType = InputType.TYPE_CLASS_NUMBER
    }

    root.addView(champPrix)

    // ====================================================
    // CATEGORIE
    // ====================================================

    val champCategorie = EditText(this).apply {
        hint = "Catégorie du produit"
        inputType = InputType.TYPE_CLASS_TEXT
    }

    root.addView(champCategorie)

    // ====================================================
    // STOCK
    // ====================================================

    val champStock = EditText(this).apply {
        hint = "Quantité en stock"
        inputType = InputType.TYPE_CLASS_NUMBER
    }

    root.addView(champStock)

    // ====================================================
    // DESCRIPTION
    // ====================================================

    val champDescription = EditText(this).apply {
        hint = "Description du produit"
        minLines = 3
        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    root.addView(champDescription)

    // ====================================================
    // PHOTOS DU PRODUIT
    // ====================================================

    root.addView(
        boutonVert("Choisir les photos") {
            ouvrirGalerieMultiple()
        }
    )

    // ====================================================
    // BOUTON ENREGISTRER LE PRODUIT
    // ====================================================

    root.addView(
        boutonVert("Enregistrer le produit") {

            // ============================================
            // RECUPERATION DES INFORMATIONS
            // ============================================

            val nomProduit =
                champNom.text.toString().trim()

            // PRIX EN STRING : CORRECTION LONG / STRING
            val prixProduit =
                champPrix.text.toString().trim()

            val categorieProduit =
                champCategorie.text.toString().trim()

            val stockProduit =
                champStock.text.toString()
                    .trim()
                    .toIntOrNull()

            val descriptionProduit =
                champDescription.text.toString().trim()

            // ============================================
            // VERIFICATION DU NOM
            // ============================================

            if (nomProduit.isEmpty()) {
                toast("Saisissez le nom du produit")
                return@boutonVert
            }

            // ============================================
            // VERIFICATION DU PRIX
            // ============================================

            if ((prixProduit.toLongOrNull() ?: 0L) <= 0L) {
                toast("Saisissez un prix valide")
                return@boutonVert
            }

            // ============================================
            // VERIFICATION DU STOCK
            // ============================================

            if (stockProduit == null || stockProduit < 0) {
                toast("Saisissez un stock valide")
                return@boutonVert
            }

            // ============================================
            // IDENTIFICATION FIREBASE
            // ============================================

            val compteVendeur = compteActuel()

            val utilisateurFirebase =
                FirebaseAuth.getInstance().currentUser

            if (utilisateurFirebase == null) {
                toast("Veuillez vous connecter")
                afficherConnexion()
                return@boutonVert
            }

            val uidVendeur = utilisateurFirebase.uid

            // ============================================
            // ADMINISTRATEUR OU VENDEUR
            // ============================================

            val estAdmin = adminConnecte

            val nomVendeur = if (estAdmin) {
                "Administration Vie Espoir Marketing"
            } else {
                compteVendeur?.nom ?: ""
            }

            val nomEntreprise = if (estAdmin) {
                "Vie Espoir Marketing"
            } else {
                compteVendeur?.entreprise ?: ""
            }

            // ============================================
            // CREATION DU PRODUIT
            // ============================================

            val nouveauProduit = Produit(
                nom = nomProduit,
                prix = prixProduit,
                categorie = categorieProduit.ifEmpty {
                    "Autres"
                },
                stock = stockProduit,
                description = descriptionProduit,
                images = imagesProduit.toMutableList(),
                vendeur = nomVendeur,
                entreprise = nomEntreprise,
                vendeurId = uidVendeur
            )

            // ============================================
            // ENREGISTREMENT LOCAL
            // ============================================

            produits.add(nouveauProduit)

            sauvegarderProduits()

            enregistrerEvenement(
                "Nouveau produit ajouté : $nomProduit"
            )

            // ============================================
            // NETTOYAGE DES PHOTOS TEMPORAIRES
            // ============================================

            imagesProduit.clear()

            imageProduit = ""

            // ============================================
            // CONFIRMATION
            // ============================================

            toast("Produit ajouté avec succès")

            afficherAccueil()
        }
    )

    // ====================================================
    // AFFICHAGE DU FORMULAIRE
    // ====================================================

    setContentView(
        scroll(root)
    )
}

// ========================================================
// FIN AJOUT PRODUIT
// ========================================================


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
 // RESULTAT PHOTO : GALERIE ET CAMERA
 // ========================================================

override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode != RESULT_OK) return

    if (requestCode == MULTI_GALLERY_REQUEST) {
        val clipData = data?.clipData
        val imagesAjoutees = mutableListOf<String>()

        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri

                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                    // Certaines galeries n'autorisent pas
                    // la conservation persistante de l'URI.
                }

                imagesAjoutees.add(uri.toString())
            }
        } else {
            data?.data?.let { uri ->
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                }

                imagesAjoutees.add(uri.toString())
            }
        }

        imagesAjoutees.forEach { chemin ->
            ajouterImageProduit(chemin)
        }

        toast(
            if (imagesAjoutees.isEmpty()) {
                "Aucune photo sélectionnée"
            } else {
                "${imagesAjoutees.size} photo(s) sélectionnée(s)"
            }
        )
    }

    if (requestCode == CAMERA_REQUEST) {
        val bitmap = data?.extras?.get("data") as? Bitmap

        if (bitmap != null) {
            try {
                val chemin = sauvegarderBitmap(bitmap)
                ajouterImageProduit(chemin)
                toast("Photo enregistrée")
            } catch (e: Exception) {
                android.util.Log.e(
                    "PHOTO_PRODUIT",
                    "Erreur d'enregistrement de la photo",
                    e
                )
                toast("Impossible d'enregistrer la photo")
            }
        } else {
            toast("Aucune photo reçue de la caméra")
        }
    }
}


// ========================================================
// AJOUTER UNE IMAGE A LA GALERIE DU FORMULAIRE
// ========================================================

private fun ajouterImageProduit(chemin: String) {
    if (chemin.isBlank()) return

    if (!imagesProduit.contains(chemin)) {
        imagesProduit.add(chemin)
    }

    if (::galeriePhotos.isInitialized) {
        val image = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                75,
                75
            ).apply {
                setMargins(4, 4, 4, 4)
            }

            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = "Photo du produit"

            chargerImageProduit(this, chemin)
        }

        galeriePhotos.addView(image)
    }
}


// ========================================================
// SAUVEGARDER UNE PHOTO PRISE AVEC LA CAMERA
// ========================================================

private fun sauvegarderBitmap(bitmap: Bitmap): String {
    val fichier = File(
        filesDir,
        "produit_${System.currentTimeMillis()}.jpg"
    )

    FileOutputStream(fichier).use { sortie ->
        if (!bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                sortie
            )
        ) {
            throw Exception("Échec de compression de la photo")
        }
    }

    return Uri.fromFile(fichier).toString()
}


// ========================================================
// ENVOYER UNE PHOTO VERS CLOUDINARY
// Exécuter cette fonction sur un thread d'arrière-plan.
// ========================================================

private fun envoyerPhotoCloudinary(chemin: String): String {
    val uri = Uri.parse(chemin)

    val boundary = "----VieEspoir${System.currentTimeMillis()}"
    val nomFichier = "photo_${System.currentTimeMillis()}.jpg"

    val connexion = URL(
        "https://api.cloudinary.com/v1_1/wxpenkfy/image/upload"
    ).openConnection() as HttpURLConnection

    try {
        connexion.requestMethod = "POST"
        connexion.doOutput = true
        connexion.connectTimeout = 30000
        connexion.readTimeout = 60000

        connexion.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=$boundary"
        )

        connexion.outputStream.use { sortie ->

            fun ecrireTexte(texte: String) {
                sortie.write(texte.toByteArray(Charsets.UTF_8))
            }

            ecrireTexte("--$boundary\r\n")
            ecrireTexte(
                "Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n"
            )
            ecrireTexte("vie_espoir_products\r\n")

            ecrireTexte("--$boundary\r\n")
            ecrireTexte(
                "Content-Disposition: form-data; name=\"file\"; filename=\"$nomFichier\"\r\n"
            )
            ecrireTexte(
                "Content-Type: image/jpeg\r\n\r\n"
            )

            val entree = when (uri.scheme) {
                "content" -> contentResolver.openInputStream(uri)

                "file" -> {
                    val fichier = File(uri.path ?: "")
                    if (fichier.exists()) fichier.inputStream() else null
                }

                else -> {
                    val fichier = File(chemin)
                    if (fichier.exists()) fichier.inputStream() else null
                }
            } ?: throw Exception(
                "Photo inaccessible : $chemin"
            )

            entree.use {
                it.copyTo(sortie)
            }

            ecrireTexte("\r\n--$boundary--\r\n")
        }

        val code = connexion.responseCode

        val fluxReponse = if (code in 200..299) {
            connexion.inputStream
        } else {
            connexion.errorStream
        } ?: throw Exception("Réponse Cloudinary vide : HTTP $code")

        val reponse = fluxReponse.bufferedReader().use {
            it.readText()
        }

        if (code !in 200..299) {
            throw Exception("Cloudinary HTTP $code : $reponse")
        }

        val json = org.json.JSONObject(reponse)
        val url = json.optString("secure_url")

        if (!url.startsWith("https://")) {
            throw Exception("URL Cloudinary invalide")
        }

        return url

    } finally {
        connexion.disconnect()
    }
}


// ========================================================
// CHARGER UNE IMAGE LOCALE OU HEBERGEE EN LIGNE
// ========================================================

private fun chargerImageProduit(
    imageView: ImageView,
    chemin: String
) {
    imageView.tag = chemin

    if (chemin.isBlank()) {
        imageView.setImageResource(
            android.R.drawable.ic_menu_gallery
        )
        return
    }

    imageView.setImageResource(
        android.R.drawable.ic_menu_gallery
    )

    Thread {
        var connexion: HttpURLConnection? = null

        try {
            val bitmap: Bitmap? =
                if (
                    chemin.startsWith("https://") ||
                    chemin.startsWith("http://")
                ) {
                    connexion = URL(chemin)
                        .openConnection() as HttpURLConnection

                    connexion.connectTimeout = 15000
                    connexion.readTimeout = 20000
                    connexion.connect()

                    if (connexion.responseCode !in 200..299) {
                        throw Exception(
                            "Erreur HTTP ${connexion.responseCode}"
                        )
                    }

                    connexion.inputStream.use {
                        BitmapFactory.decodeStream(it)
                    }
                } else {
                    val uri = Uri.parse(chemin)

                    when (uri.scheme) {
                        "content" -> contentResolver
                            .openInputStream(uri)
                            ?.use {
                                BitmapFactory.decodeStream(it)
                            }

                        "file" -> {
                            val fichier = File(uri.path ?: "")
                            if (fichier.exists()) {
                                BitmapFactory.decodeFile(
                                    fichier.absolutePath
                                )
                            } else null
                        }

                        else -> {
                            val fichier = File(chemin)
                            if (fichier.exists()) {
                                BitmapFactory.decodeFile(
                                    fichier.absolutePath
                                )
                            } else null
                        }
                    }
                }

            runOnUiThread {
                if (imageView.tag == chemin) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                    } else {
                        imageView.setImageResource(
                            android.R.drawable.ic_menu_gallery
                        )
                    }
                }
            }

        } catch (e: Exception) {
            android.util.Log.e(
                "IMAGE_PRODUIT",
                "Erreur de chargement : $chemin",
                e
            )

            runOnUiThread {
                if (imageView.tag == chemin) {
                    imageView.setImageResource(
                        android.R.drawable.ic_menu_gallery
                    )
                }
            }

        } finally {
            connexion?.disconnect()
        }
    }.start()
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
// VIE ESPOIR MARKETING
// CREATION DE COMPTE ET CONNEXION FIREBASE
// VERSION CORRIGEE
// ========================================================


// ========================================================
// 1 - CHOIX DU TYPE DE COMPTE
// ========================================================

private fun afficherCreationCompte() {

    val root = layoutBase()

    root.addView(
        titre("🦁 VIE ESPOIR MARKETING")
    )

    root.addView(
        texte(
            "Bienvenue ! Choisissez votre type de compte.",
            17f,
            VERT
        )
    )

    root.addView(
        boutonVert("👤 Créer un compte client") {
            afficherFormulaireInscription("client")
        }
    )

    root.addView(
        boutonVert("🏪 Créer un compte vendeur") {
            afficherFormulaireInscription("vendeur")
        }
    )

    root.addView(
        bouton("🔐 J'ai déjà un compte") {
            afficherConnexion()
        }
    )

    setContentView(scroll(root))
}


// ========================================================
// 2 - FORMULAIRE D'INSCRIPTION
// ========================================================

private fun afficherFormulaireInscription(role: String) {

    if (role != "client" && role != "vendeur") {
        toast("Type de compte invalide")
        return
    }

    val root = layoutBase()

    val estVendeur = role == "vendeur"

    root.addView(
        titre(
            if (estVendeur) {
                "🏪 Inscription vendeur"
            } else {
                "👤 Inscription client"
            }
        )
    )

    // ====================================================
    // INFORMATIONS PERSONNELLES
    // ====================================================

    val nom = EditText(this).apply {
        hint = "Nom complet"
    }

    val entreprise = EditText(this).apply {
        hint = "Nom de l'entreprise / boutique"
    }

    val email = EditText(this).apply {
        hint = "Adresse e-mail"

        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    val telephone = EditText(this).apply {
        hint = "Téléphone"
        inputType = InputType.TYPE_CLASS_PHONE
    }

    val adresse = EditText(this).apply {
        hint = "Adresse"
    }

    val password = EditText(this).apply {
        hint = "Mot de passe"

        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    val confirmation = EditText(this).apply {
        hint = "Confirmer le mot de passe"

        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    // ====================================================
    // AFFICHAGE DES CHAMPS
    // ====================================================

    root.addView(nom)

    if (estVendeur) {
        root.addView(entreprise)
    }

    root.addView(email)
    root.addView(telephone)
    root.addView(adresse)
    root.addView(password)
    root.addView(confirmation)

    // ====================================================
    // ABONNEMENT VENDEUR
    // ====================================================

    if (estVendeur) {

        root.addView(
            texte(
                "Abonnement vendeur : 5 000 F CFA/mois.",
                16f,
                VERT
            )
        )
    }

    // ====================================================
    // PROTECTION CONTRE LES DOUBLES CLICS
    // ====================================================

    var inscriptionEnCours = false

    // ====================================================
    // BOUTON CREER MON COMPTE
    // ====================================================

    val boutonInscription = boutonVert(
        "✅ Créer mon compte"
    ) {

        if (inscriptionEnCours) {
            return@boutonVert
        }

        val n = nom.text.toString().trim()
        val ent = entreprise.text.toString().trim()
        val e = email.text.toString().trim()
        val tel = telephone.text.toString().trim()
        val a = adresse.text.toString().trim()
        val mdp = password.text.toString()
        val confirmationMdp = confirmation.text.toString()

        // ================================================
        // VERIFICATION DES INFORMATIONS
        // ================================================

        if (
            n.isEmpty() ||
            e.isEmpty() ||
            tel.isEmpty() ||
            a.isEmpty() ||
            mdp.isEmpty() ||
            (estVendeur && ent.isEmpty())
        ) {

            toast("Remplissez tous les champs obligatoires")
            return@boutonVert
        }

        if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(e)
                .matches()
        ) {

            toast("Adresse e-mail invalide")
            return@boutonVert
        }

        if (mdp.length < 6) {

            toast(
                "Le mot de passe doit contenir au moins 6 caractères"
            )

            return@boutonVert
        }

        if (mdp != confirmationMdp) {

            toast("Les mots de passe sont différents")
            return@boutonVert
        }

        // ================================================
        // CREATION FIREBASE AUTHENTICATION
        // ================================================

        inscriptionEnCours = true

        toast("Création du compte en cours...")

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(e, mdp)
            .addOnSuccessListener { resultat ->

                val uid = resultat.user?.uid

                if (uid == null) {

                    inscriptionEnCours = false

                    toast("Impossible de créer le compte")

                    return@addOnSuccessListener
                }

                // ========================================
                // DONNEES DU PROFIL
                // ========================================

                val donnees = hashMapOf<String, Any>(

                    "id" to uid,

                    "nom" to n,

                    "entreprise" to if (estVendeur) ent else "",

                    "email" to e,

                    "telephone" to tel,

                    "adresse" to a,

                    "role" to role,

                    "type" to role.uppercase(),

                    "dateCreation" to System.currentTimeMillis(),

                    "valide" to !estVendeur
                )

                // ========================================
                // ENREGISTREMENT FIRESTORE
                // =====================================
db.collection("Comptes")
    .document(uid)
    .set(donnees)
    .addOnSuccessListener {

        inscriptionEnCours = false
        compteConnecteId = uid

        prefs.edit()
            .putString(CONNECTED_ACCOUNT, uid)
            .apply()

        if (estVendeur) {
            toast("Bienvenue dans votre espace vendeur")
            afficherEspaceVendeur()
        } else {
            toast("Bienvenue dans votre espace client")
            afficherAccueil()
        }

    } // Ferme addOnSuccessListener

    .addOnFailureListener { erreur ->
        inscriptionEnCours = false
        toast("Profil non enregistré : ${erreur.message}")
    }

// ========================================================
// FIN CORRECTION INSCRIPTION VENDEUR
// ========================================================
                    .addOnFailureListener { erreur ->

                        inscriptionEnCours = false

                        toast(
                            "Compte Firebase créé, mais profil non enregistré : ${erreur.message}"
                        )
                    }
            }
            .addOnFailureListener { erreur ->

                inscriptionEnCours = false

                toast(
                    "Inscription impossible : ${erreur.message}"
                )
            }
    }

    root.addView(boutonInscription)

    // ====================================================
    // RETOUR
    // ====================================================

    root.addView(
        bouton("⬅ Retour") {
            afficherCreationCompte()
        }
    )

    setContentView(scroll(root))
}


// ========================================================
// 3 - CONNEXION FIREBASE CLIENT ET VENDEUR
// ========================================================

private fun afficherConnexion() {

    val root = layoutBase()

    root.addView(
        titre("🔐 Connexion à mon compte")
    )

    // ====================================================
    // CHAMP E-MAIL
    // ====================================================

    val email = EditText(this).apply {

        hint = "Adresse e-mail"

        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    // ====================================================
    // CHAMP MOT DE PASSE
    // ====================================================

    val password = EditText(this).apply {

        hint = "Mot de passe"

        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    root.addView(email)
    root.addView(password)

    var connexionEnCours = false

    // ====================================================
    // BOUTON SE CONNECTER
    // ====================================================

    root.addView(
        boutonVert("🔓 Se connecter") {

            if (connexionEnCours) {
                return@boutonVert
            }

            val e = email.text.toString().trim()
            val mdp = password.text.toString()

            if (e.isEmpty() || mdp.isEmpty()) {

                toast(
                    "Entrez votre e-mail et votre mot de passe"
                )

                return@boutonVert
            }

            if (
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(e)
                    .matches()
            ) {

                toast("Adresse e-mail invalide")
                return@boutonVert
            }

            connexionEnCours = true

            toast("Connexion en cours...")

            // ============================================
            // AUTHENTIFICATION FIREBASE
            // ============================================

            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(e, mdp)
                .addOnSuccessListener { resultat ->

                    val uid = resultat.user?.uid

                    if (uid == null) {

                        connexionEnCours = false

                        toast(
                            "Impossible de récupérer votre compte"
                        )

                        return@addOnSuccessListener
                    }

                    // ====================================
                    // RECHERCHE DU PROFIL FIRESTORE
                    // ====================================

                    db.collection("Comptes")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->

                            connexionEnCours = false

                            if (!document.exists()) {

                                toast(
                                    "Connexion réussie, mais profil introuvable"
                                )

                                return@addOnSuccessListener
                            }

                            // ================================
                            // TYPE DE COMPTE
                            // ================================

                            val role = (
                                document.getString("role")
                                    ?: document.getString("type")
                                    ?: "client"
                            ).trim().lowercase()

                            val valide =
                                document.getBoolean("valide") == true

                            // ================================
                            // VERIFICATION DU ROLE
                            // ================================

                            if (
                                role != "client" &&
                                role != "vendeur"
                            ) {

                                toast(
                                    "Type de compte non reconnu"
                                )

                                return@addOnSuccessListener
                            }

                            // ================================
                            // SAUVEGARDE DE LA SESSION
                            // ================================

                            compteConnecteId = uid

                            prefs.edit()
                                .putString(
                                    CONNECTED_ACCOUNT,
                                    uid
                                )
                                .apply()

// ========================================================
// VIE ESPOIR MARKETING
// SYNCHRONISATION DU COMPTE FIREBASE
// CLIENT ET VENDEUR
// ========================================================

val compteFirebase = Compte(
    id = uid,
    nom = document.getString("nom") ?: "",
    entreprise = document.getString("entreprise") ?: "",
    email = document.getString("email") ?: e,
    telephone = document.getString("telephone") ?: "",
    adresse = document.getString("adresse") ?: "",
    motDePasse = "",
    type = role.uppercase(),
    dateCreation = document.getLong("dateCreation")
        ?: System.currentTimeMillis(),
    valide = valide
)

// Éviter les doublons dans les comptes locaux
comptes.removeAll { it.id == uid }

// Ajouter le profil synchronisé
comptes.add(compteFirebase)

// ========================================================
// FIN SYNCHRONISATION DU COMPTE
// ========================================================


                            // ================================
                            // CONNEXION VENDEUR
                            // ================================

                            if (role == "vendeur") {

                                if (valide) {

                                    toast(
                                        "Bienvenue dans votre espace vendeur"
                                    )

                                    afficherEspaceVendeur()

                                } else {

                                    toast(
                                        "Votre compte vendeur attend une validation"
                                    )

                                    // ========================================================
// ========================================================
// VIE ESPOIR MARKETING
//
// CORRECTION ACCES COMPTE VENDEUR
// OUVERTURE ESPACE VENDEUR MEME EN ATTENTE
// DE VALIDATION ADMINISTRATEUR
//
// ========================================================
// ========================================================

afficherEspaceVendeur()

// ========================================================
// FIN CORRECTION ACCES VENDEUR
// ========================================================
                                }

                            } else {

                                // ============================
                                // CONNEXION CLIENT
                                // ============================

                                toast(
                                    "Bienvenue sur VIE ESPOIR MARKETING"
                                )

                                afficherAccueil()
                            }
                        }
                        .addOnFailureListener { erreur ->

                            connexionEnCours = false

                            toast(
                                "Erreur de chargement du profil : ${erreur.message}"
                            )
                        }
                }
                .addOnFailureListener { erreur ->

                    connexionEnCours = false

                    toast(
                        "Connexion impossible : ${erreur.message}"
                    )
                }
        }
    )

    // ====================================================
    // MOT DE PASSE OUBLIE
    // ====================================================

    root.addView(
        bouton("🔑 Mot de passe oublié ?") {

            afficherMotDePasseOublie()
        }
    )

    // ====================================================
    // CREER UN COMPTE
    // ====================================================

    root.addView(
        bouton("➕ Créer un nouveau compte") {

            afficherCreationCompte()
        }
    )

    // ====================================================
    // AFFICHAGE DE LA PAGE
    // ====================================================

    setContentView(scroll(root))
}

// ========================================================
// FIN DU CODE COMPLET
// VIE ESPOIR MARKETING
// ========================================================  
                     
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
// ESPACE VENDEUR (MODERNISÉ)
// ========================================================

private fun afficherEspaceVendeur() {
    val compteLocal = compteActuel()
    if (compteLocal != null) {
        rendreUiEspaceVendeur(compteLocal)
        return
    }

    val utilisateur = FirebaseAuth.getInstance().currentUser
    if (utilisateur == null) {
        toast("Veuillez vous connecter")
        afficherConnexion()
        return
    }

    val uid = utilisateur.uid
    toast("Chargement du profil vendeur...")

    db.collection("Comptes")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (!document.exists()) {
                toast("Profil vendeur absent de Firestore")
                return@addOnSuccessListener
            }

            val role = (
                document.getString("role")
                    ?: document.getString("type")
                    ?: ""
            ).trim().lowercase()

            if (role != "vendeur") {
                toast("Ce compte n'est pas un vendeur")
                return@addOnSuccessListener
            }

            val profil = Compte(
                id = uid,
                nom = document.getString("nom") ?: "",
                entreprise = document.getString("entreprise") ?: "",
                email = document.getString("email")
                    ?: utilisateur.email
                    ?: "",
                telephone = document.getString("telephone") ?: "",
                adresse = document.getString("adresse") ?: "",
                motDePasse = "",
                type = "vendeur",
                dateCreation = document.getLong("dateCreation")
                    ?: System.currentTimeMillis(),
                valide = document.getBoolean("valide") == true
            )

            comptes.removeAll { it.id == uid }
            comptes.add(profil)
            compteConnecteId = uid

            prefs.edit()
                .putString(CONNECTED_ACCOUNT, uid)
                .apply()

            toast("Bienvenue dans votre espace vendeur")
            rendreUiEspaceVendeur(profil)
        }
        .addOnFailureListener { erreur ->
            val msg = if (erreur.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                "Accès refusé à votre profil vendeur : vérifiez vos droits ou votre connexion."
            } else {
                "Erreur Firebase : ${erreur.localizedMessage}"
            }
            toast(msg)
        }
}

private fun rendreUiEspaceVendeur(compte: Compte) {
    val root = layoutBase()

    root.addView(titre("👨‍💼 Espace vendeur"))

    val lignesProfil = buildList {
        if (compte.nom.isNotBlank()) add("👋 ${compte.nom}")
        if (compte.entreprise.isNotBlank()) add("🏢 ${compte.entreprise}")
        else add("🏢 Boutique non renseignée")
        if (compte.email.isNotBlank()) add("📧 ${compte.email}")
        if (compte.telephone.isNotBlank()) add("📞 ${compte.telephone}")
        else add("📞 Téléphone non renseigné")
    }.joinToString("\n\n")

    root.addView(
        texte(lignesProfil, 17f, VERT).apply {
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setBackgroundColor(Color.WHITE)
            setPadding(24, 24, 24, 24)
        }
    )

    val jours = joursGratuitsRestants(compte)
    if (jours > 0) {
        root.addView(
            texte("🎁 Il vous reste $jours jour(s) gratuit(s).", 16f, VERT).apply {
                gravity = Gravity.CENTER
                setPadding(12, 16, 12, 16)
            }
        )
    } else {
        root.addView(
            texte("⚠️ Votre période gratuite est terminée.", 16f, Color.RED).apply {
                gravity = Gravity.CENTER
                setPadding(12, 16, 12, 16)
            }
        )
    }

    root.addView(boutonVert("➕ Ajouter un produit") { afficherAjouterProduit() })
    root.addView(bouton("🛍️ Mes produits") { afficherMesProduitsVendeur() })
    root.addView(bouton("📦 Mes commandes") { afficherCommandes() })
    root.addView(bouton("📊 Mon tableau de bord") { afficherTableauDeBord() })
    root.addView(bouton("📦 Gestion du stock") { afficherStock() })
    root.addView(bouton("💎 Mon abonnement") { afficherAbonnement() })
    root.addView(bouton("🚪 Se déconnecter") { deconnecter() })

    setContentView(scroll(root))
}

// ========================================================
// MES PRODUITS VENDEUR (MODERNISÉ)
// ========================================================

private fun afficherMesProduitsVendeur() {
    val compte = compteActuel()
    if (compte == null) {
        afficherConnexion()
        return
    }

    val uidAuth = FirebaseAuth.getInstance().currentUser?.uid
    val uidAdmin = "1kpZWN50IHhvz4y4OxS60MVjckx1"
    val estAdmin = compte.id == uidAdmin ||
        uidAuth == uidAdmin ||
        compte.type.equals("admin", ignoreCase = true)

    val root = layoutBase()
    root.addView(titre("🛍️ Mes produits"))

    val liste = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }
    root.addView(liste)

    fun sauvegarderGestion(produit: Produit) {
        val uidActuel = FirebaseAuth.getInstance().currentUser?.uid
        if (uidActuel == null) {
            toast("Session expirée : veuillez vous reconnecter")
            afficherConnexion()
            return
        }

        if (produit.vendeurId.isBlank() && !estAdmin) {
            produit.vendeurId = uidActuel
        }

        if (produit.stock <= 0) {
            produit.stock = 0
            produit.actif = false
        }

        val donnees = hashMapOf<String, Any>(
            "id" to produit.id,
            "nom" to produit.nom,
            "prix" to produit.prix,
            "categorie" to produit.categorie,
            "stock" to produit.stock,
            "description" to produit.description,
            "images" to produit.images.toList(),
            "vendeur" to produit.vendeur,
            "entreprise" to produit.entreprise,
            "vendeurId" to produit.vendeurId,
            "actif" to produit.actif,
            "disponible" to (produit.stock > 0 && produit.actif)
        )

        db.collection("Produits")
            .document(produit.id)
            .set(donnees, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                sauvegarderProduitsLocalement()
                toast("Produit mis à jour avec succès")
                afficherMesProduitsVendeur()
            }
            .addOnFailureListener { erreur ->
                val msg = if (erreur.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                    "Accès refusé : vérifiez les règles Firestore ou votre propriété sur ce produit."
                } else {
                    "Erreur Firebase : ${erreur.localizedMessage}"
                }
                toast(msg)
            }
    }

    fun afficherModification(produit: Produit) {
        val formulaire = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 10, 25, 10)
        }

        val champNom = android.widget.EditText(this).apply {
            hint = "Nom du produit"
            setText(produit.nom)
        }

        val champPrix = android.widget.EditText(this).apply {
            hint = "Prix"
            setText(produit.prix)
        }

        val champDescription = android.widget.EditText(this).apply {
            hint = "Description"
            setText(produit.description)
            minLines = 2
        }

        formulaire.addView(champNom)
        formulaire.addView(champPrix)
        formulaire.addView(champDescription)

        android.app.AlertDialog.Builder(this)
            .setTitle("Modifier le produit")
            .setView(formulaire)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Enregistrer") { _, _ ->
                val nom = champNom.text.toString().trim()
                val prix = champPrix.text.toString().trim()

                if (nom.isEmpty() || prix.isEmpty()) {
                    toast("Veuillez remplir le nom et le prix")
                    return@setPositiveButton
                }

                produit.nom = nom
                produit.prix = prix
                produit.description = champDescription.text.toString().trim()
                sauvegarderGestion(produit)
            }
            .show()
    }

    fun modifierStock(produit: Produit, ajouter: Boolean) {
        val champ = android.widget.EditText(this).apply {
            hint = "Quantité"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        android.app.AlertDialog.Builder(this)
            .setTitle(if (ajouter) "Augmenter le stock" else "Diminuer le stock")
            .setView(champ)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Valider") { _, _ ->
                val quantite = champ.text.toString().toIntOrNull()
                if (quantite == null || quantite <= 0) {
                    toast("Quantité invalide")
                    return@setPositiveButton
                }

                if (ajouter) {
                    produit.stock += quantite
                    if (!produit.actif && produit.stock > 0) {
                        produit.actif = true
                    }
                } else {
                    if (quantite > produit.stock) {
                        toast("Stock insuffisant")
                        return@setPositiveButton
                    }
                    produit.stock -= quantite
                    if (produit.stock == 0) {
                        produit.actif = false
                    }
                }

                sauvegarderGestion(produit)
            }
            .show()
    }

    var nombre = 0

    for (produit in produits) {
        val appartient =
            produit.vendeurId == compte.id ||
                (uidAuth != null && produit.vendeurId == uidAuth) ||
                (
                    produit.vendeurId.isBlank() &&
                        (
                            produit.vendeur == compte.nom ||
                                produit.entreprise == compte.entreprise
                            )
                    )

        if (estAdmin || appartient) {
            val bloc = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 20)
                setBackgroundColor(Color.WHITE)
            }

            ajouterCarteProduit(bloc, produit)

            val statutTexte = when {
                produit.stock <= 0 -> "⚠️ Rupture de stock (0)"
                produit.actif -> "🟢 En vente • Stock : ${produit.stock}"
                else -> "🔴 Masqué • Stock : ${produit.stock}"
            }

            bloc.addView(
                texte(statutTexte, 15f).apply {
                    setPadding(0, 8, 0, 8)
                }
            )

            bloc.addView(
                bouton("✏️ Modifier le produit") {
                    afficherModification(produit)
                }
            )

            val ligneStock = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val btnMoins = bouton("➖") { modifierStock(produit, false) }.apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val btnPlus = bouton("➕") { modifierStock(produit, true) }.apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            ligneStock.addView(btnMoins)
            ligneStock.addView(btnPlus)
            bloc.addView(ligneStock)

            bloc.addView(
                bouton(
                    if (produit.actif) "👁️ Masquer de la boutique"
                    else "🛍️ Remettre en vente"
                ) {
                    if (!produit.actif && produit.stock <= 0) {
                        toast("Ajoutez du stock avant de remettre le produit en vente")
                        return@bouton
                    }
                    produit.actif = !produit.actif
                    sauvegarderGestion(produit)
                }
            )

            liste.addView(bloc)
            nombre++
        }
    }

    if (nombre == 0) {
        liste.addView(
            texte("Vous n'avez encore aucun produit.", 17f).apply {
                gravity = Gravity.CENTER
                setPadding(10, 35, 10, 35)
            }
        )
    }

    setContentView(scroll(root))
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

    val compte = compteActuel()

    if (compte == null) {
        afficherConnexion()
        return
    }

    val root = layoutBase()

    root.addView(titre("💎 Abonnement vendeur"))

    val jours = joursGratuitsRestants(compte)

    if (jours > 0) {
        root.addView(
            texte(
                "🎁 Essai gratuit : $jours jour(s) restant(s).",
                18f,
                VERT
            ).apply {
                gravity = Gravity.CENTER
            }
        )
    }

    root.addView(
        texte(
            "Choisissez la durée de votre abonnement :",
            17f,
            VERT
        ).apply {
            gravity = Gravity.CENTER
        }
    )

    root.addView(
        boutonVert("💎 1 mois — 5 000 FCFA") {
            afficherPaiementAbonnement(1)
        }
    )

    root.addView(
        boutonVert("💎 2 mois — 10 000 FCFA") {
            afficherPaiementAbonnement(2)
        }
    )

    root.addView(
        boutonVert("💎 3 mois — 15 000 FCFA") {
            afficherPaiementAbonnement(3)
        }
    )

    setContentView(scroll(root))
}


private fun afficherPaiementAbonnement(mois: Int) {

    val montant = mois * 5000

    val root = layoutBase()

    root.addView(titre("💳 Paiement abonnement"))

    root.addView(
        texte(
            """
            Abonnement : $mois mois

            Montant : ${formaterNombre(montant)} FCFA

            Choisissez votre moyen de paiement.
            """.trimIndent(),
            18f,
            VERT
        ).apply {
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
        }
    )

    root.addView(
        boutonVert("🟠 Orange Money / Wave") {
            enregistrerDemandeAbonnement(
                mois,
                "ORANGE_WAVE"
            )
        }
    )

    root.addView(
        boutonVert("🟡 MTN Mobile Money") {
            enregistrerDemandeAbonnement(
                mois,
                "MTN"
            )
        }
    )

    root.addView(
        bouton("⬅️ Retour") {
            afficherAbonnement()
        }
    )

    root.addView(
        texte(
            "L'administrateur vérifiera votre paiement avant d'activer votre abonnement.",
            14f,
            Color.GRAY
        ).apply {
            gravity = Gravity.CENTER
        }
    )

    setContentView(scroll(root))
}


// ENREGISTREMENT DE LA DEMANDE D'ABONNEMENT

private fun enregistrerDemandeAbonnement(
    mois: Int,
    moyenPaiement: String
) {

    val compte = compteActuel()

    if (compte == null) {
        afficherConnexion()
        return
    }

    if (mois !in 1..3) {
        toast("Durée d'abonnement invalide")
        return
    }

    val uid = FirebaseAuth.getInstance()
        .currentUser?.uid

    if (uid.isNullOrBlank()) {
        toast("Veuillez vous reconnecter")
        afficherConnexion()
        return
    }

    val montant = mois * 5000

    val donnees = hashMapOf<String, Any>(
        "vendeurId" to uid,
        "nomVendeur" to compte.nom,
        "telephone" to compte.telephone,
        "formuleMois" to mois,
        "montant" to montant,
        "moyenPaiement" to moyenPaiement,
        "statut" to "en_attente",
        "dateDemande" to
            com.google.firebase.firestore.FieldValue.serverTimestamp()
    )

    com.google.firebase.firestore.FirebaseFirestore
        .getInstance()
        .collection("PaiementsAbonnement")
        .add(donnees)
        .addOnSuccessListener {

            toast(
                "Demande enregistrée. En attente de validation."
            )

            enregistrerEvenement(
                "Demande abonnement $mois mois — $moyenPaiement"
            )

            if (moyenPaiement == "ORANGE_WAVE") {
                appelerNumero(ORANGE_WAVE)
            } else {
                appelerNumero(MTN)
            }

        }
        .addOnFailureListener { erreur ->

            android.util.Log.e(
                "ABONNEMENT",
                "Erreur enregistrement demande",
                erreur
            )

            toast(
                "Impossible d'enregistrer la demande. Vérifiez votre connexion."
            )
        }
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
// VIE ESPOIR MARKETING
// CONNEXION ADMINISTRATEUR AVEC FIREBASE
// ========================================================

private fun afficherConnexionAdmin() {

    // ====================================================
    // 1. IDENTIFIANT OFFICIEL DE L'ADMINISTRATEUR
    // ====================================================

    val uidAdministrateur =
        "1kpZWN50IHhvz4y4OxS60MVjckx1"

    // ====================================================
    // 2. RÉCUPÉRER LE COMPTE FIREBASE CONNECTÉ
    // ====================================================

    val utilisateur =
        com.google.firebase.auth.FirebaseAuth
            .getInstance()
            .currentUser

    // ====================================================
    // 3. SI AUCUN COMPTE N'EST CONNECTÉ
    // ====================================================

    if (utilisateur == null) {

        adminConnecte = false

        AlertDialog.Builder(this)
            .setTitle("🔐 Connexion nécessaire")
            .setMessage(
                "Veuillez d'abord vous connecter à votre " +
                "compte Firebase pour accéder à l'administration."
            )
            .setPositiveButton("Se connecter") { _, _ ->

                afficherConnexion()
            }
            .setNegativeButton("Annuler", null)
            .show()

        return
    }

    // ====================================================
    // 4. VÉRIFIER L'IDENTIFIANT ADMINISTRATEUR
    // ====================================================

    if (utilisateur.uid == uidAdministrateur) {

        // Compte administrateur reconnu
        adminConnecte = true

        toast(
            "Bienvenue dans l'administration VIE ESPOIR MARKETING"
        )

        // Ouvrir le tableau de bord administrateur
        afficherAdministration()

    } else {

        // Compte non autorisé
        adminConnecte = false

        AlertDialog.Builder(this)
            .setTitle("⛔ Accès administrateur refusé")
            .setMessage(
                "Ce compte Firebase n'est pas autorisé " +
                "à accéder à l'administration."
            )
            .setPositiveButton("OK", null)
            .show()
    }
}

// ========================================================
// FIN DE LA CONNEXION ADMINISTRATEUR
// ========================================================

    
private fun afficherAdministration() {

    val uidAdministrateur = "1kpZWN50IHhvz4y4OxS60MVjckx1"

    val utilisateur = FirebaseAuth.getInstance().currentUser

    if (utilisateur == null || utilisateur.uid != uidAdministrateur) {
        adminConnecte = false
        afficherConnexionAdmin()
        return
    }

    adminConnecte = true

    // =========================================
    // COULEURS
    // =========================================

    val vert = android.graphics.Color.rgb(23, 107, 58)
    val fond = android.graphics.Color.rgb(243, 248, 245)
    val gris = android.graphics.Color.rgb(100, 116, 139)
    val blanc = android.graphics.Color.WHITE

    fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    // =========================================
    // CONTENEUR PRINCIPAL
    // =========================================

    val root = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(22))
        setBackgroundColor(fond)
        clipToPadding = false
    }

    // =========================================
    // EN-TÊTE ADMINISTRATEUR
    // =========================================

    val entete = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(24), dp(20), dp(24))

        background = android.graphics.drawable.GradientDrawable().apply {
            setColor(vert)
            cornerRadius = dp(22).toFloat()
        }
    }

    entete.addView(
        TextView(this).apply {
            text = "👑 VIE ESPOIR MARKETING"
            textSize = 19f
            setTextColor(blanc)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    )

    entete.addView(
        TextView(this).apply {
            text = "TABLEAU DE BORD ADMINISTRATEUR"
            textSize = 14f
            setTextColor(blanc)
            setPadding(0, dp(14), 0, dp(7))
        }
    )

    entete.addView(
        TextView(this).apply {
            text = "Bienvenue dans votre espace de gestion."
            textSize = 15f
            setTextColor(blanc)
        }
    )

    root.addView(
        entete,
        LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = dp(24)
        }
    )

    // =========================================
    // TITRE DES RUBRIQUES
    // =========================================

    root.addView(
        TextView(this).apply {
            text = "GESTION DE VOTRE ENTREPRISE"
            textSize = 16f
            setTextColor(vert)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, dp(18))
        }
    )

    // =========================================
    // FONCTION DE VÉRIFICATION ADMIN
    // =========================================

    fun verifierAdministrateur(): Boolean {

        val admin = FirebaseAuth.getInstance().currentUser

        if (admin == null || admin.uid != uidAdministrateur) {
            adminConnecte = false
            afficherConnexionAdmin()
            return false
        }

        adminConnecte = true
        return true
    }

    // =========================================
    // MESSAGE POUR LES RUBRIQUES À CONNECTER
    // =========================================

    fun afficherInformation(
        titre: String,
        message: String
    ) {
        if (!verifierAdministrateur()) return

        AlertDialog.Builder(this)
            .setTitle(titre)
            .setMessage(message)
            .setPositiveButton("FERMER", null)
            .show()
    }

    // =========================================
    // CRÉATION DES CARTES
    // =========================================

    fun creerCarte(
        icone: String,
        nom: String,
        description: String,
        couleur: Int,
        action: () -> Unit
    ): LinearLayout {

        return LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_VERTICAL

            setPadding(
                dp(15),
                dp(17),
                dp(10),
                dp(17)
            )

            background =
                android.graphics.drawable.GradientDrawable().apply {

                    setColor(blanc)
                    cornerRadius = dp(19).toFloat()

                    setStroke(
                        dp(1),
                        android.graphics.Color.rgb(230, 235, 231)
                    )
                }

            elevation = dp(3).toFloat()

            isClickable = true
            isFocusable = true

            // ICÔNE

            addView(
                TextView(this@MainActivity).apply {
                    text = icone
                    textSize = 31f
                    setPadding(0, 0, 0, dp(13))
                }
            )

            // NOM DE LA RUBRIQUE

            addView(
                TextView(this@MainActivity).apply {
                    text = nom
                    textSize = 17f
                    setTextColor(couleur)
                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )
                }
            )

            // DESCRIPTION

            addView(
                TextView(this@MainActivity).apply {
                    text = description
                    textSize = 13f
                    setTextColor(gris)
                    setPadding(0, dp(7), 0, 0)
                }
            )

            // ACTION

            setOnClickListener {

                if (!verifierAdministrateur()) {
                    return@setOnClickListener
                }

                action()
            }
        }
    }

    // =========================================
    // DISPOSITION DEUX COLONNES
    // =========================================

    fun ajouterLigne(
        gauche: View,
        droite: View
    ) {

        val ligne = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }

        ligne.addView(
            gauche,
            LinearLayout.LayoutParams(
                0,
                dp(145),
                1f
            ).apply {
                marginEnd = dp(7)
                bottomMargin = dp(14)
            }
        )

        ligne.addView(
            droite,
            LinearLayout.LayoutParams(
                0,
                dp(145),
                1f
            ).apply {
                marginStart = dp(7)
                bottomMargin = dp(14)
            }
        )

        root.addView(ligne)
    }

    // =========================================
    // 1. VENDEURS
    // =========================================

    val vendeurs = creerCarte(
        "👥",
        "Vendeurs",
        "Gestion des comptes",
        android.graphics.Color.rgb(23, 107, 58)
    ) {
        afficherVendeursAdmin()
    }

    // =========================================
    // 2. ABONNEMENTS
    // =========================================

    val abonnements = creerCarte(
        "💳",
        "Abonnements",
        "Paiements et activation",
        android.graphics.Color.rgb(37, 99, 235)
    ) {
        afficherInformation(
            "GESTION DES ABONNEMENTS",
            "Cette rubrique permettra de gérer les abonnements des vendeurs, les paiements, les activations et les dates d'expiration."
        )
    }

    // =========================================
    // 3. PRODUITS
    // =========================================

    val produits = creerCarte(
        "📦",
        "Produits",
        "Ajouter et gérer",
        android.graphics.Color.rgb(217, 119, 6)
    ) {
        afficherAjouterProduit()
    }

    // =========================================
    // 4. COMMANDES
    // =========================================

    val commandes = creerCarte(
        "🛒",
        "Commandes",
        "Suivi des ventes",
        android.graphics.Color.rgb(124, 58, 237)
    ) {
        afficherCommandesAdmin()
    }

    // =========================================
    // 5. NOTIFICATIONS
    // =========================================

    val notifications = creerCarte(
        "🔔",
        "Notifications",
        "Alertes et messages",
        android.graphics.Color.rgb(219, 39, 119)
    ) {
        afficherEvenementsAdmin()
    }

    // =========================================
    // 6. FINANCES
    // =========================================

    val finances = creerCarte(
        "📊",
        "Finances",
        "Suivi des revenus",
        android.graphics.Color.rgb(8, 145, 178)
    ) {
        afficherInformation(
            "GESTION FINANCIÈRE",
            "Cette rubrique permettra de suivre les ventes, les revenus, les commissions, les paiements et les dépenses de l'entreprise."
        )
    }

    // =========================================
    // AFFICHAGE DES SIX CARTES
    // =========================================

    ajouterLigne(vendeurs, abonnements)

    ajouterLigne(produits, commandes)

    ajouterLigne(notifications, finances)

    // =========================================
    // 7. PARAMÈTRES ADMINISTRATEUR
    // =========================================

    val parametres = bouton(
        "⚙️ PARAMÈTRES ADMINISTRATEUR"
    ) {

        afficherInformation(
            "PARAMÈTRES ADMINISTRATEUR",
            "Cette rubrique permettra de configurer les informations de l'entreprise, les paramètres de gestion et les accès administrateur."
        )
    }

    root.addView(
        parametres,
        LinearLayout.LayoutParams(-1, dp(58)).apply {
            bottomMargin = dp(14)
        }
    )

    // =========================================
    // 8. QUITTER L'ADMINISTRATION
    // =========================================

    val quitter = bouton(
        "🚪 QUITTER L'ADMINISTRATION"
    ) {

        if (!verifierAdministrateur()) {
            return@bouton
        }

        AlertDialog.Builder(this)
            .setTitle("Quitter l'administration")
            .setMessage(
                "Voulez-vous vraiment quitter votre espace administrateur ?"
            )
            .setNegativeButton("ANNULER", null)
            .setPositiveButton("QUITTER") { _, _ ->

                adminConnecte = false
                afficherAccueil()
            }
            .show()
    }

    root.addView(
        quitter,
        LinearLayout.LayoutParams(-1, dp(58))
    )

    // =========================================
    // AFFICHAGE FINAL
    // =========================================

    val page = scroll(root)

    viderHistoriqueEtAfficher(page)
}

// ========================================================
// FIN TABLEAU DE BORD ADMINISTRATEUR
// ========================================================

// ========================================================
// VIE ESPOIR MARKETING
// ADMINISTRATION - GESTION DES VENDEURS FIREBASE
// BOUTONS VALIDER ET REFUSER
// ========================================================

private fun afficherVendeursAdmin() {

    if (!adminConnecte) {
        afficherConnexionAdmin()
        return
    }

    val root = layoutBase()

    root.addView(titre("👥 Gestion des vendeurs"))
    root.addView(
        texte("Chargement des vendeurs Firebase...", 16f, VERT)
    )

    root.addView(
        bouton("⬅ Retour administration") {
            afficherAdministration()
        }
    )

    setContentView(scroll(root))

    // ====================================================
    // CHARGEMENT DES VENDEURS FIREBASE
    // ====================================================

    db.collection("Comptes")
        .whereEqualTo("role", "vendeur")
        .get()
        .addOnSuccessListener { resultat ->

            if (isFinishing || isDestroyed) return@addOnSuccessListener

            val page = layoutBase()

            page.addView(titre("👥 Vendeurs enregistrés"))

            page.addView(
                texte(
                    "Nombre de vendeurs : ${resultat.size()}",
                    17f,
                    VERT
                )
            )

            if (resultat.isEmpty) {
                page.addView(
                    texte("Aucun vendeur enregistré.", 16f)
                )
            }

            // ============================================
            // AFFICHAGE DE CHAQUE VENDEUR
            // ============================================

            for (document in resultat.documents) {

                val uid = document.id

                val nom = document.getString("nom") ?: ""
                val entreprise = document.getString("entreprise") ?: ""
                val email = document.getString("email") ?: ""
                val telephone = document.getString("telephone") ?: ""
                val adresse = document.getString("adresse") ?: ""

                val statut = document.getString("statut")
                val valide = document.getBoolean("valide") == true

                val refuse = statut == "refuse"

                val texteStatut = when {
                    refuse -> "❌ REFUSÉ"
                    valide -> "✅ ACTIF"
                    else -> "⏳ EN ATTENTE"
                }

                val bloc = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(12, 12, 12, 12)
                    setBackgroundColor(Color.WHITE)

                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 6, 0, 6)
                    }
                }

                bloc.addView(
                    texte(
                        """
                        👤 $nom

                        🏢 $entreprise

                        📧 $email

                        📞 $telephone

                        📍 $adresse

                        Statut : $texteStatut
                        """.trimIndent(),
                        16f,
                        VERT
                    )
                )

                bloc.addView(
                    texte(
                        when {
                            refuse -> "Ce vendeur a été refusé."
                            valide -> "Ce vendeur est autorisé."
                            else -> "Ce vendeur attend une validation."
                        },
                        15f
                    )
                )

                // ========================================
                // BOUTON VALIDER LE VENDEUR
                // ========================================

                if (!valide || refuse) {

                    bloc.addView(
                        bouton("✅ VALIDER LE VENDEUR") {

                            if (!adminConnecte) {
                                afficherConnexionAdmin()
                                return@bouton
                            }

                            AlertDialog.Builder(this)
                                .setTitle("Validation vendeur")
                                .setMessage(
                                    "Autoriser $nom à devenir vendeur ?"
                                )
                                .setNegativeButton("Annuler", null)
                                .setPositiveButton("VALIDER") { _, _ ->

                                    db.collection("Comptes")
                                        .document(uid)
                                        .update(
                                            mapOf(
                                                "valide" to true,
                                                "statut" to "actif"
                                            )
                                        )
                                        .addOnSuccessListener {
                                            toast("Vendeur validé avec succès")
                                            afficherVendeursAdmin()
                                        }
                                        .addOnFailureListener { erreur ->
                                            toast(
                                                "Erreur : ${erreur.message}"
                                            )
                                        }
                                }
                                .show()
                        }
                    )
                }

                // ========================================
                // BOUTON REFUSER LE VENDEUR
                // ========================================

                if (!refuse) {

                    bloc.addView(
                        bouton("❌ REFUSER LE VENDEUR") {

                            if (!adminConnecte) {
                                afficherConnexionAdmin()
                                return@bouton
                            }

                            AlertDialog.Builder(this)
                                .setTitle("Refuser le vendeur")
                                .setMessage(
                                    "Voulez-vous refuser $nom ?"
                                )
                                .setNegativeButton("Annuler", null)
                                .setPositiveButton("REFUSER") { _, _ ->

                                    db.collection("Comptes")
                                        .document(uid)
                                        .update(
                                            mapOf(
                                                "valide" to false,
                                                "statut" to "refuse"
                                            )
                                        )
                                        .addOnSuccessListener {
                                            toast("Vendeur refusé")
                                            afficherVendeursAdmin()
                                        }
                                        .addOnFailureListener { erreur ->
                                            toast(
                                                "Erreur : ${erreur.message}"
                                            )
                                        }
                                }
                                .show()
                        }
                    )
                }

                page.addView(bloc)
            }

            // ============================================
            // ACTUALISATION ET RETOUR
            // ============================================

            page.addView(
                bouton("🔄 ACTUALISER") {
                    afficherVendeursAdmin()
                }
            )

            page.addView(
                bouton("⬅ RETOUR ADMINISTRATION") {
                    afficherAdministration()
                }
            )

            setContentView(scroll(page))
        }
        .addOnFailureListener { erreur ->
            toast("Erreur Firebase : ${erreur.message}")
        }
}

// ========================================================
// FIN - GESTION DES VENDEURS FIREBASE
// ========================================================

     
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
 // VIE ESPOIR MARKETING
 // PARTAGE PUBLICITAIRE AUTOMATIQUE
 // PHOTO + NOM + PRIX + BOUTIQUE + CONTACT
 // ========================================================

private fun partagerProduit(produit: Produit) {

    val prix = produit.prix.toIntOrNull() ?: 0

    val photo = produit.images.firstOrNull {
        it.isNotBlank()
    }

    val textePartage = """
        🛍️ VIE ESPOIR MARKETING
        📦 Produit : ${produit.nom}
        💰 Prix : ${formaterNombre(prix)} FCFA
        🏬 Boutique : ${produit.entreprise}
        📝 ${produit.description}
        📞 Contact : $ORANGE_WAVE
    """.trimIndent()

    toast("Création de l'affiche publicitaire...")

    Thread {
        try {

            // ============================================
            // 1. CHARGER LA PHOTO DU PRODUIT
            // ============================================

            val photoBitmap: Bitmap? = if (photo == null) {
                null
            } else if (
                photo.startsWith("https://") ||
                photo.startsWith("http://")
            ) {
                val connexion =
                    URL(photo).openConnection() as HttpURLConnection

                try {
                    connexion.connectTimeout = 15000
                    connexion.readTimeout = 20000
                    connexion.connect()

                    if (connexion.responseCode !in 200..299) {
                        throw Exception("Photo inaccessible")
                    }

                    connexion.inputStream.use {
                        BitmapFactory.decodeStream(it)
                    }
                } finally {
                    connexion.disconnect()
                }

            } else {
                val uri = Uri.parse(photo)

                when (uri.scheme) {
                    "content" ->
                        contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it)
                        }

                    "file" ->
                        BitmapFactory.decodeFile(uri.path ?: "")

                    else ->
                        BitmapFactory.decodeFile(photo)
                }
            }
// ========================================================
// VIE ESPOIR MARKETING
// VERIFICATION PHOTO AVANT PARTAGE PUBLICITAIRE
// FACEBOOK - WHATSAPP
// ========================================================

if (photoBitmap == null) {

    Log.e(
        "PARTAGE_PRODUIT",
        "Photo absente ou impossible à décoder : $photo"
    )

    runOnUiThread {
        toast(
            "Impossible de charger la photo du produit"
        )
    }

    return@Thread
}

// ========================================================
// FIN VERIFICATION PHOTO
// ========================================================


            // ============================================
            // 2. CRÉER L'AFFICHE
            // ============================================

            val largeur = 1080
            val hauteur = 1500

            val affiche = Bitmap.createBitmap(
                largeur,
                hauteur,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(affiche)

            canvas.drawColor(Color.WHITE)

            val peinture = Paint(
                Paint.ANTI_ALIAS_FLAG
            )

            // Bandeau vert
            peinture.color = Color.rgb(20, 125, 65)

            canvas.drawRect(
                0f, 0f,
                largeur.toFloat(), 170f,
                peinture
            )

            peinture.color = Color.WHITE
            peinture.textSize = 54f
            peinture.typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )
            peinture.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "VIE ESPOIR MARKETING",
                540f,
                105f,
                peinture
            )

            // ============================================
            // 3. AFFICHER LA PHOTO
            // ============================================

            if (photoBitmap != null) {

                val zone = RectF(
                    60f, 205f,
                    1020f, 880f
                )

                val ratio = minOf(
                    zone.width() / photoBitmap.width,
                    zone.height() / photoBitmap.height
                )

                val w = photoBitmap.width * ratio
                val h = photoBitmap.height * ratio

                val destination = RectF(
                    zone.centerX() - w / 2,
                    zone.centerY() - h / 2,
                    zone.centerX() + w / 2,
                    zone.centerY() + h / 2
                )

                peinture.color = Color.WHITE

                canvas.drawBitmap(
                    photoBitmap,
                    null,
                    destination,
                    peinture
                )
            }

            // ============================================
            // 4. ÉCRIRE LES INFORMATIONS
            // ============================================

            peinture.textAlign = Paint.Align.LEFT
            peinture.color = Color.BLACK
            peinture.typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            fun ecrire(
                texte: String,
                y: Float,
                taille: Float,
                couleur: Int = Color.BLACK
            ) {
                peinture.textSize = taille
                peinture.color = couleur

                val maximum = 940f

                val affichage = TextUtils.ellipsize(
                    texte,
                    android.text.TextPaint(peinture),
                    maximum,
                    TextUtils.TruncateAt.END
                ).toString()

                canvas.drawText(
                    affichage,
                    70f,
                    y,
                    peinture
                )
            }

            ecrire(
                "Produit : ${produit.nom}",
                960f,
                48f
            )

            ecrire(
                "Prix : ${formaterNombre(prix)} FCFA",
                1040f,
                60f,
                Color.rgb(20, 125, 65)
            )

            ecrire(
                "Boutique : ${produit.entreprise}",
                1120f,
                43f
            )

            ecrire(
                produit.description,
                1200f,
                36f
            )

            ecrire(
                "Contact : $ORANGE_WAVE",
                1280f,
                43f
            )

            // Bandeau inférieur
            peinture.color = Color.rgb(20, 125, 65)

            canvas.drawRect(
                0f, 1370f,
                1080f, 1500f,
                peinture
            )

            peinture.color = Color.WHITE
            peinture.textSize = 43f
            peinture.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "Découvrez notre boutique !",
                540f,
                1450f,
                peinture
            )

            // ============================================
            // 5. ENREGISTRER L'AFFICHE
            // ============================================

            val dossier = File(cacheDir, "partages")
            dossier.mkdirs()

            val fichier = File(
                dossier,
                "affiche_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(fichier).use {
                if (!affiche.compress(
                        Bitmap.CompressFormat.JPEG,
                        90,
                        it
                    )
                ) {
                    throw Exception("Enregistrement impossible")
                }
            }

            val uriPartage =
                androidx.core.content.FileProvider.getUriForFile(
                    this,
                    "$packageName.fileprovider",
                    fichier
                )

     
            
            // ========================================================
            // VIE ESPOIR MARKETING
            // 6. PARTAGER SUR LES RESEAUX SOCIAUX
            // PHOTO PUBLICITAIRE + LIEN OFFICIEL APK
            // VERSION CORRIGEE SANS DOUBLON
            // ========================================================

            runOnUiThread {

                val intentPartage =
                    Intent(Intent.ACTION_SEND).apply {

                        type = "image/jpeg"

                        // ============================================
                        // PHOTO PUBLICITAIRE DU PRODUIT
                        // ============================================

                        putExtra(
                            Intent.EXTRA_STREAM,
                            uriPartage
                        )

                        // ============================================
                        // TEXTE + LIEN DE TELECHARGEMENT APK
                        // ============================================

                        putExtra(
                            Intent.EXTRA_TEXT,
                            """
                            🛍️ VIE ESPOIR MARKETING

                            Découvrez nos produits !

                            📲 Téléchargez notre application :
                            https://github.com/yaoyaoguillaume86-dot/Vie-espoir-Marketing-/releases/download/v1.0.2/app-debug.apk

                            Commandez facilement !
                            """.trimIndent()
                        )

                        // ============================================
                        // AUTORISATION DE LECTURE PHOTO
                        // ============================================

                        clipData = ClipData.newUri(
                            contentResolver,
                            "Photo du produit",
                            uriPartage
                        )

                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                // ================================================
                // OUVRIR WHATSAPP, FACEBOOK ET AUTRES APPLICATIONS
                // ================================================

                try {

                    startActivity(
                        Intent.createChooser(
                            intentPartage,
                            "Partager le produit"
                        )
                    )

                } catch (e: Exception) {

                    Log.e(
                        "PARTAGE_PRODUIT",
                        "Erreur ouverture du partage",
                        e
                    )

                    toast(
                        "Impossible de partager le produit"
                    )
                }
            }

            // ========================================================
            // FIN PARTAGE PHOTO + LIEN APK
            // ========================================================

        } catch (e: Exception) {

            // ====================================================
            // ERREUR DE CREATION DE L'AFFICHE PUBLICITAIRE
            // ====================================================

            Log.e(
                "PARTAGE_PRODUIT",
                "Erreur de création de l'affiche",
                e
            )

            runOnUiThread {

                toast(
                    "Erreur de création de l'affiche"
                )
            }
        }

    }.start()

}

// ========================================================
// FIN DE LA FONCTION PARTAGE PUBLICITAIRE
// VIE ESPOIR MARKETING
// ========================================================


// ========================================================
// PARTAGE TEXTE SI PHOTO ABSENTE
// ========================================================

private fun partagerTexteProduit(texte: String) {

    val intent = Intent(Intent.ACTION_SEND).apply {

        type = "text/plain"

        putExtra(
            Intent.EXTRA_TEXT,
            texte
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
// FIN PARTAGE PRODUIT
// ========================================================

// ========================================================
// VIE ESPOIR MARKETING
// PARTAGER LA BOUTIQUE EN LIGNE
// TELECHARGEMENT FACULTATIF
// ========================================================

private fun partagerApplication() {

    val lienBoutique =
        "https://vie-espoir-marketing.web.app"

    val textePartage = """
        🦁 VIE ESPOIR MARKETING

        🛍️ Découvrez notre boutique en ligne !

        📦 Découvrez nos produits
        🛒 Consultez notre boutique
        💳 Découvrez nos offres
        🚚 Découvrez nos services

        🌍 Ouvrez directement notre boutique ici :

        $lienBoutique

        📲 Vous pourrez télécharger notre application
        depuis la page d'accueil si vous le souhaitez.

        Bienvenue chez VIE ESPOIR MARKETING !
    """.trimIndent()

    try {

        val intentPartage =
            Intent(Intent.ACTION_SEND).apply {

                type = "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    textePartage
                )
            }

        startActivity(
            Intent.createChooser(
                intentPartage,
                "Partager Vie Espoir Marketing"
            )
        )

    } catch (e: Exception) {

        Log.e(
            "PARTAGE_APPLICATION",
            "Erreur partage application",
            e
        )

        Toast.makeText(
            this,
            "Impossible de partager la boutique",
            Toast.LENGTH_LONG
        ).show()
    }
}

// ========================================================
// FIN PARTAGE APPLICATION
// ========================================================


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
 


 // ========================================================
 // SAUVEGARDE DES PRODUITS
 // ========================================================

private fun sauvegarderProduits() {
    sauvegarderProduitsLocalement()

    
val uidVendeur = FirebaseAuth.getInstance()
    .currentUser?.uid ?: return

val donneesProduits = produits.filter {
    it.vendeurId == uidVendeur
}.toList()


    if (donneesProduits.isEmpty()) return

    Thread {
        for (produit in donneesProduits) {
            try {
                val imagesEnLigne = mutableListOf<String>()

                for (image in produit.images.toList()) {
                    val url = if (
                        image.startsWith("https://") ||
                        image.startsWith("http://")
                    ) {
                        image
                    } else {
                        envoyerPhotoCloudinary(image)
                    }

                    imagesEnLigne.add(url)
                }

                val donnees = hashMapOf<String, Any>(
                    "id" to produit.id,
                    "nom" to produit.nom,
                    "prix" to produit.prix,
                    "categorie" to produit.categorie,
                    "stock" to produit.stock,
                    "description" to produit.description,
                    "images" to imagesEnLigne,
                     "vendeur" to produit.vendeur,
"entreprise" to produit.entreprise,
"vendeurId" to produit.vendeurId,
"actif" to produit.actif            )

                db.collection("Produits")
                    .document(produit.id)
                    .set(donnees)
                    .addOnSuccessListener {
                        // Mettre à jour les images dans le produit.
                        produit.images.clear()
                        produit.images.addAll(imagesEnLigne)

                        sauvegarderProduitsLocalement()

                        android.util.Log.d(
                            "FIREBASE_PRODUITS",
                            "Produit enregistré : ${produit.nom}"
                        )

                        toast(
                            "Produit enregistré : ${produit.nom}"
                        )
                    }
                    .addOnFailureListener { erreur ->
                        android.util.Log.e(
                            "FIREBASE_PRODUITS",
                            "Erreur Firebase : ${produit.nom}",
                            erreur
                        )

                        toast(
                            "Erreur Firebase : ${erreur.localizedMessage}"
                        )
                    }

            } catch (e: Exception) {
                android.util.Log.e(
                    "CLOUDINARY_PHOTOS",
                    "Échec pour ${produit.nom}",
                    e
                )

                runOnUiThread {
                    toast(
                        "Échec d'envoi des photos de ${produit.nom}"
                    )
                }
            }
        }
    }.start()
}


// ========================================================
// SAUVEGARDE LOCALE UNIQUEMENT
// ========================================================

private fun sauvegarderProduitsLocalement() {
    val tableau = org.json.JSONArray()

    for (produit in produits) {
        val images = org.json.JSONArray()

        for (image in produit.images) {
            images.put(image)
        }

        tableau.put(
            org.json.JSONObject().apply {
                put("id", produit.id)
                put("nom", produit.nom)
                put("prix", produit.prix)
                put("categorie", produit.categorie)
                put("stock", produit.stock)
                put("description", produit.description)
                put("images", images)
                put("vendeur", produit.vendeur)
                put("entreprise", produit.entreprise)
            }
        )
    }

    prefs.edit()
        .putString(PRODUCTS_KEY, tableau.toString())
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

    val comptesJson = prefs.getString(
        ACCOUNTS_KEY,
        null
    )

    if (!comptesJson.isNullOrEmpty()) {
        try {
            val tableau = JSONArray(comptesJson)

            for (i in 0 until tableau.length()) {
                val objet = tableau.getJSONObject(i)

                comptes.add(
                    Compte(
                        id = objet.optString("id"),
                        nom = objet.optString("nom"),
                        entreprise = objet.optString("entreprise"),
                        email = objet.optString("email"),
                        telephone = objet.optString("telephone"),
                        adresse = objet.optString("adresse"),
                        motDePasse = objet.optString("motDePasse"),
                        type = objet.optString("type", "VENDEUR"),
                        dateCreation = objet.optLong(
                            "dateCreation",
                            System.currentTimeMillis()
                        ),
                        valide = objet.optBoolean("valide", false)
                    )
                )
            }
        } catch (e: Exception) {
            android.util.Log.e(
                "CHARGEMENT_COMPTES",
                "Erreur de lecture des comptes",
                e
            )
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