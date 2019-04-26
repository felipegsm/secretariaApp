package com.example.secretariaapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.database.FirebaseDatabase
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.view.View
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val databaseRegistro = FirebaseDatabase.getInstance()
    private val myRef = databaseRegistro.getReference("registros")

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val RECORD_REQUEST_CODE = 101
    private lateinit var imageUri: Uri

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEnviar: Button = findViewById(R.id.btnEnviar)
        val editEnd: EditText = findViewById(R.id.edtEndereco)
        val editDataHora: EditText = findViewById(R.id.edtDataHora)
        val editNome: EditText = findViewById(R.id.edtNome)
        val editEmail: EditText = findViewById(R.id.edtEmail)
        val imgRegistro: ImageView = findViewById(R.id.imgRegistro)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RECORD_REQUEST_CODE)
        }

        editEnd.keyListener = null
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val addOnSuccessListener = fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if(location != null){
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val enderecos: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    editEnd.text = Editable.Factory.getInstance().newEditable(enderecos[0].thoroughfare + ", " + enderecos[0].subThoroughfare + ". " + enderecos[0].subLocality)
                    Toast.makeText(this, editEnd.text, Toast.LENGTH_LONG)
                }

            }

        editDataHora.keyListener = null
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val currentDate = sdf.format(Date())

        editDataHora.text = Editable.Factory.getInstance().newEditable(currentDate)

        btnEnviar.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val endereco = editEnd.text.toString()
            val dataHora = editDataHora.text.toString()
            val nome = editNome.text.toString()
            val email = editEmail.text.toString()

            val bitmap = (imgRegistro.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data = baos.toByteArray()

            val timeStamp = SimpleDateFormat("ddMMyyyyHHmmss", Locale("pt", "BR")).format(Date())
            val imageFileName = "IMG_".plus(timeStamp.subSequence(0, 13))
            val anexoRef = storageRef.child("anexos/$imageFileName.jpg")
            val uploadTask = anexoRef.putBytes(data)

            uploadTask.addOnFailureListener {
                it.printStackTrace()

            }.addOnSuccessListener {
                anexoRef.downloadUrl.addOnSuccessListener {
                    val imgUrl = it.toString()
                    val id = myRef.push().key
                    val registro = Registro(id!!, endereco, dataHora, nome, email, imgUrl)

                    progressBar.visibility = View.GONE
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Sucesso!")
                        .setMessage("Reclamação enviada com sucesso")
                        .setPositiveButton("OK"){dialog, which ->
                            val intent: Intent = intent
                            finish()
                            startActivity(intent)
                        }
                    builder.show()

                    myRef.child(id).setValue(registro).addOnFailureListener {
                        it.printStackTrace()

                    }
                }
            }
        }

        imgRegistro.setOnClickListener{
            selectImage()
        }
    }

    private fun imgGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, 357)
    }

    private fun imgCamera(){
        val photo = createImageFile()
        imageUri = if(Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(this, "com.example.secretariaapp.provider", photo)
        }else{
            Uri.fromFile(photo)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 358)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imgRegistro: ImageView = findViewById(R.id.imgRegistro)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                357 -> {
                    val selectedImage = data!!.data
                    imgRegistro.setImageURI(selectedImage)
                }
                358 -> {
                    imgRegistro.setImageURI(imageUri)
                }
            }
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Tirar Foto", "Selecionar da Galeria", "Cancelar")
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Anexar Imagem")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Tirar Foto" -> {
                    imgCamera()
                }
                options[item] == "Selecionar da Galeria" -> {
                    imgGaleria()
                }
                options[item] == "Cancelar" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("ddMMyyyyHHmmss", Locale("pt", "BR")).format(Date())
        val imageFileName = "IMG_".plus(timeStamp.subSequence(0, 13))
        val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }
}


