package com.webank.wedatasphere.exchangis.datasource.checks;

import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static com.webank.wedatasphere.exchangis.datasource.Constants.PARAM_DEFAULT_PASSWORD;
import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;

/**
 * Validation of greenplum connection
 */
@Service(PREFIX + "greenplum")
public class GreenPlumConnCheck extends AbstractDataSourceConnCheck {
    private static final Logger logger = LoggerFactory.getLogger(GreenPlumConnCheck.class);

    //greenplum driver
    private static final String DRIVER = "org.postgresql.Driver";
    @Resource
    private Configuration conf;
    @Resource
    private DataSourceServiceImpl dataSourceService;
    @Override
    public void validate(DataSourceModel md) throws Exception {
        Map<String, Object> param = md.resolveParams();
        Set<String> keys = param.keySet();
        if (keys.contains("host") && !StringUtils.isBlank(String.valueOf(param.get("host")))) {
            if (keys.contains("port") && !StringUtils.isBlank(String.valueOf(param.get("port")))) {
                if (keys.contains("dbName") && !StringUtils.isBlank(String.valueOf(param.get("dbName")))) {
                    if (keys.contains("username") && String.valueOf(param.get("username")).equals("sys")) {
                        throw new Exception("connection must set username and password!");
                    } else {
                        try {
                            Integer.valueOf(String.valueOf(param.get("port")));
                        } catch (NumberFormatException var5) {
                            throw new Exception("port is not a number");
                        }
                    }
                } else {
                    throw new Exception("dbNameat least one cannot be null");
                }
            } else {
                throw new Exception("port cannot be null");
            }
        } else {
            throw new Exception("host cannot be null");
        }
    }

    @Override
    public void check(DataSource ds, File file) throws Exception {Map<String, Object> param = ds.resolveParams();
        String host = String.valueOf(param.get("host"));
        int port = Integer.parseInt(param.get("port").toString());
        String dbname = String.valueOf(param.get("dbName"));
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbname);
        logger.info("postgresql connect:" + url);
        String username = String.valueOf(param.get("username"));
        String password = String.valueOf(param.get("password"));
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
        } catch (SQLException var20) {
            logger.error("SQLException:" + var20.getMessage(), var20);
            throw new Exception(var20.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var19) {
                logger.error("SQLException:" + var19.getMessage(), var19);
            }

        }

    }
}