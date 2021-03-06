package com.and.newsfeed.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.and.newsfeed.R;
import com.and.newsfeed.adapter.NewFeedArticleRecyclerAdapter;
import com.and.newsfeed.data.ArticleModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class BlogTransitionActivity extends AppCompatActivity {

    private static final String TAG = BlogTransitionActivity.class.getSimpleName();

    private ArrayList<ArticleModel> mBlogList;

    private static final int SHORT_ANIMATION_DURATION = 200;
    private static final int DELAY_ANIMATION_DURATION = 40;
    private static final int LIKE_BTN_ANIMATION_DURATION = 120;

    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private TextView titleTextView, subTitleTextView;
    private ImageButton btnLike;
    private View tintView;
    private View mBackgroundView, mContainer;
    private ScrollView mContentLayout;
    
    private boolean isOpen;
    private int position;
    private Rect startBounds;
    private Rect finalBounds;
    private String mHtmlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_transition);

        initViews();

        // Setup layout manager for mBlogList and column count
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Control orientation of the mBlogList
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        // Attach layout manager
        mRecyclerView.setLayoutManager(layoutManager);

        // Bind adapter to recycler
        mBlogList = new ArrayList<>();


        // Some of colors
        int[] color = {0xFFC2185B, 0xFFB3E5FC, 0xFFFFEB3B, 0xFFF8BBD0, 0xFFFF5252,
                0xFFE91E63, 0xFF448AFF, 0xFF00796B, 0xFFE91E63, 0xFFFF5252, 0xFFF8BBD0, 0xFF0288D1,};




        // Listen to the item touching
        mRecyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(
                        this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                if (!isOpen) {
                                    BlogTransitionActivity.this.position = position;
                                    openBlogInDetails(itemView);
                                }

                            }
                        }));



    }


    private void openBlogInDetails(View itemView) {

        // Now is open
        this.isOpen = true;

        //set Scroll View to the top
        mContentLayout.setScrollY(0);

        // Setting blog data to its views
       // mImageView.setImageResource(mBlogList.get(position).getImageRes());
       // tintView.setBackgroundColor(mBlogList.get(position).getBackGroundColor());
        titleTextView.setText(mBlogList.get(position).getTitle());
        Toast.makeText(this,""+mBlogList.get(position).getUrl(),Toast.LENGTH_SHORT).show();
       /* try {
            URL url = new URL(mBlogList.get(position).getUrl());

            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            for (String line; (line = reader.readLine()) != null; ) {
                builder.append(line.trim());
            }

            String start = "<div class=\"post-text\"><p>";
            String end = "</p>";
            String part = builder.substring(builder.indexOf(start) + start.length());
            String question = part.substring(0, part.indexOf(end));
            subTitleTextView.setText(mBlogList.get(position).getDescription()+question);
        }
        catch (Exception logOrIgnore) {

        }*/





        // Init Rect
        startBounds = new Rect();
        finalBounds = new Rect();

        // Setting the bound to startRect "startBounds"
        startBounds.left = itemView.getLeft();
        startBounds.right = itemView.getRight();
        startBounds.top = itemView.getTop();
        startBounds.bottom = itemView.getBottom();

        // Setting the bound to endRect "finalBounds"
        finalBounds.left = mContainer.getLeft();
        finalBounds.right = mContainer.getRight();
        finalBounds.top = mContainer.getTop();
        finalBounds.bottom = mContainer.getBottom();

        // Calculate scaling factor
        float startScaleX = (float) startBounds.width() / finalBounds.width();
        float startScaleY = (float) startBounds.height() / finalBounds.height();



        // Prepare the views before starting animation

        mContentLayout.setVisibility(View.VISIBLE);
        mBackgroundView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);

        mBackgroundView.setPivotX(0);
        mBackgroundView.setPivotY(0);
        mBackgroundView.setX(startBounds.left);
        mBackgroundView.setY(startBounds.top);
        mBackgroundView.setScaleX(startScaleX);
        mBackgroundView.setScaleY(startScaleY);

        btnLike.setScaleX(0.0f);
        btnLike.setScaleY(0.0f);

        // backgroundView Color Animator
        ObjectAnimator backgroundViewColor = ObjectAnimator.ofObject(
                mBackgroundView, "backgroundColor", new ArgbEvaluator(),Color.CYAN, Color.WHITE);

        // backgroundView X point Animator
        ObjectAnimator backgroundViewX = ObjectAnimator
                .ofFloat(mBackgroundView, View.X, finalBounds.left);

        // backgroundView Y point Animator
        ObjectAnimator backgroundViewY = ObjectAnimator
                .ofFloat(mBackgroundView, View.Y, finalBounds.top);

        // backgroundView width scaling Animator
        ObjectAnimator backgroundViewScaleX = ObjectAnimator
                .ofFloat(mBackgroundView, View.SCALE_X, 1f);

        // backgroundView height scaling Animator
        ObjectAnimator backgroundViewScaleY = ObjectAnimator
                .ofFloat(mBackgroundView, View.SCALE_Y, 1f);

        // Set of animators to play all of animators together.
        AnimatorSet backgroundViewAnimatorSet  = new AnimatorSet();
        backgroundViewAnimatorSet.setInterpolator(new AccelerateInterpolator());
        backgroundViewAnimatorSet.setDuration(SHORT_ANIMATION_DURATION);
        backgroundViewAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }
        });
        backgroundViewAnimatorSet.playTogether(backgroundViewColor, backgroundViewX,
                backgroundViewY, backgroundViewScaleX, backgroundViewScaleY);

        //Start animation
        backgroundViewAnimatorSet.start();


        // contentLayout Alpha Animator
        ObjectAnimator mContentLayoutAlpha = ObjectAnimator.ofFloat(mContentLayout, View.ALPHA, 1f);

        // imageView Alpha Animator
        ObjectAnimator mImageViewAlpha = ObjectAnimator.ofFloat(mImageView, View.ALPHA, 1f);

        // Set of animators to play all of animators together.
        AnimatorSet mContentLayoutAnimatorSet  = new AnimatorSet();
        mContentLayoutAnimatorSet.setInterpolator(new AccelerateInterpolator());
        mContentLayoutAnimatorSet.setStartDelay(DELAY_ANIMATION_DURATION);
        mContentLayoutAnimatorSet.setDuration(SHORT_ANIMATION_DURATION);
        mContentLayoutAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Animate LikeButton after contentLayout completely shown
                animateLikeButton();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }
        });
        mContentLayoutAnimatorSet.playTogether(mContentLayoutAlpha, mImageViewAlpha);

        //Start animation
        mContentLayoutAnimatorSet.start();

    }

    private void animateLikeButton() {
        btnLike.animate().setDuration(LIKE_BTN_ANIMATION_DURATION).scaleX(1f).scaleY(1f);
    }


    private void closeBlogDetails() {

        // Now is closed
        this.isOpen = false;

        // Calculate scaling factor
        float startScaleX = (float) startBounds.width() / finalBounds.width();
        float startScaleY = (float) startBounds.height() / finalBounds.height();

        // contentLayout Alpha Animator
        ObjectAnimator contentLayoutAlpha = ObjectAnimator.ofFloat(mContentLayout, View.ALPHA, 0f);

        // imageView Alpha Animator
        ObjectAnimator imageViewAlpha = ObjectAnimator.ofFloat(mImageView, View.ALPHA, 0f);

        // Set of animators to play all of animators together.
        AnimatorSet mContentLayoutAnimatorSet  = new AnimatorSet();
        mContentLayoutAnimatorSet.setInterpolator(new AccelerateInterpolator());
        mContentLayoutAnimatorSet.setStartDelay(0);
        mContentLayoutAnimatorSet.setDuration(SHORT_ANIMATION_DURATION);
        mContentLayoutAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mContentLayout.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mContentLayout.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
            }
        });
        mContentLayoutAnimatorSet.playTogether(contentLayoutAlpha, imageViewAlpha);

        //Start animation
        mContentLayoutAnimatorSet.start();


        // backgroundView Color Animator
        ObjectAnimator backgroundViewColor = ObjectAnimator.ofObject(
                mBackgroundView, "backgroundColor", new ArgbEvaluator(),
                Color.WHITE, Color.CYAN);

        // backgroundView X point Animator
        ObjectAnimator backgroundViewX = ObjectAnimator
                .ofFloat(mBackgroundView, View.X, startBounds.left);

        // backgroundView Y point Animator
        ObjectAnimator backgroundViewY = ObjectAnimator
                .ofFloat(mBackgroundView, View.Y, startBounds.top);

        // backgroundView width scaling Animator
        ObjectAnimator backgroundViewScaleX = ObjectAnimator
                .ofFloat(mBackgroundView, View.SCALE_X, startScaleX);

        // backgroundView height scaling Animator
        ObjectAnimator backgroundViewScaleY = ObjectAnimator
                .ofFloat(mBackgroundView, View.SCALE_Y, startScaleY);

        // Set of animators to play all of animators together.
        AnimatorSet backgroundViewAnimatorSet = new AnimatorSet();
        backgroundViewAnimatorSet.setInterpolator(new AccelerateInterpolator());
        backgroundViewAnimatorSet.setStartDelay(DELAY_ANIMATION_DURATION);
        backgroundViewAnimatorSet.setDuration(SHORT_ANIMATION_DURATION);
        backgroundViewAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBackgroundView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mBackgroundView.setVisibility(View.GONE);
            }
        });
        backgroundViewAnimatorSet.playTogether(backgroundViewColor, backgroundViewX, backgroundViewY,
                backgroundViewScaleX, backgroundViewScaleY);

        //Start animation
        backgroundViewAnimatorSet.start();

    }





    @Override
    public void onBackPressed() {

        if (this.isOpen) {
            closeBlogDetails();
        } else {
            super.onBackPressed();
        }

    }

    private void initViews() {
        mContainer = findViewById(R.id.container);
        mBackgroundView = findViewById(R.id.backgroundView);
        mContentLayout  = (ScrollView) findViewById(R.id.contentLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tintView = findViewById(R.id.tintViewFront);
        mImageView = (ImageView) findViewById(R.id.imageView);
        titleTextView = (TextView) findViewById(R.id.title);
    }


}
