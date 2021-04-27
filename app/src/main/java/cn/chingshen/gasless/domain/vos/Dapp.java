package cn.chingshen.gasless.domain.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dapp {
    private String icon;
    private String[] tag;
    private String title;
    private Tx[] tx;
}
