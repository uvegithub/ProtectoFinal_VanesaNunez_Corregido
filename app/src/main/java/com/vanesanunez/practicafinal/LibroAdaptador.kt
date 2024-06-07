package com.vanesanunez.practicafinal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibroAdaptador (var lista_libros: MutableList<Libro>):
    RecyclerView.Adapter<LibroAdaptador.LibroViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_libros

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rol_usuario: String

    var canalId:Int = 0

    private lateinit var db_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference
    private var url_libro: Uri? = null
    private lateinit var job: Job

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibroAdaptador.LibroViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_libro,parent,false)
        contexto = parent.context
        return LibroViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: LibroAdaptador.LibroViewHolder, position: Int) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contexto)


        val this_activity = this
        job = Job()

        val item_actual = lista_filtrada[position]
        holder.titulo.text = item_actual.titulo
        holder.autor.text = item_actual.autor
        holder.isbn.text = item_actual.isbn
        holder.precio.text = item_actual.precio.toString()
        holder.genero.text = item_actual.genero
        holder.disponibilidad.text = item_actual.disponible
        holder.puntos.text = item_actual.disponible
        holder.leer_sinopsis.text = "Leer sinopsis"

        var euro=item_actual.precio
        var dolar:Float = euro!!.toFloat() * 1.07f

        if(holder.dolares.isChecked){
            holder.precio.text = dolar.toString()
        }

        val URL:String? = when(item_actual.imagen){
            ""-> null
            else -> item_actual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.miniatura)


        rol_usuario = sharedPreferences.getString("usuario", "administrador").toString()

        if(rol_usuario=="cliente"){
            holder.imagen_comprar.setVisibility(View.VISIBLE)
            holder.editar.setVisibility(View.INVISIBLE)
        }else{
            holder.imagen_comprar.setVisibility(View.INVISIBLE)
            holder.editar.setVisibility(View.VISIBLE)
        }

        holder.editar.setOnClickListener {
            val activity = Intent(contexto,EditarLibro::class.java)
            activity.putExtra("libros", item_actual)
            contexto.startActivity(activity)
        }

        holder.leer_sinopsis.setOnClickListener {
            val activity = Intent(contexto, LibroCompleto::class.java)
            activity.putExtra("libros", item_actual)
            contexto.startActivity(activity)
        }

        holder.imagen_comprar.setOnClickListener {
            if(holder.disponibilidad.text!="No disponible"){
                Log.d("IF", "DISPONIBLE")
                holder.disponibilidad.text = "No disponible"

                mostrar_notificacion(contexto)
                    val activity = Intent(contexto,MiCesta::class.java)
                   activity.putExtra("libro_comprado", item_actual)
                sharedPreferences.edit().putString("disponibilidad","No disponible").apply()


                db_ref = FirebaseDatabase.getInstance().getReference()
                storage_ref = FirebaseStorage.getInstance().getReference()

                var id_generado: String? = db_ref.child("libreria").child("libros_comprados").push().key


                var id_libro = sharedPreferences.getString("id_libro", "1").toString()
                var id_usuario = sharedPreferences.getString("idusuario", "1").toString()
                //Log.d("HOLAAAA", id_usuario.toString())
                var estado = "Pendiente"
                var puntos = sharedPreferences.getString("estrellas", "1").toString()


                sharedPreferences.edit().putString("id_libro_reservado", id_generado.toString().trim()).apply()

                val androidId =
                    Settings.Secure.getString(contexto.contentResolver, Settings.Secure.ANDROID_ID)

                GlobalScope.launch(Dispatchers.IO) {
                    val url_libro_firebase =
                        Utilidades.guardarImagenReservada(storage_ref, id_generado!!, url_libro!!)

                    Utilidades.escribirLibroReservado(
                        db_ref, id_generado!!,
                        id_libro!!,
                        id_usuario,
                        estado,
                        puntos.trim().toInt(),
                        Estado.CREADO,
                        androidId,
                        url_libro_firebase
                    )
                }

            }else{
                Log.d("ELSE", "NO DISPONIBLE")
            }

        }

    }

    override fun getItemCount(): Int = lista_filtrada.size

    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val titulo: TextView = itemView.findViewById(R.id.titulo)
        val autor: TextView = itemView.findViewById(R.id.autor)
        val isbn: TextView = itemView.findViewById(R.id.isbn)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val genero: TextView = itemView.findViewById(R.id.genero)
        val disponibilidad: TextView = itemView.findViewById(R.id.disponibilidad)
        var puntos: TextView = itemView.findViewById(R.id.puntos)
        val editar: ImageView = itemView.findViewById(R.id.editar)

        val leer_sinopsis: TextView = itemView.findViewById(R.id.leer_sinopsis)

        var imagen_comprar: ImageView = itemView.findViewById(R.id.comprar)

        var dolares: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    override fun getFilter(): Filter {
        return  object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()){
                    lista_filtrada = lista_libros
                }else {
                    lista_filtrada = (lista_libros.filter {
                        it.titulo.toString().lowercase().contains(busqueda)
                    }) as MutableList<Libro>
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }

    fun mostrar_notificacion(contexto: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            canalId += 1
            var canalNombre = "canal" + canalId.toString()
            val notificationChannel = NotificationChannel(canalId.toString(), canalNombre, NotificationManager.IMPORTANCE_DEFAULT)
            contexto.getSystemService<NotificationManager>()?.createNotificationChannel(notificationChannel)
        }

        val intento_boton = Intent(contexto, MiCesta::class.java)
        val pendingIntent_boton = PendingIntent.getActivity(contexto, 0, intento_boton, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(contexto, canalId.toString())
            .setSmallIcon(R.drawable.notification_icon_124899)
            .setContentTitle("Notificacion")
            .setContentText("Has comprado el libro. Mi id es "+canalId)
            .addAction(R.drawable.notification_icon_124899,"Ir a mi cesta", pendingIntent_boton)

        with(contexto.getSystemService<NotificationManager>()){
            this?.notify(1,builder.build())
        }
    }
}