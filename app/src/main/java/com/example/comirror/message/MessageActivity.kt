package com.example.comirror.message

import android.app.Activity
import android.os.Bundle
import com.example.comirror.databinding.ActivityMessageBinding

class MessageActivity : Activity() {

private lateinit var binding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
         binding = ActivityMessageBinding.inflate(layoutInflater)
         setContentView(binding.root)
    }
}