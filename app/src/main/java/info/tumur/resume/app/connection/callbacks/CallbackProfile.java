package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.Profile;
import info.tumur.resume.app.model.Skills;
import info.tumur.resume.app.model.Social;

public class CallbackProfile implements Serializable {

    public String status = "";
    public Profile profile = null;
    public List<Skills> skills = new ArrayList<>();
    public List<Social> social = new ArrayList<>();

}
