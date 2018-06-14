package com.swj.qc;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.sql.Blob;
import java.sql.Clob;

@Table(value = "test_lob")
public class OracleLobModel {

    @Column(isPK = true)
    private Integer id;

    @Column
    private Blob blobCol;

    @Column
    private Clob clobCol;

    @Column
    private String blobStr;

    public String getBlobStr() {
        return blobStr;
    }

    public void setBlobStr(String blobStr) {
        this.blobStr = blobStr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Blob getBlobCol() {
        return blobCol;
    }

    public void setBlobCol(Blob blobCol) {
        this.blobCol = blobCol;
    }

    public Clob getClobCol() {
        return clobCol;
    }

    public void setClobCol(Clob clobCol) {
        this.clobCol = clobCol;
    }

    public OracleLobModel() {
    }

    public OracleLobModel(Integer id, Blob blobCol, Clob clobCol, String blobStr) {
        this.id = id;
        this.blobCol = blobCol;
        this.clobCol = clobCol;
        this.blobStr = blobStr;
    }
}
