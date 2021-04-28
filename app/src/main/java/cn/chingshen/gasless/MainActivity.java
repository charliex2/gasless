package cn.chingshen.gasless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.chingshen.gasless.databinding.ActivityMainBinding;
import cn.chingshen.gasless.domain.vos.Dapp;
import cn.chingshen.gasless.domain.vos.EthPrice;
import cn.chingshen.gasless.domain.vos.GasNow;
import cn.chingshen.gasless.domain.vos.GasNowResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

// https://blog.csdn.net/danpincheng0204/article/details/106778941
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private final Handler handler = new UiHandler();
    private MainActivityViewModel viewModel;

    private List<Dapp> dapps = new ArrayList<>();
    private GasNow gasNow;
    //    private EthPrice ethPrice;
    private DappRecycleViewAdapter dappRecycleViewAdapter;

    AppWidgetManager appWidgetManager;
    RemoteViews remoteViews;
    ComponentName widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        gasNow = (GasNow) intent.getSerializableExtra("gasNow");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (gasNow != null) binding.setGasNow(gasNow);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);


        binding.gasPrices.logo.setVisibility(View.GONE);
        binding.gasPrices.name.setVisibility(View.GONE);
        binding.gasPrices.divider.setVisibility(View.GONE);
        binding.gasPrices.title.setTypeface(Typeface.DEFAULT_BOLD);

        viewModel.getGasNow().observe(this, gn -> {
            binding.setGasNow(gn);
            binding.gasPrices.rapid.setTextColor(gn.getColor(gn.getRapid()));
            binding.gasPrices.fast.setTextColor(gn.getColor(gn.getFast()));
            binding.gasPrices.standard.setTextColor(gn.getColor(gn.getStandard()));
            binding.gasPrices.slow.setTextColor(gn.getColor(gn.getSlow()));

            refreshAppWidget(gn);

            dappRecycleViewAdapter.notifyDataSetChanged();
        });

        initEthPrice();
        initAppWidget();
        initDappsRecycleView();
        new SocketThread().start();
    }

    private void initAppWidget() {
        appWidgetManager = AppWidgetManager.getInstance(this);
        remoteViews = new RemoteViews(getPackageName(), R.layout.layout_gas_price_appwidget);
        widget = new ComponentName(this, GasPriceAppWidgetProvider.class);
    }

    private void refreshAppWidget(GasNow gasNow) {
        remoteViews.setTextViewText(R.id.rapid, gasNow.getRapidGWei());
        remoteViews.setTextColor(R.id.rapid, gasNow.getColor(gasNow.getRapid()));

        remoteViews.setTextViewText(R.id.fast, gasNow.getFastGWei());
        remoteViews.setTextColor(R.id.fast, gasNow.getColor(gasNow.getFast()));

        remoteViews.setTextViewText(R.id.standard, gasNow.getStandardGWei());
        remoteViews.setTextColor(R.id.standard, gasNow.getColor(gasNow.getStandard()));

        remoteViews.setTextViewText(R.id.slow, gasNow.getSlowGWei());
        remoteViews.setTextColor(R.id.slow, gasNow.getColor(gasNow.getSlow()));

        remoteViews.setTextViewText(R.id.last_updated_at, gasNow.getTime());
        appWidgetManager.updateAppWidget(widget, remoteViews);
    }

    private void initEthPrice() {
        viewModel.getEthPrice().observe(this, ethPrice -> {
            binding.setEthPrice(ethPrice);
        });
    }

    private void initDappsRecycleView() {
        dappRecycleViewAdapter = new DappRecycleViewAdapter(this, dapps);
        binding.dappsRecycleView.setAdapter(dappRecycleViewAdapter);
        viewModel.getDapps().observe(this, this::refreshDappsRecycleView);
    }

    private void refreshDappsRecycleView(List<Dapp> dapps) {
        if (dapps != null) {
            this.dapps.clear();
            this.dapps.addAll(dapps);
        }
        dappRecycleViewAdapter.notifyDataSetChanged();
    }


    private class UiHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (gasNow != null) {
                viewModel.setGasNow(gasNow);
            }
        }
    }


    class SocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .retryOnConnectionFailure(true)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            String url = "wss://www.gasnow.org/ws/gasprice";
            Request request = new Request.Builder().get().url(url).build();
            okHttpClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosed(webSocket, code, reason);
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosing(webSocket, code, reason);
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    Log.i("gasnow", "gasnow websocket failed: " + t.getMessage());
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    super.onMessage(webSocket, text);
                    Log.i("gasnow", text);
                    GasNowResponse gasNowResponse = JSON.parseObject(text, GasNowResponse.class);
                    gasNow = gasNowResponse.getData();
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }

                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    super.onOpen(webSocket, response);
                    Log.i("gasnow", "gasnow websocket connected");
                }
            });
        }
    }
}


