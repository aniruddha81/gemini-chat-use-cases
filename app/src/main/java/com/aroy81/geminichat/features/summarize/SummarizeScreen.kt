package com.aroy81.geminichat.features.summarize

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aroy81.geminichat.R

@Composable
internal fun SummarizeRoute(
    summarizeViewModel: SummarizeViewModel = hiltViewModel()
) {
    val summarizeUiState by summarizeViewModel.uiState.collectAsState()

    SummarizeScreen(
        uiState = summarizeUiState,
        onSummarizeClick = {
            summarizeViewModel.summarizeStreaming(it)
        }
    )
}

@Composable
fun SummarizeScreen(
    uiState: SummarizeUiState = SummarizeUiState.Loading,
    onSummarizeClick: (String) -> Unit
) {
    var textToSummarize by rememberSaveable { mutableStateOf("") }
    Column {
        OutlinedTextField(
            value = textToSummarize,
            onValueChange = { textToSummarize = it }
        )
        TextButton(onClick = {
            if (textToSummarize.isNotBlank())
                onSummarizeClick(textToSummarize)
        }) {
            Text(stringResource(R.string.action_go))
        }

        when (uiState) {
            is SummarizeUiState.Initial -> {
                // do nothing
            }

            is SummarizeUiState.Loading -> {
                Box(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            }

            is SummarizeUiState.Success -> {
                Card {
                    Text(uiState.outputText)
                }
            }

            is SummarizeUiState.Error -> {
                Card {
                    Text(uiState.errorMessage)
                }
            }
        }
    }
}