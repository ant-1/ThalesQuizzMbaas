package com.thales.thalesquizzmbaas.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

public class Quizz extends GenericJson implements Parcelable {

    @Key("_id")
    private String id;

    @Key
    private String creatorId;

    @Key
    private String name;

    @Key
    private String teaser;

    @Key
    private String status;

    @Key
    private List<Question> questions = new ArrayList<Question>();

    public Quizz() {
        super();
    }

    private Quizz(Parcel in) {
        super();
        id = in.readString();
        creatorId = in.readString();
        name = in.readString();
        teaser = in.readString();
        status = in.readString();
        Parcelable[] parcels = in.readParcelableArray(Question.class.getClassLoader());
        for (Parcelable parcel : parcels) {
            questions.add((Question) parcel);
        }
    }

    public static final Parcelable.Creator<Quizz> CREATOR = new Parcelable.Creator<Quizz>() {
        public Quizz createFromParcel(Parcel in) {
            return new Quizz(in);
        }

        public Quizz[] newArray(int size) {
            return new Quizz[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(creatorId);
        dest.writeString(name);
        dest.writeString(teaser);
        dest.writeString(status);
        Parcelable[] parcels = new Parcelable[questions.size()];
        int i = 0;
        for (Question question : questions) {
            parcels[i] = question;
            i++;
        }
        dest.writeParcelableArray(parcels, flags);
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return name;
    }


}
