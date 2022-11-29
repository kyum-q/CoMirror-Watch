package com.example.comirror

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.comirror.databinding.ActivityLoginBinding
import com.example.comirror.home.MainActivity

class LoginActivity : Activity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}