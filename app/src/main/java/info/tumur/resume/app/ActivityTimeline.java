package info.tumur.resume.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.adapter.AdapterTimeline;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackTimeline;
import info.tumur.resume.app.model.Timeline;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTimeline extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_TITLE = "key.EXTRA_TITLE";


    private final static int LOADING_DURATION = 1000; // Loading duration
    static boolean active = false; // indicator for activity status
    private View parent_view;
    static ActivityTimeline activityTimeLine;
    // Variables for UI Views
    private ActionBar actionBar;
    private Toolbar toolbar;
    private View lyt_progress_bar;
    private View lyt_no_internet;
    private SwipeRefreshLayout swipe_refresh;
    private View lyt_no_connect;
    private View lyt_no_item;
    private View lyt_main_content;
    private Dialog dialog = null; // DialogUtils for No Internet, Server connection and no item
    //Variables for Callbacks and API services
    private Call<CallbackTimeline> callbackCallTimeLine = null; // Callback for Timeline data
    // UI for Timeline
    private RecyclerView recyclerViewTimeLine; // RecyclerView for Timeline
    private AdapterTimeline adapterTimeLine; // Adapter for Timeline

    // Activity Intent Bundle Variables
    private int timeline_id = 0; // Indicator for Timeline type
    private String title; // Title for Timeline

    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics;  // Used for Firebase Analytics init

    // getting Instance for UI
    public static ActivityTimeline getInstance() {
        return activityTimeLine;
    }

    // Activity Transition
    public static void navigate(Activity activity, int timeline_id, String title) {
        Intent i = new Intent(activity, ActivityTimeline.class);
        i.putExtra(EXTRA_OBJECT, timeline_id);
        i.putExtra(EXTRA_TITLE, title);
        activity.startActivity(i);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_timeline);
        timeline_id = (Integer) getIntent().getSerializableExtra(EXTRA_OBJECT);
        title = (String) getIntent().getSerializableExtra(EXTRA_TITLE);
        activityTimeLine = this; // getting Instance for UI
        initToolbar(); // InitToolbar
        initComponent(); // Init Components
        // Show Progressbar
        showProgressBar(true, false, false, false, false);
        requestTimelineApi(); // API Request
    }

    // Toolbar
    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);
        Tools.systemBarLolipop(this);
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

        //Set Timeline UI
        recyclerViewTimeLine = parent_view.findViewById(R.id.recyclerViewTimeLine);
        recyclerViewTimeLine.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTimeLine.setHasFixedSize(true);

        //Set timeline data and list adapter
        adapterTimeLine = new AdapterTimeline(this, recyclerViewTimeLine, new ArrayList<Timeline>());
        recyclerViewTimeLine.setAdapter(adapterTimeLine);

        // on item list clicked
        adapterTimeLine.setOnItemClickListener(new AdapterTimeline.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Timeline obj) {
            }
        });

        // Refresh content on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCallTimeLine != null && callbackCallTimeLine.isExecuted())
                    callbackCallTimeLine.cancel();
                // Show progressbar while retry
                swipeProgress(true);
                retry(true); // retry to connect Internet and Server
            }
        });
    }


    // API Request for TimeLine data
    private void requestTimelineApi() {
        API api = RestAdapter.createAPI();
        callbackCallTimeLine = api.getTimeline(timeline_id);
        callbackCallTimeLine.enqueue(new Callback<CallbackTimeline>() {
            @Override
            public void onResponse(Call<CallbackTimeline> call, Response<CallbackTimeline> response) {
                CallbackTimeline resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    // Display gathered data
                    displayResult(resp.timeline);
                } else {
                    onFailRequest(); // Failed Request handler
                }
            }

            @Override
            public void onFailure(Call<CallbackTimeline> call, Throwable t) {
                // Failed Request handler
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    // Show result of API
    private void displayResult(final List<Timeline> timeline) {
        // Set timeline data from API
        adapterTimeLine.insertData(timeline);
        // Check item size
        if (timeline.size() == 0) {
            // show no item
            showProgressBar(false, false, false, true, false);
        } else {
            // show all items
            showProgressBar(false, false, false, false, true);
        }
    }

    // Handler IF No Internet or Server Connection
    private void onFailRequest() {
        // Checking internet connection
        if (!NetworkCheck.isConnect(this)) {
            // Hide Progressbar and show No Internet Connection Layout
            showProgressBar(false, true, false, false, false);
            // Show DialogUtils for No Internet Connection
            showDialogNoInternetConnection();
        } else {
            // Hide Progressbar and show No Server Connection Layout
            showProgressBar(false, false, true, false, false);
            // Show DialogUtils for No Server Connection
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

    // Show DialogUtils for no server connection
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


    // Show DialogUtils for no internet connection
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
                requestTimelineApi(); // Request for API
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
        if (callbackCallTimeLine != null && callbackCallTimeLine.isExecuted()) {
            callbackCallTimeLine.cancel();
        }
    }

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
}
