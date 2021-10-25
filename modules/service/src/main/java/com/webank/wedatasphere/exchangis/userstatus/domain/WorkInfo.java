package com.webank.wedatasphere.exchangis.userstatus.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WorkInfo
 * @Description: 工单格式
 * @author: lijw
 * @date: 2021/10/13 9:19
 */

public class WorkInfo implements Serializable {
    private  String userId;
    private  String accountId;
    private  String workOrderId;
    private  String masterOrderId;
    private int workOrderType;
    private  String serviceTag;
    private  String resourceId;
    private  String resourceType;
    private String   workOrderConfig;
    private List<workOrderItems> workOrderItems;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getMasterOrderId() {
        return masterOrderId;
    }

    public void setMasterOrderId(String masterOrderId) {
        this.masterOrderId = masterOrderId;
    }

    public int getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(int workOrderType) {
        this.workOrderType = workOrderType;
    }

    public String getServiceTag() {
        return serviceTag;
    }

    public void setServiceTag(String serviceTag) {
        this.serviceTag = serviceTag;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getWorkOrderConfig() {
        return workOrderConfig;
    }

    public void setWorkOrderConfig(String workOrderConfig) {
        this.workOrderConfig = workOrderConfig;
    }

    public List<WorkInfo.workOrderItems> getWorkOrderItems() {
        return workOrderItems;
    }

    public void setWorkOrderItems(List<WorkInfo.workOrderItems> workOrderItems) {
        this.workOrderItems = workOrderItems;
    }

    public  static  class  workOrderItems {
        private boolean master;
        private  String resoureId;
        private  String resourceType;
        private  String masterResourceId;
        private workOrderItemConfig workOrderItemConfig;

        public boolean isMaster() {
            return master;
        }

        public void setMaster(boolean master) {
            this.master = master;
        }

        public String getResoureId() {
            return resoureId;
        }

        public void setResoureId(String resoureId) {
            this.resoureId = resoureId;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getMasterResourceId() {
            return masterResourceId;
        }

        public void setMasterResourceId(String masterResourceId) {
            this.masterResourceId = masterResourceId;
        }

        public WorkInfo.workOrderItemConfig getWorkOrderItemConfig() {
            return workOrderItemConfig;
        }

        public void setWorkOrderItemConfig(WorkInfo.workOrderItemConfig workOrderItemConfig) {
            this.workOrderItemConfig = workOrderItemConfig;
        }
    }

    public static  class  workOrderItemConfig{
        private  int cycleCnt;
        private  int cycleType;
        private  String sale_code;
        private  String product_code;

        public int getCycleCnt() {
            return cycleCnt;
        }

        public void setCycleCnt(int cycleCnt) {
            this.cycleCnt = cycleCnt;
        }

        public int getCycleType() {
            return cycleType;
        }

        public void setCycleType(int cycleType) {
            this.cycleType = cycleType;
        }

        public String getSale_code() {
            return sale_code;
        }

        public void setSale_code(String sale_code) {
            this.sale_code = sale_code;
        }

        public String getProduct_code() {
            return product_code;
        }

        public void setProduct_code(String product_code) {
            this.product_code = product_code;
        }
    }
}
