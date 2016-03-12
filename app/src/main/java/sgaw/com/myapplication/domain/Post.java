package sgaw.com.myapplication.domain;

import android.support.annotation.Nullable;

/**
 * Domain object representing a single article.
 */
public class Post {
    int id;
    String title;
    String content;
    String date;
    @Nullable String exerpt;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}
