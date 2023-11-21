package com.example.finalyearproject.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "product_table")
    data class Product(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "Barcode") val barcode: Int?,
        @ColumnInfo(name = "Brand") val brand: String?,
        @ColumnInfo(name = "Type") val type: String?,
        @ColumnInfo(name = "Description") val description: String?,
        @ColumnInfo(name = "Quantity") val quantity: Int?,
        @ColumnInfo(name = "Date") val sellByDate: String

        )
