package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }
    @RequestMapping("addUser")
    @ResponseBody
    public int addUser(String id, String memberLevelId, String username, String password, String nickname, String phone, int status, Date createTime, String icon, int gender, Date birthday, String city, String job, String personalizedSignature, int sourceType, int integration, int growth, int luckeyCount, int historyIntegration){
        UmsMember newUser = new UmsMember();
        newUser.setId(id);
        newUser.setMemberLevelId(memberLevelId);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setNickname(nickname);
        newUser.setPhone(phone);
        newUser.setStatus(status);
        newUser.setCreateTime(createTime);
        newUser.setIcon(icon);
        newUser.setGender(gender);
        newUser.setBirthday(birthday);
        newUser.setCity(city);
        newUser.setJob(job);
        newUser.setPersonalizedSignature(personalizedSignature);
        newUser.setSourceType(sourceType);
        newUser.setIntegration(integration);
        newUser.setGrowth(growth);
        newUser.setLuckeyCount(luckeyCount);
        newUser.setHistoryIntegration(historyIntegration);
        int res = userService.addUser(newUser);
        return res;
    }

    @RequestMapping("deleteUser")
    @ResponseBody
    public int deleteUser(String Id){
        int res = userService.deleteUser(Id);
        return res;
    }
    @RequestMapping("updateUser")
    @ResponseBody
    public int updateUser(String id, String memberLevelId, String username, String password, String nickname, String phone, int status, Date createTime, String icon, int gender, Date birthday, String city, String job, String personalizedSignature, int sourceType, int integration, int growth, int luckeyCount, int historyIntegration){
        UmsMember newUser = new UmsMember();
        newUser.setId(id);
        newUser.setMemberLevelId(memberLevelId);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setNickname(nickname);
        newUser.setPhone(phone);
        newUser.setStatus(status);
        newUser.setCreateTime(createTime);
        newUser.setIcon(icon);
        newUser.setGender(gender);
        newUser.setBirthday(birthday);
        newUser.setCity(city);
        newUser.setJob(job);
        newUser.setPersonalizedSignature(personalizedSignature);
        newUser.setSourceType(sourceType);
        newUser.setIntegration(integration);
        newUser.setGrowth(growth);
        newUser.setLuckeyCount(luckeyCount);
        newUser.setHistoryIntegration(historyIntegration);
        int res = userService.updateUser(newUser);
        return res;
    }
    @RequestMapping("deleteAddress")
    @ResponseBody
    public int deleteAddress(String memberId){
        int res = userService.deleteAddress(memberId);
        return res;
    }
    @RequestMapping("addAddress")
    @ResponseBody
    public int addAddress(String id, String memberId, String  name, String  phoneNumber, int defaultStatus, String postCode, String province, String city, String region, String detailAddress){
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(id);
        umsMemberReceiveAddress.setMemberId(memberId);
        umsMemberReceiveAddress.setName(name);
        umsMemberReceiveAddress.setPhoneNumber(phoneNumber);
        umsMemberReceiveAddress.setDefaultStatus(defaultStatus);
        umsMemberReceiveAddress.setPostCode(postCode);
        umsMemberReceiveAddress.setProvince(province);
        umsMemberReceiveAddress.setCity(city);
        umsMemberReceiveAddress.setRegion(region);
        umsMemberReceiveAddress.setDetailAddress(detailAddress);
        int res = userService.addAddress(umsMemberReceiveAddress);
        return res;
    }
    @RequestMapping("changeAddress")
    @ResponseBody
    public int changeAddress(String id, String memberId, String  name, String  phoneNumber, int defaultStatus, String postCode, String province, String city, String region, String detailAddress){
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(id);
        umsMemberReceiveAddress.setMemberId(memberId);
        umsMemberReceiveAddress.setName(name);
        umsMemberReceiveAddress.setPhoneNumber(phoneNumber);
        umsMemberReceiveAddress.setDefaultStatus(defaultStatus);
        umsMemberReceiveAddress.setPostCode(postCode);
        umsMemberReceiveAddress.setProvince(province);
        umsMemberReceiveAddress.setCity(city);
        umsMemberReceiveAddress.setRegion(region);
        umsMemberReceiveAddress.setDetailAddress(detailAddress);
        int res = userService.changeAddress(umsMemberReceiveAddress);
        return res;
    }
    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "hello user";
    }
}
