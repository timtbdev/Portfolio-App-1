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
import info.tumur.resume.app.model.Contact;
import info.tumur.resume.app.utils.Tools;


public class AdapterContact extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx; // Context for UI
    private List<Contact> contact = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    // Constructor
    public AdapterContact(Context ctx, List<Contact> contact) {
        this.ctx = ctx;
        this.contact = contact;
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
            final Contact c = contact.get(position);
            vItem.contact_btn_text.setText(c.contact_btn_text);
            vItem.contact_btn_des.setText(c.contact_btn_des);
            Tools.displayImageThumbnail(ctx, vItem.contact_btn_icon, Constant.getURLimg(c.contact_btn_icon), 0.5f);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, c);
                    }
                }
            });
        }

    }

    public void insertData(List<Contact> contact) {
        this.contact = contact;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Contact> items) {
        this.contact = items;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Contact obj);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contact_btn_text;
        public TextView contact_btn_des;
        public ImageView contact_btn_icon;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            contact_btn_text = v.findViewById(R.id.btn_text);
            contact_btn_des = v.findViewById(R.id.btn_des);
            contact_btn_icon = v.findViewById(R.id.btn_icon);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }


}