package com.alonseg.iprotest.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.alonseg.iprotest.Objects.ViewHolder;
import com.alonseg.iprotest.R;
import com.parse.ParseException;
import com.parse.ParseFile;


import java.util.List;

/**
 * Created by Alon on 11/18/2015.
 */
public class PhotosAdapter extends ArrayAdapter {

    private List<Object> photosArr;
    private Context con;

    public PhotosAdapter(Context context, int resource, List<Object> potosArr) {
        super(context, resource);
        this.photosArr = potosArr;
        this.con = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_image, null);

            holder = initHolder(convertView);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        byte[] imageByteArr = new byte[0];
        try {
            imageByteArr = ((ParseFile) photosArr.get(position)).getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Bitmap b = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length);
        if (b != null) {
            holder.img.setImageBitmap(Bitmap.createScaledBitmap(b, 330, 330, false));
        }

        return convertView;
    }

    @NonNull
    private ViewHolder initHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.img = (ImageView) convertView.findViewById(R.id.simpleImageView);
        convertView.setTag(holder);
        return holder;
    }

    @Override
    public int getCount() {
        return this.photosArr != null ? this.photosArr.size() : 0;
    }
}
