package com.vanesanunez.practicafinal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button

class Primer_capitulo : AppCompatActivity() {

//    private lateinit var rol_usuario: String
//
//    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var bvolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primer_capitulo)

//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        rol_usuario = sharedPreferences.getString("usuario", "cliente").toString()

        bvolver = findViewById(R.id.button_volver)

        bvolver.setOnClickListener {
            val activity = Intent(this, LibroCompleto::class.java)
            startActivity(activity)
        }
    }
}