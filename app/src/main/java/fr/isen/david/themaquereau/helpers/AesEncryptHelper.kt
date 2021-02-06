package fr.isen.david.themaquereau.helpers

import android.annotation.SuppressLint
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import fr.isen.david.themaquereau.ENC_VALUE
import fr.isen.david.themaquereau.IV_VALUE
import fr.isen.david.themaquereau.util.fromByteToString
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface AesEncryptHelper {
    fun createSymmetricKey() : SecretKey
    fun decryptData(hashMap: HashMap<String, ByteArray>): String
    fun encryptData(data: ByteArray): HashMap<String, ByteArray>
    fun decryptNoBase(ivBytes : ByteArray, encryptedBytes : ByteArray): String
    fun getSymmetricKey(): SecretKey
    fun removeKeyStoreKey()
    fun getCipher(): Cipher
}

//AES Encryption will be available after API 23+ (ANDROID M)
//Source: https://github.com/charanolati/Android-encryption-sample
class AesEncryptHelperImpl : AesEncryptHelper {
    companion object{
        const val AES_NO_PAD_TRANS = "AES/GCM/NoPadding"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "themaqkey"
    }

    private fun createKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore
    }

    @RequiresApi(23)
    override fun createSymmetricKey() : SecretKey {
        try{
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
            )

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true) // 4 different cipher text for same plaintext on each call
                .build()
            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        }
    }

    override fun decryptData(hashMap: HashMap<String, ByteArray>): String {
        val encryptedBytes = Base64.decode(hashMap[ENC_VALUE],Base64.NO_WRAP)
        val ivBytes = Base64.decode(hashMap[IV_VALUE],Base64.NO_WRAP)

        val cipher = Cipher.getInstance(AES_NO_PAD_TRANS)
        cipher.init(Cipher.DECRYPT_MODE, getSymmetricKey(), GCMParameterSpec(128, ivBytes))

        return cipher.doFinal(encryptedBytes).fromByteToString()
    }

    override fun encryptData(data: ByteArray): HashMap<String, ByteArray> {
        val cipher = Cipher.getInstance(AES_NO_PAD_TRANS)
        cipher.init(Cipher.ENCRYPT_MODE, getSymmetricKey())

        val eiv = (Base64.encodeToString(cipher.iv,Base64.NO_WRAP)).toByteArray()
        val edata = (Base64.encodeToString(cipher.doFinal(data),Base64.NO_WRAP)).toByteArray()

        return hashMapOf(Pair(IV_VALUE,eiv),Pair(ENC_VALUE,edata))
    }

    override fun decryptNoBase(ivBytes : ByteArray, encryptedBytes : ByteArray): String {
        val cipher = Cipher.getInstance(AES_NO_PAD_TRANS)
        cipher.init(Cipher.DECRYPT_MODE, getSymmetricKey(), GCMParameterSpec(128, ivBytes))
        return cipher.doFinal(encryptedBytes).fromByteToString()
    }

    @SuppressLint("NewApi")
    override fun getSymmetricKey(): SecretKey {
        val keyStore = createKeyStore()

        if(!isKeyExists(keyStore)){
            createSymmetricKey()
        }

        return keyStore.getKey(KEY_ALIAS,null) as SecretKey
    }

    override fun removeKeyStoreKey() {
        val keyStore = createKeyStore()

        if(isKeyExists(keyStore)) {
            keyStore.deleteEntry(KEY_ALIAS)
        }
    }

    private fun isKeyExists(keyStore : KeyStore): Boolean {
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            return (KEY_ALIAS == aliases.nextElement())
        }
        return false
    }

    override fun getCipher(): Cipher {
        val key = getSymmetricKey()
        val cipher = Cipher.getInstance(AES_NO_PAD_TRANS)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return cipher
    }
}