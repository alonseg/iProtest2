package com.alonseg.iprotest.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.MyBaseObject;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.Objects.ViewHolder;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.Activities.SinglePostDialog;

import java.util.ArrayList;

/**
 * Created by Alon on 7/14/2015.
 */
public class PostAdapter extends BasePostAdapter {



    public final String TAG = "POST_ADPTR";
    public Context con;

    public PostAdapter(Context context, int resource, ArrayList<Post> psts) {
        super(context, resource);
        this.con = context;
        this.myBaseObjs = new ArrayList<MyBaseObject>(psts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.row_post, null);

            holder = initHolder(convertView);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Post post = (Post) myBaseObjs.get(position);

        setHolderValues(holder, post);

        handleRowClicked(convertView, post);

        boolean canDel = (post.getPublisher() != null &&
                post.getPublisher().equals(app.getUsername())) ||
                (ProtestActivity.getAdmin() != null &&
                        ProtestActivity.getAdmin().equals(post.getPublisher()));

        handleOptionsBtn(holder, con, canDel, position);

        TextView summery = (TextView) convertView.findViewById(R.id.postSum);
        summery.setTypeface(Consts.appFont);

        return convertView;
    }

    private void handleRowClicked(View convertView, final Post post) {
        final Context con = getContext();
        RelativeLayout postRow = (RelativeLayout) convertView.findViewById(R.id.rowPostRelView);
        postRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, SinglePostDialog.class);
                intent.putExtra(Consts.POST, post);

                con.startActivity(intent);
            }
        });
    }

    private void setHolderValues(ViewHolder holder, Post post) {
        if (post.hasImage()) {
            Bitmap bitmap = post.getThumb();
            holder.img.setImageBitmap(bitmap);
        }else
            holder.img.setImageBitmap(null);
        holder.publishDetails.setText(post.getPublisher() + " at "
                + Consts.getFormat().format(post.getPublishDate()));
        holder.summery.setText(post.getBodySummery());
        holder.summery.setTag(post);
    }

    @NonNull
    private ViewHolder initHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.publishDetails = (TextView) convertView.findViewById(R.id.postRowNameAndDate);
        holder.summery = (TextView) convertView.findViewById(R.id.postSum);
        holder.img = (ImageView) convertView.findViewById(R.id.postImg);
        holder.optionsBtn = (ImageButton) convertView.findViewById(R.id.postRowOption);
        convertView.setTag(holder);
        return holder;
    }
}
