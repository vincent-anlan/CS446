package ca.uwaterloo.cs446.ezbill;


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
    String setCurrentUsername;
    String clickedAccountBookId;
    ArrayList<GroupTransaction> currentGroupTransactionList;

    Model() {
        groupAccountBookList = new ArrayList<>();
        individualAccountBookList = new ArrayList<>();
        currentGroupTransactionList = new ArrayList<GroupTransaction>();
    }


    public ArrayList<GroupTransaction> getCurrentGroupTransactionList() {
        return currentGroupTransactionList;
    }

    public void addToCurrentGroupTransactionList(GroupTransaction newTransaction) {
        currentGroupTransactionList.add(newTransaction);
        GroupAccountBook groupAccountBook = getGroupAccountBook(clickedAccountBookId);
        groupAccountBook.setMyExpense(calculateMyExpense());
        groupAccountBook.setMyExpense(calculateTotalExpense());

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

    public int calculateMyExpense() {
        return 0;
    }

    public int calculateTotalExpense() {
        return 0;
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