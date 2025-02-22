package com.webank.wedatasphere.exchangis.datasource.core.vo;

import java.util.List;

public class ExchangisJobTransformsContent {
    private String type;
    private String sql;
    private List<ExchangisJobTransformsItem> mapping;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ExchangisJobTransformsItem> getMapping() {
        return mapping;
    }

    public void setMapping(List<ExchangisJobTransformsItem> mapping) {
        this.mapping = mapping;
    }
}
