package com.detroitteatime.datagatherer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.datagatherer.R;


/**
 * Created by Mark on 10/22/2015.
 */
public class ModelList extends ListActivity {
    ProgressBar pb;
    private Cursor cursor;
    private DataBaseHelper helper;
    private Button addCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_list);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        pb = (ProgressBar) findViewById(R.id.progressBar);
        addCategory = (Button)findViewById(R.id.add_category);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModelList.this, CategoryDialog.class);
                startActivity(i);
            }
        });



        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ModelList.this, CategoryDialog.class);
                intent.putExtra("id", l);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // helper.close();
        cursor.close();

    }

    @Override
    protected void onStart(){
        super.onStart();
        QueryDatabaseTask qt = new QueryDatabaseTask();
        qt.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getListView().invalidate();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(ModelList.this, MainActivity.class);
        i.putExtra(DataBaseHelper.ID, id);
        startActivity(i);
    }



    private class QueryDatabaseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper = DataBaseHelper.getInstance(ModelList.this);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pb.setVisibility(View.GONE);
            String[] columns = {DataBaseHelper.NAME, DataBaseHelper.CLASS, DataBaseHelper.METHOD};
            int[] to = {R.id.name, R.id.category, R.id.method};

            CursorAdapter adapter = new ModelAdapter(ModelList.this, cursor, 0);
            setListAdapter(adapter);

        }

        @Override
        protected Void doInBackground(Void... params) {


            cursor = helper.getPredictorData();

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
            }


            return null;
        }

    }
}

