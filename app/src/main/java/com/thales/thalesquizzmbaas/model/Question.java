package com.thales.thalesquizzmbaas.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.HashMap;
import java.util.List;

public class Question extends GenericJson implements Parcelable {

    @Key
    private String type;

    @Key
    private String questionText;

    @Key
    private String status;

    @Key
    private List<String> solutions;

    @Key
    private String correctAnswer;

    @Key
    private double latitude;

    @Key
    private double longitude;

    @Key
    private List<Answer> results;

    public Question() {
        super();
    }

    private Question(Parcel in) {
        super();
        type = in.readString();
        questionText = in.readString();
        status = in.readString();
        solutions = in.readArrayList(String.class.getClassLoader());
        correctAnswer = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        results = in.readArrayList(HashMap.class.getClassLoader());
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(questionText);
        dest.writeString(status);
        dest.writeList(solutions);
        dest.writeString(correctAnswer);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeList(results);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<String> solutions) {
        this.solutions = solutions;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Answer> getResults() {
        return results;
    }

    public void setResults(List<Answer> results) {
        this.results = results;
    }
}
