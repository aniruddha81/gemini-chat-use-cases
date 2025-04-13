package com.aroy81.geminichat.features.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.aroy81.geminichat.R
import kotlinx.coroutines.launch

@Composable
internal fun ChatRoute(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val chatUiState by chatViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    Scaffold(
        bottomBar = {
            MessageInput(
                onSendMessage = {
                    chatViewModel.sendMessage(it)
                },
                resetScroll = {
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                    }
                }
            )
        }
    ) {
        Column(Modifier
            .padding(it)
            .fillMaxSize()) {
            ChatList(
                chatList = chatUiState.messages,
                listState = lazyListState
            )
        }
    }
}

@Composable
fun ChatList(
    chatList: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(state = listState, reverseLayout = true) {
        items(chatList.reversed()) {
            ChatBubbleItem(it)
        }
    }
}

@Composable
fun ChatBubbleItem(chatMessage: ChatMessage) {
    val alignment =
        if (chatMessage.participant == Participant.USER) Alignment.End else Alignment.Start

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(chatMessage.participant.name)
        Row {
            if (chatMessage.isPending) {
                CircularProgressIndicator()
            }
            Card {
                Text(chatMessage.text)
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit
) {
    var userInput by rememberSaveable { mutableStateOf("") }
    ElevatedCard {
        Row {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it }
            )
            IconButton(onClick = {
                if (userInput.isNotBlank()) {
                    onSendMessage(userInput)
                    userInput = ""
                    resetScroll
                }
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.action_send)
                )
            }
        }
    }
}