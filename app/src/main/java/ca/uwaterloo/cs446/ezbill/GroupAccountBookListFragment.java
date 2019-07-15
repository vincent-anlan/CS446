package ca.uwaterloo.cs446.ezbill;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class GroupAccountBookListFragment extends AccountBookListFragmentTemplate {

    public GroupAccountBookListFragment() {}

    @Override
    public void addDates(Model model, ArrayList<String> dates) {
        for (GroupAccountBook groupAccountBook : model.getGroupAccountBookList()) {
            dates.add(groupAccountBook.getEndDate());
        }
    }

    @Override
    public void setMyAdapter(Model model, RecyclerView Rv) {
        ArrayList<AccountBook> accountBooks = new ArrayList<>();
        for (GroupAccountBook groupAccountBook : model.getGroupAccountBookList()) {
            AccountBook accountBook = groupAccountBook;
            accountBooks.add(accountBook);
        }
        TimeAdapter myAdapter = new TimeAdapter(getActivity(), accountBooks, "Group");
        Rv.setAdapter(myAdapter);
    }
}
