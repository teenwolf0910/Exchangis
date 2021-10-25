package com.webank.wedatasphere.exchangis.userstatus.domain;

/**
 * @ClassName: UserStatusInfo
 * @Description: 用户信息表
 * @author: lijw
 * @date: 2021/10/13 9:45
 */
public class UserStatusInfo {
    private String user_id;
    private  String account_id;
    private  String start_time;
    private  String end_time;
    private  String unsub_time;
    private  int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUnsub_time() {
        return unsub_time;
    }

    public void setUnsub_time(String unsub_time) {
        this.unsub_time = unsub_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
