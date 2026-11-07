package lat.pam.kopidannyemil

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryActivity : AppCompatActivity() {

    private val brown = Color.rgb(92, 57, 35)
    private val cream = Color.rgb(250, 246, 240)

    private lateinit var orderContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setBackgroundColor(cream)

        // HEADER
        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(20, 20, 20, 20)

        val backButton = TextView(this)
        backButton.text = "‹"
        backButton.textSize = 35f
        backButton.setTextColor(brown)
        backButton.setPadding(0, 0, 20, 0)

        backButton.setOnClickListener {
            finish()
        }

        val title = TextView(this)
        title.text = "Riwayat Pesanan"
        title.textSize = 23f
        title.setTextColor(brown)

        header.addView(backButton)
        header.addView(title)

        mainLayout.addView(header)

        // SCROLL VIEW
        val scrollView = ScrollView(this)

        orderContainer = LinearLayout(this)
        orderContainer.orientation = LinearLayout.VERTICAL
        orderContainer.setPadding(16, 8, 16, 24)

        scrollView.addView(orderContainer)

        mainLayout.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(mainLayout)

        showOrders()
    }

    // REFRESH ORDER HISTORY
    override fun onResume() {
        super.onResume()

        if (::orderContainer.isInitialized) {
            showOrders()
        }
    }

    // DISPLAY ORDERS
    private fun showOrders() {

        orderContainer.removeAllViews()

        val preferences = getSharedPreferences(
            "KopiNyemilOrders",
            MODE_PRIVATE
        )

        val savedData = preferences.getString("orders", "[]")

        val orders = try {
            JSONArray(savedData ?: "[]")
        } catch (e: Exception) {
            JSONArray()
        }

        // EMPTY ORDER
        if (orders.length() == 0) {

            val emptyText = TextView(this)

            emptyText.text =
                "Belum ada pesanan.\nPesanan kamu akan muncul di sini."

            emptyText.textSize = 16f
            emptyText.gravity = Gravity.CENTER
            emptyText.setTextColor(brown)
            emptyText.setPadding(20, 100, 20, 100)

            orderContainer.addView(emptyText)

            return
        }

        // NEWEST ORDER FIRST
        for (i in orders.length() - 1 downTo 0) {

            val order = orders.optJSONObject(i) ?: continue

            val orderId = order.optString("order_id", "-")
            val customerName = order.optString("customer_name", "-")
            val phone = order.optString("customer_phone", "-")
            val address = order.optString("customer_address", "-")
            val payment = order.optString("payment_method", "-")
            val total = order.optInt("total", 0)
            val time = order.optLong("order_time", 0)

            // ORDER CARD
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(18, 18, 18, 18)
            card.setBackgroundColor(Color.WHITE)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.bottomMargin = 16

            card.addView(
                createText(
                    "Pesanan #$orderId",
                    17f,
                    brown
                )
            )

            card.addView(
                createText(
                    "Berhasil dibuat",
                    13f,
                    Color.rgb(65, 125, 75)
                )
            )

            val dateFormat = SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale("id", "ID")
            )

            val dateText = if (time > 0) {
                dateFormat.format(Date(time))
            } else {
                "-"
            }

            card.addView(
                createText(
                    dateText,
                    13f,
                    Color.GRAY
                )
            )

            addDivider(card)

            card.addView(
                createText(
                    "Nama: $customerName",
                    14f,
                    Color.DKGRAY
                )
            )

            card.addView(
                createText(
                    "Telepon: $phone",
                    14f,
                    Color.DKGRAY
                )
            )

            card.addView(
                createText(
                    "Alamat: $address",
                    14f,
                    Color.DKGRAY
                )
            )

            addDivider(card)

            card.addView(
                createText(
                    "Daftar Produk",
                    15f,
                    brown
                )
            )

            val items = order.optJSONArray("items") ?: JSONArray()

            for (j in 0 until items.length()) {

                val item = items.optJSONObject(j) ?: continue

                val productName = item.optString("name", "-")
                val quantity = item.optInt("quantity", 0)
                val price = item.optInt("price", 0)

                card.addView(
                    createText(
                        "$productName x $quantity = ${formatRupiah(price * quantity)}",
                        14f,
                        Color.DKGRAY
                    )
                )
            }

            addDivider(card)

            card.addView(
                createText(
                    "Pembayaran: $payment",
                    14f,
                    Color.DKGRAY
                )
            )

            card.addView(
                createText(
                    "Total: ${formatRupiah(total)}",
                    17f,
                    brown
                )
            )

            orderContainer.addView(card, params)
        }
    }

    // CREATE TEXT
    private fun createText(
        value: String,
        size: Float,
        color: Int
    ): TextView {

        val text = TextView(this)

        text.text = value
        text.textSize = size
        text.setTextColor(color)
        text.setPadding(0, 5, 0, 5)

        return text
    }

    // DIVIDER
    private fun addDivider(container: LinearLayout) {

        val divider = View(this)

        divider.setBackgroundColor(
            Color.rgb(230, 220, 210)
        )

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )

        params.setMargins(0, 12, 0, 12)

        container.addView(divider, params)
    }

    // RUPIAH FORMAT
    private fun formatRupiah(amount: Int): String {

        return "Rp" + String.format(
            Locale("in", "ID"),
            "%,d",
            amount
        )
    }
}