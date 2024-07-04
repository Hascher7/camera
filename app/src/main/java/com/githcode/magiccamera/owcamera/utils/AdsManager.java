package com.githcode.magiccamera.owcamera.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.githcode.magiccamera.owcamera.MainActivity;
import com.githcode.magiccamera.owcamera.R;
import com.google.ads.consent.AdProvider;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AdsManager {
    private static final String TAG = AdsManager.class.getSimpleName();
    public static InterstitialAd mInterstitialAd;
    private static ConsentForm form;

    public static void initiOrShowGdprForm(final Context ctx, boolean showForm, Activity activity, boolean showAd) {
        if(ctx == null || activity == null || activity.isFinishing()) return;

        ConsentInformation consentInformation = ConsentInformation.getInstance(ctx);
        consentInformation.addTestDevice("34BA965035FE4A66D98A5B1496D91EB0");
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        String[] publisherIds = {ctx.getString(R.string.pub_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("CONSENT","displaying consent status"+consentStatus);
                // User's consent status successfully updated.
                if(showForm){
                    if (consentInformation.isRequestLocationInEeaOrUnknown()) {
                        displayConsentForm(ctx, consentInformation, activity, showAd);
                    } else {
//                        initializeAdmob(ctx, activity);
                    }
                }else{
                    if(consentInformation.getConsentStatus() == ConsentStatus.UNKNOWN){
                        if (consentInformation.isRequestLocationInEeaOrUnknown()) {
                            displayConsentForm(ctx, consentInformation, activity, showAd);
                        } else {
//                            initializeAdmob(ctx, activity);
                        }
                    }else{
//                        initializeAdmob(ctx, activity);
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }


    private static void displayConsentForm(Context context, ConsentInformation consentInformation, Activity activity, boolean showAd) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ads_show", false) && !showAd) {
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("ads_show", true)
                .apply();

        Log.d("CONSENT","displaying consent");
        URL privacyUrl = null;
        try {
            privacyUrl = new URL("https://docs.google.com/document/d/1hyoEMQBouocqlr8VSVYpLZNXufPk53nn1tt4k9kA5DA/edit#heading=h.r83oez369v93");
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing privacy policy url", e);
        }
        form = new ConsentForm.Builder(context, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        if(context != null) {
                            form.show();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        if (consentStatus.equals(ConsentStatus.PERSONALIZED)) {
                            consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);
//                            initializeAdmob(context, activity);
                        } else {
                            consentInformation.setConsentStatus(ConsentStatus.NON_PERSONALIZED);
//                            initializeAdmob(context, activity);
                        }
                    }
                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error. This usually happens if the user is not in the EU.
                        Log.e(TAG, "Error loading consent form: " + errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();

        form.load();
    }

    public static void initializeAdmob(final Context ctx, Activity activity) {
        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        InterstitialAd.load(ctx,ctx.getString(R.string.adsId), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        loadAd(ctx, activity);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    public static void loadAd(Context ctx, Activity activity) {
        ConsentInformation consentInformation = ConsentInformation.getInstance(ctx);
        if (consentInformation.getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");

            List<String> testDeviceIds = Collections.singletonList("77ECFA3F01EBB7FD94F178109E4A1C66");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        } else {
            List<String> testDeviceIds = Collections.singletonList("77ECFA3F01EBB7FD94F178109E4A1C66");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }
        mInterstitialAd.show(activity);
    }

    public static void showAds(@NonNull Context ctx, AdsClosedCallback adsClosedCallback, Activity activity) {
        if (mInterstitialAd == null) {
            initializeAdmob(ctx, activity);
            return;
        }
        if (mInterstitialAd != null && PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("show_ads", true)) {
            mInterstitialAd.show(activity);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    adsClosedCallback.launchActivity();
                    PreferenceManager
                            .getDefaultSharedPreferences(ctx)
                            .edit()
                            .putBoolean("show_ads", false)
                            .apply();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
        }else {
            adsClosedCallback.launchActivity();
            PreferenceManager
                    .getDefaultSharedPreferences(ctx)
                    .edit()
                    .putBoolean("show_ads", false)
                    .apply();
        }
    }

    public  interface AdsClosedCallback{
        void launchActivity();
    }

    public static void showConsentDialog(final Context ctx, final List<AdProvider> adProviders, final ConsentInformation consentInformation, final Handler.Callback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        LayoutInflater inflater = MainActivity.act.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.gdprcostumdialog, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final LinearLayout admobConsentForm = alertDialog.findViewById(R.id.gdprAciklamaLinear);
        final LinearLayout admobProviderLayout = alertDialog.findViewById(R.id.gdprListLinear);
        final ListView listView = alertDialog.findViewById(R.id.gdprListVoew);
        Button backButton = alertDialog.findViewById(R.id.gdprBackButton);
        Button listButton = alertDialog.findViewById(R.id.gdprListButton);
        Button acceptButton = alertDialog.findViewById(R.id.gdprAcceptButton);
        Button rejectButton = alertDialog.findViewById(R.id.gdprRejectButton);

        TextView aciklamaTextView = alertDialog.findViewById(R.id.gdprAciklamaTxView);
        TextView messageTextView = alertDialog.findViewById(R.id.gdprmessage);


        aciklamaTextView.setMovementMethod(LinkMovementMethod.getInstance());
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString ss = new SpannableString(" Learn how Admob interstitial example and our partners collect and use data.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                admobProviderLayout.setVisibility(View.VISIBLE);
                admobConsentForm.setVisibility(View.GONE);
                ArrayList<String> providersName = new ArrayList<String>();
                for (int i = 0; i < adProviders.size(); i++) {
                    providersName.add(adProviders.get(i).getName());
                }
                StableArrayAdapter adapter = new StableArrayAdapter(ctx,
                        android.R.layout.simple_list_item_1, providersName);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.e(getClass().getName(), "internet sayfasina gidicek link = " + adProviders.get(i).getPrivacyPolicyUrlString());
                        //burada internet sayfasina gidicek
                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(adProviders.get(i).getPrivacyPolicyUrlString()));

                        ctx.startActivity(browse);
                    }
                });
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, 75, Spanned.SPAN_MARK_POINT);

        aciklamaTextView.append(ss);


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);
                callback.handleMessage(new Message());
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                consentInformation.setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                callback.handleMessage(new Message());
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admobProviderLayout.setVisibility(View.VISIBLE);
                admobConsentForm.setVisibility(View.GONE);
                ArrayList<String> providersName = new ArrayList<String>();
                for (int i = 0; i < adProviders.size(); i++) {
                    providersName.add(adProviders.get(i).getName());
                }

                listView.setAdapter(new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, providersName));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.e(getClass().getName(), "internet sayfasina gidicek link = " + adProviders.get(i).getPrivacyPolicyUrlString());
                        //burada internet sayfasina gidicek
                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(adProviders.get(i).getPrivacyPolicyUrlString()));

                        ctx.startActivity(browse);
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admobProviderLayout.setVisibility(View.GONE);
                admobConsentForm.setVisibility(View.VISIBLE);
            }
        });
    }

    public static class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =super.getView(position, convertView, parent);

            TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
            textView.setTextColor(Color.BLUE);

            return view;
        }
    }
}

