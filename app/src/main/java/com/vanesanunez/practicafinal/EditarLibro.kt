package com.vanesanunez.practicafinal

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditarLibro : AppCompatActivity(), CoroutineScope {

    private lateinit var titulo: TextInputEditText
    private lateinit var autor: TextInputEditText
    private lateinit var isbn: TextInputEditText
    private lateinit var precio: TextInputEditText
    private lateinit var disponibilidad: TextInputEditText
    private lateinit var genero: TextInputEditText
    private lateinit var puntos: TextInputEditText
    private lateinit var sinopsis: TextInputEditText
    private lateinit var imagen: ImageView
    private lateinit var beditar: Button
    private lateinit var bvolver: Button

    private var url_imagen: Uri? = null
    private lateinit var database_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference
    private lateinit var lista_libros: MutableList<Libro>

    private lateinit var job: Job

    private lateinit var pojo_libro: Libro

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rol_usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_libro)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        rol_usuario = sharedPreferences.getString("usuario", "administrador").toString()

        val this_activity = this
        job = Job()

        pojo_libro = intent.getParcelableExtra<Libro>("libros")!!

        titulo=findViewById(R.id.textinputedittextTitulo)
        autor=findViewById(R.id.textinputedittextAutor)
        isbn=findViewById(R.id.textinputedittextIBNS)
        precio=findViewById(R.id.textinputedittextPrecio)
        genero=findViewById(R.id.textinputedittextGenero)
        disponibilidad=findViewById(R.id.textinputedittextDisponibilidad)
        sinopsis=findViewById(R.id.textinputedittextSinopsis)
        puntos=findViewById(R.id.textinputedittextPuntos)
        beditar=findViewById(R.id.button)
        bvolver=findViewById(R.id.button_volver)
        imagen=findViewById(R.id.imageView)

        titulo.setText(pojo_libro.titulo)
        autor.setText(pojo_libro.autor)
        isbn.setText(pojo_libro.isbn)
        precio.setText(pojo_libro.precio.toString())
        genero.setText(pojo_libro.genero)
        disponibilidad.setText(pojo_libro.disponible)
        sinopsis.setText(pojo_libro.sinopsis)
        puntos.setText(pojo_libro.puntos.toString())

        database_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()
        lista_libros = Utilidades.obtenerListaLibros(database_ref)

        Glide.with(applicationContext)
            .load(pojo_libro.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(imagen)

        beditar.setOnClickListener {

//            val dateTime = LocalDateTime.now()
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            if (titulo.text.toString().trim().isEmpty() ||
                autor.text.toString().trim().isEmpty() ||
                isbn.text.toString().trim().isEmpty() ||
                precio.text.toString().trim().isEmpty() ||
                genero.text.toString().trim().isEmpty() ||
                disponibilidad.text.toString().trim().isEmpty() ||
                sinopsis.text.toString().trim().isEmpty() ||
                puntos.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if ( !titulo.text.toString().trim().equals(pojo_libro.titulo) && Utilidades.existeLibro(lista_libros, titulo.text.toString().trim())) {
                Toast.makeText(applicationContext, "Esa libro ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

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

                    Utilidades.escribirLibro(
                        database_ref, pojo_libro.id!!,
                        titulo.text.toString().trim(),
                        autor.text.toString().trim(),
                        isbn.text.toString().trim(),
                        precio.text.toString().trim().toFloat(),
                        genero.text.toString().trim(),
                        disponibilidad.text.toString().trim(),
                        sinopsis.text.toString().trim(),
                        puntos.text.toString().trim().toInt(),
                        url_imagen_firebase,
                        Estado.MODIFICADO,
                        androidId
                    )
                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Libro modificado con exito"
                    )
                    val activity = Intent(applicationContext, VerLibros::class.java)
                    startActivity(activity)
                }
            }

        }

        bvolver.setOnClickListener {
            val activity = Intent(applicationContext, VerLibros::class.java)
            startActivity(activity)
        }

        imagen.setOnClickListener {
            accesoGaleria.launch("image/*")
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