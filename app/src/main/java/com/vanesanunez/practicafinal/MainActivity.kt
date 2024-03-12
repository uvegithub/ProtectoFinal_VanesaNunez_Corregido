package com.vanesanunez.practicafinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var cambio_modo: Switch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val ref= FirebaseDatabase.getInstance().getReference()

//        ref.child("Hola").setValue("Hola desde Kotlin")

        cambio_modo=findViewById(R.id.switch_modo)

        cambio_modo.setOnClickListener {
            if(cambio_modo.isChecked){
                cambioModo(0)
            }else{
                cambioModo(1)
            }
        }

    }

    fun cambioModo(modo:Int){
        if(modo == 0){
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }
}