package cn.chingshen.gasless.domain.vos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthPrice {
    private float cny = 0f;
    private float usd = 0f;

    public int getUsdInt() {
        return (int) usd;
    }
}
