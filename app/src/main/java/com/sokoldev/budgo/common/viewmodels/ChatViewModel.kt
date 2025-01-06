package com.sokoldev.budgo.common.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.data.models.ChatMessage

class ChatViewModel : ViewModel() {

    private val _listChat: MutableLiveData<List<com.sokoldev.budgo.common.data.models.ChatMessage>> = MutableLiveData()
    val listChat: LiveData<List<com.sokoldev.budgo.common.data.models.ChatMessage>>
        get() = _listChat


    fun getAllMessages() {
        val arraylist = ArrayList<com.sokoldev.budgo.common.data.models.ChatMessage>()
        arraylist.add(
            com.sokoldev.budgo.common.data.models.ChatMessage("Hello!", R.drawable.user_image, true)
        )
        arraylist.add(
            ChatMessage(
                "Hi! How are you?",
                R.drawable.user_image,
                false
            )
        )
        arraylist.add(
            ChatMessage(
                "I'm good, thanks. How about you?",
                R.drawable.user_image,
                true
            )
        )
        arraylist.add(
            ChatMessage(
                "Doing well, thank you!",
                R.drawable.user_image,
                false
            )
        )
        _listChat.value = arraylist
    }

}