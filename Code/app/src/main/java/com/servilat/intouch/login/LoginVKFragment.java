package com.servilat.intouch.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.servilat.intouch.MainActivity;
import com.servilat.intouch.R;
import com.servilat.intouch.Util;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;
import java.util.Arrays;

import static com.servilat.intouch.Util.showAlertMessage;

public class LoginVkFragment extends Fragment implements View.OnClickListener {
    private String[] vkScope = {VKScope.MESSAGES, VKScope.OFFLINE, VKScope.STATUS};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_vk_layout, container, false);

        Button button = view.findViewById(R.id.login_button_vk);

        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (Util.isConnectedToInternet()) {
            loginAction();
        } else {
            showAlertMessage(
                    getActivity(),
                    getString(R.string.internet_connection_error_title),
                    getString(R.string.internet_connection_error_message));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                ((MainActivity) getActivity()).vkRequestForHeader();
                getActivity().getSupportFragmentManager().popBackStack();
                ((MainActivity) getActivity()).showUserDialogs();
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loginAction() {
        Intent intent = new Intent(getActivity(), VKServiceActivity.class);
        intent.putExtra("arg1", "Authorization");
        ArrayList<String> scope = new ArrayList<>(Arrays.asList(vkScope));
        intent.putStringArrayListExtra("arg2", scope);
        intent.putExtra("arg4", VKSdk.isCustomInitialize());
        startActivityForResult(intent, VKServiceActivity.VKServiceType.Authorization.getOuterCode());
    }
}
