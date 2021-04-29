package cn.chingshen.gasless;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cn.chingshen.gasless.apis.Api;
import cn.chingshen.gasless.domain.vos.Dapp;
import cn.chingshen.gasless.domain.vos.DappsResponse;
import cn.chingshen.gasless.domain.vos.EthPrice;
import cn.chingshen.gasless.domain.vos.EthPriceResponse;
import cn.chingshen.gasless.domain.vos.GasNow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<GasNow> gasNow;
    private MutableLiveData<List<Dapp>> dapps;
    private MutableLiveData<EthPrice> ethPrice;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void setGasNow(GasNow gasNow) {
        if (this.gasNow == null) this.gasNow = new MutableLiveData<>();
        this.gasNow.setValue(gasNow);
    }

    public void postGasNow(GasNow gasNow) {
        if (this.gasNow == null) this.gasNow = new MutableLiveData<>();
        this.gasNow.postValue(gasNow);
    }

    public LiveData<GasNow> getGasNow() {
        if (gasNow == null) {
            gasNow = new MutableLiveData<>();
            gasNow.setValue(new GasNow());
        }
        return gasNow;
    }

    public void setDapps(List<Dapp> dapps) {
        this.dapps.setValue(dapps);
    }

    public LiveData<List<Dapp>> getDapps() {
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
                List<Dapp> dapps = response.body().getData().getData();
                setDapps(dapps);
            }

            @Override
            public void onFailure(Call<DappsResponse> call, Throwable t) {

            }
        });
    }

    public void requestEthPrice() {
        new Api(new Api.Builder(getApplication())).gasNowApi.getEthPrice().enqueue(new Callback<EthPriceResponse>() {
            @Override
            public void onResponse(Call<EthPriceResponse> call, Response<EthPriceResponse> response) {
                EthPrice ethPrice = response.body().getData();
                setEthPrice(ethPrice);
            }

            @Override
            public void onFailure(Call<EthPriceResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<EthPrice> getEthPrice() {
        if (ethPrice == null) {
            ethPrice = new MutableLiveData<>();
            ethPrice.setValue(new EthPrice());
            requestEthPrice();
        }
        return ethPrice;
    }

    public void setEthPrice(EthPrice ethPrice) {
        this.ethPrice.setValue(ethPrice);
    }
}
