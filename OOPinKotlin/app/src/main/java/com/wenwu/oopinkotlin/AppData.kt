package com.wenwu.oopinkotlin

import com.wenwu.oopinkotlin.MainActivity.Vehicle

class AppData {
    companion object
    {
        val t = Vehicle("Tesla")
        val j = Vehicle("Jeep")
        val r = Vehicle("Rivian")

        val vehicles = arrayOf(t, j, r)
    }
}