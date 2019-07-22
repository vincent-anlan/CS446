package ca.uwaterloo.cs446.ezbill;

import java.io.Serializable;

public class Participant implements Serializable {

    private String id;
    private String name;
    private String photoUri;
    private String email;

    public Participant(String id, String name, String photoUri, String email) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
