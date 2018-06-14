package com.swj.qc;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

@Table("doublepk")
public class DoublePKModel {

    @Column(isPK = true)
    private int id;
    @Column(isPK = true)
    private String code;
    @Column
    private String name;
    @Column
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DoublePKModel() {
    }

    public DoublePKModel(int id, String code, String name, String password) {

        this.id = id;
        this.code = code;
        this.name = name;
        this.password = password;
    }
}
