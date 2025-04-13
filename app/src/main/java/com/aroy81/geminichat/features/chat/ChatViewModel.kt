package com.aroy81.geminichat.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    generativeModel: GenerativeModel
) : ViewModel() {
    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("hello i am plabon") },
            content(role = "model") { text("hey, nice to meet you..") }
        )
    )

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = chat.history.map {
                ChatMessage(
                    text = it.parts.first().asTextOrNull() ?: "",
                    participant = if (it.role == "user") Participant.USER else Participant.MODEL
                )
            }
        )
    )
    val uiState = _uiState.asStateFlow()

    fun sendMessage(userMessage: String) {
        // add a pending msg
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.USER,
                isPending = true
            )
        )

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastChatMessage()

                response.text?.let {
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = it,
                            participant = Participant.MODEL
                        )
                    )
                }
            } catch (e: Exception) {

                _uiState.value.replaceLastChatMessage()

                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage!!,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }
}