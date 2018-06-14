package com.swj.mustang;

public class IllegalSqlStatementException extends Exception {

    public IllegalSqlStatementException(){
        super();
    }

    public IllegalSqlStatementException(String message){
        super(message);
    }

}
