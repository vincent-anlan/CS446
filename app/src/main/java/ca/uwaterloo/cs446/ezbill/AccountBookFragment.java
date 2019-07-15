package ca.uwaterloo.cs446.ezbill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public abstract class AccountBookFragment extends Fragment implements Observer {

    Model model;
    private ArrayList<String> dates;
    private RecyclerView Rv;

    public AccountBookFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        model = Model.getInstance();
        model.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_book_fragment, container, false);
        // Init data for timeline
        dates = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(new Date());
        dates.add(date);

        // Init RecyclerView for timeline
        Rv = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();

        addDates(model, dates);
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        setMyAdapter(model, Rv);

        return view;
    }

    public abstract void addDates(Model model, ArrayList<String> dates);

    public abstract void setMyAdapter(Model model, RecyclerView Rv);

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration();
        addDates(model, dates);
        dividerItemDecoration.setDates(dates);
        Rv.addItemDecoration(dividerItemDecoration);
        setMyAdapter(model, Rv);
    }
}
