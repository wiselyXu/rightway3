package com.swj.qc;

/**
 * com.swj.framework.dao.model
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class EnterpriseModel {

    private static Integer accessNumber;

    private String name;

    private String address;

    public static Integer getAccessNumber() {
        return accessNumber;
    }

    public static void setAccessNumber(Integer accessNumber) {
        EnterpriseModel.accessNumber = accessNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
