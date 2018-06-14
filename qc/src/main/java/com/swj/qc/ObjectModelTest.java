package com.swj.qc;

import com.swj.basic.annotation.Length;
import com.swj.basic.annotation.Pattern;
import com.swj.basic.annotation.Range;

public class ObjectModelTest extends ParentModel{
    private static String id="initId";
    private final String Name="initName";
    private String age;

    @Pattern(errorMessage = "testPattern值不符合规则",regexp = "\\d+")
    private String testPattern;

    @Range(min = 1,max = 10)
    private int testRange;

    @Length(value = 2)
    private String testLength;

    public String getTestLength() {
        return testLength;
    }

    public void setTestLength(String testLength) {
        this.testLength = testLength;
    }

    public String getTestPattern() {
        return testPattern;
    }

    public void setTestPattern(String testPattern) {
        this.testPattern = testPattern;
    }

    public int getTestRange() {
        return testRange;
    }

    public void setTestRange(int testRange) {
        this.testRange = testRange;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        ObjectModelTest.id = id;
    }

    public String getName() {
        return Name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}


class ParentModel{
    @Pattern(errorMessage = "parentPattern值不符合规则",regexp = "\\d+")
    private String parentPattern;

    @Range(min = 1,max = 10)
    private int parentRange;

    @Length(value = 2)
    private String parentLength;

    public String getParentPattern() {
        return parentPattern;
    }

    public void setParentPattern(String parentPattern) {
        this.parentPattern = parentPattern;
    }

    public int getParentRange() {
        return parentRange;
    }

    public void setParentRange(int parentRange) {
        this.parentRange = parentRange;
    }

    public String getParentLength() {
        return parentLength;
    }

    public void setParentLength(String parentLength) {
        this.parentLength = parentLength;
    }
}