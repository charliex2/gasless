package cn.chingshen.gasless.domain.vos;

import android.graphics.Color;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * // {
 * //       "rapid": 60000000000,
 * //       "fast": 58000000000,
 * //       "standard": 55000000000,
 * //       "slow": 53000000000,
 * //       "timestamp": 1619284003821
 * // }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GasNow implements Serializable {
    private long rapid = 0L;
    private long fast = 0L;
    private long standard = 0L;
    private long slow = 0L;
    private long timestamp = 0L;

    public String getRapidGWei() {
        return String.valueOf((long) (rapid / 10e8));
    }

    public String getFastGWei() {
        return String.valueOf((long) (fast / 10e8));
    }

    public String getStandardGWei() {
        return String.valueOf((long) (standard / 10e8));
    }

    public String getSlowGWei() {
        return String.valueOf((long) (slow / 10e8));
    }

    public String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return simpleDateFormat.format(this.getTimestamp());
    }

    /**
     * 全红 (250,0,0)
     * 全绿 (0,250,0)
     * 全黄色 (250,250,0)
     * 由绿变到红，则是G减少、R增加同时进行. 对应 GasPrice 0 - 150
     *
     * @return
     */
    public int getColor(long wei) {
        if (wei == 0) return Color.rgb(125, 125, 125);
        long gwei = (long) (wei / 10e8 / 150 * 250);
        if (gwei > 250) gwei = 250;
        int red = (int) gwei;
        int green = 250 - (int) gwei;
        return Color.rgb(red, green, 0);
    }
}
