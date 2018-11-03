package com.servilat.intouch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

public class Util {
    public static final String executeCodeDialogs = "var offset = %d;" +
            "var dialogs = [];" +
            "var posts = API.messages.getDialogs({\"count\": 15, \"offset\" : offset});" +
            "var i = 0;" +
            "var count = posts.count;" +
            "while(i<posts.items.length) {" +
            "if(posts.items[i].message.chat_id!=null) {" +
            "dialogs.push(posts.items[i].message);" +
            "} else {" +
            "var user = API.users.get({\"user_ids\":  posts.items[i].message.user_id, \"fields\": \"photo_100\"});" +
            "var temp = posts.items[i].message + user[0];" +
            "dialogs.push(temp);" +
            "}" +
            "i = i +1;" +
            "}" +
            "return {\"dialogs\": dialogs, \"count\": count};";
    public static final String executeCodeNotifications = "var longPull = API.messages.getLongPollHistory({\"ts\":  %s, \"pts\": %s" +
            "});" +
            "var messages = [];" +
            "var  i=0;" +
            "var new_pts = longPull.new_pts;" +
            "var result = [];" +
            "messages = longPull.messages.items;" +
            "while(i < messages.length) {" +
            "if(messages[i].out == 0) { var temp = messages[i];" +
            "if(messages[i].chat_id == null) {" +
            "var name = API.users.get({\"user_ids\" : messages[i].user_id, \"fields\": \"photo_100\"});" +
            "temp = messages[i] + name[0];" +
            "}" +
            "result.push(temp);" +
            "}" +
            "i = i + 1;" +
            "}" +
            "return {\"messages\" : result, \"new_pts\": new_pts};";

    public static final String longPullServerRequest = "https://%s?act=a_check&key=%s&ts=%s&wait=25&mode=96&version=2";

    public static boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static void showAlertMessage(Activity activity, String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Holo_Light);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static String convertTime(long unixTime) {
        Date date = new Date(unixTime * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
}
