package com.example.comirror.message

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comirror.MirrorApplication
import com.example.comirror.data.MessageDTO
import com.example.comirror.databinding.ActivityMessageBinding
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

class MessageActivity : Activity() {

    private lateinit var binding: ActivityMessageBinding
    private var messageDataList: ArrayList<MessageDTO> = arrayListOf()

    val gson = GsonBuilder().create() // json을 object로 변환해줌
    val groupListType: Type = object : TypeToken<ArrayList<MessageDTO>>() {}.type
    private var pref: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Data push는 이렇게 하면 될듯
//        val data = MessageDTO("유송연", "메시지다")
//         messageDataList.add(data)
//         Log.d("song-parsing",gson.toJson(messageDataList,groupListType))
//         MirrorApplication.prefs.setString("message", gson.toJson(messageDataList,groupListType))

        initData()

        val messageAdapter = MessageAdapter(messageDataList,this)
        binding.messageRv.apply {
            setHasFixedSize(true)
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@MessageActivity)
        }

    }

    fun initData(){
        //Shared Preference 값 가져오기(Data 초기화 - 값이 없으면 none)
        pref = MirrorApplication.prefs.getString("message","none")
        Log.d("pref == ",pref+"")
        if (pref != "none"){
            messageDataList = gson.fromJson(pref, groupListType)
            Log.d("song-parsing 결과",messageDataList.toString())
        }

    }
}