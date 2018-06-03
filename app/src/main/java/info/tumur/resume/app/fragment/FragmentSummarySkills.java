package info.tumur.resume.app.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import info.tumur.resume.app.ActivityMain;
import info.tumur.resume.app.R;
import info.tumur.resume.app.adapter.AdapterSkills;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackSummarySkills;
import info.tumur.resume.app.model.Skills;
import info.tumur.resume.app.utils.NetworkCheck;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSummarySkills extends Fragment {

    private View root_view;
    private Call<CallbackSummarySkills> callbackCallSkills = null;
    private RecyclerView recyclerView;
    private AdapterSkills adapterSkills;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_summary_skills, null);

        initComponent();
        requestSummarySkillsApi();
        return root_view;
    }

    private void initComponent() {
        recyclerView = root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        //adapterSkills = new AdapterSkills(getActivity(), new ArrayList<Skills>());
    }

    private void requestSummarySkillsApi() {
        API api = RestAdapter.createAPI();
        callbackCallSkills = api.getSummarySkills();
        callbackCallSkills.enqueue(new Callback<CallbackSummarySkills>() {
            @Override
            public void onResponse(Call<CallbackSummarySkills> call, Response<CallbackSummarySkills> response) {
                CallbackSummarySkills resp = response.body();
                if (resp != null && resp.status.equals("success")) {

                    List<Skills> items = resp.summary_skills;
                    String temp_category = "test";
                    for (int i = 0; i < items.size(); i++) {
                        if (!temp_category.equals(items.get(i).skill_category.toString())) {
                            items.add(i, new Skills(items.get(i).skill_category, true));
                            temp_category = items.get(i).skill_category.toString();
                        }
                    }

                    adapterSkills = new AdapterSkills(getActivity(), items);
                    recyclerView.setAdapter(adapterSkills);
                    recyclerView.setNestedScrollingEnabled(false);
                    ActivityMain.getInstance().s_skills = true;
                    ActivityMain.getInstance().showDataLoaded();
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackSummarySkills> call, Throwable t) {
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