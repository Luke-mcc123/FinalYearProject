package com.example.finalyearproject

class Product {

    // Initialise parameters
    var id : Int = 0
    var barcode : Int = 0
    var brand : String = ""
    var type : String = ""
    var note : String = ""
    var quantity : Int = 0

    // Constructor for Object
    constructor(
        barcode: Int,
        brand: String,
        type: String,
        note: String,
        quantity: Int
    ) {

        this.barcode = barcode
        this.brand = brand
        this.type = type
        this.note = note
        this.quantity = quantity
    }

    // Empty Constructor
    constructor(){

    }
}