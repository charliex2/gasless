package cn.chingshen.gasless.domain.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tx {
    private Integer gas;
    private String name;
    private String url;
}
