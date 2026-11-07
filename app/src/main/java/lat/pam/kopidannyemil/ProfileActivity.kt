package lat.pam.kopidannyemil

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvAvatarInitial = findViewById<TextView>(R.id.tvAvatarInitial)
        val tvInfoName = findViewById<TextView>(R.id.tvInfoName)
        val tvInfoEmail = findViewById<TextView>(R.id.tvInfoEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnOrderHistory = findViewById<Button>(R.id.btnOrderHistory)

        val sharedPreferences = getSharedPreferences("KopiNyemil", MODE_PRIVATE)

        val name = sharedPreferences.getString("name", "Coffee Lover")
        val email = sharedPreferences.getString("email", "-")

        tvProfileName.text = name
        tvProfileEmail.text = email

        tvInfoName.text = name
        tvInfoEmail.text = email

        tvAvatarInitial.text =
            name?.trim()?.firstOrNull()?.uppercase() ?: "C"

        // Membuka halaman riwayat pesanan
        btnOrderHistory.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        // Logout
        btnLogout.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}