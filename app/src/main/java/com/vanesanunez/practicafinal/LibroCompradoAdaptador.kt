package com.vanesanunez.practicafinal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class LibroCompradoAdaptador(
    var lista_libros_reservados: MutableList<LibroComprado>,
    var contentResolver: ContentResolver
):
    RecyclerView.Adapter<LibroCompradoAdaptador.LibroReservadoViewHolder>(), Filterable,
    CoroutineScope {
    private lateinit var contexto: Context

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rol_usuario: String

    private lateinit var db_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference

    var canalId:Int = 0

    private var url_carta: Uri? = null

    private var lista_filtrada = lista_libros_reservados

//    private lateinit var puntuacion: RatingBar

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibroCompradoAdaptador.LibroReservadoViewHolder {
        val vista_item =
            LayoutInflater.from(parent.context).inflate(R.layout.item_libro_comprado, parent, false)
        contexto = parent.context
        return LibroCompradoAdaptador.LibroReservadoViewHolder(vista_item)
    }

    override fun onBindViewHolder(
        holder: LibroCompradoAdaptador.LibroReservadoViewHolder,
        position: Int
    ) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contexto)

        rol_usuario = sharedPreferences.getString("usuario", "administrador").toString()

        if (rol_usuario == "cliente") {
            holder.accept.setVisibility(View.INVISIBLE)
        } else {
            holder.accept.setVisibility(View.VISIBLE)
        }


        holder.accept.setOnClickListener {
            if (holder.estado_c.text != "Preparado") {
                mostrar_notificacion(contexto)

                sharedPreferences.edit().putString("estado", "Preparado").apply()

                val item_actual = lista_filtrada[position]
                holder.libro.text = sharedPreferences.getString("id_carta", "1c").toString()
                holder.user.text = sharedPreferences.getString("idusuario", "1u").toString()
                holder.estado_c.text = "Preparado"

                val URL: String? = when (item_actual.imagen) {
                    "" -> null
                    else -> item_actual.imagen
                }

                Glide.with(contexto)
                    .load(URL)
                    .apply(Utilidades.opcionesGlide(contexto))
                    .transition(Utilidades.transicion)
                    .into(holder.miniatura)

                val activity2 = Intent(contexto, MiCesta::class.java)
                activity2.putExtra("libro_comprado", item_actual)

            }

        }

    }

    override fun getItemCount(): Int = lista_filtrada.size

    class LibroReservadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.imagen_defecto)
        val libro: TextView = itemView.findViewById(R.id.carta_reservada)
        val user: TextView = itemView.findViewById(R.id.user_comprador)
        val estado_c: TextView = itemView.findViewById(R.id.estado)
        val puntuacion: RatingBar = itemView.findViewById(R.id.ratingBar)
        val accept: ImageView = itemView.findViewById(R.id.icono_aceptar)
    }

    fun mostrar_notificacion(contexto: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            canalId += 1
            var canalNombre = "canal" + canalId.toString()
            val notificationChannel = NotificationChannel(canalId.toString(), canalNombre, NotificationManager.IMPORTANCE_DEFAULT)
            contexto.getSystemService<NotificationManager>()?.createNotificationChannel(notificationChannel)
        }

//        val intento = Intent(contexto, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(contexto, 0, intento, PendingIntent.FLAG_UPDATE_CURRENT)

        val intento_boton = Intent(contexto, VerLibros::class.java)
        val pendingIntent_boton = PendingIntent.getActivity(contexto, 0, intento_boton, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(contexto, canalId.toString())
            .setSmallIcon(R.drawable.notification_icon_124899)
            .setContentTitle("Notificacion")
            .setContentText("El pedido ya está preparado. Mi id es "+canalId)
//            .setContentIntent(pendingIntent)
            .addAction(R.drawable.notification_icon_124899,"Ir al catalogo", pendingIntent_boton)

        with(contexto.getSystemService<NotificationManager>()){
            this?.notify(1,builder.build())
        }
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
}