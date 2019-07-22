package ca.uwaterloo.cs446.ezbill;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
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
    String profilePhotoURL;
    String clickedAccountBookId;
    String mIndividualExpense;
    ArrayList<Transaction> currentTransactionList;
    String userEmail;
    HashMap<String, Float> exchangeRates;

    Model() {
        groupAccountBookList = new ArrayList<>();
        individualAccountBookList = new ArrayList<>();
        currentTransactionList = new ArrayList<>();
        exchangeRates = new HashMap<>();
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
            groupAccountBook.setMyExpense(calculateMyExpense(groupAccountBook.getDefaultCurrency()));
            groupAccountBook.setGroupExpense(calculateTotalExpense(groupAccountBook.getDefaultCurrency()));
        } else {
            IndividualAccountBook individualAccountBook = getIndividualAccountBook(clickedAccountBookId);
            individualAccountBook.setExpense(calculateTotalExpense(individualAccountBook.getDefaultCurrency()));
            individualAccountBook.setIncome(calculateTotalIncome(individualAccountBook.getDefaultCurrency()));
        }
        setChanged();
        notifyObservers();
    }

    public void removeFromCurrentTransactionList(Transaction transaction) {
        currentTransactionList.remove(transaction);
        deleteTransactionInDB(transaction.getUuid());
        setChanged();
        notifyObservers();
    }

    public void updateTransactionInCurrentList(Transaction transaction, boolean isGroup) {
        int index = 0;
        for (int i = 0; i < currentTransactionList.size(); i++) {
            if (currentTransactionList.get(i).getUuid().equals(transaction.getUuid())) {
                index = i;
            }
        }
        currentTransactionList.set(index, transaction);
        Collections.sort(currentTransactionList);
        updateTransactionInDB(transaction, isGroup);

        if (isGroup) {
            GroupAccountBook groupAccountBook = getGroupAccountBook(clickedAccountBookId);
            groupAccountBook.setMyExpense(calculateMyExpense(groupAccountBook.getDefaultCurrency()));
            groupAccountBook.setGroupExpense(calculateTotalExpense(groupAccountBook.getDefaultCurrency()));
        } else {
            IndividualAccountBook individualAccountBook = getIndividualAccountBook(clickedAccountBookId);
            individualAccountBook.setExpense(calculateTotalExpense(individualAccountBook.getDefaultCurrency()));
            individualAccountBook.setIncome(calculateTotalIncome(individualAccountBook.getDefaultCurrency()));
        }

        setChanged();
        notifyObservers();
    }

    public void addToCurrentGroupAccountBookList(GroupAccountBook newGroupAccountBook, String email, String userId, String username, String userPhotoUrl) {
        groupAccountBookList.add(newGroupAccountBook);
        addAccountBookToDB(newGroupAccountBook, "Group", email, userId, username, userPhotoUrl);
        Collections.sort(groupAccountBookList);

        setChanged();
        notifyObservers();
    }

    public void addToCurrentIndividualAccountBookList(IndividualAccountBook newGroupAccountBook, String email, String userId, String username, String userPhotoUrl) {
        individualAccountBookList.add(newGroupAccountBook);
        addAccountBookToDB(newGroupAccountBook, "Individual", email, userId, username, userPhotoUrl);
        Collections.sort(individualAccountBookList);

        setChanged();
        notifyObservers();
    }

    public void removeFromGroupAccountBookList(String id) {
        GroupAccountBook groupAccountBook = null;
        for (GroupAccountBook accountBook : groupAccountBookList) {
            if (accountBook.getId().equals(id)) {
                groupAccountBook = accountBook;
            }
        }
        groupAccountBookList.remove(groupAccountBook);
        Collections.sort(groupAccountBookList);
        deleteAccountBookInDB(id);
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

    public Transaction getTransaction(String id) {
        for (Transaction transaction : currentTransactionList) {
            if (transaction.getUuid().equals(id)) {
                return transaction;
            }
        }
        return null;
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

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public float calculateMyExpense(String accountBookCurrency) {
        float totalAmount = 0;
        for (Transaction transaction : currentTransactionList) {
            String currency = transaction.getCurrency();
            float rate = exchangeRates.get(accountBookCurrency) / exchangeRates.get(currency);
            HashMap<Participant, Float> participants =  ((GroupTransaction) transaction).getParticipants();
            for (HashMap.Entry<Participant,Float> entry : participants.entrySet()) {
                Participant key = entry.getKey();
                Float value = entry.getValue();
                if (key.getId().equals(currentUserId)) {
                    totalAmount += value * rate;
                }
            }
        }
        return BigDecimal.valueOf(totalAmount).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public float calculateTotalExpense(String accountBookCurrency) {
        float totalAmount = 0;
        for (Transaction transaction : currentTransactionList) {
            if(transaction.getType().equals("Expense")){
                String currency = transaction.getCurrency();
                float value = transaction.getAmount();
                float rate = exchangeRates.get(accountBookCurrency) / exchangeRates.get(currency);
                totalAmount += value * rate;
            }else{

            }
        }
        return BigDecimal.valueOf(totalAmount).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public float calculateTotalIncome(String accountBookCurrency) {
        float totalAmount = 0;
        for (Transaction transaction : currentTransactionList) {
            if(transaction.getType().equals("Income")){
                String currency = transaction.getCurrency();
                float value = transaction.getAmount();
                float rate = exchangeRates.get(accountBookCurrency) / exchangeRates.get(currency);
                totalAmount += value * rate;
            }else{

            }
        }
        return BigDecimal.valueOf(totalAmount).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public float convertToABDefaultCurrency(float amount, String fromCurrency, String toCurrency) {
        float rate = exchangeRates.get(toCurrency) / exchangeRates.get(fromCurrency);
        return  amount * rate;
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

    public String getPhotoUri(String id) {
        ArrayList<Participant> participants = getGroupAccountBook(getClickedAccountBookId()).getParticipantList();
        for (Participant participant : participants) {
            if (participant.getId().equals(id)) {
                return participant.getPhotoUri();
            }
        }
        return "";
    }

    public String getUserEmail(String id) {
        ArrayList<Participant> participants = getGroupAccountBook(getClickedAccountBookId()).getParticipantList();
        for (Participant participant : participants) {
            if (participant.getId().equals(id)) {
                return participant.getPhotoUri();
            }
        }
        return "";
    }

    public void updateProfilePhotoUrl(String photoUrl) {
        setProfilePhotoURL(photoUrl);
        for (GroupAccountBook groupAccountBook : groupAccountBookList) {
            for (Participant participant : groupAccountBook.getParticipantList()) {
                if (participant.getId().equals(currentUserId)) {
                    participant.setPhotoUri(photoUrl);
                }
            }
        }
        updateProfilePhotoUrlInDB(photoUrl);

        setChanged();
        notifyObservers();
    }

    public void updateUsername(String username) {
        setCurrentUsername(username);
        for (GroupAccountBook groupAccountBook : groupAccountBookList) {
            for (Participant participant : groupAccountBook.getParticipantList()) {
                if (participant.getId().equals(currentUserId)) {
                    participant.setName(username);
                }
            }
        }
        updateUsernameInAuth(username);
        updateUsernameInDB(username);

        setChanged();
        notifyObservers();
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

    public void setExchangeRates(HashMap<String, Float> rates) {
        exchangeRates = rates;
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
                                        String creatorPhoto = getPhotoUri(creatorId);
                                        String creatorEmail = getUserEmail(creatorId);
                                        Participant creator = new Participant(creatorId, creatorName, creatorPhoto, creatorEmail);

                                        String payerId = document.getData().get("payer").toString();
                                        String payerName = getUsername(payerId);
                                        String payerPhoto = getPhotoUri(payerId);
                                        String payerEmail = getUserEmail(payerId);
                                        Participant payer = new Participant(payerId, payerName, payerPhoto, payerEmail);

                                        String data = document.getData().get("participant").toString();
                                        data = data.substring(1,data.length()-1);

                                        HashMap<Participant, Float> participants = new HashMap<>();
                                        String[] pairs = data.split(", ");
                                        for (int i=0;i<pairs.length;i++) {
                                            String pair = pairs[i];
                                            String[] keyValue = pair.split("=");
                                            Participant participant = new Participant(keyValue[0], getUsername(keyValue[0]), getPhotoUri(keyValue[0]), getUserEmail(keyValue[0]));
                                            participants.put(participant, Float.valueOf(keyValue[1]));
                                        }

                                        transaction = new GroupTransaction(transactionId, category, type, amount, currency, note, date, creator, payer, participants);
                                    }
                                    addTransaction(transaction);
                                }

                                Log.d("READ", document.getId() + " => " + document.getData());
                            }
                            if (isGroup) {
                                GroupAccountBook groupAccountBook = getGroupAccountBook(getClickedAccountBookId());
                                groupAccountBook.setMyExpense(calculateMyExpense(groupAccountBook.getDefaultCurrency()));
                                groupAccountBook.setGroupExpense(calculateTotalExpense(groupAccountBook.getDefaultCurrency()));
                            } else {
                                IndividualAccountBook individualAccountBook = getIndividualAccountBook(getClickedAccountBookId());
                                individualAccountBook.setExpense(calculateTotalExpense(individualAccountBook.getDefaultCurrency()));
                                individualAccountBook.setIncome(calculateTotalIncome(individualAccountBook.getDefaultCurrency()));
                            }

                            setChanged();
                            notifyObservers();
                        } else {
                            Log.w("READ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readParticipantsFromDB(final String groupAccountBookId) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("accountBookId", groupAccountBookId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GroupAccountBook groupAccountBook = getGroupAccountBook(groupAccountBookId);

                                String userId = document.getData().get("userId").toString();
                                String username = document.getData().get("username").toString();
                                String photoUri = document.getData().get("userPhotoUrl").toString();
                                String userEmail = document.getData().get("email").toString();

                                if (!hasParticipant(groupAccountBook.getId(), userId)) {
                                    Participant participant = new Participant(userId, username, photoUri, userEmail);
                                    groupAccountBook.addParticipant(participant);
                                }

                                Log.d("READ", document.getId() + " => " + document.getData());
                            }
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

    public void addAccountBookToDB(AccountBook accountBook, String type, String email, String userId, String username, String userPhotoUrl) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> ab = new HashMap<>();
        ab.put("accountBookId", accountBook.getId());
        ab.put("accountBookName", accountBook.getName());
        ab.put("accountBookCurrency", accountBook.getDefaultCurrency());
        ab.put("accountBookStartDate", accountBook.getStartDate());
        ab.put("accountBookEndDate", accountBook.getEndDate());
        ab.put("accountBookType", type);
        ab.put("accountBookCreator", accountBook.getCreatorId());
        ab.put("email", email);
        ab.put("userId", userId);
        ab.put("username", username);
        ab.put("userPhotoUrl", userPhotoUrl);

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

    public void deleteTransactionInDB(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // read data from database
        db.collection("transactions")
                .whereEqualTo("id", id)
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

    public void updateTransactionInDB(Transaction transaction, Boolean isGroup) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String, Object> updates = new HashMap<>();
        updates.put("amount", transaction.getAmount());
        updates.put("category", transaction.getCategory());
        updates.put("currency", transaction.getCurrency());
        updates.put("date", transaction.getDate());
        updates.put("note", transaction.getNote());
        if (isGroup) {
            updates.put("payer", ((GroupTransaction) transaction).getPayer().getId());
            HashMap<Participant, Float> participants = ((GroupTransaction) transaction).getParticipants();
            Map<String, Object> data = new HashMap<>();
            for (HashMap.Entry<Participant,Float> entry : participants.entrySet()) {
                data.put(entry.getKey().getId(), entry.getValue());
            }
            updates.put("participant", data);
        }

        // read data from database
        db.collection("transactions")
                .whereEqualTo("id", transaction.getUuid())
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

    public void updateProfilePhotoUrlInDB(String photoUrl) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String, Object> updates = new HashMap<>();
        updates.put("userPhotoUrl", photoUrl);

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("userId", currentUserId)
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

    public void updateUsernameInDB(String username) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        // read data from database
        db.collection("user_account_book_info")
                .whereEqualTo("userId", currentUserId)
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

    public void updateUsernameInAuth(String username) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //add username;
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                //.setPhotoUri(Uri.parse("https://www.google.com/logo.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("00", "User profile updated.");
                        }
                    }
                });
    }


    public void cameraUpdateExpense(String fromCamera){
        mIndividualExpense = fromCamera;
        setChanged();
        notifyObservers();
        Log.i("model","create");
    }
    public String getCameraUpdateExpense(){
        Log.i("model","called in");
        return mIndividualExpense;
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