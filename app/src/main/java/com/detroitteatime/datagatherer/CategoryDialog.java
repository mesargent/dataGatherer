package com.detroitteatime.datagatherer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.datagatherer.R;

public class CategoryDialog extends Activity {
    private Button enter;
    private TextView name, category;
    private RadioGroup methodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_dialog);
        methodGroup = (RadioGroup)findViewById(R.id.method_group);
        name = (TextView)findViewById(R.id.name);
        category = (TextView)findViewById(R.id.category);
        enter = (Button)findViewById(R.id.enter_category);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = methodGroup.getCheckedRadioButtonId();
                RadioButton selected = (RadioButton)findViewById(selectedId);
                String method = selected.getText().toString();

                switch(method){
                    case "Logistic Regression":
                        method = Constants.LOGISIC_REGRESSION;
                        break;
                    case "Support Vector Machine":
                        method = Constants.SVM;
                        break;
                    case "Neural Net":
                        method = Constants.NEURAL_NET;
                        break;
                }

                DataBaseHelper helper = new DataBaseHelper(CategoryDialog.this);
                helper.insertModel(name.getText().toString(),category.getText().toString(), method, null, null);
                helper.close();

                Intent intent = new Intent(CategoryDialog.this, ModelList.class);
                startActivity(intent);
            }
        });
    }
}