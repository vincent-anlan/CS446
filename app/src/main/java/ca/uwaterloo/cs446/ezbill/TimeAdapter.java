package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AccountBook> data;
    private String type;
    Model model;


    public TimeAdapter(Context context, ArrayList<AccountBook> data, String type) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        this.type = type;
        model = Model.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView text;

        public ViewHolder(View root) {
            super(root);
            text = (TextView) root.findViewById(R.id.Itemtext);
            text.setOnClickListener(this);
        }

        public TextView getText() {
            return text;
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            Intent intent;
            if (position > 0) {
                if (type.equals("Group")) {
                    intent = new Intent(context, GroupAccountBookDetailsActivity.class);
                } else {
                    intent = new Intent(context, IndividualAccountBookDetailsActivity.class);
                }
                model.currentTransactionList = new ArrayList<>();
                model.setClickedAccountBookId(data.get(position - 1).getId());
                context.startActivity(intent);
            } else {
                if (type.equals("Group")) {
                    intent = new Intent(context, GroupAccountBookUpsertActivity.class);
                } else {
                    intent = new Intent(context, IndividualAccountBookUpsertActivity.class);
                }
                context.startActivity(intent);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.content_main, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (position == 0) {
            viewHolder.text.setText("+ New Account Book");
        } else {
            viewHolder.text.setText(data.get(position - 1).getName());
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

}
