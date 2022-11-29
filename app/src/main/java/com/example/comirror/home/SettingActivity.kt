package com.example.comirror.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.comirror.databinding.ActivitySettingBinding

class SettingActivity : Activity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}