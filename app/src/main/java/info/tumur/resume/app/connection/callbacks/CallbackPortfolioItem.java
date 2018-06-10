package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.PortfolioImage;
import info.tumur.resume.app.model.PortfolioItem;

public class CallbackPortfolioItem implements Serializable {

    public String status = "";
    public PortfolioItem portfolioItem = null;
    public List<PortfolioImage> portfolio_images = new ArrayList<>();

}
