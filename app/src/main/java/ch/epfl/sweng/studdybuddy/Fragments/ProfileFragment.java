package ch.epfl.sweng.studdybuddy.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.activities.CourseSelectActivity;
import ch.epfl.sweng.studdybuddy.activities.NavigationActivity;
import ch.epfl.sweng.studdybuddy.auth.AuthManager;
import ch.epfl.sweng.studdybuddy.auth.FirebaseAuthManager;
import ch.epfl.sweng.studdybuddy.auth.GoogleSignInActivity;
import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.User;
import ch.epfl.sweng.studdybuddy.firebase.FirebaseReference;
import ch.epfl.sweng.studdybuddy.firebase.MetaGroup;
import ch.epfl.sweng.studdybuddy.firebase.ReferenceWrapper;
import ch.epfl.sweng.studdybuddy.tools.CourseAdapter;
import ch.epfl.sweng.studdybuddy.tools.GroupsRecyclerAdapter;
import ch.epfl.sweng.studdybuddy.tools.Intentable;
import ch.epfl.sweng.studdybuddy.tools.RecyclerAdapterAdapter;
import ch.epfl.sweng.studdybuddy.util.StudyBuddy;

import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.onClickLaunch;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment
{

    private final List<String> userCourses =  new ArrayList<>();
    private  final List<Group> userGroups = new ArrayList<>();
    private GroupsRecyclerAdapter ad;
    private CourseAdapter adCourse;
    private User user;
    private String userID;
    private MetaGroup metabase = new MetaGroup();
    private AuthManager mAuth = null;

    public ProfileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUser();
        setUI(v);
        setCoursesUp();
        setGroupsUp();

        return v;
    }

    public void setupUser() {
        user = ((StudyBuddy) getActivity().getApplication()).getAuthendifiedUser();
        userID = user.getUserID().toString();
    }

    public ValueEventListener setGroupsUp() {
        metabase.addListenner(new RecyclerAdapterAdapter(ad));
        return metabase.getUserGroups(userID, userGroups);
    }

    public ValueEventListener setCoursesUp() {
        metabase.addListenner(new RecyclerAdapterAdapter(adCourse));
        return metabase.getUserCourses(userID, userCourses);
    }

    private void setUI(View v){
        TextView nameView = (TextView) v.findViewById(R.id.profile_name_text);
        nameView.setText(user.getName());

        adCourse = new CourseAdapter(userCourses);
        RecyclerView recyclerView_courses = (RecyclerView) v.findViewById(R.id.courses_list);
        adCourse.initRecyclerView(v.getContext(), recyclerView_courses);

        ad = new GroupsRecyclerAdapter(userGroups, userID);
        RecyclerView recyclerView_groups = (RecyclerView) v.findViewById(R.id.groups_list);
        recyclerView_groups.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView_groups.setAdapter(ad);
        //Intentable toCourseEdit = new Intentable(v.getContext(), new Intent(v.getContext(), CourseSelectActivity.class));
        Button editCourses = v.findViewById(R.id.editCourses);
        editCourses.setOnClickListener(onClickLaunch(new Intentable(v.getContext(), CourseSelectActivity.class)));
    }

    public ReferenceWrapper getDB(){
        return new FirebaseReference();
    }
    public AuthManager getAuthManager()
    {
        if(mAuth == null)
        {
            //mAuth = new FirebaseAuthManager(NavigationActivity.this, getString(R.string.default_web_client_id));
        }
        return mAuth;
    }

    /*
    *
    @Override
    protected void onStart()
    {
        super.onStart();
        Account currentUser = getAuthManager().getCurrentUser();
        if(currentUser == null)
        {
            signOut();
        }
    }

    * */


}
