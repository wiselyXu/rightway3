package com.swj.qc;


import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.io.Serializable;
import java.util.Date;

@Table("framework_test_table3")
public class MysqlUpdateModel implements Serializable {
    @Column(isPK = true)
    private String id;

    @Column
    private String userName;

    @Column(name = "nickName")
    private String otherName;

    @Column
    private Date createDate;

    @Column
    private Date lastUpdateTime;

    @Column
    private Integer order;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public MysqlUpdateModel(String id, String userName, String otherName, Date createDate, Date lastUpdateTime, Integer order) {
        this.id = id;
        this.userName = userName;
        this.otherName = otherName;
        this.createDate = createDate;
        this.lastUpdateTime = lastUpdateTime;
        this.order = order;
    }

    public MysqlUpdateModel(String id) {

        this.id = id;
    }

    public MysqlUpdateModel() {

    }
}
