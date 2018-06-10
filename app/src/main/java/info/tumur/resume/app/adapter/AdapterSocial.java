package info.tumur.resume.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.Social;
import info.tumur.resume.app.utils.Tools;


public class AdapterSocial extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx; // Context for UI
    private List<Social> social = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    // Constructor
    public AdapterSocial(Context ctx, List<Social> social) {
        this.ctx = ctx;
        this.social = social;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Setting data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Social s = social.get(position);
            vItem.social_btn_text.setText(s.social_btn_text);
            vItem.social_btn_des.setText(s.social_btn_des);
            Tools.displayImageThumbnail(ctx, vItem.social_btn_icon, Constant.getURLimg(s.social_btn_icon), 0.5f);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, s);
                    }
                }
            });
        }

    }

    public void insertData(List<Social> social) {
        this.social = social;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return social.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void resetListData(List<Social> items) {
        this.social = items;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Social obj);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView social_btn_text;
        public TextView social_btn_des;
        public ImageView social_btn_icon;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            social_btn_text = v.findViewById(R.id.btn_text);
            social_btn_des = v.findViewById(R.id.btn_des);
            social_btn_icon = v.findViewById(R.id.btn_icon);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }


}