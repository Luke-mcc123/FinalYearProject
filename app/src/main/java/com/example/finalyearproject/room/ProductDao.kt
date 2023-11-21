package com.example.finalyearproject.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


    @Dao
    interface ProductDao {
        @Query("SELECT * FROM product_table Order by brand ASC")
        fun getAll(): Flow<List<Product>>

        @Query("SELECT * FROM product_table WHERE id IN (:productIds)")
        fun loadAllByIds(productIds: IntArray): List<Product>

        @Query("SELECT * FROM product_table WHERE brand LIKE :brand LIMIT 1")
        fun findByBrand(brand: String): Product

        @Insert
        fun insertAll(vararg product: Product)

        @Delete
        fun delete(vararg product: Product)

        @Update
        fun updateProducts(vararg product: Product)
    }

