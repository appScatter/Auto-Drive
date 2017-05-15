package com.appscatter.autodrive;

import com.appscatter.iab.core.ASIab;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import android.app.Application;
import android.content.Context;

public class AutoApplication extends Application {

    public static RefWatcher getRefWatcher(final Context context) {
        final Context applicationContext = context.getApplicationContext();
        final AutoApplication application = (AutoApplication) applicationContext;
        return application.refWatcher;
    }


    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        AutoData.init(this);
        AutoBilling.init(this);

        ASIab.setLogEnabled(true, true);
        ASIab.init("test@appscatter.com", this, new AutoBillingListener(this));

        refWatcher = LeakCanary.install(this);
    }

}