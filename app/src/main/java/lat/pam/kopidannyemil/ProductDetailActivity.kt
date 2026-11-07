package lat.pam.kopidannyemil

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {

    private var quantity = 1
    private var productId = ""
    private var productName = ""
    private var productPrice = 0

    private lateinit var tvQuantity: TextView
    private lateinit var tvProductPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Menghubungkan komponen XML
        val btnBack = findViewById<TextView>(R.id.btnBack)
        val imgProduct = findViewById<ImageView>(R.id.imgProduct)
        val tvProductCategory = findViewById<TextView>(R.id.tvProductCategory)
        val tvProductName = findViewById<TextView>(R.id.tvProductName)

        tvProductPrice = findViewById(R.id.tvProductPrice)

        val tvProductDescription =
            findViewById<TextView>(R.id.tvProductDescription)

        val btnMinus = findViewById<TextView>(R.id.btnMinus)

        tvQuantity = findViewById(R.id.tvQuantity)

        val btnPlus = findViewById<TextView>(R.id.btnPlus)

        val btnAddToCart =
            findViewById<Button>(R.id.btnAddToCart)

        // Mengambil data produk dari HomeActivity
        productId = intent.getStringExtra("product_id") ?: ""
        productName = intent.getStringExtra("product_name") ?: "Produk"

        val category =
            intent.getStringExtra("product_category") ?: ""

        productPrice =
            intent.getIntExtra("product_price", 0)

        val description =
            intent.getStringExtra("product_description") ?: ""

        val image =
            intent.getIntExtra("product_image", 0)

        // Menampilkan informasi produk
        tvProductName.text = productName
        tvProductCategory.text = category
        tvProductDescription.text = description

        if (image != 0) {
            imgProduct.setImageResource(image)
        }

        updateQuantity()

        // Tombol kembali
        btnBack.setOnClickListener {
            finish()
        }

        // Mengurangi jumlah produk
        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantity()
            }
        }

        // Menambah jumlah produk
        btnPlus.setOnClickListener {
            quantity++
            updateQuantity()
        }

        // Menambahkan produk ke keranjang
        btnAddToCart.setOnClickListener {

            if (productId.isNotEmpty()) {

                CartManager.addItem(
                    this,
                    productId,
                    quantity
                )

                Toast.makeText(
                    this,
                    "$productName berhasil ditambahkan ke keranjang",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "ID produk tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Memperbarui jumlah dan harga
    private fun updateQuantity() {

        tvQuantity.text = quantity.toString()

        val total = productPrice * quantity

        tvProductPrice.text = "Rp %,d".format(total)
    }
}