package com.thales.thalesquizzmbaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.thales.thalesquizzmbaas.mbass.Mbaas;
import com.thales.thalesquizzmbaas.model.Answer;
import com.thales.thalesquizzmbaas.model.Question;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.ui.fragment.ConfirmDialogFragment;
import com.thales.thalesquizzmbaas.ui.listitems.QuestionAdminLinearLayout;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;

import java.util.ArrayList;
import java.util.List;


public class AdminQuizzActivity extends MainActivity {

    private static final int REQUEST_ADD_QUESTION = 1;

    @InjectView(R.id.mStatusImageView)
    ImageView mStatusImageView;

    @InjectView(R.id.mNameTextView)
    TextView mNameTextView;

    @InjectView(R.id.mTeaserTextView)
    TextView mTeaserTextView;

    @InjectView(R.id.mStartQuizzButton)
    Button mStartQuizzButton;

    @InjectView(R.id.mFinishQuizzButton)
    Button mFinishQuizzButton;

    @InjectView(R.id.mAddQuestionButton)
    Button mAddQuestionButton;

    @InjectView(R.id.mSaveMbaasButton)
    Button mSaveMbaasButton;

    @InjectView(R.id.mSeeLiveResultsButton)
    Button mSeeLiveResultsButton;

    @InjectView(R.id.mDeleteQuizzButton)
    Button mDeleteQuizzButton;

    @InjectView(R.id.mPlayQuizzButton)
    Button mPlayQuizzButton;

    @InjectView(R.id.mQuestionsLinearLayout)
    LinearLayout mQuestionsLinearLayout;

    Quizz mQuizz;

    boolean mStarted = false;

    // Create a reference to a Firebase location
    Firebase mQuizzRef;
    Firebase mQuizzStatusReference;

    private static final String CONFIRM_DELETE = "CONFIRM_DELETE";

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() == null || dataSnapshot.getValue().equals(ThalesQuizzMbaasConstants.STATUS_FINISHED)) {
                Toast.makeText(AdminQuizzActivity.this, getString(R.string.quizz_finished), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Toast.makeText(AdminQuizzActivity.this, getString(R.string.quizz_finished), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quizz);
        injectViews();
        mQuizz = (Quizz) getIntent().getParcelableArrayExtra(ThalesQuizzMbaasConstants.KEY_QUIZZ)[0];
        if (mQuizz.getStatus() != null) {
            mStarted = mQuizz.getStatus().equals(ThalesQuizzMbaasConstants.STATUS_STARTED);
            if (mStarted) {
                //start in firebase
                mQuizzRef = new Firebase("https://incandescent-heat-910.firebaseio.com/quizzes/" + mQuizz.getId());
                mQuizzStatusReference = mQuizzRef.child("status");
                mQuizzStatusReference.addValueEventListener(mValueEventListener);
            }
        } else {
            mStarted = false;
        }
        updateQuizzDisplay();
        updateButtons();
    }

    private void updateQuizzDisplay() {
        if (mQuizz.getStatus() != null && mQuizz.getStatus().equals(ThalesQuizzMbaasConstants.STATUS_STARTED)) {
            mStatusImageView.setImageResource(R.drawable.on);
        } else {
            mStatusImageView.setImageResource(R.drawable.off);
        }
        mNameTextView.setText(mQuizz.getName());
        mTeaserTextView.setText(mQuizz.getTeaser());
        mQuestionsLinearLayout.removeAllViews();
        List<Question> questions = mQuizz.getQuestions();
        int i = 0;
        for (Question question : questions) {
            QuestionAdminLinearLayout layout = (QuestionAdminLinearLayout) getLayoutInflater().inflate(R.layout.linearlayout_question_admin, null);
            layout.setQuestion(question, i);
            if (mStarted) {
                layout.modeLive();
            } else {
                layout.modeoffline();
            }
            mQuestionsLinearLayout.addView(layout);
            i++;
        }
        updateButtons();
    }

    private void updateButtons() {
        mStartQuizzButton.setEnabled(!mStarted);
        mAddQuestionButton.setEnabled(!mStarted);
        mFinishQuizzButton.setEnabled(mStarted);
        mSeeLiveResultsButton.setEnabled(mStarted);
        mDeleteQuizzButton.setEnabled(!mStarted);
        mPlayQuizzButton.setEnabled(mStarted);
        for (int i = 0; i < mQuestionsLinearLayout.getChildCount(); i++) {
            QuestionAdminLinearLayout layout = (QuestionAdminLinearLayout) mQuestionsLinearLayout.getChildAt(i);
            if (mStarted) {
                layout.modeLive();
            } else {
                layout.modeoffline();
            }
        }
    }

    @OnClick(R.id.mStartQuizzButton)
    public void onStartClicked() {
        Mbaas.getInstance().startQuizz(mQuizz);
    }


    public void onEventMainThread(Mbaas.StartQuizzEvent event) {
        if (event.isSuccessfull()) {
            mQuizz = (Quizz) event.result;

            //start in firebase
            mQuizzRef = new Firebase("https://incandescent-heat-910.firebaseio.com/quizzes/" + mQuizz.getId());
            mQuizzRef.setValue(mQuizz);
            mQuizzStatusReference = mQuizzRef.child("status");
            mQuizzStatusReference.addValueEventListener(mValueEventListener);
            //update ui
            mStarted = true;
            updateButtons();
            updateQuizzDisplay();
        } else {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.mSaveMbaasButton)
    public void onSaveMbaasClicked() {
        if (mQuizzRef != null) {
            //save data from firebase
            mQuizzRef.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (Question question : mQuizz.getQuestions()) {
                        List<Answer> answers = new ArrayList<Answer>();
                        Iterable<DataSnapshot> results = dataSnapshot.child("" + i).child("results").getChildren();
                        for (DataSnapshot resultsDataSnapshot : results) {
                            Answer answer = new Answer();
                            answer.setUserId((String) resultsDataSnapshot.child("userId").getValue());
                            answer.setUserName((String) resultsDataSnapshot.child("userName").getValue());
                            answer.setAnswer((String) resultsDataSnapshot.child("answer").getValue());
                            answer.setLatitude((Double) resultsDataSnapshot.child("latitude").getValue());
                            answer.setLongitude((Double) resultsDataSnapshot.child("longitude").getValue());
                            answers.add(answer);
                        }
                        question.setResults(answers);
                        i++;
                    }
                    Mbaas.getInstance().saveQuizz(mQuizz);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(AdminQuizzActivity.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Mbaas.getInstance().saveQuizz(mQuizz);
        }
    }

    public void onEventMainThread(Mbaas.SaveQuizzEvent event) {
        if (event.isSuccessfull()) {
            mQuizz = (Quizz) event.result;
            Toast.makeText(AdminQuizzActivity.this, getString(R.string.quizz_saved), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.mFinishQuizzButton)
    public void onFinishClicked() {
        //save solutions and delete live
        if (mStarted) {
            //remove from firebase
            if (mQuizzRef != null) {
                mQuizzRef.removeValue();
                mQuizzRef = null;
            }
            Mbaas.getInstance().finishQuizz(mQuizz);
        } else {
            Toast.makeText(AdminQuizzActivity.this, getString(R.string.cant_stop_live), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(Mbaas.FinishQuizzEvent event) {
        if (event.isSuccessfull()) {
            mQuizz = (Quizz) event.result;
            mStarted = false;
            updateButtons();
            Toast.makeText(AdminQuizzActivity.this, getString(R.string.quizz_stopped), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.mAddQuestionButton)
    public void addQuestion() {
        Intent intent = new Intent(this, AddQuestionActivity.class);
        startActivityForResult(intent, REQUEST_ADD_QUESTION);
    }

    @OnClick(R.id.mSeeLiveResultsButton)
    public void seeLiveResults() {
        Intent intent = new Intent(this, SeeLiveResultsQuizzActivity.class);
        Parcelable[] quizzTab = new Parcelable[1];
        quizzTab[0] = mQuizz;
        intent.putExtra(ThalesQuizzMbaasConstants.KEY_QUIZZ, quizzTab);
        startActivity(intent);
    }


    @OnClick(R.id.mDeleteQuizzButton)
    public void deleteQuizzClicked() {
        final ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
        confirmDialogFragment.setQuestionText(getString(R.string.delete_quizz));
        confirmDialogFragment.setOkText(getString(R.string.yes));
        confirmDialogFragment.setCancelText(getString(R.string.no));
        confirmDialogFragment.setConfirmDialogFragmentListener(new ConfirmDialogFragment.ConfirmDialogFragmentListener() {
            @Override
            public void onOkClicked() {
                showLoader();
                Mbaas.getInstance().deleteQuizz(mQuizz);
            }

            @Override
            public void onCancelClicked() {
                confirmDialogFragment.dismiss();
            }
        });
        confirmDialogFragment.show(getFragmentManager(), CONFIRM_DELETE);
    }

    @OnClick(R.id.mPlayQuizzButton)
    public void playQuizzClicked() {
        //user participates in the quizz
        Intent intent = new Intent(this, PlayQuizzActivity.class);
        Parcelable[] quizzTab = new Parcelable[1];
        quizzTab[0] = mQuizz;
        intent.putExtra(ThalesQuizzMbaasConstants.KEY_QUIZZ, quizzTab);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_QUESTION:
                if (resultCode == RESULT_OK) {
                    Question question = (Question) data.getParcelableArrayExtra(ThalesQuizzMbaasConstants.KEY_QUESTION)[0];
                    mQuizz.addQuestion(question);
                    updateQuizzDisplay();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void onEventMainThread(QuestionAdminLinearLayout.PublishQuestionEvent event) {
        event.question.setStatus(ThalesQuizzMbaasConstants.STATUS_STARTED);
        mQuizzRef.child("questions").child("" + event.index).child("status").setValue(ThalesQuizzMbaasConstants.STATUS_STARTED);
    }

    public void onEventMainThread(QuestionAdminLinearLayout.DeleteQuestionEvent event) {
        //delete is not when the quizz is live.
        mQuizz.getQuestions().remove(event.question);
        updateQuizzDisplay();
    }

    public void onEventMainThread(QuestionAdminLinearLayout.StopQuestionEvent event) {
        event.question.setStatus(ThalesQuizzMbaasConstants.STATUS_FINISHED);
        mQuizzRef.child("questions").child("" + event.index).child("status").setValue(ThalesQuizzMbaasConstants.STATUS_FINISHED);
    }

    public void onEventMainThread(Mbaas.DeleteQuizzEvent event) {
        hideLoader();
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.quizz_quizz_deleted), Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
