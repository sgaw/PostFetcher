package sgaw.com.myapplication;

import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import sgaw.com.myapplication.domain.Post;
import sgaw.com.myapplication.domain.PostListResponse;

/**
 *
 */
public class PostManager {
    public static final int TITLE = 0;
    public static final int DATE = 1;

    private static final String BASE_URL = "http://www.washingtonpost.com/wp-srv/simulation/";
    private static final PostManager sInstance = new PostManager();

    private ArrayList<Post> mPosts;
    private SimpleDateFormat mDateFormat;

    interface Service {
        @GET("simulation_test.json")
        Call<PostListResponse> get();
    }

    private PostManager() {
        // disallow instantiation in favor of singleton access.
        mPosts = new ArrayList<>();
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    public static PostManager getInstance() {
        return sInstance; // Could do singleton holder lazy loading instead
    }

    public void init(final PostListActivity.SimpleItemRecyclerViewAdapter adapter) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Service service = retrofit.create(Service.class);
        service.get().enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Response<PostListResponse> response, Retrofit retrofit) {
                setPosts(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PostManager", "Errors with fetching from site.", t);
            }
        });

    }

    public Post getPost(int id) {
        // Hashmap would be more efficient in a real system
        for (Post post: mPosts) {
            if (id == post.getId()) {
                return post;
            }
        }
        throw new IllegalArgumentException("No post found with id: " + id);
    }

    public Post get(int position) {
        return mPosts.get(position);
    }

    public int size() {
        return mPosts.size();
    }

    public void resort(int type) {
        if (type == DATE) {
            Collections.sort(mPosts, new PostDateComparator());
        } else if (type == TITLE) {
            Collections.sort(mPosts, new PostTitleComparator());
        }
    }

    private class PostDateComparator implements java.util.Comparator<Post> {
        @Override
        public int compare(Post lhs, Post rhs) {
            try {
                Date left = mDateFormat.parse(lhs.getDate());
                Date right = mDateFormat.parse(rhs.getDate());
                return left.compareTo(right);
            } catch (ParseException e) {
                Log.e("PostManager", "Unable to pare dates in posts: " + lhs.getDate() + ", " + rhs.getDate());
            }
            return 0;
        }
    }

    private class PostTitleComparator implements java.util.Comparator<Post> {
        @Override
        public int compare(Post lhs, Post rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }

    private void setPosts(PostListResponse response) {
        for (Post post: response.getPosts()) {
            mPosts.add(post);
        }
    }
}
