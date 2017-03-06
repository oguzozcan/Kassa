package com.mallardduckapps.kassa.objects;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 16/09/16.
 */
public class Event extends BaseSwipeListItem{


    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("event_members")
    ArrayList<EventMember> eventMembers;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<EventMember> getEventMembers() {
        return eventMembers;
    }

    public void setEventMembers(ArrayList<EventMember> eventMembers) {
        this.eventMembers = eventMembers;
    }


/*   [
    {
        "Id": 0,
            "UserId": 0,
            "Name": "string",
            "StartDate": "2016-09-15T21:02:56.053Z",
            "EndDate": "2016-09-15T21:02:56.053Z",
            "CategoryId": 0,
            "SubCategoryId": 0,
            "EventMembers": [
        {
            "Id": 0,
                "PhoneNumber": "string",
                "UserPublicProfile": {
            "FirstName": "string",
                    "LastName": "string",
                    "PhoneNumber": "string",
                    "EmailAddress": "string",
                    "PhotoUrl": "string"
        },
            "CreateDate": "2016-09-15T21:02:56.054Z"
        }
        ],
        "CreateDate": "2016-09-15T21:02:56.054Z"
    }
    ]*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeList(this.eventMembers);
    }

    public Event() {
    }

    protected Event(Parcel in) {
        super(in);
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.eventMembers = new ArrayList<EventMember>();
        in.readList(this.eventMembers, EventMember.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
