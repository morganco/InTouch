package com.servilat.intouch.friends;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.servilat.intouch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendListAdapter extends ArrayAdapter<FriendItem> {
    Context context;
    List<FriendItem> friendItems;

    public FriendListAdapter(List<FriendItem> friendItems, Context context) {
        super(context, R.layout.message_item_layout, friendItems);
        this.context = context;
        this.friendItems = friendItems;
    }

    private class ViewHolder {
        TextView userNameTextView;
        ImageView userPhotoImageView;
    }

    @Override
    public int getCount() {
        return friendItems.size();
    }

    @Override
    public FriendItem getItem(int position) {
        return friendItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friendItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendListAdapter.ViewHolder holder;

        FriendItem friendItem = friendItems.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.friend_item_layout, parent, false);

            holder = new FriendListAdapter.ViewHolder();

            holder.userNameTextView = convertView.findViewById(R.id.preview_friend_name);
            holder.userPhotoImageView = convertView.findViewById(R.id.preview_friend_photo);
            convertView.setTag(holder);
        } else {
            holder = (FriendListAdapter.ViewHolder) convertView.getTag();
        }

        holder.userNameTextView.setText(friendItem.getUserName());

        Picasso.with(getContext())
                .load(friendItem.getImageURL())
                .placeholder(R.drawable.placeholder_person)
                .error(R.drawable.placeholder_person)
                .into(holder.userPhotoImageView);

        return convertView;
    }
}
