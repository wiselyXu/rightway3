package com.swj.qc;


import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.io.Serializable;
import java.sql.Timestamp;

@Table("framework_test_table_a")
public class OracleSelectModel implements Serializable {
    @Column(isPK = true)
    private String id;

    @Column
    private String userName;

    @Column(name = "nickName")
    private String otherName;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastUpdateTime;

    @Column
    private Integer order;

    @Column
    private Double salery;

    /*@Column
    private Boolean testBit;

    public Boolean getTestBit() {
        return testBit;
    }

    public void setTestBit(Boolean testBit) {
        this.testBit = testBit;
    }*/

    public Double getSalery() {
        return salery;
    }

    public void setSalery(Double salery) {
        this.salery = salery;
    }

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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public OracleSelectModel(String id, String userName, String otherName, Timestamp createDate, Timestamp lastUpdateTime, Integer order, Double salery) {//,Boolean testBit
        this.id = id;
        this.userName = userName;
        this.otherName = otherName;
        this.createDate = createDate;
        this.lastUpdateTime = lastUpdateTime;
        this.order = order;
        this.salery = salery;
       // this.testBit = testBit;
    }

    public OracleSelectModel(String id) {

        this.id = id;
    }

    public OracleSelectModel() {

    }
}
