package com.application.util

object Validation {

    const val NORMAL_TEXT_LENGTH = 100
    const val LONG_TEXT_LENGTH = 255

    fun checkNormalText(text: String): Boolean = text.length <= 100
    fun checkLongText(text: String): Boolean = text.length <= 255

}