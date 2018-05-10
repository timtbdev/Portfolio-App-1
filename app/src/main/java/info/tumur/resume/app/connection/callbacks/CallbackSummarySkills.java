package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.Skills;

public class CallbackSummarySkills implements Serializable {

    public String status = "";
    public List<Skills> summary_skills = new ArrayList<>();

}
