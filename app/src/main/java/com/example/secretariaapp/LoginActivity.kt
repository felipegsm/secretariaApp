package com.example.secretariaapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var edtLogin: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnCadastrar: Button
    private lateinit var txtEsqueciSenha: TextView

    private lateinit var email: String
    private lateinit var senha: String

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference = mDatabase.reference.child("Usuarios")
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(mAuth.currentUser != null){
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

        edtLogin = findViewById(R.id.edtLogin)
        edtSenha = findViewById(R.id.edtSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        txtEsqueciSenha = findViewById(R.id.txtEsqueci)

        btnCadastrar.setOnClickListener{
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        txtEsqueciSenha.setOnClickListener {
            val intent = Intent(this, EsqueciSenha::class.java)
            startActivity(intent)
        }

        btnEntrar.setOnClickListener{
            email = edtLogin.text.toString()
            senha = edtSenha.text.toString()

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha)){
                mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val intent = Intent(this, PerfilActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Autenticação falhou", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
