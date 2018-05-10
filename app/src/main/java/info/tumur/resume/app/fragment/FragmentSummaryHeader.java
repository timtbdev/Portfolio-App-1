package info.tumur.resume.app.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import info.tumur.resume.app.ActivityContact;
import info.tumur.resume.app.ActivityMain;
import info.tumur.resume.app.R;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackSummaryHeader;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.SummaryHeader;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSummaryHeader extends Fragment {

    private View root_view;
    private Call<CallbackSummaryHeader> callbackCallHeader = null;
    private static final String TAG = "from";
    private String from;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_summary_header, null);
        from = getArguments().getString(TAG);
        initComponent();
        requestSummaryHeaderApi();
        return root_view;
    }

    private void initComponent() {

    }

    private void requestSummaryHeaderApi() {
        API api = RestAdapter.createAPI();
        callbackCallHeader = api.getSummaryProile();
        callbackCallHeader.enqueue(new Callback<CallbackSummaryHeader>() {
            @Override
            public void onResponse(Call<CallbackSummaryHeader> call, Response<CallbackSummaryHeader> response) {
                CallbackSummaryHeader resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    displayResultData(resp.summary_header);
                    switch (from) {
                        case "home":
                            ActivityMain.getInstance().s_header = true;
                            ActivityMain.getInstance().showDataLoaded();
                            break;
                        case "contact":
                            ActivityContact.getInstance().s_header = true;
                            ActivityContact.getInstance().showDataLoaded();
                            break;
                        default:
                            break;
                    }

                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackSummaryHeader> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(R.string.msg_failed_load_data);
        } else {
            showFailedView(R.string.no_internet_text);
        }
    }

    private void showFailedView(@StringRes int message) {
        ActivityMain.getInstance().showDialogFailed(message);
    }

    private void displayResultData(final SummaryHeader summary_profile) {
        ((TextView) root_view.findViewById(R.id.summary_name)).setText(summary_profile.summary_name);
        ((TextView) root_view.findViewById(R.id.summary_android)).setText(summary_profile.summary_android);
        ((TextView) root_view.findViewById(R.id.summary_location)).setText(summary_profile.summary_location);
        Tools.displayImageOriginalCircle(getActivity(), ((ImageView) root_view.findViewById(R.id.summary_avatar)), Constant.getURLimg(summary_profile.summary_avatar));
        Tools.displayImageOriginal(getActivity(), ((ImageView) root_view.findViewById(R.id.summary_background)), Constant.getURLimg(summary_profile.summary_background));
    }
}