package com.wks.servicemarketplace.common

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.wks.servicemarketplace.common.auth.StandardTokenValidator
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.stream.Collectors

fun InputStream.readPublicKey() : Result<PublicKey, Throwable>{
    try {
        InputStreamReader(this).use { inputStreamReader ->
            BufferedReader(inputStreamReader).use { reader ->
                val content = reader.lines().collect(Collectors.joining(""))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                val keyFactory = KeyFactory.getInstance("RSA")
                val keySpec = X509EncodedKeySpec(Base64.getMimeDecoder().decode(content))
                return Ok(keyFactory.generatePublic(keySpec))
            }
        }
    } catch (e: IOException) {
        return Err(e)
    } catch (e: NoSuchAlgorithmException) {
        return Err(e)
    } catch (e: InvalidKeySpecException) {
        return Err(e)
    }
}