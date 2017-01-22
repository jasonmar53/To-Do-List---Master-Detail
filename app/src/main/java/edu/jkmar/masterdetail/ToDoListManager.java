package edu.jkmar.masterdetail;

import java.util.ArrayList;

/**
 * Created by Jason on 11/12/2016.
 */

public class ToDoListManager {
    public static ArrayList<ListItem> mList = new ArrayList<ListItem>();

    public static ArrayList<ListItem> getmList() {
        return mList;
    }

    public static ArrayList<ListItem> getnewList() {
        mList = new ArrayList<ListItem>();
        return mList;
    }

    public static void addItem(ListItem x) {
        mList.add(x);
    }
    public static String sending() {
        String msg = "To Do List! \n";
        for (int i = 0; i < mList.size(); i++) {
            msg = msg + mList.get(i).name + "    ";
            if (mList.get(i).checked) {
                msg = msg + "1\n";
            } else {
                msg = msg + "0\n";
            }
        }
        return msg;
    }

    public static void remove(int position) {
        mList.remove(position);
    }

    public static ListItem getItem(int position) {
        return mList.get(position);
    }

    public static void setName(int position, String name) {
        mList.get(position).name = name;
        // Log.i("TAG", "setName: " + mList.get(position).name);
    }
}
