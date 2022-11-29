package com.example.comirror.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.comirror.databinding.ActivityMainBinding
import com.example.comirror.message.MessageActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding


    val ServerIP:String = "tcp://172.30.1.41:1883"  //1번 서버 IP
    val TOPIC:String = "watch/4004"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //getFCMToken()
       // jsonToByteArray()

        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.messageBtn.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        var mqttClient: MqttClient? = null
        mqttClient = MqttClient(ServerIP, MqttClient.generateClientId(), null) //3번 연결설정
        mqttClient.connect()

        mqttClient.subscribe(TOPIC) //5번 구독 설정
        mqttClient.setCallback(object : MqttCallback { //6번 콜백 설정
            override fun connectionLost(p0: Throwable?) {
                //연결이 끊겼을 경우
                Log.d("MQTTService","Connection Lost")
            }

            override fun messageArrived(p0: String?, p1: MqttMessage?) {
                //메세지가 도착했을 때 여기
                Log.d("MQTTService","여기 들어옴")
//                Log.d("MQTTService","Message Arrived : " + p1.toString()) //7번 메세지 도착
            }

            override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                //메세지가 도착 하였을 때
                Log.d("MQTTService","Delivery Complete")
            }

        })


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