package com.appscatter.autodrive.ui.view;

import com.appscatter.iab.core.api.IabHelper;
import com.appscatter.autodrive.R;
import com.appscatter.autodrive.AutoBilling;
import com.appscatter.autodrive.AutoData;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.CENTER_VERTICAL;
import static com.appscatter.autodrive.AutoBilling.SKU_GAS;
import static com.appscatter.autodrive.AutoBilling.SKU_PREMIUM;
import static com.appscatter.autodrive.AutoBilling.SKU_SUBSCRIPTION;

public class AutoView extends LinearLayout
        implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final Set<String> SKUS = new HashSet<>(
            Arrays.asList(SKU_GAS, SKU_PREMIUM, SKU_SUBSCRIPTION));

    private View btnDrive;
    private View btnBuyGas;
    private View btnBuyPremium;
    private View btnBuySubscription;

    private ImageView ivCar;
    private ImageView ivGas;

    private SkuDetailsView sdvGas;
    private SkuDetailsView sdvPremium;
    private SkuDetailsView sdvSubscription;

    private IabHelper iabHelper;

    public AutoView(Context context) {
        super(context);
        init();
    }

    public AutoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        final Context context = getContext();
        final int orientation = getResources().getConfiguration().orientation;
        setOrientation(orientation == Configuration.ORIENTATION_PORTRAIT ? VERTICAL : HORIZONTAL);
        setGravity(getOrientation() == VERTICAL ? CENTER_HORIZONTAL : CENTER_VERTICAL);
        inflate(context, R.layout.view_auto, this);
        if (isInEditMode()) {
            return;
        }

        btnBuyGas = findViewById(R.id.btn_buy_gas);
        btnBuyPremium = findViewById(R.id.btn_buy_premium);
        btnBuySubscription = findViewById(R.id.btn_buy_subscription);
        btnDrive = findViewById(R.id.btn_drive);

        ivGas = (ImageView) findViewById(R.id.img_gas);
        ivCar = (ImageView) findViewById(R.id.img_car);

        sdvGas = (SkuDetailsView) findViewById(R.id.sdv_gas);
        sdvPremium = (SkuDetailsView) findViewById(R.id.sdv_premium);
        sdvSubscription = (SkuDetailsView) findViewById(R.id.sdv_subscription);

        btnDrive.setOnClickListener(this);
        btnBuyGas.setOnClickListener(this);
        btnBuyPremium.setOnClickListener(this);
        btnBuySubscription.setOnClickListener(this);

        update();

        AutoData.registerOnSharedPreferenceChangeListener(this);
    }

    private void drive() {
        final boolean success;
        if (AutoBilling.hasValidSubscription()) {
            success = true;
        } else if (AutoData.canSpendGas()) {
            AutoData.spendGas();
            success = true;
        } else {
            success = false;
        }

        if (success) {
            Toast.makeText(getContext(), R.string.msg_item_success, Toast.LENGTH_SHORT).show();
        } else {
            btnBuyGas.callOnClick();
        }
    }

    private void updateGas() {
        final boolean hasValidSubscription = AutoBilling.hasValidSubscription();
        if (hasValidSubscription) {
            ivGas.setImageResource(R.drawable.img_item_inf);
        } else {
            ivGas.setImageResource(R.drawable.img_item_level);
            ivGas.getDrawable().setLevel(AutoData.getGas());
        }
        btnBuyGas.setEnabled(!AutoBilling.hasValidSubscription() && AutoData.canAddGas());
    }

    public void setIabHelper(final IabHelper iabHelper) {
        this.iabHelper = iabHelper;
    }

    public void updatePremium() {
        ivCar.setImageResource(AutoBilling.hasPremium()
                ? R.drawable.img_item_premium
                : R.drawable.img_item);
    }

    public void updateSubscription() {
        updateGas();
    }

    public void updateSkuDetails() {
        sdvGas.setSkuDetails(AutoBilling.getDetails(SKU_GAS));
        sdvPremium.setSkuDetails(AutoBilling.getDetails(SKU_PREMIUM));
        sdvSubscription.setSkuDetails(AutoBilling.getDetails(SKU_SUBSCRIPTION));
    }

    public void update() {
        updatePremium();
        updateSubscription();
        updateSkuDetails();
    }

    @Override
    public void onClick(final View v) {
        if (v == btnDrive) {
            drive();
        } else if (v == btnBuyGas && !AutoBilling.hasValidSubscription() && AutoData.canAddGas()) {
            iabHelper.purchase(SKU_GAS);
        } else if (v == btnBuyPremium) {
            iabHelper.purchase(SKU_PREMIUM);
        } else if (v == btnBuySubscription) {
            iabHelper.purchase(SKU_SUBSCRIPTION);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        AutoData.unregisterOnSharedPreferenceChangeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        updateGas();
    }
}
