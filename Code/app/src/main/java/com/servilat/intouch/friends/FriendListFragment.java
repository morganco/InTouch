package com.servilat.intouch.friends;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.servilat.intouch.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends ListFragment {
    private Context context;

    private FriendListAdapter adapter;
    private int friendsCount;
    private int friendsOffset;
    private static final int COUNT = 15;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.friends_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                VKRequest deleteFriendRequest = VKApi.friends().delete(VKParameters.from(
                        VKApiConst.USER_ID, adapter.getItem(info.position).getUserID()));
                deleteFriendRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        adapter.remove(adapter.getItem(info.position));
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            case R.id.menu_blacklist:
                VKRequest banFriendRequest = new VKRequest("account.ban", VKParameters.from(
                        VKApiConst.OWNER_ID, adapter.getItem(info.position).getUserID()));
                banFriendRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        adapter.remove(adapter.getItem(info.position));
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.friends);
        ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_friends);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        friendsOffset = 0;
        List<FriendItem> friendItems = new ArrayList<>();

        adapter = new FriendListAdapter(friendItems, context);
        setListAdapter(adapter);

        View view = inflater.inflate(R.layout.friends_layout, container, false);
        getVkFriends();

        return view;
    }

    private ArrayList<FriendItem> getVkFriends() {
        final ArrayList<FriendItem> friendItems = new ArrayList<>();
        VKRequest request = VKApi.friends().get(VKParameters.from(
                "order", "hints",
                VKApiConst.COUNT, COUNT,
                VKApiConst.OFFSET, friendsOffset,
                VKApiConst.FIELDS, "photo_100"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                adapter.addAll(parseFriends(parseVkResponse(response.json)));
                adapter.notifyDataSetChanged();
            }
        });
        return friendItems;
    }

    JSONArray parseVkResponse(JSONObject vkResponse) {
        JSONArray items = null;
        try {
            items = vkResponse.getJSONObject("response").getJSONArray("items");
            friendsCount = vkResponse.getJSONObject("response").getInt("count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        registerForContextMenu(listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int preLast;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (preLast != lastItem) {
                        if (friendsOffset < friendsCount) {
                            friendsOffset += 15;
                            getVkFriends();
                        }
                        preLast = lastItem;
                    }
                }
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            showUserProfile(position);
        });
    }

    ArrayList<FriendItem> parseFriends(JSONArray friends) {
        ArrayList<FriendItem> friendsList = new ArrayList<>();

        for (int i = 0; i < friends.length(); i++) {
            try {
                FriendItem friendItem = new FriendItem(friends.getJSONObject(i));
                friendsList.add(friendItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return friendsList;
    }

    void showUserProfile(int position) {
        /*UserChatFragment dialogsListFragment = new UserChatFragment();

        FriendItem dialogsItem = friendItems.get(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("CURRENT_USER_ID", new User(
                "",
                "",
                VKAccessToken.currentToken().userId));

        bundle.putSerializable(UserChatFragment.CURRENT_USER_ID_DIALOG_WITH, new User(
                dialogsItem.getImageURL(),
                dialogsItem.getDialogName(),
                dialogsItem.getUserID()));

        dialogsListFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, dialogsListFragment, "visible_dialog");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();*/
    }
}
