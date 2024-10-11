package com.parinherm.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object CaptureScope: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO//Dispatchers.Default
}