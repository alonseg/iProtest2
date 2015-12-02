package com.alonseg.iprotest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.MyBaseObject;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.Objects.ViewHolder;
import com.alonseg.iprotest.R;

import java.util.ArrayList;

/**
 * Created by Alon on 7/14/2015.
 */
public class ProtestsAdapter extends MyBaseAdapter{


    Context con;

    public ProtestsAdapter(Context context, int resource, ArrayList<Protest> manageProtestList) {
        super(context, resource, Consts.PROTEST, "none");
        this.con = context;
        this.myBaseObjs = new ArrayList<MyBaseObject>(manageProtestList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.row_protest, null);

            holder = new ViewHolder();
            initHolderViews(convertView, holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Protest protest = (Protest) myBaseObjs.get(position);

        initHolderValues(holder, protest);

        final Context con = getContext();

        initCounter(holder, protest, con);

        handleTtl(convertView, holder, protest, con);

        handleOptionsBtn(holder, con, false, position);
        TextView desc = (TextView) convertView.findViewById(R.id.protestRowSummary);
        desc.setTypeface(Consts.appFont);

        return convertView;
    }

    private void handleTtl(View convertView, ViewHolder holder, final Protest protest, final Context con) {
        holder.ttl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProtestActivity.class);
                intent.putExtra(Consts.PROTEST, protest);
                con.startActivity(intent);

            }
        });
        TextView ttl = (TextView) convertView.findViewById(R.id.protestRowName);
        ttl.setTypeface(Consts.appFont);
    }

    private void initCounter(ViewHolder holder, Protest protest, final Context con) {
        int count = protest.getCount();
        holder.postFollowCount.setText(String.valueOf(count));

        if (count > 1) {
            if (count > Consts.LOTS) {
                holder.postFollowImage.setImageResource(R.drawable.stick_man_lots);
            } else
                holder.postFollowImage.setImageResource(R.drawable.stick_man_few);
        }else
            holder.postFollowImage.setImageResource(R.drawable.stick_man_one);

        final ViewHolder finalHolder = holder;
        holder.postFollowCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(con, "Already " + finalHolder.postFollowCount.getText().toString() +
                        " are following!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.postFollowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(con, "Already " + finalHolder.postFollowCount.getText().toString() +
                        " are following!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initHolderValues(ViewHolder holder, Protest protest) {
        holder.ttl.setText(protest.getName());
        holder.img.setImageDrawable(protest.getImage());
        holder.summery.setText(protest.getDescriptionSummary());
        holder.summery.setTag(protest);
    }

    private void initHolderViews(View convertView, ViewHolder holder) {
        holder.ttl = (TextView) convertView.findViewById(R.id.protestRowName);
        holder.summery = (TextView) convertView.findViewById(R.id.protestRowSummary);
        holder.img = (ImageView) convertView.findViewById(R.id.protestRowImg);
        holder.postFollowCount = (TextView) convertView.findViewById(R.id.postFollowCount);
        holder.postFollowImage = (ImageView) convertView.findViewById(R.id.postFollowImage);
        holder.postProtestTtlBar = (RelativeLayout) convertView.findViewById(R.id.protestTtlBar);
        holder.optionsBtn = (ImageButton) convertView.findViewById(R.id.protestRowOption);

        convertView.setTag(holder);
    }


    @Override
    public Protest getItem(int position){
        if (position < this.myBaseObjs.size()){
            return (Protest) this.myBaseObjs.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return this.myBaseObjs.size();
    }

}
