package com.thales.thalesquizzmbaas.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.thales.thalesquizzmbaas.mbass.Mbaas;
import com.thales.thalesquizzmbaas.ui.fragment.DialogProgressFragment;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;
import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {

    EventBus mEventBus;
    DialogFragment mProgressFragment;

    private static final String DIALOG_LOADER = "DIALOG_LOADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mProgressFragment = DialogProgressFragment.newInstance();
    }

    public void onEventMainThread(Mbaas.MbaasEvent event){

    }

    SharedPreferences getSharedPreferences(){
        return getSharedPreferences(ThalesQuizzMbaasConstants.SHARED_PREFS,MODE_PRIVATE);
    }

    SharedPreferences.Editor getSharedPreferencesEditor(){
        return getSharedPreferences(ThalesQuizzMbaasConstants.SHARED_PREFS,MODE_PRIVATE).edit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    protected void injectViews(){
        ButterKnife.inject(this);
    }

    void showLoader() {
        mProgressFragment.show(getFragmentManager(), DIALOG_LOADER);
    }

    void hideLoader() {
        mProgressFragment.dismiss();
    }
}
