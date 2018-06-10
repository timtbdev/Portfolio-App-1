package info.tumur.resume.app;

import android.app.Activity;
import android.app.Dialog;
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

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.adapter.AdapterPortfolioImage;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackPortfolioItem;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.PortfolioImage;
import info.tumur.resume.app.model.PortfolioItem;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPortfolioItem extends AppCompatActivity {

    private static final String EXTRA_OBJECT_ID = "key.EXTRA_OBJECT_ID";
    private static final String EXTRA_TITLE = "key.EXTRA_TITLE";
    private final static int LOADING_DURATION = 1000; // Loading duration
    static boolean active = false; // indicator for activity status
    static ActivityPortfolioItem activityPortfolioItem;
    // Variables for UI Views
    private ActionBar actionBar;
    private View parent_view;
    private Toolbar toolbar;
    private View lyt_progress_bar;
    private View lyt_no_internet;
    private View lyt_no_connect;
    private View lyt_no_item;
    private View lyt_main_content;
    private SwipeRefreshLayout swipe_refresh;
    private long exitTime = 0; // Application exit time
    private Dialog dialog = null; // Dialog for No Internet, Server connection and no item
    // Activity Intent Bundle Data
    private int portfolio_id;
    private String portfolio_title;
    //Variables for Callbacks and API services
    private Call<CallbackPortfolioItem> callbackCallPortfolioItem = null;
    // Ripple Effect Layout
    private MaterialRippleLayout lyt_btn_url;
    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics; // Used for Firebase Analytics init

    // activity transition
    public static void navigate(Activity activity, int id, String title) {
        Intent i = navigateBase(activity, id, title);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, int id, String title) {
        Intent i = new Intent(context, ActivityPortfolioItem.class);
        i.putExtra(EXTRA_OBJECT_ID, id);
        i.putExtra(EXTRA_TITLE, title);
        return i;
    }

    public static ActivityPortfolioItem getInstance() {
        return activityPortfolioItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_portfolio_item);
        portfolio_id = (Integer) getIntent().getSerializableExtra(EXTRA_OBJECT_ID);
        portfolio_title = getIntent().getStringExtra(EXTRA_TITLE);
        activityPortfolioItem = this; // getting Instance for UI
        initToolbar(); // InitToolbar
        initComponent(); // Init Components
        // Show Progressbar
        showProgressBar(true, false, false, false, false);
        requestPortfolioItemApi();// API Request
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(portfolio_title); // Toolbar title
        Tools.systemBarLolipop(activityPortfolioItem);
    }

    // UI Components
    private void initComponent() {
        parent_view = findViewById(R.id.parent_view);
        lyt_progress_bar = parent_view.findViewById(R.id.lyt_progress_bar);
        lyt_no_internet = parent_view.findViewById(R.id.lyt_no_internet);
        lyt_no_connect = parent_view.findViewById(R.id.lyt_no_connect);
        lyt_no_item = parent_view.findViewById(R.id.lyt_no_item);
        lyt_main_content = parent_view.findViewById(R.id.lyt_main_content);
        swipe_refresh = parent_view.findViewById(R.id.swipe_refresh_layout);
        // Refresh content on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCallPortfolioItem != null && callbackCallPortfolioItem.isExecuted())
                    callbackCallPortfolioItem.cancel();
                // Show progressbar while retry
                swipeProgress(true);
                retry(true); // retry to connect Internet and Server
            }
        });
    }

    // API Request for Portfolio Item data
    private void requestPortfolioItemApi() {
        API api = RestAdapter.createAPI();
        callbackCallPortfolioItem = api.getPortfolioItem(portfolio_id);
        callbackCallPortfolioItem.enqueue(new Callback<CallbackPortfolioItem>() {
            @Override
            public void onResponse(Call<CallbackPortfolioItem> call, Response<CallbackPortfolioItem> response) {
                CallbackPortfolioItem resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    // Display gathered data
                    displayResult(resp.portfolioItem, resp.portfolio_images);
                } else {
                    onFailRequest(); // Failed Request handler
                }
            }

            @Override
            public void onFailure(Call<CallbackPortfolioItem> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                // Failed Request handler
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    // Show result of API
    private void displayResult(final PortfolioItem portfolio, final List<PortfolioImage> portfolio_images) {
        ((TextView) lyt_main_content.findViewById(R.id.title)).setText(portfolio.portfolio_title);
        ((TextView) lyt_main_content.findViewById(R.id.date)).setText(Tools.getFormattedDate(portfolio.portfolio_date));
        ((TextView) lyt_main_content.findViewById(R.id.brief)).setText(portfolio.portfolio_brief);
        ((TextView) lyt_main_content.findViewById(R.id.title_features)).setText(this.getResources().getString(R.string.txt_features));
        ((TextView) lyt_main_content.findViewById(R.id.features)).setText(portfolio.portfolio_features);
        ((TextView) lyt_main_content.findViewById(R.id.title_description)).setText(this.getResources().getString(R.string.txt_description));
        ((TextView) lyt_main_content.findViewById(R.id.description)).setText(portfolio.portfolio_description);
        ((Button) lyt_main_content.findViewById(R.id.url)).setText(portfolio.portfolio_btn_title);

        findViewById(R.id.url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.directUrl(getInstance(), portfolio.portfolio_btn_url);
            }
        });

        // Display Portfolio Image Slider
        displayImageSlider(portfolio.id, portfolio.portfolio_image, portfolio_images);

        // Check item size
        if (portfolio == null || portfolio.toString().trim().isEmpty()) {
            // show no item
            showProgressBar(false, false, false, true, false);
        } else {
            // show all items
            showProgressBar(false, false, false, false, true);
        }
    }

    // Show result of Portfolio Images
    private void displayImageSlider(int id, String image, List<PortfolioImage> images) {
        final LinearLayout layout_dots = findViewById(R.id.layout_dots);
        ViewPager viewPager = findViewById(R.id.pager);
        final AdapterPortfolioImage adapterSlider = new AdapterPortfolioImage(this, new ArrayList<PortfolioImage>());

        final List<PortfolioImage> portfolioImages = new ArrayList<>();
        PortfolioImage p = new PortfolioImage();
        p.id = id;
        p.portfolio_image = image;
        portfolioImages.add(p);
        if (images != null) portfolioImages.addAll(images);
        adapterSlider.setItems(portfolioImages);
        viewPager.setAdapter(adapterSlider);

        // Display select image as first
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
        for (PortfolioImage img : portfolioImages) {
            images_list.add(Constant.getURLimg(img.portfolio_image));
        }

        adapterSlider.setOnItemClickListener(new AdapterPortfolioImage.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PortfolioImage obj, int pos) {
                Intent i = new Intent(ActivityPortfolioItem.this, ActivityFullScreenImage.class);
                i.putExtra(ActivityFullScreenImage.EXTRA_POS, pos);
                i.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, images_list);
                startActivity(i);
            }
        });
    }

    // Display Portfolio Image indicator for Image Slider
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

    // Handler IF No Internet or Server Connection
    private void onFailRequest() {
        // Checking internet connection
        if (!NetworkCheck.isConnect(this)) {
            // Hide Progressbar and show No Internet Connection Layout
            showProgressBar(false, true, false, false, false);
            // Show Dialog for No Internet Connection
            showDialogNoInternetConnection();
        } else {
            // Hide Progressbar and show No Server Connection Layout
            showProgressBar(false, false, true, false, false);
            // Show Dialog for No Server Connection
            showDialogNoServerConnection();
        }
    }

    // Show and Hide Progressbar
    private void showProgressBar(boolean show, boolean no_internet, boolean no_connect, boolean no_item, boolean main_content) {
        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
        if (show) {
            lyt_progress_bar.setVisibility(View.VISIBLE);
            lyt_main_content.setVisibility(View.GONE);
            lyt_no_internet.setVisibility(View.GONE);
            lyt_no_connect.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.GONE);
            return;
        } else {
            lyt_progress_bar.setVisibility(View.GONE);
            if (no_internet) lyt_no_internet.setVisibility(View.VISIBLE);
            else if (lyt_no_internet.isShown()) lyt_no_internet.setVisibility(View.GONE);
            if (no_connect) lyt_no_connect.setVisibility(View.VISIBLE);
            else if (lyt_no_connect.isShown()) lyt_no_connect.setVisibility(View.GONE);
            if (no_item) lyt_no_item.setVisibility(View.VISIBLE);
            else if (lyt_no_item.isShown()) lyt_no_item.setVisibility(View.GONE);
            if (main_content) lyt_main_content.setVisibility(View.VISIBLE);
            else if (lyt_main_content.isShown()) lyt_main_content.setVisibility(View.GONE);
        }
    }

    // Show Dialog for no server connection
    public void showDialogNoServerConnection() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_unable_connect, R.string.msg_unable_connect, R.string.txt_try_again, R.string.txt_close, R.drawable.ic_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retry(false); // retry to connect Internet and Server
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    // Show Dialog for no internet connection
    public void showDialogNoInternetConnection() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_no_internet, R.string.msg_no_internet, R.string.txt_try_again, R.string.txt_close, R.drawable.ic_no_internet, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retry(false); // retry to connect Internet and Server
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // Retry with delay
    private void retry(boolean refresh) {
        // Show progressbar while retry
        if (!refresh) showProgressBar(true, false, false, false, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPortfolioItemApi(); // Request for API
            }
        }, LOADING_DURATION);
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


    // Handler for Appbar Menu Item Selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed(); // Back to previous Activity
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

    @Override
    public void onStart() {
        super.onStart();
        active = true; // status of activity
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false; // status of activity
        if (callbackCallPortfolioItem != null && callbackCallPortfolioItem.isExecuted()) {
            callbackCallPortfolioItem.cancel();
        }
    }
}
