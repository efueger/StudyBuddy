package ch.epfl.sweng.studdybuddy.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.activities.CourseSelectActivity;
import ch.epfl.sweng.studdybuddy.activities.NavigationActivity;
import ch.epfl.sweng.studdybuddy.core.Account;
import ch.epfl.sweng.studdybuddy.core.ID;
import ch.epfl.sweng.studdybuddy.core.User;
import ch.epfl.sweng.studdybuddy.firebase.FirebaseReference;
import ch.epfl.sweng.studdybuddy.firebase.ReferenceWrapper;
import ch.epfl.sweng.studdybuddy.sql.SqlWrapper;
import ch.epfl.sweng.studdybuddy.tools.Consumer;
import ch.epfl.sweng.studdybuddy.tools.Intentable;
import ch.epfl.sweng.studdybuddy.util.StudyBuddy;

import static ch.epfl.sweng.studdybuddy.controllers.GoogleSigninController.fetchUserAndStart;
import static ch.epfl.sweng.studdybuddy.controllers.GoogleSigninController.fetchUserAndStartConsumer;
import static ch.epfl.sweng.studdybuddy.controllers.GoogleSigninController.fetchUserCallback;
import static ch.epfl.sweng.studdybuddy.sql.DAOs.SqlConsumers.clearAndFill;

public class GoogleSignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GoogleActivity";

    private AuthManager mAuth = null;

    private StudyBuddy app;
    private SqlWrapper sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        SignInButton mGoogleBtn = findViewById(R.id.googleBtn);
        sql = new SqlWrapper(this);
        app =  ((StudyBuddy) GoogleSignInActivity.this.getApplication());
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTest()){
                    onActivityResult(1,0,null);
                }else{
                    getAuthManager().startLoginScreen();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        Account acct = getAuthManager().getCurrentUser();
        if (acct != null ) {
            String personName = acct.getDisplayName();
            //appears only when the user is connected
            Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
            List<User> users = new ArrayList<>();
            sql.getUser(acct.getId(), Consumer.sequenced(clearAndFill(users), fetchUserAndStartConsumer(acct, app, getBaseContext())));
        } else {
            //appears only when the user isn't connected to the app
            Toast.makeText(this, "No User", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInWrapper gsw = new GoogleSignInWrapper(onTest());
            Task<GoogleSignInAccount> task = gsw.getTask(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Account account = getRightAccount(task);
                getAuthManager().login(account, new OnLoginCallback() {
                    @Override
                    public void then(Account acct) {
                        if (acct != null) {
                            if (onTest()) {
                                startActivity(new Intent(GoogleSignInActivity.this, CourseSelectActivity.class));
                            } else {
                                fetchUserAndStart(mAuth.getCurrentUser(), app, getBaseContext());
                            }
                        }
                    }
                }, TAG);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed.", e);
            }
        }
    }

    AuthManager getAuthManager(){
        if (mAuth == null){
            mAuth = new FirebaseAuthManager(this, getString(R.string.default_web_client_id));
        }
        return mAuth;
    }

    boolean onTest(){
        return false;
    }

    private Account getRightAccount(Task<GoogleSignInAccount> task) throws ApiException {
        return onTest() ? new Account() : Account.from(task.getResult(ApiException.class));
    }

}
