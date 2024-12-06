import kotlinx.coroutines.GlobalScope

println("""I am a worksheet""")

//coroutines
//be explicit about blocking
runBlocking {
    delay(1000L)
}

println("what time is it")