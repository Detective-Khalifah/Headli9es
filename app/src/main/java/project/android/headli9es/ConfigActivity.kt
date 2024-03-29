package project.android.headli9es

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import project.android.headli9es.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_config)

        mBinding.topAppBar.setNavigationOnClickListener { finish() }
    }

    class NewsConfigFragment : PreferenceFragment(), OnPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.configs)

            // TODO: Set the default news outlet to always load on open, regardless of selected,
            //  TEMPORARY outlet used at runtime.

            // Find the {@link ListPreference}s in configs.xml
            val newsOutlet = findPreference(getString(R.string.settings_news_outlet_key))
            val pageSize = findPreference(getString(R.string.settings_page_size_key))

            // Set and display summary of the {@link ListPreference} according to selected value
            setSummary(newsOutlet)
            setSummary(pageSize)
        }

        private fun setSummary(changedPreference: Preference) {
            changedPreference.onPreferenceChangeListener = this
            val preferences =
                PreferenceManager.getDefaultSharedPreferences(changedPreference.context)
            val preferenceString = preferences.getString(changedPreference.key, "")!!
            onPreferenceChange(changedPreference, preferenceString)
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            val preferenceValue = newValue.toString()
            return if (preference is ListPreference) {
                val listPref = preference
                val indexOfSelectedValue = listPref.findIndexOfValue(preferenceValue)
                if (indexOfSelectedValue >= 0) {
                    val prefLabels = listPref.entries
                    preference.setSummary(prefLabels[indexOfSelectedValue])
                }
                true
            } else false
        }
    }
}