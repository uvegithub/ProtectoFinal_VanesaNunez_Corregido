package com.vanesanunez.practicafinal

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import androidx.swiperefreshlayout.widget.CircularProgressDrawable


class Utilidades {
    companion object{
        fun existeCliente(clientes : List<Usuario>, login:String):Boolean{
            return clientes.any{ it.login!!.lowercase()==login.lowercase()}
        }

        fun existeLibro(cartas : List<Libro>, nombre:String):Boolean{
            return cartas.any{ it.titulo!!.lowercase()==nombre.lowercase()}
        }
//
//        fun existeEvento(eventos : List<Evento>, nombre:String):Boolean{
//            return eventos.any{ it.nombre!!.lowercase()==nombre.lowercase()}
//        }

        fun obtenerListaClientes(database_ref: DatabaseReference):MutableList<Usuario>{
            var lista = mutableListOf<Usuario>()

            database_ref.child("libreria")
                .child("usuarios")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo : DataSnapshot ->
                            val pojo_usuario = hijo.getValue(Usuario::class.java)
                            lista.add(pojo_usuario!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun obtenerListaLibros(database_ref: DatabaseReference):MutableList<Libro>{
            var lista_libros = mutableListOf<Libro>()

            database_ref.child("libreria")
                .child("libros")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo : DataSnapshot ->
                            val pojo_libro = hijo.getValue(Libro::class.java)
                            lista_libros.add(pojo_libro!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista_libros
        }

//        fun obtenerListaEventos(database_ref: DatabaseReference):MutableList<Evento>{
//            var lista_eventos = mutableListOf<Evento>()
//
//            database_ref.child("tienda")
//                .child("eventos")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        snapshot.children.forEach{hijo : DataSnapshot ->
//                            val pojo_evento = hijo.getValue(Evento::class.java)
//                            lista_eventos.add(pojo_evento!!)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        println(error.message)
//                    }
//                })
//
//            return lista_eventos
//        }

        fun obtenerListaLibrosReservados(database_ref: DatabaseReference):MutableList<LibroComprado>{
            var lista_libros = mutableListOf<LibroComprado>()

            database_ref.child("libreria")
                .child("libros_reservados")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo : DataSnapshot ->
                            val pojo_libro_comprado = hijo.getValue(LibroComprado::class.java)
                            lista_libros.add(pojo_libro_comprado!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista_libros
        }

        fun escribirCliente(db_ref: DatabaseReference, id: String, login:String, password:String, tipo:String, estado_notificacion: Int, user_notificacion: String)=
            db_ref.child("libreria").child("usuarios").child(id).setValue(Usuario(
                id,
                login,
                password,
                tipo,
                estado_notificacion,
                user_notificacion,
            ))

        fun escribirLibroReservado(db_ref: DatabaseReference, id: String, id_libro:String, id_usuario:String, estado_libro:String, puntos: Int, estado_notificacion: Int, user_notificacion: String, url_firebase:String)=
            db_ref.child("libreria").child("libros_comprados").child(id).setValue(LibroComprado(
                id,
                id_libro,
                id_usuario,
                estado_libro,
                puntos,
                estado_notificacion,
                user_notificacion,
                url_firebase
            ))

        fun escribirLibro(db_ref: DatabaseReference, id: String, titulo:String, autor:String, isbn:String, precio:Float, disponibilidad:String, genero:String, sinopsis:String, puntos:Int, url_firebase:String, estado_notificacion: Int, user_notificacion: String)=
            db_ref.child("libreria").child("libros").child(id).setValue(Libro(
                id,
                titulo,
                autor,
                isbn,
                precio,
                disponibilidad,
                genero,
                sinopsis,
                puntos,
                url_firebase,
                estado_notificacion,
                user_notificacion,
            ))

//        fun escribirEvento(db_ref: DatabaseReference, id: String, nombre:String, precio:Float, aforoMax:Int, aforoOcu:Int, estado_notificacion: Int, user_notificacion: String)=
//            db_ref.child("tienda").child("eventos").child(id).setValue(Evento(
//                id,
//                nombre,
//                precio,
//                aforoMax,
//                aforoOcu,
//                estado_notificacion,
//                user_notificacion,
//            ))

        suspend fun guardarImagen(sto_ref: StorageReference, id:String, imagen: Uri):String{
            lateinit var url_libro_firebase: Uri

            url_libro_firebase=sto_ref.child("libreria").child("libros").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_libro_firebase.toString()
        }

        suspend fun guardarImagenReservada(sto_ref: StorageReference, id:String, imagen: Uri):String{
            lateinit var url_carta_firebase: Uri

            url_carta_firebase=sto_ref.child("tienda").child("cartas_reservadas").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_carta_firebase.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.portada_libro)
                .error(R.drawable.error)
            return options
        }
    }
}