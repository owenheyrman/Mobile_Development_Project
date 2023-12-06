package edu.ap.playtomicandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmailAddress: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()


        editTextEmailAddress = findViewById(R.id.editTextEmailAddress)
        editTextPassword = findViewById(R.id.editTextPassword)
    }

    fun register(view: View) {
        val email = editTextEmailAddress.text.toString()
        val password = editTextPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
    fun goToLogin(view: View){
        val intent= Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
}
