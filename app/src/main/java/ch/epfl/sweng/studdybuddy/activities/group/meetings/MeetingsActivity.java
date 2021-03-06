package ch.epfl.sweng.studdybuddy.activities.group.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.activities.NavigationActivity;
import ch.epfl.sweng.studdybuddy.activities.group.GlobalBundle;
import ch.epfl.sweng.studdybuddy.core.ID;
import ch.epfl.sweng.studdybuddy.core.Pair;
import ch.epfl.sweng.studdybuddy.firebase.MetaMeeting;
import ch.epfl.sweng.studdybuddy.services.meeting.Meeting;
import ch.epfl.sweng.studdybuddy.services.meeting.MeetingLocation;
import ch.epfl.sweng.studdybuddy.services.meeting.MeetingRecyclerAdapter;
import ch.epfl.sweng.studdybuddy.util.ActivityHelper;
import ch.epfl.sweng.studdybuddy.util.Messages;
import ch.epfl.sweng.studdybuddy.util.RequestCodes;

public class MeetingsActivity extends AppCompatActivity {

    private String groupId;
    private String adminId;
    private static MetaMeeting metaM = new MetaMeeting();

    private static List<Meeting> meetingList = new ArrayList<>();

    private RecyclerView.Adapter adapter;

    private Bundle origin;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);
        origin = GlobalBundle.getInstance().getSavedBundle();

        groupId = origin.getString(Messages.groupID);
        adminId = origin.getString(Messages.ADMIN);

        if(groupId == null || adminId == null ){
            String TAG = "MEETINGS_ACTIVITY";
            Log.d(TAG, "Information of the group is not fully recovered");
            startActivity(new Intent(this, NavigationActivity.class));
        }

        RecyclerView meetingRV = findViewById(R.id.meetingRV);
        meetingRV.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MeetingRecyclerAdapter(this, this, meetingList, origin);

        metaM.getMeetingsOfGroup(new ID<>(groupId), ActivityHelper.getConsumerForMeetings(meetingList, metaM, new ID<>(groupId), adapter));

        meetingRV.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent d){
        if(requestCode == RequestCodes.CREATEMEETING.getRequestCode() && resultCode == RESULT_OK){
            GlobalBundle data = GlobalBundle.getInstance();
            origin.putAll(data.getSavedBundle());
            Meeting meeting = data.getMeeting();
            metaM.pushMeeting(meeting, new ID<>(groupId));
        }
    }

    public static void setMetaM(MetaMeeting m){
        metaM = m;
    }

    public static void setMeetingList(List<Meeting> meetingL){
        meetingList.addAll(meetingL);
    }

    @Override
    protected void onDestroy(){
        meetingList.clear();
        metaM = new MetaMeeting();
        super.onDestroy();
    }
}
