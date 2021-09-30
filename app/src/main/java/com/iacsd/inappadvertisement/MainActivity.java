package com.iacsd.inappadvertisement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="InAppAdvertising" ;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private final String APP_KEY = "85460dcd";

    private Button btnBanner;
    private Button btnInterstitial;
    private Button btnRewardedVideo;
    private Button btnOfferwall;
    private Button btnPushNotification;
    private Button btnInterstitialVideo;

    //For local Notification
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
//Before loading ads, have your app initialize the Mobile Ads SDK by calling MobileAds.initialize()
// which initializes the SDK and calls back a completion listener once initialization is complete
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        List<String> testDeviceIds = Arrays.asList("C3DA0992820B05F99B0CC0554B620EDC");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
*/
        //for The ironSource SDK fires several events to inform you of your ad unit activity. To receive these events, register to the listener of the ad units you set up on the ironSource platform.
        //Make sure you set the listeners before initializing the SDK, to avoid any loss of information.
        OfferwallListener mOfferwallListener = null;
        IronSource.setOfferwallListener(mOfferwallListener);
        IronSource.init(this, APP_KEY);
        IntegrationHelper.validateIntegration(this);
        MobileAds.initialize(this);//this is to be done only once

        btnBanner = findViewById(R.id.Banner);
        btnInterstitial = findViewById(R.id.Interstitial);
        btnInterstitialVideo = findViewById(R.id.InterstitialVideo);
        btnOfferwall = findViewById(R.id.Offerwall);
        btnPushNotification = findViewById(R.id.PushNotification);
        btnRewardedVideo = findViewById(R.id.RewardedVideo);





        btnBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBanner();
            }

        });



        btnInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInterstitial();
            }


        });



        btnInterstitialVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInterstitialVideo();
            }


        });



        btnOfferwall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowOfferWall();
            }


        });


        btnRewardedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRewardedVedio();
            }


        });

        //Local Push Notification
        btnPushNotification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Uri sound = Uri. parse (ContentResolver. SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/quite_impressed.mp3" ) ;
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity. this,
                        default_notification_channel_id )
                        .setSmallIcon(R.drawable. ic_launcher_foreground )
                        .setContentTitle( "Eureka! Got the Local Push Notification" )
                        .setSound(sound)
                        .setContentText( "Congratulations...!!!You have been rewarded with gems...." );
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
                if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                            .setUsage(AudioAttributes. USAGE_ALARM )
                            .build() ;
                    int importance = NotificationManager. IMPORTANCE_HIGH ;
                    NotificationChannel notificationChannel = new
                            NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
                    notificationChannel.enableLights( true ) ;
                    notificationChannel.setLightColor(Color. RED ) ;
                    notificationChannel.enableVibration( true ) ;
                    notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
                    notificationChannel.setSound(sound , audioAttributes) ;
                    mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
                    assert mNotificationManager != null;
                    mNotificationManager.createNotificationChannel(notificationChannel) ;
                }
                assert mNotificationManager != null;
                mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
            }
        }) ;

        //For preload interstitial Ad
        IronSource.loadInterstitial();
    }

    private void ShowInterstitialVideo() {
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             * Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
            }
            /**
             * invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
            }
            /**
             * Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
            }
            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
                IronSource.loadInterstitial();
            }
            /**
             * Invoked when Interstitial ad failed to show.
             * @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
            }
            /*
             * Invoked when the end user clicked on the interstitial ad, for supported networks only.
             */
            @Override
            public void onInterstitialAdClicked() {
            }
            /** Invoked right before the Interstitial screen is about to open.
             *  NOTE - This event is available only for some of the networks.
             *  You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
            }
        });

        IronSource.showInterstitial();
    }

    //for offerwalls we need to implement activity life cycle methods.
//ironsource life cycle methods..
protected void onResume() {
    super.onResume();
    IronSource.onResume(this);
}
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    //Ironsource Offerwall integrtions


    public void ShowBanner() {

        //AdView adView = new AdView(this);

        //adView.setAdSize(AdSize.BANNER);

        //adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        //load the add in your application...
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setBottom(Gravity.BOTTOM);
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(MainActivity.this,"Hello Banner Ad Loaded",Toast.LENGTH_LONG).show();
                System.out.println("Hello Ad Banner Loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

//Load the interstital add
    public void ShowInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.show(MainActivity.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
//load the rewarded vedio ad...
    public void ShowRewardedVedio() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
                        Toast.makeText(MainActivity.this,"Hello RewardedAd  Loaded",Toast.LENGTH_LONG).show();
                        // mRewardedAd.show(MainActivity.this);
                        mRewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                Log.d(TAG, "The user earned the reward.");
                                int rewardAmount = rewardItem.getAmount();
                                String rewardType = rewardItem.getType();
                            }
                        });
                    }
                });

    }

    public void ShowOfferWall() {
        IronSource.setOfferwallListener(new OfferwallListener() {
            /**
             * Invoked when there is a change in the Offerwall availability status.
             * @param - available - value will change to YES when Offerwall are available.
             * You can then show the offerwall by calling showOfferwall(). Value will *change to NO when Offerwall isn't available.
             */
            @Override
            public void onOfferwallAvailable(boolean isAvailable) {
            }
            /**
             * Invoked when the Offerwall successfully loads for the user, after calling the 'showOfferwall' method
             */
            @Override
            public void onOfferwallOpened() {
            }
            /**
             * Invoked when the method 'showOfferWall' is called and the OfferWall fails to load.
             * @param error - A IronSourceError Object which represents the reason of 'showOfferwall' failure.
             */
            @Override
            public void onOfferwallShowFailed(IronSourceError error) {
            }
            /**
             * Invoked each time the user completes an Offer.
             * Award the user with the credit amount corresponding to the value of the *‘credits’ parameter.
             * @param credits - The number of credits the user has earned.
             * @param totalCredits - The total number of credits ever earned by the user.
             * @param totalCreditsFlag - In some cases, we won’t be able to provide the exact
             * amount of credits since the last event (specifically if the user clears
             * the app’s data). In this case the ‘credits’ will be equal to the ‘totalCredits’, and this flag will be ‘true’.
             * @return boolean - true if you received the callback and rewarded the user, otherwise false.
             */
            @Override
            public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
                return true;
            }
            /**
             * Invoked when the method 'getOfferWallCredits' fails to retrieve
             * the user's credit balance info.
             * @param error - A IronSourceError object which represents the reason of 'getOfferwallCredits' failure.
             * If using client-side callbacks to reward users, it is mandatory to return true on this event
             */
            @Override
            public void onGetOfferwallCreditsFailed(IronSourceError error) {
            }
            /**
             * Invoked when the user is about to return to the application after closing
             * the Offerwall.
             */
            @Override
            public void onOfferwallClosed() {
            }
        });

        IronSource.showOfferwall();
    }
}
