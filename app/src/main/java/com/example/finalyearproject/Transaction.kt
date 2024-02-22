package com.example.finalyearproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
class Transaction (
    var date: Date? = null,
    var quantityChange: Int = 0
) : Parcelable {

}

