package com.aroy81.geminichat.utils

import android.net.Uri
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.core.net.toUri

class UriSaver : Saver<MutableList<Uri>, List<String>> {
    override fun restore(value: List<String>): MutableList<Uri>? {
        return value.map {
            it.toUri()
        }.toMutableList()
    }

    override fun SaverScope.save(value: MutableList<Uri>): List<String>? {
        return value.map {
            it.toString()
        }
    }
}