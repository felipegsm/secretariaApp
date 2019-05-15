package com.example.secretariaapp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class RegistroActivity : AppCompatActivity() {

    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtEndereco: TextView
    private lateinit var txtDataHora: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtParecer: TextView
    private lateinit var imgAnexo: ImageView

    private val mDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        txtNome = findViewById(R.id.txtNome)
        txtEmail = findViewById(R.id.txtEmail)
        txtEndereco = findViewById(R.id.txtEndereco)
        txtDataHora = findViewById(R.id.txtDataHora)
        txtStatus = findViewById(R.id.txtStatus)
        txtParecer = findViewById(R.id.txtParecer)
        imgAnexo = findViewById(R.id.imgAnexo)

        val extras = intent.extras
        lateinit var registroId: String

        if (extras != null){
            registroId = extras.getString("registroId")
        }

        val mDatabaseReference = mDatabase.reference.child("registros").child(registroId)

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                txtNome.text = p0.child("nome").value as String
                txtEmail.text = p0.child("email").value as String
                txtEndereco.text = p0.child("endereco").value as String
                txtDataHora.text = p0.child("dataHora").value as String
                txtStatus.text = Html.fromHtml("<b> Status: </b>" + p0.child("status").value as String)
                txtParecer.text = Html.fromHtml("<b> Parecer: </b>" + p0.child("parecer").value as String)

                Picasso.with(this@RegistroActivity).load(p0.child("imgUrl").value as String).into(imgAnexo)

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
