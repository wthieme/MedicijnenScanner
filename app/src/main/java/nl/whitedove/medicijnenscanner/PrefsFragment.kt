package nl.whitedove.medicijnenscanner

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat

class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle, s: String) {
        //add xml
        addPreferencesFromResource(R.xml.preferences)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings))
    }

    override fun onResume() {
        super.onResume()
        //unregister the preferenceChange listener
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference(key)
        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(sharedPreferences.getString(key, ""))
            if (prefIndex >= 0) {
                preference.setSummary(preference.entries[prefIndex])
            }
        } else {
            preference.summary = sharedPreferences.getString(key, "")

        }
    }

    override fun onPause() {
        super.onPause()
        //unregister the preference change listener
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }
}