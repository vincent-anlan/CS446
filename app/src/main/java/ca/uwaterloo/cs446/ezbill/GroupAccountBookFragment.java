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

public class GroupAccountBookFragment extends Fragment implements Observer {

    Model model;
    private ArrayList<String> dates;
    private TimeAdapter myAdapter;
    private RecyclerView Rv;
    private Context context;

    public GroupAccountBookFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        model = Model.getInstance();
        model.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.group_account_book_fragment, container, false);
        // Init data for timeline
        dates = new ArrayList<>();
        dates.add("07/13/2019");

        // Init RecyclerView for timeline
        Rv = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();

        for (GroupAccountBook groupAccountBook : model.getGroupAccountBookList()) {
            dates.add(groupAccountBook.getEndDate());
        }
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        myAdapter = new TimeAdapter(getActivity(), model.getGroupAccountBookList());
        Rv.setAdapter(myAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();
        for (GroupAccountBook groupAccountBook : model.getGroupAccountBookList()) {
            dates.add(groupAccountBook.getEndDate());
        }
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        myAdapter = new TimeAdapter(getActivity(), model.getGroupAccountBookList());
        Rv.setAdapter(myAdapter);
    }
}
