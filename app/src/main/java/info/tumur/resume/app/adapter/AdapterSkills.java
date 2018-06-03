package info.tumur.resume.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.model.Skills;


public class AdapterSkills extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private Context ctx;
    private List<Skills> items = new ArrayList<>();

    // Provide a suitable constructor
    public AdapterSkills(Context ctx, List<Skills> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skills, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skills_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Skills s = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            vItem.skill_name.setText(s.skill_name);
            vItem.skill_description.setText(s.skill_description);
        } else {
            SectionViewHolder view = (SectionViewHolder) holder;
            view.skill_section_name.setText(s.skill_category);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).section ? VIEW_SECTION : VIEW_ITEM;
    }

    public void setItems(int index, Skills skills) {
        items.add(index, skills);
        notifyDataSetChanged();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView skill_section_name;

        public SectionViewHolder(View v) {
            super(v);
            skill_section_name = v.findViewById(R.id.skill_section_name);
        }
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView skill_name;
        public TextView skill_description;
        public LinearLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            skill_name = v.findViewById(R.id.skill_name);
            skill_description = v.findViewById(R.id.skill_description);
        }
    }


}