package com.detroitteatime.datagatherer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.datagatherer.R;

/**
 * Created by Mark on 10/24/2015.
 */
public class ModelAdapter extends CursorAdapter {

    private Cursor cur;
    private final LayoutInflater inflater;



    public ModelAdapter(Context context, Cursor cur, int flag){
        super(context, cur, flag);
        this.cur = cur;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row, parent, false);
        TextView nameView = (TextView)view.findViewById(R.id.name);
        TextView classView = (TextView)view.findViewById(R.id.category);
        TextView methodView = (TextView)view.findViewById(R.id.method);
        view.setTag(R.id.name, nameView);
        view.setTag(R.id.category, classView);
        view.setTag(R.id.method, methodView);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView)view.getTag(R.id.name)).setText(cur.getString(cur.getColumnIndex(DataBaseHelper.NAME)));
        ((TextView)view.getTag(R.id.category)).setText(cur.getString(cur.getColumnIndex(DataBaseHelper.CLASS)));
        ((TextView)view.getTag(R.id.method)).setText(cur.getString(cur.getColumnIndex(DataBaseHelper.METHOD)));
    }
}
