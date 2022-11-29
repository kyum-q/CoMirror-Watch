package com.example.comirror.message

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.comirror.data.MessageModel


class MessageViewModel : ViewModel() {

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null


    private val messageDataList = mutableListOf<MessageModel>()
    private val _messageLiveData = MutableLiveData<List<MessageModel>>()
    val messageLiveData: LiveData<List<MessageModel>> get() = _messageLiveData

    init {


    }


}