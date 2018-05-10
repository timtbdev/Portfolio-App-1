package info.tumur.resume.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.Social;
import info.tumur.resume.app.utils.Tools;


public class AdapterSocial extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Social> items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Social obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView btn_text;
        public TextView btn_des;
        public ImageView btn_icon;
        public LinearLayout lyt_color;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            btn_text = (TextView) v.findViewById(R.id.btn_text);
            btn_des = (TextView) v.findViewById(R.id.btn_des);
            btn_icon = (ImageView) v.findViewById(R.id.btn_icon);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterSocial(Context ctx, List<Social> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Social s = items.get(position);
            vItem.btn_text.setText(s.btn_text);
            vItem.btn_des.setText(s.btn_des);
            Tools.displayImageThumbnail(ctx, vItem.btn_icon, Constant.getURLimg(s.btn_icon), 0.5f);

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

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Social> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}