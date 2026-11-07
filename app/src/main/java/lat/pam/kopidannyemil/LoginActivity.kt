package lat.pam.kopidannyemil

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLoginNow = findViewById<Button>(R.id.btnLoginNow)
        val tvBackRegister = findViewById<TextView>(R.id.tvBackRegister)

        // Mengambil akun yang sudah didaftarkan
        val sharedPreferences = getSharedPreferences(
            "KopiNyemil",
            MODE_PRIVATE
        )

        btnLoginNow.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            val savedEmail = sharedPreferences.getString("email", "")
            val savedPassword = sharedPreferences.getString("password", "")

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (email == savedEmail && password == savedPassword) {

                Toast.makeText(
                    this,
                    "Login Successful!",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()

            } else {

                Toast.makeText(
                    this,
                    "Email or password is incorrect",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Kembali ke halaman Register
        tvBackRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}