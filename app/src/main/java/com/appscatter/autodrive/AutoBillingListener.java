package com.appscatter.autodrive;

import com.appscatter.iab.core.listener.DefaultBillingListener;
import com.appscatter.iab.core.model.billing.Purchase;
import com.appscatter.iab.core.model.event.SetupResponse;
import com.appscatter.iab.core.model.event.SetupStartedEvent;
import com.appscatter.iab.core.model.event.billing.BillingEventType;
import com.appscatter.iab.core.model.event.billing.BillingResponse;
import com.appscatter.iab.core.model.event.billing.ConsumeResponse;
import com.appscatter.iab.core.model.event.billing.InventoryResponse;
import com.appscatter.iab.core.model.event.billing.PurchaseResponse;
import com.appscatter.iab.core.model.event.billing.SkuDetailsResponse;
import com.appscatter.iab.core.model.event.billing.Status;
import com.appscatter.autodrive.ui.view.AutoView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class AutoBillingListener extends DefaultBillingListener {

    private final Context context;

    public AutoBillingListener(final Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    public void onSetupStarted(@NonNull final SetupStartedEvent setupStartedEvent) {
        super.onSetupStarted(setupStartedEvent);
        AutoBilling.updateSetup();
    }

    @Override
    public void onSetupResponse(@NonNull final SetupResponse setupResponse) {
        super.onSetupResponse(setupResponse);
        if (setupResponse.isSuccessful()) {
            // update inventory and sku data every time provider is picked
            getHelper().inventory(true);
            getHelper().skuDetails(AutoView.SKUS);
        }
    }

    @Override
    public void onResponse(@NonNull final BillingResponse billingResponse) {
        super.onResponse(billingResponse);
        if (!billingResponse.isSuccessful()) {
            @BillingEventType final int type = billingResponse.getType();
            @Status final int status = billingResponse.getStatus();
            final String msg = context.getString(R.string.msg_request_failed, String.valueOf(type), String.valueOf(status));
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSkuDetails(@NonNull final SkuDetailsResponse skuDetailsResponse) {
        super.onSkuDetails(skuDetailsResponse);
        AutoBilling.updateSkuDetails(skuDetailsResponse);
    }

    @Override
    public void onPurchase(@NonNull final PurchaseResponse purchaseResponse) {
        super.onPurchase(purchaseResponse);
        AutoBilling.updatePurchase(purchaseResponse);
    }

    @Override
    public void onInventory(@NonNull final InventoryResponse inventoryResponse) {
        super.onInventory(inventoryResponse);
        AutoBilling.updateInventory(inventoryResponse);
    }

    @Override
    protected boolean canConsume(@Nullable final Purchase purchase) {
        return AutoData.canAddGas() && super.canConsume(purchase);
    }

    @Override
    public void onConsume(@NonNull final ConsumeResponse consumeResponse) {
        super.onConsume(consumeResponse);
        // Available gas should be persistent regardless of current billing state
        if (consumeResponse.isSuccessful()) {
            AutoData.addGas();
        }
    }
}
