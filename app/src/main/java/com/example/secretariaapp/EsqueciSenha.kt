package com.example.secretariaapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class EsqueciSenha : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnEnviar: Button

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_senha)

        edtEmail = findViewById(R.id.edtEmailEsq)
        btnEnviar = findViewById(R.id.btnEnviarEmail)

        btnEnviar.setOnClickListener {
            val email = edtEmail.text.toString()
            if(!TextUtils.isEmpty(email)){
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    Toast.makeText(this, "Email enviado!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else{
                Toast.makeText(this, "Informe o seu email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
