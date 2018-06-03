package info.tumur.resume.app.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.tumur.resume.app.ActivityMain;
import info.tumur.resume.app.R;
import info.tumur.resume.app.adapter.AdapterSocial;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackSummarySocial;
import info.tumur.resume.app.model.Social;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSummarySocial extends Fragment {

    private View root_view;
    private Call<CallbackSummarySocial> callbackCallSocial = null;
    private RecyclerView recyclerView;
    private AdapterSocial adapterSocial;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_summary_social, null);

        initComponent();
        requestSummarySocialApi();
        return root_view;
    }

    private void initComponent() {
        recyclerView = root_view.findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity())));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        adapterSocial = new AdapterSocial(getActivity(), new ArrayList<Social>());
        recyclerView.setAdapter(adapterSocial);
        recyclerView.setNestedScrollingEnabled(false);

        adapterSocial.setOnItemClickListener(new AdapterSocial.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Social obj) {
                Snackbar.make(root_view, obj.btn_text, Snackbar.LENGTH_SHORT).show();
                Tools.directUrl(getActivity(), obj.btn_url);
            }
        });
    }

    private void requestSummarySocialApi() {
        API api = RestAdapter.createAPI();
        callbackCallSocial = api.getSummarySocial();
        callbackCallSocial.enqueue(new Callback<CallbackSummarySocial>() {
            @Override
            public void onResponse(Call<CallbackSummarySocial> call, Response<CallbackSummarySocial> response) {
                CallbackSummarySocial resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    adapterSocial.setItems(resp.summary_social);
                    ActivityMain.getInstance().s_social = true;
                    ActivityMain.getInstance().showDataLoaded();
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackSummarySocial> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(R.string.msg_failed_load_data);
        } else {
            showFailedView(R.string.txt_no_internet);
        }
    }

    private void showFailedView(@StringRes int message) {
        ActivityMain.getInstance().showDialogFailed(message);
    }
}