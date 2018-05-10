package info.tumur.resume.app.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.Locale;

import info.tumur.resume.app.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Tools {

    public static boolean needRequestPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setSystemBarColor(Activity act, int color) {
        if (isLolipopOrHigher()) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setSystemBarColor(Activity act, String color) {
        setSystemBarColor(act, Color.parseColor(color));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setSystemBarColorDarker(Activity act, int color) {
        setSystemBarColor(act, colorDarker(color));
    }

    public static boolean isLolipopOrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public static void systemBarLolipop(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void rateAction(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void shareAction(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToShare = new Intent(Intent.ACTION_SEND);
        goToShare.putExtra(Intent.EXTRA_TEXT, "I would like to share this app");
        goToShare.putExtra(Intent.EXTRA_STREAM, uri);
        goToShare.setType("text/plain");
        try {
            activity.startActivity(Intent.createChooser(goToShare, "Share this app via ..."));
        } catch (ActivityNotFoundException e) {
            //activity.startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }


    public static int getVersionCode(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static void displayImageOriginal(Context ctx, ImageView img, String url) {
        try {
            Glide.with(ctx).load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .transition(withCrossFade())
                    .into(img);
        } catch (Exception e) {
        }
    }

    public static void displayImageOriginalCircle(Context ctx, ImageView img, String url) {
        try {
            Glide.with(ctx).load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .into(img);
        } catch (Exception e) {
        }
    }

    public static void displayImageThumbnail(Context ctx, ImageView img, String url, float thumb) {
        try {
            Glide.with(ctx).load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .thumbnail(thumb)
                    .into(img);
        } catch (Exception e) {

        }
    }

    public static String getFormattedDate(Long dateTime) {
        //SimpleDateFormat newFormat = new SimpleDateFormat("MM dd, yyyy");
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(dateTime * 1000L);
        String date = DateFormat.format("MMMM dd, yyyy", cal).toString();
        return date;
    }

    public static int getGridSpanCount(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.spacing_190);
        return Math.round(screenWidth / cellWidth);
    }

    public static void dialNumber(Context ctx, String phone) {
        try {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + phone));
            ctx.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(ctx, "Cannot dial number", Toast.LENGTH_SHORT);
        }
    }

    public static void directUrl(Context ctx, String website) {
        String url = website;
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        ctx.startActivity(i);
    }
    public static void sendEmail(Context ctx, String email) {
        String e = email;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{e});
        i.putExtra(Intent.EXTRA_SUBJECT, "Contact from app");
        i.putExtra(Intent.EXTRA_TEXT, "message");
        i.setType("message/rfc822");
        ctx.startActivity(Intent.createChooser(i, "Choose an Email client :"));
    }


    public static int colorDarker(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);
    }
}
