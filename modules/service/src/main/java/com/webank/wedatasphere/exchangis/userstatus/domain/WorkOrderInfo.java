package com.webank.wedatasphere.exchangis.userstatus.domain;

/**
 * @ClassName: WorkOrderInfo
 * @Description: 工单信息
 * @author: lijw
 * @date: 2021/10/13 9:47
 */
public class WorkOrderInfo {
    private  String workOrder_id;
    private  int workOrder_type;
    private  String user_id;
    private  int cycleCnt;
    private  int cycleType;
    private  String account_id;
    private  String masterOrder_id;
    private  String serviceTag;
    private  String resource_id;
    private  String resourceType;
    private  String workerOrderConfig;
    private  String workOrderItems;
    private  String operTime;

    public String getOperTime() {
        return operTime;
    }

    public void setOperTime(String operTime) {
        this.operTime = operTime;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getMasterOrder_id() {
        return masterOrder_id;
    }

    public void setMasterOrder_id(String masterOrder_id) {
        this.masterOrder_id = masterOrder_id;
    }

    public String getServiceTag() {
        return serviceTag;
    }

    public void setServiceTag(String serviceTag) {
        this.serviceTag = serviceTag;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getWorkerOrderConfig() {
        return workerOrderConfig;
    }

    public void setWorkerOrderConfig(String workerOrderConfig) {
        this.workerOrderConfig = workerOrderConfig;
    }

    public String getWorkOrderItems() {
        return workOrderItems;
    }

    public void setWorkOrderItems(String workOrderItems) {
        this.workOrderItems = workOrderItems;
    }

    public String getWorkOrder_id() {
        return workOrder_id;
    }

    public void setWorkOrder_id(String workOrder_id) {
        this.workOrder_id = workOrder_id;
    }

    public int getWorkOrder_type() {
        return workOrder_type;
    }

    public void setWorkOrder_type(int workOrder_type) {
        this.workOrder_type = workOrder_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

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
}
