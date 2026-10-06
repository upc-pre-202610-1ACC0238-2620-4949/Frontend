package com.smartgas_frontend.iam.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartgas_frontend.R
import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.iam.data.repository.AccountRepository
import com.smartgas_frontend.iam.data.repository.EMAIL_EXISTS
import com.smartgas_frontend.iam.data.repository.RegisterProfileData
import com.smartgas_frontend.iam.presentation.login.emailRegex
import kotlinx.coroutines.launch

data class RegisterForm(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val accountType: String = "commercial",
    val businessName: String = "",
    val acceptedTerms: Boolean = false
)

class RegisterViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _form = mutableStateOf(RegisterForm())
    val form: State<RegisterForm> get() = _form

    private val _error = mutableStateOf<Int?>(null)
    val error: State<Int?> get() = _error

    private val _success = mutableStateOf(false)
    val success: State<Boolean> get() = _success

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> get() = _loading

    fun onFormChanged(form: RegisterForm) {
        _form.value = form
    }

    fun register() {
        val form = _form.value

        _error.value = null
        _success.value = false

        if (form.fullName.isEmpty() || form.email.isEmpty() || form.password.isEmpty() ||
            form.confirmPassword.isEmpty() || form.businessName.isEmpty()
        ) {
            _error.value = R.string.emptyFields
            return
        }

        if (!emailRegex.matches(form.email)) {
            _error.value = R.string.emailInvalid
            return
        }

        if (form.password.length < 6) {
            _error.value = R.string.passwordMin
            return
        }

        if (form.password != form.confirmPassword) {
            _error.value = R.string.passwordMismatch
            return
        }

        if (!form.acceptedTerms) {
            _error.value = R.string.acceptTerms
            return
        }

        _loading.value = true

        viewModelScope.launch {
            val result = repository.register(
                form.email,
                form.password,
                RegisterProfileData(
                    fullName = form.fullName,
                    accountType = form.accountType,
                    businessName = form.businessName
                )
            )

            if (result is Resource.Success) {
                _success.value = true
            } else {
                _error.value = if (result.message == EMAIL_EXISTS) R.string.emailExists else R.string.apiError
            }

            _loading.value = false
        }
    }
}
