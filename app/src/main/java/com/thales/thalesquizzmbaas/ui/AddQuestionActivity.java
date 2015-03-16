package com.thales.thalesquizzmbaas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.thales.thalesquizzmbaas.model.Question;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends MainActivity {

    @InjectView(R.id.mQuestionTextEditText)
    EditText mQuestionTextEditText;

    @InjectView(R.id.mSolutionsLinearLayout)
    LinearLayout mSolutionsLinearLayout;

    @InjectView(R.id.mQCMSolutionsLinearLayout)
    LinearLayout mQCMSolutionsLinearLayout;

    @InjectView(R.id.mQuestionTypeSpinner)
    Spinner mQuestionTypeSpinner;

    @InjectView(R.id.mMapSolutionLinearLayout)
    LinearLayout mMapSolutionLinearLayout;

    @InjectView(R.id.mMapView)
    MapView mMapView;

    @InjectView(R.id.mStreetView)
    StreetViewPanoramaView mStreetView;

    private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);

    Marker mMarker;
    Question mQuestion;
    ArrayAdapter<CharSequence> mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        injectViews();
        mQuestion = new Question();
        mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.questions_types_array, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuestionTypeSpinner.setAdapter(mSpinnerAdapter);
        mQuestionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) mSpinnerAdapter.getItem(position);
                mQuestion.setType(type);
                if (type.equals(getResources().getStringArray(R.array.questions_types_array)[1])) {
                    //QCM type
                    resetSolutions();
                    mMapSolutionLinearLayout.setVisibility(View.GONE);
                    mQCMSolutionsLinearLayout.setVisibility(View.VISIBLE);
                } else if (type.equals(getResources().getStringArray(R.array.questions_types_array)[2])) {
                    //street view question type
                    resetSolutions();
                    mQCMSolutionsLinearLayout.setVisibility(View.GONE);
                    mMapSolutionLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    //free question type
                    resetSolutions();
                    mQCMSolutionsLinearLayout.setVisibility(View.GONE);
                    mMapSolutionLinearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMapSolutionLinearLayout.setVisibility(View.GONE);

        MapsInitializer.initialize(this);
        mMapView.onCreate(savedInstanceState);
        mStreetView.onCreate(savedInstanceState);
        setUpStreetViewPanoramaIfNeeded();
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetSolutions();
            }
        }, 500);

        mMapView.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                updateUserPosition(latLng, true, true);
            }
        });
        mStreetView.getStreetViewPanorama().setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if (streetViewPanoramaLocation != null) {
                    updateUserPosition(streetViewPanoramaLocation.position, true, false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mStreetView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mStreetView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mStreetView.onDestroy();
    }

    private void setUpStreetViewPanoramaIfNeeded() {
        if (mStreetView != null) {
            mStreetView.getStreetViewPanorama().setPanningGesturesEnabled(true);
            mStreetView.getStreetViewPanorama().setUserNavigationEnabled(true);
            mStreetView.getStreetViewPanorama().setZoomGesturesEnabled(true);
            mStreetView.getStreetViewPanorama().setStreetNamesEnabled(true);
        }
    }

    private void updateUserPosition(LatLng latLng, boolean updateMap, boolean updateStreetView) {
        if (updateMap) {
            if (mMarker != null) {
                mMarker.remove();
            }
            mMarker = mMapView.getMap().addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.question_place_to_find)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            mMapView.getMap().animateCamera(cameraUpdate);
        }
        if (updateStreetView) {
            mStreetView.getStreetViewPanorama().setPosition(latLng);
        }
        mQuestion.setLatitude(latLng.latitude);
        mQuestion.setLongitude(latLng.longitude);
    }

    private void resetSolutions() {
        mQuestion.setSolutions(new ArrayList<String>());
        mQuestion.setLatitude(0);
        mQuestion.setLongitude(0);
        mSolutionsLinearLayout.removeAllViews();
        updateUserPosition(SAN_FRAN, true, true);

    }

    @OnClick(R.id.mAddSolutionQuestionButton)
    public void addAnswer() {
        getLayoutInflater().inflate(R.layout.linearlayout_question_answer, mSolutionsLinearLayout);
    }

    @OnClick(R.id.mCreateQuestionButton)
    public void addQuestion() {
        mQuestion.setQuestionText(mQuestionTextEditText.getText().toString());
        List<String> solutions = new ArrayList<String>();
        for (int i = 0; i < mSolutionsLinearLayout.getChildCount(); i++) {
            solutions.add(((TextView) mSolutionsLinearLayout.getChildAt(i).findViewById(R.id.mAnswerTextEditText)).getText().toString());
            if (((CheckBox) mSolutionsLinearLayout.getChildAt(i).findViewById(R.id.mCorrectAnswerButton)).isChecked()) {
                mQuestion.setCorrectAnswer(((TextView) mSolutionsLinearLayout.getChildAt(i).findViewById(R.id.mAnswerTextEditText)).getText().toString());
            }
        }
        mQuestion.setSolutions(solutions);
        Intent result = new Intent();
        Parcelable[] resultParcel = new Parcelable[1];
        resultParcel[0] = mQuestion;
        result.putExtra(ThalesQuizzMbaasConstants.KEY_QUESTION, resultParcel);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
