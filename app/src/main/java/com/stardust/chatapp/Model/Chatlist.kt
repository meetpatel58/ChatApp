package com.stardust.chatapp.Model

class Chatlist {

    private var ID: String = ""

    constructor()

    constructor(ID: String) {
        this.ID = ID
    }

    fun getID(): String? {
        return ID
    }
    fun setID(iD: String){
        this.ID = iD!!
    }


}