package ca.uwaterloo.cs446.ezbill;

import java.util.ArrayList;

public class GroupTransaction extends Transaction {

    private Participant creator;
    private Participant payer;
    private ArrayList<Participant> participants;

    public Participant getCreator() {
        return creator;
    }

    public void setCreator(Participant creator) {
        this.creator = creator;
    }

    public Participant getPayer() {
        return payer;
    }

    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}
