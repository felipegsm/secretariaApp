package com.example.secretariaapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilActivity : AppCompatActivity() {

    private lateinit var txtNome: TextView
    private lateinit var txtTelefone: TextView
    private lateinit var txtEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnCriarRegistro: Button
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnListaRegistros: Button

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference = mDatabase.reference.child("Usuarios")
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        if(mAuth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        txtNome = findViewById(R.id.txtNome)
        txtTelefone = findViewById(R.id.txtTelefone)
        txtEmail = findViewById(R.id.txtEmail)
        btnLogout = findViewById(R.id.btnSair)
        btnCriarRegistro = findViewById(R.id.btnNovoRegistro)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnListaRegistros = findViewById(R.id.btnRegistros)

        btnLogout.setOnClickListener{
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCriarRegistro.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfil::class.java)
            startActivity(intent)
        }

        btnListaRegistros.setOnClickListener {
            val intent = Intent(this, ListaRegistros::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference.child(mUser!!.uid)

        txtEmail.text = mUser.email

        mUserReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                txtNome.text = p0.child("nome").value as String
                txtTelefone.text = p0.child("telefone").value as String
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
