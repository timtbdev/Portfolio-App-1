package info.tumur.resume.app.model;

import java.io.Serializable;

public class Skills implements Serializable {
    public String skill_name;
    public String skill_description;
    public String skill_category;
    public boolean section = false; // section for category

    public Skills() {
    }

    public Skills(String skill_category, boolean section) {
        this.skill_category = skill_category;
        this.section = section;
    }

}
