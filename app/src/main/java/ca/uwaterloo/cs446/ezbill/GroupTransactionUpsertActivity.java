
package ca.uwaterloo.cs446.ezbill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GroupTransactionUpsertActivity extends TransactionUpsertActivityTemplate {

    private Spinner mSelectPayer;
    private String payerIDSaveString;
    private String sumSaveString;
    private ArrayList<String> selectName;
    private ArrayList<String> selectId;
    private HashMap<Participant, Float> select_participants;
    private ArrayList<String> pstring;
    private Button mSum;

    ArrayList<Participant> participants;

    private Button mParticipant;
    private String[] listPart;
    private boolean[] checkedPart;
    private ArrayList<Integer> mUserPart = new ArrayList<>();

    LinearLayout linearLayout_v;
    LinearLayout.LayoutParams params;
    LinearLayout.LayoutParams params_h;

    ArrayList<String> collectSumParticipant;
    float totalExpense;
    int onetimeUse;
    ArrayList<EditText> allEds;

    public int dpTopx(int dp) {
        return (int) (10 * Resources.getSystem().getDisplayMetrics().density);
    }

    public void setPayerSelector() {
        //set payer
        mSelectPayer = (Spinner) findViewById(R.id.payer_spinner);

        pstring = new ArrayList<>();
        participants = model.getGroupAccountBook(model.getClickedAccountBookId()).getParticipantList();
        for (Participant participant : participants) {
            pstring.add(participant.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, pstring);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectPayer.setAdapter(adapter);
        mSelectPayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(GroupTransactionUpsertActivity.this, "Selected:" + selectedvalue, Toast.LENGTH_SHORT).show();
                payerIDSaveString = participants.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setPaticipantAutoSum() {
        //COLLECT sum
        allEds = new ArrayList<EditText>();
        selectName = new ArrayList<>();
        selectId = new ArrayList<>();
        collectSumParticipant = new ArrayList<>();
        sumSaveString = "";

        mSum = (Button) findViewById(R.id.totalAmount);
        onetimeUse = 0;
        totalExpense = Float.parseFloat("0.0");

        //select participant
        linearLayout_v = (LinearLayout) findViewById(R.id.listparticipant);
        linearLayout_v.setOrientation(LinearLayout.VERTICAL);
        mParticipant = (Button) findViewById(R.id.selectParticipant);

        listPart = new String[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            listPart[i] = participants.get(i).getName();
        }
        checkedPart = new boolean[listPart.length];
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(0, dpTopx(10), dpTopx(1000), dpTopx(10));
        params_h = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupTransactionUpsertActivity.this);
                mBuilder.setTitle("Select Participants For Current Transaction");
                mBuilder.setMultiChoiceItems(listPart, checkedPart, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserPart.contains(position)) {
                                mUserPart.add(position);
                            }
                        } else if (mUserPart.contains(position)) {
                            mUserPart.remove(position);
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ArrayList<String> collect = new ArrayList<>();
                        for (int i = 0; i < mUserPart.size(); i++) {
                            String item = listPart[mUserPart.get(i)];
                            String id = participants.get(mUserPart.get(i)).getId();
                            selectName.add(item);
                            selectId.add(id);
                            collect.add(item);
                        }
                        for (int i = 0; i < collect.size(); i++) {
                            String item = collect.get(i);
                            collectSumParticipant.add(item);

                            TextView btn = new TextView(GroupTransactionUpsertActivity.this);
                            btn.setText(item);
                            btn.setTextSize(25);
                            btn.setLayoutParams(params);
                            btn.setGravity(Gravity.START);

                            EditText subExpense = new EditText(GroupTransactionUpsertActivity.this);
                            subExpense.setTextSize(25);
                            subExpense.setLayoutParams(params);
                            subExpense.setGravity(Gravity.CENTER);
                            subExpense.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            allEds.add(subExpense);

                            LinearLayout linearLayout_h = new LinearLayout(GroupTransactionUpsertActivity.this);
                            linearLayout_h.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout_h.setGravity(Gravity.START);
                            linearLayout_h.addView(btn);
                            linearLayout_h.addView(subExpense);
                            linearLayout_v.setLayoutParams(params_h);
                            linearLayout_v.addView(linearLayout_h);
                        }
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedPart.length; i++) {
                            checkedPart[i] = false;
                            mUserPart.clear();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        mSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onetimeUse == 0) {
                    for (int i = 0; i < allEds.size(); i++) {
                        String item = allEds.get(i).getText().toString();
                        float f = Float.parseFloat(item);
                        totalExpense = totalExpense + f;
                    }
                    mSum.setText(Float.toString(totalExpense));
                    sumSaveString = Float.toString(totalExpense);
                    select_participants = new HashMap<>();
                    for (int i = 0; i < allEds.size(); i++) {
                        String item = allEds.get(i).getText().toString();
                        float f = Float.parseFloat(item);
                        String checkName = selectName.get(i);
                        String checkId = selectId.get(i);
                        Participant p = new Participant(checkId, checkName);
                        select_participants.put(p, f);
                    }
                    onetimeUse = 1;
                } else {
                }
            }
        });
    }

    @Override
    public void initView() {
        LinearLayout transLayout = (LinearLayout) findViewById(R.id.addtransTemplate);
        View newView = getLayoutInflater().inflate(R.layout.add_trans_detail_group, transLayout, false);
        transLayout.addView(newView);

        setPayerSelector();
        setPaticipantAutoSum();
    }

    @Override
    public void saveButtonHandler(View v) {
        Participant transactionCreator = new Participant(model.getCurrentUserId(), model.getCurrentUsername());
        Participant payer = new Participant(payerIDSaveString, mSelectPayer.getSelectedItem().toString());
        String uuid = UUID.randomUUID().toString();

        String note = getNote();
        String date = getDate();
        String currency = getSelectedCurrency();
        String category = getCategory();

        GroupTransaction newGroupTransaction = new GroupTransaction(uuid, category, "Expense", Float.valueOf(sumSaveString), currency, note, date, transactionCreator, payer, select_participants);

        model.addToCurrentTransactionList(newGroupTransaction, true);
        finish();
    }

    @Override
    public void cameraScan(View view) {
    }
}
