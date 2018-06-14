package com.swj.qc;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * @create 2018-06-07 11:54
 **/
@Table("framework_test_transaction5")
public class Framework_test_transaction5 implements Serializable {
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

    public Framework_test_transaction5() {
    }

    public Framework_test_transaction5(Integer id) {
        this.id = id;
    }

    public Framework_test_transaction5(Integer id, String userName, Date createDate, Integer sex) {
        this.id = id;
        this.userName = userName;
        this.createDate = createDate;
        this.sex = sex;
    }
}