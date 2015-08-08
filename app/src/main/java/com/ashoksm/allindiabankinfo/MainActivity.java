package com.ashoksm.allindiabankinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static Spinner bankNameSpinner;

    private static AutoCompleteTextView stateNameTextView;

    private static AutoCompleteTextView districtNameTextView;

    private static EditText branchName;

    public final static String EXTRA_STATE = "com.ashoksm.allindiabankinfo.STATE";

    public final static String EXTRA_DISTRICT = "com.ashoksm.allindiabankinfo.DISTRICT";

    public final static String EXTRA_BANK = "com.ashoksm.allindiabankinfo.BANK";

    public final static String EXTRA_BRANCH = "com.ashoksm.allindiabankinfo.BRANCH";

    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bankNameSpinner = (Spinner) findViewById(R.id.bankName);
        stateNameTextView = (AutoCompleteTextView) findViewById(R.id.stateName);
        districtNameTextView = (AutoCompleteTextView) findViewById(R.id.districtName);
        branchName = (EditText) findViewById(R.id.branchName);

        loadAd();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.bank_names,
                R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
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
                            R.layout.spinner_dropdown_item);
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
                            R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    districtNameTextView.setAdapter(districtAdapter);
                }
            }
        });

        Button btnSubmit = (Button) findViewById(R.id.ifscSearch);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(v);
            }

        });

        branchName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadAd() {
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.admob_id));

        // load ad
        final LinearLayout adParent = (LinearLayout) this.findViewById(R.id.adLayout);
        final AdView ad = new AdView(this);
        ad.setAdUnitId(getString(R.string.admob_id));
        ad.setAdSize(AdSize.SMART_BANNER);

        final AdListener listener = new AdListener() {
            @Override
            public void onAdLoaded() {
                adParent.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adParent.setVisibility(View.GONE);
                super.onAdFailedToLoad(errorCode);
            }
        };

        ad.setAdListener(listener);

        adParent.addView(ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
    }

    private void performSearch(View v) {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String bankName = bankNameSpinner.getSelectedItem().toString();
        if (!"Please Select a Bank".equals(bankName)) {
            String stateName = stateNameTextView.getText().toString();
            String districtName = districtNameTextView.getText().toString();
            String branch = branchName.getText().toString();
            Intent intent = new Intent(getApplicationContext(), DisplayBankBranchResultActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_STATE, stateName.trim());
            intent.putExtra(EXTRA_DISTRICT, districtName.trim());
            intent.putExtra(EXTRA_BANK, bankName.trim());
            intent.putExtra(EXTRA_BRANCH, branch.trim());
            getApplicationContext().startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left, 0);
        } else {
            Toast.makeText(getApplicationContext(), "Please Select a Bank!!!", Toast.LENGTH_LONG).show();
        }
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    public void onBackPressed() {
        displayInterstitial();
        super.onBackPressed();
    }

}
