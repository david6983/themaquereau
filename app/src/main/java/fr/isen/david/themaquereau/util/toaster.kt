package fr.isen.david.themaquereau.util

import android.content.Context
import android.widget.Toast

fun displayToast(text: CharSequence, context: Context) {
    val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
    toast.show()
}