package com.swj.qc;


import com.swj.mustang.SwjDao;
import com.swj.mustang.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TransactionManagerTest {//事务中会读取到未提交的数据（脏读）

    static SwjDao swjDao = SwjDao.get("test-db");


    @BeforeClass
    public static void init() throws Exception{
        //测试表一
        CreateTable("framework_test_transaction1");
        CreateTable("framework_test_transaction2");
        CreateTable("framework_test_transaction3");
        CreateTable("framework_test_transaction4");
        CreateTable("framework_test_transaction5");
        CreateTable("framework_test_transaction6");
        insert();
    }

    public static void CreateTable(String tableName) throws Exception{
        String sql = "create table "+tableName+" ("
                + "`id` int(11) NOT NULL,"
                + "createDate datetime NOT NULL,"
                + "`sex` char(2),"
                + "userName varchar(255) NOT NULL,PRIMARY KEY (`id`))";

        int result = SwjDao.get("test-db").execute(sql, null);
        Assert.assertEquals(0, result);
    }

    public static void insert() throws Exception{
        for (int i = 1; i <= 100; i++) {
            //新增表一
            int result = add(i,1,new Date());
            Assert.assertEquals(1, result);
            //新增表二
            result = add(i,2,new Date());
            Assert.assertEquals(1, result);
            //新增表三
            result = add(i,3,new Date());
            Assert.assertEquals(1, result);
            //新增表四
            result = add(i,4,new Date());
            Assert.assertEquals(1, result);
            //新增表五
            result = add(i,5,new Date());
            Assert.assertEquals(1, result);
            //新增表六
            result = add(i,6,new Date());
            Assert.assertEquals(1, result);
        }
        assertTotalCount(100,1);
        assertTotalCount(100,2);
        assertTotalCount(100,3);
        assertTotalCount(100,4);
        assertTotalCount(100,5);
        assertTotalCount(100,6);
    }

    /**
     *
     * @param id 实体id
     * @param type 实体表
     * @param date 日期
     * @return
     */
    public static int add(int id,int type,Date... date) throws Exception{
        int result=0;
        Date d = null;
        if(date!=null && date.length>0)
        {
            d=date[0];
        }
        switch (type) {
            case 1:
                Framework_test_transaction testTransaction = new Framework_test_transaction(id, "user" + id, d, 0);
                result = swjDao.insert(testTransaction);
                break;
            case 2:
                Framework_test_transaction2 testTransaction2 = new Framework_test_transaction2(id, "user" + id, d, 0);
                result = swjDao.insert(testTransaction2);
                break;
            case 3:
                Framework_test_transaction3 testTransaction3 = new Framework_test_transaction3(id, "user" + id, d, 0);
                result = swjDao.insert(testTransaction3);
                break;
            case 4:
                Framework_test_transaction4 testTransaction4 = new Framework_test_transaction4(id, "user" + id, d, 0);
                result = swjDao.insert(testTransaction4);
                break;
            case 5:
                Framework_test_transaction5 testTransaction5 = new Framework_test_transaction5(id, "user" + id, d, 0);
                result = swjDao.insert(testTransaction5);
                break;
            case 6:
                Framework_test_transaction6 testTransaction6 = new Framework_test_transaction6(id, "user" + id, d, 0);
                result = swjDao.insert(testTransaction6);
                break;
        }
        Assert.assertEquals(1, result);
        return result;
    }

    public static void assertTotalCount(int totalCount,int type)
    {
        int resultSize=0;
        switch (type) {
            case 1:
                List<Framework_test_transaction> transactionList = swjDao.selectAll(Framework_test_transaction.class);
                resultSize = transactionList.size();
                break;
            case 2:
                List<Framework_test_transaction2> transactionList2 = swjDao.selectAll(Framework_test_transaction2.class);
                resultSize = transactionList2.size();
                break;
            case 3:
                List<Framework_test_transaction3> transactionList3 = swjDao.selectAll(Framework_test_transaction3.class);
                resultSize = transactionList3.size();
                break;
            case 4:
                List<Framework_test_transaction4> transactionList4 = swjDao.selectAll(Framework_test_transaction4.class);
                resultSize = transactionList4.size();
                break;
            case 5:
                List<Framework_test_transaction5> transactionList5 = swjDao.selectAll(Framework_test_transaction5.class);
                resultSize = transactionList5.size();
                break;
            case 6:
                List<Framework_test_transaction6> transactionList6 = swjDao.selectAll(Framework_test_transaction6.class);
                resultSize = transactionList6.size();
                break;
        }
        Assert.assertEquals(totalCount,resultSize);
    }

    public void delete(int id,Class clazz) throws Exception{
        int result = swjDao.delete(clazz,String.valueOf(id));
        Assert.assertEquals(1, result);
    }

    public Object get(int id,Class clazz){
        return swjDao.select(clazz,String.valueOf(id));
    }

    @Test
    public void TestTransaction() throws Exception {
        //单个事务测试1
        TransactionManager.open();

        add(101,1,new Date());
        Framework_test_transaction entity = (Framework_test_transaction)get(101,Framework_test_transaction.class);
        Assert.assertNotNull(entity);//这里还未提交，应该是查询不到的,但实际情况是读到了未提交的数据
        Assert.assertNotNull(entity);
        Assert.assertEquals("user101",entity.getUserName());
        Assert.assertEquals(101,entity.getId().intValue());

        TransactionManager.commit();

        entity = (Framework_test_transaction)get(101,Framework_test_transaction.class);
        Assert.assertNotNull(entity);
        Assert.assertEquals("user101",entity.getUserName());
        Assert.assertEquals(101,entity.getId().intValue());

    }

    @Test
    public void TestTransaction1() throws Exception {
        //单个事务测试2　
        TransactionManager.open();//开启事务

        add(102,2,new Date());
        Framework_test_transaction2 entity1 = (Framework_test_transaction2)get(102,Framework_test_transaction2.class);

        assertTotalCount(101,2);
        Assert.assertNotNull(entity1);
        Assert.assertEquals("user102",entity1.getUserName());
        Assert.assertEquals(102,entity1.getId().intValue());

        delete(1,Framework_test_transaction2.class);
        assertTotalCount(100,2);

        TransactionManager.commit();//提交事务

        Framework_test_transaction2 entity2 = (Framework_test_transaction2)get(1,Framework_test_transaction2.class);
        Assert.assertNull(entity2);
    }

    @Test
    public void TestTransaction2() throws SQLException {
        //单个事务测试3:新增方法失败,导致事物回滚delete方法也不会成功
        TransactionManager.open();
        try {
            add(103,1);//unSuccess
            delete(2,Framework_test_transaction.class);
            TransactionManager.commit();
        }catch (Exception e){
            TransactionManager.rollback();
        }

        Framework_test_transaction entity1 = (Framework_test_transaction)get(103,Framework_test_transaction.class);
        Framework_test_transaction entity2 = (Framework_test_transaction)get(2,Framework_test_transaction.class);

        Assert.assertNull(entity1);

        Assert.assertNotNull(entity2);
        Assert.assertEquals("user2",entity2.getUserName());
        Assert.assertEquals(0,entity2.getSex().intValue());

    }

    public void add_delete(int add_id,int delet_id,Class clazz,int type) throws Exception {
        TransactionManager.open();
        try {
            add(add_id,type,new Date());
            delete(delet_id,clazz);
            TransactionManager.commit();
        } catch (SQLException e) {
            TransactionManager.rollback();
        }
    }

    @Test
    public void TestTransaction3() throws SQLException {
        //事务嵌套1:内嵌的事务成功,外层的事务也成功
        TransactionManager.open();//外层的事务
        try {
            add_delete(110,10,Framework_test_transaction3.class,3);//内嵌的事务
            add(111,3,new Date());
            delete(11,Framework_test_transaction3.class);
            TransactionManager.commit();
        }catch (Exception e){
            TransactionManager.rollback();
            Assert.assertEquals(NullPointerException.class,e.getClass());
        }

        //预言内嵌事务
        Framework_test_transaction3 entity1 = (Framework_test_transaction3)get(110,Framework_test_transaction3.class);
        Framework_test_transaction3 entity2 = (Framework_test_transaction3)get(10,Framework_test_transaction3.class);

        Assert.assertNotNull(entity1);
        Assert.assertEquals("user110",entity1.getUserName());
        Assert.assertEquals(110,entity1.getId().intValue());
        Assert.assertNull(entity2);

        //预言外层事务
        Framework_test_transaction3 entity3 = (Framework_test_transaction3)get(111,Framework_test_transaction3.class);
        Framework_test_transaction3 entity4 = (Framework_test_transaction3)get(11,Framework_test_transaction3.class);

        Assert.assertNull(entity4);

        Assert.assertNotNull(entity3);
        Assert.assertEquals("user111",entity3.getUserName());
        Assert.assertEquals(111,entity3.getId().intValue());

    }

    @Test
    public void TestTransaction4() throws SQLException {
        //事务嵌套2:内嵌的事务成功,外层的事务失败【结果：内嵌的事务失败,外层的事务失败】
        TransactionManager.open();//开启外层的事务
        try {
            add_delete(105,5,Framework_test_transaction4.class,4);//内嵌的事务
            add(106,1);
            delete(6,Framework_test_transaction4.class);
            TransactionManager.commit();//提交事务
        }catch (Exception e){
            TransactionManager.rollback();
        }

        //预言内嵌事务
        Framework_test_transaction4 entity1 = (Framework_test_transaction4)get(105,Framework_test_transaction4.class);
        Framework_test_transaction4 entity2 = (Framework_test_transaction4)get(5,Framework_test_transaction4.class);

        Assert.assertNull(entity1);
        Assert.assertNotNull(entity2);
        Assert.assertEquals("user5",entity2.getUserName());
        Assert.assertEquals(5,entity2.getId().intValue());


        //预言外层事务
        Framework_test_transaction4 entity3 = (Framework_test_transaction4)get(106,Framework_test_transaction4.class);
        Framework_test_transaction4 entity4 = (Framework_test_transaction4)get(6,Framework_test_transaction4.class);

        Assert.assertNull(entity3);
        Assert.assertNotNull(entity4);
        Assert.assertEquals("user6",entity4.getUserName());
        Assert.assertEquals(6,entity4.getId().intValue());

    }

    @Test
    public void TestTransaction5() throws SQLException {
        //事务嵌套3(内嵌事务openNew()失败,外层事务成功【结果：内嵌的事务失败,外层的事务成功】)
        TransactionManager.open();//开启外层的事务
        try {
            add(107,1,new Date());
            delete(7,Framework_test_transaction.class);
            //打开新事务
            try {
                TransactionManager.openNew();
                add(108,1);
                TransactionManager.commit();
            }
            catch(Exception e)
            {
                TransactionManager.rollback();
            }
            TransactionManager.commit();//提交事务
        }catch (Exception e){
            TransactionManager.rollback();
        }

        //预言
        Framework_test_transaction entity1 = (Framework_test_transaction)get(108,Framework_test_transaction.class);
        Framework_test_transaction entity2 = (Framework_test_transaction)get(7,Framework_test_transaction.class);
        Framework_test_transaction entity3 = (Framework_test_transaction)get(107,Framework_test_transaction.class);

        Assert.assertNull(entity1);
        Assert.assertNull(entity2);
        Assert.assertNotNull(entity3);
        Assert.assertEquals("user107",entity3.getUserName());
        Assert.assertEquals(107,entity3.getId().intValue());

    }

    private void tt() throws Exception {
        //打开新事务　（新的事务无论是否提交成功都不会影响外层的事）
        try {
            TransactionManager.open();
            add(31,1);
            TransactionManager.commit();
        }
        catch(Exception e)
        {
            TransactionManager.rollback();
            throw e;
        }
    }

    @Test
    public void TestTransaction6() throws SQLException {//todo 不符合
        //事务嵌套4(内嵌事务open()失败,外层事务成功【结果：内嵌的事务失败,外层的事务成功】)
        TransactionManager.open();//开启外层的事务
        try {
            add(130,1,new Date());
            delete(30,Framework_test_transaction.class);
            tt();
            TransactionManager.commit();//提交事务
        }catch (Exception e){
            TransactionManager.rollback();
        }


        //预言
        Framework_test_transaction entity1 = (Framework_test_transaction)get(131,Framework_test_transaction.class);
        Framework_test_transaction entity2 = (Framework_test_transaction)get(30,Framework_test_transaction.class);
        Framework_test_transaction entity3 = (Framework_test_transaction)get(130,Framework_test_transaction.class);

        Assert.assertNull(entity1);
        Assert.assertNotNull(entity2);
        Assert.assertNull(entity3);
    }

    @Test
    public void TestTransaction7() throws SQLException {
        //事务嵌套5(内嵌事务open()成功,外层事务失败【结果：内嵌的事务失败,外层的事务失败】)
        TransactionManager.open();//开启外层的事务
        try {
            //打开新事务
            try {
                TransactionManager.open();
                add(134,1,new Date());
                TransactionManager.commit();
            }
            catch(Exception e)
            {
                TransactionManager.rollback();
            }

            add(133,1);
            delete(33,Framework_test_transaction.class);
            TransactionManager.commit();//提交事务
        }catch (Exception e){
            TransactionManager.rollback();
        }


        //预言
        Framework_test_transaction entity1 = (Framework_test_transaction)get(133,Framework_test_transaction.class);
        Framework_test_transaction entity2 = (Framework_test_transaction)get(33,Framework_test_transaction.class);
        Framework_test_transaction entity3 = (Framework_test_transaction)get(134,Framework_test_transaction.class);
        Assert.assertNull(entity1);
        Assert.assertNotNull(entity2);
        Assert.assertEquals("user33",entity2.getUserName());
        Assert.assertEquals(33,entity2.getId().intValue());
        Assert.assertNull(entity3);
    }

    @Test
    public void TestTransaction8() throws SQLException {
        //事务嵌套5(内嵌事务openNew()成功,外层事务失败【结果：内嵌的事务成功,外层的事务失败】)
        TransactionManager.open();//开启外层的事务
        try {
            //打开新事务
            try {
                TransactionManager.openNew();
                add(138,1,new Date());
                TransactionManager.commit();
            }
            catch(Exception e)
            {
                TransactionManager.rollback();
                throw e;
            }

            add(137,1);
            delete(37,Framework_test_transaction.class);
            TransactionManager.commit();//提交事务
        }catch (Exception e){
            TransactionManager.rollback();
        }


        //预言
        Framework_test_transaction entity1 = (Framework_test_transaction)get(137,Framework_test_transaction.class);
        Framework_test_transaction entity2 = (Framework_test_transaction)get(37,Framework_test_transaction.class);
        Framework_test_transaction entity3 = (Framework_test_transaction)get(138,Framework_test_transaction.class);
        Assert.assertNull(entity1);
        Assert.assertNotNull(entity2);
        Assert.assertEquals("user37",entity2.getUserName());
        Assert.assertEquals(37,entity2.getId().intValue());
        Assert.assertNotNull(entity3);
    }

    public static void DrupTable(String tableName)throws Exception{
        String sql = "drop table "+tableName;
        swjDao.execute(sql, null);
    }

    @AfterClass
    public static void destroty() throws Exception{
        DrupTable("framework_test_transaction1");
        DrupTable("framework_test_transaction2");
        DrupTable("framework_test_transaction3");
        DrupTable("framework_test_transaction4");
        DrupTable("framework_test_transaction5");
        DrupTable("framework_test_transaction6");
    }

}
