package facebook;

import javax.jws.soap.SOAPBinding;
import java.util.*;

public class CompileFacebook {
    /**
     * Social network application.
     * <p>
     * Future improvements:
     * - messages between users
     * - timestamp posts
     * - wall using hash set container for posts
     * - use picture tags
     * - refactor User class to less than 100 lines: separate into wall, friends and messages controllers.
     * - test's for all classes
     */

    public static void main(String[] args) {
        final User john = new User("John");
        final User william = new User("William");
        final User mary = new User("Mary");

        createRelations(john, william, mary);
        regularDayPosts(john, william, mary);
        showWall(Arrays.asList(john, william, mary));
    }


    private static void showWall(List<User> users){
        users.forEach(CompileFacebook::showWallOfUsers);
    }

    private static void showWallOfUsers(User user){
        System.out.println(user.showWall(user));
        user.getAddedFriends().forEach(addedFriend -> System.out.println(user.showWall(addedFriend)));
        user.getFriendsRequest().forEach(friendRequest-> System.out.println(user.showWall(friendRequest)));
        user.getFriends().forEach(friend -> System.out.println(user.showWall(friend)));
    }

    public static void createRelations(User user1, User user2, User user3){
        user1.addFriend(user2);
        user1.addFriend(user3);
        user1.acceptedFriendRequest(user1);
        user2.addFriend(user3);
        user3.acceptedFriendRequest(user2);
        user3.acceptedFriendRequest(user1);
    }

    private static void regularDayPosts(User user1, User user2, User user3) {
        final Post post11 = new Picture("Holiday picture 2019.");
        final Post post12 = new Picture("After work hangout.");
        final Post post13 = new Text("Party mood.");
        user1.post(post11);
        user1.post(post12);
        user1.post(post13);

        final Post post21 = new Text("Reading The Subtle Art of Not Giving a F*ck.");
        user2.post(post21);

        final Post post31 = new Picture("Morning face!");
        user3.post(post31);

        user1.like(user2, post21);
        user1.like(user2, post31); // Invalid
        user2.like(user3, post31); // Insufficient access: not friends
        user3.like(user2, post21); // Valid even if not friends: public post
    }
}