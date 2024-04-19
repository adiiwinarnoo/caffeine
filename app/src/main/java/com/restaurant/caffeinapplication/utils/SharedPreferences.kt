@file:Suppress("KDocUnresolvedReference")
package com.restaurant.caffeinapplication.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferences(context : Context) {
    private val prefsfilename = "prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsfilename, 0)

    /** put Int Value
     * @param key
     * @param value
     */
    fun putIntData(key: String,value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    /** put Boolean Value
     * @param key
     * @param value
     */
    fun putBooleanData(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    /** put String Vaule
     * @param key
     * @param value
     */
    fun putStringData(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    /** get Int Value
     * @param key
     */
    fun getIntData(key: String) : Int{
        return prefs.getInt(key, 0)
    }

    /** get Boolean Value
     * @param key
     */
    fun getBooleanData(key: String) : Boolean {
        return prefs.getBoolean(key, false)
    }

    /** get String Vaule
     * @param key
     */
    fun getStringData(key: String) : String? {
        Log.e("comment","getStringData : "+key+"and result : "+prefs.getString(key, ""))
        return prefs.getString(key, "")
    }

    /** put Int Value
     * @param key
     * @param value
     */
//    fun putArrayData(key: String,value: Set<String>) {
//        prefs.edit().putStringSet(key,value)
//    }

//    fun getArrayData(key: String) : Set<String> {
//        return prefs.getStringSet(key, null) as Set<String>
//    }

}