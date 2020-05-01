package com.wellcome.main.dto.admin.request

data class ChangePasswordRequest(val email: String,
                                 val newPassword: String)