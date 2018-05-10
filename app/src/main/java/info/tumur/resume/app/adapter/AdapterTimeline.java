package info.tumur.resume.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.Timeline;
import info.tumur.resume.app.utils.Tools;

public class AdapterTimeline extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Timeline> items = new ArrayList<>();
    private boolean loading;
    private Context ctx;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Timeline obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterTimeline(Context context, RecyclerView view, List<Timeline> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView timeline_photo;
        public TextView timeline_institution;
        public TextView timeline_occupation;
        public TextView timeline_main_content;
        public TextView timeline_date;
        public TextView timeline_location;
        public TextView timeline_btn_web_text;
        public TextView timeline_btn_certificate_text;
        public LinearLayout timeline_btn_web;
        public LinearLayout timeline_btn_certificate;

        public OriginalViewHolder(View v) {
            super(v);
            timeline_photo = (ImageView) v.findViewById(R.id.timeline_photo);
            timeline_institution= (TextView) v.findViewById(R.id.timeline_institution);
            timeline_occupation= (TextView) v.findViewById(R.id.timeline_occupation);
            timeline_main_content = (TextView) v.findViewById(R.id.timeline_main_content);
            timeline_date = (TextView) v.findViewById(R.id.timeline_date);
            timeline_location = (TextView) v.findViewById(R.id.timeline_location);
            timeline_btn_web_text = (TextView) v.findViewById(R.id.timeline_btn_web_text);
            timeline_btn_certificate_text = (TextView) v.findViewById(R.id.timeline_btn_certificate_text);
            timeline_btn_web = (LinearLayout) v.findViewById(R.id.timeline_btn_web);
            timeline_btn_certificate = (LinearLayout) v.findViewById(R.id.timeline_btn_certificate);
        }
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Timeline t = items.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            Tools.displayImageThumbnail(ctx, vItem.timeline_photo, Constant.getURLimg(t.timeline_photo), 0.5f);
            vItem.timeline_institution.setText(t.timeline_institution);
            vItem.timeline_occupation.setText(t.timeline_occupation);
            vItem.timeline_main_content.setText(Html.fromHtml(t.timeline_main_content));
            vItem.timeline_date.setText(t.timeline_date);
            vItem.timeline_location.setText(t.timeline_location);
            vItem.timeline_btn_web_text.setText(t.timeline_btn_web_text);
            vItem.timeline_btn_certificate_text.setText(t.timeline_btn_certificate_text);

            vItem.timeline_btn_web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        Tools.directUrl(ctx, t.timeline_btn_web.toString());
                    }
                }
            });

            vItem.timeline_btn_certificate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        Tools.directUrl(ctx, Constant.getURLimg(t.timeline_btn_certificate.toString()));
                    }
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void insertData(List<Timeline> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

}