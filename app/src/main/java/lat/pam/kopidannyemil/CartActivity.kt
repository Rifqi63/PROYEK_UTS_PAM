package lat.pam.kopidannyemil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {

    private data class CartProduct(
        val id: String,
        val name: String,
        val price: Int,
        val image: Int
    )

    private val products = listOf(
        CartProduct(
            "caramel_latte",
            "Caramel Latte",
            25000,
            R.drawable.caramel_latte
        ),
        CartProduct(
            "ice_americano",
            "Ice Americano",
            20000,
            R.drawable.ice_americano
        ),
        CartProduct(
            "matcha_latte",
            "Matcha Latte",
            23000,
            R.drawable.matcha_latte
        ),
        CartProduct(
            "butter_croissant",
            "Butter Croissant",
            18000,
            R.drawable.butter_croissant
        ),
        CartProduct(
            "chocolate_donut",
            "Chocolate Donut",
            12000,
            R.drawable.chocolate_donut
        )
    )

    private lateinit var layoutCartItems: LinearLayout
    private lateinit var layoutEmptyCart: LinearLayout
    private lateinit var layoutCartSummary: LinearLayout
    private lateinit var tvCartItemCount: TextView
    private lateinit var tvCartSubtotal: TextView
    private lateinit var tvCartTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        layoutCartItems = findViewById(R.id.layoutCartItems)
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart)
        layoutCartSummary = findViewById(R.id.layoutCartSummary)

        tvCartItemCount = findViewById(R.id.tvCartItemCount)
        tvCartSubtotal = findViewById(R.id.tvCartSubtotal)
        tvCartTotal = findViewById(R.id.tvCartTotal)

        // Tombol kembali
        findViewById<TextView>(R.id.btnCartBack).setOnClickListener {
            finish()
        }

        // Tombol checkout
        findViewById<Button>(R.id.btnCheckout).setOnClickListener {

            val items = CartManager.getItems(this)

            if (items.isEmpty()) {

                Toast.makeText(
                    this,
                    "Keranjang masih kosong",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)

            }
        }

        showCartItems()
    }

    override fun onResume() {
        super.onResume()
        showCartItems()
    }

    // Menampilkan isi keranjang
    private fun showCartItems() {

        layoutCartItems.removeAllViews()

        val cartItems = CartManager.getItems(this)

        var subtotal = 0
        var totalQuantity = 0

        if (cartItems.isEmpty()) {

            layoutEmptyCart.visibility = View.VISIBLE
            layoutCartSummary.visibility = View.GONE

        } else {

            layoutEmptyCart.visibility = View.GONE
            layoutCartSummary.visibility = View.VISIBLE

            for ((productId, quantity) in cartItems) {

                val product = products.find {
                    it.id == productId
                } ?: continue

                subtotal += product.price * quantity
                totalQuantity += quantity

                createCartItem(product, quantity)
            }
        }

        tvCartItemCount.text = totalQuantity.toString()
        tvCartSubtotal.text = formatRupiah(subtotal)
        tvCartTotal.text = formatRupiah(subtotal)
    }

    // Membuat tampilan setiap produk
    private fun createCartItem(
        product: CartProduct,
        quantity: Int
    ) {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setPadding(12, 12, 12, 12)
        card.setBackgroundColor(Color.WHITE)

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        cardParams.bottomMargin = 12

        val image = ImageView(this)
        image.setImageResource(product.image)
        image.scaleType = ImageView.ScaleType.CENTER_CROP

        val imageParams = LinearLayout.LayoutParams(85, 85)
        card.addView(image, imageParams)

        val detail = LinearLayout(this)
        detail.orientation = LinearLayout.VERTICAL
        detail.setPadding(12, 0, 0, 0)

        val detailParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val name = TextView(this)
        name.text = product.name
        name.textSize = 15f
        name.setTextColor(Color.parseColor("#4B3025"))
        name.setTypeface(null, android.graphics.Typeface.BOLD)

        detail.addView(name)

        val price = TextView(this)
        price.text = formatRupiah(product.price)
        price.textSize = 13f
        price.setTextColor(Color.parseColor("#A4775B"))

        val priceParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        priceParams.topMargin = 5
        detail.addView(price, priceParams)

        // Kontrol jumlah produk
        val controls = LinearLayout(this)
        controls.orientation = LinearLayout.HORIZONTAL
        controls.gravity = Gravity.CENTER_VERTICAL

        val minus = TextView(this)
        minus.text = " − "
        minus.textSize = 20f
        minus.setTextColor(Color.parseColor("#79513B"))

        val quantityText = TextView(this)
        quantityText.text = quantity.toString()
        quantityText.textSize = 15f
        quantityText.setTextColor(Color.parseColor("#4B3025"))

        val plus = TextView(this)
        plus.text = " + "
        plus.textSize = 20f
        plus.setTextColor(Color.parseColor("#79513B"))

        controls.addView(minus)
        controls.addView(quantityText)
        controls.addView(plus)

        val controlsParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        controlsParams.topMargin = 5
        detail.addView(controls, controlsParams)

        // Tombol hapus
        val remove = TextView(this)
        remove.text = "Hapus"
        remove.textSize = 12f
        remove.setTextColor(Color.parseColor("#C94F45"))

        val removeParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        removeParams.topMargin = 5
        detail.addView(remove, removeParams)

        card.addView(detail, detailParams)

        layoutCartItems.addView(card, cardParams)

        // Kurangi jumlah
        minus.setOnClickListener {

            CartManager.updateQuantity(
                this,
                product.id,
                quantity - 1
            )

            showCartItems()
        }

        // Tambah jumlah
        plus.setOnClickListener {

            CartManager.updateQuantity(
                this,
                product.id,
                quantity + 1
            )

            showCartItems()
        }

        // Hapus produk
        remove.setOnClickListener {

            CartManager.removeItem(this, product.id)

            showCartItems()

            Toast.makeText(
                this,
                product.name + " dihapus dari keranjang",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Format harga rupiah
    private fun formatRupiah(amount: Int): String {
        return "Rp " + String.format("%,d", amount)
            .replace(',', '.')
    }
}