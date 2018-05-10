package info.tumur.resume.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import info.tumur.resume.app.fragment.FragmentContact;
import info.tumur.resume.app.fragment.FragmentSummaryHeader;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.Tools;

public class ActivityContact extends AppCompatActivity {

    private static final String TAG = "from";
    private ActionBar actionBar;
    private Toolbar toolbar;
    private RelativeLayout lyt_parent;
    private ProgressBar progressBar;
    private final static int LOADING_DURATION = 500;
    private View parent_view;
    private NavigationView nav_view;
    private Dialog dialog_failed = null;
    public boolean s_header = false, s_contact = false;
    static ActivityContact activityContact;
    private FirebaseAnalytics mFirebaseAnalytics;

    // activity transition
    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivityContact.class);
        activity.startActivity(i);
    }
    public static ActivityContact getInstance() {
        return activityContact;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_contact);
        activityContact = this;
        initComponent();
        showProgressBar();
        initToolbar();
        initFragment();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.main_menu_contact));
        Tools.systemBarLolipop(this);
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

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // init fragment summary header
        Bundle bundle = new Bundle();
        bundle.putString(TAG, "contact");
        FragmentSummaryHeader fragmentSummaryHeader = new FragmentSummaryHeader();
        fragmentSummaryHeader.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_summary_header, fragmentSummaryHeader);
        // init fragment summary social
        FragmentContact fragmentContact = new FragmentContact();
        fragmentTransaction.replace(R.id.fragment_contact, fragmentContact);
        // init fragment summary skills
        fragmentTransaction.commit();
    }

    private void initComponent() {
        parent_view = findViewById(R.id.parent_view);
        progressBar = parent_view.findViewById(R.id.progress_bar);
        lyt_parent = parent_view.findViewById(R.id.lyt_parent);
    }

    private void refreshFragment() {
        s_header = false;
        s_contact = false;
        showProgressBar();
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showDataLoaded() {
        if (s_header && s_contact) {
            hideProgressBar();
            Snackbar.make(parent_view, R.string.msg_data_loaded, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showDialogFailed(@StringRes int msg) {
        if (dialog_failed != null && dialog_failed.isShowing()) return;
        hideProgressBar();
        dialog_failed = new DialogUtils(this).buildDialogWarning(-1, msg, R.string.try_again, R.drawable.img_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                refreshFragment();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
            }
        });
        dialog_failed.show();
    }
    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        lyt_parent.setVisibility(View.GONE);
    }
    private void hideProgressBar(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                lyt_parent.setVisibility(View.VISIBLE);
            }
        }, LOADING_DURATION);
    }
}