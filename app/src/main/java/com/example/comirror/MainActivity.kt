package com.example.comirror

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.comirror.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFCMToken()
        jsonToByteArray()

        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.messageBtn.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }
    }
    private fun jsonToByteArray() {
        var json: JSONObject = JSONObject()
        json.put("x", 5)
        json.put("y", 6)

        println("~~~~~~~~~~~~ 원본 json : "+json)

        var string = json.toString()
        //원본 string 데이터 출력
        println("~~~~~~~~~~~~ 원본 string : "+string)

        val charSet = Charsets.UTF_8
        val byt_arr = string.toByteArray(charSet)

        //string to byte 데이터 형변환
        println("~~~~~~~~~~~~ string to byte : "+byt_arr.contentToString())

        //byte to string 데이터 형변환
        var str_string = byt_arr.toString(charSet)
        println("~~~~~~~~~~~~ byte to string : "+str_string)
    }
    private fun getFCMToken(): String?{
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

            // Get new FCM registration token
            token = task.result

            // Log and toast
            println( "FCM Token is ${token}")

            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushTokens").document("Token").set(map)
        })

        return token
    }
}