package com.vanesanunez.practicafinal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(
    var id : String? = null,
    var login: String? = null,
    var password: String? = null,
    var tipo:String? = null,
    var estado_notificacion:Int? = null,
    var user_notificacion:String? = null,
): Parcelable
