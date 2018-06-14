package com.swj.qc;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Table(value = "test_date")
public class MysqlDateModel {

    @Column(isPK = true)
    private int id;
    @Column
    private Date dateCol;
    /*@Column
    private Time timeCol;*/

    @Column
    private Timestamp timestampCol;

    @Column
    private Date dateTimeCol;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateCol() {
        return dateCol;
    }

    public void setDateCol(Date dateCol) {
        this.dateCol = dateCol;
    }

    /*public Time getTimeCol() {
        return timeCol;
    }

    public void setTimeCol(Time timeCol) {
        this.timeCol = timeCol;
    }*/

    public Timestamp getTimestampCol() {
        return timestampCol;
    }

    public void setTimestampCol(Timestamp timestampCol) {
        this.timestampCol = timestampCol;
    }

    public Date getDateTimeCol() {
        return dateTimeCol;
    }

    public void setDateTimeCol(Date dateTimeCol) {
        this.dateTimeCol = dateTimeCol;
    }

    public MysqlDateModel() {
    }

    public MysqlDateModel(int id, Date dateCol, Time timeCol, Timestamp timestampCol, Date dateTimeCol) {
        this.id = id;
        this.dateCol = dateCol;
        /*this.timeCol = timeCol;*/
        this.timestampCol = timestampCol;
        this.dateTimeCol = dateTimeCol;
    }
}
