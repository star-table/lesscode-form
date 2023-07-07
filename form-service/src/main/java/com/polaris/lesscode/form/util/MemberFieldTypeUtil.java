package com.polaris.lesscode.form.util;

import com.polaris.lesscode.form.bo.MemberFieldData;
import com.polaris.lesscode.form.bo.MemberFieldDatas;
import com.polaris.lesscode.form.constant.FormConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 成员类型控件工具类
 *
 * @author roamer
 * @version v1.0
 * @date 2021/2/1 10:56
 */
public final class MemberFieldTypeUtil {

    private static final Pattern PATTERN = Pattern.compile("^([URD]_)(\\d+)$");

    private MemberFieldTypeUtil() {
    }

    /**
     * 解析为MemberFieldDatas对象
     *
     * @param members
     * @return {@code MemberFieldDatas}
     */
    public static MemberFieldDatas parseMemberFieldDataList(Collection<String> members) {
        MemberFieldDatas dataList = new MemberFieldDatas();
        if (Objects.isNull(members) || members.isEmpty()) {
            return dataList;
        }
        for (String idItem : members) {
            MemberFieldData data = new MemberFieldData();
            if (StringUtils.isNumeric(idItem)) {
                data.setType(FormConstant.MEMBER_USER_PREFIX);
                data.setId(idItem);
                data.setRealId(Long.valueOf(idItem));
                dataList.appendData(data);
                continue;
            }

            Matcher matcher = PATTERN.matcher(idItem);
            // 匹配规则
            if (!matcher.find() || matcher.groupCount() != 2) {
                dataList.appendInvalidItem(idItem);
                continue;
            }

            String prefix = matcher.group(1);
            Long realId = Long.valueOf(matcher.group(2));
            data.setType(prefix);
            data.setId(idItem);
            data.setRealId(realId);
            dataList.appendData(data);
        }
        return dataList;
    }

    public static MemberFieldDatas parseMemberFieldDataList(Object o) {
        if (o instanceof Collection){
            return parseMemberFieldDataList(((Collection<?>) o).stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return new MemberFieldDatas();
    }
}
