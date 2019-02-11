package facebook;

import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class WallTest {

    @Test
    public void addPost() {

        //Given
        Picture testingAddingPicture = new Picture("TEST");
        Wall wall = new Wall((Collection<Post>) testingAddingPicture);


        //When
        boolean addingPicturePost = wall.addPost(testingAddingPicture);


        //Then
        assertTrue(addingPicturePost);


    }

    @Test
    public void removePost() {
    }

    @Test
    public void editPost() {
    }

    @Test
    public void show() {
        Picture testingAddingPicture = new Picture("TEST");
        Wall wall = new Wall((Collection<Post>) testingAddingPicture);

        //When
        boolean addingPicturePost = wall.addPost(testingAddingPicture);
        testingAddingPicture.show();
        //Then
        assertEquals("Optional[Picture{description='TEST'}]",testingAddingPicture.show());

    }
}