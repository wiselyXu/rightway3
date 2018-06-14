package com.swj.qc;

import com.swj.basic.PageResult;
import com.swj.basic.helper.ObjectHelper;
import com.swj.mustang.SwjDao;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FixMethodOrder(MethodSorters.JVM)
public class SwjDaoTestOracle {

    static SwjDao swjDao = SwjDao.get("test-db-oracle");
    @BeforeClass
    public static void init() throws Exception {

        //table
        String sql_oracle="create table framework_test_table_a ("
                +"lastUpdateTime timestamp ,"
                +"createDate timestamp,"
                +"id varchar(255) NOT NULL,"
                +"nickName varchar(255),"
                +"\"ORDER\" int,"
                +"\"SALERY\" number(10,2),"
               // +"\"testBit\" char(1),"
                +"userName varchar(255) NOT NULL)";
        //table3
        String sql1_oracle="create table framework_test_table_b ("
                +"lastUpdateTime timestamp ,"
                +"createDate timestamp,"
                +"id varchar(255) NOT NULL,"
                +"nickName varchar(255),"
                +"\"ORDER\" int,"
                +"userName varchar(255) NOT NULL)";
        //table2
        String sql2_oracle="create table framework_test_table_c ("
                +"id int NOT NULL PRIMARY KEY,"
                +"createDate timestamp,"
                +"\"SEX\" char(2),"
                +"\"USERNAME\" varchar(255) NOT NULL)";

        //测试下划线
        String sql3_oracle="create table framework_test_table_d ("
                +"\"ID\" int NOT NULL PRIMARY KEY,"
                +"\"USER_PWD\" varchar(255),"
                +"\"_USERROLE\" varchar(255),"
                +"\"RESOURCE_\" varchar(255),"
                +"\"SEX\" int,"
                +"userName varchar(255) NOT NULL)";

        //测试双主键
        String doublePkSql="CREATE TABLE doublepk (\n" +
                "  \"ID\" INT NOT NULL,\n" +
                "  \"CODE\" VARCHAR(45) NOT NULL,\n" +
                "  \"NAME\" VARCHAR(45) NULL,\n" +
                "  \"PASSWORD\" VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (\"ID\", \"CODE\"))";

        //测试日期
        String testDate="CREATE TABLE test_date (\n" +
                "  \"ID\" INT NOT NULL ,\n" +
                "  \"DATECOL\" DATE NULL,\n" +
                "  \"TIMESTAMPCOL\" TIMESTAMP NULL,\n" +
                "  PRIMARY KEY (\"ID\"))";

        //测试长文本类型
        String testLob="CREATE TABLE test_lob (\n" +
                "  \"ID\" INT NOT NULL ,\n" +
                "  \"BLOBCOL\" BLOB NULL,\n" +
                "  \"CLOBCOL\" CLOB NULL,\n" +
                "  PRIMARY KEY (\"ID\"))";


        int result=swjDao.execute(sql_oracle,null);
        Assert.assertEquals(0,result);

        int result1=swjDao.execute(sql1_oracle,null);
        Assert.assertEquals(0,result1);

        int result2=swjDao.execute(sql2_oracle,null);
        Assert.assertEquals(0,result2);

        int result3=swjDao.execute(sql3_oracle,null);
        Assert.assertEquals(0,result3);

        int result4=swjDao.execute(doublePkSql,null);
        Assert.assertEquals(0,result4);

        int result5=swjDao.execute(testDate,null);
        Assert.assertEquals(0,result5);

        int result6=swjDao.execute(testLob,null);
        Assert.assertEquals(0,result6);

        insert();
        insert2();
        insertBatch();
        insertDateModel();

    }

    /*@Test
    public void insertLobModel() throws IOException {
        for (int i = 1; i <= 100; i++) {
            String sql="insert into TEST_LOB values ("+i+", empty_blob(), empty_clob())";
            int result = swjDao.execute(sql,null);
            Assert.assertEquals(1,result);
        }
        System.out.println();
        List<OracleLobModel> list = swjDao.selectAll(OracleLobModel.class);

        Assert.assertEquals(100,list.size());
    }*/

    public static void insertDateModel() throws ParseException, SQLException {//todo oracle无法插入date类型的值
        List<OracleDateModel> list = new ArrayList();

        for (int i = 1; i <= 100; i++) {
            OracleDateModel dateModel = new OracleDateModel();
            dateModel.setId(i);
            //dateModel.setDateCol((Date)ObjectHelper.convertToType(Date.class,"2018-05-29 12:53:10"));
            dateModel.setDateCol(java.sql.Date.valueOf("2018-05-29"));
            dateModel.setTimestampCol(Timestamp.valueOf("2018-05-29 12:53:10"));
            list.add(dateModel);
        }

        int result = swjDao.insertBatch(list);
        Assert.assertNotNull(result);
        Assert.assertEquals(0,result);

        List<OracleDateModel> afterList=swjDao.selectAll(OracleDateModel.class);
        Assert.assertEquals(100,afterList.size());
    }

    //批插入
    public static void insertBatch() throws SQLException {
        List<DoublePKModel> list=new ArrayList();
        for (int i = 1; i <= 100; i++) {
            DoublePKModel model = new DoublePKModel(i,"code"+i,"user"+i,"123");
            list.add(new DoublePKModel(i,"code"+i,"user"+i,"123"));
        }
        int result=swjDao.insertBatch(list);
        Assert.assertNotNull(result);
        //Assert.assertEquals(100,result.length);

        List<DoublePKModel> afterList=swjDao.selectAll(DoublePKModel.class);
        Assert.assertEquals(100,afterList.size());

    }

    public static void insert() throws Exception {
        DecimalFormat df=new DecimalFormat("0000");
        OracleSelectModel testTable=new OracleSelectModel();
        OracleUpdateModel testTableUpdate=new OracleUpdateModel();
        for (int i = 1; i <= 100; i++) {
            testTable.setId("U"+df.format(i));
            testTable.setUserName("user"+i);

            testTableUpdate.setId("U"+df.format(i));
            testTableUpdate.setUserName("user"+i);
            if (i%2==0){
                testTable.setOtherName("a_othName"+i);
                testTableUpdate.setOtherName("a_othName"+i);
            }else{
                testTable.setOtherName("othName"+i);
                testTableUpdate.setOtherName("othName"+i);
            }
            testTable.setOrder(100-i);
            testTable.setCreateDate(new Timestamp(System.currentTimeMillis()));
            testTable.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            testTable.setSalery(1000.50);
           // testTable.setTestBit(true);

            testTableUpdate.setOrder(100-i);
            testTableUpdate.setCreateDate(new Timestamp(System.currentTimeMillis()));
            testTableUpdate.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            int result=swjDao.insert(testTable);
            int result1=swjDao.insert(testTableUpdate);
            Assert.assertEquals(1,result);
           // Assert.assertEquals(1,result1);
        }
    }

    public static void insert2() throws  Exception{
        OracleSelect2Model testTable=new OracleSelect2Model();
        OracleUnderlineModel testTable4=new OracleUnderlineModel();
        for (int i = 1; i <= 100; i++) {
            testTable.setId(i);
            testTable.setUserName("user"+i);
            testTable.setCreateDate(new Timestamp(System.currentTimeMillis()));
            testTable.setSex(0);
            int result=swjDao.insert(testTable);
            Assert.assertEquals(1,result);

            testTable4.setId(i);
            testTable4.setUserName("user"+i);
            testTable4.setUser_pwd("123");
            testTable4.setSex(0);
            testTable4.set_userRole("admin");
            testTable4.setResource_("resource1");
            result=swjDao.insert(testTable4);
            Assert.assertEquals(1,result);
        }
    }

    @Test
    public void queryEntity() {
        List<OracleSelectModel> list=swjDao.selectAll(OracleSelectModel.class);
        //查询单条记录（实体）
        OracleSelectModel entity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id='U0001'",null);

        Map map= ObjectHelper.getParameter("id","U0002");
        OracleSelectModel entity2 = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",map);

        Assert.assertEquals("U0001",entity.getId());
        Assert.assertEquals("user1",entity.getUserName());
        Assert.assertEquals("othName1",entity.getOtherName());
        Assert.assertEquals(99,entity.getOrder().intValue());
        Assert.assertTrue(1000.50==entity.getSalery().doubleValue());
        //Assert.assertTrue(entity.getTestBit());

        Assert.assertEquals("U0002",entity2.getId());
    }

    @Test
    public void queryEntities() {
        //查询多条记录（实体）,无排序
        List<OracleSelectModel> list = swjDao.queryEntities(OracleSelectModel.class,"select * from framework_test_table_a",null);

        //查询多条记录（实体）,排序
        List<OracleSelectModel> list2 = swjDao.queryEntities(OracleSelectModel.class,"select * from framework_test_table_a","order by username desc",null);

        //查询多条记录（实体）,排序参数为空
        List<OracleSelectModel> list3 = swjDao.queryEntities(OracleSelectModel.class,"select * from framework_test_table_a",null,null);


        Assert.assertEquals(100,list.size());
        Assert.assertEquals(100,list2.size());
        Assert.assertEquals(100,list3.size());

        Assert.assertEquals(99+"",list.get(0).getOrder()+"");
        Assert.assertEquals(98+"",list.get(1).getOrder()+"");

        Assert.assertEquals(1+"",list2.get(0).getOrder()+"");
        Assert.assertEquals(2+"",list2.get(1).getOrder()+"");
    }

    @Test
    public void selectAll() {
        List<OracleSelectModel> list = swjDao.selectAll(OracleSelectModel.class);
        Assert.assertEquals(100,list.size());
    }

    @Test
    public void select() {
        OracleSelectModel entity = swjDao.select(OracleSelectModel.class,"U0001");
        Assert.assertEquals("U0001",entity.getId());
        Assert.assertEquals("user1",entity.getUserName());
        Assert.assertEquals("99",entity.getOrder()+"");
    }

    @Test
    public void queryEntitiesByPage() {
        //查询多条记录（实体）,排序
        PageResult pageResult = swjDao.queryEntitiesByPage(OracleSelectModel.class,"select * from framework_test_table_a","ORDER BY \"ORDER\" desc",1,10,null);
        OracleSelectModel entity=(OracleSelectModel)pageResult.getResult().get(0);
        Assert.assertEquals(10,pageResult.getResult().size());
        Assert.assertEquals(99,entity.getOrder().intValue());
        Assert.assertEquals(100,pageResult.getRecordCount());
    }

    @Test
    public void queryMap() {
        Map map= ObjectHelper.getParameter("id","U0001");
        Map<String,Object> maps = swjDao.queryMap("select * from framework_test_table_a where id=#id#",map);
        Assert.assertTrue(maps.containsKey("lastupdatetime"));
        Assert.assertTrue(maps.containsKey("nickname"));
        Assert.assertTrue(maps.containsKey("salery"));
        Assert.assertTrue(maps.containsKey("createdate"));
        Assert.assertTrue(maps.containsKey("id"));
        Assert.assertTrue(maps.containsKey("order"));
        Assert.assertTrue(maps.containsKey("username"));

    }

    @Test
    public void queryMaps() {
        Map map= ObjectHelper.getParameter("id","U0001");
        List<Map<String,Object>> maps = swjDao.queryMaps("select * from framework_test_table_a where id=#id#",map);
        Assert.assertEquals(1,maps.size());
    }

    @Test
    public void queryMapsByPage2() {//orcle
        PageResult pageResult = swjDao.queryMapsByPage("select * from framework_test_table_a","ID",1,10,null);
        Assert.assertEquals(10,pageResult.getResult().size());
    }

    @Test
    public void queryScalar() {
        Map map= ObjectHelper.getParameter("id","U0001");
        String result= swjDao.queryScalar("select userName from framework_test_table_a where id=#id#",map,String.class);
        Assert.assertEquals("user1",result);
    }

    @Test
    public void queryScalarList() {
        List<String> list= swjDao.queryScalarList("select userName from framework_test_table_a",null,String.class);
        Assert.assertEquals(100,list.size());
        Assert.assertEquals("user1",list.get(0));
        Assert.assertEquals("user2",list.get(1));
    }

    @Test
    public void update() throws Exception {
        OracleUpdateModel entity = swjDao.queryEntity(OracleUpdateModel.class,"select * from framework_test_table_b where id=#id#",ObjectHelper.getParameter("id","U0003"));
        entity.setUserName("testUpdate");
        entity.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
        int result= swjDao.update(entity);
        Assert.assertEquals(1,result);
        OracleUpdateModel entity2 = swjDao.queryEntity(OracleUpdateModel.class,"select * from framework_test_table_b where id=#id#",ObjectHelper.getParameter("id","U0003"));
        Assert.assertEquals("testUpdate",entity2.getUserName());
    }

    @Test
    public void updateFiles1() throws Exception {//true
        OracleSelectModel oldEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0004"));

        String oldNickName=oldEntity.getOtherName();
        oldEntity.setUserName("testUpdateFiles");
        oldEntity.setOtherName("nickName4");

        int result= swjDao.updateFields(oldEntity,true, "userName");
        //获取修改后的记录
        OracleSelectModel newEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0004"));
        //未进行修改的记录
        OracleSelectModel noUpdateEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0014"));
        Assert.assertEquals(1,result);
        Assert.assertEquals("testUpdateFiles",newEntity.getUserName());
        Assert.assertEquals(oldNickName,newEntity.getOtherName());
        Assert.assertEquals("user14",noUpdateEntity.getUserName());

    }
    @Test
    public void updateFiles2() throws Exception {//model为空
        int result= swjDao.updateFields(null,true, "userName");
        Assert.assertEquals(0,result);
    }

    @Test
    public void updateFiles3() throws Exception {//fileds为空
        OracleSelectModel oldEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0004"));

        oldEntity.setUserName("testUpdateFiles");
        oldEntity.setOtherName("nickName4");

        int result= swjDao.updateFields(oldEntity,true, null);
        //获取修改后的记录
        OracleSelectModel newEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0004"));
        //未进行修改的记录
        OracleSelectModel noUpdateEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0014"));
        Assert.assertEquals(1,result);
        Assert.assertEquals("testUpdateFiles",newEntity.getUserName());
        Assert.assertEquals("nickName4",newEntity.getOtherName());
        Assert.assertEquals("user14",noUpdateEntity.getUserName());

    }
    @Test
    public void updateFiles() throws Exception {//false
        OracleSelectModel oldEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0007"));

        String oldNickName=oldEntity.getOtherName();

        oldEntity.setUserName("testUpdateFiles");
        oldEntity.setOtherName("nickName7");

        int result= swjDao.updateFields(oldEntity,false, "userName","id");
        //获取修改后的记录
        OracleSelectModel newEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0007"));
        //未进行修改的记录
        OracleSelectModel noUpdateEntity = swjDao.queryEntity(OracleSelectModel.class,"select * from framework_test_table_a where id=#id#",ObjectHelper.getParameter("id","U0014"));
        Assert.assertEquals(1,result);
        Assert.assertNotEquals("testUpdateFiles",newEntity.getUserName());
        Assert.assertEquals("nickName7",newEntity.getOtherName());
        Assert.assertEquals("user14",noUpdateEntity.getUserName());
    }

    @Test
    public void delete() throws Exception {

        int result= swjDao.delete(OracleUpdateModel.class,"U0005");
        Assert.assertEquals(1,result);
    }

    @Test
    public void get(){
       SwjDao swjDaotest2=SwjDao.get();
    }

    @Test
    public void testSqlException(){//todo 应该报错却没报错
        try {
            List<OracleSelectModel> list = swjDao.queryEntities(OracleSelectModel.class,"select * form framework_test_table_a",null);
        }catch (Exception e){
            System.out.println(e.getMessage());
           // Assert.assertEquals();
        }
    }

    @Test
    public void SqlStatement(){
        String [] ids={"U0001","U0002","U0003","U0004"};
        Map map=ObjectHelper.getParameter("id",ids);
        List<OracleSelectModel> list = swjDao.queryEntities(OracleSelectModel.class,"oracle.getStatement1",map);
        Assert.assertEquals(4,list.size());
        Assert.assertEquals("U0001",list.get(0).getId());
    }

    @Test
    public void SqlStatement2(){
        Map map=ObjectHelper.getParameter("userName","9");
        List<OracleSelectModel> list = swjDao.queryEntities(OracleSelectModel.class,"oracle.getStatement2",map);
        Assert.assertEquals(19,list.size());
    }

    @Test
    public void SqlStatement3(){

        List<String> listParam=new ArrayList();
        listParam.add("U0021");
        listParam.add("U0022");
        listParam.add("U0023");
        listParam.add("U0024");
        Map map=ObjectHelper.getParameter("id",listParam);
        List<OracleSelectModel> list = swjDao.queryEntities(OracleSelectModel.class,"oracle.getStatement1",map);
        Assert.assertEquals(4,list.size());
        Assert.assertEquals("U0021",list.get(0).getId());
    }

    @Test
    public void testIsNull() throws Exception {
        OracleUpdateModel testTable=new OracleUpdateModel();
            testTable.setId("NameIsNull");
            testTable.setUserName("user");
            testTable.setOtherName(null);
            testTable.setOrder(200);
            testTable.setCreateDate(new Timestamp(System.currentTimeMillis()));
            testTable.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            int result=swjDao.insert(testTable);
            Assert.assertEquals(1,result);
            List<OracleUpdateModel> liste = swjDao.queryEntities(OracleUpdateModel.class,"select * from framework_test_table_b",null);
            List<OracleUpdateModel> list = swjDao.queryEntities(OracleUpdateModel.class,"oracle.getStatement3",ObjectHelper.getParameter("otherName",null));
            Assert.assertEquals(1,list.size());
            Assert.assertEquals("NameIsNull",testTable.getId());
            Assert.assertEquals("user",testTable.getUserName());
            Assert.assertEquals(200,testTable.getOrder().intValue());
    }

    /**
     * 测试下划线用例
     */
    @Test
    public void testUnderline(){
        //字段名有下划线，
        List<OracleUnderlineModel> list=swjDao.selectAll(OracleUnderlineModel.class);
        Assert.assertEquals(100,list.size());
    }

    @Test
    public void testUnderline1() throws Exception {
        //传带有下划线的参数
        OracleUnderlineModel addEntity = new OracleUnderlineModel();
        addEntity.setId(101);
        addEntity.setUserName("addName1");
        addEntity.setResource_("resource_");
        addEntity.set_userRole("_super");
        addEntity.setUser_pwd("pwd_123");
        addEntity.setSex(1);
        int result = swjDao.insert(addEntity);
        Assert.assertEquals(1,result);

        //查询已新增的数据并进行
        OracleUnderlineModel seletEtity = swjDao.select(OracleUnderlineModel.class,101);
        Assert.assertNotNull(seletEtity);
        Assert.assertEquals(101,seletEtity.getId().intValue());
        Assert.assertEquals("addName1",seletEtity.getUserName());
        Assert.assertEquals("resource_",seletEtity.getResource_());
        Assert.assertEquals("_super",seletEtity.get_userRole());
        Assert.assertEquals("pwd_123",seletEtity.getUser_pwd());
        Assert.assertEquals(1,seletEtity.getSex().intValue());

        OracleUnderlineModel noUpdateEtity = swjDao.select(OracleUnderlineModel.class,1);
        Assert.assertNotNull(noUpdateEtity);
        Assert.assertEquals(1,noUpdateEtity.getId().intValue());
        Assert.assertEquals("user1",noUpdateEtity.getUserName());
        Assert.assertEquals("resource1",noUpdateEtity.getResource_());
        Assert.assertEquals("admin",noUpdateEtity.get_userRole());
        Assert.assertEquals("123",noUpdateEtity.getUser_pwd());
        Assert.assertEquals(0,noUpdateEtity.getSex().intValue());
    }

    //批修改
    @Test
    public void doublePk_batch_uadate() throws SQLException {

        List<DoublePKModel> listParams = new ArrayList();
        for (int i = 10; i < 20; i++) {
            DoublePKModel singleEntity = swjDao.select(DoublePKModel.class,ObjectHelper.getParameter("id",i,"code","code"+i));
            singleEntity.setName("updateName"+i);
            singleEntity.setPassword("updatePwd"+i);
            listParams.add(singleEntity);
        }
        int result = swjDao.updateBatch(listParams);
        Assert.assertNotNull(result);
        //Assert.assertEquals(10,result.length);
        //查询已修改的数据看是否修改成功
        for (int i = 10; i < 20; i++) {
            DoublePKModel selectEntity = swjDao.select(DoublePKModel.class,ObjectHelper.getParameter("id",i,"code","code"+i));
            Assert.assertNotNull(selectEntity);
            Assert.assertEquals(i,selectEntity.getId());
            Assert.assertEquals("code"+i,selectEntity.getCode());
            Assert.assertEquals("updateName"+i,selectEntity.getName());
            Assert.assertEquals("updatePwd"+i,selectEntity.getPassword());
        }
        DoublePKModel noUpdateEntity = swjDao.select(DoublePKModel.class,ObjectHelper.getParameter("id",2,"code","code2"));
        Assert.assertNotNull(noUpdateEntity);
        Assert.assertEquals(2,noUpdateEntity.getId());
        Assert.assertEquals("code2",noUpdateEntity.getCode());
        Assert.assertEquals("user2",noUpdateEntity.getName());
        Assert.assertEquals("123",noUpdateEntity.getPassword());
    }

    /**
     * 批量更新实体的部分字段（更新部分字段）
     * @date 2018/06/08
     */
    @Test
    public void updateFieldsBatch() throws Exception {
        List<OracleUpdateModel> trueList=new ArrayList();
        List<OracleUpdateModel> falseList=new ArrayList();
        Timestamp date = Timestamp.valueOf("2017-06-08 08:16:24");
        PageResult pageResult = swjDao.queryEntitiesByPage(OracleUpdateModel.class,"select * from framework_test_table_b","id",50,10,null);
        for (int i = 0; i < pageResult.getResult().size(); i++) {
            OracleUpdateModel model = (OracleUpdateModel)pageResult.getResult().get(i);
            model.setUserName("testUpdateFieldsBatch");
            model.setLastUpdateTime(date);
            model.setOtherName("testUpdateFieldsBatch1");
            if(i<5){
                trueList.add(model);
            }else{
                falseList.add(model);
            }
        }
        //true
        int trueResult = swjDao.updateFieldsBatch(trueList,true,"userName","lastUpdateTime");
        //false
        int falseResult = swjDao.updateFieldsBatch(trueList,false,"userName","lastUpdateTime");

        pageResult = swjDao.queryEntitiesByPage(OracleUpdateModel.class,"select * from framework_test_table_b","id",50,10,null);
        for (int i = 50; i < pageResult.getResult().size(); i++) {
            OracleUpdateModel updateModel = (OracleUpdateModel)pageResult.getResult().get(i);
            if(i<55){//true
                Assert.assertEquals("testUpdateFieldsBatch",updateModel.getUserName());
                Assert.assertEquals(date,updateModel.getLastUpdateTime());
                Assert.assertEquals("a_othName"+i,updateModel.getOtherName());
            }else{//false
                Assert.assertEquals("user"+i,updateModel.getUserName());
                Assert.assertFalse(date==updateModel.getLastUpdateTime());
                Assert.assertEquals("testUpdateFieldsBatch1",updateModel.getOtherName());
            }

        }

        OracleUpdateModel unUpdateModel = swjDao.select(OracleUpdateModel.class,ObjectHelper.getParameter("id","U0070"));
        Assert.assertNotNull(unUpdateModel);
        Assert.assertEquals("user70",unUpdateModel.getUserName());
        Assert.assertEquals("a_othName70",unUpdateModel.getOtherName());

    }

    @AfterClass
    public static void destroty() throws Exception {
        String sql="drop table framework_test_table_a";
        String sql1="drop table framework_test_table_b";
        String sql2="drop table framework_test_table_c";
        String sql3="drop table framework_test_table_d";
        String doublePkSql="drop table doublePk";
        String testDate="drop table test_date";
        String testLob="drop table test_lob";

        int result=swjDao.execute(sql,null);
        int result1=swjDao.execute(sql1,null);
        int result2=swjDao.execute(sql2,null);
        int result3=swjDao.execute(sql3,null);
        int result4=swjDao.execute(doublePkSql,null);
        int result5=swjDao.execute(testDate,null);
        int result6=swjDao.execute(testLob,null);

    }


}