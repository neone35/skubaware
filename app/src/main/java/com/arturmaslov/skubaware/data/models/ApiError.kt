package com.arturmaslov.skubaware.data.models

import com.google.gson.annotations.SerializedName

data class ApiError(
    @field:SerializedName("status_code")
    var statusCode: Int? = null,

    @field:SerializedName("message")
    var message: String? = null
)
