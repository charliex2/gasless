package cn.chingshen.gasless;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.chingshen.gasless.domain.vos.GasNow;
import cn.chingshen.gasless.domain.vos.GasNowResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    RemoteViews views;
    ComponentName widget;
    AppWidgetManager appWidgetManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        views = new RemoteViews(context.getPackageName(), R.layout.layout_gas_price_appwidget);
        widget = new ComponentName(context, GasPriceAppWidgetProvider.class);
        appWidgetManager = AppWidgetManager.getInstance(context);
        requestGasPrice();
    }


    private void requestGasPrice() {
        Log.i("gasnow", "requestGasPrice");
        String url = "https://www.gasnow.org/api/v3/gas/price?utm_source=gasless";
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("gasnow", "requestGasPrice failed" + e.getMessage());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i("gasnow", "requestGasPrice success");
                GasNowResponse gasNowResponse = JSON.parseObject(response.body().string(), GasNowResponse.class);
                GasNow gasNow = gasNowResponse.getData();
                views.setTextViewText(R.id.rapid, gasNow.getRapidGWei() + "");
                views.setTextViewText(R.id.fast, gasNow.getFastGWei() + "");
                views.setTextViewText(R.id.standard, gasNow.getStandardGWei() + "");
                views.setTextViewText(R.id.slow, gasNow.getSlowGWei() + "");
                appWidgetManager.updateAppWidget(widget, views);
            }
        });

    }
}
