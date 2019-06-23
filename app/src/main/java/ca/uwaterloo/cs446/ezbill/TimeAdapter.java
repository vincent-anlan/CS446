package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> listItem;
    private ArrayList<AccountBook> data;


    public TimeAdapter(Context context, ArrayList<AccountBook> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
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
            Intent groupIntent = new Intent(context,GroupAccountBookActivity.class);
            Log.d("READ", this.getAdapterPosition()+"@@@@@@@@@@@@");
//            View parentRow = (View) view.getParent();
//            RecyclerView listView = (RecyclerView) parentRow.getParent();
//            int position = listView.getChildLayoutPosition(parentRow);

            groupIntent.putExtra("accountBook", data.get(this.getAdapterPosition()));
            context.startActivity(groupIntent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.content_main, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.text.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
