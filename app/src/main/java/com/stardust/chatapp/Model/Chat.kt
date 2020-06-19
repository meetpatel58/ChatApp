package com.stardust.chatapp.Model

class Chat {

    private var Sender: String = ""
    private var Message: String = ""
    private var MessageID: String = ""
    private var Receiver: String = ""
    private var MessageSeen = false
    private var ImageUrl: String = ""

    constructor()
    constructor(
        Sender: String,
        Message: String,
        MessageID: String,
        Receiver: String,
        MessageSeen: Boolean,
        ImageUrl: String
    ) {
        this.Sender = Sender
        this.Message = Message
        this.MessageID = MessageID
        this.Receiver = Receiver
        this.MessageSeen = MessageSeen
        this.ImageUrl = ImageUrl
    }

    fun getSender(): String? {
        return Sender
    }
    fun setSender(sender: String){
        this.Sender = sender
    }

    //------------------------------------------------------------------------------

    fun getMessage(): String? {
        return Message
    }
    fun setMessage(message: String){
        this.Message = message
    }

    //------------------------------------------------------------------------------

    fun getMessageID(): String? {
        return MessageID
    }
    fun setMessageID(messageID: String){
        this.MessageID = messageID
    }

    //------------------------------------------------------------------------------

    fun getReceiver(): String? {
        return Receiver
    }
    fun setReceiver(receiver: String){
        this.Receiver = receiver
    }

    //------------------------------------------------------------------------------

    fun getMessageSeen(): Boolean? {
        return MessageSeen
    }
    fun setMessageSeen(messageSeen: Boolean){
        this.MessageSeen = messageSeen
    }

    //------------------------------------------------------------------------------

    fun getImageUrl(): String? {
        return ImageUrl
    }
    fun setImageUrl(imageUrl: String){
        this.ImageUrl = imageUrl
    }

    //------------------------------------------------------------------------------



}