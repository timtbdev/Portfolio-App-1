package info.tumur.resume.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.adapter.AdapterSkills;
import info.tumur.resume.app.adapter.AdapterSocial;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackProfile;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.Profile;
import info.tumur.resume.app.model.Skills;
import info.tumur.resume.app.model.Social;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMain extends AppCompatActivity {

    private final static int LOADING_DURATION = 1000; // Loading duration
    private Toolbar toolbar;
    private View parent_view;
    private NavigationView nav_view;
    static boolean active = false; // indicator for activity status
    // Variables for UI Views
    private ActionBar actionBar;
    private View lyt_progress_bar;
    private View lyt_no_internet;
    private View lyt_no_connect;
    private View lyt_no_item;
    private View lyt_main_content;
    private SwipeRefreshLayout swipe_refresh;
    private long exitTime = 0; // Application exit time
    private Dialog dialog = null; // Dialog for No Internet, Server connection and no item
    static ActivityMain activityMain;

    //Variables for Callbacks and API services
    private Call<CallbackProfile> callbackCallProfile = null; // Callback for Profile data
    // UI for Skills
    private RecyclerView recyclerViewSkills; // RecyclerView for Skills
    private AdapterSkills adapterSkills; // Adapter for Skills
    // UI for Social buttons
    private RecyclerView recyclerViewSocial; // RecyclerView for Social buttons
    private AdapterSocial adapterSocial; // Adapter for Social buttons
    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics; // Used for Firebase Analytics init

    // getting Instance for UI
    public static ActivityMain getInstance() {
        return activityMain;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_main);
        activityMain = this; // getting Instance for UI
        initToolbar(); // InitToolbar
        initDrawerMenu(); // Init Drawer Menu
        initComponent(); // Init Components
        // Show Progressbar
        showProgressBar(true, false, false, false, false);
        requestProfileApi();// API Request

    }

    // Toolbar
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.txt_main_menu_summary); // Toolbar title
        Tools.systemBarLolipop(activityMain);
    }

    // Drawer Menu
    private void initDrawerMenu() {
        nav_view = findViewById(R.id.main_nav_view);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.txt_main_menu_navigation_drawer_open, R.string.txt_main_menu_navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Setting Menu SelectListener
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                onItemSelected(item.getItemId());
                return true;
            }
        });
        // Setting menu icon color for user interaction
        nav_view.setItemIconTintList(getResources().getColorStateList(R.color.nav_state_list));
    }

    // Initialize Drawer menu selection
    public boolean onItemSelected(int id) {
        Intent i;
        switch (id) {
            //Drawer menus
            case R.id.nav_experience:
                ActivityTimeline.navigate(this, 1, getResources().getString(R.string.txt_main_menu_experience));
                break;
            case R.id.nav_education:
                ActivityTimeline.navigate(this, 2, getResources().getString(R.string.txt_main_menu_education));
                break;
            case R.id.nav_language:
                ActivityTimeline.navigate(this, 3, getResources().getString(R.string.txt_main_menu_languages));
                break;
            case R.id.nav_portfolio:
                ActivityPortfolio.navigate(this);
                break;
            case R.id.nav_rate:
                Tools.rateAction(this);
                break;
            case R.id.nav_share:
                Tools.shareAction(this);
                break;
            case R.id.nav_contact:
                ActivityContact.navigate(this);
                break;
            default:
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        return true;
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

        //Set Social button UI
        recyclerViewSocial = parent_view.findViewById(R.id.recyclerViewSocial);
        recyclerViewSocial.setLayoutManager(new GridLayoutManager(this, Tools.getGridSpanCount(activityMain)));
        recyclerViewSocial.setHasFixedSize(true);

        //Set social button data and list adapter
        adapterSocial = new AdapterSocial(activityMain, new ArrayList<Social>());
        recyclerViewSocial.setAdapter(adapterSocial);
        recyclerViewSocial.setNestedScrollingEnabled(false);
        // Social item clicked
        adapterSocial.setOnItemClickListener(new AdapterSocial.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Social social) {
                Tools.directUrl(activityMain, social.social_btn_url);
            }
        });

        //Set Skills UI
        recyclerViewSkills = parent_view.findViewById(R.id.recyclerViewSkills);
        recyclerViewSkills.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSkills.setHasFixedSize(true);

        //Set skills data and list adapter
        adapterSkills = new AdapterSkills(activityMain, new ArrayList<Skills>());
        recyclerViewSkills.setAdapter(adapterSkills);
        recyclerViewSkills.setNestedScrollingEnabled(false);

        // Refresh content on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCallProfile != null && callbackCallProfile.isExecuted())
                    callbackCallProfile.cancel();
                // Show progressbar while retry
                swipeProgress(true);
                retry(true); // retry to connect Internet and Server
            }
        });
    }

    // API Request for Profile data
    private void requestProfileApi() {
        API api = RestAdapter.createAPI();
        callbackCallProfile = api.getProfile();
        callbackCallProfile.enqueue(new Callback<CallbackProfile>() {
            @Override
            public void onResponse(Call<CallbackProfile> call, Response<CallbackProfile> response) {
                CallbackProfile resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    // Display gathered data
                    displayResult(resp.profile, resp.skills, resp.social);
                } else {
                    onFailRequest(); // Failed Request handler
                }
            }

            @Override
            public void onFailure(Call<CallbackProfile> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                // Failed Request handler
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    // Show result of API
    private void displayResult(final Profile profile, final List<Skills> skills, final List<Social> social) {

        // Set Profile data
        Tools.displayImageOriginal(activityMain, ((ImageView) parent_view.findViewById(R.id.background_image)), Constant.getURLimg(profile.profile_background));
        Tools.displayImageOriginal(activityMain, ((ImageView) parent_view.findViewById(R.id.avatar)), Constant.getURLimg(profile.profile_avatar));
        ((TextView) parent_view.findViewById(R.id.name)).setText(profile.profile_name);
        ((TextView) parent_view.findViewById(R.id.title)).setText(profile.profile_title);
        ((TextView) parent_view.findViewById(R.id.location)).setText(profile.profile_location);

        adapterSocial.insertData(social);

        // Adding skills category
        String temp_category = "empty"; // temporary holder for a category of skills for if method
        for (int i = 0; i < skills.size(); i++) {
            if (!temp_category.equals(skills.get(i).skill_category.toString())) {
                skills.add(i, new Skills(skills.get(i).skill_category, true));
                temp_category = skills.get(i).skill_category.toString();
            }
        }

        adapterSkills.insertData(skills);

        // Setting Introduction text
        ((TextView) parent_view.findViewById(R.id.introduction)).setText(profile.profile_introduction);

        // Check item size
        if (skills.size() == 0 || social.size() == 0 || profile == null || profile.toString().trim().isEmpty()) {
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
                requestProfileApi(); // Request for API
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
        if (callbackCallProfile != null && callbackCallProfile.isExecuted()) {
            callbackCallProfile.cancel();
        }
    }

    // Hander for Back button
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            doExitApp(); // Exit app
        }
    }

    // Exit app with delay
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > LOADING_DURATION) {
            Toast.makeText(this, R.string.txt_press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}