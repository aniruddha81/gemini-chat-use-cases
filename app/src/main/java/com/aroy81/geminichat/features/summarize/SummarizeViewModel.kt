package com.aroy81.geminichat.features.summarize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummarizeViewModel @Inject constructor(
    private val generativeModel: GenerativeModel
) : ViewModel() {
    private val _uiState = MutableStateFlow<SummarizeUiState>(SummarizeUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun summarizeStreaming(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        val prompt = "summarize this text : $inputText"

        viewModelScope.launch {
            try {
                var result = ""
                generativeModel.generateContentStream(prompt).collect {
                    result += it.text
                    _uiState.value = SummarizeUiState.Success(result)
                }

            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}