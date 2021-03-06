package ch.epfl.sweng.studdybuddy.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import ch.epfl.sweng.studdybuddy.Fragments.ChatListsFragment;
import ch.epfl.sweng.studdybuddy.Fragments.FeedFragment;
import ch.epfl.sweng.studdybuddy.Fragments.ProfileFragment;
import ch.epfl.sweng.studdybuddy.Fragments.SettingsFragment;
import ch.epfl.sweng.studdybuddy.R;

public class NavigationActivity extends AppCompatActivity
{

    private ChatListsFragment chatFragment;
    private FeedFragment feedFragment;
    private ProfileFragment profileFragment;
    private SettingsFragment settingsFragment;
    private static final String DATABASE_NAME = "StudyBuddy";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        FrameLayout mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        BottomNavigationView mMainNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        chatFragment = new ChatListsFragment();
        feedFragment = new FeedFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();
        setFragment(feedFragment);
        mMainNav.setOnNavigationItemSelectedListener(getListener());
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getListener()
    {
        return new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch(menuItem.getItemId())
                {
                    case R.id.navToChat:
                        setFragment(chatFragment);
                        return true;
                    case R.id.navToHome:
                        setFragment(feedFragment);
                        return true;
                    case R.id.navToProfile:
                        setFragment(profileFragment);
                        return true;
                    case R.id.navToSettings:
                        setFragment(settingsFragment);
                        return true;


                    default:
                        return false;
                }
            }
        };
    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment, "mainFragment");
        fragmentTransaction.commit();
    }
}
