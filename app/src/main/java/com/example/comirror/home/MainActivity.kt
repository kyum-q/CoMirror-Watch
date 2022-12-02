package com.example.comirror.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.comirror.data.MessageDTO
import com.example.comirror.databinding.ActivityMainBinding
import com.example.comirror.message.MessageActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import java.lang.reflect.Type


class MainActivity : Activity(), SensorEventListener {
    private var mTextViewHeart: TextView? = null
    var mSensorManager: SensorManager? = null
    var mHeartRateSensor: Sensor? = null
    var sensorEventListener: SensorEventListener? = null

    private lateinit var binding: ActivityMainBinding

    val ServerIP:String = "tcp://192.168.0.2:1883" //서버 IP
    val TOPIC:String = "watch/4004"

    val gson = GsonBuilder().create() // json을 object로 변환해줌
    val groupListType: Type = object : TypeToken<ArrayList<MessageDTO>>() {}.type
    private var pref: String? = null
    private var messageDataList: ArrayList<MessageDTO> = arrayListOf()

    //
    private var TAG: String? = MainActivity::class.java.name
    private var googleApiClient: GoogleApiClient? = null
    private var authInProgress = false
    private var onDataPointListener: OnDataPointListener? = null
    private val AUTH_REQUEST = 1

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged - accuracy: $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val msg = "" + event.values[0].toInt()
            mTextViewHeart!!.text = msg
            Log.d(TAG, msg)
        } else Log.d(TAG, "Unknown sensor type")
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private val REQUEST_PERMISSION_CODE = 12345
    private val missingPermission: ArrayList<String> = ArrayList()

    private var bCheckStarted = false
    private var bGoogleConnected = false

    private var btnStart: Button? = null
    private var spinner: ProgressBar? = null
    private var powerManager: PowerManager? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var textMon: TextView? = null


    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mTextViewHeart = binding.textView2
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        mHeartRateSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        mSensorManager!!.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.i(TAG, "LISTENER REGISTERED.")
        mTextViewHeart!!.text = "Something here"
        mSensorManager!!.registerListener(
            sensorEventListener,
            mHeartRateSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        //getFCMToken()

        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.messageBtn.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        /*
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
        */


        //심박패턴을 측정하는 동안 화면이 꺼지지 않도록 제어하기 위해 전원관리자를 얻어옵니다
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager!!.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "WAKELOCK"
        )
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



    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onStart connect attempted")
    }


    @Synchronized
    private fun addContentToView(value: Float) {
        runOnUiThread {
            if (spinner!!.visibility == View.VISIBLE) spinner!!.visibility = View.INVISIBLE
            Log.d(TAG, "Heart Beat Rate Value : $value")
            textMon!!.text = "Heart Beat Rate Value : $value"
        }
    }
}