package com.atguigu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.SearchService;
import com.atguigu.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Controller
public class SearchController {
    @Reference
    SearchService searchService;
    @Reference
    AttrService attrService;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){
        HashSet<String> valueIdSet = new HashSet<>();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos =  searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);
        for(PmsSearchSkuInfo pmsSearchSkuInfo:pmsSearchSkuInfos){
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for(PmsSkuAttrValue pmsSkuAttrValue:skuAttrValueList){
                valueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueId(valueIdSet);
        modelMap.put("attrList", pmsBaseAttrInfos);
        String[] delValueIds = pmsSearchParam.getValueId();
        if(delValueIds!=null) {
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            for (String delValueId : delValueIds) {
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam, delValueId));
            while (iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    String valueId = pmsBaseAttrValue.getId();
                        if (valueId.equals(delValueId)) {
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            iterator.remove();
                        }
                    }
                }
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList", pmsSearchCrumbs);
        }
        String urlParam = getUrlParam(pmsSearchParam);
        modelMap.put("urlParam", urlParam);
        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){modelMap.put("keyword", keyword);}
        return "list";
    }

    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam, String delValueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String urlParam = "";
        if(keyword!=null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if(catalog3Id!=null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if(skuAttrValueList != null){
            for (String pmsSkuAttrValue : skuAttrValueList) {
                if(!pmsSkuAttrValue.equals(delValueId)){
                urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
                }
            }
        }
        return urlParam;
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String urlParam = "";
        if(keyword!=null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if(catalog3Id!=null){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if(skuAttrValueList != null){
            for (String pmsSkuAttrValue : skuAttrValueList) {
                urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
            }
        }
        return urlParam;
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
