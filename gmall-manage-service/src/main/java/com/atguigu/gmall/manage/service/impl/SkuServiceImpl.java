package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        List<PmsSkuImage> skuImages = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage skuImage : skuImages) {
            skuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(skuImage);
        }
        return "success";
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        pmsSkuInfo1.setSkuImageList(pmsSkuImageMapper.select(pmsSkuImage));
        return pmsSkuInfo1;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId, String ip) {
        System.out.println("ip 为" + ip +" 用户" + Thread.currentThread().getName()+"进入商品页面请求");
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        Jedis jedis = redisUtil.getJedis();
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);
        if(StringUtils.isNotBlank(skuJson)) {
            System.out.println("ip 为" + ip +" 用户" + Thread.currentThread().getName()+"从缓存中获得请求信息");
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }
        else{
            System.out.println("缓存中不存在该信息， ip 为" + ip +" 用户" + Thread.currentThread().getName()+"申请锁" + "sku:"+skuId+":lock");
            String token = UUID.randomUUID().toString();
            String OK = jedis.set("sku:"+skuId+":lock", token,"nx", "px", 10000);
            if(StringUtils.isNotBlank(OK)&&OK.equals("OK")){
                System.out.println("ip 为" + ip +" 用户" + Thread.currentThread().getName()+ "拥有访问数据库的权限， 有效时间10秒");
                pmsSkuInfo = getSkuByIdFromDb(skuId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(pmsSkuInfo!=null){
                    jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }
                else{
                    jedis.setex("sku:"+skuId+":info", 60*3, JSON.toJSONString(""));
                }
                System.out.println("ip 为" + ip +" 用户" + Thread.currentThread().getName()+ "将锁归还"+ "sku:"+skuId+":lock");
                String lockToken = jedis.get("sku:" + skuId + ":lock");
                if(StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)){
                    jedis.del("sku:" + skuId + ":lock");
                }
            }else{
                System.out.println("ip 为" + ip +" 用户" + Thread.currentThread().getName()+ "没有拿到锁，开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId, ip);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.getSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }
}
