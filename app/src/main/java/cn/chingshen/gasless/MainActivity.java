package cn.chingshen.gasless;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.chingshen.gasless.databinding.ActivityMainBinding;
import cn.chingshen.gasless.dialogs.BaseDialog;
import cn.chingshen.gasless.domain.vos.Dapp;
import cn.chingshen.gasless.domain.vos.GasNow;
import cn.chingshen.gasless.domain.vos.GasNowResponse;
import cn.chingshen.gasless.domain.vos.VersionVO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

// https://blog.csdn.net/danpincheng0204/article/details/106778941
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    private List<Dapp> dapps = new ArrayList<>();
    private DappRecycleViewAdapter dappRecycleViewAdapter;

    AppWidgetManager appWidgetManager;
    RemoteViews remoteViews;
    ComponentName widget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        Intent intent = getIntent();
        GasNow gasNow = (GasNow) intent.getSerializableExtra("gasNow");
        if (gasNow != null) {
            binding.setGasNow(gasNow);
            viewModel.setGasNow(gasNow);
        }

        initGasNow();
        initEthPrice();
        initAppWidget();
        initDappsRecycleView();
        initBroadReceiver();
        initBottomInfo();
        initCurrency();

        SocketThread socketThread = new SocketThread();
        socketThread.start();

        checkUpdate();
    }

    private void initCurrency() {
        binding.btnChange.setOnClickListener(v -> viewModel.setCurrency(viewModel.getCurrency().getValue().equals("USD") ? "ETH" : "USD"));
        viewModel.getCurrency().observe(this, s -> {
            refreshRecycleViewItems();
            binding.setCurrency(s);
        });
    }

    private void refreshRecycleViewItems() {
        for (int i = 0; i < dapps.size(); i++) {
            dappRecycleViewAdapter.notifyItemChanged(i);
        }
    }

    private void initGasNow() {
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
            refreshRecycleViewItems();
            refreshAppWidget(gn);
        });
    }

    private void initBottomInfo() {
        binding.bottom.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void initBroadReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initAppWidget() {
        appWidgetManager = AppWidgetManager.getInstance(this);
        remoteViews = new RemoteViews(getPackageName(), R.layout.layout_gas_price_appwidget);
        widget = new ComponentName(this, GasPriceAppWidgetProvider.class);
    }

    private void refreshAppWidget(GasNow gasNow) {
        if (gasNow.getRapid() == 0) return;
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
            refreshRecycleViewItems();
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

    class SocketThread extends Thread {

        private int startSocketTime = 0;

        private void startSocket() {
            Log.i("gasnow", "starting socket " + startSocketTime);
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .retryOnConnectionFailure(true)
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
                    Log.i("gasnow", "thread closed");
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosing(webSocket, code, reason);
                    Log.i("gasnow", "gasnow websocket onClosing");
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    Log.i("gasnow", "gasnow websocket failed: " + t.getMessage());
                    // 尝试 5 次
                    if (startSocketTime < 5) {
                        startSocket();
                        startSocketTime++;
                    } else {
                        binding.gasPrices.lastUpdatedAt.setText(MainActivity.this.getResources().getString(R.string.retry));
                    }
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    super.onMessage(webSocket, text);
                    Log.i("gasnow", text);
                    GasNowResponse gasNowResponse = JSON.parseObject(text, GasNowResponse.class);
                    GasNow gasNow = gasNowResponse.getData();
                    viewModel.postGasNow(gasNow);
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }

                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    super.onOpen(webSocket, response);
                    startSocketTime = 0;
                    Log.i("gasnow", "gasnow websocket connected");
                }
            });
        }

        @Override
        public void run() {
            super.run();
            startSocket();
        }
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewModel.requestEthPrice();
        }
    };


    // 检查更新
    private void checkUpdate() {
        AllenVersionChecker.getInstance()
                .requestVersion()
                .setRequestUrl("http://defarmer-api.chingshen.cn/clients/android/latest-version")
                .request(new RequestVersionListener() {

                    @androidx.annotation.Nullable
                    @Override
                    public UIData onRequestVersionSuccess(DownloadBuilder downloadBuilder, String result) {
                        Log.i("update", result);
                        VersionVO versionVO = new Gson().fromJson(result, VersionVO.class);
                        Log.i("update", versionVO.toString());
                        if (!getAppVersionName().equals(versionVO.getData().getVersion())) {
                            return UIData.create().setDownloadUrl(versionVO.getData().getDownload())
                                    .setContent(versionVO.getData().getReleaseNote())
                                    .setTitle(versionVO.getData().getVersion());
                        }
                        return null;
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {
                        Toast.makeText(MainActivity.this, "获取版本信息失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCustomVersionDialogListener((context, versionBundle) -> {
                    BaseDialog baseDialog = new BaseDialog(context, R.style.CustomerDialog, R.layout.dialog_update);
                    baseDialog.setCanceledOnTouchOutside(false);
                    TextView versionTv = baseDialog.findViewById(R.id.version);
                    TextView updateContentTv = baseDialog.findViewById(R.id.update_content);
                    versionTv.setText(versionBundle.getTitle());
                    updateContentTv.setText(versionBundle.getContent());
                    return baseDialog;
                })
                .setCustomDownloadingDialogListener(new CustomDownloadingDialogListener() {
                    @Override
                    public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                        BaseDialog baseDialog = new BaseDialog(context, R.style.CustomerDialog, R.layout.dialog_downloading);
                        baseDialog.setCanceledOnTouchOutside(false);
                        return baseDialog;
                    }

                    @Override
                    public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
                        TextView versionTv = dialog.findViewById(R.id.version);
                        TextView updateContentTv = dialog.findViewById(R.id.update_content);
                        versionTv.setText(versionBundle.getTitle());
                        updateContentTv.setText(versionBundle.getContent());
                        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                        progressBar.setProgress(progress);
                    }
                })
                .executeMission(getApplicationContext());
    }

    /**
     * 获取当前app version name
     */
    public String getAppVersionName() {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionName;
    }
}


