package com.example.secretariaapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_editar_perfil.*

class EditarPerfil : AppCompatActivity() {

    private lateinit var edNome: EditText
    private lateinit var edTelefone: EditText
    private lateinit var edEmail: EditText
    private lateinit var edSenha: EditText
    private lateinit var edConfSenha: EditText
    private lateinit var btnAtualizar: Button

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference = mDatabase.reference.child("Usuarios")
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        edNome = findViewById(R.id.edtNome)
        edTelefone = findViewById(R.id.edtTelefone)
        edEmail = findViewById(R.id.edtEmail)
        edSenha = findViewById(R.id.edtSenha)
        edConfSenha = findViewById(R.id.edtConfirmar)
        btnAtualizar = findViewById(R.id.btnAtualizar)
    }

    override fun onStart() {
        super.onStart()

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                edNome.text = Editable.Factory.getInstance().newEditable(p0.child("nome").value as String)
                edTelefone.text = Editable.Factory.getInstance().newEditable(p0.child("telefone").value as String)
                edEmail.text = Editable.Factory.getInstance().newEditable(p0.child("email").value as String)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        btnAtualizar.setOnClickListener {

            var can = true

            if(!TextUtils.isEmpty(edNome.text) && edNome.text != mUserReference.child("nome")){
                mUserReference.child("nome").setValue(edNome.text.toString())
            }

            if(!TextUtils.isEmpty(edTelefone.text) && edTelefone.text != mUserReference.child("telefone")){
                mUserReference.child("telefone").setValue(edTelefone.text.toString())
            }

            if(!TextUtils.isEmpty(edEmail.text) && edEmail.text != mUserReference.child("email")){
                mUserReference.child("email").setValue(edEmail.text.toString())
                mUser.updateEmail(edEmail.text.toString())
            }

            if(!TextUtils.isEmpty(edSenha.text)){
                if(edSenha.text.toString() != edConfSenha.text.toString()){
                    Toast.makeText(this, "As senhas precisam ser iguais, caso desista de alterar deixe em branco!", Toast.LENGTH_SHORT).show()
                    can = false
                }else{
                    can = true
                    mUser.updatePassword(edSenha.text.toString())
                }
            }

            if(can){
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
