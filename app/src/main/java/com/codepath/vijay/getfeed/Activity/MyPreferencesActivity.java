package com.codepath.vijay.getfeed.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;

import com.codepath.vijay.getfeed.R;

import java.util.Calendar;

/**
 * Created by uttamavillain on 2/9/16.
 */
public class MyPreferencesActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, DatePickerDialog.OnDateSetListener
    {
        public MyPreferenceFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add 'general' preferences, defined in the XML file
            addPreferencesFromResource(R.xml.preferences);

            // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
            // updated when the preference changes.
            bindPreferenceSummaryToValue(findPreference("begin_date"));
            //bindPreferenceSummaryToValue(findPreference("applicationUpdates"));
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            // Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.i("dasd", "stringValue "+stringValue);
            if(preference instanceof Preference){
                preference.setSummary(stringValue);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        showDateDialog();
                        return false;
                    }
                });
            }
            else if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Log.i("dasd", "year " + year + " month " + month + " day " + day);
        }

        private void showDateDialog(){
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getActivity(),this, year, month, day).show();

        }
    }
}