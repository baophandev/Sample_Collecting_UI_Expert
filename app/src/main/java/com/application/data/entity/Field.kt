package com.application.data.entity

data class Field(
    val id: String,
    val numberOrder: Int,
    val name: String,
    val formId: String
) {
    companion object {
        val ERROR_FIELD = Field(
            id = "error-field",
            numberOrder = Int.MAX_VALUE,
            name = "Error field",
            formId = "no-form-id"
        )
    }
}
