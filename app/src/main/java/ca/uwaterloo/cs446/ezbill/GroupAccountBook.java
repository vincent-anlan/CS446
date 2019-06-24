package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupAccountBook extends AccountBook implements Serializable {

    private int myExpense;
    private int groupExpense;
    private ArrayList<Participant> participantList;

    GroupAccountBook(String id, String name,  String startDate, String endDate, String defaultCurrency) {
            super(id, name, startDate, endDate, defaultCurrency);
            myExpense = 0;
            groupExpense = 0;
            Participant participant = new Participant();
            participantList = new ArrayList<Participant>();
            participantList.add(participant);
    }

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

    public void addParticipant(Participant participant) {
        this.participantList.add(participant);
    }



}