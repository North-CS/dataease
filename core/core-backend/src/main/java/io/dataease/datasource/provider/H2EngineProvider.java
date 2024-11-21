package io.dataease.datasource.provider;


import io.dataease.dataset.utils.TableUtils;
import io.dataease.datasource.dao.auto.entity.CoreDeEngine;
import io.dataease.datasource.request.EngineRequest;
import io.dataease.datasource.type.H2;
import io.dataease.datasource.type.Mysql;
import io.dataease.extensions.datasource.dto.ConnectionObj;
import io.dataease.extensions.datasource.dto.DatasourceDTO;
import io.dataease.extensions.datasource.dto.TableField;
import io.dataease.extensions.datasource.vo.DatasourceConfiguration;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@Service("h2Engine")
public class H2EngineProvider extends EngineProvider {

    private static final String creatTableSql =
            "CREATE TABLE IF NOT EXISTS `TABLE_NAME`" +
                    "Column_Fields;";


    @Override
    public String createView(String name, String viewSQL) {
        return "CREATE or replace view " + name + " AS (" + viewSQL + ")";
    }

    @Override
    public String insertSql(String name, List<String[]> dataList, int page, int pageNumber) {
        String insertSql = "INSERT INTO `TABLE_NAME` VALUES ".replace("TABLE_NAME", name);
        StringBuffer values = new StringBuffer();

        Integer realSize = page * pageNumber < dataList.size() ? page * pageNumber : dataList.size();
        for (String[] strings : dataList.subList((page - 1) * pageNumber, realSize)) {
            String[] strings1 = new String[strings.length];
            for (int i = 0; i < strings.length; i++) {
                if (StringUtils.isEmpty(strings[i])) {
                    strings1[i] = null;
                } else {
                    strings1[i] = strings[i].replace("'", "\\'");
                }
            }
            values.append("('").append(String.join("','", Arrays.asList(strings1)))
                    .append("'),");
        }
        return (insertSql + values.substring(0, values.length() - 1)).replaceAll("'null'", "null");
    }


    @Override
    public String dropTable(String name) {
        return "DROP TABLE IF EXISTS `" + name + "`";
    }

    @Override
    public String dropView(String name) {
        return "DROP VIEW IF EXISTS `" + name + "`";
    }

    @Override
    public String replaceTable(String name) {
        return "ALTER TABLE `FROM_TABLE` rename to `FROM_TABLE_tmp`; ALTER TABLE `TO_TABLE` rename to `FROM_TABLE`; DROP TABLE IF EXISTS `FROM_TABLE_tmp`;".replace("FROM_TABLE", name).replace("TO_TABLE", TableUtils.tmpName(name));
    }


    @Override
    public String createTableSql(String tableName, List<TableField> tableFields, CoreDeEngine engine) {
        String dorisTableColumnSql = createTableSql(tableFields);
        return creatTableSql.replace("TABLE_NAME", tableName).replace("Column_Fields", dorisTableColumnSql);
    }

    private String createTableSql(final List<TableField> tableFields) {
        StringBuilder columnFields = new StringBuilder("`");
        StringBuilder key = new StringBuilder();
        for (TableField tableField : tableFields) {
            if (tableField.isPrimaryKey()) {
                key.append("`").append(tableField.getName()).append("`, ");
            }
            columnFields.append(tableField.getName()).append("` ");
            int size = tableField.getPrecision() * 4;
            switch (tableField.getDeType()) {
                case 0:
                    columnFields.append("varchar(2048)").append(",`");
                    break;
                case 1:
                    columnFields.append("varchar(2048)").append(",`");
                    break;
                case 2:
                    columnFields.append("bigint(20)").append(",`");
                    break;
                case 3:
                    columnFields.append("varchar(100)").append(",`");
                    break;
                case 4:
                    columnFields.append("TINYINT(length)".replace("length", String.valueOf(tableField.getPrecision()))).append(",`");
                    break;
                default:
                    columnFields.append("varchar(2048)").append(",`");
                    break;
            }
        }
        if (StringUtils.isEmpty(key.toString())) {
            columnFields = new StringBuilder(columnFields.substring(0, columnFields.length() - 2));
        } else {
            key = new StringBuilder(key.substring(0, key.length() - 2));
            columnFields = new StringBuilder(columnFields.substring(0, columnFields.length() - 1));
            columnFields.append("PRIMARY KEY (PRIMARYKEY)".replace("PRIMARYKEY", key.toString()));
        }
        columnFields = new StringBuilder("(" + columnFields + ")");
        return columnFields.toString();
    }
}
