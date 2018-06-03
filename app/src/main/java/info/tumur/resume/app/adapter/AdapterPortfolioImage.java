package info.tumur.resume.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.data.Constant;
import info.tumur.resume.app.model.PortfolioImage;
import info.tumur.resume.app.utils.Tools;

public class AdapterPortfolioImage extends PagerAdapter {

    private Activity act;
    private List<PortfolioImage> items;

    private OnItemClickListener onItemClickListener;

    // Provide a suitable constructor
    public AdapterPortfolioImage(Activity activity, List<PortfolioImage> items) {
        this.act = activity;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public PortfolioImage getItem(int pos) {
        return items.get(pos);
    }

    public void setItems(List<PortfolioImage> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // Replace the contents of a view
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PortfolioImage o = items.get(position);
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_portfolio_image, container, false);
        ImageView image = v.findViewById(R.id.image);
        MaterialRippleLayout lyt_parent = v.findViewById(R.id.lyt_parent);
        Tools.displayImageOriginal(act, image, Constant.getURLimg(o.name));
        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, o, position);
                }
            }
        });

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }

    public interface OnItemClickListener {
        void onItemClick(View view, PortfolioImage obj, int position);
    }

}
