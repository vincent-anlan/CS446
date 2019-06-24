package ca.uwaterloo.cs446.ezbill;


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
        for (GroupTransaction groupTransaction : currentGroupTransactionList) {
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