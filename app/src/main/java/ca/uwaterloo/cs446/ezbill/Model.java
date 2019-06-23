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

    Model() {
        groupAccountBookList = new ArrayList<>();
        individualAccountBookList = new ArrayList<>();
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

    public int calculateMyExpense() {
        return 0;
    }

    public int calculateTotalExpense() {
        return 0;
    }

    public void addTranscation(String id)
    {
        GroupAccountBook groupAccountBook = getGroupAccountBook(id);
        groupAccountBook.setMyExpense(calculateMyExpense());
        groupAccountBook.setMyExpense(calculateTotalExpense());

        setChanged();
        notifyObservers();
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