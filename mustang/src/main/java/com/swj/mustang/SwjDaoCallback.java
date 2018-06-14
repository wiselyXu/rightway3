package com.swj.mustang;

import java.sql.SQLException;

@FunctionalInterface
public interface SwjDaoCallback<P,R> {

    public R call(P param) throws Exception;
}
