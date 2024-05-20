package com.vanesanunez.practicafinal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LibroComprado (
    var id_reserva_libro : String? = null,
    var id_libro: String? = null,
    var id_usuario:String? = null,
    var estado:String? = null,
    var puntos:Int? = null,
    var estado_notificacion:Int? = null,
    var user_notificacion:String? = null,
    var imagen: String? = "",
): Parcelable