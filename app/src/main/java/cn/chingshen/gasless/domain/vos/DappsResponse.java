package cn.chingshen.gasless.domain.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DappsResponse {
    private int code;
    private DappsData data;
}
