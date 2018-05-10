package info.tumur.resume.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.adapter.AdapterPortfolioImage;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackPortfolioDetails;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.PortfolioDetails;
import info.tumur.resume.app.model.PortfolioImage;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPortfolioDetails extends AppCompatActivity {

    private static final String EXTRA_OBJECT_ID = "key.EXTRA_OBJECT_ID";
    private static final String EXTRA_TITLE = "key.EXTRA_TITLE";

    // activity transition
    public static void navigate(Activity activity, int id, String title) {
        Intent i = navigateBase(activity, id, title);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, int id, String title) {
        Intent i = new Intent(context, ActivityPortfolioDetails.class);
        i.putExtra(EXTRA_OBJECT_ID, id);
        i.putExtra(EXTRA_TITLE, title);
        return i;
    }

    public static ActivityPortfolioDetails getInstance() {
        return activityPortfolioDetails;
    }

    private int portfolio_id;
    private String portfolio_title;
    private PortfolioDetails portfolio;

    private Call<CallbackPortfolioDetails> callbackCall = null;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private SwipeRefreshLayout swipe_refresh;
    private MaterialRippleLayout lyt_btn_url;
    private FirebaseAnalytics mFirebaseAnalytics;
    static ActivityPortfolioDetails activityPortfolioDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_portfolio_details);
        portfolio_id = (Integer) getIntent().getSerializableExtra(EXTRA_OBJECT_ID);
        portfolio_title = getIntent().getStringExtra(EXTRA_TITLE);
        activityPortfolioDetails = this;
        initToolbar();
        initComponent();
        requestAction();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(portfolio_title);
    }

    private void initComponent() {
        parent_view = findViewById(android.R.id.content);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        // on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
            }
        });
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPortfolioDetailsApi();
            }
        }, 500);
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestPortfolioDetailsApi() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getPortfolioDetails(portfolio_id);
        callbackCall.enqueue(new Callback<CallbackPortfolioDetails>() {
            @Override
            public void onResponse(Call<CallbackPortfolioDetails> call, Response<CallbackPortfolioDetails> response) {
                CallbackPortfolioDetails resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    portfolio = resp.portfolio;
                    displayPostData();
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackPortfolioDetails> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    private void displayPostData() {
        ((TextView) findViewById(R.id.title)).setText(portfolio.title);
        ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDate(portfolio.date));
        ((TextView) findViewById(R.id.brief)).setText(portfolio.brief);
        ((TextView) findViewById(R.id.title_features)).setText(this.getResources().getString(R.string.features));
        ((TextView) findViewById(R.id.features)).setText(portfolio.features);
        ((TextView) findViewById(R.id.title_description)).setText(this.getResources().getString(R.string.description));
        ((TextView) findViewById(R.id.description)).setText(portfolio.description);
        ((Button) findViewById(R.id.url)).setText(portfolio.btn_title);

        ((Button) findViewById(R.id.url)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.directUrl(getInstance(), portfolio.btn_url);
            }
        });

        // display Image slider
        displayImageSlider();

        Toast.makeText(this, R.string.msg_data_loaded, Toast.LENGTH_SHORT).show();

    }

    private void displayImageSlider() {
        final LinearLayout layout_dots = (LinearLayout) findViewById(R.id.layout_dots);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final AdapterPortfolioImage adapterSlider = new AdapterPortfolioImage(this, new ArrayList<PortfolioImage>());

        final List<PortfolioImage> productImages = new ArrayList<>();
        PortfolioImage p = new PortfolioImage();
        p.portfolio_id = portfolio.id;
        p.name = portfolio.image_bg;
        productImages.add(p);
        if (portfolio.portfolio_images != null) productImages.addAll(portfolio.portfolio_images);
        adapterSlider.setItems(productImages);
        viewPager.setAdapter(adapterSlider);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, adapterSlider.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, adapterSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        final ArrayList<String> images_list = new ArrayList<>();
        for (PortfolioImage img : productImages) {
            images_list.add(Constant.getURLimg(img.name));
        }

        adapterSlider.setOnItemClickListener(new AdapterPortfolioImage.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PortfolioImage obj, int pos) {
                Intent i = new Intent(ActivityPortfolioDetails.this, ActivityFullScreenImage.class);
                i.putExtra(ActivityFullScreenImage.EXTRA_POS, pos);
                i.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, images_list);
                startActivity(i);
            }
        });
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(this, R.color.darkOverlaySoft));
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[current].setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight));
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        View lyt_main_content = (View) findViewById(R.id.lyt_main_content);

        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_main_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_main_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
