package com.wks.servicemarketplace.common

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.stream.Collectors

fun InputStream.readPublicKey(): PublicKey {
    InputStreamReader(this).use { inputStreamReader ->
        BufferedReader(inputStreamReader).use { reader ->
            val content = reader.lines().collect(Collectors.joining(""))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = X509EncodedKeySpec(Base64.getMimeDecoder().decode(content))
            return keyFactory.generatePublic(keySpec)
        }
    }
}