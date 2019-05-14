package com.example.secretariaapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CustomAdapter (private val context: Context, private val dataList: ArrayList<Registro>) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>(){

    inner class CustomViewHolder(mView: View) : RecyclerView.ViewHolder(mView){
        val txtOcorrencia: TextView = mView.findViewById(R.id.txtOcorrencia)
        val txtEndereco: TextView = mView.findViewById(R.id.txtEndereco)
        val txtStatus: TextView = mView.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.custom_row, parent, false)

        return CustomViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val numOco = position + 1
        holder.txtOcorrencia.text = "Ocorrência nº $numOco"
        holder.txtEndereco.text = dataList[position].endereco
        holder.txtStatus.text = dataList[position].status

        holder.itemView.setOnClickListener{
            val intent = Intent(context, RegistroActivity::class.java)
            val registroId = dataList[position].registroId
            intent.putExtra("registroId", registroId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}