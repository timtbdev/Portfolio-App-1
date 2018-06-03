package info.tumur.resume.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Timer;
import java.util.TimerTask;

import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackCheck;
import info.tumur.resume.app.utils.CallbackDialog;
import info.tumur.resume.app.utils.DialogUtils;
import info.tumur.resume.app.utils.NetworkCheck;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    // Loading duration
    private final static int LOADING_DURATION = 1000;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startProcess();
    }

    private void startProcess() {
        // Checking internet connection
        if (!NetworkCheck.isConnect(this)) {
            dialogNoInternet();
        } else {
            // Checking server connection
            requestInfo();
            startActivityMainDelay();
        }
    }

    // Delay for SplashScreen
    private void startActivityMainDelay() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
                startActivity(i);
                finish();
            }
        };
        new Timer().schedule(task, LOADING_DURATION);
    }

    // Request for API connection check
    private void requestInfo() {
        API api = RestAdapter.createAPI();
        Call<CallbackCheck> callbackCall = api.getCheck();
        callbackCall.enqueue(new Callback<CallbackCheck>() {
            @Override
            public void onResponse(Call<CallbackCheck> call, Response<CallbackCheck> response) {
                CallbackCheck resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    // Start next activity
                    startActivityMainDelay();
                } else {
                    // No server connection
                    dialogServerNotConnect();
                }
            }

            @Override
            public void onFailure(Call<CallbackCheck> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                dialogServerNotConnect();
            }
        });
    }

    // Dialog for no server connection
    public void dialogServerNotConnect() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_unable_connect, R.string.msg_unable_connect, R.string.txt_try_again, R.string.txt_close, R.drawable.img_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }


    // Dialog for no internet connection
    public void dialogNoInternet() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.txt_no_internet, R.string.msg_no_internet, R.string.txt_try_again, R.string.txt_close, R.drawable.img_no_internet, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
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
}
