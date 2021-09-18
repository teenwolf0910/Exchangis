{
  "name": "postgresqlreader",
  "parameter": {
    "datasource":#{datasourceId},
    "username": #{username},
    "password": #{password},
    "proxyHost": #{proxyHost},
    "proxyPort": #{proxyPort|0},
    "column_i": ${column},
    "table": #{table},
    "database":#{database|database},
    "where": #{where},
    "connection": [{
    "querySql": [
       #{querySql}
    ],
    "jdbcUrl":[
       #{jdbcUrl}
     ]
   }]
  }
}
