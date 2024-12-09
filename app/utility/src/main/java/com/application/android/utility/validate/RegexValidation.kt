package com.application.android.utility.validate

object RegexValidation {
    val EMAIL: Regex = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", RegexOption.IGNORE_CASE)
}