package com.swj.qc;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.sql.Timestamp;
import java.util.Date;

@Table(value = "test_date")
public class OracleDateModel {

    @Column(isPK = true)
    private Integer id;

    @Column
    private Date dateCol;

    @Column
    private Timestamp timestampCol;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateCol() {
        return dateCol;
    }

    public void setDateCol(Date dateCol) {
        this.dateCol = dateCol;
    }

    public Timestamp getTimestampCol() {
        return timestampCol;
    }

    public void setTimestampCol(Timestamp timestampCol) {
        this.timestampCol = timestampCol;
    }

    public OracleDateModel(Integer id, Date dateCol, Timestamp timestampCol) {
        this.id = id;
        this.dateCol = dateCol;
        this.timestampCol = timestampCol;
    }

    public OracleDateModel() {
    }
}
