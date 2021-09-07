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

package com.webank.wedatasphere.exchangis.datasource.conns;

import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Basic wrapper of greenplum connection
 */
public class GreenPlum {
    private static final Logger LOG = LoggerFactory.getLogger(GreenPlum.class);
    private String DRIVER = "org.postgresql.Driver";
    private String DEFAULT_DB = "database";
    private String host;
    private String port;
    private String username;
    private String password;
    private String dbname;


    private GreenPlum(String host, String port, String username, String password, String dbname) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
    }

    public static GreenPlum createGreenPlum(Map<String, Object> param) throws IOException, ClassNotFoundException {
        String host = String.valueOf(param.get("host"));
        String port = param.get("port").toString();
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(CryptoUtils.string2Object(String.valueOf(param.get("password"))));
        String dbName = String.valueOf(param.get("dbName"));
        return new GreenPlum(host, port, username, password, dbName);
    }

    private Connection getDbConnect(String database) throws Exception {
        return getDbConnect();
    }

    private Connection getDbConnect() throws Exception {
        Connection conn = null;
        try {
            Class.forName(this.DRIVER);
            String url = String.format("jdbc:postgresql://%s:%s/%s", this.host, this.port, this.dbname);
            conn = DriverManager.getConnection(url, this.username, this.password);
            return conn;
        } catch (Exception var3) {
            throw new EndPointException("exchange.greenplum.obtain.database_info.failed", var3, new Object[0]);
        }
    }

    /**
     * Get all databases in server instance
     *
     * @return name list
     */
    public List<String> getAllDatabases() {
        List<String> dataBaseName = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(DRIVER);
            conn = getDbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select datname from pg_database");
            while(rs.next()) {
                dataBaseName.add(rs.getString("datname"));
            }
        } catch (Exception e) {
            if (e instanceof SQLException) {
                if (((SQLException) e).getErrorCode() == 942) {
                    dataBaseName.add(DEFAULT_DB);
                    return dataBaseName;
                }
            }
            LOG.error("Failed to obtain database information: [" + e.getMessage() + "]");
            throw new EndPointException("exchange.greenplum.obtain.database_info.failed", e);
        } finally {
            closeResource(conn, stmt, rs);
        }
        return dataBaseName;
    }

    /**
     * Get all tables from sid
     *
     * @param database database
     * @return name list
     */
    public List<String> getAllTables(String database) {
        List<String> tableNames = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.getDbConnect(database);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT relname from pg_class a,pg_namespace b where relname not like '%prt%' and relkind ='r'  and a.relnamespace=b.oid and nspname not in ('pg_catalog','information_schema','gp_toolkit') and nspname not like '%pg_temp%'");
            while (rs.next()) {
                tableNames.add(rs.getString("relname"));
            }
        } catch (Exception e) {
            throw new EndPointException("exchange.greenplum.obtain.table_info.failed", e);
        } finally {
            closeResource(conn, stmt, rs);
        }
        return tableNames;
    }

    public List<MetaColumnInfo> getColumn(String database, String table) throws Exception {
        List<MetaColumnInfo> metaColumnInfos = new ArrayList<>();
        Connection conn = this.getDbConnect(database);
        String sql = String.format("select * from %s where 1=0", table);
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSetMetaData meta;
        try {
            List<String> primaryKeys = getPrimaryKeys(conn, table);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                MetaColumnInfo info = new MetaColumnInfo();
                info.setIndex(i);
                info.setName(meta.getColumnName(i));
                info.setType(meta.getColumnTypeName(i));
                if (primaryKeys.contains(meta.getColumnName(i))) {
                    info.setPrimaryKey(true);
                }
                metaColumnInfos.add(info);
            }
        } catch (SQLException e) {
            throw new EndPointException("exchange.greenplum.obtain.field_info.failed", e);
        } finally {
            closeResource(conn, ps, rs);
        }
        return metaColumnInfos;
    }

    /**
     * Get primary keys
     *
     * @param connection connection
     * @param table      table name
     * @return key list
     * @throws SQLException
     */
    private List<String> getPrimaryKeys(Connection connection, String table) throws SQLException {
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList primaryKeys = new ArrayList();
        try {
            String sql = String.format("select c.attname from pg_class a join pg_index b on a.oid=b.indrelid join pg_attribute c on string_to_array(b.indkey::text, ' ')::int2[] = ARRAY[c.attnum] and b.indrelid=c.attrelid where a.relname='%s' and b.indisprimary=true;", table);
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                primaryKeys.add(rs.getString("attname"));
            }
            return primaryKeys;
        } finally {
            if (null != rs) {
                this.closeResource((Connection)null, (Statement)null, rs);
            }

        }
    }

    public List<String> getPrimaryKeys(String database, String table) {
        Connection conn = null;

        List primayKeys = new ArrayList();
        try {
            conn = this.getDbConnect(database);
            primayKeys = this.getPrimaryKeys(conn, table);
        } catch (Exception var8) {
            throw new EndPointException("exchange.greenplum.obtain.field_info.failed", var8, new Object[0]);
        } finally {
            this.closeResource(conn, (Statement)null, (ResultSet)null);
        }

        return primayKeys;
    }

    /**
     * Close database resource
     *
     * @param connection connection
     * @param statement  statement
     * @param resultSet  result set
     */
    private void closeResource(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (null != resultSet && !resultSet.isClosed()) {
                resultSet.close();
            }
            if (null != statement && !statement.isClosed()) {
                statement.close();
            }
            if (null != connection && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOG.error("SQLException: " + e.getMessage(), e);
        }
    }


    public boolean isUseableTable(String database, String table) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getDbConnect(database);
            stmt = conn.createStatement();
            stmt.executeQuery("select * from " +  table + " where 1 = 2");
        } catch (Exception e) {
            LOG.error("Exception: " + e.getMessage(), e);
            throw new EndPointException("exchange.greenplum_meta.get.table.by.user.input.failed", e, database,table);
        } finally {
            closeResource(conn, null, null);
        }

        return true;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}

