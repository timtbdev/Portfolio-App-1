package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.Timeline;

public class CallbackTimeline implements Serializable {

    public String status = "";
    public List<Timeline> timeline = new ArrayList<>();

}
