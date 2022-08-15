package com.example.vcl_etlrechargevoucher;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CALL = 1;
    private static final String TAG = "MainActivity";

    FloatingActionButton FAB;
    TextView rechargeNumber;
    AdView adView, mAdView;
 //   ImageButton openCam;

    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-6326528360221769~1671641993");

      //  openCam = findViewById(R.id.openCam);
       /* try{
            openCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BlankFragment fragment = new BlankFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.screen_area, fragment, "");
                    ft2.commit();
                }
            });
        }catch (NullPointerException ignored){

        }*/

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        // Load an ad into the AdMob banner view.
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-6326528360221769/9886664625");
        mAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("FA1E51F5E28BAC515FF93EB763F2ED89")
                .build();

        mAdView.loadAd(adRequest);

        interstitial =  new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        interstitial.loadAd(adRequest);

        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(MainActivity.this, "Could not load ads", Toast.LENGTH_SHORT).show();
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(MainActivity.this, "Ad has been opened", Toast.LENGTH_SHORT).show();
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivity.this, "Ad has been loaded", Toast.LENGTH_SHORT).show();
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(MainActivity.this, "You clicked on the add", Toast.LENGTH_SHORT).show();
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

        FAB = findViewById(R.id.FAB);
        rechargeNumber = findViewById(R.id.Recharge);

        FAB = findViewById(R.id.FAB);
//tap button to speak to application and enter recharge pin
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }

        });

        interstitial.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
            @Override
            public void onAdLoaded() {
                displayInterstitial();
            }
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
        loadRewardedVideoAd();

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    private void displayInterstitial() {
        if(interstitial.isLoaded()){
            interstitial.show();
        }
    }

    private void makePhoneCall() {
        String Recharger = rechargeNumber.getText().toString().trim();

        if (Recharger.length() == 14) {
            callETL();
        } else if (Recharger.length() == 16 || Recharger.length() == 17) {
            callVCL();
        } else {
            Toast.makeText(MainActivity.this, "Recharge Voucher Incompelete "+ Recharger.length(), Toast.LENGTH_SHORT).show();
        }
    }
    //function to recharge vodacom lesotho recharge voucher
    public void callVCL(){
        String number = rechargeNumber.getText().toString();
        if(number.trim().length() > 0){

            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String [] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {

                String encodedHash = Uri.encode("#");
                String mNumber = "*100*01*" + number;

                String R_Text = mNumber + encodedHash;
                String dial = "tel:"+R_Text;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }
    }
    //function to recharge Econet Telecom Lesotho recharge voucher
    public void callETL(){
        String number = rechargeNumber.getText().toString();
        if(number.trim().length() > 0){

            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String [] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {

                String encodedHash = Uri.encode("#");
                String mNumber = "*133*" + number;

                String R_Text = mNumber + encodedHash;
                String dial = "tel:"+R_Text;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }
    }
    //function to request user to allow permission to use call utilities
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    //function that creates the platform to speak to the application
    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "enter numbers to recharge");

        try{

            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        }catch(Exception e){

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                rechargeNumber.setText(result.get(0));

            }
        }

        makePhoneCall();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {
        Log.i(TAG, "Rewarded: onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
