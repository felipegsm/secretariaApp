package com.example.secretariaapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaRegistros : AppCompatActivity() {

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference = mDatabase.reference.child("registros")
    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var registro: Registro
    private var list: ArrayList<Registro> = arrayListOf()

    private lateinit var recyclerView: RecyclerView
    private var adapter: CustomAdapter = CustomAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_registros)

        mDatabaseReference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val id1 = mAuth.currentUser!!.uid
                val id2 = p0.child("userId").value as String

                if(id1 == id2){
                        val endereco = p0.child("endereco").value as String
                        val dataHora = p0.child("dataHora").value as String
                        val nome = p0.child("nome").value as String
                        val email = p0.child("email").value as String
                        val userId = p0.child("userId").value as String
                        val status = p0.child("status").value as String
                        val parecer = p0.child("parecer").value as String
                        val imgUrl = p0.child("imgUrl").value as String
                        val registroId = p0.child("registroId").value as String

                        registro = Registro(registroId, endereco, dataHora, nome, email, imgUrl, userId, status, parecer)

                        list.add(registro)
                        adapter.notifyDataSetChanged()
                    }
                    gerarLista()
                }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun gerarLista(){
        recyclerView = findViewById(R.id.listOcorrencia)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}
