package com.polaris.lesscode.form.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportProgress {

    private Integer total;

    private Integer suc;

    private Integer fail;

    private Integer insertSuc;

    private Integer updateSuc;
}
