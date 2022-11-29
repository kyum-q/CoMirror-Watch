package com.example.comirror

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.comirror.databinding.ActivityMessageBinding
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class MessageActivity(intent: Intent) : Activity() {

    private lateinit var binding: ActivityMessageBinding
    val ServerIP:String = "tcp://192.168.0.16:1883"  //1번 서버 IP
    val TOPIC:String = "TopicName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var mqttClient: MqttClient? = null
        mqttClient = MqttClient(ServerIP, MqttClient.generateClientId(), null) //3번 연결설정
        mqttClient.connect()

        binding.buttonSend.setOnClickListener {
            Log.d("MQTTService", "Send")
            mqttClient.publish(TOPIC, MqttMessage("hello!".toByteArray())) //4번 메세지 전송
        }

        mqttClient.subscribe(TOPIC) //5번 구독 설정
        mqttClient.setCallback(object : MqttCallback { //6번 콜백 설정
            override fun connectionLost(p0: Throwable?) {
                //연결이 끊겼을 경우
                Log.d("MQTTService","Connection Lost")
            }

            override fun messageArrived(p0: String?, p1: MqttMessage?) {
                //메세지가 도착했을 때 여기
                Log.d("MQTTService","Message Arrived : " + p1.toString()) //7번 메세지 도착
            }

            override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                //메세지가 도착 하였을 때
                Log.d("MQTTService","Delivery Complete")
            }

        })

    }


}