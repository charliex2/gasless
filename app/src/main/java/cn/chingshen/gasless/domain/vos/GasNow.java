package cn.chingshen.gasless.domain.vos;

/**
 * // {
 * //       "rapid": 60000000000,
 * //       "fast": 58000000000,
 * //       "standard": 55000000000,
 * //       "slow": 53000000000,
 * //       "timestamp": 1619284003821
 * // }
 */
public class GasNow {
    private long rapid;
    private long fast;
    private long standard;
    private long slow;
    private long timestamp;

    public GasNow() {
    }

    public GasNow(long rapid, long fast, long standard, long slow, long timestamp) {
        this.rapid = rapid;
        this.fast = fast;
        this.standard = standard;
        this.slow = slow;
        this.timestamp = timestamp;
    }

    public long getRapid() {
        return rapid;
    }

    public long getRapidGWei() {
        return (long) (rapid / 10e8);
    }

    public void setRapid(long rapid) {
        this.rapid = rapid;
    }


    public long getFast() {
        return fast;
    }

    public long getFastGWei() {
        return (long) (fast / 10e8);
    }

    public void setFast(long fast) {
        this.fast = fast;
    }

    public long getStandard() {
        return standard;
    }

    public long getStandardGWei() {
        return (long) (standard / 10e8);
    }

    public void setStandard(long standard) {
        this.standard = standard;
    }

    public long getSlow() {
        return slow;
    }

    public long getSlowGWei() {
        return (long) (slow / 10e8);
    }

    public void setSlow(long slow) {
        this.slow = slow;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
