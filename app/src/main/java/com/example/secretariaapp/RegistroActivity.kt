package com.example.secretariaapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistroActivity : AppCompatActivity() {

    private lateinit var txtNome: TextView

    private val mDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        txtNome = findViewById(R.id.txtNome)

        val extras = intent.extras
        lateinit var registroId: String

        if (extras != null){
            registroId = extras.getString("registroId")
        }

        val mDatabaseReference = mDatabase.reference.child("registros").child(registroId)

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                txtNome.text = p0.child("nome").value as String

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
