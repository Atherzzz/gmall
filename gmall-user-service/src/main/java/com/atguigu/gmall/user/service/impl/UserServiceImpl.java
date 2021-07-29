package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMemberList = userMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return umsMemberReceiveAddresses;
    }

    @Override
    public Integer addUser(UmsMember newUser) {
        int res = userMapper.insert(newUser);
        return res;
    }

    @Override
    public int deleteUser(String Id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(Id);
        int res = userMapper.deleteByPrimaryKey(umsMember);
        return res;
    }

    @Override
    public int updateUser(UmsMember newUser) {
        int res = userMapper.updateByPrimaryKey(newUser);
        return res;
    }

    @Override
    public int deleteAddress(String memberId) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        int res = umsMemberReceiveAddressMapper.deleteByExample(example);
        return res;
    }

    @Override
    public int addAddress(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        int res = umsMemberReceiveAddressMapper.insert(umsMemberReceiveAddress);
        return res;
    }

    @Override
    public int changeAddress(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        int res = umsMemberReceiveAddressMapper.updateByPrimaryKey(umsMemberReceiveAddress);
        return res;
    }
}
