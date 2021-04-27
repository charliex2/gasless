package cn.chingshen.gasless;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import cn.chingshen.gasless.apis.Api;
import cn.chingshen.gasless.domain.vos.Dapp;
import cn.chingshen.gasless.domain.vos.DappsResponse;
import cn.chingshen.gasless.domain.vos.GasNow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<GasNow> gasNow;
    private MutableLiveData<Dapp[]> dapps;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void setGasNow(GasNow gasNow) {

        this.gasNow.setValue(gasNow);
    }

    public LiveData<GasNow> getGasNow() {
        if (gasNow == null) {
            gasNow = new MutableLiveData<>();
            gasNow.setValue(new GasNow());
        }
        return gasNow;
    }

    public void setDapps(Dapp[] dapps) {
        this.dapps.setValue(dapps);
    }

    public LiveData<Dapp[]> getDapps() {
        if (dapps == null) {
            requestDapps();
            dapps = new MutableLiveData<>();
        }
        return dapps;
    }

    private void requestDapps() {
        String baseUrl = "https://apimaster.sparkpool.com";
        new Api(new Api.Builder(getApplication(), baseUrl)).gasNowApi.getDapps().enqueue(new Callback<DappsResponse>() {
            @Override
            public void onResponse(Call<DappsResponse> call, Response<DappsResponse> response) {
                Dapp[] dapps = response.body().getData().getData();
                setDapps(dapps);
            }

            @Override
            public void onFailure(Call<DappsResponse> call, Throwable t) {

            }
        });
    }
}
