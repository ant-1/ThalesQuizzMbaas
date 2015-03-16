package com.thales.thalesquizzmbaas.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.firebase.client.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thales.thalesquizzmbaas.model.Answer;
import com.thales.thalesquizzmbaas.model.Question;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.ui.listitems.QCMAnswerResultTotalRelativeLayout;
import com.thales.thalesquizzmbaas.utils.GLog;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SeeLiveResultsQuizzActivity extends MainActivity {

    private static final int REQUEST_ANSWER_QUESTION = 1;
    Quizz mQuizz;

    @InjectView(R.id.mWaitingLinearLayout)
    LinearLayout mWaitingLinearLayout;

    @InjectView(R.id.mParticipantsCountTextView)
    TextView mParticipantsCountTextView;

    //free question
    @InjectView(R.id.mFreeQuestionResultsLinearLayout)
    LinearLayout mFreeQuestionResultsLinearLayout;

    @InjectView(R.id.mFreeQuestionTextDisplayTextView)
    TextView mFreeQuestionTextDisplayTextView;

    @InjectView(R.id.mAnswersFreeQuestionResultsLinearLayout)
    LinearLayout mAnswersFreeQuestionResultsLinearLayout;

    //QCM question
    @InjectView(R.id.mQCMQuestionResultsLinearLayout)
    LinearLayout mQCMQuestionResultsLinearLayout;

    @InjectView(R.id.mAnswersQCMQuestionResultsLinearLayout)
    LinearLayout mAnswersQCMQuestionResultsLinearLayout;

    @InjectView(R.id.mQCMQuestionTextDisplayTextView)
    TextView mQCMQuestionTextDisplayTextView;

    //Street view question
    @InjectView(R.id.mStreetViewQuestionResultsLinearLayout)
    LinearLayout mStreetViewQuestionResultsLinearLayout;

    @InjectView(R.id.mStreetViewQuestionTextView)
    TextView mStreetViewQuestionTextView;

    @InjectView(R.id.mShowResultsButton)
    Button mShowResultsButton;

    @InjectView(R.id.mResultsMapView)
    MapView mResultsMapView;

    private static final LatLng SAN_FRAN = new LatLng(45.213248, 5.808961);

    private long mParticipantsCount;
    Firebase mQuizzReference;
    Firebase mQuizzStatusReference;
    Firebase mQuestionsReference;
    Firebase mCurrentQuestionReference;
    Firebase mParticipantsReference;

    boolean mShowMapResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_solutions_quizz);
        injectViews();
        mQuizz = (Quizz) getIntent().getParcelableArrayExtra(ThalesQuizzMbaasConstants.KEY_QUIZZ)[0];
        //start in firebase
        mQuizzReference = new Firebase("https://incandescent-heat-910.firebaseio.com/quizzes/" + mQuizz.getId());
        mQuizzStatusReference = mQuizzReference.child("status");
        mQuizzStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null || dataSnapshot.getValue().equals(ThalesQuizzMbaasConstants.STATUS_FINISHED)) {
                    Toast.makeText(SeeLiveResultsQuizzActivity.this, getString(R.string.quizz_finished), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(SeeLiveResultsQuizzActivity.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mQuestionsReference = mQuizzReference.child("questions");
        mQuestionsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //ignore
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                checkQuestions(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //ignore
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //ignore
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(SeeLiveResultsQuizzActivity.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mParticipantsReference = mQuizzReference.child("participants");
        mParticipantsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                saveParticipantsSize(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mStreetViewQuestionResultsLinearLayout.setVisibility(View.GONE);
        MapsInitializer.initialize(this);
        mResultsMapView.onCreate(savedInstanceState);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(SAN_FRAN);
        mResultsMapView.getMap().animateCamera(cameraUpdate);

        //load active question
        reloadActiveQuestion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //map views
        mResultsMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //maps
        mResultsMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResultsMapView.onDestroy();
    }

    private void saveParticipantsSize(DataSnapshot dataSnapshot) {
        mParticipantsCount = dataSnapshot.getChildrenCount();
        mParticipantsCountTextView.setText(mParticipantsCount + " " + getString(R.string.quizz_participants));
    }

    private void checkQuestions(DataSnapshot dataSnapshot, String s) {
        GLog.d(this, dataSnapshot.getValue().toString());
        if (dataSnapshot.child("status") != null && dataSnapshot.child("status").getValue().equals(ThalesQuizzMbaasConstants.STATUS_STARTED)) {
            mCurrentQuestionReference = dataSnapshot.getRef();
            mCurrentQuestionReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals(ThalesQuizzMbaasConstants.STATUS_STARTED)) {
                        mWaitingLinearLayout.setVisibility(View.VISIBLE);

                        mAnswersQCMQuestionResultsLinearLayout.removeAllViews();
                        mQCMQuestionResultsLinearLayout.setVisibility(View.GONE);

                        mAnswersFreeQuestionResultsLinearLayout.removeAllViews();
                        mFreeQuestionResultsLinearLayout.setVisibility(View.GONE);

                        mStreetViewQuestionResultsLinearLayout.setVisibility(View.GONE);

                        Toast.makeText(SeeLiveResultsQuizzActivity.this, getString(R.string.quizz_question_finished), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    mCurrentQuestionReference = null;
                }
            });
            Question questionObject = new Question();
            questionObject.setType((String) dataSnapshot.child("type").getValue());
            questionObject.setQuestionText((String) dataSnapshot.child("questionText").getValue());
            questionObject.setLatitude((Double) dataSnapshot.child("latitude").getValue());
            questionObject.setLongitude((Double) dataSnapshot.child("longitude").getValue());
            questionObject.setStatus((String) dataSnapshot.child("status").getValue());
            List<String> solutions = new ArrayList<String>();
            Iterable<DataSnapshot> solutionsList = dataSnapshot.child("solutions").getChildren();
            for (DataSnapshot snapshot : solutionsList) {
                solutions.add((String) snapshot.getValue());
            }
            questionObject.setSolutions(solutions);

            List<Answer> results = new ArrayList<Answer>();
            Iterable<DataSnapshot> resultsList = dataSnapshot.child("results").getChildren();
            for (DataSnapshot result : resultsList) {
                Answer answer = new Answer();
                answer.setUserName((String) result.child("userName").getValue());
                answer.setUserId((String) result.child("userId").getValue());
                answer.setAnswer((String) result.child("answer").getValue());
                answer.setLatitude((Double) result.child("latitude").getValue());
                answer.setLongitude((Double) result.child("longitude").getValue());
                results.add(answer);
            }
            questionObject.setResults(results);

            mWaitingLinearLayout.setVisibility(View.GONE);
            if (questionObject.getType().equals(getResources().getStringArray(R.array.questions_types_array)[1])) {
                //QCM questions
                mStreetViewQuestionResultsLinearLayout.setVisibility(View.GONE);
                mFreeQuestionResultsLinearLayout.setVisibility(View.GONE);

                mQCMQuestionTextDisplayTextView.setText(questionObject.getQuestionText() + " (" + questionObject.getResults().size() + "/" + mParticipantsCount + " " + getString(R.string.quizz_results) + ")");
                mAnswersQCMQuestionResultsLinearLayout.removeAllViews();
                int answersCount = 0;
                final HashMap<String, Integer> resultsSorted = new HashMap<String, Integer>();
                for (String solution : solutions) {
                    resultsSorted.put(solution, 0);
                }
                for (Answer answer : questionObject.getResults()) {
                    answersCount++;
                    for (String solution : resultsSorted.keySet()) {
                        if (answer.getAnswer().equals(solution)) {
                            resultsSorted.put(solution, resultsSorted.get(solution) + 1);
                        }
                    }
                }
                final int finalAnswerCount = answersCount;
                for (String solution : resultsSorted.keySet()) {
                    View view = getLayoutInflater().inflate(R.layout.linearlayout_answer_qcm, mAnswersQCMQuestionResultsLinearLayout, false);
                    final QCMAnswerResultTotalRelativeLayout layout = (QCMAnswerResultTotalRelativeLayout) view.findViewById(R.id.mQCMAnswerTotalLinearLayout);
                    mAnswersQCMQuestionResultsLinearLayout.addView(layout);
                    final String finalSolution = solution;
                    layout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setResult(resultsSorted.get(finalSolution), finalAnswerCount, finalSolution);
                        }
                    }, 250);
                }
                mQCMQuestionResultsLinearLayout.setVisibility(View.VISIBLE);
            } else if (questionObject.getType().equals(getResources().getStringArray(R.array.questions_types_array)[2])) {
//                street view question type
                mQCMQuestionResultsLinearLayout.setVisibility(View.GONE);
                mFreeQuestionResultsLinearLayout.setVisibility(View.GONE);

                mStreetViewQuestionTextView.setText(questionObject.getQuestionText() + " (" + questionObject.getResults().size() + "/" + mParticipantsCount + " " + getString(R.string.quizz_results) + ")");
                LatLng latLng = new LatLng(questionObject.getLatitude(), questionObject.getLongitude());
                mResultsMapView.getMap().clear();
                //correct response
                if (mShowMapResult) {
                    BitmapDescriptor bitmapDescriptor
                            = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE);
                    mResultsMapView.getMap().addMarker(new MarkerOptions()
                            .position(new LatLng(questionObject.getLatitude(), questionObject.getLongitude()))
                            .icon(bitmapDescriptor)
                            .title(questionObject.getQuestionText()));
                }
                //user responses
                for (Answer answer : questionObject.getResults()) {
                    mResultsMapView.getMap().addMarker(new MarkerOptions()
                            .position(new LatLng(answer.getLatitude(), answer.getLongitude()))
                            .title(answer.getUserName()));
                }
                mStreetViewQuestionResultsLinearLayout.setVisibility(View.VISIBLE);
            } else {
                //free answer
                mStreetViewQuestionResultsLinearLayout.setVisibility(View.GONE);
                mQCMQuestionResultsLinearLayout.setVisibility(View.GONE);

                mFreeQuestionTextDisplayTextView.setText(questionObject.getQuestionText() + " (" + questionObject.getResults().size() + "/" + mParticipantsCount + " " + getString(R.string.quizz_results) + ")");
                mAnswersFreeQuestionResultsLinearLayout.removeAllViews();
                for (Answer answer : questionObject.getResults()) {
                    LinearLayout resultLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.linearlayout_answer_free_text, mAnswersFreeQuestionResultsLinearLayout, false);
                    TextView usernameTextView = (TextView) resultLayout.findViewById(R.id.mUserName);
                    usernameTextView.setText(answer.getUserName());
                    TextView userAnswerTextView = (TextView) resultLayout.findViewById(R.id.mUserAnswer);
                    userAnswerTextView.setText(answer.getAnswer());
                    mAnswersFreeQuestionResultsLinearLayout.addView(resultLayout);
                }
                mFreeQuestionResultsLinearLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    @OnClick(R.id.mShowResultsButton)
    public void onShowMapResultClicked() {
        mShowMapResult = true;
        reloadActiveQuestion();
    }

    private void reloadActiveQuestion() {
        mQuestionsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> questionsDataSnapshotIterable = dataSnapshot.getChildren();
                for (DataSnapshot dataSnapshot1 : questionsDataSnapshotIterable) {
                    if (dataSnapshot1.child("status").getValue() != null && dataSnapshot1.child("status").getValue().equals(ThalesQuizzMbaasConstants.STATUS_STARTED)) {
                        checkQuestions(dataSnapshot1, null);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}