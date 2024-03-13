package com.vanesanunez.practicafinal

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_libros)
    }
}