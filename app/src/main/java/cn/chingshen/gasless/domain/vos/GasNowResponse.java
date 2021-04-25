package cn.chingshen.gasless.domain.vos;

/**
 * // {
 * //   "code": 200,
 * //   "data": {
 * //       "rapid": 60000000000,
 * //       "fast": 58000000000,
 * //       "standard": 55000000000,
 * //       "slow": 53000000000,
 * //       "timestamp": 1619284003821
 * //   }
 * //}
 */
public class GasNowResponse {
    private int code;
    private GasNow data;

    public GasNowResponse() {
    }

    public GasNowResponse(int code, GasNow data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public GasNow getData() {
        return data;
    }

    public void setData(GasNow data) {
        this.data = data;
    }


}
