package com.swj.qc;

import com.swj.mustang.IllegalSqlStatementException;
import com.swj.mustang.SqlClause;
import com.swj.mustang.SqlParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class SqlParserTest {

    @Test
    public void parse() throws Exception {

        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {? or loginid=#loginid#}  {? and id=#id#} order by cc");

        Assert.assertEquals(4,clauseList.size());

        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());

        Assert.assertEquals("",clauseList.get(0).getParameter().trim());

        Assert.assertEquals("or loginid=#loginid#",clauseList.get(1).getClause().trim());
        Assert.assertEquals("loginid",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(false,clauseList.get(1).isAppendIfKeyExist());

        Assert.assertEquals("and id=#id#",clauseList.get(2).getClause().trim());
        Assert.assertEquals("id",clauseList.get(2).getParameter().trim());
        Assert.assertEquals(false,clauseList.get(2).isAppendIfKeyExist());

        Assert.assertEquals("order by cc",clauseList.get(3).getClause().trim());
        Assert.assertEquals("",clauseList.get(3).getParameter().trim());
    }

    @Test
    public void parse1() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0");
        Assert.assertEquals(1,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
    }

    @Test
    public void parse3() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 and id=#id#");
        Assert.assertEquals(1,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0 and id=#id#",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
    }

    @Test
    public void parse5(){
        try {
            List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=1 {? and id=0 }");
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),"动态表达式中必须包含动态参数名");
        }
    }

    @Test
    public void parse4() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {?? and id=#id# }");
        Assert.assertEquals(2,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("and id=#id#",clauseList.get(1).getClause().trim());
        Assert.assertEquals("id",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(true,clauseList.get(1).isAppendIfKeyExist());
    }

    @Test
    public void parse14() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {?? and id=#id_id# }");
        Assert.assertEquals(2,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("and id=#id_id#",clauseList.get(1).getClause().trim());
        Assert.assertEquals("id_id",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(true,clauseList.get(1).isAppendIfKeyExist());
    }

    @Test
    public void parse2() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {? or loginid=#loginid#}  {? and id=#id#} order by cc {?  aa=#aa#}");
        Assert.assertEquals(5,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("or loginid=#loginid#",clauseList.get(1).getClause().trim());
        Assert.assertEquals("loginid",clauseList.get(1).getParameter().trim());
        Assert.assertEquals("and id=#id#",clauseList.get(2).getClause().trim());
        Assert.assertEquals("id",clauseList.get(2).getParameter().trim());
        Assert.assertEquals("order by cc",clauseList.get(3).getClause().trim());
        Assert.assertEquals("",clauseList.get(3).getParameter().trim());
        Assert.assertEquals("aa=#aa#",clauseList.get(4).getClause().trim());
        Assert.assertEquals("aa",clauseList.get(4).getParameter().trim());
    }

    @Test
    public void parse6() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {? and id=#id# and id2=#id# }");
        Assert.assertEquals(2,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("and id=#id# and id2=#id#",clauseList.get(1).getClause().trim());
        Assert.assertEquals("id",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(false,clauseList.get(1).isAppendIfKeyExist());
    }

    @Test
    public void parse7() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {?? and id=#id1# and id2=#id1# }");
        Assert.assertEquals(2,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("and id=#id1# and id2=#id1#",clauseList.get(1).getClause().trim());
        Assert.assertEquals("id1",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(true,clauseList.get(1).isAppendIfKeyExist());
    }

    @Test
    public void parse8() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {? and id like $i1d$ and id2 like $i1d$ }");
        Assert.assertEquals(2,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("and id like $i1d$ and id2 like $i1d$",clauseList.get(1).getClause().trim());
        Assert.assertEquals("i1d",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(false,clauseList.get(1).isAppendIfKeyExist());
    }

    @Test
    public void parse9() throws Exception {
        List<SqlClause> clauseList = SqlParser.parse("select * from sys_user where 1=0 {?? and id like $i1d$ and id2 like $i1d$ }");
        Assert.assertEquals(2,clauseList.size());
        Assert.assertEquals("select * from sys_user where 1=0",clauseList.get(0).getClause().trim());
        Assert.assertEquals("",clauseList.get(0).getParameter().trim());
        Assert.assertEquals("and id like $i1d$ and id2 like $i1d$",clauseList.get(1).getClause().trim());
        Assert.assertEquals("i1d",clauseList.get(1).getParameter().trim());
        Assert.assertEquals(true,clauseList.get(1).isAppendIfKeyExist());
    }

    @Test
    public void parse10(){
        List<SqlClause> clauseList = null;
        try {
            clauseList = SqlParser.parse("select * from sys_user where 1=0 {? and id = $i1d$ }");
        } catch (Exception e) {
            Assert.assertEquals(IllegalSqlStatementException.class,e.getClass());
        }
    }

    @Test
    public void parse11(){
        List<SqlClause> clauseList = null;
        try {
            clauseList = SqlParser.parse("select * from sys_user where 1=0 {? and id in ($i1d$) and id2 like '%$i1d$%' }");
        } catch (Exception e) {
            Assert.assertEquals(IllegalSqlStatementException.class,e.getClass());
        }
    }
}