package cn.chingshen.gasless.apis;

import cn.chingshen.gasless.domain.vos.EthPriceResponse;
import cn.chingshen.gasless.domain.vos.GasNowResponse;
import cn.chingshen.gasless.domain.vos.DappsResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GasNowApi {
    // baseUrl = https://www.gasnow.org
    @GET(value = "/api/v3/gas/price?utm_source=gasless")
    Call<GasNowResponse> getGasPrice();


    // baseUrl = https://apimaster.sparkpool.com
    @GET(value = "/api/gasnow/component/gasused2?utm_source=web")
    Call<DappsResponse> getDapps();

    // https://www.gasnow.org/api/v1/eth/price?utm_source=web
    @GET(value = "/api/v1/eth/price?utm_source=web")
    Call<EthPriceResponse> getEthPrice();


}
