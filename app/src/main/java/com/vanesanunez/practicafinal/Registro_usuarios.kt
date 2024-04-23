package com.vanesanunez.practicafinal

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Registro_usuarios : AppCompatActivity(), CoroutineScope {

    lateinit var user_layout: TextInputLayout
    lateinit var contrasena_layout: TextInputLayout
    lateinit var boton_ingresar: Button
    lateinit var user_edit2: TextInputEditText
    lateinit var contrasena_edit2: TextInputEditText

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var database_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference
    private lateinit var lista_clientes: MutableList<Usuario>

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuarios)

        user_layout=findViewById(R.id.textinputlayoutUsuario)
        contrasena_layout=findViewById(R.id.textinputlayoutContrasena)
        user_edit2=findViewById(R.id.textinputedittextusuario)
        contrasena_edit2=findViewById(R.id.textinputedittextcontrasena)
        boton_ingresar=findViewById(R.id.button)

        val this_activity = this
        job = Job()

        database_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()
        lista_clientes = Utilidades.obtenerListaClientes(database_ref)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        boton_ingresar.setOnClickListener {

//            val dateTime = LocalDateTime.now()
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            if (user_edit2.text.toString().trim().isEmpty() ||
                contrasena_edit2.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if (Utilidades.existeCliente(lista_clientes, user_edit2.text.toString().trim())) {
                Toast.makeText(applicationContext, "Ese usuario ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else if (contrasena_edit2.text.toString().trim().length < 6|| !contrasena_edit2.text.toString().contains("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])".toRegex())) {
                Toast.makeText(applicationContext, "La contrasena debe tener al menos 6 caracteres, un numero, una letra mayuscula y otra minuscula", Toast.LENGTH_SHORT)
                    .show()
            } else {

                var id_generado: String? = database_ref.child("tienda").child("usuarios").push().key

                //GlobalScope(Dispatchers.IO)
                launch {
                    val androidId =
                        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                    Utilidades.escribirCliente(
                        database_ref, id_generado!!,
                        user_edit2.text.toString().trim(),
                        contrasena_edit2.text.toString().trim(),
                        "cliente",
                        Estado.CREADO,
                        androidId)

                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Nuevo usuario registrado con exito"
                    )

                    sharedPreferences.edit().putString("usuario","cliente").apply()
                    sharedPreferences.edit().putString("login_"+user_edit2.text.toString().trim(),user_edit2.text.toString().trim()).apply()
                    sharedPreferences.edit().putString("password_"+user_edit2.text.toString().trim(), contrasena_edit2.text.toString().trim()).apply()
                    sharedPreferences.edit().putString("idusuario", id_generado.toString().trim()).apply()



                    val activity = Intent(applicationContext, VerLibros::class.java)
                    startActivity(activity)
                }







            }
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}