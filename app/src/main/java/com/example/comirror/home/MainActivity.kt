package com.example.comirror.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.comirror.MirrorApplication
import com.example.comirror.data.MessageDTO
import com.example.comirror.databinding.ActivityMainBinding
import com.example.comirror.message.MessageActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.lang.reflect.Type

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding

    val ServerIP:String = "tcp://192.168.0.2:1883" //서버 IP
    val TOPIC:String = "watch/4004"

    val gson = GsonBuilder().create() // json을 object로 변환해줌
    val groupListType: Type = object : TypeToken<ArrayList<MessageDTO>>() {}.type
    private var pref: String? = null
    private var messageDataList: ArrayList<MessageDTO> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getFCMToken()

        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.messageBtn.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        var mqttClient: MqttClient? = null
        mqttClient = MqttClient(ServerIP, MqttClient.generateClientId(), null) //3번 연결설정
        mqttClient.connect()

        mqttClient.subscribe(TOPIC)
        mqttClient.setCallback(object : MqttCallback { //6번 콜백 설정
            override fun connectionLost(p0: Throwable?) {
                //연결이 끊겼을 경우
                Log.d("MQTTService","Connection Lost")
            }

            override fun messageArrived(p0: String?, p1: MqttMessage?) {
                //메세지가 도착했을 때 여기
                Log.d("MQTTService","여기 들어옴")
                Log.d("MQTTService","Message Arrived : " + p1.toString()) //7번 메세지 도착

                // 기존 데이터 가져오기
                pref = MirrorApplication.prefs.getString("message","none")
                if (pref != "none"){
                    Log.d("songsong","안녕하세요 none이 아니다")
                    messageDataList = gson.fromJson(pref, groupListType)

                }
                //현재 데이터 넣기
                var currData = gson.fromJson(p1.toString(),MessageDTO::class.java)
                if (!(messageDataList.contains(currData))) {
                    messageDataList.add(currData)
                    Log.d("songsong", messageDataList.toString())

                    MirrorApplication.prefs.setString( // update
                        "message",
                        gson.toJson(messageDataList, groupListType)
                    )
                }
            }

            override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                //메세지가 도착 하였을 때
                Log.d("MQTTService","Delivery Complete")
            }

        })

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