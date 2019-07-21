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

public abstract class AccountBookListFragmentTemplate extends Fragment implements Observer {

    Model model;
    ArrayList<String> dates;
    RecyclerView Rv;

    public AccountBookListFragmentTemplate() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        model = Model.getInstance();
        model.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = initView(inflater, container);

        // Init date for timeline
        initDate();

        addDateToView();

        addAccountBookInfoToView();

        return view;
    }

    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.account_book_fragment, container, false);

        // Init RecyclerView for timeline
        Rv = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);

        return view;
    }

    public void initDate() {
        dates = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(new Date());
        dates.add(date);
    }

    public abstract void addDateToView();

    public abstract void addAccountBookInfoToView();

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        while (Rv.getItemDecorationCount() > 0) {
            Rv.removeItemDecorationAt(0);
        }
        addDateToView();
        addAccountBookInfoToView();
    }
}
