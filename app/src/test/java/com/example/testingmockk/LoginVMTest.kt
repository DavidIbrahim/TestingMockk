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
        val privateMethodValue = slot<Boolean>()
        val arg = slot<String>()
        every { viewModel["isFormValid"](capture(arg), any<String>()) } answers {
            val result = callOriginal()
            privateMethodValue.captured = result as Boolean
            privateMethodValue.isCaptured = true
            result
        }

        println("is Slot captured before method call = ${privateMethodValue.isCaptured}")
        val result = viewModel.login("david@gmail", "Test@123")

        println("is Slot captured after method call = ${privateMethodValue.isCaptured}")

        Assert.assertEquals(false, result)
        verify {
            viewModel.login(any(), any())
        }
        verify {
            viewModel["isFormValid"](any<String>(), any<String>())
        }
        assertEquals(privateMethodValue.captured, false)

        //todo discuss: Should I unit test private methods?
    }

    @Test
    fun testCaptureArguments() {
        val loginUseCase = mockk<ILoginUseCase>()
        val viewModel = spyk(LoginVM(loginUseCase), recordPrivateCalls = true)
        val captureEmailArg = mutableListOf<String>()


        viewModel.login("david@gmail", "Test@123")
        viewModel.login("davi@gmail", "Test@123")
        viewModel.login("dav@gmail", "Test@123")

        verify {
            viewModel.login(capture(captureEmailArg), any())
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
        assertEquals(viewModel.login("david@gmail.com", "Test@123"), false)
        assertEquals(viewModel.login("david@gmail.com", "Test@123"), true)
        assertEquals(viewModel.login("david@gmail.com", "Test@123"), false)
        assertEquals(viewModel.login("david@gmail.com", "Test@123"), true)
        assertEquals(viewModel.login("david@gmail.com", "Test@123"), true)
    }

    @Test
    fun testMockingBehaviour3() {
        val loginUseCase = mockk<ILoginUseCase>(relaxed = true)
        // note that the last value will be the default after exhausting the function call
        every { loginUseCase.login(any(), any()) } answers {
            val email: String = arg(0)
            val password: String = arg(1)

            email == "david@gmail.com" && password == "Test@123"
        }
        val viewModel = spyk(LoginVM(loginUseCase))


        assertEquals(viewModel.login("david@gmail.com", "Test@123"), true)
        assertEquals(viewModel.login("davi@gmail.com", "Test@123"), false)
        assertEquals(viewModel.login("david@gmail.com", "Test@23"), false)
    }
}
