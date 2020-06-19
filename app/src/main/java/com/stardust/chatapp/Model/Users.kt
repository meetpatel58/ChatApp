package com.stardust.chatapp.Model

class Users {

    private var Uid: String = ""
    private var Username: String = ""
    private var ProfilePicture: String = ""
    private var Status: String = ""
    private var Search: String = ""

    constructor()
    constructor(
        Uid: String,
        Username: String,
        ProfilePicture: String,
        Status: String,
        Search: String
    ) {
        this.Uid = Uid
        this.Username = Username
        this.ProfilePicture = ProfilePicture
        this.Status = Status
        this.Search = Search
    }

    fun getUid():  String{
        return Uid
    }
    fun setUid(uid : String){
        this.Uid = uid
    }

    fun getUsername():  String{
        return Username
    }
    fun setUsername(user : String){
        this.Username = user
    }

    fun getProfilePicture():  String{
        return ProfilePicture
    }
    fun setProfilePicture(profilePicture : String){
        this.ProfilePicture = profilePicture
    }

    fun getStatus():  String{
        return Status
    }
    fun setStatus(status : String){
        this.Status = status
    }

    fun getSearch():  String{
        return Search
    }
    fun setSearch(search : String){
        this.Search = search
    }




}