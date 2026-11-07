package lat.pam.kopidannyemil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private val shippingCost = 5000

    data class Product(
        val id: String,
        val name: String,
        val price: Int
    )

    private val products = listOf(
        Product("caramel_latte", "Caramel Latte", 25000),
        Product("ice_americano", "Ice Americano", 20000),
        Product("matcha_latte", "Matcha Latte", 23000),
        Product("butter_croissant", "Butter Croissant", 18000),
        Product("chocolate_donut", "Chocolate Donut", 12000)
    )

    private lateinit var etCustomerName: EditText
    private lateinit var etCustomerPhone: EditText
    private lateinit var etCustomerAddress: EditText
    private lateinit var rgPaymentMethod: RadioGroup

    private lateinit var tvCheckoutSubtotal: TextView
    private lateinit var tvCheckoutShipping: TextView
    private lateinit var tvCheckoutTotal: TextView

    private lateinit var btnPlaceOrder: Button
    private lateinit var btnCheckoutBack: TextView

    private var subtotal = 0
    private var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        etCustomerName = findViewById(R.id.etCustomerName)
        etCustomerPhone = findViewById(R.id.etCustomerPhone)
        etCustomerAddress = findViewById(R.id.etCustomerAddress)
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod)

        tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal)
        tvCheckoutShipping = findViewById(R.id.tvCheckoutShipping)
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal)

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnCheckoutBack = findViewById(R.id.btnCheckoutBack)

        btnCheckoutBack.setOnClickListener {
            finish()
        }

        val preferences = getSharedPreferences(
            "KopiNyemil",
            MODE_PRIVATE
        )

        etCustomerName.setText(
            preferences.getString("name", "")
        )

        calculateTotal()

        btnPlaceOrder.setOnClickListener {
            validateCheckout()
        }
    }

    private fun calculateTotal() {
        val cartItems = CartManager.getItems(this)

        subtotal = 0

        for ((productId, quantity) in cartItems) {
            val product = products.find {
                it.id == productId
            }

            if (product != null) {
                subtotal += product.price * quantity
            }
        }

        total = subtotal + shippingCost

        tvCheckoutSubtotal.text = formatRupiah(subtotal)
        tvCheckoutShipping.text = formatRupiah(shippingCost)
        tvCheckoutTotal.text = formatRupiah(total)
    }

    private fun validateCheckout() {
        val name = etCustomerName.text.toString().trim()
        val phone = etCustomerPhone.text.toString().trim()
        val address = etCustomerAddress.text.toString().trim()

        val cartItems = CartManager.getItems(this)

        if (cartItems.isEmpty()) {
            Toast.makeText(
                this,
                "Keranjang masih kosong",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (name.isEmpty()) {
            etCustomerName.error = "Nama wajib diisi"
            etCustomerName.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etCustomerPhone.error = "Nomor telepon wajib diisi"
            etCustomerPhone.requestFocus()
            return
        }

        if (phone.length < 10) {
            etCustomerPhone.error = "Nomor telepon belum valid"
            etCustomerPhone.requestFocus()
            return
        }

        if (address.isEmpty()) {
            etCustomerAddress.error = "Alamat wajib diisi"
            etCustomerAddress.requestFocus()
            return
        }

        val selectedPayment = rgPaymentMethod.checkedRadioButtonId

        if (selectedPayment == -1) {
            Toast.makeText(
                this,
                "Pilih metode pembayaran",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val paymentMethod = findViewById<RadioButton>(
            selectedPayment
        ).text.toString()

        saveOrder(
            name,
            phone,
            address,
            paymentMethod
        )
    }

    private fun saveOrder(
        name: String,
        phone: String,
        address: String,
        paymentMethod: String
    ) {
        val orderId = "KN${System.currentTimeMillis()}"
        val cartItems = CartManager.getItems(this)

        // Membuat detail produk dalam pesanan
        val itemsArray = JSONArray()

        for ((productId, quantity) in cartItems) {
            val product = products.find {
                it.id == productId
            }

            if (product != null) {
                val item = JSONObject()
                item.put("id", product.id)
                item.put("name", product.name)
                item.put("price", product.price)
                item.put("quantity", quantity)

                itemsArray.put(item)
            }
        }

        // Membuat data pesanan
        val order = JSONObject()
        order.put("order_id", orderId)
        order.put("customer_name", name)
        order.put("customer_phone", phone)
        order.put("customer_address", address)
        order.put("payment_method", paymentMethod)
        order.put("subtotal", subtotal)
        order.put("shipping", shippingCost)
        order.put("total", total)
        order.put("order_time", System.currentTimeMillis())
        order.put("items", itemsArray)

        // Mengambil riwayat lama
        val preferences = getSharedPreferences(
            "KopiNyemilOrders",
            MODE_PRIVATE
        )

        val oldOrders = preferences.getString("orders", "[]")

        val ordersArray = try {
            JSONArray(oldOrders ?: "[]")
        } catch (e: Exception) {
            JSONArray()
        }

        // Menambahkan pesanan baru
        ordersArray.put(order)

        // Menyimpan seluruh riwayat
        preferences.edit()
            .putString("orders", ordersArray.toString())
            .putString("last_order_id", orderId)
            .putString("last_order_name", name)
            .putString("last_order_phone", phone)
            .putString("last_order_address", address)
            .putString("last_order_payment", paymentMethod)
            .putInt("last_order_total", total)
            .putLong("last_order_time", System.currentTimeMillis())
            .apply()

        // Mengosongkan keranjang
        CartManager.clearCart(this)

        // Membuka halaman sukses
        val intent = Intent(this, OrderSuccessActivity::class.java)
        intent.putExtra("order_id", orderId)
        intent.putExtra("customer_name", name)
        intent.putExtra("order_total", total)

        startActivity(intent)
        finish()
    }

    private fun formatRupiah(amount: Int): String {
        return "Rp" + String.format(
            Locale("in", "ID"),
            "%,d",
            amount
        )
    }
}


// HALAMAN PESANAN BERHASIL

class OrderSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderId = intent.getStringExtra("order_id") ?: "-"
        val customerName = intent.getStringExtra("customer_name") ?: "-"
        val orderTotal = intent.getIntExtra("order_total", 0)

        val background = LinearLayout(this)
        background.orientation = LinearLayout.VERTICAL
        background.gravity = Gravity.CENTER
        background.setPadding(32, 32, 32, 32)
        background.setBackgroundColor(Color.rgb(250, 246, 240))

        val title = TextView(this)
        title.text = "✓"
        title.textSize = 65f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(75, 120, 75))

        val heading = TextView(this)
        heading.text = "Pesanan Berhasil!"
        heading.textSize = 26f
        heading.gravity = Gravity.CENTER
        heading.setTextColor(Color.rgb(92, 57, 35))

        val message = TextView(this)
        message.text = "Terima kasih, $customerName!\nPesanan kamu berhasil dibuat."
        message.textSize = 16f
        message.gravity = Gravity.CENTER
        message.setTextColor(Color.DKGRAY)
        message.setPadding(0, 20, 0, 20)

        val detail = TextView(this)
        detail.text = "Nomor Pesanan:\n$orderId\n\nTotal Pembayaran:\nRp${String.format(Locale("in", "ID"), "%,d", orderTotal)}"
        detail.textSize = 17f
        detail.gravity = Gravity.CENTER
        detail.setTextColor(Color.rgb(92, 57, 35))
        detail.setPadding(0, 20, 0, 30)

        val button = Button(this)
        button.text = "Kembali ke Beranda"
        button.setTextColor(Color.WHITE)
        button.setBackgroundColor(Color.rgb(110, 70, 45))

        background.addView(title)
        background.addView(heading)
        background.addView(message)
        background.addView(detail)

        background.addView(
            button,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        setContentView(background)

        button.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}