package cn.chingshen.gasless.domain.vos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthPriceResponse {
    private int code;
    private EthPrice data;
}
