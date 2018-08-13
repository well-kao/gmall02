package com.gy.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;

public class BaseAttrValue implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 获取mysql自动增长的主键信息
    private String id;
    @Column
    private String valueName;

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    @Column

    private String attrId;

    //getAttrList(List<String> attrValueIdList)方法实现后，还有一个问题就是，
    // 点击属性时，要把上次查询的内容也带上，即带上历史参数。
    @Transient
    private String urlParam;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }
}

