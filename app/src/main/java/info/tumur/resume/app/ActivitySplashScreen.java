package info.tumur.resume.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.app.Dialog;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Timer;
import java.util.TimerTask;

import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackCheckVersion;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplashScreen extends AppCompatActivity {


    private final static int LOADING_DURATION = 1000; // Loading duration
    private final static int NO_INTERNET = 1; // No Internet Connection
    private final static int NO_SERVER = 2; // NO Server Connection or Server is under maintenance
    private final static int APP_OUTDATED = 3; // Application is outdated
    private Dialog dialog = null; // DialogUtils for No Internet, Server connection and no item
    private FirebaseAnalytics mFirebaseAnalytics; // Used for Firebase Analytics init

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_splash);

    }

    // Starting process
    private void startProcess() {
        // Checking internet connection
        if (!NetworkCheck.isConnect(this)) {

            showDialogNoInternetConnection();
        } else {
            // Checking server connection
            requestInfo();
        }
    }

    // Delay for SplashScreen
    private void startActivityMainDelay() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(ActivitySplashScreen.this, ActivityMain.class);
                startActivity(i);
                finish();
            }
        };
        new Timer().schedule(task, LOADING_DURATION);
    }

    // API Request for active App version and network connection
    private void requestInfo() {
        API api = RestAdapter.createAPI();
        Call<CallbackCheckVersion> callbackCall = api.getCheckVersion(Tools.getVersionCode(this));
        callbackCall.enqueue(new Callback<CallbackCheckVersion>() {
            @Override
            public void onResponse(Call<CallbackCheckVersion> call, Response<CallbackCheckVersion> response) {
                CallbackCheckVersion resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    checkAppVersion(resp.active);
                } else {
                    // show DialogUtils for No Server Connection
                    showDialogNoServerConnection();
                }
            }

            @Override
            public void onFailure(Call<CallbackCheckVersion> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                // show DialogUtils for No Server Connection
                showDialogNoServerConnection();
            }
        });
    }

    // Check active App version
    private void checkAppVersion(boolean active) {
        if (!active) {
            showDialogOutDate();
        } else {
            startActivityMainDelay();
        }
    }

    // Delay for next retry
    private void retryOpenApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProcess();
            }
        }, LOADING_DURATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    // DialogUtils for no internet connection
    public void showDialogNoInternetConnection() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_no_internet, R.string.msg_no_internet, R.string.txt_try_again, R.string.txt_close, R.drawable.ic_no_internet, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication(); // retry to connect Internet and Server
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }

    // DialogUtils for Update of App version
    public void showDialogOutDate() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_update, R.string.msg_app_out_date, R.string.txt_update, R.string.txt_close, R.drawable.ic_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                Tools.rateAction(ActivitySplashScreen.this); // Go to Application Update
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                dialog.dismiss();
                startActivityMainDelay();
            }
        });
        if (!isFinishing()) {
            dialog.show();
        }

    }

    // DialogUtils for no server connection
    public void showDialogNoServerConnection() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_unable_connect, R.string.msg_unable_connect, R.string.txt_try_again, R.string.txt_close, R.drawable.ic_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication(); // retry to connect Internet and Server
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        if (!isFinishing()) {
            dialog.show();
        }

    }
}
