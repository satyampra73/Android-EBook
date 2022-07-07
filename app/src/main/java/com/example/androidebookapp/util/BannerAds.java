package com.example.androidebookapp.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applovin.mediation.ads.MaxAdView;
import com.example.androidebookapp.R;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ixidev.gdpr.GDPRChecker;
import com.startapp.sdk.ads.banner.Banner;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class BannerAds {
    public static void showBannerAds(Context context, LinearLayout mAdViewLayout) {
        if (Constant.appRP.isBanner_ad()) {
            switch (Constant.appRP.getAd_network()) {
                case "admob":
                    AdView mAdView = new AdView(context);
                    mAdView.setAdSize(AdSize.BANNER);
                    mAdView.setAdUnitId(Constant.appRP.getBanner_ad_id());
                    AdRequest.Builder builder = new AdRequest.Builder();
                    GDPRChecker.Request request = GDPRChecker.getRequest();
                    if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                        // load non Personalized ads
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    } // else do nothing , it will load PERSONALIZED ads
                    mAdView.loadAd(builder.build());
                    mAdViewLayout.addView(mAdView);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case "facebook":
                    com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Constant.appRP.getBanner_ad_id(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                    adView.loadAd();
                    mAdViewLayout.addView(adView);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case "startapp":
                    Banner startAppBanner = new Banner((Activity) context);
                    mAdViewLayout.addView(startAppBanner);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case "applovins":
                    MaxAdView maxAdView = new MaxAdView(Constant.appRP.getBanner_ad_id(), (Activity) context);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = context.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                    maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    maxAdView.loadAd();
                    mAdViewLayout.addView(maxAdView);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
                case "unityds":
                    BannerView bottomBanner = new BannerView((Activity) context, Constant.appRP.getBanner_ad_id(), new UnityBannerSize(320, 50));
                    bottomBanner.load();
                    mAdViewLayout.addView(bottomBanner);
                    mAdViewLayout.setGravity(Gravity.CENTER);
                    break;
            }
        } else {
            mAdViewLayout.setVisibility(View.GONE);
        }
    }
}
