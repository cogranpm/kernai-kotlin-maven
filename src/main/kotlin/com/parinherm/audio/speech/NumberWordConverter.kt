package com.parinherm.audio.speech

import java.util.*

object NumberWordConverter {
    private var numbers = mapOf(
        "zero" to 0,
        "one" to 1,
        "two" to 2,
        "to" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "ten" to 10,
        "eleven" to 11,
        "twelve" to 12,
        "thirteen" to 13,
        "fourteen" to 14,
        "fifteen" to 15,
        "sixteen" to 16,
        "seventeen" to 17,
        "eighteen" to 18,
        "nineteen" to 19,
    )



    public fun wordToNumber(input: String): String{
        val splitNumbers = input.split(" ")
        var result = ""
        for (number in splitNumbers){
            result += numbers[number].toString()
        }
        return result
    }
}