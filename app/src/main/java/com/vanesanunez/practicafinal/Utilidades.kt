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

class Utilidades {
    companion object{
        fun existeCliente(clientes : List<Usuario>, login:String):Boolean{
            return clientes.any{ it.login!!.lowercase()==login.lowercase()}
        }

//        fun existeCarta(cartas : List<Carta>, nombre:String):Boolean{
//            return cartas.any{ it.nombre!!.lowercase()==nombre.lowercase()}
//        }
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

//        fun obtenerListaCartas(database_ref: DatabaseReference):MutableList<Carta>{
//            var lista_cartas = mutableListOf<Carta>()
//
//            database_ref.child("tienda")
//                .child("cartas")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        snapshot.children.forEach{hijo : DataSnapshot ->
//                            val pojo_carta = hijo.getValue(Carta::class.java)
//                            lista_cartas.add(pojo_carta!!)
//                        }
//                    }

//                    override fun onCancelled(error: DatabaseError) {
//                        println(error.message)
//                    }
//                })

//            return lista_cartas
//        }

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

//        fun obtenerListaCartasReservadas(database_ref: DatabaseReference):MutableList<CartaReservada>{
//            var lista_cartas = mutableListOf<CartaReservada>()
//
//            database_ref.child("tienda")
//                .child("cartas_reservadas")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        snapshot.children.forEach{hijo : DataSnapshot ->
//                            val pojo_carta_reservada = hijo.getValue(CartaReservada::class.java)
//                            lista_cartas.add(pojo_carta_reservada!!)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        println(error.message)
//                    }
//                })
//
//            return lista_cartas
//        }

        fun escribirCliente(db_ref: DatabaseReference, id: String, login:String, password:String, tipo:String, estado_notificacion: Int, user_notificacion: String)=
            db_ref.child("libreria").child("usuarios").child(id).setValue(Usuario(
                id,
                login,
                password,
                tipo,
                estado_notificacion,
                user_notificacion,
            ))

//        fun escribirCartaReservada(db_ref: DatabaseReference, id: String, idcarta:String, idusuario:String, estado_c:String, estado_notificacion: Int, user_notificacion: String)=
//            db_ref.child("tienda").child("cartas_compradas").child(id).setValue(CartaReservada(
//                id,
//                idcarta,
//                idusuario,
//                estado_c,
//                estado_notificacion,
//                user_notificacion,
//            ))

//        fun escribirCarta(db_ref: DatabaseReference, id: String, nombre:String, precio:Float, disponibilidad:String, categoria:String, url_firebase:String, estado_notificacion: Int, user_notificacion: String)=
//            db_ref.child("tienda").child("cartas").child(id).setValue(Carta(
//                id,
//                nombre,
//                precio,
//                disponibilidad,
//                categoria,
//                url_firebase,
//                estado_notificacion,
//                user_notificacion,
//            ))

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

//        suspend fun guardarImagen(sto_ref: StorageReference, id:String, imagen: Uri):String{
//            lateinit var url_carta_firebase: Uri
//
//            url_carta_firebase=sto_ref.child("tienda").child("cartas").child(id)
//                .putFile(imagen).await().storage.downloadUrl.await()
//
//            return url_carta_firebase.toString()
//        }

//        suspend fun guardarImagenReservada(sto_ref: StorageReference, id:String, imagen: Uri):String{
//            lateinit var url_carta_firebase: Uri
//
//            url_carta_firebase=sto_ref.child("tienda").child("cartas_reservadas").child(id)
//                .putFile(imagen).await().storage.downloadUrl.await()
//
//            return url_carta_firebase.toString()
//        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

//        fun animacion_carga(contexto: Context): CircularProgressDrawable {
//            val animacion = CircularProgressDrawable(contexto)
//            animacion.strokeWidth = 5f
//            animacion.centerRadius = 30f
//            animacion.start()
//            return animacion
//        }


//        val transicion = DrawableTransitionOptions.withCrossFade(500)
//
//        fun opcionesGlide(context: Context): RequestOptions {
//            val options = RequestOptions()
//                .placeholder(animacion_carga(context))
//                .fallback(R.drawable.cm1)
//                .error(R.drawable.error)
//            return options
//        }
    }
}