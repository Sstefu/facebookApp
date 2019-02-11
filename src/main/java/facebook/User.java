package facebook;

import javafx.geometry.Pos;
import sun.rmi.server.UnicastServerRef;

import javax.jws.soap.SOAPBinding;
import java.util.*;

/**
 * User class representing a user's state and options.
 * <p>
 * Note the access types of the methods: for user options the access public, for internal mechanisms such as receiving
 * friend requests the access is package private.
 */

public class User {

    private static final String TAG_SEPARATOR = ";";
    private static final String TAG_POST = "Post: ";
    private static final String TAG_USER = "User: ";
    private static final String POST_NOT_FOUND_MESSAGE = "Like not added: Post not found";
    private static final String LIKE_ADDED_MESSAGE = "Like added, total likes: ";
    private static final String POST_FOUND_MESSAGE = "Like added: Post found";
    private static final String POST_PRIVATE_MESSAGE = "Like not added: Post is private";
    private static final String USER_POST_DESCRIPTION_TAG = TAG_POST + "%s" + TAG_SEPARATOR + TAG_USER + "%s";
    private static final String FRIEND_ONLY_MESSAGE = "Like not added: Post is only friends only";

    private String name;
    private final Set<User> friends;
    private final Set<User> addedFriends;
    private final Set<User> friendsRequest;
    private final Wall wall;

    public User(String name) {
        this(name, new HashSet<>(), new HashSet<>(), new HashSet<>(), new Wall(new LinkedList<>()));
    }

    public User(String name, Set<User> friends, Set<User> addedFriends, Set<User> friendsRequest, Wall wall) {
        this.name = name;
        this.friends = friends;
        this.addedFriends = addedFriends;
        this.friendsRequest = friendsRequest;
        this.wall = wall;
    }

    public boolean post(Post post) {
        return wall.addPost(post);
    }

    /**
     * Like a post of a user. Can be also a self-like.
     * Cannot like other users private posts or friends access posts if not friends.
     *
     * @param user the owner of the post.
     * @param post the liked post
     * @return all likes of the given post after addition, or -1 if the like could not be added.
     */

    public int like(User user, Post post) {
        final String postDescription = String.format(USER_POST_DESCRIPTION_TAG, post, user);
        if (user == this) {
            return like(post);
        }
        if (post.getAccess() == Access.PRIVATE) {
            System.out.println(POST_FOUND_MESSAGE + postDescription);
            return -1;
        }
        if (post.getAccess() == Access.FRIENDS) {
            if (!friends.contains(user)) {
                System.out.println(FRIEND_ONLY_MESSAGE + postDescription);
                return -1;
            }
            return user.like(post);
        }
        return user.like(post);
    }

    public Set<User> getFriends() {
        return new HashSet<>(friends);
    }

    public Set<User> getAddedFriends() {
        return new HashSet<>(addedFriends);
    }

    public Set<User> getFriendsRequest() {
        return new HashSet<>(friendsRequest);
    }

    /**
     * Show the wall of a given user. This depends on the access rights based on the friends relationship status.
     *
     * @param user the user from which to query the wall of.
     * @return the wall posts in string format.
     */

    public String showWall(User user) {
        System.out.println(name + " showing wall of " + user.getName() + " : ");
        final StringBuilder stringBuilder = new StringBuilder();
        user.getPostForUser(this).forEach(post -> stringBuilder.append(post.show()));
        return stringBuilder.toString();
    }

    public boolean addFriend(User user) {
        return user.receivedFriendRequest(this) && user.friendRequestAccepted(this);
    }

    public boolean acceptedFriendRequest(User user) {
        if (!friendsRequest.contains(user)) {
            return false;
        }
        friendsRequest.remove(user);
        return friends.add(user) && user.friendRequestAccepted(this);
    }

    //TO DO

    /**
     * Shows current user's wall by alternating own posts with friends and followed users posts.
     *
     * @return the wall represented in text form.
     */

    public String show() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator<User> friendsIterator = getFollowedUser().iterator();
        wall.getPosts()
                .forEach(post -> alternatePostWithTheLastPostOfFollowedUser(post, friendsIterator, stringBuilder));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }


    private void alternatePostWithTheLastPostOfFollowedUser(Post post, Iterator<User> friendsIterator, StringBuilder stringBuilder) {
        stringBuilder.append(name)
                .append(" : ")
                .append(post.show())
                .append(System.lineSeparator());

        if (!friendsIterator.hasNext()) {
            return;
        }

        final User followedUser = friendsIterator.next();
        final Post lastPostForUser = followedUser.getLastPostForUser(this);

        if (lastPostForUser == null) {
            return;
        }

        stringBuilder.append(followedUser.getName())
                .append(": ")
                .append(lastPostForUser.show())
                .append(System.lineSeparator());
    }




    /* ************************************************************************
     *  Internal methods to provide inter-user mechanics.
     *  ************************************************************************ */

    private String getName() {
        return name;
    }


    /**
     * Add like for the given post. Can be self-called or called from other users.
     *
     * @param post the liked post.
     * @return the number of likes or -1 if the like could not be added.
     */

    private int like(Post post) {
        final String postDescription = String.format(USER_POST_DESCRIPTION_TAG, post, this);
        final int likes = wall.like(post);
        if (likes == -1) {
            System.out.println(POST_NOT_FOUND_MESSAGE + postDescription);
            return likes;
        }
        System.out.println(LIKE_ADDED_MESSAGE + likes + TAG_SEPARATOR + postDescription);
        return likes;
    }

    /**
     * Provide own wall posts to a user depending on the access rights of the requester.
     *
     * @param user initiator of the request
     * @return the wall posts with the correct access rights.
     */

    private List<Post> getPostForUser(User user) {
        final List<Access> accesses = getAccessesForUser(user);
        return wall.getPosts(accesses);
    }

    private List<Access> getAccessesForUser(User user) {
        final List<Access> accesses = new ArrayList<>(2);
        accesses.add(Access.PUBLIC);
        if (friends.contains(user)) {
            accesses.add(Access.FRIENDS);
        }
        return accesses;
    }

    /**
     * Get last post request from a specific user. If the user is a friend, he can access also the friend access posts.
     *
     * @param user the user who initiated the request.
     * @return the last post of the current user.
     */

    private Post getLastPostForUser(User user) {
        final List<Access> accesses = getAccessesForUser(user);
        return wall.getLastPost(accesses);
    }

    /**
     * Gets all followed users: friends and added friends.
     *
     * @return the connected users.
     */

    private List<User> getFollowedUser() {
        final List<User> users = new ArrayList<>(friends);
        users.addAll(addedFriends);
        return users;
    }

    /**
     * Listener for friend requests.
     *
     * @param user the user who requested friendship.
     * @return true if the request is received successfully.
     */

    private boolean receivedFriendRequest(User user) {
        return friendsRequest.add(user);
    }

    /**
     * Callback for when a user accepts the friend request of the current user.
     *
     * @param user the user who accepted the friend request.
     * @return true if user is added successfully to the friends list.
     */

    private boolean friendRequestAccepted(User user) {
        if (!friends.contains(user)) {
            return false;
        }
        friendsRequest.remove(user);
        friends.add(user);
        return true;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", friends=" + friends +
                ", wall=" + wall +
                '}';
    }
}
