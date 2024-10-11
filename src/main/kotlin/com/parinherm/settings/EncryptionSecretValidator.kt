package com.parinherm.settings

object EncryptionSecretValidator {
    fun validate(secret: String?){
        if(secret.isNullOrBlank()){
            throw SettingsValidationException("Encryption Secret cannot be blank")
        }
        if(secret.length < 4){
            throw SettingsValidationException("Encryption Secret should be at least 4 characters")
        }
    }
}