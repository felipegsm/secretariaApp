package com.example.secretariaapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CadastroActivity : AppCompatActivity() {

    private lateinit var edNome: EditText
    private lateinit var edTelefone: EditText
    private lateinit var edEmail: EditText
    private lateinit var edSenha: EditText
    private lateinit var edConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button

    private lateinit var nome: String
    private lateinit var telefone: String
    private lateinit var email: String
    private lateinit var senha: String
    private lateinit var confirmarSenha: String

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference = mDatabase.reference.child("Usuarios")
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        edNome = findViewById(R.id.cdNome)
        edTelefone = findViewById(R.id.cdTelefone)
        edEmail = findViewById(R.id.cdEmail)
        edSenha = findViewById(R.id.cdSenha)
        edConfirmarSenha = findViewById(R.id.cdConfirmar)
        btnCadastrar = findViewById(R.id.btnCadastro)

        btnCadastrar.setOnClickListener{
            nome = edNome.text.toString()
            telefone = edTelefone.text.toString()
            email = edEmail.text.toString()
            senha = edSenha.text.toString()
            confirmarSenha = edConfirmarSenha.text.toString()

            if(!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(telefone) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha) && !TextUtils.isEmpty(confirmarSenha)){
                if(senha == confirmarSenha){
                    mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            val idUsuario = mAuth.currentUser!!.uid

                            val usuarioDb = mDatabaseReference.child(idUsuario)
                            usuarioDb.child("nome").setValue(nome)
                            usuarioDb.child("telefone").setValue(telefone)
                            usuarioDb.child("email").setValue(email)

                            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, PerfilActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                    }
                }else{
                    Toast.makeText(this, "As senhas precisam ser iguais!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
