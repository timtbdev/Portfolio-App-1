package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.Portfolio;

public class CallbackPortfolio implements Serializable {

    public String status = "";
    public List<Portfolio> portfolio = new ArrayList<>();

}
