package com.nerdery.umbrella.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.ListPreference;
import android.preference.EditTextPreference;
import com.nerdery.umbrella.R;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;

public class PrefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_with_actionbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setBackgroundColor(Color.argb(255, 117, 117, 117));
        toolbar.setTitleTextColor((Color.WHITE));
        toolbar.setTitle("Umbrella");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        Preference singlePref = preferenceGroup.getPreference(j);
                        updatePreference(singlePref, singlePref.getKey());
                    }
                } else {
                    updatePreference(preference, preference.getKey());
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key), key);
        }

        public void updatePreference(Preference preference, String key) {
            if (preference == null) return;
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary("%s");
                return;
            }

            if (preference instanceof EditTextPreference) {
                SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
                EditTextPreference editTextPref = (EditTextPreference) findPreference("uZip");
                editTextPref.setSummary(sp.getString("uZip", "Some Default Text"));
            }
        }
    }
}