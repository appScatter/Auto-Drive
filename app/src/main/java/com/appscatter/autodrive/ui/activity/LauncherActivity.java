package com.appscatter.autodrive.ui.activity;

import com.appscatter.autodrive.Helper;
import com.appscatter.autodrive.AutoBilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Helper helper = AutoBilling.getHelper();
        startActivity(new Intent(this, helper.getActivityClass()));
        finish();
    }
}
