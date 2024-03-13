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

class CrearLibro : AppCompatActivity(), CoroutineScope {

    private lateinit var titulo: TextInputEditText
    private lateinit var autor: TextInputEditText
    private lateinit var isbn: TextInputEditText
    private lateinit var precio: TextInputEditText
    private lateinit var disponibilidad: TextInputEditText
    private lateinit var genero: TextInputEditText
    private lateinit var puntos: TextInputEditText
    private lateinit var sinopsis: TextInputEditText
    private lateinit var imagen: ImageView
    private lateinit var bcrear: Button
    private lateinit var bvolver: Button

    private var url_libro: Uri? = null
    private lateinit var database_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference
    private lateinit var lista_libros: MutableList<Libro>

    private lateinit var job: Job

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rol_usuario: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_libro)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        rol_usuario = sharedPreferences.getString("usuario", "administrador").toString()

        val this_activity = this
        job = Job()

        titulo=findViewById(R.id.textinputedittextTitulo)
        autor=findViewById(R.id.textinputedittextAutor)
        isbn=findViewById(R.id.textinputedittextIBNS)
        precio=findViewById(R.id.textinputedittextPrecio)
        disponibilidad=findViewById(R.id.textinputedittextDisponibilidad)
        genero=findViewById(R.id.textinputedittextGenero)
        sinopsis=findViewById(R.id.textinputedittextSinopsis)
        puntos=findViewById(R.id.textinputedittextPuntos)
        bcrear=findViewById(R.id.button)
        bvolver=findViewById(R.id.button_volver)
        imagen=findViewById(R.id.imageView)

        database_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()
        lista_libros = Utilidades.obtenerListaLibros(database_ref)


        bcrear.setOnClickListener {

//            val dateTime = LocalDateTime.now()
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            if (titulo.text.toString().trim().isEmpty() ||
                autor.text.toString().trim().isEmpty() ||
                isbn.text.toString().trim().isEmpty() ||
                precio.text.toString().trim().isEmpty() ||
                disponibilidad.text.toString().trim().isEmpty() ||
                genero.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if (url_libro == null) {
                Toast.makeText(
                    applicationContext, "Falta seleccionar la " +
                            "portada", Toast.LENGTH_SHORT
                ).show()
            } else if (Utilidades.existeLibro(lista_libros, titulo.text.toString().trim())) {
                Toast.makeText(applicationContext, "Ese libro ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                var id_generado: String? = database_ref.child("libreria").child("libros").push().key

                //GlobalScope(Dispatchers.IO)
                launch {
                    val url_carta_firebase =
                        Utilidades.guardarImagen(storage_ref, id_generado!!, url_libro!!)

                    val androidId =
                        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                    Utilidades.escribirLibro(
                        database_ref, id_generado!!,
                        titulo.text.toString().trim(),
                        autor.text.toString().trim(),
                        isbn.text.toString().trim(),
                        precio.text.toString().trim().toFloat(),
                        disponibilidad.text.toString().trim(),
                        genero.text.toString().trim(),
                        sinopsis.text.toString().trim(),
                        puntos.text.toString().trim().toInt(),
                        url_carta_firebase,
                        Estado.CREADO,
                        androidId)

                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Libro creado con exito"
                    )

//                    sharedPreferences.edit().putString("id_libro", id_generado.toString().trim()).apply()

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
    { uri: Uri ->
        if (uri != null) {
            url_libro = uri
            imagen.setImageURI(uri)
        }
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
//        if(rol_usuario == "administrador"){
//            menuInflater.inflate(R.menu.menu_admin, menu)
//        }else{
//            menuInflater.inflate(R.menu.menu_user, menu)
//        }
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.accion_ver_cartas -> {
//                val intent = Intent(this, Ver_cartas::class.java)
//                startActivity(intent)
//            }
//            R.id.accion_crear_cartas -> {
//                val intent2 = Intent(this, Crear_carta::class.java)
//                startActivity(intent2)
//            }
////            R.id.accion_editar_cartas -> {
////                val intent3 = Intent(this, Editar_carta::class.java)
////                startActivity(intent3)
////            }
//            R.id.accion_aceptar_compra -> {
//                val intent3 = Intent(this, Mi_cesta::class.java)
//                startActivity(intent3)
//            }
//            R.id.accion_crear_evento -> {
//                val intent4 = Intent(this, CrearEvento::class.java)
//                startActivity(intent4)
//            }
//            R.id.accion_ver_eventos -> {
//                val intent5 = Intent(this, VerEventos::class.java)
//                startActivity(intent5)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}