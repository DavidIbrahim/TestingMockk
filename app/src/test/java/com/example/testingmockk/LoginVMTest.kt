package com.example.testingmockk

import io.mockk.*
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test

class LoginVMTest : TestCase() {

    @Test
    fun testLogin() {
        val loginUseCase = mockk<ILoginUseCase>()
        val viewModel = LoginVM(loginUseCase)
        val result = viewModel.login("", "")
        Assert.assertEquals(false, result)

        verify { loginUseCase wasNot Called } //used only for the whole object

        verify(exactly = 0) {
            loginUseCase.login(
                any(),
                any()
            )
        } //used to detect if a method was not called

    }

    @Test
    fun testLoginSequence() {
        val loginUseCase = mockk<ILoginUseCase>()
        val viewModel = spyk(LoginVM(loginUseCase), recordPrivateCalls = true)

        val result = viewModel.login("david", "123")
        Assert.assertEquals(false, result)
        verify {
            viewModel.login("david", "123")
        }

        verify {
            viewModel["isFormValid"](any<String>(), any<String>())
        }
        //todo test verify sequence
        verifySequence {
            viewModel.login("david", "123")
            viewModel["isFormValid"](any<String>(), any<String>())
        }
    }

    @Test
    fun testReturnValuePrivateMethod() {
        val loginUseCase = mockk<ILoginUseCase>()
        val viewModel = spyk(LoginVM(loginUseCase), recordPrivateCalls = true)
        var returnValue: Boolean? = null
        every { viewModel["isFormValid"](any<String>(), any<String>()) } answers {
            returnValue = callOriginal() as Boolean
            return@answers returnValue
        }
        viewModel.login("david", "123")
        assertEquals(false, returnValue!!)

        //todo discuss: Should I unit test private methods?
    }

    @Test
    fun testCaptureArguments() {
        val loginUseCase = mockk<ILoginUseCase>(relaxed = true)
        val viewModel = LoginVM(loginUseCase)
        val captureEmailArg = mutableListOf<String>()


        viewModel.login("david@gmail.com", "Test@123")
        viewModel.login("davi@gmail.com", "Test@123")
        viewModel.login("dav@gmail.com", "Test@123")

        verify {
            loginUseCase.login(capture(captureEmailArg), any())
        }

        println(captureEmailArg)
        //similar behaviour we use in orange to capture the changes in a live data
        // we make a spyk live data and verify its call capturing the values in a list
    }

    @Test
    fun testRelaxMocking() {
        val loginUseCase = mockk<ILoginUseCase>(relaxed = true)
        val viewModel = spyk(LoginVM(loginUseCase))
        val result = viewModel.login("david@gmail.com", "Test@123")
        assertEquals(false, result)
    }

    @Test
    fun testMockingBehaviour1() {
        val loginUseCase = mockk<ILoginUseCase>()
        every { loginUseCase.login(any(), any()) } returns false andThen true

        val viewModel = LoginVM(loginUseCase)
        //return false
        var result = viewModel.login("david@gmail.com", "Test@123")
        assertEquals(false, result)

        //return true
        result = viewModel.login("david@gmail.com", "Test@123")
        assertEquals(true, result)

        result = viewModel.login("david@gmail.com", "Test@123")
        assertEquals(true, result)
    }

    @Test
    fun testMockingBehaviour2() {
        val loginUseCase = mockk<ILoginUseCase>()
        // note that the last value will be the default after exhausting the function call
        every { loginUseCase.login(any(), any()) } returnsMany listOf(false, true, false, true)

        val viewModel = spyk(LoginVM(loginUseCase))
        assertEquals(false, viewModel.login("david@gmail.com", "Test@123"))
        assertEquals(true, viewModel.login("david@gmail.com", "Test@123"))
        assertEquals(false, viewModel.login("david@gmail.com", "Test@123"))
        assertEquals(true, viewModel.login("david@gmail.com", "Test@123"))
        assertEquals(true, viewModel.login("david@gmail.com", "Test@123"))
    }

    @Test
    fun testMockingBehaviour3() {
        val loginUseCase = mockk<ILoginUseCase>()
        every { loginUseCase.login(any(), any()) } answers {
            val email: String = arg(0)
            val password: String = arg(1)

            email == "david@gmail.com" && password == "Test@123"
        }
        val viewModel = LoginVM(loginUseCase)
        assertEquals(true, viewModel.login("david@gmail.com", "Test@123"))
        assertEquals(false, viewModel.login("davi@gmail.com", "Test@123"))
        assertEquals(false, viewModel.login("david@gmail.com", "Test@23"))
    }
}
