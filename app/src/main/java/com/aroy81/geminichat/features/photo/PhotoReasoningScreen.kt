package com.aroy81.geminichat.features.photo

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import com.aroy81.geminichat.R
import com.aroy81.geminichat.utils.UriSaver
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
internal fun PhotoReasoningRoute(viewModel: PhotoReasoningViewModel = hiltViewModel()) {
    val photoReasoningUiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val imageRequestBuilder = ImageRequest.Builder(LocalContext.current)
    val imageLoader = ImageLoader.Builder(LocalContext.current).build()

    PhotoReasoningScreen(
        uiState = photoReasoningUiState,
        onReasonClick = { inputText, selectedItems ->
            coroutineScope.launch {
                val bitmaps = selectedItems.mapNotNull {
                    val imageRequest =
                        imageRequestBuilder.data(it).size(768).precision(Precision.EXACT).build()
                    try {
                        val result = imageLoader.execute(imageRequest)
                        if (result is SuccessResult) {
                            return@mapNotNull (result.drawable as BitmapDrawable).bitmap
                        } else {
                            return@mapNotNull null
                        }
                    } catch (e: Exception) {
                        return@mapNotNull null
                    }
                }
                viewModel.reason(inputText, bitmaps)
            }
        }
    )
}

@Composable
fun PhotoReasoningScreen(
    uiState: PhotoReasoningUiState,
    onReasonClick: (String, List<Uri>) -> Unit = { _, _ -> }
) {
    var userQuestion by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let {
            imageUris.add(it)
        }
    }

    /*
    --------------this is for multiple media selection----------------

        val pickMultipleMedia = rememberLauncherForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(Int.MAX_VALUE) // Or Int.MAX_VALUE for unlimited
        ) { uris ->
            uris.let {
                imageUris.addAll(it)
            }
        }
    */

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Card {
            Row {
                IconButton(onClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_image)
                    )
                }
                OutlinedTextField(value = userQuestion, onValueChange = { userQuestion = it })
                TextButton(onClick = {
                    if (userQuestion.isNotBlank()) {
                        onReasonClick(userQuestion, imageUris.toList())
                    }
                }) {
                    Text(stringResource(R.string.action_go))
                }
            }

            LazyRow {
                items(imageUris) {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .requiredSize(72.dp)
                    )
                }
            }
        }
        when(uiState){
            is PhotoReasoningUiState.Initial -> {
                // do nothing
            }
            is PhotoReasoningUiState.Loading -> {
                Box(Modifier.fillMaxWidth()){
                    CircularProgressIndicator()
                }
            }
            is PhotoReasoningUiState.Success -> {
                Card {
                    Text(uiState.outputText)
                }
            }
            is PhotoReasoningUiState.Error -> {
                Card {
                    Text(uiState.errorMessage)
                }
            }
        }
    }
}