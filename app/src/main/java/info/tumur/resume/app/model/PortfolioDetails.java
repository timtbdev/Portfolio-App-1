package info.tumur.resume.app.model;

import java.util.ArrayList;
import java.util.List;

public class PortfolioDetails {

    public int id;
    public String title;
    public String brief;
    public String type;
    public Long date;
    public String description;
    public String image_bg;
    public String features;
    public String btn_title;
    public String btn_url;

    public List<PortfolioImage> portfolio_images = new ArrayList<>();

    public PortfolioDetails() {
    }

}
