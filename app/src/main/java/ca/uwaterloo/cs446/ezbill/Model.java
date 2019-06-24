package ca.uwaterloo.cs446.ezbill;


import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.Nonnull;

public class Model extends Observable {

    private static Model modelInstance = new Model();
    static Model getInstance() {
        return modelInstance;
    }
//    private ArrayList<Participant> participantList;

    ArrayList<GroupAccountBook> groupAccountBookList;
    ArrayList<IndividualAccountBook> individualAccountBookList;
    String currentUserId;
    String setCurrentUsername;
    String clickedAccountBookId;
    ArrayList<GroupTransaction> currentGroupTransactionList;

    Model() {
        groupAccountBookList = new ArrayList<>();
        individualAccountBookList = new ArrayList<>();
        currentGroupTransactionList = new ArrayList<>();
    }


    public ArrayList<GroupTransaction> getCurrentGroupTransactionList() {
        return currentGroupTransactionList;
    }

    public void addToCurrentGroupTransactionList(GroupTransaction newTransaction) {
        currentGroupTransactionList.add(newTransaction);
        GroupAccountBook groupAccountBook = getGroupAccountBook(clickedAccountBookId);
        groupAccountBook.setMyExpense(calculateMyExpense(clickedAccountBookId));
        groupAccountBook.setMyExpense(calculateTotalExpense(clickedAccountBookId));
        addTransactionToDB(newTransaction);

        setChanged();
        notifyObservers();
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

    public boolean hasGroupTransaction(String id) {
        for (GroupTransaction groupTransaction : currentGroupTransactionList) {
            if (groupTransaction.getUuid().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addGroupTransaction(GroupTransaction groupTransaction) {
        currentGroupTransactionList.add(groupTransaction);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUsername() {
        return setCurrentUsername;
    }

    public void setCurrentUsername(String setCurrentUsername) {
        this.setCurrentUsername = setCurrentUsername;
    }

    public String getClickedAccountBookId() {
        return clickedAccountBookId;
    }

    public void setClickedAccountBookId(String clickedAccountBookId) {
        this.clickedAccountBookId = clickedAccountBookId;
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
        for (GroupTransaction groupTransaction : currentGroupTransactionList) {
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
        for (GroupTransaction groupTransaction : currentGroupTransactionList) {
            totalAmount += groupTransaction.getAmount();
        }
        return totalAmount;
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

    public void addTransactionToDB(GroupTransaction groupTransaction) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
        // Create a new user with a first and last name
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("id", groupTransaction.getUuid());
        transaction.put("category", groupTransaction.getCategory());
        transaction.put("type", groupTransaction.getType());
        transaction.put("amount", groupTransaction.getAmount().toString());
        transaction.put("currency", groupTransaction.getCurrency());
        transaction.put("note", groupTransaction.getNote());
        transaction.put("date", groupTransaction.getDate());
        transaction.put("creator", groupTransaction.getCreator().getId());
        transaction.put("payer", groupTransaction.getPayer().getId());
        Map<String, Object> participant = new HashMap<>();
        for (HashMap<Participant, Float> data : groupTransaction.getParticipants()) {
            for (HashMap.Entry<Participant,Float> entry : data.entrySet()) {
                participant.put(entry.getKey().getId(), entry.getValue());
            }
        }
        transaction.put("participant", participant);

        // Add a new document with a generated ID
        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("WRITE", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@Nonnull Exception e) {
                        Log.w("WRITE", "Error adding document", e);
                    }
                });
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