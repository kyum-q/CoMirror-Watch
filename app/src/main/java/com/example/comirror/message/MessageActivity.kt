package com.example.comirror.message

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
        //val data = MessageDTO("1001", "유송연","메시지")
        // {
        // messageDataList.add(data)
        // Log.d("song-parsing",gson.toJson(messageDataList,groupListType))
        // MirrorApplication.prefs.setString("message", gson.toJson(messageDataList,groupListType))
        // }
        initData()

    }

    fun initData(){
        //Shared Preference에서 값 가져오기(Data 초기화 - 값이 없으면 none)
        pref = MirrorApplication.prefs.getString("message","none")
        if (pref != "none"){
            messageDataList = gson.fromJson(pref, groupListType)
            Log.d("song-parsing 결과",messageDataList.toString())
        }
    }
}