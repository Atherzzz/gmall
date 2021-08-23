package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {
    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        List<OmsCartItem> omsCartItems =new ArrayList<>();
        String userId = "1";
        if(StringUtils.isNotBlank(userId)) {
            omsCartItems = cartService.cartList(userId);
        }
        else{
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }
        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }
        modelMap.put("cartList", omsCartItems);
        return "cartList";
    }

    @RequestMapping("addToCart")
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId, "");
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("1111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));
        String memberId = "1";
        if(StringUtils.isBlank(memberId)){
            //cookie 原有数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookie)) {
                omsCartItems.add(omsCartItem);
            }else{
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    //添加过， 更新数据
                    for (OmsCartItem cartItem : omsCartItems) {
                        if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }
                } else {
                    omsCartItems.add(omsCartItem);
                }
            }

            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60*60*72, true );

        }else {
            //已登录
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId, skuId);
            if(omsCartItemFromDb==null){
                //用户没插入过该商品
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname("test");
                omsCartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(omsCartItem);
            }else{
                //用户插入过该商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }
            //同步缓存
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        for (OmsCartItem cartItem : omsCartItems) {
            String productId = cartItem.getProductId();
            if(productId.equals(omsCartItem.getProductId())){
                return true;
            }
        }
        return false;
    }
}
