package com.parinherm.errors

sealed class Result<T, E> {
    data class Success<T>(val data: T): Result<T, Exception>()
    data class Error<T>(val e: Exception): Result<T, Exception>()
}
