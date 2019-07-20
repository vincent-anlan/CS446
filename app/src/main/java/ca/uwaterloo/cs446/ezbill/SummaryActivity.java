package ca.uwaterloo.cs446.ezbill;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity {

    Model model;
    HashMap<String, Float> summary;
    Float total;

    View lineSeparator;
    LinearLayout linearLayout_v;
    LinearLayout.LayoutParams params_v;
    LinearLayout.LayoutParams params_tv;

    FloatingActionButton menu;
    FloatingActionButton delete;
    FloatingActionButton edit;
    FloatingActionButton add;
    boolean isMenuOpen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();

        setContentView(R.layout.summary_page);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.summary_page_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(model.getIndividualAccountBook(model.getClickedAccountBookId()).getName()+ "Summary");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        calculatePercentage();
        showPieChart();
        showCategoryWithDescendingOrder();
        initFloatingActionMenu();
    }

    private void calculatePercentage() {
        summary = new HashMap<>();
        total = (float) 0;
        for (Transaction transaction : model.getCurrentTransactionList()) {
            // add total expense value to the payer
            IndividualTransaction individualTransaction = (IndividualTransaction) transaction;
            String category = individualTransaction.getCategory();
            Float amount = individualTransaction.getAmount();
            total += amount;
            if (summary.containsKey(category)) {
                summary.put(category, summary.get(category) + amount);
            } else {
                summary.put(category, amount);
            }
        }
    }

    private void initFloatingActionMenu() {
        menu = (FloatingActionButton) findViewById(R.id.menu);
        delete = (FloatingActionButton) findViewById(R.id.delete);
        edit = (FloatingActionButton) findViewById(R.id.edit);
        add = (FloatingActionButton) findViewById(R.id.add);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });

        delete.setOnClickListener(onButtonClick());
        edit.setOnClickListener(onButtonClick());
        add.setOnClickListener(onButtonClick());
    }

    private void toggleMenu() {
        if (!isMenuOpen) {
            delete.animate().translationY(-getResources().getDimension(R.dimen.delete));
            edit.animate().translationY(-getResources().getDimension(R.dimen.edit));
            add.animate().translationY(-getResources().getDimension(R.dimen.add));
            isMenuOpen = true;
        } else {
            delete.animate().translationY(0);
            edit.animate().translationY(0);
            add.animate().translationY(0);
            isMenuOpen = false;
        }
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
                Intent intent;
                if (view.getId() == R.id.delete) {
                    intent = new Intent(SummaryActivity.this, IndividualTransactionUpsertActivity.class);
                } else if (view.getId() == R.id.edit) {
                    intent = new Intent(SummaryActivity.this, IndividualTransactionUpsertActivity.class);
                } else {
                    intent = new Intent(SummaryActivity.this, IndividualTransactionUpsertActivity.class);
                }
                startActivity(intent);
            }
        };
    }


    private void showPieChart() {
        // initialize id of Pie Chart
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);

        // enable DataSet in Percentage
        pieChart.setUsePercentValues(true);

        // create dataset
        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        PieDataSet dataSet = new PieDataSet(values, "");
        int index = 0;
        for (HashMap.Entry<String, Float> entry : summary.entrySet()) {
            Float percentage = entry.getValue()/total;
            values.add(new Entry(percentage, index));
            labels.add(entry.getKey());
        }

        PieData data = new PieData(labels, dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // set hole radius
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(30f);

        // set text size and color
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        // disable description
        pieChart.setDescription(null);

        //show legend
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);
    }

    public void addRow(String leftText, String rightText) {
        LinearLayout linearLayout_h = new LinearLayout(this);
        linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv1 = new TextView(this);
        tv1.setText(leftText);
        tv1.setLayoutParams(params_tv);
        tv1.setGravity(Gravity.START);
        linearLayout_h.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(rightText);
        tv2.setLayoutParams(params_tv);
        tv2.setGravity(Gravity.END);
        linearLayout_h.addView(tv2);

        linearLayout_v.addView(linearLayout_h);
    }
    private void showCategoryWithDescendingOrder() {
        linearLayout_v = (LinearLayout) findViewById(R.id.category_list);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        params_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_v.setLayoutParams(params_v);
        params_tv = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params_tv.setMargins(dpTopx(30), 0, dpTopx(30), 0);


        // convert hashmap to list and sort
        List<Map.Entry<String, Float>> list =
                new LinkedList<Map.Entry<String, Float>>(summary.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // display the entries
        for (Map.Entry<String, Float> entry : list) {
            Float amount = entry.getValue();
            Float percentage = amount * 100 / total;
            addRow(entry.getKey(), amount.toString());
            addRow(String.format("%.1f%%", percentage), "CAD");
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, linearLayout_v, false);
            linearLayout_v.addView(lineSeparator);
        }
    }

    private int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }
}
