package fr.isen.david.themaquereau.helpers

import android.content.Context
import fr.isen.david.themaquereau.FIRST_TIME_SIGN_IN
import fr.isen.david.themaquereau.ID_CLIENT
import fr.isen.david.themaquereau.QUANTITY_KEY
import fr.isen.david.themaquereau.R

class AppPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    fun getFirstTimeSignIn(): Boolean = sharedPreferences.getBoolean(FIRST_TIME_SIGN_IN, false)

    fun setFirstTimeSignIn(value: Boolean) = sharedPreferences.edit().putBoolean(FIRST_TIME_SIGN_IN, value).apply()

    fun getQuantity(): Int = sharedPreferences.getInt(QUANTITY_KEY, 0)

    fun setQuantity(value: Int) = sharedPreferences.edit().putInt(QUANTITY_KEY, value).apply()

    fun getClientId(): Int = sharedPreferences.getInt(ID_CLIENT, -1)

    fun setClientId(value: Int) = sharedPreferences.edit().putInt(ID_CLIENT, value).apply()

    fun isClientIdDefined(): Boolean = sharedPreferences.contains(ID_CLIENT)

    fun isQuantityDefined(): Boolean = sharedPreferences.contains(QUANTITY_KEY)

    fun isFirstTimeSignInDefined(): Boolean = sharedPreferences.contains(FIRST_TIME_SIGN_IN)

    fun removeClientId() = sharedPreferences.edit().remove(ID_CLIENT).apply()

    fun removeQuantity() = sharedPreferences.edit().remove(QUANTITY_KEY).apply()
}