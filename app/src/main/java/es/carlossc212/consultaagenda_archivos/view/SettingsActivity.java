package es.carlossc212.consultaagenda_archivos.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

import es.carlossc212.consultaagenda_archivos.R;

public class SettingsActivity extends AppCompatActivity {

    private static ArrayList<CheckBoxPreference> cbpList = new ArrayList<CheckBoxPreference>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    for (CheckBoxPreference cbp : cbpList) {
                        if (!cbp.getKey().equals(preference.getKey()) && cbp.isChecked()) {
                            cbp.setChecked(false);
                        }
                    }
                    return false;
                }
            };

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            CheckBoxPreference chbpBuscar1 = getPreferenceManager().findPreference("buscar1");
            CheckBoxPreference chbpBuscar2 = getPreferenceManager().findPreference("buscar2");
            chbpBuscar1.setOnPreferenceClickListener(listener);
            chbpBuscar2.setOnPreferenceClickListener(listener);
            cbpList.add(chbpBuscar1);
            cbpList.add(chbpBuscar2);

        }
    }
}