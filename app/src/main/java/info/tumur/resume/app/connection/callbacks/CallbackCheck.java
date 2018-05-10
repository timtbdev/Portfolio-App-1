package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;

import info.tumur.resume.app.model.Check;

public class CallbackCheck implements Serializable {
    public String status = "";
    public Check check = null;
}
