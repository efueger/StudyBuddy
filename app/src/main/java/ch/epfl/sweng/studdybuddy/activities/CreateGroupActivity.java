package ch.epfl.sweng.studdybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.core.Course;
import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.User;
import ch.epfl.sweng.studdybuddy.firebase.FirebaseReference;
import ch.epfl.sweng.studdybuddy.firebase.MetaGroup;
import ch.epfl.sweng.studdybuddy.services.calendar.Availability;
import ch.epfl.sweng.studdybuddy.services.calendar.ConnectedAvailability;
import ch.epfl.sweng.studdybuddy.tools.AdapterConsumer;
import ch.epfl.sweng.studdybuddy.tools.ArrayAdapterAdapter;
import ch.epfl.sweng.studdybuddy.tools.Consumer;
import ch.epfl.sweng.studdybuddy.tools.Intentable;
import ch.epfl.sweng.studdybuddy.util.Language;
import ch.epfl.sweng.studdybuddy.util.Messages;
import ch.epfl.sweng.studdybuddy.util.StudyBuddy;

import static ch.epfl.sweng.studdybuddy.controllers.CreateGroupController.joinGroupsAndGo;
import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.showDropdown;

public class CreateGroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private String selectedCourse="";
    private String selectedLanguage;
    private int maxParticipants = 2;//default value
    private static List<String> coursesDB;

    private static final List<String> courseSelection = new ArrayList<>();
    FirebaseReference firebase;
    MetaGroup mb;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mb = new MetaGroup();
        Intent intent = getIntent();
        setUpLang();
        setUpNumberPicker();
        firebase = new FirebaseReference();
        coursesDB = new ArrayList<>();
        coursesDB.add("untitled");
        firebase.select("courses").getAll(String.class, AdapterConsumer.adapterConsumer(String.class, coursesDB, new ArrayAdapterAdapter(setUpAutoComplete())));
        create = (Button)findViewById(R.id.confirmGroupCreation);
        create.setEnabled(false);
    }

    ArrayAdapter<String> setUpAutoComplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, coursesDB);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.courseComplete2);
        textView.setAdapter(adapter);
        textView.setOnClickListener(showDropdown(textView));
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectedCourse =  parent.getItemAtPosition(position).toString();;
                create.setEnabled(true);
            }
        });
        return adapter;
    }

    void setUpNumberPicker() {
        //Number picker
        NumberPicker np = findViewById(R.id.numberPicker);
        np.setMinValue(2);
        np.setMaxValue(10);
        np.setOnValueChangedListener(onValueChangeListener);
    }

    void setUpLang() {
        //Language spinner
        Spinner spinnerLanguage = (Spinner) findViewById(R.id.spinnerLanguage);
        spinnerLanguage.setOnItemSelectedListener(this);
        FirebaseReference ref = new FirebaseReference();
        String uId = ((StudyBuddy) this.getApplication()).getAuthendifiedUser().getUserID().getId();

        ref.select(Messages.FirebaseNode.USERS).select(uId).get(User.class, new Consumer<User>() {
            @Override
            public void accept(User user) {
                selectedLanguage = user.getFavoriteLanguage() != null ? user.getFavoriteLanguage() : Language.EN;
                spinnerLanguage.setSelection(Language.LanguageToInt(selectedLanguage));
            }
        });
        ArrayAdapter<String> dataAdapterLanguages = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Language.languages);
        dataAdapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(dataAdapterLanguages);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String item = parent.getItemAtPosition(position).toString();

        switch(parent.getId())
        {
            case R.id.spinnerLanguage:
                selectedLanguage = item;
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    public void addtoGroups(View view) {
        User user = ((StudyBuddy) CreateGroupActivity.this.getApplication()).authendifiedUser;
        Group g = new Group(maxParticipants, new Course(selectedCourse),selectedLanguage, UUID.randomUUID().toString(), user.getUserID().getId());
        Intentable toNav = new Intentable(this, NavigationActivity.class);
        joinGroupsAndGo(mb, user, g, toNav);
    }

    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener()
    {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1)
        {

            maxParticipants = numberPicker.getValue();
        }
    };

}
