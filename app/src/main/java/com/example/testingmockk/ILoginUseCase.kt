package com.example.testingmockk

interface ILoginUseCase {
    fun login(userIdentifier: String, password: String): Boolean
}