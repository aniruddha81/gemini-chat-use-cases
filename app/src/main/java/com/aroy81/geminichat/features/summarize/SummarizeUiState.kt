package com.aroy81.geminichat.features.summarize

sealed interface SummarizeUiState {
    data object Initial : SummarizeUiState
    data object Loading : SummarizeUiState
    data class Success(val outputText: String) : SummarizeUiState
    data class Error(val errorMessage: String) : SummarizeUiState
}