package ca.uwaterloo.cs446.ezbill;

import java.util.ArrayList;

public class GroupAccountBook {

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
}
