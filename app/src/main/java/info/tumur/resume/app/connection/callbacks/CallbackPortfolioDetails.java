package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;

import info.tumur.resume.app.model.PortfolioDetails;

public class CallbackPortfolioDetails implements Serializable {

    public String status = "";
    public PortfolioDetails portfolio = null;

}
