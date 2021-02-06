package fr.isen.david.themaquereau.helpers

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import fr.isen.david.themaquereau.KEY_SIZE
import fr.isen.david.themaquereau.MASTER_KEY_ALIAS
import fr.isen.david.themaquereau.R

interface SharedPrefEncryptHelper {
    fun getSharedPref(): SharedPreferences
    //fun getEncryptedFile(file: File): EncryptedFile
}

class SharedPrefEncryptHelperImpl(
    private val context: Context
) : SharedPrefEncryptHelper {
    private val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(KEY_SIZE)
        .build()

    private val masterKeysAlias = MasterKey.Builder(context, MASTER_KEY_ALIAS)
        .setKeyGenParameterSpec(keyGenParameterSpec)
        .build()

    override fun getSharedPref(): SharedPreferences {
        val keyEncryptedScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
        val valueEncryptedScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        return EncryptedSharedPreferences.create(
            context,
            context.getString(
                R.string.preference_file_key),
            masterKeysAlias,
            keyEncryptedScheme,
            valueEncryptedScheme
        )
    }
}