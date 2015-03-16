package com.thales.thalesquizzmbaas.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;


public class Setup extends GenericJson implements Parcelable {

    @Key("_id")
    private String id;

    @Key
    private boolean demoStarted;

    public Setup() {
        super();
    }

    private Setup(Parcel in) {
        super();
        in.readString();
        boolean[] demoStartedArray = new boolean[1];
        in.readBooleanArray(demoStartedArray);
        demoStarted = demoStartedArray[0];
    }

    public static final Creator<Setup> CREATOR = new Creator<Setup>() {
        public Setup createFromParcel(Parcel in) {
            return new Setup(in);
        }

        public Setup[] newArray(int size) {
            return new Setup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        boolean[] values = new boolean[1];
        values[0] = demoStarted;
        dest.writeBooleanArray(values);
    }

    public boolean isDemoStarted() {
        return demoStarted;
    }

    public void setDemoStarted(boolean demoStarted) {
        this.demoStarted = demoStarted;
    }
}
