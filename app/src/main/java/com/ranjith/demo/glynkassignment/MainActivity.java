package com.ranjith.demo.glynkassignment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.Video;
import com.ranjith.demo.glynkassignment.common.AndroidUtils;
import com.ranjith.demo.glynkassignment.common.ScreenHelper;
import com.ranjith.demo.glynkassignment.common.StartPageSnapHelper;

import java.io.IOException;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.ToroPlayer;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class MainActivity extends AppCompatActivity implements YouTubePlayerDialog.Callback  {


    // Views
    Container container;
    LinearLayout loadingLayout;
    LinearLayout errorLayout;
    TextView errorMsgView;


    YouTubePlaylistAdapter adapter;
    YouTubePlayerManager playerManager;
    PlaylistViewModel viewModel;
    RecyclerView.LayoutManager layoutManager;

    private WindowManager windowManager;
    private int originalOrientation;

    PlayerSelector selector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);

        originalOrientation = getRequestedOrientation();
        windowManager = getWindowManager();

        playerManager = new YouTubePlayerManager(this, getSupportFragmentManager());
        if (savedInstanceState != null) {
            playerManager.onRestoreState(savedInstanceState,
                    ScreenHelper.shouldUseBigPlayer(windowManager.getDefaultDisplay()));
        }

        container = findViewById(R.id.container);
        loadingLayout = findViewById(R.id.linear_LoadingLayout);
        errorLayout = findViewById(R.id.linear_EmptyView);
        errorMsgView = findViewById(R.id.txt_EmptyMsg);


        SnapHelper snapHelper = new StartPageSnapHelper();
        snapHelper.attachToRecyclerView(container);

        adapter = new YouTubePlaylistAdapter(this,playerManager);
        layoutManager = new LinearLayoutManager(this);
        container.setLayoutManager(layoutManager);
        container.setAdapter(adapter);
        container.setCacheManager(adapter);

        selector = container.getPlayerSelector();


        try {
            viewModel.getPlaylist().observe(this, response -> {
                if(response.getItems().size()>0){
                    adapter.setData(response);
                    showContentView();
                }else {
                    showErrorMsg("Problem fetching video list, Try again later...");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMsg("Problem fetching video list, Try again later...");
        }

        if (savedInstanceState == null) {
            if(AndroidUtils.isNetworkAvailable(this)) {
                showLoadingView();
                try {
                    viewModel.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                showErrorMsg("Internet not Available !!!\n Please Turn on Wifi / Mobile Data Services !!");
            }
        }

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        windowManager = null;
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        YouTubePlayerDialog.InitData initData = null;
        List<ToroPlayer> activePlayers = container.filterBy(Container.Filter.PLAYING);
        if (!activePlayers.isEmpty()) {
            ToroPlayer firstPlayer = activePlayers.get(0);  // get the first one only.
            // We will store the Media object, playback state.
            Video item = adapter.getItem(firstPlayer.getPlayerOrder());
            if (item == null) {
                throw new IllegalStateException("Video is null for active Player: " + firstPlayer);
            }

            initData = new YouTubePlayerDialog.InitData(firstPlayer.getPlayerOrder(), item.getId(),
                    firstPlayer.getCurrentPlaybackInfo(), originalOrientation);
        }

        super.onSaveInstanceState(outState);
        playerManager.onSaveState(outState, initData, isChangingConfigurations());
    }


    @Override public void onBigPlayerCreated() {
        container.setPlayerSelector(PlayerSelector.NONE);
        container.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBigPlayerDestroyed(int videoOrder, String baseItem, PlaybackInfo latestInfo) {
        container.savePlaybackInfo(videoOrder, latestInfo);
        container.setPlayerSelector(selector);
        container.setVisibility(View.VISIBLE);
    }


    public void showContentView(){
        container.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
    }

    public void showLoadingView(){
        container.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
    }

    public void showErrorMsg(String msg){
        container.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
        errorMsgView.setText(msg);
    }
}
