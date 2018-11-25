package ch.epfl.sweng.studdybuddy.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.ID;
import ch.epfl.sweng.studdybuddy.firebase.MetaMeeting;
import ch.epfl.sweng.studdybuddy.services.meeting.Meeting;
import ch.epfl.sweng.studdybuddy.tools.Adapter;
import ch.epfl.sweng.studdybuddy.tools.AdapterAdapter;
import ch.epfl.sweng.studdybuddy.tools.Consumer;

public class ActivityHelper {

    public static final Comparator<Meeting> comparator = new Comparator<Meeting>() {
        @Override
        public int compare(Meeting o1, Meeting o2) {
            return (Long.compare(o1.getStarting(), o2.getStarting()));
        }
    };

    public static View.OnClickListener showDropdown(AutoCompleteTextView tv) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                tv.showDropDown();
            }
        };
    }
  /*  public static Consumer<List<Meeting>> meetingConsumer(TextView title, Button time, Button date, Button plus) {
        return new Consumer<List<Meeting>>() {
            @Override
            public void accept(List<Meeting> meetings) {
                if(meetings.size() == 0) {
                    title.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                }
                else {
                    date.setText(meetings.get(0).date());
                    time.setText(meetings.get(meetings.size()-1).time());
                    title.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    plus.setVisibility(View.GONE);
                }
            }
        };
    }*/
 /*   public static Consumer<List<Meeting>> singleMeeting(Meeting dest) {
        return new Consumer<List<Meeting>>() {
            @Override
            public void accept(List<Meeting> meetings) {
                if(!meetings.isEmpty()) dest.copy(meetings.get(0));
            }
        };
    }
 */   public static android.app.DatePickerDialog.OnDateSetListener listenDate(TextView mDisplayDate, Date startingDate, Date endingDate, AdapterAdapter adapter) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int realMonth = month+1;
                mDisplayDate.setText(realMonth + "/" + dayOfMonth + "/" + year);
                setDate(startingDate, year, month, dayOfMonth);
                setDate(endingDate, year, month, dayOfMonth);
                adapter.update();
            }
        };
    }

    private static void setDate(Date date, int year, int month, int dayOfMonth){
        date.setYear(year-1900);
        date.setMonth(month);
        date.setDate(dayOfMonth);
    }

    public static TimePickerDialog.OnTimeSetListener listenTime(TextView mDisplayTime, Date dateToSet, AdapterAdapter adapter) {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String amPm;
                if(hourOfDay >= 12){
                    amPm = " PM";
                } else{
                    amPm = " AM";
                }
                mDisplayTime.setText(hourOfDay%12 + " : " + minute/10 + minute%10 + amPm);
                dateToSet.setHours(hourOfDay);
                dateToSet.setMinutes(minute);
                adapter.update();
            }
        };
    }

    public static Consumer<List<Meeting>> getConsumerForMeetings(List<Meeting> meetingList, MetaMeeting metaM, ID<Group> groupId, RecyclerView.Adapter adapter){

     return new Consumer<List<Meeting>>() {
            @Override
            public void accept(List<Meeting> meetings) {
                meetingList.clear();
                for (Meeting m : meetings) {
                    checkMeeting(m, metaM, groupId, meetingList);
                }
                Collections.sort(meetingList, comparator);
                adapter.notifyDataSetChanged();
            }
        };
    }

    private static void checkMeeting(Meeting m, MetaMeeting metaM, ID<Group> groupId, List<Meeting> meetingList){
        Date currentDate = new Date();
        if (m.getStarting() < currentDate.getTime()) {
            metaM.deleteMeeting(m.getId(), groupId);
        } else {
            meetingList.add(m);
        }
    }

    /*
    public static void adminMeeting(Button add, Group group, String userID) {
        Boolean admin = group.getAdminID().equals(userID);
        add.setVisibility(admin ? View.VISIBLE : View.GONE);
    }
    */
}
