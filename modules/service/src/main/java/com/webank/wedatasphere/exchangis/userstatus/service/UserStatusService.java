package com.webank.wedatasphere.exchangis.userstatus.service;

import com.webank.wedatasphere.exchangis.userstatus.domain.UserStatusInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkOrderInfo;

import java.util.List;


/**
 * @ClassName: UserStatusService
 * @Description: 用户状态
 * @author: lijw
 * @date: 2021/10/13 9:38
 */
public interface UserStatusService {
    public  void construction(WorkInfo workInfo);
    public  void addUser(UserStatusInfo userStatusInfo);
    public  UserStatusInfo searchUser(String user_id);
    public  void  updateUserByUserId(String user_id,String endTime);
    public  void  unsubUserByUserId(String user_id,String unsubTime);
    public  void  updateUser(UserStatusInfo userStatusInfo);
    public  void deleteUser(UserStatusInfo userStatusInfo);
    public void  insertWorkOrder(WorkOrderInfo workOrderInfo);
    public List<WorkOrderInfo> searchWorkInfo(String user_id);
}
