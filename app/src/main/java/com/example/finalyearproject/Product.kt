package com.example.finalyearproject

import java.util.Date
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
class Product(
    var barcode: Long = 0,
    var brand: String = "",
    var description: String = "",
    var note: String = "",
    var quantity: Int = 0,
    var category: String = "",
    var date: Date? = null
) : Parcelable {
    fun isExpiryApproaching(days: Int): Boolean {
        date?.let {
            val currentDate = Calendar.getInstance()
            val expiryDate = Calendar.getInstance().apply { time = it }
            currentDate.add(Calendar.DAY_OF_YEAR, days)
            return expiryDate.before(currentDate)
        } ?: return false
    }

    fun isQuantityLow(stockThreshold: Int): Boolean {
        return quantity < stockThreshold
    }
}

