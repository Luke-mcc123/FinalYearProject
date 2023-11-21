package com.example.finalyearproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast

// Constants for database/column names
const val DATABASE_NAME = "FinalYearProject.SQLite"
const val TABLE_NAME = "Products"
const val COL_BARCODE = "Barcode"
const val COL_BRAND = "Brand"
const val COL_TYPE = "Type"
const val COL_NOTE = "Note"
const val COL_QUANTITY = "Quantity"
const val COL_ID = "Id"

// Class responsible for handling the database operations
class DataBaseHandler (private var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

        // SQL query to create table with columns
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_BARCODE INTEGER,$COL_BRAND VARCHAR(256),$COL_TYPE VARCHAR(256),$COL_NOTE VARCHAR(256),$COL_QUANTITY INTEGER)"

        // Execute SQL query
        db?.execSQL(createTable)
    }

    // Method called when the database is upgraded (not yet implemented)
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // Method to insert the data into the database
    fun insertData (product: Product){
        val db = this.writableDatabase
        var cv = ContentValues()

        // Assigning data to the content values
        cv.put(COL_BARCODE, product.barcode)
        cv.put(COL_BRAND, product.brand)
        cv.put(COL_TYPE, product.type)
        cv.put(COL_NOTE, product.note)
        cv.put(COL_QUANTITY, product.quantity)

        // Insert data into the table and show a Toast message based on the result
        var result = db.insert(TABLE_NAME, null, cv)
        if(result == -1.toLong())
            Toast.makeText(context,"Please Ensure All Required Fields Are Filled Out",Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context,"You Have Successfully Added " + product.brand + " " + product.type,Toast.LENGTH_SHORT).show()
    }

    // Method to read and return data from the database
    @SuppressLint("Range")
    fun readData() : MutableList<Product>{

        // Create an observable array of products
        var list: MutableList<Product> = ArrayList()

        // Get a readable database
        val db = this.readableDatabase

        // SQL query to select all data from the table
        val query = "Select * from $TABLE_NAME"

        // Execute the query and get the result
        val result = db.rawQuery(query, null)

        // Checks whether the query returned a result, if so,  iterate through the result and add products to the list
        if(result.moveToFirst()){
            do{
                var product = Product()

                // Get data from the result and add it into an instance of the product object
                product.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                product.barcode = result.getString(result.getColumnIndex(COL_BARCODE)).toInt()
                product.brand = result.getString(result.getColumnIndex(COL_BRAND))
                product.type = result.getString(result.getColumnIndex(COL_TYPE))
                product.note = result.getString(result.getColumnIndex(COL_NOTE))
                product.quantity = result.getString(result.getColumnIndex(COL_QUANTITY)).toInt()
                list.add(product)
            } while (result.moveToNext())
        }
        // Close the result and the database, then return the list
        result.close()
        db.close()
        return list
    }

    // Method to delete all data from the table
    fun deleteData(){
        // TODO("UPDATE FUNCTION TO DELETE ROW INSTEAD OF TABLE")
        val db = this.writableDatabase
        // Delete all data from the table
        db.delete(TABLE_NAME, null, null)
        // Close the database
        db.close()
    }

    // Method to update the quantity of all products in the table
    @SuppressLint("Range")
    fun updateData(){
        // TODO("UPDATE FUNCTION TO EDIT SPECIFIC ROW INSTEAD OF INCREMENT QUANTITY")
        val db = this.writableDatabase
        // SQL query to select all data from the table
        val query = "Select * from $TABLE_NAME"
        // Execute the query and get the result
        val result = db.rawQuery(query,null)
        // If there is data, iterate through the result and update the quantity
        if(result.moveToFirst()){
            do {
                var cv = ContentValues()
                // Increment the quantity value and update the row in the table
                cv.put(COL_QUANTITY,(result.getInt(result.getColumnIndex(COL_QUANTITY))+1))
                db.update(TABLE_NAME,cv,COL_ID + "=? AND " + COL_BRAND + "=?",
                    arrayOf(result.getString(result.getColumnIndex(COL_ID)),
                        result.getString(result.getColumnIndex(COL_BRAND))))
            }while (result.moveToNext())
        }
        // Close the result and the database
        result.close()
        db.close()
    }
}

