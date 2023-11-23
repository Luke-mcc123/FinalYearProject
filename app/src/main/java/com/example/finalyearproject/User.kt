package com.example.finalyearproject

class User {

    // Initialise parameters
    var id : Int = 0
    var username : String = ""
    var password : String = ""
    var role : String = ""

    // Constructor for User
    constructor(
        username: String,
        password: String,
        role: String
    ) {

        this.username = username
        this.password = password
        this.role = role
    }

    // Empty Constructor
    constructor(){

    }

}