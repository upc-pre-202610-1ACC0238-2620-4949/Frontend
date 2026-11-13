package com.smartgas_frontend.shared.domain.model

data class SessionUser(
    val id: Int,
    val email: String,
    val fullName: String = "",
    val role: String = ""
)
