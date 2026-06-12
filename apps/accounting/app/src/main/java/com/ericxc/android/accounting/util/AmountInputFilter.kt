package com.ericxc.android.accounting.util

import android.text.InputFilter
import android.text.Spanned

class AmountInputFilter : InputFilter {

    private val mDecimalDigits = 2

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val destStr = dest.toString()
        val newStr = destStr.substring(0, dstart) + source.subSequence(start, end) + destStr.substring(dend)

        if (newStr.isEmpty()) return null

        if (newStr == ".") return "0."

        val dotIndex = newStr.indexOf(".")
        if (dotIndex >= 0 && newStr.indexOf(".", dotIndex + 1) >= 0) return ""

        if (dotIndex >= 0 && newStr.length - dotIndex - 1 > mDecimalDigits) {
            return ""
        }

        val match = Regex("^\\d*\\.?\\d{0,$mDecimalDigits}$").matches(newStr)
        return if (match) null else ""
    }
}
