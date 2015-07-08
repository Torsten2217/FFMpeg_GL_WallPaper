package com.fatowl.screensprovaw.main;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.fatowl.screensprovaw.R;

public class Preferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
