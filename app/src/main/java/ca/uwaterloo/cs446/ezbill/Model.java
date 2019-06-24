package ca.uwaterloo.cs446.ezbill;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Model extends Observable {

    private static Model modelInstance = new Model();
    static Model getInstance() {
        return modelInstance;
    }
//    private ArrayList<Participant> participantList;

    ArrayList<GroupAccountBook> groupAccountBookList;
    ArrayList<IndividualAccountBook> individualAccountBookList;
    String currentUserId;
    ArrayList<GroupTransaction> currentGroupABTransactions;

    Model() {
        groupAccountBookList = new ArrayList<>();
        individualAccountBookList = new ArrayList<>();
        currentGroupABTransactions = new ArrayList<>();
    }

    public ArrayList<GroupAccountBook> getGroupAccountBookList() {
        return groupAccountBookList;
    }

    public GroupAccountBook getGroupAccountBook(String id) {
        for (GroupAccountBook groupAccountBook : groupAccountBookList) {
            if (groupAccountBook.getId().equals(id)) {
                return groupAccountBook;
            }
        }
        return null;
    }

    public void addGroupAccountBook(GroupAccountBook groupAccountBook) {
        groupAccountBookList.add(groupAccountBook);
    }

    public void addIndividualAccountBook(IndividualAccountBook individualAccountBook) {
        individualAccountBookList.add(individualAccountBook);
    }

    public boolean hasGroupAccountBook(String id) {
        for (GroupAccountBook groupAccountBook : groupAccountBookList) {
            if (groupAccountBook.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasIndividualAccountBook(String id) {
        for (IndividualAccountBook individualAccountBook : individualAccountBookList) {
            if (individualAccountBook.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addGroupTransaction(GroupTransaction groupTransaction) {
        currentGroupABTransactions.add(groupTransaction);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void initObservers() {
        setChanged();
        notifyObservers();
    }

    public void addParticipant(String id) {
        GroupAccountBook groupAccountBook = getGroupAccountBook(id);
        groupAccountBook.addParticipant(new Participant());
        setChanged();
        notifyObservers();

    }

    public ArrayList<Participant> getParticipantsById(String id) {
        GroupAccountBook groupAccountBook = getGroupAccountBook(id);
        return groupAccountBook.getParticipantList();
    }

    public float calculateMyExpense(String id) {
        float totalAmount = 0;
        for (GroupTransaction groupTransaction : currentGroupABTransactions) {
            ArrayList<HashMap<Participant, Float>> participants = groupTransaction.getParticipants();
            for (HashMap<Participant, Float> participant :  groupTransaction.getParticipants()) {
                for (HashMap.Entry<Participant,Float> entry : participant.entrySet()) {
                    Participant key = entry.getKey();
                    Float value = entry.getValue();
                    if (key.getId().equals(currentUserId)) {
                        totalAmount += value;
                    }
                }
            }
        }
        return totalAmount;
    }

    public float calculateTotalExpense(String id) {
        float totalAmount = 0;
        for (GroupTransaction groupTransaction : currentGroupABTransactions) {
            totalAmount += groupTransaction.getAmount();
        }
        return totalAmount;
    }

    public void addTranscation(String id)
    {
        GroupAccountBook groupAccountBook = getGroupAccountBook(id);
        groupAccountBook.setMyExpense(calculateMyExpense(id));
        groupAccountBook.setMyExpense(calculateTotalExpense(id));

        setChanged();
        notifyObservers();
    }

    public String getUsername(String id) {
        if (id == "U1") {
            return "Alice";
        } else if (id == "U2") {
            return "Bob";
        } else if (id == "U3") {
            return "Carol";
        } else {
            return "David";
        }
    }


    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }


    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

}