package com.example.bluesky.vocabulary;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Bluesky on 2018/8/4.
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter {
    public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to) {
        super(context,layout,c,from,to);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView != null) {
            view = convertView;

        } else {
            view = super.getView(position, convertView, parent);

        }


/*author:conowen
 * date:2012.4.2
 * MySimpleCursorAdapter
 */

        int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };// RGB颜色

        view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

        return super.getView(position, view, parent);
    }
}
