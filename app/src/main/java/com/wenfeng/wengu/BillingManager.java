package com.wenfeng.wengu;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.ProductDetails;

import java.util.List;

/**
 * Manages Google Play in-app billing for one-time unlock purchase.
 * Product ID: unlock_full_version (managed product)
 *
 * Flow: free 30-min trial -> purchase -> permanent unlock via SharedPreferences
 */
public class BillingManager implements PurchasesUpdatedListener {

    private static final String TAG = "BillingManager";
    private static final String SKU_UNLOCK = "unlock_full_version";
    private static final String PREFS_NAME = "wengu_prefs";
    private static final String KEY_UNLOCKED = "is_unlocked";
    private static final String KEY_TRIAL_START_TIME = "trial_start_time";
    private static final long TRIAL_DURATION_MS = 30 * 60 * 1000; // 30 minutes

    private final Context context;
    private final BillingClient billingClient;
    private final BillingCallback callback;
    private ProductDetails productDetails;

    public interface BillingCallback {
        void onPurchased();
        void onPurchaseFailed(String message);
        void onBillingReady();
        void onBillingUnavailable();
    }

    public BillingManager(Context context, BillingCallback callback) {
        this.context = context;
        this.callback = callback;
        this.billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
    }

    /**
     * Start connection to Google Play Billing
     */
    public void startConnection() {
        if (!billingClient.isReady()) {
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Billing connected");
                        queryProductDetails();
                        callback.onBillingReady();
                    } else {
                        Log.e(TAG, "Billing setup failed: " + billingResult.getDebugMessage());
                        callback.onBillingUnavailable();
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.d(TAG, "Billing disconnected");
                }
            });
        }
    }

    /**
     * Query the unlock product details
     */
    private void queryProductDetails() {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(java.util.Collections.singletonList(
                        QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(SKU_UNLOCK)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()
                ))
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && !productDetailsList.isEmpty()) {
                productDetails = productDetailsList.get(0);
                Log.d(TAG, "Product details loaded: " + productDetails.getName());
            }
        });
    }

    /**
     * Launch the purchase flow
     */
    public void launchPurchaseFlow(android.app.Activity activity) {
        if (productDetails == null) {
            callback.onPurchaseFailed("Product details not loaded yet. Try again.");
            return;
        }

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                        java.util.Collections.singletonList(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .build()
                        )
                )
                .build();

        billingClient.launchBillingFlow(activity, params);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getProducts().contains(SKU_UNLOCK)) {
                    if (purchase.getPurchaseState() == PurchaseState.PURCHASED) {
                        // Acknowledge the purchase
                        if (!purchase.isAcknowledged()) {
                            acknowledgePurchase(purchase);
                        }
                        // Mark as unlocked
                        setUnlocked(true);
                        callback.onPurchased();
                    }
                }
            }
        } else if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.USER_CANCELED) {
            callback.onPurchaseFailed(billingResult.getDebugMessage());
        }
    }

    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(params, billingResult -> {
            Log.d(TAG, "Purchase acknowledged: " + billingResult.getDebugMessage());
        });
    }

    // ========== Trial / Unlock state ==========

    /**
     * Check if user has purchased the full version
     */
    public static boolean isUnlocked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_UNLOCKED, false);
    }

    /**
     * Check if user is still within the free trial period
     */
    public static boolean isTrialValid(Context context) {
        if (isUnlocked(context)) return true;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long startTime = prefs.getLong(KEY_TRIAL_START_TIME, 0);
        if (startTime == 0) {
            // First launch — start trial
            prefs.edit().putLong(KEY_TRIAL_START_TIME, System.currentTimeMillis()).apply();
            return true;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed < TRIAL_DURATION_MS;
    }

    /**
     * Get remaining trial minutes
     */
    public static int getTrialMinutesRemaining(Context context) {
        if (isUnlocked(context)) return -1; // unlimited
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long startTime = prefs.getLong(KEY_TRIAL_START_TIME, 0);
        if (startTime == 0) return 30;
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = TRIAL_DURATION_MS - elapsed;
        return Math.max(0, (int) (remaining / (60 * 1000)));
    }

    private void setUnlocked(boolean unlocked) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_UNLOCKED, unlocked).apply();
    }

    /**
     * End connection
     */
    public void endConnection() {
        if (billingClient.isReady()) {
            billingClient.endConnection();
        }
    }
}
