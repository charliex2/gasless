package cn.chingshen.gasless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import cn.chingshen.gasless.databinding.ActivityMainBinding;
import cn.chingshen.gasless.domain.vos.Dapp;
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
    private GasNow gasNow;
    private MainActivityViewModel viewModel;
    private Dapp[] dapps = {};
    private DappRecycleViewAdapter dappRecycleViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        GasNow gasNow = (GasNow) intent.getSerializableExtra("gasNow");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if (gasNow != null) {
            viewModel.setGasNow(gasNow);
        }

        viewModel.getGasNow().observe(this, gasNow1 -> binding.setGasNow(gasNow1));
        initDappsRecycleView();
        new SocketThread().start();
    }

    private void initDappsRecycleView() {
        dappRecycleViewAdapter = new DappRecycleViewAdapter(this, dapps);
        binding.dappsRecycleView.setAdapter(dappRecycleViewAdapter);
        viewModel.getDapps().observe(this, this::refreshDappsRecycleView);
    }

    private void refreshDappsRecycleView(Dapp[] dapps) {
        if (dapps != null) {
            this.dapps = dapps;
        }
    }


    private class UiHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (gasNow != null) viewModel.setGasNow(gasNow);
        }
    }

    class SocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
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


