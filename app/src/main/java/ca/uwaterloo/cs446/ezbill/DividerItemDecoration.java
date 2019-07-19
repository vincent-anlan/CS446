package ca.uwaterloo.cs446.ezbill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint1;
    private Paint mPaint2;
    private int interval_left;
    private int interval_top;
    private int circle_radius;

    private ArrayList<String> dates;

    public DividerItemDecoration() {

        int primaryColor = Color.rgb(0,133,119);

        mPaint1 = new Paint();
        mPaint1.setColor(primaryColor);

        mPaint2 = new Paint();
        mPaint2.setColor(Color.BLACK);
        mPaint2.setTextSize(25);

        interval_left = 200;
        interval_top = 50;
        circle_radius = 15;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, interval_top, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View child = parent.getChildAt(i);

            float center_x = child.getLeft() - (float) interval_left / 4;
            float center_y = child.getTop() - interval_top + (float) (interval_top + child.getHeight()) / 2;
            c.drawCircle(center_x, center_y, circle_radius, mPaint1);

            float upLine_up_x = center_x;
            float upLine_up_y = child.getTop() - interval_top;
            float upLine_bottom_x = center_x;
            float upLine_bottom_y = center_y - circle_radius;
            mPaint1.setStrokeWidth(5);
            c.drawLine(upLine_up_x, upLine_up_y, upLine_bottom_x, upLine_bottom_y, mPaint1);

            float bottomLine_up_x = center_x;
            float bottom_up_y = center_y + circle_radius;
            float bottomLine_bottom_x = center_x;
            float bottomLine_bottom_y = child.getBottom();
            c.drawLine(bottomLine_up_x, bottom_up_y, bottomLine_bottom_x, bottomLine_bottom_y, mPaint1);

            int index = parent.getChildAdapterPosition(child);
            float text_x = child.getLeft() - (float) interval_left * 9 / 10;
            float text_y = upLine_bottom_y;

            String year = dates.get(index).substring(6);
            String date = dates.get(index).substring(0,5);
            mPaint2.setTextSize(48f);
            c.drawText(date, text_x, text_y, mPaint2);
            text_y += 45;
            mPaint2.setTextSize(30f);
            c.drawText(year, text_x, text_y, mPaint2);
        }
    }

}
