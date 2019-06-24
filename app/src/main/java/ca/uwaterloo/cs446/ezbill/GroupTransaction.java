package ca.uwaterloo.cs446.ezbill;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupTransaction extends Transaction {

    private Participant creator;
    private Participant payer;
//    private ArrayList<Participant> participants;
    private ArrayList<HashMap<Participant, Float>> participants;

    public GroupTransaction(String category, String type, Float amount, String currency, String note, String date, Participant creator, Participant payer, ArrayList<HashMap<Participant, Float>> participants) {
        super(category, type, amount, currency, note, date);
        this.creator = creator;
        this.payer = payer;
        this.participants = participants;
    }

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

    public ArrayList<HashMap<Participant, Float>> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<HashMap<Participant, Float>> participants) {
        this.participants = participants;
    }
}
