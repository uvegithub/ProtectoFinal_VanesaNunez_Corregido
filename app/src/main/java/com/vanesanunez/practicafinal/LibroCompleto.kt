package com.vanesanunez.practicafinal

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LibroCompleto : AppCompatActivity(), CoroutineScope {

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
    private lateinit var bcomprar: ImageView

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
        bvolver=findViewById(R.id.button_volver)

        lista_libros = Utilidades.obtenerListaLibros(db_ref)

        Glide.with(applicationContext)
            .load(pojo_libro.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(imagen)

        if(rol_usuario=="cliente"){
            beditar.setVisibility(View.INVISIBLE)
            bcomprar.setVisibility(View.VISIBLE)
        }else{
            beditar.setVisibility(View.VISIBLE)
            bcomprar.setVisibility(View.INVISIBLE)
        }

        //GlobalScope(Dispatchers.IO)
        var url_imagen_firebase = String()
        launch {
            if(url_imagen == null){
                url_imagen_firebase = pojo_libro.imagen!!
            }else{
                val url_imagen_firebase =
                    Utilidades.guardarImagen(storage_ref, pojo_libro.id!!, url_imagen!!)
            }

            val androidId =
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }

        bvolver.setOnClickListener {
            val activity = Intent(applicationContext, VerLibros::class.java)
            startActivity(activity)
        }

        imagen.setOnClickListener {
            //accesoGaleria.launch("image/*")
            val activity = Intent(this, Primer_capitulo::class.java)
            startActivity(activity)
        }
    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri ->
        if(uri!=null){
            url_imagen = uri
            imagen.setImageURI(uri)
        }
    }
    override val coroutineContext: CoroutineContext
         get() = Dispatchers.IO + job

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if(rol_usuario == "administrador"){
            menuInflater.inflate(R.menu.menu_admin, menu)
        }else{
            menuInflater.inflate(R.menu.menu_user, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.accion_ver_libros -> {
                val intent = Intent(this, VerLibros::class.java)
                startActivity(intent)
            }
            R.id.accion_crear_libro -> {
                val intent2 = Intent(this, CrearLibro::class.java)
                startActivity(intent2)
            }
//            R.id.accion_editar_libro -> {
//                val intent3 = Intent(this, EditarLibro::class.java)
//                startActivity(intent3)
//            }
//            R.id.accion_aceptar_compra -> {
//                val intent4 = Intent(this, MiCesta::class.java)
//                startActivity(intent4)
//            }
            R.id.accion_crear_evento -> {
                val intent3 = Intent(this, CrearEvento::class.java)
                startActivity(intent3)
            }
            R.id.accion_ver_eventos -> {
                val intent4 = Intent(this, VerEvento::class.java)
                startActivity(intent4)
            }
//            R.id.accion_ver_grafico -> {
//                val intent3 = Intent(this, VerGrafico::class.java)
//                startActivity(intent3)
//            }
        }
        return super.onOptionsItemSelected(item)
    }
}