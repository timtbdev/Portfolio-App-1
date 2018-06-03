package info.tumur.resume.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.Portfolio;
import info.tumur.resume.app.utils.Tools;

public class AdapterPortfolio extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Portfolio> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    // Provide a suitable constructor
    public AdapterPortfolio(Context context, RecyclerView view, List<Portfolio> items) {
        this.items = items;
        ctx = context;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Portfolio p = items.get(position);
            view.title.setText(p.title);
            view.brief.setText(p.brief);
            switch (p.type) {
                case "web":
                    view.category.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_btn_web));
                    break;
                case "android":
                    view.category.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_android));
                    break;
                default:
                    break;
            }
            Tools.displayImageThumbnail(ctx, view.image_bg, Constant.getURLimg(p.image_bg), 0.1f);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void insertData(List<Portfolio> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Portfolio obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_bg;
        public ImageView category;
        public TextView title;
        public TextView brief;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image_bg = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            brief = v.findViewById(R.id.brief);
            category = v.findViewById(R.id.category);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

}