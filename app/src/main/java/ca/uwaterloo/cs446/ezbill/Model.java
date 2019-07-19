package ca.uwaterloo.cs446.ezbill;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    ArrayList<GroupAccountBook> groupAccountBookList;
    ArrayList<IndividualAccountBook> individualAccountBookList;
    String currentUserId;
    String currentUsername;
    String clickedAccountBookId;
    ArrayList<Transaction> currentTransactionList;
    String userEmail = "alice@gmail.com";
    boolean viewAllBillClicked;

    Model() {
        groupAccountBookList = new ArrayList<>();
        individualAccountBookList = new ArrayList<>();
        currentTransactionList = new ArrayList<>();
        viewAllBillClicked = false;
        readAccountBooksFromDB();
    }


    public boolean getViewAllBillClicked() {
        return viewAllBillClicked;
    }

    public void setViewAllBillClicked(boolean isClicked) {
        viewAllBillClicked = isClicked;
        setChanged();
        notifyObservers();

    }

    public ArrayList<Transaction> getCurrentTransactionList() {
        return currentTransactionList;
    }

    public void addToCurrentTransactionList(Transaction newTransaction, boolean isGroup) {
        currentTransactionList.add(newTransaction);
        addTransactionToDB(newTransaction, isGroup);
        Collections.sort(currentTransactionList);

        if (isGroup) {
            GroupAccountBook groupAccountBook = getGroupAccountBook(clickedAccountBookId);
            groupAccountBook.setMyExpense(calculateMyExpense(clickedAccountBookId));
            groupAccountBook.setGroupExpense(calculateTotalExpense(clickedAccountBookId));
        } else {
            IndividualAccountBook individualAccountBook = getIndividualAccountBook(clickedAccountBookId);
            individualAccountBook.setExpense(calculateTotalExpense(clickedAccountBookId));
        }
        setChanged();
        notifyObservers();
    }

    public void addToCurrentGroupAccountBookList(GroupAccountBook newGroupAccountBook, String email, String userId, String username) {
        groupAccountBookList.add(newGroupAccountBook);
        addAccountBookToDB(newGroupAccountBook, "Group", email, userId, username);
        Collections.sort(groupAccountBookList);

        setChanged();
        notifyObservers();
    }

    public void addToCurrentIndividualAccountBookList(IndividualAccountBook newGroupAccountBook, String email, String userId, String username) {
        individualAccountBookList.add(newGroupAccountBook);
        addAccountBookToDB(newGroupAccountBook, "Individual", email, userId, username);
        Collections.sort(individualAccountBookList);

        setChanged();
        notifyObservers();
    }

    public void removeFromGroupAccountBookList(String id) {
        for (IndividualAccountBook individualAccountBook : individualAccountBookList) {
            if (individualAccountBook.getId().equals(id)) {
                individualAccountBookList.remove(individualAccountBook);
                Collections.sort(individualAccountBookList);
            }
        }
        setChanged();
        notifyObservers();
    }

    public void removeFromIndividualAccountBookList(String id) {
        IndividualAccountBook individualAccountBook = null;
        for (IndividualAccountBook accountBook : individualAccountBookList) {
            if (accountBook.getId().equals(id)) {
                individualAccountBook = accountBook;
            }
        }
        individualAccountBookList.remove(individualAccountBook);
        Collections.sort(individualAccountBookList);
        deleteAccountBookInDB(id);

        setChanged();
        notifyObservers();
    }

    public void updateAccountBookInGroupList(GroupAccountBook updatedAccountBook) {
        int index = 0;
        for (int i = 0; i < groupAccountBookList.size(); i++) {
            if (groupAccountBookList.get(i).getId().equals(updatedAccountBook.getId())) {
                index = i;
            }
        }
        groupAccountBookList.set(index, updatedAccountBook);
        updateAccountBookInDB(updatedAccountBook);

        setChanged();
        notifyObservers();
    }

    public void updateAccountBookInIndividualList(IndividualAccountBook updatedAccountBook) {
        int index = 0;
        for (int i = 0; i < individualAccountBookList.size(); i++) {
            if (individualAccountBookList.get(i).getId().equals(updatedAccountBook.getId())) {
                index = i;
            }
        }
        individualAccountBookList.set(index, updatedAccountBook);
        updateAccountBookInDB(updatedAccountBook);

        setChanged();
        notifyObservers();
    }

    public ArrayList<GroupAccountBook> getGroupAccountBookList() {
        return groupAccountBookList;
    }

    public ArrayList<IndividualAccountBook> getIndividualAccountBookList() {
        return  individualAccountBookList;
    }

    public GroupAccountBook getGroupAccountBook(String id) {
        for (GroupAccountBook groupAccountBook : groupAccountBookList) {
            if (groupAccountBook.getId().equals(id)) {
                return groupAccountBook;
            }
        }
        return null;
    }

    public IndividualAccountBook getIndividualAccountBook(String id) {
        for (IndividualAccountBook individualAccountBook : individualAccountBookList) {
            if (individualAccountBook.getId().equals(id)) {
                return individualAccountBook;
            }
        }
        return null;
    }

    public void addGroupAccountBook(GroupAccountBook groupAccountBook) {
        groupAccountBookList.add(groupAccountBook);
        Collections.sort(groupAccountBookList);
    }

    public void addIndividualAccountBook(IndividualAccountBook individualAccountBook) {
        individualAccountBookList.add(individualAccountBook);
        Collections.sort(individualAccountBookList);
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

    public boolean hasTransaction(String id) {
        for (Transaction transaction : currentTransactionList) {
            if (transaction.getUuid().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addTransaction(Transaction transaction) {
        currentTransactionList.add(transaction);
        Collections.sort(currentTransactionList);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
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

    public boolean hasParticipant(String id, String userId) {
        ArrayList<Participant> participants = getGroupAccountBook(id).getParticipantList();
        for (Participant participant : participants) {
            if (participant.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public void addParticipant(String id, Participant participant) {
        GroupAccountBook groupAccountBook = getGroupAccountBook(id);
        groupAccountBook.addParticipant(participant);
        setChanged();
        notifyObservers();

    }

    public ArrayList<Participant> getParticipantsById(String id) {
        GroupAccountBook groupAccountBook = getGroupAccountBook(id);
        return groupAccountBook.getParticipantList();
    }

    public float calculateMyExpense(String id) {
        float totalAmount = 0;
        for (Transaction transaction : currentTransactionList) {
            HashMap<Participant, Float> participants =  ((GroupTransaction) transaction).getParticipants();
            for (HashMap.Entry<Participant,Float> entry : participants.entrySet()) {
                Participant key = entry.getKey();
                Float value = entry.getValue();
                if (key.getId().equals(currentUserId)) {
                    totalAmount += value;
                }
            }
        }
        return totalAmount;
    }

    public float calculateTotalExpense(String id) {
        float totalAmount = 0;
        for (Transaction transaction : currentTransactionList) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    public String getUsername(String id) {
        ArrayList<Participant> participants = getGroupAccountBook(getClickedAccountBookId()).getParticipantList();
        for (Participant participant : participants) {
            if (participant.getId().equals(id)) {
                return participant.getName();
            }
        }
        return "anonym";
    }

    public Date parseStringToDate(String date) throws Exception{
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date parsedDate = (Date) formatter.parse(date);
        return parsedDate;
    }

    public String parseDateToString(Date date) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public void readAccountBooksFromDB() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//      read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getData().get("userId").toString();
                                setCurrentUserId(userId);
                                String username = document.getData().get("username").toString();
                                setCurrentUsername(username);

                                String accountBookId = document.getData().get("accountBookId").toString();
                                String accountBookName = document.getData().get("accountBookName").toString();
                                String startDate = document.getData().get("accountBookStartDate").toString();
                                String endDate = document.getData().get("accountBookEndDate").toString();
                                String defaultCurrency = document.getData().get("accountBookCurrency").toString();
                                String type = document.getData().get("accountBookType").toString();

                                if (type.equals("Group")) {
                                    if (!hasGroupAccountBook(accountBookId)) {
                                        GroupAccountBook groupAccountBook = new GroupAccountBook(accountBookId, accountBookName, startDate, endDate, defaultCurrency);
                                        addGroupAccountBook(groupAccountBook);
                                    }
                                } else {
                                    if (!hasIndividualAccountBook(accountBookId)) {
                                        IndividualAccountBook individualAccountBook = new IndividualAccountBook(accountBookId, accountBookName, startDate, endDate, defaultCurrency);
                                        addIndividualAccountBook(individualAccountBook);
                                    }
                                }
                                Log.d("READ", document.getId() + " => " + document.getData());
                            }

                            setChanged();
                            notifyObservers();

                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readTransactionsFromDB(final boolean isGroup) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("transactions")
                .whereEqualTo("accountBookId", getClickedAccountBookId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String transactionId = document.getData().get("id").toString();

                                if (!hasTransaction(transactionId)) {
                                    String category = document.getData().get("category").toString();
                                    float amount = Float.valueOf(document.getData().get("amount").toString());
                                    String date = document.getData().get("date").toString();
                                    String note = document.getData().get("note").toString();
                                    String type = document.getData().get("type").toString();
                                    String currency = document.getData().get("currency").toString();

                                    Transaction transaction;
                                    if (!isGroup) {
                                        transaction = new IndividualTransaction(transactionId, category, type, amount, currency, note, date);
                                    } else {
                                        String creatorId = document.getData().get("creator").toString();
                                        String creatorName = getUsername(creatorId);
                                        Participant creator = new Participant(creatorId, creatorName);

                                        String payerId = document.getData().get("payer").toString();
                                        String payerName = getUsername(payerId);
                                        Participant payer = new Participant(payerId, payerName);

                                        String data = document.getData().get("participant").toString();
                                        data = data.substring(1,data.length()-1);

                                        HashMap<Participant, Float> participants = new HashMap<>();
                                        String[] pairs = data.split(", ");
                                        for (int i=0;i<pairs.length;i++) {
                                            String pair = pairs[i];
                                            String[] keyValue = pair.split("=");
                                            Participant participant = new Participant(keyValue[0], getUsername(keyValue[0]));
                                            participants.put(participant, Float.valueOf(keyValue[1]));
                                        }

                                        transaction = new GroupTransaction(transactionId, category, type, amount, currency, note, date, creator, payer, participants);
                                    }
                                    addTransaction(transaction);
                                }

                                Log.d("READ", document.getId() + " => " + document.getData());
                            }
                            if (isGroup) {
                                getGroupAccountBook(getClickedAccountBookId()).setMyExpense(calculateMyExpense(getClickedAccountBookId()));
                                getGroupAccountBook(getClickedAccountBookId()).setGroupExpense(calculateTotalExpense(getClickedAccountBookId()));
                            } else {
                                getIndividualAccountBook(getClickedAccountBookId()).setExpense(calculateTotalExpense(getClickedAccountBookId()));
                            }

                            setChanged();
                            notifyObservers();
                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readParticipantsFromDB() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("accountBookId", getClickedAccountBookId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GroupAccountBook groupAccountBook = getGroupAccountBook(getClickedAccountBookId());

                                String userId = document.getData().get("userId").toString();
                                String username = document.getData().get("username").toString();

                                if (!hasParticipant(getClickedAccountBookId(), userId)) {
                                    Participant participant = new Participant(userId, username);
                                    groupAccountBook.addParticipant(participant);
                                }

                                Log.d("READ", document.getId() + " => " + document.getData());
                            }

                            setChanged();
                            notifyObservers();
                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void addTransactionToDB(Transaction newTransaction, boolean isGroup) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
        // Create a new user with a first and last name
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("accountBookId", clickedAccountBookId);
        transaction.put("id", newTransaction.getUuid());
        transaction.put("category", newTransaction.getCategory());
        transaction.put("type", newTransaction.getType());
        transaction.put("amount", newTransaction.getAmount().toString());
        transaction.put("currency", newTransaction.getCurrency());
        transaction.put("note", newTransaction.getNote());
        transaction.put("date", newTransaction.getDate());

        if (isGroup) {
            transaction.put("creator", ((GroupTransaction) newTransaction).getCreator().getId());
            transaction.put("payer", ((GroupTransaction) newTransaction).getPayer().getId());
            Map<String, Object> participant = new HashMap<>();
            HashMap<Participant, Float> data = ((GroupTransaction) newTransaction).getParticipants();
            for (HashMap.Entry<Participant,Float> entry : data.entrySet()) {
                participant.put(entry.getKey().getId(), entry.getValue());
            }
            transaction.put("participant", participant);
        }

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

    public void addAccountBookToDB(AccountBook accountBook, String type, String email, String userId, String username) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> ab = new HashMap<>();
        ab.put("accountBookId", accountBook.getId());
        ab.put("accountBookName", accountBook.getName());
        ab.put("accountBookCurrency", accountBook.getDefaultCurrency());
        ab.put("accountBookStartDate", accountBook.getStartDate());
        ab.put("accountBookEndDate", accountBook.getEndDate());
        ab.put("accountBookType", type);
        ab.put("email", email);
        ab.put("userId", userId);
        ab.put("username", username);

        // Add a new document with a generated ID
        db.collection("user_account_book_info")
                .add(ab)
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

    public void deleteAccountBookInDB(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("accountBookId", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                Log.d("WRITE", "Document deleted");
                            }
                        } else {
                            Log.w("WRITE", "Error deleting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateAccountBookInDB(AccountBook accountBook) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String, Object> updates = new HashMap<>();
        updates.put("accountBookName", accountBook.getName());
        updates.put("accountBookCurrency", accountBook.getDefaultCurrency());

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("accountBookId", accountBook.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(updates);

                                Log.d("WRITE", "Document updated");
                            }
                        } else {
                            Log.w("WRITE", "Error getting documents.", task.getException());
                        }
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