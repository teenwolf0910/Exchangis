package com.webank.wedatasphere.exchangis.userstatus.service.impl;

import com.webank.wedatasphere.exchangis.userstatus.dao.UserStatusInfoDao;
import com.webank.wedatasphere.exchangis.userstatus.domain.UserStatusInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkOrderInfo;
import com.webank.wedatasphere.exchangis.userstatus.service.UserStatusService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: UserStatusServiceImpl
 * @Description:
 * @author: lijw
 * @date: 2021/10/13 10:09
 */
@Service
public class UserStatusServiceImpl implements UserStatusService {
    @Resource
    UserStatusInfoDao userStatusInfoDao;
    @Override
    public void construction(WorkInfo workInfo) {

    }

    @Override
    public void addUser(UserStatusInfo userStatusInfo) {
        userStatusInfoDao.addUser(userStatusInfo);
    }

    @Override
    public UserStatusInfo searchUser(String user_id) {
        return userStatusInfoDao.searchUser(user_id);
    }

    @Override
    public void updateUserByUserId(String user_id, String endTime) {
        userStatusInfoDao.updateUserByUserId(user_id,endTime);
    }

    @Override
    public void unsubUserByUserId(String user_id, String unsubTime) {
        userStatusInfoDao.unsubUserByUserId(user_id,unsubTime);
    }

    @Override
    public void updateUser(UserStatusInfo userStatusInfo)
    {
        userStatusInfoDao.updateUser(userStatusInfo);
    }

    @Override
    public void deleteUser(UserStatusInfo userStatusInfo) {
        userStatusInfoDao.deleteUser(userStatusInfo);
    }

    @Override
    public void insertWorkOrder(WorkOrderInfo workOrderInfo) {
        userStatusInfoDao.insertWorkOrder(workOrderInfo);
    }

    @Override
    public List<WorkOrderInfo> searchWorkInfo(String user_id) {
        return userStatusInfoDao.searchWorkInfo(user_id);
    }
}
