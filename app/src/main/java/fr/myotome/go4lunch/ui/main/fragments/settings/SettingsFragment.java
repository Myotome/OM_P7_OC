package fr.myotome.go4lunch.ui.main.fragments.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkInfo;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.databinding.FragmentSettingsBinding;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding mBinding;
    private final String SWITCH = "switch", MAP_CHOICE = "map_choice", RADIUS = "radius", ZOOM = "zoom";
    private String mMessage;
    private int mProgressRadius = 300, mProgressZoom = 16;
    private static final String TAG = "DebugKey_settings";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);

        NotificationViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(NotificationViewModel.class);
        viewModel.getViewState().observe(getViewLifecycleOwner(), notificationViewState -> mMessage = notificationViewState.getMessage());

        String SHARED_PREFERENCES_NAME = "SharedPreferences" ;
        SharedPreferences preferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        displayPreferenceData(preferences);
        seekBarRadiusManager();
        seekBarZoomManager();

        mBinding.btSettingsSave.setOnClickListener(v -> {
            savePreferencesData(preferences);
            viewModel.notificationIsChecked(mBinding.swSettingsNotification.isChecked(), mMessage);
            Toast.makeText(requireActivity(), getResources().getText(R.string.preferences), Toast.LENGTH_SHORT).show();
        });


        // Use for testing workManager activity
        // Show work status, added in onCreate()
        viewModel.getOutputWorkInfo().observe(getViewLifecycleOwner(), listOfWorkInfo -> {

            // If there are no matching work info, do nothing
            if (listOfWorkInfo == null || listOfWorkInfo.isEmpty()) {
                return;
            }

            // We only care about the first output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            WorkInfo workInfo = listOfWorkInfo.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                Log.d(TAG, "onCreateView: work info : in progress");
            } else {
                Log.d(TAG, "onCreateView: work info : finish");
            }
        });


        return mBinding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    private void displayPreferenceData(SharedPreferences preferences) {
        mBinding.swSettingsNotification.setChecked(preferences.getBoolean(SWITCH, false));
        ((RadioButton) mBinding.radiogroupSettings.getChildAt(preferences.getInt(MAP_CHOICE, 0))).setChecked(true);
        mBinding.sbSettingsRadius.setProgress(preferences.getInt(RADIUS, 300));
        mBinding.tvSettingsRadius.setText(requireContext().getResources().getText(R.string.radius) + " : " + preferences.getInt(RADIUS, 300));
        mBinding.sbSettingsZoom.setProgress(preferences.getInt(ZOOM, 16));
        mBinding.tvSettingsZoom.setText(requireContext().getResources().getText(R.string.zoom) + " : " + preferences.getInt(ZOOM, 16));


    }

    private void savePreferencesData(SharedPreferences preferences) {

        int index = mBinding.radiogroupSettings.indexOfChild(requireActivity().findViewById(mBinding.radiogroupSettings.getCheckedRadioButtonId()));

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(SWITCH, mBinding.swSettingsNotification.isChecked());
        editor.putInt(MAP_CHOICE, index);
        editor.putInt(RADIUS, mProgressRadius);
        editor.putInt(ZOOM, mProgressZoom);

        editor.apply();

    }

    private void seekBarRadiusManager() {
        mBinding.sbSettingsRadius.setProgress(300);
        mBinding.sbSettingsRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int stepSize = 50;
                progress = (progress/stepSize)*stepSize;
                mBinding.tvSettingsRadius.setText(requireContext().getResources().getText(R.string.radius) + " : " + progress);
                mProgressRadius = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void seekBarZoomManager() {
        mBinding.sbSettingsZoom.setProgress(16);
        mBinding.sbSettingsZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBinding.tvSettingsZoom.setText(requireContext().getResources().getText(R.string.zoom) + " : " + progress);
                mProgressZoom = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
