package info.tumur.resume.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import info.tumur.resume.app.adapter.AdapterFullScreenImage;
import info.tumur.resume.app.utils.Tools;

public class ActivityFullScreenImage extends AppCompatActivity {

    public static final String EXTRA_POS = "key.EXTRA_POS";
    public static final String EXTRA_IMGS = "key.EXTRA_IMGS";

    private AdapterFullScreenImage adapter;
    private ViewPager viewPager;
    private TextView text_page;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_full_screen_image);
        viewPager = findViewById(R.id.pager);
        text_page = findViewById(R.id.text_page);

        ArrayList<String> items = new ArrayList<>();
        Intent i = getIntent();
        final int position = i.getIntExtra(EXTRA_POS, 0);
        items = i.getStringArrayListExtra(EXTRA_IMGS);
        adapter = new AdapterFullScreenImage(ActivityFullScreenImage.this, items);
        final int total = adapter.getCount();
        viewPager.setAdapter(adapter);

        text_page.setText(String.format(getString(R.string.txt_image_of), (position + 1), total));

        // displaying selected image first
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                text_page.setText(String.format(getString(R.string.txt_image_of), (pos + 1), total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }


}

