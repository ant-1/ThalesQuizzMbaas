package com.thales.thalesquizzmbaas.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.thales.thalesquizzmbaas.mbass.Mbaas;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;


public class CreateQuizzActivity extends MainActivity {


    @InjectView(R.id.mQuizzNameEditText)
    EditText mQuizzNameEditText;

    @InjectView(R.id.mQuizzTeaserEditText)
    EditText mQuizzTeaserEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quizz);
        injectViews();
    }


    @OnClick(R.id.mCreateQuizzButton)
    void createQuizz() {
        if (checkForm()) {
            Quizz quizz = new Quizz();
            quizz.setCreatorId(Mbaas.getInstance().getUserId());
            quizz.setStatus(ThalesQuizzMbaasConstants.STATUS_FINISHED);
            quizz.setName(mQuizzNameEditText.getText().toString());
            quizz.setTeaser(mQuizzTeaserEditText.getText().toString());
            Mbaas.getInstance().createQuizz(quizz);
        }
    }

    private boolean checkForm() {
        if (TextUtils.isEmpty(mQuizzNameEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.quizz_name_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(mQuizzTeaserEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.teaser_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onEventMainThread(Mbaas.CreateQuizzEvent event) {
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }
}
