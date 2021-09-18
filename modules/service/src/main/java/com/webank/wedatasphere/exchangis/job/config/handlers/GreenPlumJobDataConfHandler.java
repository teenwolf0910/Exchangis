/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.job.config.handlers;

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.service.GreenPlumMetaDbService;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author ronaldyang
 */
@Service(JobDataConfHandler.PREFIX + "greenplum")
public class GreenPlumJobDataConfHandler extends AbstractJobDataConfHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GreenPlumJobDataConfHandler.class);
    private static final String TABLE_GREENPLUM_NAME_FILTER = "_plum_change_tmp_";
    @Resource
    private GreenPlumMetaDbService greenplumMetaDbService;

    @Override
    protected String[] connParamNames() {
        return new String[]{Constants.PARAM_GREENPLUM_HOST,
                Constants.PARAM_GREENPLUM_PORT,
                Constants.PARAM_GREENPLUM_DB_NAME
        };
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        if (dataFormParams.get("database") != null && !StringUtils.isBlank(String.valueOf(dataFormParams.get("database")))) {
            if (dataFormParams.get("table") != null && !StringUtils.isBlank(String.valueOf(dataFormParams.get("table")))) {
                if (StringUtils.isNotBlank(String.valueOf(dataFormParams.getOrDefault("batchSize", "")))) {
                    int batchSize = Integer.parseInt(String.valueOf(dataFormParams.get("batchSize")));
                    if (batchSize > 100000) {
                        throw new JobDataParamsInValidException("exchange.job.handler.jdbc.batchSize", new Object[]{100000});
                    }
                }

            } else {
                throw new JobDataParamsInValidException("exchange.job.handler.jdbc.table.notNull", new Object[0]);
            }
        } else {
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.db.notNull", new Object[0]);
        }
    }

    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {

    }

    @Override
    @SuppressWarnings({"rawtypes"})
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        if (dataFormParams.get("table") instanceof String) {
            dataFormParams.put("table", Collections.singletonList((String)dataFormParams.get("table")));
        }

        List<?> tables = (List)dataFormParams.get("table");
        List columns = (List)dataFormParams.get("column");
        String querySql = this.contactSql(tables, columns, String.valueOf(dataFormParams.getOrDefault("where", "")));
        dataFormParams.put("querySql", querySql);
        Map<String, Object> objectMap = dataSource.resolveParams();
        String host = String.valueOf(objectMap.get("host"));
        String port = objectMap.get("port").toString();
        String dbname = String.valueOf(objectMap.get("dbName"));
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dataFormParams.getOrDefault("database",dbname));
        dataFormParams.put("jdbcUrl", jdbcUrl);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        List columns = (List)dataFormParams.get("column");
        if (null != columns && !columns.isEmpty()) {
            List sqlColumns = (List)columns.stream().map((value) -> {
                return value instanceof DataColumn ? ((DataColumn)value).getName() : null;
            }).collect(Collectors.toList());
            List sqlOrderColumns = (List)columns.stream().sorted(Comparator.comparing(DataColumn::getIndex)).map((value) -> {
                return value instanceof DataColumn ? ((DataColumn)value).getName() : null;
            }).collect(Collectors.toList());
            dataFormParams.put("sqlOrderColumn", sqlOrderColumns);
            dataFormParams.put("sqlColumn", sqlColumns);
        }

        Object tableNameParam = dataFormParams.getOrDefault("table", "");
        String tableName;
        if (tableNameParam instanceof List) {
            tableName = String.valueOf(((List)tableNameParam).get(0));
        } else {
            tableName = String.valueOf(tableNameParam);
        }
        String tmpTableName = tableName+TABLE_GREENPLUM_NAME_FILTER+System.currentTimeMillis();
        List<String> primaryKeys = this.greenplumMetaDbService.getPrimaryKeys(dataSource, String.valueOf(dataFormParams.getOrDefault("database", "")), tableName);
        String writeMode = String.valueOf(dataFormParams.getOrDefault("writeMode","insert"));
        if(primaryKeys.size()==0 && !writeMode.equals("insert")){
            throw new JobDataParamsInValidException("Greenplum Table:"+tableName+" Not Primary Key, And Only Support Insert Writer Mode");
        }

        dataFormParams.put("primaryKeys", primaryKeys);

        List preSqls =  new ArrayList<>();
        preSqls.add(String.format("drop table if exists %s;",tmpTableName));
        //create table test6 (like test2);
        preSqls.add(String.format("create table %s (like %s);",tmpTableName,tableName));
        if(primaryKeys.size()>0) {
            preSqls.add(String.format("alter table %s add constraint %s primary key (%s);", tmpTableName, tmpTableName + "_primary_key", StringUtils.join(primaryKeys, ",")));
        }
        dataFormParams.put("table",tmpTableName);
        dataFormParams.put("preSql",preSqls);

        List postSqls =  new ArrayList<>();
        StringBuilder primaryJoin = new StringBuilder();
        for (int i = 0; i < primaryKeys.size(); i++) {
            String column = primaryKeys.get(i);
            if( i ==0 ) {
                primaryJoin.append("n." + column + "=o." + column);
            }else{
                primaryJoin.append("and n." + column + "=o." + column);
            }
        }
        switch (writeMode){
                case "replace":
                    postSqls.add(String.format("delete from %s where id in(select n.id from %s o join %s n on %s);",tableName,tableName,tmpTableName,primaryJoin));
                    break;
                case "update":
                    postSqls.add(String.format("delete from %s where id not in(select n.id from %s o join %s n on %s);",tmpTableName,tableName,tmpTableName,primaryJoin));
                    postSqls.add(String.format("delete from %s where id in(select n.id from %s o join %s n on %s);",tableName,tableName,tmpTableName,primaryJoin));
                    break;
                default:
                    if(primaryKeys.size()>0){
                        postSqls.add(String.format("delete from %s where id in(select n.id from %s o join %s n on %s);", tmpTableName, tableName, tmpTableName, primaryJoin));
                    }
                    break;
        }

        postSqls.add(String.format("insert into %s select * from %s;",tableName,tmpTableName));
        postSqls.add(String.format("drop table %s;",tmpTableName));
        dataFormParams.put("postSql",postSqls);


        Map<String, Object> objectMap = dataSource.resolveParams();
        String host = String.valueOf(objectMap.get("host"));
        String port = objectMap.get("port").toString();
        String dbname = String.valueOf(objectMap.get("dbName"));
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dataFormParams.getOrDefault("database",dbname));
        dataFormParams.put("jdbcUrl", jdbcUrl);
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
        return super.postGet0(dataConfParams);
    }

    @Override
    protected Map<String, Object> postGetReader(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap(5);
        result.put("database", dataConfParams.getOrDefault("database", ""));
        result.put("table", Json.fromJson(String.valueOf(dataConfParams.getOrDefault("table", "[]")), String.class, new Class[0]));
        result.put("where", dataConfParams.getOrDefault("where", ""));
        return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap(2);
        result.put("table", dataConfParams.getOrDefault("table", ""));
        result.put("database", dataConfParams.getOrDefault("database", ""));
        result.put("writeMode", dataConfParams.getOrDefault("writeMode", ""));
        result.put("batchSize", dataConfParams.getOrDefault("batchSize", ""));
        return result;
    }

    @Override
    protected boolean isColumnAutoFill() {
        return true;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    protected void autoFillColumn(List<DataColumn> columns, DataSource dataSource, Map<String, Object> dataFormParams, DataConfType type) {
        LOG.info("GreenPlum Fill column list automatically, type: " + type.name() + ", datasourceId: " + dataSource.getId() + ",  database: " + dataFormParams.get("database") + ", table:" + dataFormParams.get("table"));
        Object tableName = dataFormParams.get("table");
        List tables = new ArrayList();
        if (tableName instanceof String) {
            tables = Collections.singletonList(tableName);
        } else if (tableName instanceof List) {
            tables = (List)dataFormParams.get("table");
        }

        Iterator var7 = ((List)tables).iterator();

        while(var7.hasNext()) {
            Object table = var7.next();
            List<MetaColumnInfo> metaColumns = this.greenplumMetaDbService.getColumns(dataSource, String.valueOf(dataFormParams.get("database")), String.valueOf(table));
            String finalNamePrefix = "";
            metaColumns.forEach((metaColumn) -> {
                DataColumn dataColumn = new DataColumn(finalNamePrefix + metaColumn.getName(), metaColumn.getType(), metaColumn.getIndex());
                if (DataConfType.READER.equals(type)) {
                    dataColumn.setIndex(metaColumn.getIndex());
                }

                columns.add(dataColumn);
            });
        }
    }

    @SuppressWarnings({"rawtypes"})
    private String contactSql(List tables,
                              List columns, String whereClause){
        StringBuilder builder = (new StringBuilder(" SELECT ")).append(this.columnListSql(columns)).append(" FROM ").append(this.tableOnSql(tables, (List)null, (List)null));
        if (StringUtils.isNotBlank(whereClause)) {
            builder.append(" WHERE ").append(whereClause);
        }

        return builder.toString();
    }

    @SuppressWarnings({"rawtypes"})
    private String columnListSql(List columns){
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < columns.size(); ++i) {
            Object rawColumn = columns.get(i);
            if (rawColumn instanceof DataColumn) {
                builder.append("\"");
                builder.append(((DataColumn)rawColumn).getName());
                builder.append("\"");
                if (i < columns.size() - 1) {
                    builder.append(",");
                }

                builder.append(" ");
            }
        }

        return builder.toString();
    }

    @SuppressWarnings({"rawtypes"})
    private String tableOnSql(List tables, List alias, List joinInfo){
        StringBuilder builder = new StringBuilder();
        for (Object table : tables) {
            builder.append(table);
        }
        return builder.toString();
    }

}
