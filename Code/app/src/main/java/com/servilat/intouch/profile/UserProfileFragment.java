package com.servilat.intouch.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.servilat.intouch.R;
import com.servilat.intouch.chat.User;
import com.servilat.intouch.chat.UserChatFragment;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    public static final String UNKNOWN = "unknown";
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_layout, container, false);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.user_profile);

        Button button = view.findViewById(R.id.write_message_btn);

        button.setOnClickListener(this);

        VKRequest getUserInfo = VKApi.users().get(VKParameters.from(
                VKApiConst.USER_IDS, getArguments().getString("USER_ID"),
                VKApiConst.FIELDS, "bdate,personal,home_town,country,city,status,contacts, photo_200",
                VKApiConst.NAME_CASE, "Nom"
        ));

        getUserInfo.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    parseUserProfile(response.json.getJSONArray("response").getJSONObject(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    void parseUserProfile(JSONObject userInfo) {
        try {
            user = new User(
                    userInfo.getString("photo_200"),
                    userInfo.getString("first_name") + " " + userInfo.getString("last_name"),
                    userInfo.getString("id")
            );
            ((TextView) getActivity().findViewById(R.id.profile_user_id)).append(user.getUserID());
            ((TextView) getActivity().findViewById(R.id.profile_user_name)).setText(user.getDialogName());

            Picasso.with(getContext())
                    .load(user.getImageURL())
                    .placeholder(R.drawable.placeholder_person)
                    .error(R.drawable.placeholder_person)
                    .into((ImageView) getActivity().findViewById(R.id.profile_user_photo));

            if (userInfo.has("bdate") && !userInfo.getString("bdate").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_user_birthday)).append(userInfo.getString("bdate"));
            } else {
                ((TextView) getActivity().findViewById(R.id.profile_user_birthday)).append(UNKNOWN);
            }

            if (userInfo.has("city") && !userInfo.getString("city").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_user_city)).append(userInfo.getJSONObject("city").getString("title"));
            } else {
                ((TextView) getActivity().findViewById(R.id.profile_user_city)).append(UNKNOWN);
            }
            if (userInfo.has("home_town") && !userInfo.getString("home_town").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_user_home_town)).append(userInfo.getString("home_town"));
            } else {
                ((TextView) getActivity().findViewById(R.id.profile_user_home_town)).append(UNKNOWN);
            }
            if (userInfo.has("status")) {
                ((TextView) getActivity().findViewById(R.id.profile_user_status)).append(userInfo.getString("status"));
            }
            if (userInfo.has("mobile_phone") && !userInfo.getString("mobile_phone").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_phone_number)).append(userInfo.getString("mobile_phone"));
            } else {
                ((TextView) getActivity().findViewById(R.id.profile_phone_number)).append(UNKNOWN);
            }

            if (userInfo.has("personal") && userInfo.getJSONObject("personal").has("langs")) {
                ((TextView) getActivity().findViewById(R.id.profile_user_languages))
                        .append(userInfo.getJSONObject("personal").getString("langs").replaceAll("[\"\\[\\]]", ""));
            } else {
                ((TextView) getActivity().findViewById(R.id.profile_user_languages)).append(UNKNOWN);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        UserChatFragment dialogsListFragment = new UserChatFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("CURRENT_USER_ID", new User(
                "",
                "",
                VKAccessToken.currentToken().userId));

        bundle.putSerializable(UserChatFragment.CURRENT_USER_ID_DIALOG_WITH, user);

        dialogsListFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, dialogsListFragment, "visible_dialog");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}
