package sgaw.com.myapplication.domain;

/**
 * Domain object representing response from fetching posts from server.
 */
public class PostListResponse {
    Post [] posts;
    
    public Post[] getPosts() {
        return posts;
    }
}
