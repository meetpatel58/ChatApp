package com.stardust.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewImageActivity : AppCompatActivity() {

    private var imageView: ImageView ?= null
    private var imageUrl: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        imageUrl = intent.getStringExtra("url")
        imageView = findViewById(R.id.full_imageView)

        Picasso.get().load(imageUrl).into(imageView)
    }
}