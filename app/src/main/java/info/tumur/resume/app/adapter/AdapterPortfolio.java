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

    private Context ctx;
    private List<Portfolio> portfolio = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    // Constructor
    public AdapterPortfolio(Context context, List<Portfolio> portfolio) {
        this.portfolio = portfolio;
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

    // Setting data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Portfolio p = portfolio.get(position);
            view.portfolio_title.setText(p.portfolio_title);
            view.portfolio_brief.setText(p.portfolio_brief);
            switch (p.portfolio_type) {
                case "web":
                    view.portfolio_category.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_btn_web));
                    break;
                case "android":
                    view.portfolio_category.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_android));
                    break;
                default:
                    break;
            }
            Tools.displayImageThumbnail(ctx, view.portfolio_image, Constant.getURLimg(p.portfolio_image), 0.1f);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, portfolio.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return portfolio.size();
    }

    public void insertData(List<Portfolio> portfolio) {
        this.portfolio = portfolio;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.portfolio = new ArrayList<>();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Portfolio obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView portfolio_image;
        public ImageView portfolio_category;
        public TextView portfolio_title;
        public TextView portfolio_brief;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            portfolio_image = v.findViewById(R.id.portfolio_image);
            portfolio_title = v.findViewById(R.id.portfolio_title);
            portfolio_brief = v.findViewById(R.id.portfolio_brief);
            portfolio_category = v.findViewById(R.id.portfolio_category);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

}