package info.tumur.resume.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Check implements Serializable {

    public Boolean active;
    public Double tax;
    public String currency;
    public List<String> shipping = new ArrayList<>();

}
