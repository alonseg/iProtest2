package com.alonseg.iprotest.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.MyBaseObject;
import com.alonseg.iprotest.Objects.Step;
import com.alonseg.iprotest.Objects.ViewHolder;
import com.alonseg.iprotest.R;

import java.util.ArrayList;

/**
 * Created by Alon on 7/14/2015.
 */
public class StepsAdapter extends MyBaseAdapter {

    public final String TAG = "STEP_ADPTR";
    public Context con;

    public StepsAdapter(Context context, int resource, ArrayList<Step> steps) {
        super(context, resource, Consts.STEP, Consts.P_STEP);
        this.con = context;
        this.myBaseObjs = new ArrayList<MyBaseObject>(steps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.row_step, null);

            holder = initHolder(convertView);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Step step = (Step) myBaseObjs.get(position);

        setHolderValues(holder, step);

        boolean canDel = (step.getPublisher() != null &&
                step.getPublisher().equals(app.getUsername())) ||
                (ProtestActivity.getAdmin() != null &&
                        ProtestActivity.getAdmin().equals(step.getPublisher()));

        handleOptionsBtn(holder, con, canDel, position);

        holder.summery.setTypeface(Consts.appFont);

        return convertView;
    }

    private void setHolderValues(ViewHolder holder, Step step) {
        holder.publishDetails.setText(step.getPublisher() + " at "
                + Consts.getFormat().format(step.getDate()));
        holder.summery.setText(step.getMsg());
        holder.summery.setTag(step);
    }

    @NonNull
    private ViewHolder initHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.publishDetails = (TextView) convertView.findViewById(R.id.stepNameAndDate);
        holder.summery = (TextView) convertView.findViewById(R.id.stepText);
        holder.optionsBtn = (ImageButton) convertView.findViewById(R.id.stepRowOptions);
        convertView.setTag(holder);
        return holder;
    }


    @Override
    public Step getItem(int position){
        if (position < this.myBaseObjs.size()){
            return (Step)this.myBaseObjs.get(position);
        }
        return null;
    }


}
