package info.tumur.resume.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.tumur.resume.app.R;


public class DialogUtils {

    private final static int NO_INTERNET = 1; // No Internet Connection
    private final static int NO_SERVER = 2; // NO Server Connection or Server is under maintenance
    private final static int APP_OUTDATED = 3; // Application is outdated

    @BindView(R.id.iv_dialog_icon)
    ImageView iv_dialog_icon;
    @BindView(R.id.tv_dialog_title)
    TextView tv_dialog_title;
    @BindView(R.id.tv_dialog_message)
    TextView tv_dialog_message;
    @BindView(R.id.btn_dialog_cancel)
    Button btn_dialog_cancel;
    @BindView(R.id.btn_dialog_ok)
    Button btn_dialog_ok;

    private Activity activity;

    public DialogUtils(Activity activity) {
        this.activity = activity;
    }

    private Dialog buildDialogView(@LayoutRes int layout) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Inflating view for Butterknife bind
        View view = View.inflate(activity, layout, null);
        ButterKnife.bind(this, view);

        //Setting view for dialog
        dialog.setContentView(view);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        return dialog;
    }

    // DialogUtils Warning for App version Update, No Internet and Server connections
    public Dialog buildDialogWarning(@StringRes int title, @StringRes int content, @StringRes int bt_text_pos, @StringRes int bt_text_neg, @DrawableRes int icon, final CallbackDialog callback) {
        String _title = null;
        String _content = null;
        String _bt_text_neg = null;

        if (title != -1) _title = activity.getString(title);
        if (content != -1) _content = activity.getString(content);
        if (bt_text_neg != -1) _bt_text_neg = activity.getString(bt_text_neg);

        return buildDialogWarning(_title, _content, activity.getString(bt_text_pos), _bt_text_neg, icon, callback);
    }

    // DialogUtils Warning for No Internet and Server connections
    public Dialog buildDialogWarning(String title, String content, String bt_text_pos, String bt_text_neg, @DrawableRes int icon, final CallbackDialog callback) {

        final Dialog dialog = buildDialogView(R.layout.dialog_warning);

        // if id = -1 view will gone
        if (title != null && !title.isEmpty()) {
            tv_dialog_title.setText(title);
        } else {
            tv_dialog_title.setVisibility(View.GONE);
        }

        // if id = -1 view will gone
        if (content != null) {
            tv_dialog_message.setText(content);
        } else {
            tv_dialog_message.setVisibility(View.GONE);
        }

        btn_dialog_ok.setText(bt_text_pos);

        if (bt_text_neg != null) {
            btn_dialog_cancel.setText(bt_text_neg);
        } else {
            btn_dialog_cancel.setVisibility(View.GONE);
        }
        iv_dialog_icon.setImageResource(icon);

        btn_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPositiveClick(dialog);
            }
        });

        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onNegativeClick(dialog);
            }
        });
        return dialog;
    }

}
