package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    Integer addUser(UmsMember newUser);

    int deleteUser(String Id);

    int updateUser(UmsMember newUser);

    int deleteAddress(String memberId);

    int addAddress(UmsMemberReceiveAddress umsMemberReceiveAddress);

    int changeAddress(UmsMemberReceiveAddress umsMemberReceiveAddress);
}
