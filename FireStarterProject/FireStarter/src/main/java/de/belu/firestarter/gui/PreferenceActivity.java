package de.belu.firestarter.gui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import java.util.List;

import de.belu.firestarter.R;
import de.belu.firestarter.observer.ForeGroundService;
import de.belu.firestarter.tools.AppInfo;
import de.belu.firestarter.tools.SettingsProvider;

/**
 * Preferences activity
 */
public class PreferenceActivity extends PreferenceFragment
{
    SettingsProvider mSettings = SettingsProvider.getInstance(this.getActivity());

    public PreferenceActivity()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.preferencesactivity);

        InstalledAppsAdapter actAppsAdapter = new InstalledAppsAdapter(getActivity(), true, false);
        List<AppInfo> actApps = actAppsAdapter.getAppList();

        CharSequence[] entries = new CharSequence[actApps.size()+1];
        CharSequence[] entryValues = new CharSequence[actApps.size()+1];

        entries[0] = " - No Action - ";
        entryValues[0] = "";

        for(Integer i = 1; i < actApps.size()+1; i++)
        {
            AppInfo actApp = actApps.get(i-1);
            entries[i] = actApp.getDisplayName();
            entryValues[i] = actApp.packageName;
        }

        ListPreference startUpPackage = (ListPreference) findPreference("prefStartupPackage");
        startUpPackage.setEntries(entries);
        startUpPackage.setEntryValues(entryValues);
        startUpPackage.setDefaultValue(mSettings.getStartupPackage());

        ListPreference singleClick = (ListPreference) findPreference("prefHomeSingleClickPackage");
        singleClick.setEntries(entries);
        singleClick.setEntryValues(entryValues);
        singleClick.setDefaultValue(mSettings.getSingleClickApp());

        ListPreference doubleClick = (ListPreference) findPreference("prefHomeDoubleClickPackage");
        doubleClick.setEntries(entries);
        doubleClick.setEntryValues(entryValues);
        doubleClick.setDefaultValue(mSettings.getDoubleClickApp());

        InstalledAppsAdapter actHiddenAppsAdapter = new InstalledAppsAdapter(getActivity(), true, true);
        List<AppInfo> actHiddenApps = actHiddenAppsAdapter.getAppList();
        CharSequence[] hiddenEntries = new CharSequence[actHiddenApps.size()];
        CharSequence[] hiddenEntryValues = new CharSequence[actHiddenApps.size()];
        for(Integer i = 0; i < actHiddenApps.size(); i++)
        {
            AppInfo actApp = actHiddenApps.get(i);
            hiddenEntries[i] = actApp.getDisplayName();
            hiddenEntryValues[i] = actApp.packageName;
        }

        MultiSelectListPreference hiddenAppsList = (MultiSelectListPreference) findPreference("prefHiddenApps");
        hiddenAppsList.setEntries(hiddenEntries);
        hiddenAppsList.setEntryValues(hiddenEntryValues);
        hiddenAppsList.setDefaultValue(mSettings.getHiddenApps());
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Force read-settings
        SettingsProvider settingsProvider = SettingsProvider.getInstance(this.getActivity());
        settingsProvider.readValues(true);

        // Check if background observer is active
        if(settingsProvider.getBackgroundObserverEnabled())
        {
            // Start foreground service
            Intent startIntent = new Intent(this.getActivity(), ForeGroundService.class);
            startIntent.setAction(ForeGroundService.FOREGROUNDSERVICE_START);
            this.getActivity().startService(startIntent);
        }
        else
        {
            // Stop foreground service
            Intent startIntent = new Intent(this.getActivity(), ForeGroundService.class);
            startIntent.setAction(ForeGroundService.FOREGROUNDSERVICE_STOP);
            this.getActivity().startService(startIntent);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}