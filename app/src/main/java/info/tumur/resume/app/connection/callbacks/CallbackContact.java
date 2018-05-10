package info.tumur.resume.app.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.tumur.resume.app.model.Contact;

public class CallbackContact implements Serializable {

    public String status = "";
    public List<Contact> contacts = new ArrayList<>();

}
