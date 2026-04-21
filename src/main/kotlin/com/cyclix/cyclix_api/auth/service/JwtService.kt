package com.cyclix.cyclix_api.auth.service

import com.cyclix.cyclix_api.user.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class JwtService(
    @Value("\${app.jwt.secret}") private val secret: String,
    @Value("\${app.jwt.expiration-seconds}") val expirationSeconds: Long
) {
    fun generateToken(user: User): String {
        val now = Instant.now()
        val expiresAt = now.plusSeconds(expirationSeconds)
        val header = """{"alg":"HS256","typ":"JWT"}"""
        val payload = """
            {
              "sub":"${escapeJson(user.email)}",
              "userId":${user.id},
              "role":"${escapeJson(user.role.name)}",
              "iat":${now.epochSecond},
              "exp":${expiresAt.epochSecond}
            }
        """.trimIndent().replace(Regex("\\s+"), "")

        val encodedHeader = base64Url(header.toByteArray(StandardCharsets.UTF_8))
        val encodedPayload = base64Url(payload.toByteArray(StandardCharsets.UTF_8))
        val unsignedToken = "$encodedHeader.$encodedPayload"
        val signature = base64Url(sign(unsignedToken))

        return "$unsignedToken.$signature"
    }

    private fun sign(value: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        val key = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(key)
        return mac.doFinal(value.toByteArray(StandardCharsets.UTF_8))
    }

    private fun base64Url(bytes: ByteArray): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

    private fun escapeJson(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"")
}
