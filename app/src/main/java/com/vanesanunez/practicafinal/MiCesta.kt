package com.vanesanunez.practicafinal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class MiCesta : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<LibroComprado>
    private lateinit var adaptador: LibroCompradoAdaptador
    private lateinit var db_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference

    private lateinit var rol_usuario: String

    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_cesta)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        rol_usuario = sharedPreferences.getString("usuario", "cliente").toString()

        db_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()
//        lista_cartas_compradas = Utilidades.obtenerListaCartasReservadas(db_ref)

//        var id_generado: String? = db_ref.child("tienda").child("cartas_compradas").push().key
//        Utilidades.escribirCartaReservada(
//            db_ref, id_generado!!,
//            idcarta.text.toString().trim(),
//            precio.text.toString().trim().toFloat(),
//            disponibilidad.text.toString().trim(),
//            categoria.text.toString().trim(),
//            url_carta_firebase,
//            Estado.CREADO,
//            androidId)

        lista = mutableListOf()

        db_ref.child("libreria")
            .child("libros_comprados")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    GlobalScope.launch(Dispatchers.IO) {
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val pojo_libro_reservado = hijo?.getValue(LibroComprado::class.java)
                            //Log.d("IDDDDDDD", pojo_libro_reservado!!.id_usuario.toString() )

                            var semaforo = CountDownLatch(2)

                            db_ref.child("libreria").child("libros")
                                .child(pojo_libro_reservado!!.id_libro!!)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pojo_libro = snapshot.getValue(Libro::class.java)
                                        pojo_libro_reservado.imagen = pojo_libro!!.imagen
                                        semaforo.countDown()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })

                            db_ref.child("libreria").child("usuarios")
                                .child(pojo_libro_reservado!!.id_usuario!!)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pojo_user= snapshot.getValue(Usuario::class.java)
                                        //Log.d("TIENEEEE", pojo_libro_reservado.id_usuario.toString() )
                                        pojo_libro_reservado.id_usuario = pojo_user!!.login
                                        semaforo.countDown()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })


                            val pojo_libro_reservado2 = intent.getParcelableExtra<LibroComprado>("libro_comprado")
                            semaforo.await()
                            lista.add(pojo_libro_reservado!!)
                        }
                        runOnUiThread {
                            recycler.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = LibroCompradoAdaptador(lista, contentResolver)
        recycler = findViewById(R.id.recycler)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recycler.setHasFixedSize(true)



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
