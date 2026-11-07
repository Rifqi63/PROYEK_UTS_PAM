package lat.pam.kopidannyemil

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity

class FavoriteActivity : AppCompatActivity() {

    data class Product(
        val id: String,
        val name: String,
        val description: String,
        val price: String,
        val image: Int
    )

    private val products = listOf(
        Product(
            "caramel_latte",
            "Caramel Latte",
            "Sweet & creamy coffee",
            "Rp 25.000",
            R.drawable.caramel_latte
        ),
        Product(
            "ice_americano",
            "Ice Americano",
            "Fresh & bold espresso",
            "Rp 20.000",
            R.drawable.ice_americano
        ),
        Product(
            "matcha_latte",
            "Matcha Latte",
            "Smooth & earthy matcha",
            "Rp 23.000",
            R.drawable.matcha_latte
        ),
        Product(
            "butter_croissant",
            "Butter Croissant",
            "Flaky & buttery pastry",
            "Rp 18.000",
            R.drawable.butter_croissant
        ),
        Product(
            "chocolate_donut",
            "Chocolate Donut",
            "Chocolate glazed treat",
            "Rp 12.000",
            R.drawable.chocolate_donut
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        findViewById<ImageView>(R.id.btnBackFavorite).setOnClickListener {
            finish()
        }

        showFavorites()
    }

    override fun onResume() {
        super.onResume()
        showFavorites()
    }

    private fun showFavorites() {
        val favoriteList = findViewById<LinearLayout>(R.id.layoutFavoriteList)
        val emptyLayout = findViewById<LinearLayout>(R.id.layoutEmptyFavorite)
        val favoriteCount = findViewById<TextView>(R.id.tvFavoriteCount)

        favoriteList.removeAllViews()

        val savedFavorites = FavoriteManager.getFavorites(this)
        val favoriteProducts = products.filter {
            savedFavorites.contains(it.id)
        }

        favoriteCount.text = "${favoriteProducts.size} Items"

        if (favoriteProducts.isEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            favoriteList.visibility = View.GONE
            return
        }

        emptyLayout.visibility = View.GONE
        favoriteList.visibility = View.VISIBLE

        favoriteProducts.forEach { product ->
            addFavoriteItem(favoriteList, product)
        }
    }

    private fun addFavoriteItem(
        container: LinearLayout,
        product: Product
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = getDrawable(R.drawable.bg_home_card)
            elevation = dp(2).toFloat()
        }

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(112)
        ).apply {
            topMargin = dp(12)
        }

        val image = ImageView(this).apply {
            setImageResource(product.image)
            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = product.name
        }

        card.addView(
            image,
            LinearLayout.LayoutParams(dp(88), dp(84))
        )

        val information = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val informationParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            marginStart = dp(14)
        }

        val name = TextView(this).apply {
            text = product.name
            textSize = 16f
            setTextColor(Color.parseColor("#38251C"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val description = TextView(this).apply {
            text = product.description
            textSize = 12f
            setTextColor(Color.parseColor("#927967"))
        }

        val price = TextView(this).apply {
            text = product.price
            textSize = 14f
            setTextColor(Color.parseColor("#75452D"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        information.addView(name)
        information.addView(description)
        information.addView(price)

        card.addView(information, informationParams)

        val heart = TextView(this).apply {
            text = "♥"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#C94F45"))
            contentDescription = "Remove from favorites"
            isClickable = true
            isFocusable = true

            setOnClickListener {
                FavoriteManager.toggleFavorite(
                    this@FavoriteActivity,
                    product.id
                )

                Toast.makeText(
                    this@FavoriteActivity,
                    "Dihapus dari Favorite",
                    Toast.LENGTH_SHORT
                ).show()

                showFavorites()
            }
        }

        card.addView(
            heart,
            LinearLayout.LayoutParams(dp(40), dp(60))
        )

        container.addView(card, cardParams)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}