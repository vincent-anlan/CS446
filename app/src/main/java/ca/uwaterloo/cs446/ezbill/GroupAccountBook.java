package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupAccountBook extends AccountBook {

    private int myExpense;
    private int groupExpense;
    private ArrayList<Participant> participantList;

    public int getMyExpense() {
        return myExpense;
    }

    public void setMyExpense(int myExpense) {
        this.myExpense = myExpense;
    }

    public int getGroupExpense() {
        return groupExpense;
    }

    public void setGroupExpense(int groupExpense) {
        this.groupExpense = groupExpense;
    }

    public ArrayList<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<Participant> participantList) {
        this.participantList = participantList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_account_book);


        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get the intent that started this activity
        Intent intent = getIntent();
        String text = intent.getStringExtra("title");
        title.setText(text);

        TextView numOfParticipants = (TextView)findViewById(R.id.num_of_participants);
        numOfParticipants.setText("5 People");




    }

}