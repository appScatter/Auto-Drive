package com.appscatter.autodrive.ui.activity;

import com.appscatter.autodrive.R;
import com.appscatter.autodrive.ui.fragment.AutoFragment;

import android.os.Bundle;

public class FragmentHelperActivity extends AutoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_content);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, AutoFragment.newInstance())
                    .commit();
        }
    }
}
