package facebook;

import javax.jws.soap.SOAPBinding;
import java.util.*;

public class Picture extends  Post{
    private static final String DESCRIPTION_TAG = "Picture Description: ";
    private static final String TAGGET_TAG = "Tagged : ";

    private String description;
    private final List<User> tags = new LinkedList<>();

    public Picture(String description) {
        this.description = description;
        setAccess(Access.FRIENDS);
    }

    public void addTag(User users){
        tags.add(users);
    }

    public boolean removeTag(User users){
        return tags.remove(users);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "description='" + description + '\'' +
                ", tags=" + tags +
                '}';
    }

    @Override
    public String show() {
        return DESCRIPTION_TAG  + description + SEPARATOR +
                TAGGET_TAG + tags.toString() + SEPARATOR +
                TAG_LIKES + getLikes();
    }

}

