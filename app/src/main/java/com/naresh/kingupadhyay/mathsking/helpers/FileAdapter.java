package com.naresh.kingupadhyay.mathsking.helpers;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.network.FileBeen;

import java.util.ArrayList;

public class FileAdapter extends ArrayAdapter<FileBeen> {
    Context cxt;
    int res;
    ArrayList<FileBeen> list;

    public FileAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<FileBeen> objects) {
        super(context, resource, objects);

        cxt = context;
        res = resource;
        list = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Initializing view which will point to layout file list_item
        View view = LayoutInflater.from(cxt).inflate(res, parent, false);

        //Text view showing pdf file name
        TextView txtView = (TextView)view.findViewById(R.id.txtFileName);

        //setting the file name
        txtView.setText(list.get(position).getFileName());
        return view;
    }
}
