package com.wks.servicemarketplace.authservice.config

import org.glassfish.hk2.api.Factory
import java.io.BufferedReader
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

class PublicKeyFactory : Factory<PublicKey> {

    private val publicKey: PublicKey

    init {
        // Should come from vault.
        val publicKeyContent = javaClass.classLoader.getResourceAsStream("publicKey.pem")!!.bufferedReader().use(BufferedReader::readText)
                .replace("\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")

        val kf = KeyFactory.getInstance("RSA")

        val keySpec = X509EncodedKeySpec(Base64.getMimeDecoder().decode(publicKeyContent))
        publicKey = kf.generatePublic(keySpec)
    }

    override fun provide() = publicKey

    override fun dispose(instance: PublicKey?) {

    }
}