package com.example.MIIOW;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DirectoryAdapter extends ArrayAdapter {
    Context c;
    private ArrayList<DirectoryObject> list;

    public DirectoryAdapter(Context context, int textViewResourceId, ArrayList<DirectoryObject> list) {
        super(context, textViewResourceId, list);
        this.c = context;
        this.list = list;
    }

    // hold the elements in the layout
    public static class ViewHolder {
        public TextView objectName;
        public ImageView icon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.directorylayout, null);
            holder = new ViewHolder();
            holder.objectName = (TextView) v.findViewById(R.id.objectName);
            holder.icon = (ImageView) v.findViewById(R.id.icon);
            v.setTag(holder);
        } else
            holder = (ViewHolder) v.getTag();
        if (DirectoryObject.getPeek().get(position) != null) {
            holder.objectName.setText(DirectoryObject.dirStack.peek().get(position).getName());
            if (DirectoryObject.dirStack.peek().get(position).isDir())
                holder.icon.setImageResource(R.drawable.foldericon);
            else
                holder.icon.setImageResource(R.drawable.fileicon);
        }
        return v;
    }
}
