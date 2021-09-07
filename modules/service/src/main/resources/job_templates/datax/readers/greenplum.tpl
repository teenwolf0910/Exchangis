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