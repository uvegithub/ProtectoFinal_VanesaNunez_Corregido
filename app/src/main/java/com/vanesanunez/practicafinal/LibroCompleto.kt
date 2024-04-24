package com.vanesanunez.practicafinal

import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibroCompleto : AppCompatActivity() {

    private lateinit var rol_usuario: String

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var imagen_cesta: ImageView

    private lateinit var db_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference

    private lateinit var titulo: TextView
    private lateinit var autor:TextView
    private lateinit var isbn:TextView
    private lateinit var precio:TextView
    private lateinit var disponible:TextView
    private lateinit var genero:TextView
    private lateinit var sinopsis:TextView
    private lateinit var imagen: ImageView
    private lateinit var beditar: ImageView
    private lateinit var bvolver: Button
    private lateinit var bcomprar: Button

    private var url_imagen: Uri? = null

    private lateinit var lista_libros: MutableList<Libro>

    private lateinit var job: Job

    private  lateinit var  pojo_libro:Libro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libro_completo)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        rol_usuario = sharedPreferences.getString("usuario", "cliente").toString()

        db_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()

        val this_activity = this
        job = Job()

        pojo_libro = intent.getParcelableExtra<Libro>("libros")!!

        titulo = findViewById(R.id.titulo)
        autor = findViewById(R.id.autor)
        isbn = findViewById(R.id.isbn)
        precio=findViewById(R.id.precio)
        disponible=findViewById(R.id.disponibilidad)
        genero=findViewById(R.id.categoria)
        sinopsis=findViewById(R.id.sinopsis)
        imagen=findViewById(R.id.item_miniatura)
        beditar=findViewById(R.id.editar)
        bcomprar=findViewById(R.id.comprar)
        beditar=findViewById(R.id.button_volver)

        //seguir como editar_carta


    }
}