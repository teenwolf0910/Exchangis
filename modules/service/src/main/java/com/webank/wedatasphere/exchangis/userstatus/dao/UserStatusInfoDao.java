package com.webank.wedatasphere.exchangis.userstatus.dao;

import com.webank.wedatasphere.exchangis.userstatus.domain.UserStatusInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkOrderInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: UserStatusInfoDao
 * @Description:
 * @author: lijw
 * @date: 2021/10/13 10:11
 */
@Mapper
public interface UserStatusInfoDao {
    @Insert("insert into exchangis_user_status(user_id,account_id,start_time,end_time) values(#{userStatusInfo.user_id},#{userStatusInfo.account_id},#{userStatusInfo.start_time},#{userStatusInfo.end_time}) ")
    public  void addUser(@Param("userStatusInfo") UserStatusInfo userStatusInfo);
    @Update("UPDATE exchangis_user_status SET account_id=#{userStatusInfo.account_id} , start_time=#{userStatusInfo.start_time} , end_time=#{userStatusInfo.end_time} , unsub_time=#{userStatusInfo.unsub_time} where user_id=#{userStatusInfo.user_id} ")
    public  void  updateUser(@Param("userStatusInfo")  UserStatusInfo userStatusInfo);
    @Delete("DELETE  FROM exchangis_user_status WHERE user_id=#{userStatusInfo.user_id}")
    public  void deleteUser(@Param("userStatusInfo")  UserStatusInfo userStatusInfo);
    @Select("select * from exchangis_user_status  where user_id=#{user_id}")
    public  UserStatusInfo searchUser(@Param("user_id") String user_id);
    @Update("UPDATE  exchangis_user_status set end_time=#{end_time} , unsub_time=null where user_id=#{user_id}")
    public  void  updateUserByUserId(@Param("user_id")  String user_id,@Param("end_time")  String end_time);
    @Update("UPDATE  exchangis_user_status set unsub_time=#{unsub_time} where user_id=#{user_id}")
    public  void  unsubUserByUserId(@Param("user_id")  String user_id,@Param("unsub_time")  String unsub_time);
    @Insert("insert into exchangis_work_info (" +
            "workOrder_id,workOrder_type,user_id,cycleCnt,cycleType,account_id,masterOrder_id,serviceTag,resource_id,resourceType,workerOrderConfig,workOrderItems,operTime) values(" +
            "#{workOrderInfo.workOrder_id},#{workOrderInfo.workOrder_type},#{workOrderInfo.user_id},#{workOrderInfo.cycleCnt},#{workOrderInfo.cycleType},#{workOrderInfo.account_id},#{workOrderInfo.masterOrder_id},#{workOrderInfo.serviceTag},#{workOrderInfo.resource_id},#{workOrderInfo.resourceType},#{workOrderInfo.workerOrderConfig},#{workOrderInfo.workOrderItems},#{workOrderInfo.operTime}) ")
    public  void insertWorkOrder(@Param("workOrderInfo") WorkOrderInfo workOrderInfo);

    @Select("select * from exchangis_work_info  where user_id=#{user_id}")
    public List<WorkOrderInfo> searchWorkInfo(@Param("user_id") String user_id);
}
