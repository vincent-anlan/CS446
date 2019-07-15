package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class IndividualAccountBookFragment extends AccountBookFragment {

    public IndividualAccountBookFragment() {}

    @Override
    public void addDates(Model model, ArrayList<String> dates) {
        for (IndividualAccountBook individualAccountBook : model.getIndividualAccountBookList()) {
            dates.add(individualAccountBook.getEndDate());
        }
    }

    @Override
    public void setMyAdapter(Model model, RecyclerView Rv) {
        ArrayList<AccountBook> accountBooks = new ArrayList<>();
        for (IndividualAccountBook individualAccountBook : model.getIndividualAccountBookList()) {
            AccountBook accountBook = individualAccountBook;
            accountBooks.add(accountBook);
        }
        TimeAdapter myAdapter = new TimeAdapter(getActivity(), accountBooks, "Individual");
        Rv.setAdapter(myAdapter);
    }
}
