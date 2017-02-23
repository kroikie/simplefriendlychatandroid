package sample.firebase.google.com.simplefriendlypix.model;

import com.google.firebase.database.PropertyName;

/**
 * Created by arthurthompson on 2/23/17.
 */

public class Post {

    public String author;
    @PropertyName("image_url")
    public String imageUrl;
    public long timePosted;
    public String userId;

    public Post() {

    }
}
