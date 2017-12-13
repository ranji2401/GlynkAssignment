/*
 * Copyright (c) 2017 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ranjith.demo.glynkassignment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.ranjith.demo.glynkassignment.common.AspectRatioFrameLayout;
import com.ranjith.demo.glynkassignment.common.CustomTypefaceSpan;
import com.ranjith.demo.glynkassignment.common.RandomSpanHelper;
import com.ranjith.demo.glynkassignment.common.ViewUtil;

import java.util.Random;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;


/**
 * @author eneim (8/1/17).
 */

@SuppressWarnings({"WeakerAccess", "unused"}) //
public class YouTubeVideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {

    private static final String TAG = "YouT:ViewHolder";

    static final int LAYOUT_RES = R.layout.view_holder_youtube_player_full;

    private final YouTubePlayerManager manager;
    private String videoId;

    YouTubePlayerHelper helper;

    private final RequestOptions options =
            new RequestOptions().fitCenter().placeholder(R.drawable.exo_edit_mode_logo);

    AspectRatioFrameLayout playerViewContainer;

    TextView videoName;
    //  TextView videoCaption;
    ImageView thumbnailView;

    final FrameLayout playerView;

    YouTubeVideoViewHolder(YouTubePlayerManager manager, View itemView) {
        super(itemView);
        this.manager = manager;
        playerViewContainer = itemView.findViewById(R.id.player_container);
        videoName = itemView.findViewById(R.id.video_id);
//    videoCaption = itemView.findViewById(R.id.video_description);
        thumbnailView = itemView.findViewById(R.id.thumbnail);

        playerView = itemView.findViewById(R.id.player_view);
        int viewId = ViewUtil.generateViewId();
        playerView.setId(viewId);
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
        if (helper == null) {
            helper = manager.obtainHelper(container, this, this.videoId);
        }

        helper.initialize(playbackInfo);
        thumbnailView.setVisibility(View.VISIBLE);
    }

    @Override
    public void play() {
        thumbnailView.setVisibility(View.GONE);
        if (helper != null) helper.play();
        // change the alpha of the textView
        videoName.setAlpha(1.0f);
    }

    @Override
    public void pause() {
        thumbnailView.setVisibility(View.VISIBLE);
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        thumbnailView.setVisibility(View.VISIBLE);
        manager.releaseHelper(this);
        this.helper = null;
        videoName.setAlpha(0.2f);
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.999;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    @Override
    public void onSettled(Container container) {

        if (helper != null) helper.onSettled();
    }

    void bind(Context context, Video item, int position, Random random) {
        this.videoId = item.getId();

//        StringBuilder sb = new StringBuilder(item.getSnippet().getTitle());
        String title = item.getSnippet().getTitle();
        int splitIndex = title.length() - 1;
        if (title.contains("-")) {
            splitIndex = title.indexOf("-");
            title = title.replace('-','\n');
            splitIndex--;
        }

        int primaryRandom = random.nextInt(6);
        int i = primaryRandom % 2;
        int secondRand = random.nextInt(3);
        int thirdRand = secondRand + 3;
        if (i == 0) {
            int temp = secondRand;
            secondRand = thirdRand;
            thirdRand = temp;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(title);

        Typeface typefaceHeader = Typeface.createFromAsset(context.getAssets(), RandomSpanHelper.fontList.get(secondRand));
        Typeface typefaceContent = Typeface.createFromAsset(context.getAssets(), RandomSpanHelper.fontList.get(thirdRand));

        AbsoluteSizeSpan headerSize = new AbsoluteSizeSpan(RandomSpanHelper.sizeList.get(thirdRand));
        AbsoluteSizeSpan contentSize = new AbsoluteSizeSpan(RandomSpanHelper.sizeList.get(secondRand));

        if (i == 0) {
            typefaceHeader = Typeface.createFromAsset(context.getAssets(), RandomSpanHelper.fontList.get(thirdRand));
            typefaceContent = Typeface.createFromAsset(context.getAssets(), RandomSpanHelper.fontList.get(secondRand));

            headerSize = new AbsoluteSizeSpan(RandomSpanHelper.sizeList.get(secondRand));
            contentSize = new AbsoluteSizeSpan(RandomSpanHelper.sizeList.get(thirdRand));
        }

        builder.setSpan(new CustomTypefaceSpan("",typefaceHeader),0,splitIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(new CustomTypefaceSpan("",typefaceContent),splitIndex,title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        builder.setSpan(headerSize,0,splitIndex,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(contentSize,splitIndex,title.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);



        int rand = random.nextInt(6);
        String color = RandomSpanHelper.colorList.get(rand);
        switch (rand%3){
            case 0:
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(color)),splitIndex,title.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                break;
            case 1:
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(color)),0,splitIndex,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                break;
            case 2:
                builder.setSpan(new BackgroundColorSpan(Color.parseColor(color)),0,splitIndex,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                break;
        }

        builder.insert(0," ");
        builder.insert(splitIndex+1," ");

        this.videoName.setText(builder);
//    this.videoCaption.setText(item.getSnippet().getDescription());

        Thumbnail thumbnail = item.getSnippet().getThumbnails().getHigh();
        if (thumbnail != null) {
            playerViewContainer.setAspectRatio(thumbnail.getWidth() / (float) thumbnail.getHeight());
            Glide.with(itemView).load(thumbnail.getUrl()).apply(options).into(thumbnailView);
        }
    }

    @Override
    public String toString() {
        return "Player{" + "playerOrder=" + getPlayerOrder() + '}';
    }
}
