package info.tumur.resume.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import info.tumur.resume.app.fragment.FragmentSummaryHeader;
import info.tumur.resume.app.fragment.FragmentSummaryIntroduction;
import info.tumur.resume.app.fragment.FragmentSummarySkills;
import info.tumur.resume.app.fragment.FragmentSummarySocial;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.Tools;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "from";
    private ActionBar actionBar;
    private Toolbar toolbar;
    private NestedScrollView nested_view;
    private ProgressBar progressBar;
    private final static int LOADING_DURATION = 1000;
    private View parent_view;
    private NavigationView nav_view;
    private Dialog dialog_failed = null;
    public boolean s_header = false, s_social = false, s_skills = false, s_introduction = false;
    static ActivityMain activityMain;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static ActivityMain getInstance() {
        return activityMain;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_main);
        activityMain = this;
        initComponent();
        showProgressBar();
        initToolbar();
        initDrawerMenu();
        initFragment();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.txt_main_menu_summary);
    }

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

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                onItemSelected(item.getItemId());
                return true;
            }
        });
        nav_view.setItemIconTintList(getResources().getColorStateList(R.color.nav_state_list));
    }

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

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // init fragment summary header
        Bundle bundle = new Bundle();
        bundle.putString(TAG, "home");
        FragmentSummaryHeader fragmentSummaryHeader = new FragmentSummaryHeader();
        fragmentSummaryHeader.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_summary_header, fragmentSummaryHeader);
        // init fragment summary social
        FragmentSummarySocial fragmentSummarySocial = new FragmentSummarySocial();
        fragmentTransaction.replace(R.id.fragment_summary_social, fragmentSummarySocial);
        FragmentSummaryIntroduction fragmentSummaryIntroduction = new FragmentSummaryIntroduction();
        fragmentTransaction.replace(R.id.fragment_summary_introduction, fragmentSummaryIntroduction);
        FragmentSummarySkills fragmentSummarySkills = new FragmentSummarySkills();
        fragmentTransaction.replace(R.id.fragment_summary_skills, fragmentSummarySkills);
        // init fragment summary skills
        fragmentTransaction.commit();
    }

    private void initComponent() {
        parent_view = findViewById(R.id.parent_view);
        nested_view = parent_view.findViewById(R.id.nested_content);
        progressBar = parent_view.findViewById(R.id.progress_bar);
    }

    private void refreshFragment() {
        s_header = false;
        s_social = false;
        s_introduction = false;
        s_skills = false;
        showProgressBar();
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    private long exitTime = 0;
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.txt_press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void showDataLoaded() {
        if (s_header && s_social && s_introduction && s_skills) {
            hideProgressBar();
        }
    }

    public void showDialogFailed(@StringRes int msg) {
        if (dialog_failed != null && dialog_failed.isShowing()) return;
        hideProgressBar();
        dialog_failed = new DialogUtils(this).buildDialogWarning(-1, msg, R.string.txt_try_again, R.drawable.img_no_connect, new CallbackDialog() {
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
        nested_view.setVisibility(View.GONE);
    }
    private void hideProgressBar(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                nested_view.setVisibility(View.VISIBLE);
            }
        }, LOADING_DURATION);
    }
}