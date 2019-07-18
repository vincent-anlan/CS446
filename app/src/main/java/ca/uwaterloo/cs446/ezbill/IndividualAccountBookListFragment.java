package ca.uwaterloo.cs446.ezbill;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class IndividualAccountBookListFragment extends AccountBookListFragmentTemplate {

    public IndividualAccountBookListFragment() {}

    @Override
    public void addDateToView(Model model, ArrayList<String> dates, RecyclerView Rv) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();
        for (IndividualAccountBook individualAccountBook : model.getIndividualAccountBookList()) {
            dates.add(individualAccountBook.getEndDate());
        }
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void addAccountBookInfoToView(Model model, RecyclerView Rv) {
        ArrayList<AccountBook> accountBooks = new ArrayList<>();
        for (IndividualAccountBook individualAccountBook : model.getIndividualAccountBookList()) {
            AccountBook accountBook = individualAccountBook;
            accountBooks.add(accountBook);
        }
        TimeAdapter myAdapter = new TimeAdapter(getActivity(), accountBooks, "Individual");
        Rv.setAdapter(myAdapter);
    }
}
