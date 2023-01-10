package encryption

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Decrypts a [cipherText] with a [key] using the AES Algorithm.
 * @param cipherText encrypted string to decrypt.
 * @param key key used in the decryption process.
 */
fun decrypt(cipherText: String, key: String): String {
    val encryptionKey = SecretKeySpec(key.toByteArray(), "AES")
    // Initialization vector
    val iv = IvParameterSpec(ByteArray(16))
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, encryptionKey, iv)
    val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
    return String(plainText)
}