package persistence.sql.common.instance;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.common.meta.Columns;
import utils.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Values {
    private Value[] values;

    private  <T> Values(T t) {
        this.values = Value.of(t);
    }

    public static <T> Values of(T t) {
        return new Values(t);
    }

    /**
     * 값을 ','으로 이어 한 문자열로 반환합니다.
     * 예) "홍길동, 13, F"
     */
    public String getValuesWithComma() {
        return StringUtils.withComma(Arrays.stream(values)
                .map(Value::getValue)
                .toArray(String[]::new));
    }

    /**
     * @Id, @Transient를 제외한 모둔 field를 가져와 [ 필드명 = 값 ] 형식으로 묶어 하나의 문자열로 반환합니다.
     * 예) "name = '홍길동', age = 13, gender = 'F'"
     */
    public String getFieldNameAndValue(Columns columns) {
        return Arrays.stream(columns.getValue())
                .filter(column -> !column.isPrimaryKey())
                .map(column -> String.format("%s = %s", column.getName(), getValue(column.getFieldName())))
                .collect(Collectors.joining(", "));
    }

    private String getValue(String fieldName) {
        return Arrays.stream(values)
                .filter(value -> value.isEquals(fieldName))
                .findFirst()
                .get()
                .getValue();
    }
}
