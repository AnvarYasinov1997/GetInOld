package com.wellcome.main.component

import org.springframework.stereotype.Component
import java.security.MessageDigest

interface CryptographyProvider {
    fun getSHA256Hash(data: String): String
}

@Component
open class DefaultCryptographyProvider : CryptographyProvider {

    override fun getSHA256Hash(data: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(data.toByteArray(Charsets.UTF_8))
            .let(::bytesToHex)
    }

    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder()
        for (i in hash) {
            val hex = Integer.toHexString(0xff and i.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

}