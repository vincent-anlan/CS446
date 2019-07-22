package ca.uwaterloo.cs446.ezbill;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
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
    String accountBookDefaultCurrency;

    View lineSeparator;
    LinearLayout linearLayout_v;
    LinearLayout.LayoutParams params_v;

    public void cancelButtonHandlerBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();

        setContentView(R.layout.summary_page);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.summary_page_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Summary");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        calculateCategoryTotal();
        showPieChart();
        showCategoryWithDescendingOrder();
    }

    private void calculateCategoryTotal() {
        summary = new HashMap<>();
        total = (float) 0;
        accountBookDefaultCurrency = model.getIndividualAccountBook(model.getClickedAccountBookId()).getDefaultCurrency();
        for (Transaction transaction : model.getCurrentTransactionList()) {
            // add total expense value to the payer
            IndividualTransaction individualTransaction = (IndividualTransaction) transaction;
            String category = individualTransaction.getCategory();
            Float amount = model.convertToABDefaultCurrency(individualTransaction.getAmount(),individualTransaction.getCurrency(),accountBookDefaultCurrency);
            total += amount;
            if (summary.containsKey(category)) {
                summary.put(category, summary.get(category) + amount);
            } else {
                summary.put(category, amount);
            }
        }
    }

    private void showPieChart() {
        // initialize id of Pie Chart
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);

        // enable DataSet in Percentage
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Expense");

        // create dataset
        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        int index = 0;
        for (HashMap.Entry<String, Float> entry : summary.entrySet()) {
            Float percentage = entry.getValue()/total;
            values.add(new Entry(percentage, index));
            labels.add(entry.getKey());
            ++index;
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        PieData data = new PieData(labels, dataSet);

        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // set hole radius
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(30f);
        pieChart.animateY(1000, Easing.EasingOption.Linear);
        pieChart.animateX(1000, Easing.EasingOption.Linear);

        // set text size and color
        data.setValueTextSize(13f);
//        data.setDrawValues(false);
//        pieChart.setDrawSliceText(false);
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


    private void showCategoryWithDescendingOrder() {
        linearLayout_v = (LinearLayout) findViewById(R.id.category_list);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        params_v = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_v.setLayoutParams(params_v);


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
            Float amount = BigDecimal.valueOf(entry.getValue()).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
            Float percentage = BigDecimal.valueOf(amount * 100 / total).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();

            LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            TextView tv1 = new TextView(this);
            tv1.setText(entry.getKey());
            tv1.setLayoutParams(params_tv);
            tv1.setGravity(Gravity.START);

            TextView tv2 = new TextView(this);
            tv2.setText(percentage + "%");
            tv2.setLayoutParams(params_tv);
            tv2.setGravity(Gravity.START);

            TextView tv3 = new TextView(this);
            tv3.setText(accountBookDefaultCurrency + " " + amount);
            tv3.setLayoutParams(params_tv);
            tv3.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);

            /*** add two textview (Category and Percentage) as a column and place it on the left ***/
            LinearLayout.LayoutParams params_v1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
            params_v1.setMargins(dpTopx(30), 0, dpTopx(10), 0);
            LinearLayout linearLayout_v_left = new LinearLayout(this);
            linearLayout_v_left.setOrientation(LinearLayout.VERTICAL);
            linearLayout_v_left.setLayoutParams(params_v1);
            linearLayout_v_left.addView(tv1);
            linearLayout_v_left.addView(tv2);
            /*** add one textview (Amount) as a column and place it on the right ***/
            LinearLayout.LayoutParams params_v2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);
            params_v2.setMargins(dpTopx(10), 0, dpTopx(30), 0);
            LinearLayout linearLayout_v_right = new LinearLayout(this);
            linearLayout_v_right.setOrientation(LinearLayout.VERTICAL);
            linearLayout_v_right.setLayoutParams(params_v2);
            linearLayout_v_right.addView(tv3);

            /*** add this two columns into a horizontal layout ***/
            LinearLayout linearLayout_h = new LinearLayout(this);
            linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout_h.addView(linearLayout_v_left);
            linearLayout_h.addView(linearLayout_v_right);

            /*** add this horizontal layout into the overall vertical layout ***/
            linearLayout_v.addView(linearLayout_h);
            /*** add a line separator into the overall vertical layout ***/
            lineSeparator = getLayoutInflater().inflate(R.layout.line_separator, linearLayout_v, false);
            linearLayout_v.addView(lineSeparator);
        }
    }

    private int dpTopx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }
}
