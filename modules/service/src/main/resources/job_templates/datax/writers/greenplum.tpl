{
  "name": "gpdbwriter",
  "parameter": {
    "datasource": #{datasourceId},
     "batchSize": #{batchSize|1000},
     "username": #{username},
     "password": #{password},
     "proxyHost": #{proxyHost},
     "proxyPort": #{proxyPort|0},
     "column_i": ${column},
     "column": ${sqlColumn|["*"]},
     "primaryKeys": ${primaryKeys},
     "connection": [{
       "table": [#{table}],
       "jdbcUrl": #{jdbcUrl}
     }]
   }
}