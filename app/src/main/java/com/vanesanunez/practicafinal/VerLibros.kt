package com.vanesanunez.practicafinal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerLibros : AppCompatActivity() {

    private lateinit var rol_usuario: String

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var imagen_cesta: ImageView

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Libro>
    private lateinit var adaptador: LibroAdaptador
    private lateinit var db_ref: DatabaseReference

    private lateinit var spinner: Spinner

    private lateinit var busqueda: EditText
    private lateinit var boton_busqueda: Button

    private lateinit var libro_con_sinopsis: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_libros)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        rol_usuario = sharedPreferences.getString("usuario", "administrador").toString()

        imagen_cesta=findViewById(R.id.cesta)

//        libro_con_sinopsis.findViewById<TextView>(R.id.leer_sinopsis)

        if(rol_usuario=="cliente"){
            imagen_cesta.setVisibility(View.VISIBLE)
        }else{
            imagen_cesta.setVisibility(View.INVISIBLE)
        }

        imagen_cesta.setOnClickListener {
            val activity = Intent(applicationContext, MiCesta::class.java)
            startActivity(activity)
        }

        //esto va en el adaptador
//        libro_con_sinopsis.setOnClickListener {
//            val activity_completo = Intent(applicationContext, LibroCompleto::class.java)
//            startActivity(activity_completo)
//        }

        spinner= findViewById(R.id.filtro)

        val items = resources.getStringArray(R.array.spinner_items)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("libreria")
            .child("libros")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_libro = hijo?.getValue(Libro::class.java)
                        lista.add(pojo_libro!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = LibroAdaptador(lista)
        recycler = findViewById(R.id.lista_libros)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        recycler.setHasFixedSize(true)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val item_seleccionado = items[position]
                lista.filter { it.genero==item_seleccionado }
//                lista.retainAll { it.genero==item_seleccionado }

                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        busqueda=findViewById(R.id.textinputedittextNombre)
        boton_busqueda=findViewById(R.id.button)

        boton_busqueda.setOnClickListener {
            if(busqueda.toString()!=""){
                Log.v("lista", lista.toString())
                lista.retainAll{
//                    Log.v("tag",busqueda.toString())
//                    Log.v("tag", it.titulo.toString())
                    it.titulo!!.contains(busqueda.text.toString(), ignoreCase = true)
                }
                Log.v("lista", lista.toString())
                recycler.adapter?.notifyDataSetChanged()
            }
        }

    }

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
//            R.id.accion_ver_grafico -> {
//                val intent3 = Intent(this, VerGrafico::class.java)
//                startActivity(intent3)
//            }
        }
        return super.onOptionsItemSelected(item)
    }
}