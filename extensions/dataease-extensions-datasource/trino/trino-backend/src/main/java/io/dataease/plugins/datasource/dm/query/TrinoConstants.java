package io.dataease.plugins.datasource.dm.query;



import io.dataease.plugins.common.constants.datasource.SQLConstants;


public class TrinoConstants extends SQLConstants {

    public static final String KEYWORD_TABLE = "%s";
    public static final String KEYWORD_FIX = "%s.\"%s\"";

    public static final String ALIAS_FIX = "%s";
    public static final String UNIX_TIMESTAMP = "to_unixtime(%s)";
    public static final String FROM_UNIXTIME = "from_unixtime(%s)";
    public static final String STR_TO_DATE = "to_timestamp(%s, '%s')";
    public static final String CAST = "CAST(%s AS %s)";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_PARSE = "%Y-%m-%d H%:i%:s%";
    public static final String FORMAT_DATETIME = "format_datetime(%s, '%s')";
    public static final String date_parse = "date_parse(%s, '%s')";
    public static final String to_date = "to_date(%s, '%s')";
    public static final String DEFAULT_INT_FORMAT = "bigint";
    public static final String DEFAULT_FLOAT_FORMAT = "DOUBLE";
    public static final String WHERE_VALUE_NULL = "(NULL,'')";
    public static final String WHERE_VALUE_VALUE = "'%s'";
    public static final String WHERE_NUMBER_VALUE = "%s";
    public static final String AGG_COUNT = "COUNT(*)";
    public static final String AGG_FIELD = "%s(%s)";
    public static final String AGG_FIELD2 = "count(distinct(%s))";
    public static final String WHERE_BETWEEN = "%s AND %s";
    public static final String WHERE_TIME_BETWEEN = "timestamp '%s' AND timestamp '%s'";
    public static final String BRACKETS = "(%s)";
    public static final String NAME = "trino";

    public TrinoConstants() {
    }


}