package info.tumur.resume.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.adapter.AdapterTimeline;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackTimeline;
import info.tumur.resume.app.model.Timeline;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTimeline extends AppCompatActivity {
    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_TITLE = "key.EXTRA_TITLE";
    private FirebaseAnalytics mFirebaseAnalytics;
    private int timeline_id = 0;
    private String title;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private SwipeRefreshLayout swipe_refresh;
    private Call<CallbackTimeline> callbackCall = null;
    private RecyclerView recyclerView;
    private AdapterTimeline mAdapter;

    // activity transition
    public static void navigate(Activity activity, int timeline_id, String title) {
        Intent i = new Intent(activity, ActivityTimeline.class);
        i.putExtra(EXTRA_OBJECT, timeline_id);
        i.putExtra(EXTRA_TITLE, title);
        activity.startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_timeline);
        parent_view = findViewById(android.R.id.content);
        timeline_id = (Integer) getIntent().getSerializableExtra(EXTRA_OBJECT);
        title = (String) getIntent().getSerializableExtra(EXTRA_TITLE);
        initComponent();
        initToolbar();
        requestAction();
    }

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

    private void initComponent() {
        swipe_refresh = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterTimeline(this, recyclerView, new ArrayList<Timeline>());
        recyclerView.setAdapter(mAdapter);

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                mAdapter.resetListData();
                requestAction();
            }
        });
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterTimeline.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Timeline obj) {
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

    private void displayApiResult(final List<Timeline> items) {
        mAdapter.insertData(items);
        swipeProgress(false);
        if (items.size() == 0) showNoItemView(true);
    }

    private void requestTimeline() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getTimeline(timeline_id);
        callbackCall.enqueue(new Callback<CallbackTimeline>() {
            @Override
            public void onResponse(Call<CallbackTimeline> call, Response<CallbackTimeline> response) {
                CallbackTimeline resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    displayApiResult(resp.timeline);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackTimeline> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.txt_failed));
        } else {
            showFailedView(true, getString(R.string.txt_no_internet));
        }
    }

    private void requestAction() {
        showFailedView(false, "");
        showNoItemView(false);
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestTimeline();
            }
        }, 500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_no_item);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
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
}
