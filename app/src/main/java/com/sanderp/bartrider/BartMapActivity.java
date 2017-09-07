package com.sanderp.bartrider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Displays the BART Map.
 */
public class BartMapActivity extends AppCompatActivity {
    private static final String TAG = "BartMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BartTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // Set up Toolbar to replace ActionBar.
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("BART Map");
        mToolbar.setNavigationIcon(R.drawable.ic_menu_arrow_back);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.bart_map);
        imageView.setImage(ImageSource.resource(R.drawable.bart_map_med_res).dimensions(1150, 1047),
                ImageSource.resource(R.drawable.bart_map_low_res).dimensions(750, 683));
    }
}
