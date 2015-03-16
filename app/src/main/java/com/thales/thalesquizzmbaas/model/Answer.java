package com.thales.thalesquizzmbaas.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Answer extends GenericJson implements Parcelable {


    @Key
    private String userId;

    @Key
    private String userName;

    @Key
    private String answer;

    @Key
    private double latitude;

    @Key
    private double longitude;


    public Answer() {
        super();
    }

    private Answer(Parcel in) {
        super();
        userId = in.readString();
        userName = in.readString();
        answer = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(answer);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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
}
