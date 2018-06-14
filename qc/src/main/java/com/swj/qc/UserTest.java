package com.swj.qc;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Length;
import com.swj.basic.annotation.Require;
import com.swj.basic.annotation.Table;

import java.io.Serializable;

/**
 * com.swj.service.entiry
 *
 * @DESCRIPTION
 * @AUTHOR Chenjw
 * @DATE 2018/3/12
 * @EMAIL chenjw@3vjia.com
 **/
@Table("srb_bx")
public class UserTest implements Serializable {

    @Column(isPK = true)
    private String bxid;


    @Column(isSelect = false)
    @Length(value = 4)
    private String name;

    @Column(isUpdate = false)
    private String ts;

    @Column(isDataKey = false)
    @Require
    private String ss;

    @Column(name = "aa")
    @Require
    private String os;

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserTest() {
    }

    public String getBxId() {
        return bxid;
    }

    public void setBxId(String bxId) {
        this.bxid = bxId;
    }

    public UserTest(String bxId) {
        this.bxid = bxId;
    }

    public UserTest(String bxId, String name) {
        this.bxid = bxId;
        this.name = name;
    }
}

@Table("srb_bx")
class UnPKClass
{
    private String bxid;
}

