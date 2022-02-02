package com.example.testingmockk

import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

object FormValidationUtil {

    fun isFieldEmpty(fieldText: String): Boolean {
        return fieldText.isEmpty()
    }

    fun isEmailValid(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        val passwordPattern =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+,.\\\\\\/;':\"-]).{8,}\$")
        return passwordPattern.matcher(password).matches()
    }

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val egpPhoneRegex = Pattern.compile("^(01)[0-9]{9}")
        return egpPhoneRegex.matcher(phoneNumber).matches()
    }

    fun isLanLindeValid(phoneNumber: String): Boolean {
        val egpLandLineRegex = Pattern.compile("^(0)[0-9]{9}")
        return egpLandLineRegex.matcher(phoneNumber).matches()
    }

    fun arePasswordsMatching(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun areNamesMatching(
        firstName: String,
        lastName: String,
        oldFirstName: String,
        oldLastName: String
    ): Boolean {
        return firstName == oldFirstName && lastName == oldLastName
    }

    fun isNationalIdValid(id: String): Boolean {
        val allNumbersPattern = Pattern.compile("[0-9]+")
        return (id.length == 14) && allNumbersPattern.matcher(id).matches()
    }



    fun isInputNumerical(input: String): Boolean {
        val allNumbersPattern = Pattern.compile("[0-9]+")
        return allNumbersPattern.matcher(input).matches()
    }


}