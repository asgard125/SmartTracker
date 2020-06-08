package com.example.smartTracker.objects

import java.math.BigInteger
import java.security.MessageDigest

object SHA256 {

    fun hash(string : String) : String{
        val md5Input = string.toByteArray()
        val md5 = MessageDigest.getInstance("SHA-256")
        md5.update(md5Input)
        val md5Data = BigInteger(1, md5.digest())
        return md5Data.toString(16)
    }

}