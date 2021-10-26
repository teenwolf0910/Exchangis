package com.webank.wedatasphere.exchangis.userstatus;

import com.alibaba.fastjson.JSON;
import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.userstatus.domain.UserStatusInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkInfo;
import com.webank.wedatasphere.exchangis.userstatus.domain.WorkOrderInfo;
import com.webank.wedatasphere.exchangis.userstatus.service.UserStatusService;
import groovy.util.logging.Slf4j;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: UserStatusController
 * @Description: 用户状态
 * @author: lijw
 * @date: 2021/10/13 9:28
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/exchangis")
public class UserStatusController extends ExceptionResolverContext {

    @Resource
    UserStatusService userStatusService;
    //查询用户状态
    @RequestMapping(value="/status/{userId}", method= RequestMethod.GET)
    public Response<Object> serviceStatus(@Context HttpServletRequest request, @PathVariable("userId")String userId) throws  Exception{
        Response<Object> result = new Response<>();
        result.setCode(200);
        if(userStatusService.searchUser(userId)==null){
            result.setData(0);
            result.setMessage("没有找到该用户");
            return  result;
        }
        else {
            UserStatusInfo userStatusInfo = userStatusService.searchUser(userId);
            int status = userStatusInfo.getStatus();
            if(status==1){
                result.setData(1);
                result.setMessage("用户可用");
                return  result;
            }
            else{
                result.setData(0);
                result.setMessage("用户暂不可用");
                return  result;
            }
        }
    }

    //获取用户订单信息
    @RequestMapping(value="/search/{userId}", method= RequestMethod.GET)
    public  Response<Object> searchWorkInfo(@Context HttpServletRequest request,@PathVariable("userId") String userId) throws  Exception{
        Response<Object> result = new Response<>();
        result.setCode(200);
        if(userStatusService.searchWorkInfo(userId)==null){
            result.setData(" ");
            result.setMessage("该用户暂无订单信息");
            return  result;
        }
        else {
            result.setData(userStatusService.searchWorkInfo(userId));
            result.setMessage("success");
            return  result;
        }
    }

    //处理用户工单信息
    @RequestMapping(value = "/process", method = {RequestMethod.POST})
    public Response<Object> construction(@Valid @RequestBody WorkInfo workInfo) throws  Exception {
        Response<Object> result = new Response<>();
        result.setCode(200);
        int cycleCnt=0;
        int     cycleType = 0;
        UserStatusInfo userStatusInfo=new UserStatusInfo();
        WorkOrderInfo workOrderInfo = new WorkOrderInfo();
        String userId = workInfo.getUserId();
        String accountId = workInfo.getAccountId();
        userStatusInfo.setUser_id(userId);
        userStatusInfo.setAccount_id(accountId);
        int workOrderType = workInfo.getWorkOrderType();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar=Calendar.getInstance();
        workOrderInfo.setUser_id(userId);
        workOrderInfo.setWorkOrder_id(workInfo.getWorkOrderId());
        workOrderInfo.setWorkOrder_type(workInfo.getWorkOrderType());
        if(workOrderType==1||workOrderType==2||workOrderType==5){
            cycleCnt = workInfo.getWorkOrderItems().get(0).getWorkOrderItemConfig().getCycleCnt();
            cycleType = workInfo.getWorkOrderItems().get(0).getWorkOrderItemConfig().getCycleType();
            workOrderInfo.setCycleCnt(cycleCnt);
            workOrderInfo.setCycleType(cycleType);
        }
        workOrderInfo.setAccount_id(workInfo.getAccountId());
        workOrderInfo.setMasterOrder_id(workInfo.getMasterOrderId());
        workOrderInfo.setServiceTag(workInfo.getServiceTag());
        workOrderInfo.setResource_id(workInfo.getResourceId());
        workOrderInfo.setResourceType(workInfo.getResourceType());
        workOrderInfo.setWorkerOrderConfig(workInfo.getWorkOrderConfig());
        workOrderInfo.setWorkOrderItems(JSON.toJSONString(workInfo.getWorkOrderItems().get(0)));

        //订购
        if(workOrderType==1){
            String nowTime=sdf.format(new Date());
            String endTime="";
            calendar.setTime(new Date());
            if(cycleType==3){
                calendar.add(Calendar.MONTH,cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            if(cycleType==5){
                calendar.add(Calendar.YEAR,cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            if(cycleType==6){
                calendar.add(Calendar.YEAR,2*cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            if(cycleType==7){
                calendar.add(Calendar.YEAR,3*cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            userStatusInfo.setStart_time(nowTime);
            userStatusInfo.setEnd_time(endTime);
            userStatusInfo.setUnsub_time(null);
            userStatusInfo.setStatus(1);
            if(userStatusService.searchUser(userId)!=null){
                userStatusService.updateUser(userStatusInfo);
            }
            else {
                userStatusService.addUser(userStatusInfo);
            }
            workOrderInfo.setOperTime(nowTime);
            workOrderInfo.setExpireTime(endTime);
            workOrderInfo.setStatus(1);
            userStatusService.insertWorkOrder(workOrderInfo);
            result.setData(true);
            result.setMessage("success");
            return  result;
        }
        //2.续订
        if(workOrderType==2){
            UserStatusInfo user = userStatusService.searchUser(userId);
            if(user==null){
                result.setData(false);
                result.setMessage("请先开通账号");
                return  result;
            }
            String end_time = user.getEnd_time();
            Date date = sdf.parse(end_time);
            String endTime="";
            calendar.setTime(date);
            if(cycleType==3){
                calendar.add(Calendar.MONTH,cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            if(cycleType==5){
                calendar.add(Calendar.YEAR,cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            if(cycleType==6){
                calendar.add(Calendar.YEAR,2*cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            if(cycleType==7){
                calendar.add(Calendar.YEAR,3*cycleCnt);
                Date time = calendar.getTime();
                endTime= sdf.format(time);
            }
            userStatusService.insertWorkOrder(workOrderInfo);
            userStatusService.updateWorkOrderInfo(userId,workInfo.getResourceId(),endTime,1);
            userStatusService.updateUserByUserId(userId,endTime,1);
            result.setData(true);
            result.setMessage("success");
            return  result;
        }
        //3.退订
        if(workOrderType==5){
            UserStatusInfo user = userStatusService.searchUser(userId);
            if(user==null){
                result.setData(false);
                result.setMessage("请先开通账号");
                return  result;
            }
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE,-1);
            Date time = calendar.getTime();
            String unsubTime = sdf.format(time);
            userStatusService.insertWorkOrder(workOrderInfo);
            userStatusService.unsubUserByUserId(userId,unsubTime,0);
            userStatusService.updateWorkOrderInfo(userId,workInfo.getResourceId(),unsubTime,0);
            return new Response<>().successResponse("退订成功");
        }
        if(workOrderType==6 ){
            UserStatusInfo user = userStatusService.searchUser(userId);
            if(user==null){
                result.setData(false);
                result.setMessage("请先开通账号");
                return  result;
            }
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE,-1);
            Date time = calendar.getTime();
            String nowTime = sdf.format(time);
            userStatusService.insertWorkOrder(workOrderInfo);
            userStatusService.updateUserByUserId(userId,nowTime,0);
            userStatusService.updateWorkOrderInfo(userId,workInfo.getResourceId(),nowTime,0);
            result.setData(true);
            result.setMessage("success");
            return  result;
        }
        if( workOrderType==7){
            UserStatusInfo user = userStatusService.searchUser(userId);
            if(user==null){
                result.setData(false);
                result.setMessage("请先开通账号");
                return  result;
            }
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE,-1);
            Date time = calendar.getTime();
            String nowTime = sdf.format(time);
            userStatusService.insertWorkOrder(workOrderInfo);
            userStatusService.updateUserByUserId(userId,nowTime,0);
            userStatusService.updateWorkOrderInfo(userId,workInfo.getResourceId(),nowTime,0);
            result.setData(true);
            result.setMessage("success");
            return  result;
        }
        return  null;
    }



}
