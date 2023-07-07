package com.polaris.lesscode.form.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsertData {
    Map<String, Object> mainData;
    List<Object> listData;
    Long mainId;
}
