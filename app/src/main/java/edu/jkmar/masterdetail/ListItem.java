package edu.jkmar.masterdetail;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Jason on 10/20/2016.
 */
public class ListItem implements Parcelable {
    String name;
    String code;
    boolean checked;
    String detail;


    public ListItem(String name, boolean bool, String code, String detail) {
        this.name = name;
        this.checked = bool;
        this.code = code;
        this.detail = detail;
    }

    public int describeContents() {
        return 0;
    }



    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(code);
        out.writeByte((byte) (checked ? 1 : 0));
    }

    public static final Creator<ListItem> CREATOR
            = new Creator<ListItem>() {
        public ListItem createFromParcel(Parcel in) {
            return new ListItem(in);
        }

        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };

    private ListItem(Parcel in) {
        name = in.readString();
        checked = in.readByte() != 0;
        code = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getChecked() {
        if(checked) {
            return "true";
        }
        return "false";
    }

    public String getDetail() { return detail;
    }

    public void setName(String name) {
        this.name=name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setChecked(String checked) {
        if(checked.equals("true")) {
            this.checked = true;
        }
        else {
            this.checked = false;
        }
    }
    public void setDetail(String detail) {this.detail=detail;}

}
