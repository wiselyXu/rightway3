package com.swj.qc;


import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;

/**
 **/
@Table("dao_flow_detail")
public class FlowDaoModel {

    @Column(name = "flowdetailid",isPK = true)
    private String flowdetailid;

    @Column(name = "flowid")
    private String flowid;

    @Column(name="flowtypeid")
    private String flowtypeid;

    @Column(name = "amount")
    private Double amount;

    public String getFlowdetailid() {
        return flowdetailid;
    }

    public void setFlowdetailid(String flowdetailid) {
        this.flowdetailid = flowdetailid;
    }

    public String getFlowid() {
        return flowid;
    }

    public void setFlowid(String flowid) {
        this.flowid = flowid;
    }

    public String getFlowtypeid() {
        return flowtypeid;
    }

    public void setFlowtypeid(String flowtypeid) {
        this.flowtypeid = flowtypeid;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
