package cn.chingshen.gasless;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import cn.chingshen.gasless.apis.Api;
import cn.chingshen.gasless.domain.vos.GasNow;
import cn.chingshen.gasless.domain.vos.GasNowResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GasPriceAppWidgetProvider extends AppWidgetProvider {
    String action = "android.appwidget.action.APPWIDGET_UPDATE";
    GasNow gasNow;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i("gasnow", "GasPriceAppWidgetProvider onDisabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("gasnow", "GasPriceAppWidgetProvider onUpdate");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_gas_price_appwidget);

        Intent intentRefresh = new Intent(action);
        intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pIntentRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh, 0);
        views.setOnClickPendingIntent(R.id.last_updated_at, pIntentRefresh);

        Intent intentOpenActivity = new Intent(context, MainActivity.class);

        if (gasNow != null) {
            intentOpenActivity.putExtra("gasNow", gasNow);
        }

        PendingIntent pIntentOpenActivity = PendingIntent.getActivity(context, 0, intentOpenActivity, 0);
        views.setOnClickPendingIntent(R.id.prices, pIntentOpenActivity);
        requestGasPrice(context, appWidgetManager, appWidgetIds, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i("gasnow", "onEnabled");
    }

    private void requestGasPrice(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, RemoteViews views) {
        Log.i("gasnow", "requestGasPrice");
//        String url = "https://www.gasnow.org/api/v3/gas/price?utm_source=gasless";
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .build();
//        Request request = new Request.Builder().get().url(url).build();

        for (int appWidgetId : appWidgetIds) {
            views.setTextViewText(R.id.last_updated_at, "updating");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        new Api(new Api.Builder(context)).gasNowApi.getGasPrice().enqueue(new Callback<GasNowResponse>() {
            @Override
            public void onResponse(Call<GasNowResponse> call, Response<GasNowResponse> response) {
                Log.i("gasnow", "requestGasPrice success");
                gasNow = response.body().getData();
                for (int appWidgetId : appWidgetIds) {
                    Log.i("gasnow", "widget onUpdate");
                    views.setTextViewText(R.id.rapid, gasNow.getRapidGWei());
                    views.setTextViewText(R.id.fast, gasNow.getFastGWei());
                    views.setTextViewText(R.id.standard, gasNow.getStandardGWei());
                    views.setTextViewText(R.id.slow, gasNow.getSlowGWei());
                    views.setTextViewText(R.id.last_updated_at, gasNow.getTime());
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }

            @Override
            public void onFailure(Call<GasNowResponse> call, Throwable t) {
                Log.i("gasnow", "requestGasPrice failed" + t.getMessage());
                for (int appWidgetId : appWidgetIds) {
                    views.setTextViewText(R.id.last_updated_at, "pls retry");
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        });
    }
}
