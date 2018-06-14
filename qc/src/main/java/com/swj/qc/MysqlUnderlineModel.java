package com.swj.qc;


import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.io.Serializable;

@Table("framework_test_table4")
public class MysqlUnderlineModel implements Serializable {
    @Column(isPK = true)
    private Integer id;

    @Column
    private String userName;

    @Column
    private String user_pwd;

    @Column
    private String resource_;

    @Column
    private String _userRole;

    @Column
    private Integer sex;

    public String getResource_() {
        return resource_;
    }

    public void setResource_(String resource_) {
        this.resource_ = resource_;
    }

    public String get_userRole() {
        return _userRole;
    }

    public void set_userRole(String _userRole) {
        this._userRole = _userRole;
    }

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

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public MysqlUnderlineModel() {
    }

    public MysqlUnderlineModel(Integer id) {
        this.id = id;
    }

    public MysqlUnderlineModel(Integer id, String userName, String user_pwd, String resource_, String _userRole, Integer sex) {
        this.id = id;
        this.userName = userName;
        this.user_pwd = user_pwd;
        this.resource_ = resource_;
        this._userRole = _userRole;
        this.sex = sex;
    }
}
