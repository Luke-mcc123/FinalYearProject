package com.example.finalyearproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.Date

/**
 * Code Adapted from https://github.com/kmvignesh/SqliteExample
 */

// Constants for database/column names
const val DATABASE_NAME = "FinalYearProject.SQLite"
const val COL_ID = "Id"

// Product Table Values
const val TABLE_PRODUCTS = "Products"
const val COL_BARCODE = "Barcode"
const val COL_BRAND = "Brand"
const val COL_DESCRIPTION = "Description"
const val COL_NOTE = "Note"
const val COL_QUANTITY = "Quantity"
const val COL_CATEGORY = "Category"
const val COL_DATE = "Date"

// Transaction Table Values
const val TABLE_TRANSACTIONS = "Transactions"
const val COL_TRANSACTION_ID = "Id"
const val COL_TRANSACTION_DATE = "Date"
const val COL_QUANTITY_CHANGE = "QuantityChange"
const val COL_PRODUCT_BARCODE = "ProductBarcode"


// User Table Values
const val TABLE_USERS = "Users"
const val COL_USERNAME = "Username"
const val COL_PASSWORD = "Password"
const val COL_ROLE = "Role"

// Class responsible for handling the database operations
class DataBaseHandler(private var context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {

        // Creating the Products Table
        val createProductsTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_PRODUCTS ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_BARCODE INTEGER,$COL_BRAND VARCHAR(256),$COL_DESCRIPTION VARCHAR(256),$COL_NOTE VARCHAR(256),$COL_QUANTITY INTEGER,$COL_CATEGORY VARCHAR(256),$COL_DATE INTEGER)"

        // Execute SQL query
        db?.execSQL(createProductsTable)

        // Creating the Transaction Table
        val createTransactionTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_TRANSACTIONS ($COL_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_TRANSACTION_DATE INTEGER, $COL_QUANTITY_CHANGE INTEGER, $COL_PRODUCT_BARCODE INTEGER, FOREIGN KEY ($COL_PRODUCT_BARCODE) REFERENCES $TABLE_PRODUCTS($COL_BARCODE))"

        //Execute SQL Query
        db?.execSQL(createTransactionTable)

        //Creating the User Table
        val createUserTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_USERS ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_USERNAME VARCHAR(256),$COL_PASSWORD VARCHAR(256),$COL_ROLE VARCHAR(256))"

        // Execute SQL query
        db?.execSQL(createUserTable)
    }

    // Method called when the database is upgraded (not yet implemented)
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Allow for incremental database upgrades
        var upgradeVersion = oldVersion

        if(upgradeVersion == 1){
            // Create the transactions table
            val createTransactionTable =
                "CREATE TABLE IF NOT EXISTS $TABLE_TRANSACTIONS ($COL_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_TRANSACTION_DATE INTEGER, $COL_QUANTITY_CHANGE INTEGER, $COL_PRODUCT_BARCODE INTEGER, FOREIGN KEY ($COL_PRODUCT_BARCODE) REFERENCES $TABLE_PRODUCTS($COL_BARCODE))"

            //Execute SQL Query
            db?.execSQL(createTransactionTable)

            //Increment version number
            upgradeVersion = 2
        }
    }

    /*---------------------------- PRODUCT FUNCTIONS -------------------------------*/

    // Update Specific Product (Edit)
    // Takes inputted barcode and finds related entry and updates the row with the new data
    fun updateProductData(product: Product) {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_BARCODE, product.barcode)
        cv.put(COL_BRAND, product.brand)
        cv.put(COL_DESCRIPTION, product.description)
        cv.put(COL_NOTE, product.note)
        cv.put(COL_QUANTITY, product.quantity)
        cv.put(COL_CATEGORY, product.category)
        cv.put(COL_DATE, product.date?.time)


        db.update(TABLE_PRODUCTS, cv, "barcode =" + product.barcode, null)
    }

    // Delete Specific Product Data
    // Takes inputted barcode and deletes the associated row
    fun deleteProductData(product: Product) {
        val db = this.writableDatabase
        db.delete(TABLE_PRODUCTS, "$COL_BARCODE = ?", arrayOf(product.barcode.toString()))
        db.close()
    }


    // Insert Product Data
    fun insertData(product: Product) {
        val db = this.writableDatabase
        var cv = ContentValues()

        // Assigning data to the content values
        cv.put(COL_BARCODE, product.barcode)
        cv.put(COL_BRAND, product.brand)
        cv.put(COL_DESCRIPTION, product.description)
        cv.put(COL_NOTE, product.note)
        cv.put(COL_QUANTITY, product.quantity)
        cv.put(COL_CATEGORY, product.category)
        cv.put(COL_DATE, product.date?.time)

        // Insert data into the table and show a Toast message based on the result
        var result = db.insert(TABLE_PRODUCTS, null, cv)
        if (result == -1.toLong())
            Toast.makeText(
                context,
                "Please Ensure All Required Fields Are Filled Out",
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(
                context,
                "You Have Successfully Added " + product.brand + " " + product.description,
                Toast.LENGTH_SHORT
            ).show()
    }


    // Read and List All Product Information
    @SuppressLint("Range")
    fun readData(): MutableList<Product> {
        val list: MutableList<Product> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLE_PRODUCTS"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val product = Product()
                product.barcode = result.getString(result.getColumnIndex(COL_BARCODE)).toLong()
                product.brand = result.getString(result.getColumnIndex(COL_BRAND))
                product.description = result.getString(result.getColumnIndex(COL_DESCRIPTION))
                product.note = result.getString(result.getColumnIndex(COL_NOTE))
                product.category = result.getString(result.getColumnIndex(COL_CATEGORY))
                product.quantity = result.getString(result.getColumnIndex(COL_QUANTITY)).toInt()

                // Handle date conversion
                val dateString = result.getLong(result.getColumnIndex(COL_DATE))

                // Convert Unix timestamp to Date
                if (dateString != null) {
                    val date = Date(dateString)
                    product.date = date
                }
                list.add(product)
            } while (result.moveToNext())
        }

        result.close()
        db.close()

        return list
    }

    // Read Specific Product
    @SuppressLint("Range")
    fun getProductByBarcode(barcode: Long): Product?{
        val db= this.readableDatabase
        val query = "SELECT * FROM $TABLE_PRODUCTS WHERE $COL_BARCODE = ?"
        val selectionArgs = arrayOf(barcode.toString())

        val result = db.rawQuery(query, selectionArgs)
        var product: Product? = null

        if(result.moveToFirst()){
            product = Product()
            product.barcode = result.getString(result.getColumnIndex(COL_BARCODE)).toLong()
            product.brand = result.getString(result.getColumnIndex(COL_BRAND))
            product.description = result.getString(result.getColumnIndex(COL_DESCRIPTION))
            product.note = result.getString(result.getColumnIndex(COL_NOTE))
            product.category = result.getString(result.getColumnIndex(COL_CATEGORY))
            product.quantity = result.getString(result.getColumnIndex(COL_QUANTITY)).toInt()
            // Handle date conversion
            val dateString = result.getLong(result.getColumnIndex(COL_DATE))

            // Convert Unix timestamp to Date
            if (dateString != null) {
                val date = Date(dateString)
                product.date = date
            }

        }
        result.close()
        db.close()

        return product
    }

    /* ----------------------------------- USER FUNCTIONS -------------------------------------- */
    // Method to create a new user
    fun insertUserData(user: User) {
        val db = this.writableDatabase
        var cv = ContentValues()

        // Assigning data to content values
        cv.put(COL_USERNAME, user.username)
        cv.put(COL_PASSWORD, user.password)
        cv.put(COL_ROLE, user.role)

        // Insert data into the table and show a toast message based on the result
        var result = db.insert(TABLE_USERS, null, cv)
        if (result == -1.toLong())
            Toast.makeText(
                context,
                "Please Ensure All Required Fields Are Filled Out",
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(
                context,
                "You Have Successfully Added " + user.username + " As A New User",
                Toast.LENGTH_SHORT
            ).show()
    }

    // Method to get all data and order by username
    // No longer used, TESTING PURPOSES ONLY
    // Can be adapted to just show username and role for administrative purposes
    @SuppressLint("Range")
    fun readUserData(): MutableList<User> {
        // Create an observable array of users
        var list: MutableList<User> = ArrayList()

        // Get a readable database
        val db = this.readableDatabase

        // SQL query to select all data from the table
        val query = "Select * from $TABLE_USERS ORDER BY $COL_USERNAME ASC"

        // Execute the query and get the result
        val result = db.rawQuery(query, null)
        // Checks whether the query returned a result, if so,  iterate through the result and add usernames to the list
        if (result.moveToFirst()) {
            do {
                var user = User()
                // Get data from the result and add it into an instance of the product object
                user.username = result.getString(result.getColumnIndex(COL_USERNAME))
                list.add(user)
            } while (result.moveToNext())
        }
        // Close the result and the database, then return the list
        result.close()
        db.close()
        return list
    }

    // Checks user credentials
    // Takes values as pairs to check against one another
    fun checkUserCredentials(username: String, password: String): Boolean {
        val db = this.readableDatabase

        // Creates an array of what columns to use for the query
        val columns = arrayOf("username", "password")

        // 'WHERE' criteria
        val selection = "username = ? AND password = ?"

        // Values for WHERE
        val selectionArgs = arrayOf(username, password)

        // Execute query
        val cursor = db.query("Users", columns, selection, selectionArgs, null, null, null)

        // Checks if it returns any rows
        val result = cursor.count > 0
        cursor.close()
        db.close()

        return result
    }
}

