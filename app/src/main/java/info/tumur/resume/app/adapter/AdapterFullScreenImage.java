package info.tumur.resume.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import info.tumur.resume.app.R;
import info.tumur.resume.app.utils.Tools;
import info.tumur.resume.app.widget.TouchImageView;

public class AdapterFullScreenImage extends PagerAdapter {

    private Activity activityFullScreenImage;
    private List<String> imagePaths;
    private LayoutInflater inflater;

    // Constructor
    public AdapterFullScreenImage(Activity activity, List<String> imagePaths) {
        this.activityFullScreenImage = activity;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this.imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        inflater = (LayoutInflater) activityFullScreenImage.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.item_fullscreen_image, container, false);

        imgDisplay = viewLayout.findViewById(R.id.imgDisplay);
        Tools.displayImageOriginal(activityFullScreenImage, imgDisplay, imagePaths.get(position));
        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }

}
