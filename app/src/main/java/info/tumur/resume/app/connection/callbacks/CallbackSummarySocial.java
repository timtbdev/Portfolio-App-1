package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.Social;

public class CallbackSummarySocial implements Serializable {

    public String status = "";
    public List<Social> summary_social = new ArrayList<>();

}
