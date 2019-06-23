package ca.uwaterloo.cs446.ezbill;


import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Model extends Observable {

    private static final Model modelInstance = new Model();
    static Model getInstance() {
        return modelInstance;
    }
//    private ArrayList<Participant> participantList;

    GroupAccountBook groupAccountBook;

    Model() {
        groupAccountBook = new GroupAccountBook();
    }

    public void initObservers() {
        setChanged();
        notifyObservers();
    }

    public void addParticipant() {
        groupAccountBook.addParticipant(new Participant());
        setChanged();
        notifyObservers();

    }

    public int calculateMyExpense() {
        return 0;
    }

    public int calculateTotalExpense() {
        return 0;
    }

    public void addTranscation()
    {
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