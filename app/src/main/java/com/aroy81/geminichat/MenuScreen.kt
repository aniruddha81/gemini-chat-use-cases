package com.aroy81.geminichat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class MenuItem(
    val routeId: String,
    val titleResId: Int,
    val descriptionResId: Int
)

@Composable
fun MenuScreen(
    onItemClick: (String) -> Unit
) {
    val menuItems = listOf(
        MenuItem("summarize", R.string.menu_summarize_title, R.string.menu_summarize_description),
        MenuItem("photo_reasoning", R.string.menu_reason_title, R.string.menu_reason_description),
        MenuItem("chat", R.string.menu_chat_title, R.string.menu_chat_description)
    )

    LazyColumn {
        items(menuItems) {
            Card {
                Column {
                    Text(stringResource(it.titleResId))
                    Text(stringResource(it.descriptionResId))
                    TextButton(onClick = { onItemClick(it.routeId) }) {
                        Text(stringResource(R.string.action_try))
                    }
                }
            }
        }
    }
}