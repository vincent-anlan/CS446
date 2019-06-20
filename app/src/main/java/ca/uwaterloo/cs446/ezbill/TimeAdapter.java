package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private ArrayList<String> data;

    public TimeAdapter(Context context, ArrayList<String> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;

        public ViewHolder(View root) {
            super(root);
            text = (TextView) root.findViewById(R.id.Itemtext);
        }

        public TextView getText() {
            return text;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.content_main, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.text.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
