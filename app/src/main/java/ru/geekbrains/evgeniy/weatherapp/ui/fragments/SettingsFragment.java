package ru.geekbrains.evgeniy.weatherapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithSharedPreferences;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    public static final String PREF_UPDATE_EVERY_5_MINUTES = "preference_update_every_5_minutes";
    private static final String EXTRA_UPDATE_EVERY_5_MINUTES = "extra_update_every_5_minutes";
    Switch switchUpdate;
    private boolean currentUpdateEvery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // init view
        View view = inflater.inflate(R.layout.settings, container, false);

        initViews(view);

        if (savedInstanceState != null) {
            currentUpdateEvery = savedInstanceState.getBoolean(EXTRA_UPDATE_EVERY_5_MINUTES);
        }
        else {
            currentUpdateEvery = WorkWithSharedPreferences.getProperty(PREF_UPDATE_EVERY_5_MINUTES, false);
        }

        switchUpdate.setChecked(currentUpdateEvery);

        return view;
    }

    private void initViews(View view) {
        switchUpdate = view.findViewById(R.id.switchUpdate);
        switchUpdate.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchUpdate:
                currentUpdateEvery = isChecked;
                WorkWithSharedPreferences.saveProperty(PREF_UPDATE_EVERY_5_MINUTES, currentUpdateEvery);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(EXTRA_UPDATE_EVERY_5_MINUTES, currentUpdateEvery);
        super.onSaveInstanceState(outState);
    }
}
