package com.ashoksm.allindiabankinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static Spinner bankNameSpinner;

    private static AutoCompleteTextView stateNameTextView;

    private static AutoCompleteTextView districtNameTextView;

    private static EditText branchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bankNameSpinner = (Spinner) findViewById(R.id.bankName);
        stateNameTextView = (AutoCompleteTextView) findViewById(R.id.stateName);
        districtNameTextView = (AutoCompleteTextView) findViewById(R.id.districtName);
        branchName = (EditText) findViewById(R.id.branchName);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.bank_names,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        bankNameSpinner.setAdapter(adapter);

        // add listener
        bankNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateNameTextView.setText("");
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = parent.getItemAtPosition(position).toString();
                String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('&', ' ')
                        .replaceAll(" ", "").replaceAll("-", "_")
                        + "_states";
                int bankId = getResources().getIdentifier(resourceName, "array", getApplicationContext().getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getApplicationContext(), bankId,
                            android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    stateNameTextView.setAdapter(stateAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        stateNameTextView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = bankNameSpinner.getSelectedItem().toString();
                String stateName = stateNameTextView.getText().toString();
                String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('&', ' ')
                        .replaceAll(" ", "").replaceAll("-", "_")
                        + "_"
                        + stateName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
                        .replaceAll(" ", "").replaceAll("-", "_") + "_districts";
                int bankId = getResources().getIdentifier(resourceName, "array", getApplicationContext().getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getApplicationContext(), bankId,
                            android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    districtNameTextView.setAdapter(districtAdapter);
                }
            }
        });
    }


}
