package fr.isen.david.themaquereau.helpers

import fr.isen.david.themaquereau.FIRST_TIME_SIGN_IN
import fr.isen.david.themaquereau.ID_CLIENT
import fr.isen.david.themaquereau.QUANTITY_KEY
import fr.isen.david.themaquereau.R

interface AppPreferencesHelper {
    fun getFirstTimeSignIn(): Boolean
    fun setFirstTimeSignIn(value: Boolean)
    fun getQuantity(): Int
    fun setQuantity(value: Int)
    fun getClientId(): Int
    fun setClientId(value: Int)
    fun isClientIdDefined(): Boolean
    fun isQuantityDefined(): Boolean
    fun isFirstTimeSignInDefined(): Boolean
    fun removeClientId()
    fun removeQuantity()
}

class AppPreferencesHelperImpl(
    encryption: EncryptHelperImpl
) : AppPreferencesHelper {
    private val sharedPreferences = encryption.getSharedPref()

    override fun getFirstTimeSignIn(): Boolean = sharedPreferences.getBoolean(FIRST_TIME_SIGN_IN, false)

    override fun setFirstTimeSignIn(value: Boolean) = sharedPreferences.edit().putBoolean(FIRST_TIME_SIGN_IN, value).apply()

    override fun getQuantity(): Int = sharedPreferences.getInt(QUANTITY_KEY, 0)

    override fun setQuantity(value: Int) = sharedPreferences.edit().putInt(QUANTITY_KEY, value).apply()

    override fun getClientId(): Int = sharedPreferences.getInt(ID_CLIENT, -1)

    override fun setClientId(value: Int) = sharedPreferences.edit().putInt(ID_CLIENT, value).apply()

    override fun isClientIdDefined(): Boolean = sharedPreferences.contains(ID_CLIENT)

    override fun isQuantityDefined(): Boolean = sharedPreferences.contains(QUANTITY_KEY)

    override fun isFirstTimeSignInDefined(): Boolean = sharedPreferences.contains(FIRST_TIME_SIGN_IN)

    override fun removeClientId() = sharedPreferences.edit().remove(ID_CLIENT).apply()

    override fun removeQuantity() = sharedPreferences.edit().remove(QUANTITY_KEY).apply()
}