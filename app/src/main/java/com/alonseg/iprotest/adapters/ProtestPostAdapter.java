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
import android.widget.Toast;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.SinglePostDialog;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.MyBaseObject;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.Objects.ViewHolder;
import com.alonseg.iprotest.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alon on 7/14/2015.
 */
public class ProtestPostAdapter extends BasePostAdapter {


    public static final String TAG = "PROTEST_POST_ADPTR";
    private Map<String, Protest> protests;
    public Context con;

    public ProtestPostAdapter(Context context, int resource, ArrayList<Post> posts, Map<String, Protest> protests) {
        super(context, resource);
        this.con = context;
        this.myBaseObjs = new ArrayList<MyBaseObject>(posts);
        this.protests = protests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.row_protest_post, null);

            holder = initHolder(convertView);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Post post = (Post) myBaseObjs.get(position);

        setHolderValues(holder, post);

        handleTtlAndSummary(convertView, holder, post);

        boolean canDel = (post.getPublisher() != null &&
                post.getPublisher().equals(app.getUsername()));

        handleOptionsBtn(holder, con, canDel, position);

        return convertView;
    }

    private void handleTtlAndSummary(View convertView, ViewHolder holder, final Post post) {


        holder.summery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, SinglePostDialog.class);
                intent.putExtra(Consts.POST, post);

                con.startActivity(intent);
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, SinglePostDialog.class);
                intent.putExtra(Consts.POST, post);

                con.startActivity(intent);
            }
        });

        holder.ttl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, ProtestActivity.class);
                intent.putExtra(Consts.P_ID, post.getProtestID());
                con.startActivity(intent);
            }
        });


        TextView ttl = (TextView) convertView.findViewById(R.id.postProtestName);
        ttl.setTypeface(Consts.appFont);
        TextView summery = (TextView) convertView.findViewById(R.id.postSum);
        summery.setTypeface(Consts.appFont);
    }

    private void setHolderValues(final ViewHolder holder, Post post) {
        holder.ttl.setText(post.getProtestName());
        String nameAndDate = post.getPublisher() + " at "
                + Consts.getFormat().format(post.getPublishDate());
        holder.publishDetails.setText(nameAndDate);

        if (post.hasImage()) {
            Bitmap bitmap = post.getThumb();
            holder.img.setImageBitmap(bitmap);
        }else
            holder.img.setImageBitmap(null);

        holder.summery.setText(post.getBodySummery());
        holder.summery.setTag(post);

        int count = protests.get(post.getProtestName()).getCount();
        holder.postFollowCount.setText(String.valueOf(count));

        if (count > 1) {
            if (count > Consts.LOTS) {
                holder.postFollowImage.setImageResource(R.drawable.stick_man_lots);
            } else
                holder.postFollowImage.setImageResource(R.drawable.stick_man_few);
        }else
            holder.postFollowImage.setImageResource(R.drawable.stick_man_one);

        holder.postFollowCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(con, "Already " + holder.postFollowCount.getText().toString() +
                        " are following!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.postFollowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(con, "Already " + holder.postFollowCount.getText().toString() +
                        " are following!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private ViewHolder initHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.publishDetails = (TextView) convertView.findViewById(R.id.protestPostRowNameAndDate);
        holder.ttl = (TextView) convertView.findViewById(R.id.postProtestName);
        holder.summery = (TextView) convertView.findViewById(R.id.postSum);
        holder.img = (ImageView) convertView.findViewById(R.id.postImg);
        holder.optionsBtn = (ImageButton) convertView.findViewById(R.id.protestPostRowOption);
        holder.postFollowCount = (TextView) convertView.findViewById(R.id.postFollowCount);
        holder.postFollowCount.setTypeface(Consts.appFont);
        holder.postFollowImage = (ImageView) convertView.findViewById(R.id.postFollowImage);
        holder.postProtestTtlBar = (RelativeLayout) convertView.findViewById(R.id.postProtestTtlBar);

        convertView.setTag(holder);
        return holder;
    }

    @NonNull
    public static Map<String, Protest> getProtestsForPosts(ArrayList<Post> postArrList) {
        ArrayList<String> protestsNames = new ArrayList<>();
        final Map<String, Protest> protests = new HashMap<>();

        for (final Post post: postArrList) {
            if (!protestsNames.contains(post.getProtestName())) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
                ParseObject object = null;
                try {
                    object = query.get(post.getProtestID());
                } catch (ParseException e) {
                    Log.d(TAG, "exception while getting the protest" + e.getMessage());
                }
                protests.put(post.getProtestName(), new Protest(object));
                protestsNames.add(post.getProtestName());
            }
        }
        return protests;
    }
}
