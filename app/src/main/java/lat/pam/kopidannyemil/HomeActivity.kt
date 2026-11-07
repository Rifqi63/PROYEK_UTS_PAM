package lat.pam.kopidannyemil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private var selectedCategory = "Coffee"

    private data class Product(
        val id: String,
        val name: String,
        val category: String,
        val price: Int,
        val description: String,
        val image: Int,
        val cardId: Int,
        val favoriteId: Int
    )

    private val products = listOf(
        Product(
            "caramel_latte", "Caramel Latte", "Coffee", 25000,
            "Perpaduan espresso, susu creamy, dan manisnya saus caramel yang cocok menemani aktivitasmu.",
            R.drawable.caramel_latte,
            R.id.cardCaramel, R.id.btnFavoriteCaramel
        ),
        Product(
            "ice_americano", "Ice Americano", "Coffee", 20000,
            "Kopi espresso dengan perpaduan air dan es yang memberikan rasa kopi segar dan nikmat.",
            R.drawable.ice_americano,
            R.id.cardAmericano, R.id.btnFavoriteAmericano
        ),
        Product(
            "matcha_latte", "Matcha Latte", "Drinks", 23000,
            "Minuman matcha dengan susu lembut yang menghasilkan rasa creamy dan aroma teh hijau.",
            R.drawable.matcha_latte,
            R.id.cardMatcha, R.id.btnFavoriteMatcha
        ),
        Product(
            "butter_croissant", "Butter Croissant", "Snacks", 18000,
            "Croissant dengan tekstur renyah di luar dan lembut di dalam, dengan aroma butter yang menggugah selera.",
            R.drawable.butter_croissant,
            R.id.cardCroissant, R.id.btnFavoriteCroissant
        ),
        Product(
            "chocolate_donut", "Chocolate Donut", "Snacks", 12000,
            "Donat lembut dengan lapisan cokelat manis yang cocok menjadi teman minum kopi.",
            R.drawable.chocolate_donut,
            R.id.cardDonut, R.id.btnFavoriteDonut
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupNavigation()
        setupCategories()
        setupSearch()
        setupProducts()
        setupFavorites()

        filterProducts("")
        updateCategoryAppearance()
    }

    // NAVIGATION
    private fun setupNavigation() {

        val navHome = findViewById<TextView>(R.id.navHome)
        val navFavorite = findViewById<TextView>(R.id.navFavorite)
        val navOrders = findViewById<TextView>(R.id.navOrders)
        val navProfile = findViewById<TextView>(R.id.navProfile)
        val tvProfile = findViewById<TextView>(R.id.tvProfile)

        navHome.setOnClickListener {
            findViewById<ScrollView>(R.id.homeScroll)
                .smoothScrollTo(0, 0)
        }

        navFavorite.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        // Orders sekarang membuka keranjang
        navOrders.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        tvProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // CATEGORY
    private fun setupCategories() {

        findViewById<TextView>(R.id.btnCategoryCoffee)
            .setOnClickListener {
                selectedCategory = "Coffee"
                updateCategoryAppearance()
                filterProducts(findViewById<EditText>(R.id.etSearch).text.toString())
            }

        findViewById<TextView>(R.id.btnCategorySnacks)
            .setOnClickListener {
                selectedCategory = "Snacks"
                updateCategoryAppearance()
                filterProducts(findViewById<EditText>(R.id.etSearch).text.toString())
            }

        findViewById<TextView>(R.id.btnCategoryDrinks)
            .setOnClickListener {
                selectedCategory = "Drinks"
                updateCategoryAppearance()
                filterProducts(findViewById<EditText>(R.id.etSearch).text.toString())
            }

        findViewById<TextView>(R.id.tvOurMenu)
            .setOnClickListener {
                selectedCategory = "All"
                findViewById<EditText>(R.id.etSearch).text.clear()
                updateCategoryAppearance()
                filterProducts("")

                Toast.makeText(
                    this,
                    "Menampilkan semua menu",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // CATEGORY APPEARANCE
    private fun updateCategoryAppearance() {

        val coffee = findViewById<TextView>(R.id.btnCategoryCoffee)
        val snacks = findViewById<TextView>(R.id.btnCategorySnacks)
        val drinks = findViewById<TextView>(R.id.btnCategoryDrinks)

        val categories = listOf(
            Pair("Coffee", coffee),
            Pair("Snacks", snacks),
            Pair("Drinks", drinks)
        )

        for ((category, button) in categories) {
            if (selectedCategory == category) {
                button.setBackgroundResource(R.drawable.bg_primary_button)
                button.setTextColor(Color.WHITE)
            } else {
                button.setBackgroundResource(R.drawable.bg_home_chip)
                button.setTextColor(Color.parseColor("#5D4033"))
            }
        }
    }

    // SEARCH
    private fun setupSearch() {

        val search = findViewById<EditText>(R.id.etSearch)

        search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    // FILTER PRODUCTS
    private fun filterProducts(query: String) {

        var visibleCount = 0

        for (product in products) {

            val card = findViewById<View>(product.cardId)

            val matchesCategory =
                selectedCategory == "All" ||
                        product.category == selectedCategory ||
                        (selectedCategory == "Drinks" && product.category == "Coffee")

            val matchesSearch =
                product.name.contains(query.trim(), ignoreCase = true)

            val shouldShow = matchesCategory && matchesSearch

            card.visibility = if (shouldShow) View.VISIBLE else View.GONE

            if (shouldShow) visibleCount++
        }

        val caramelVisible =
            findViewById<View>(R.id.cardCaramel).visibility == View.VISIBLE
        val americanoVisible =
            findViewById<View>(R.id.cardAmericano).visibility == View.VISIBLE
        val matchaVisible =
            findViewById<View>(R.id.cardMatcha).visibility == View.VISIBLE
        val croissantVisible =
            findViewById<View>(R.id.cardCroissant).visibility == View.VISIBLE
        val donutVisible =
            findViewById<View>(R.id.cardDonut).visibility == View.VISIBLE

        findViewById<LinearLayout>(R.id.rowProducts1).visibility =
            if (caramelVisible || americanoVisible) View.VISIBLE else View.GONE

        findViewById<LinearLayout>(R.id.rowProducts2).visibility =
            if (matchaVisible || croissantVisible) View.VISIBLE else View.GONE

        findViewById<LinearLayout>(R.id.rowProducts3).visibility =
            if (donutVisible) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.tvProductCount).text =
            if (visibleCount == 0) "Menu tidak ditemukan"
            else "$visibleCount Products"
    }

    // PRODUCT CARD
    private fun setupProducts() {

        for (product in products) {

            findViewById<View>(product.cardId)
                .setOnClickListener {

                    val intent = Intent(this, ProductDetailActivity::class.java)

                    intent.putExtra("product_id", product.id)
                    intent.putExtra("product_name", product.name)
                    intent.putExtra("product_category", product.category)
                    intent.putExtra("product_price", product.price)
                    intent.putExtra("product_description", product.description)
                    intent.putExtra("product_image", product.image)

                    startActivity(intent)
                }
        }
    }

    // FAVORITE BUTTONS
    private fun setupFavorites() {

        for (product in products) {

            val favoriteButton =
                findViewById<TextView>(product.favoriteId)

            favoriteButton.setOnClickListener {

                val isNowFavorite =
                    FavoriteManager.toggleFavorite(this, product.id)

                updateFavoriteIcon(favoriteButton, product.id)

                if (isNowFavorite) {
                    Toast.makeText(
                        this,
                        product.name + " ditambahkan ke Favorite ❤️",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        product.name + " dihapus dari Favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            updateFavoriteIcon(favoriteButton, product.id)
        }
    }

    // UPDATE FAVORITE ICON
    private fun updateFavoriteIcon(
        button: TextView,
        productId: String
    ) {

        if (FavoriteManager.isFavorite(this, productId)) {
            button.text = "♥"
            button.setTextColor(Color.parseColor("#C94F45"))
        } else {
            button.text = "♡"
            button.setTextColor(Color.parseColor("#B65B45"))
        }
    }

    // REFRESH FAVORITE WHEN RETURNING TO HOME
    override fun onResume() {
        super.onResume()

        for (product in products) {
            val button = findViewById<TextView>(product.favoriteId)
            updateFavoriteIcon(button, product.id)
        }
    }
}