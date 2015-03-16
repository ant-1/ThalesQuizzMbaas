package com.thales.thalesquizzmbaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.thales.thalesquizzmbaas.mbass.Mbaas;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;


public class LoginActivity extends MainActivity {

    @InjectView(R.id.mWaitingDemoLinearLayout)
    LinearLayout mWaitingDemoLinearLayout;

    @InjectView(R.id.mLoginLinearLayout)
    LinearLayout mLoginLinearLayout;
    @InjectView(R.id.mRegisterLinearLayout)
    LinearLayout mRegisterLinearLayout;

    @InjectView(R.id.mLoginEditText)
    EditText mLoginEditText;
    @InjectView(R.id.mLoginRegisterEditText)
    EditText mLoginRegisterEditText;
    @InjectView(R.id.mPasswordEditText)
    EditText mPasswordEditText;
    @InjectView(R.id.mPasswordRegisterEditText)
    EditText mPasswordRegisterEditText;
    @InjectView(R.id.mPassword2RegisterEditText)
    EditText mPassword2RegisterEditText;
    @InjectView(R.id.mEmailRegisterEditText)
    EditText mEmailRegisterEditText;

    @InjectView(R.id.mRegisterButton)
    Button mRegisterButton;
    @InjectView(R.id.mLoginButton)
    Button mLoginButton;

    @InjectView(R.id.mOrLoginTextView)
    TextView mOrLoginTextView;
    @InjectView(R.id.mOrRegisterTextView)
    TextView mOrRegisterTextView;

    private final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        injectViews();

        if (BuildConfig.DEBUG) {
            //debug no hide
            // autologin
            mWaitingDemoLinearLayout.setVisibility(View.GONE);
            mLoginLinearLayout.setVisibility(View.VISIBLE);

            String login = getSharedPreferences().getString(ThalesQuizzMbaasConstants.KEY_LOGIN, "");
            String password = getSharedPreferences().getString(ThalesQuizzMbaasConstants.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
                mLoginEditText.setText(login);
                mPasswordEditText.setText(password);
                onLoginClicked();
            }
        } else {
            if (checkDemoStarted()) {
                //autologin
                String login = getSharedPreferences().getString(ThalesQuizzMbaasConstants.KEY_LOGIN, "");
                String password = getSharedPreferences().getString(ThalesQuizzMbaasConstants.KEY_PASSWORD, "");
                if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
                    mLoginEditText.setText(login);
                    mPasswordEditText.setText(password);
                    onLoginClicked();
                }
            }
        }
    }

    private boolean checkDemoStarted() {
        String login = getSharedPreferences().getString(ThalesQuizzMbaasConstants.KEY_LOGIN, "");
        if ((login != null && login.equals(MainMenuActivity.DEMO_HOST_USERNAME)) || Mbaas.getInstance().getSetup() != null && Mbaas.getInstance().getSetup().isDemoStarted()) {
            mWaitingDemoLinearLayout.setVisibility(View.GONE);
            mLoginLinearLayout.setVisibility(View.VISIBLE);
            return true;
        } else {
            mWaitingDemoLinearLayout.setVisibility(View.VISIBLE);
            mLoginLinearLayout.setVisibility(View.GONE);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @OnClick(R.id.mRefreshDemoSetup)
    void refreshSetup() {
        showLoader();
        Mbaas.getInstance().reloadSetup();
    }

    public void onEventMainThread(Mbaas.ReloadSetupEvent event) {
        hideLoader();
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            checkDemoStarted();
        }
    }

    @OnClick(R.id.mOrRegisterTextView)
    void showRegister() {
        mLoginLinearLayout.setVisibility(View.GONE);
        mRegisterLinearLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.mOrLoginTextView)
    void showLogin() {
        mRegisterLinearLayout.setVisibility(View.GONE);
        mLoginLinearLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.mLoginButton)
    void onLoginClicked() {
        if (checkCredentialsLogin()) {
            showLoader();
            Mbaas.getInstance().login(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());
        }
    }

    @OnClick(R.id.mRegisterButton)
    void onRegisterClicked() {
        if (checkCredentialsRegister()) {
            showLoader();
            Mbaas.getInstance().register(mLoginRegisterEditText.getText().toString(), mPasswordRegisterEditText.getText().toString());
        }
    }

    private boolean checkCredentialsLogin() {
        String username = mLoginEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getString(R.string.login_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.password_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkCredentialsRegister() {
        String username = mLoginRegisterEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getString(R.string.login_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        String password = mPasswordRegisterEditText.getText().toString();
        String passwordConfirmation = mPassword2RegisterEditText.getText().toString();
        if (TextUtils.isEmpty(password) || !password.equals(passwordConfirmation)) {
            Toast.makeText(this, getString(R.string.password_error), Toast.LENGTH_LONG).show();
            return false;
        }
        String email = mEmailRegisterEditText.getText().toString();
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void saveCredentialsInPreferences() {
        String login = mLoginEditText.getText().toString();
        if (TextUtils.isEmpty(login)) {
            login = mLoginRegisterEditText.getText().toString();
        }
        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            password = mPasswordRegisterEditText.getText().toString();
        }
        getSharedPreferencesEditor().putString(ThalesQuizzMbaasConstants.KEY_LOGIN, login).commit();
        getSharedPreferencesEditor().putString(ThalesQuizzMbaasConstants.KEY_PASSWORD, password).commit();
    }

    public void onEventMainThread(Mbaas.LoginEvent event) {
        handleResult(event);
    }

    public void onEventMainThread(Mbaas.RegisterEvent event) {
        if (event.isSuccessfull()) {
            Mbaas.getInstance().saveMail(mLoginRegisterEditText.getText().toString(), mEmailRegisterEditText.getText().toString());
        } else {
            handleResult(event);
        }
    }

    public void onEventMainThread(Mbaas.SaveMailEvent event) {
        handleResult(event);
    }

    private void handleResult(Mbaas.MbaasEvent event) {
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            saveCredentialsInPreferences();
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        hideLoader();
    }

}
