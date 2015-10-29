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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_STATE = "com.ashoksm.allindiabankinfo.STATE";
    public final static String EXTRA_DISTRICT = "com.ashoksm.allindiabankinfo.DISTRICT";
    public final static String EXTRA_BANK = "com.ashoksm.allindiabankinfo.BANK";
    public final static String EXTRA_BRANCH = "com.ashoksm.allindiabankinfo.BRANCH";
    private static AutoCompleteTextView bankNameSpinner;
    private static AutoCompleteTextView stateNameTextView;
    private static AutoCompleteTextView districtNameTextView;
    private static EditText branchName;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bankNameSpinner = (AutoCompleteTextView) findViewById(R.id.bankName);
        stateNameTextView = (AutoCompleteTextView) findViewById(R.id.stateName);
        districtNameTextView = (AutoCompleteTextView) findViewById(R.id.districtName);
        branchName = (EditText) findViewById(R.id.branchName);

        loadAd();
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getApplicationContext(), R.array.bank_names,
                        R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        bankNameSpinner.setAdapter(adapter);

        // add listener
        bankNameSpinner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateNameTextView.setText("");
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = parent.getItemAtPosition(position).toString();
                String resourceName =
                        bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('&', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_")
                                + "_states";
                int bankId =
                        getResources().getIdentifier(resourceName, "array", getApplicationContext().getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> stateAdapter =
                            ArrayAdapter.createFromResource(getApplicationContext(), bankId,
                                    R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    stateNameTextView.setAdapter(stateAdapter);
                }
            }
        });

        stateNameTextView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = bankNameSpinner.getText().toString();
                String stateName = stateNameTextView.getText().toString();
                String resourceName =
                        bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('&', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_")
                                + "_"
                                + stateName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_") + "_districts";
                int bankId =
                        getResources().getIdentifier(resourceName, "array", getApplicationContext().getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> districtAdapter =
                            ArrayAdapter.createFromResource(getApplicationContext(), bankId,
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
                showInterstitial();
            }

        });

        branchName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    showInterstitial();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadAd() {

        // load ad
        final LinearLayout adParent = (LinearLayout) this.findViewById(R.id.adLayout);
        final AdView ad = new AdView(this);
        ad.setAdUnitId(getString(R.string.admob_id_for_banner));
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
        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest adRequest = builder.build();
        ad.loadAd(adRequest);
    }

    private void performSearch() {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(),
                InputMethodManager
                        .HIDE_NOT_ALWAYS);

        String bankName = bankNameSpinner.getText().toString();
        if (bankName.trim().length() > 0) {
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

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                performSearch();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if(bankNameSpinner.getText().toString().trim().length() > 0) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                performSearch();
                mInterstitialAd = newInterstitialAd();
                loadInterstitial();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Select a Bank!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

}
