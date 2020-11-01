package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory
import java.io.BufferedReader
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

class PrivateKeyFactory  : Factory<PrivateKey> {

    private val privateKey: PrivateKey

    init{
        // Should come from vault.
        val privateKeyContent = javaClass.classLoader.getResourceAsStream("privateKey.pem")!!.bufferedReader().use(BufferedReader::readText)
                .replace("\n","")
                .replace("-----BEGIN PRIVATE KEY-----","")
                .replace("-----END PRIVATE KEY-----","")

        val kf = KeyFactory.getInstance("RSA")

        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(privateKeyContent))
        privateKey = kf.generatePrivate(keySpecPKCS8)
    }

    override fun provide() = privateKey

    override fun dispose(instance: PrivateKey?) {

    }
}