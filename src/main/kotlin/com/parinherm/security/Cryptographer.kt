package com.parinherm.security

import org.jasypt.util.text.AES256TextEncryptor

object Cryptographer {
    val encrypter = AES256TextEncryptor()
    var secretSet: Boolean = false

    init {
    }

    fun encrypt(valueToEncrypt: String) : String {
        if(!secretSet){
            throw Exception("Crytography secret not set")
        }
        return encrypter.encrypt(valueToEncrypt)
    }

    fun decrypt(valueToDecrypt: String) : String {
        if(!secretSet){
            throw Exception("Crytography secret not set")
        }
        val value = encrypter.decrypt(valueToDecrypt)
        return value;
    }

    fun setSecret(secret: String){
        if(secret.isBlank()){
            return
        }
        encrypter.setPassword(secret)
        secretSet = true
    }
}