package com.appscatter.autodrive.ui.fragment;

import com.appscatter.iab.core.ASIab;
import com.appscatter.iab.core.api.FragmentIabHelper;
import com.appscatter.iab.core.listener.OnInventoryListener;
import com.appscatter.iab.core.listener.OnPurchaseListener;
import com.appscatter.iab.core.listener.OnSetupListener;
import com.appscatter.iab.core.listener.OnSkuDetailsListener;
import com.appscatter.iab.core.model.event.SetupResponse;
import com.appscatter.iab.core.model.event.SetupStartedEvent;
import com.appscatter.iab.core.model.event.billing.InventoryResponse;
import com.appscatter.iab.core.model.event.billing.PurchaseResponse;
import com.appscatter.iab.core.model.event.billing.SkuDetailsResponse;
import com.appscatter.autodrive.R;
import com.appscatter.autodrive.AutoApplication;
import com.appscatter.autodrive.ui.view.AutoView;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AutoFragment extends Fragment
        implements OnSetupListener, OnPurchaseListener, OnInventoryListener, OnSkuDetailsListener {

    public static AutoFragment newInstance() {
        return new AutoFragment();
    }


    private FragmentIabHelper iabHelper;
    private AutoView mAutoView;

    public AutoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.include_auto, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAutoView = (AutoView) view.findViewById(R.id.trivial);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iabHelper = ASIab.getFragmentHelper(this);
        mAutoView.setIabHelper(iabHelper);

        iabHelper.addPurchaseListener(this);
        iabHelper.addInventoryListener(this);
        iabHelper.addSkuDetailsListener(this);
        iabHelper.addSetupListener(this, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAutoView.update();
    }

    @Override
    public void onDestroyView() {
        //noinspection AssignmentToNull
        mAutoView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AutoApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onSetupStarted(@NonNull final SetupStartedEvent setupStartedEvent) {
        mAutoView.update();
    }

    @Override
    public void onSetupResponse(@NonNull final SetupResponse setupResponse) { }

    @Override
    public void onPurchase(@NonNull final PurchaseResponse purchaseResponse) {
        mAutoView.updatePremium();
        mAutoView.updateSubscription();
    }

    @Override
    public void onInventory(@NonNull final InventoryResponse inventoryResponse) {
        mAutoView.updatePremium();
        mAutoView.updateSubscription();
    }

    @Override
    public void onSkuDetails(@NonNull final SkuDetailsResponse skuDetailsResponse) {
        mAutoView.updateSkuDetails();
    }
}
