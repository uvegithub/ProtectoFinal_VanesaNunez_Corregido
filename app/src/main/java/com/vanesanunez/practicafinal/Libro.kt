package com.vanesanunez.practicafinal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Libro(
    var id : String? = null,
    var titulo: String? = null,
    var autor: String? = null,
    var isbn: String? = null,
    var precio: Float? = null,
    var disponible:String? = null,
    var genero:String? = null,
    var sinopsis:String? = null,
    var imagen: String? = null,
    var estado_notificacion:Int? = null,
    var user_notificacion:String? = null,
): Parcelable
