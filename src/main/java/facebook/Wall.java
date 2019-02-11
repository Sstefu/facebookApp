package facebook;

import javafx.geometry.Pos;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Wall {

    private final List<Post> posts;
    private Post post;

    public Wall(Collection<Post> posts) {
        this.posts = new LinkedList<>(posts);
    }

    public boolean addPost(Post post) {
        return posts.add(post);
    }

    public boolean removePost(Post post) {
        return posts.remove(post);
    }

    public boolean editPost(Post newPost, Post oldPost) {
        final int oldPostIndex = posts.indexOf(oldPost);
        if (oldPostIndex < 0) {
            return false;
        }
        posts.set(oldPostIndex, newPost);
        return true;
    }

    Post getLastPost(List<Access> accesses) {
        return IntStream.range(posts.size() - 1, 0)
                .mapToObj(posts::get)
                .filter(post -> accesses.contains(post.getAccess()))
                .findFirst().orElse(null);
    }

    List<Post> getPosts(List<Access> accesses) {
        Predicate<Post> accessIsGranted = post1 -> accesses.contains(post.getAccess());
        return IntStream.range(posts.size() - 1, 0)
                .mapToObj(posts::get)
                .filter(accessIsGranted)
                .collect(Collectors.toList());
    }

    List<Post> getPosts(){
        return new LinkedList<>(posts);
    }

    int like(Post post) {
        if (!posts.contains(post)) {
            return -1;
        }
        return posts.get(posts.indexOf(post)).addLinkes();
    }

    @Override
    public String toString() {
        return "Wall{" +
                "posts=" + posts +
                ", post=" + post +
                '}';
    }
}
