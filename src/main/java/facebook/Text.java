package facebook;

public class Text extends Post {
    private static final String TAG_TEXT_MESSAGE = "Text message: ";

    private String message;

    public Text(String mmessage) {
        this.message = mmessage;
        setAccess(Access.PUBLIC);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String show() {
        return TAG_TEXT_MESSAGE + message + SEPARATOR + TAG_LIKES + getLikes();
    }

    @Override
    public String toString() {
        return "Text{" +
                "message='" + message + '\'' +
                '}';
    }
}
