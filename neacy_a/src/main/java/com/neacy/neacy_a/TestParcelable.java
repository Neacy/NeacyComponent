package com.neacy.neacy_a;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/19
 */
public class TestParcelable implements Parcelable {

    public String name;

    public TestParcelable(String name) {
        this.name = name;
    }

    protected TestParcelable(Parcel in) {
        name = in.readString();
    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        @Override
        public TestParcelable createFromParcel(Parcel in) {
            return new TestParcelable(in);
        }

        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "name = " + name;
    }
}
