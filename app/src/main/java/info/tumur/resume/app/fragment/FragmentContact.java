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

import info.tumur.resume.app.ActivityContact;
import info.tumur.resume.app.ActivityMain;
import info.tumur.resume.app.R;
import info.tumur.resume.app.adapter.AdapterContact;
import info.tumur.resume.app.connection.API;
import info.tumur.resume.app.connection.RestAdapter;
import info.tumur.resume.app.connection.callbacks.CallbackContact;
import info.tumur.resume.app.model.Contact;
import info.tumur.resume.app.utils.NetworkCheck;
import info.tumur.resume.app.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentContact extends Fragment {

    private View root_view;
    private Call<CallbackContact> callbackCall = null;
    private RecyclerView recyclerView;
    private AdapterContact adapterContact;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_contact, null);

        initComponent();
        requestContactApi();
        return root_view;
    }

    private void initComponent() {
        recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity())));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        adapterContact = new AdapterContact(getActivity(), new ArrayList<Contact>());
        recyclerView.setAdapter(adapterContact);
        recyclerView.setNestedScrollingEnabled(false);

        adapterContact.setOnItemClickListener(new AdapterContact.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Contact obj) {
                Snackbar.make(root_view, obj.btn_text, Snackbar.LENGTH_SHORT).show();
                switch (obj.type.toString()) {
                    case "email":
                        Tools.sendEmail(getActivity(), obj.btn_url);
                        break;
                    case "phone":
                        Tools.dialNumber(getActivity(), obj.btn_url);
                        break;
                    default:
                        Tools.directUrl(getActivity(), obj.btn_url);
                        break;
                }
            }
        });
    }

    private void requestContactApi() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getContact();
        callbackCall.enqueue(new Callback<CallbackContact>() {
            @Override
            public void onResponse(Call<CallbackContact> call, Response<CallbackContact> response) {
                CallbackContact resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    adapterContact.setItems(resp.contacts);
                    ActivityContact.getInstance().s_contact = true;
                    ActivityContact.getInstance().showDataLoaded();
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackContact> call, Throwable t) {
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
}