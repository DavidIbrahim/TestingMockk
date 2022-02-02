package com.example.testingmockk

import com.example.testingmockk.FormValidationUtil.isEmailValid
import com.example.testingmockk.FormValidationUtil.isFieldEmpty
import com.example.testingmockk.FormValidationUtil.isInputNumerical
import com.example.testingmockk.FormValidationUtil.isPasswordValid
import com.example.testingmockk.FormValidationUtil.isPhoneNumberValid


class LoginVM constructor(
    private val loginUseCase: ILoginUseCase,
) {

    fun login(userIdentifier: String, password: String): Boolean {
        return if (isFormValid(userIdentifier, password)) {
            loginValidatedUser(userIdentifier, password)
        } else false
    }


    private fun isFormValid(userIdentifier: String, password: String): Boolean {
        return if (isFieldEmpty(userIdentifier)) {
            false
        } else if (!isInputNumerical(userIdentifier) && !isEmailValid(userIdentifier)) {
            false
        } else if (isInputNumerical(userIdentifier) && !isPhoneNumberValid(userIdentifier)) {
            false
        } else if (isFieldEmpty(password)) {
            false
        } else isPasswordValid(password)
    }

    private fun loginValidatedUser(userIdentifier: String, password: String): Boolean {
        return loginUseCase.login(userIdentifier, password)
    }
}






