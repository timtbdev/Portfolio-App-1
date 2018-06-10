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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.adapter.AdapterContact;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackContact;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.Contact;
import info.tumur.resume.app.model.Profile;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityContact extends AppCompatActivity {

    private final static int LOADING_DURATION = 1000; // Loading duration
    private Toolbar toolbar;
    private View parent_view;
    static boolean active = false; // indicator for activity status
    // Variables for UI Views
    private ActionBar actionBar;
    private View lyt_progress_bar;
    private View lyt_no_internet;
    private View lyt_no_connect;
    private View lyt_no_item;
    private View lyt_main_content;
    private SwipeRefreshLayout swipe_refresh;
    private Dialog dialog = null; // Dialog for No Internet, Server connection and no item
    static ActivityContact activityContact;

    //Variables for Callbacks and API services
    private Call<CallbackContact> callbackCallContact = null; // Callback for Profile data

    // UI for Contact buttons
    private RecyclerView recyclerView; // RecyclerView for Contact buttons
    private AdapterContact adapterContact; // Adapter for Contact buttons
    //Firebase Analytics
    private FirebaseAnalytics mFirebaseAnalytics;// Used for Firebase Analytics init

    // Activity transition
    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivityContact.class);
        activity.startActivity(i);
    }

    // getting Instance for UI
    public static ActivityContact getInstance() {
        return activityContact;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_contact);
        activityContact = this; // getting Instance for UI
        initToolbar(); // InitToolbar
        initComponent();// Init Components
        // show Progressbar
        showProgressBar(true, false, false, false, false);
        requestContactApi();// API Request

    }

    // Toolbar
    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.txt_main_menu_contact)); // Toolbar title
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

        //Set Contact button UI
        recyclerView = parent_view.findViewById(R.id.recyclerViewSocial);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Tools.getGridSpanCount(activityContact)));
        recyclerView.setHasFixedSize(true);

        //Set social button data and list adapter
        adapterContact = new AdapterContact(activityContact, new ArrayList<Contact>());
        recyclerView.setAdapter(adapterContact);
        recyclerView.setNestedScrollingEnabled(false);
        // Social item clicked
        adapterContact.setOnItemClickListener(new AdapterContact.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Contact contact) {
                switch (contact.contact_type) {
                    case "email":
                        Tools.sendEmail(activityContact, contact.contact_btn_url);
                        break;
                    case "phone":
                        Tools.dialNumber(activityContact, contact.contact_btn_url);
                        break;
                    default:
                        Tools.directUrl(activityContact, contact.contact_btn_url);
                }
            }
        });

        // Refresh content on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCallContact != null && callbackCallContact.isExecuted())
                    callbackCallContact.cancel();
                // Show progressbar while retry
                swipeProgress(true);
                retry(true); // retry to connect Internet and Server
            }
        });
    }

    // API Request for Contact data
    private void requestContactApi() {
        API api = RestAdapter.createAPI();
        callbackCallContact = api.getContact();
        callbackCallContact.enqueue(new Callback<CallbackContact>() {
            @Override
            public void onResponse(Call<CallbackContact> call, Response<CallbackContact> response) {
                CallbackContact resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    // Display gathered data
                    displayResult(resp.profile, resp.contact);

                } else {
                    onFailRequest();// Failed Request handler
                }
            }

            @Override
            public void onFailure(Call<CallbackContact> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                // Failed Request handler
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    // Show result of API
    private void displayResult(final Profile profile, final List<Contact> contact) {

        // Set Profile data
        Tools.displayImageOriginal(activityContact, ((ImageView) parent_view.findViewById(R.id.background_image)), Constant.getURLimg(profile.profile_background));
        Tools.displayImageOriginal(activityContact, ((ImageView) parent_view.findViewById(R.id.avatar)), Constant.getURLimg(profile.profile_avatar));
        ((TextView) parent_view.findViewById(R.id.name)).setText(profile.profile_name);
        ((TextView) parent_view.findViewById(R.id.title)).setText(profile.profile_title);
        ((TextView) parent_view.findViewById(R.id.location)).setText(profile.profile_location);

        // Set portfolio data from API
        adapterContact.insertData(contact);

        // Check item size
        if (contact.size() == 0 || profile == null || profile.toString().trim().isEmpty()) {
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
                requestContactApi();// Request for API
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
        if (callbackCallContact != null && callbackCallContact.isExecuted()) {
            callbackCallContact.cancel();
        }
    }


}