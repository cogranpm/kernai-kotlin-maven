package com.parinherm

import kotlinx.coroutines.*

fun printIt(what: String) {
    println("******************************")
    println(what)
    println("******************************")
}

fun basics() {
    printIt("Coroutines")

    //suspending functions must be called from coroutine

    //builders
    // functions that create a new coroutine


    printIt("now, non blocking")
    GlobalScope.launch {
        someFunc()
    }
    printIt("right after the non blocking call")

    printIt("async will return a value")
    runBlocking {
        val result: Deferred<String> = async {
            someFuncThatReturns()
        }
        printIt("getting result")
        printIt("result was: ${result.await()}")
    }


    printIt("about to block main thread")
    runBlocking {
        someFunc()
    }

    printIt("after the blocking")
    printIt("at the end of it")

}

suspend fun someFunc() {
    printIt("I'm in the suspending function")
    delay(2000L)
    printIt("I'm exiting suspending function")
}

suspend fun someFuncThatReturns(): String {
    printIt("In the suspending function that returns a value")
    delay(2000L)
    printIt("done my work, exiting suspending function that returns a value")
    return "fred"
}

/* Coroutine Scope
* create scope
* new job gets created and gets associated with it
* coroutine created in this scope becomes child of this job
* if coroutine throws exception, parent job is cancelled, along with all it's children
* this is what's know as structured concurrency
*
* Structured Concurrency
* a coroutine builder adds an instance of CoroutineScope to the scope of it's code block
* it means that "this" inside the builder block, refers to a CoroutineScope instance
* builders are functions are functions that create a coroutine
*   launch - start in background and keep working
*   async - return a deferred object, which can be awaited on
*   runBlocking - blocks current thread and waits for coroutine to finish
*   coroutineScope - suspends, does not block current thread
*
* Dispatchers - come in flavours, what thread runs the coroutine
*   Dispatchers.Default
*   Dispatchers.Main
*   Dispatchers.IO
*
* MainScope - for UI components, creates a SupervisorJob and runs on main thread
* exception to rule that failure of coroutine triggers termination of job
* failures are handled with CoroutineExceptionHandler
* this is where the swing, javafx etc dependencies are used, and the redheaded stepchild swt
*
* CoroutineScope
* run some asyncronous tasks
* will wait for tasks to complete
*
* GlobalScope
* top level coroutines
* not recommended? lose benefits of structured concurrency
* works like a daemon thread, fire and forget
*
* Cancelling
* inside your suspending function you can call isActive
* for example in a while loop
* while (isActive) { blah blah
* it will be set to false if the job was cancelled
* which is performed via a call to cancel on the job object
* the job object is returns by the coroutine builders like launch
* val job = launch { blah blah }
* job.cancel()
* job.join() //wait for completion
* // or
* job.cancelAndJoin()
*
* Cleaning up resources inside cancelled suspending functions
* on cancellation, a CancellationException will be thrown
* so put stuff in a try finally block to clean up if cancelled
*
* Testing
* use runBlocking in unit tests to test suspending functions in isolation:
* runBlocking<ReturnType> {
*
* }
*
*
* */
