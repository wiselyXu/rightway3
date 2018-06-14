package com.swj.qc;


import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.io.Serializable;
import java.util.Date;

@Table("framework_test_table_c")
public class OracleSelect2Model implements Serializable {
    @Column(isPK = true)
    private Integer id;

    @Column
    private String userName;

    @Column
    private Date createDate;

    @Column
    private Integer sex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public OracleSelect2Model() {
    }



    public OracleSelect2Model(Integer id) {
        this.id = id;
    }

    public OracleSelect2Model(Integer id, String userName, Date createDate, Integer sex) {
        this.id = id;
        this.userName = userName;
        this.createDate = createDate;
        this.sex = sex;
    }
}
