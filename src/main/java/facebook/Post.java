package facebook;

import java.util.Map;
import java.util.Random;

public abstract class Post {

    static final String SEPARATOR = "; ";
    static final String TAG_LIKES = "Likes: ";

    private Access access = Access.values()[(int) (Math.random()*10)%3];
    private int likes;

    public Access getAccess() {
        return access;
    }

    public int getLikes() {
        return likes;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public int addLinkes(){
        return likes++;
    }

    public abstract String show();

}
