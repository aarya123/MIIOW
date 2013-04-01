package com.example.MIIOW;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Anubhaw
 * Date: 3/29/13
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryAdapter extends ArrayAdapter{
    public DirectoryAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);
    }
}
