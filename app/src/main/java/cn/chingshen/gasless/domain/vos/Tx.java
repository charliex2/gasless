package cn.chingshen.gasless.domain.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tx {
    private int gas;
    private String name;
    private String url;

    public String getName() {
        return name.trim();
    }
}
