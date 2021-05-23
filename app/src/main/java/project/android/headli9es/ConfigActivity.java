package project.android.headli9es;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
    }

    public static class NewsConfigFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.configs);

            // Find the {@link ListPreference}s in configs.xml
            Preference newsOutlet = findPreference(getString(R.string.settings_news_outlet_key));
            Preference pageSize = findPreference(getString(R.string.settings_page_size_key));

            // Set and display summary of the {@link ListPreference} according to selected value
            setSummary(newsOutlet);
            setSummary(pageSize);
        }

        private void setSummary (Preference changedPreference) {
            changedPreference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(changedPreference.getContext());
            String preferenceString = preferences.getString(changedPreference.getKey(), "");
            assert preferenceString != null;
            onPreferenceChange(changedPreference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange (Preference preference, Object newValue) {
            String preferenceValue = newValue.toString();

            switch (preference.getTitleRes()) {
                case R.string.settings_news_outlet_label:
                    ListPreference nOutlet = (ListPreference) preference;
                    final int indexOfSelectedOutlet = nOutlet.findIndexOfValue(preferenceValue);
                    if (indexOfSelectedOutlet >= 0) {
                        CharSequence[] sizes = nOutlet.getEntries();
                        preference.setSummary(sizes[indexOfSelectedOutlet]);
                        return true;
                    }
                case R.string.settings_page_size_label:
                    ListPreference psize = (ListPreference) preference;
                    final int indexOfSelectedSize = psize.findIndexOfValue(preferenceValue);
                    if (indexOfSelectedSize >= 0) {
                        CharSequence[] sizes = psize.getEntries();
                        preference.setSummary(sizes[indexOfSelectedSize]);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }
}