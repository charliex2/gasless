package cn.chingshen.gasless.domain.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GasNowResponse {
    private int code;
    private GasNow data;
}
