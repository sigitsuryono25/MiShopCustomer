package com.lauwba.surelabs.mishopcustomer.libs

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object ChangeFormat {

    fun toRupiahFormat2(nominal: String): String {

        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat

        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = '.'
        df.decimalFormatSymbols = dfs

        df.maximumFractionDigits = 0
        val rupiah = df.format(d(nominal))

        return rupiah


    }

    fun d(transPokok: String): Double? {
        var x: Double? = 0.0
        try {
            x = java.lang.Double.parseDouble(transPokok)
        } catch (e: Exception) {
            // TODO: handle exception
        }

        return x
    }

    fun clearRp(c: String): String {
        return c.replace("Rp", "").replace(".", "").replace(" ", "")
    }

}


