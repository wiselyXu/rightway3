package com.swj.qc;

import com.swj.basic.PageResult;
import com.swj.basic.helper.ObjectHelper;
import com.swj.mustang.SwjDao;
import com.swj.mustang.TransactionManager;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@FixMethodOrder(MethodSorters.JVM)
public class SwjDaoTest {

    static SwjDao swjDao = SwjDao.get("test-db");

    @BeforeClass
    public static void init() throws Exception {
        String sql = "create table framework_test_table ("
                + "`lastUpdateTime` datetime ,"
                + "createDate datetime,"
                + "`id` varchar(255) NOT NULL,"
                + "`nickName` varchar(255),"
                + "`order` int(11),"
                + "`salery` decimal(10,2),"
                + "`testBit` BIT,"
                + "userName varchar(255) NOT NULL)";

        String sql1 = "create table framework_test_table3 ("
                + "`lastUpdateTime` datetime ,"
                + "createDate datetime,"
                + "`id` varchar(255) NOT NULL,"
                + "`nickName` varchar(255),"
                + "`order` int(11),"
                + "userName varchar(255) NOT NULL)";

        String sql2 = "create table framework_test_table2 ("
                + "`id` int(11) NOT NULL AUTO_INCREMENT,"
                + "createDate datetime,"
                + "`sex` char(2),"
                + "userName varchar(255) NOT NULL,PRIMARY KEY (`id`))";
        //测试下划线
        String sql3 = "create table framework_test_table4 ("
                + "`id` int(11) NOT NULL AUTO_INCREMENT,"
                + "user_pwd varchar(255),"
                + "_userRole varchar(255),"
                + "resource_ varchar(255),"
                + "`sex` char(2),"
                + "userName varchar(255) NOT NULL,PRIMARY KEY (`id`))";

        //测试双主键
        String doublePkSql = "CREATE TABLE `doublepk` (\n" +
                "  `id` INT NOT NULL,\n" +
                "  `code` VARCHAR(45) NOT NULL,\n" +
                "  `name` VARCHAR(45) NULL,\n" +
                "  `password` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`id`, `code`))";

        //测试日期
        String testDate = "CREATE TABLE `test_date` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `dateCol` DATE NULL,\n" +
                //"  `timeCol` TIME NULL,\n" +
                //"  `yearCol` YEAR NULL,\n" +
                "  `timeStampCol` TIMESTAMP NULL,\n" +
                "  `dateTimeCol` DATETIME NULL,\n" +
                "  PRIMARY KEY (`id`))";

        int result = swjDao.execute(sql, null);
        Assert.assertEquals(0, result);

        int result1 = swjDao.execute(sql1, null);
        Assert.assertEquals(0, result1);

        int result2 = swjDao.execute(sql2, null);
        Assert.assertEquals(0, result2);

        int result3 = swjDao.execute(sql3, null);
        Assert.assertEquals(0, result3);

        int result4 = swjDao.execute(doublePkSql, null);
        Assert.assertEquals(0, result4);

        int result5 = swjDao.execute(testDate, null);
        Assert.assertEquals(0, result5);

        insert();
        insert2();
        insertBatch();
        insertDateModel();
    }

    public static void insertDateModel() throws Exception {
        List<MysqlDateModel> list = new ArrayList();

        for (int i = 1; i <= 100; i++) {
            MysqlDateModel dateModel = new MysqlDateModel();

            Date date = (Date) ObjectHelper.convertToType(Date.class, "2018-05-29 12:53:10");
            Timestamp timestamp = Timestamp.valueOf("2018-05-29 12:53:10");

            dateModel.setDateCol(date);
            dateModel.setTimestampCol(timestamp);
            list.add(dateModel);
        }

        int result = swjDao.insertBatch(list);
        Assert.assertNotNull(result);
        Assert.assertEquals(100, result);

        List<MysqlDateModel> afterList = swjDao.selectAll(MysqlDateModel.class);
        Assert.assertEquals(100, afterList.size());
    }

    //批插入
    public static void insertBatch() throws Exception{
        List<DoublePKModel> list = new ArrayList();
        for (int i = 1; i <= 100; i++) {
            DoublePKModel model = new DoublePKModel(i, "code" + i, "user" + i, "1232");
            list.add(new DoublePKModel(i, "code" + i, "user" + i, "1232"));
        }
        int result = swjDao.insertBatch(list);
        Assert.assertNotNull(result);
        Assert.assertEquals(100, result);

        List<DoublePKModel> afterList = swjDao.selectAll(DoublePKModel.class);
        Assert.assertEquals(100, afterList.size());

    }

    public static void insert() throws Exception {
        DecimalFormat df = new DecimalFormat("0000");
        MysqlSelectModel testTable = new MysqlSelectModel();
        MysqlUpdateModel testTableUpdate = new MysqlUpdateModel();
        for (int i = 1; i <= 100; i++) {
            testTable.setId("U" + df.format(i));
            testTable.setUserName("user" + i);

            testTableUpdate.setId("U" + df.format(i));
            testTableUpdate.setUserName("user" + i);
            if (i % 2 == 0) {
                testTable.setOtherName("a_othName" + i);
                testTableUpdate.setOtherName("a_othName" + i);
            } else {
                testTable.setOtherName("othName" + i);
                testTableUpdate.setOtherName("othName" + i);
            }
            testTable.setOrder(100 - i);
            testTable.setCreateDate((Date) ObjectHelper.convertToType(Date.class, "2018-05-18 10:25:10"));
            testTable.setLastUpdateTime((Date) ObjectHelper.convertToType(Date.class, "2018-05-18 10:25:10"));
            testTable.setSalery(1000.50);
            testTable.setTestBit(true);

            testTableUpdate.setOrder(100 - i);
            testTableUpdate.setCreateDate((Date) ObjectHelper.convertToType(Date.class, "2018-05-18 10:25:10"));
            testTableUpdate.setLastUpdateTime((Date) ObjectHelper.convertToType(Date.class, "2018-05-18 10:25:10"));
            int result = swjDao.insert(testTable);
            int result1 = swjDao.insert(testTableUpdate);
            Assert.assertEquals(1, result);
            Assert.assertEquals(1, result1);
        }
        List<MysqlSelectModel> selectModelList = swjDao.selectAll(MysqlSelectModel.class);
        List<MysqlUpdateModel> updateModelList = swjDao.selectAll(MysqlUpdateModel.class);
        Assert.assertEquals(100, selectModelList.size());
        Assert.assertEquals(100, updateModelList.size());
    }

    public static void insert2() throws Exception{
        MysqlSelect2Model testTable = new MysqlSelect2Model();
        MysqlUnderlineModel testTable4 = new MysqlUnderlineModel();
        for (int i = 1; i <= 100; i++) {
            testTable.setUserName("user" + i);
            testTable.setCreateDate(new Date());
            testTable.setSex(0);
            int result = swjDao.insert(testTable);
            Assert.assertEquals(1, result);

            testTable4.setUserName("user" + i);
            testTable4.setUser_pwd("123");
            testTable4.setSex(0);
            testTable4.set_userRole("admin");
            testTable4.setResource_("resource1");
            result = swjDao.insert(testTable4);
            Assert.assertEquals(1, result);

        }
    }

    @Test
    public void doublePk_select() {
        Map map = ObjectHelper.getParameter("id", 1, "code", "code1");
        DoublePKModel singleEntity = swjDao.select(DoublePKModel.class, map);
        Assert.assertNotNull(singleEntity);
        Assert.assertEquals(1, singleEntity.getId());
        Assert.assertEquals("code1", singleEntity.getCode());
        Assert.assertEquals("user1", singleEntity.getName());
        Assert.assertEquals("1232", singleEntity.getPassword());
    }

    //下划线，批量修改
    @Test
    public void doublePk_batch_uadate() throws Exception{

        List<DoublePKModel> listParams = new ArrayList();
        for (int i = 10; i < 20; i++) {
            DoublePKModel singleEntity = swjDao.select(DoublePKModel.class, ObjectHelper.getParameter("id", i, "code", "code" + i));
            singleEntity.setName("updateName" + i);
            singleEntity.setPassword("updatePwd" + i);
            listParams.add(singleEntity);
        }
        int result = swjDao.updateBatch(listParams);
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result);
        //查询已修改的数据看是否修改成功
        for (int i = 10; i < 20; i++) {
            DoublePKModel selectEntity = swjDao.select(DoublePKModel.class, ObjectHelper.getParameter("id", i, "code", "code" + i));
            Assert.assertNotNull(selectEntity);
            Assert.assertEquals(i, selectEntity.getId());
            Assert.assertEquals("code" + i, selectEntity.getCode());
            Assert.assertEquals("updateName" + i, selectEntity.getName());
            Assert.assertEquals("updatePwd" + i, selectEntity.getPassword());
        }
        DoublePKModel noUpdateEntity = swjDao.select(DoublePKModel.class, ObjectHelper.getParameter("id", 2, "code", "code2"));
        Assert.assertNotNull(noUpdateEntity);
        Assert.assertEquals(2, noUpdateEntity.getId());
        Assert.assertEquals("code2", noUpdateEntity.getCode());
        Assert.assertEquals("user2", noUpdateEntity.getName());
        Assert.assertEquals("1232", noUpdateEntity.getPassword());
    }

    @Test
    public void queryEntity() throws Exception {
        //查询单条记录（实体）
        MysqlSelectModel entity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id='U0001'", null);

        Map map = ObjectHelper.getParameter("id", "U0002");
        MysqlSelectModel entity2 = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", map);

        Assert.assertEquals("U0001", entity.getId());
        Assert.assertEquals("user1", entity.getUserName());
        Assert.assertEquals("othName1", entity.getOtherName());
        Assert.assertEquals(99, entity.getOrder().intValue());
        Assert.assertTrue(1000.50 == entity.getSalery().doubleValue());
        Assert.assertTrue(entity.getTestBit());
        Assert.assertEquals(ObjectHelper.convertToType(Date.class, "2018-05-18"), entity.getCreateDate());
        Assert.assertEquals(ObjectHelper.convertToType(Date.class, "2018-05-18"), entity.getLastUpdateTime());

        Assert.assertEquals("U0002", entity2.getId());
    }

    @Test
    public void queryEntities() {
        //查询多条记录（实体）,无排序
        List<MysqlSelectModel> list = swjDao.queryEntities(MysqlSelectModel.class, "select * from framework_test_table", null);

        //查询多条记录（实体）,排序
        List<MysqlSelectModel> list2 = swjDao.queryEntities(MysqlSelectModel.class, "select * from framework_test_table", "order by username desc", null);

        //查询多条记录（实体）,排序参数为空
        List<MysqlSelectModel> list3 = swjDao.queryEntities(MysqlSelectModel.class, "select * from framework_test_table", null, null);


        Assert.assertEquals(100, list.size());
        Assert.assertEquals(100, list2.size());
        Assert.assertEquals(100, list3.size());

        Assert.assertEquals(99 + "", list.get(0).getOrder() + "");
        Assert.assertEquals(98 + "", list.get(1).getOrder() + "");

        Assert.assertEquals(1 + "", list2.get(0).getOrder() + "");
        Assert.assertEquals(2 + "", list2.get(1).getOrder() + "");
    }

    @Test
    public void selectAll() {
        List<MysqlSelectModel> list = swjDao.selectAll(MysqlSelectModel.class);
        Assert.assertEquals(100, list.size());
    }

    @Test
    public void select() {
        MysqlSelectModel entity = swjDao.select(MysqlSelectModel.class, "U0001");
        Assert.assertEquals("U0001", entity.getId());
        Assert.assertEquals("user1", entity.getUserName());
        Assert.assertEquals("99", entity.getOrder() + "");
    }

    @Test
    public void queryEntitiesByPage() {
        //查询多条记录（实体）,排序
        PageResult pageResult = swjDao.queryEntitiesByPage(MysqlSelectModel.class, "select * from framework_test_table", "ORDER BY `order` desc", 1, 10, null);
        MysqlSelectModel entity = (MysqlSelectModel) pageResult.getResult().get(0);
        Assert.assertEquals(10, pageResult.getResult().size());
        Assert.assertEquals(99, entity.getOrder().intValue());
        Assert.assertEquals(100, pageResult.getRecordCount());
    }

    @Test
    public void queryMap() {
        Map map = ObjectHelper.getParameter("id", "U0001");
        Map<String, Object> maps = swjDao.queryMap("select * from framework_test_table where id=#id#", map);
        Assert.assertEquals(8, maps.size());
    }

    @Test
    public void queryMaps() {
        Map map = ObjectHelper.getParameter("id", "U0001");
        List<Map<String, Object>> maps = swjDao.queryMaps("select * from framework_test_table where id=#id#", map);
        Assert.assertEquals(1, maps.size());
    }

    @Test
    public void queryMapsByPage() {//mysql
        PageResult pageResult = swjDao.queryMapsByPage("select * from sys_user", "id", 1, 10, null);
        Assert.assertEquals(10, pageResult.getResult().size());
    }

    @Test
    public void queryMapsByPage2() {//orcle todo 出错了
        PageResult pageResult = SwjDao.get("oracle-syscore").queryMapsByPage("select * from users", "id", 1, 10, null);
        // Assert.assertEquals(10,pageResult.getResult().size());
    }

    @Test
    public void queryScalar() {
        Map map = ObjectHelper.getParameter("id", "U0001");
        String result = swjDao.queryScalar("select userName from framework_test_table where id=#id#", map, String.class);
        Assert.assertEquals("user1", result);
    }

    @Test
    public void queryScalarList() {
        List<String> list = swjDao.queryScalarList("select userName from framework_test_table", null, String.class);
        Assert.assertEquals(100, list.size());
        Assert.assertEquals("user1", list.get(0));
        Assert.assertEquals("user2", list.get(1));
    }

    @Test
    public void update() throws Exception{
        List<MysqlUpdateModel> list = swjDao.selectAll(MysqlUpdateModel.class);
        MysqlUpdateModel entity = swjDao.select(MysqlUpdateModel.class, "U0003");
        entity.setUserName("testUpdate");
        entity.setLastUpdateTime(new Date());
        int result = swjDao.update(entity);
        Assert.assertEquals(1, result);
        MysqlUpdateModel entity2 = swjDao.select(MysqlUpdateModel.class, "U0003");
        Assert.assertEquals("testUpdate", entity2.getUserName());
    }

    /*@Test
    public void update1() {//todo 将已经有值的字段修改为null,该功能暂时无法实现
        List<MysqlUpdateModel> list = swjDao.selectAll(MysqlUpdateModel.class);
        MysqlUpdateModel entity = swjDao.select(MysqlUpdateModel.class,"U0053");
        entity.setUserName("update53");
        entity.setCreateDate(null);
        int result = swjDao.update(entity);
        Assert.assertEquals(1, result);
        MysqlUpdateModel entity2 = swjDao.select(MysqlUpdateModel.class,"U0053");

        Assert.assertEquals("update53", entity2.getUserName());
        Assert.assertNull(entity2.getCreateDate());
    }*/

    @Test
    public void updateFiles1() throws Exception{//true
        MysqlSelectModel oldEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0097"));

        String oldNickName = oldEntity.getOtherName();
        oldEntity.setUserName("testUpdateFiles");
        oldEntity.setOtherName("nickName4");

        int result = swjDao.updateFields(oldEntity, true, "userName");
        //获取修改后的记录
        MysqlSelectModel newEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0097"));
        //未进行修改的记录
        MysqlSelectModel noUpdateEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0014"));
        Assert.assertEquals(1, result);
        Assert.assertEquals("testUpdateFiles", newEntity.getUserName());
        Assert.assertEquals(oldNickName, newEntity.getOtherName());
        Assert.assertEquals("user14", noUpdateEntity.getUserName());

    }

    @Test
    public void updateFiles2() throws Exception{//model为空
        int result = swjDao.updateFields(null, true, "userName");
        Assert.assertEquals(0, result);
    }

    @Test
    public void updateFiles3() throws Exception{//fileds为空
        MysqlSelectModel oldEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0004"));

        oldEntity.setUserName("testUpdateFiles");
        oldEntity.setOtherName("nickName4");

        int result = swjDao.updateFields(oldEntity, true, null);
        //获取修改后的记录
        MysqlSelectModel newEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0004"));
        //未进行修改的记录
        MysqlSelectModel noUpdateEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0014"));
        Assert.assertEquals(1, result);
        Assert.assertEquals("testUpdateFiles", newEntity.getUserName());
        Assert.assertEquals("nickName4", newEntity.getOtherName());
        Assert.assertEquals("user14", noUpdateEntity.getUserName());

    }

    @Test
    public void updateFiles() throws Exception{//false
        MysqlSelectModel oldEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0007"));

        String oldNickName = oldEntity.getOtherName();

        oldEntity.setUserName("testUpdateFiles");
        oldEntity.setOtherName("nickName7");

        int result = swjDao.updateFields(oldEntity, false, "userName", "id");
        //获取修改后的记录
        MysqlSelectModel newEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0007"));
        //未进行修改的记录
        MysqlSelectModel noUpdateEntity = swjDao.queryEntity(MysqlSelectModel.class, "select * from framework_test_table where id=#id#", ObjectHelper.getParameter("id", "U0014"));
        Assert.assertEquals(1, result);
        Assert.assertNotEquals("testUpdateFiles", newEntity.getUserName());
        Assert.assertEquals("nickName7", newEntity.getOtherName());
        Assert.assertEquals("user14", noUpdateEntity.getUserName());
    }

    @Test
    public void delete() throws Exception{

        int result = swjDao.delete(MysqlUpdateModel.class, "U0005");
        Assert.assertEquals(1, result);
    }

    @Test
    public void get() {
        SwjDao swjDaotest2 = SwjDao.get();
        // Assert.assertEquals(swjDaoTest,swjDaotest2);
    }

    @Test
    public void testSqlException() {
        List<MysqlSelectModel> list = swjDao.queryEntities(MysqlSelectModel.class, "select * form framework_test_table", null);
        System.out.println("");
    }

    @Test
    public void SqlStatement() {
        String[] ids = {"U0001", "U0002", "U0003", "U0004"};
        Map map = ObjectHelper.getParameter("id", ids);
        List<MysqlSelectModel> list = swjDao.queryEntities(MysqlSelectModel.class, "test.getStatement1", map);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("U0001", list.get(0).getId());
    }

    @Test
    public void SqlStatement2() {
        Map map = ObjectHelper.getParameter("userName", "u");
        List<MysqlSelectModel> list = swjDao.queryEntities(MysqlSelectModel.class, "test.getStatement2", map);
        Assert.assertEquals(100, list.size());
    }

    @Test
    public void SqlStatement3() {

        List<String> listParam = new ArrayList();
        listParam.add("U0021");
        listParam.add("U0022");
        listParam.add("U0023");
        listParam.add("U0024");
        Map map = ObjectHelper.getParameter("id", listParam);
        List<MysqlSelectModel> list = swjDao.queryEntities(MysqlSelectModel.class, "test.getStatement1", map);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("U0021", list.get(0).getId());
    }

    @Test
    public void testIsNull() throws Exception{
        MysqlUpdateModel testTable = new MysqlUpdateModel();
        testTable.setId("NameIsNull");
        testTable.setUserName("user");
        testTable.setOtherName(null);
        testTable.setOrder(200);
        testTable.setCreateDate(new Date());
        testTable.setLastUpdateTime(new Date());
        int result = swjDao.insert(testTable);
        Assert.assertEquals(1, result);
        List<MysqlUpdateModel> liste = swjDao.queryEntities(MysqlUpdateModel.class, "select * from framework_test_table3", null);
        List<MysqlUpdateModel> list = swjDao.queryEntities(MysqlUpdateModel.class, "test.getStatement3", ObjectHelper.getParameter("otherName", null));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("NameIsNull", testTable.getId());
        Assert.assertEquals("user", testTable.getUserName());
        Assert.assertEquals(200, testTable.getOrder().intValue());
    }

    @Test
    public void insertSelective() throws Exception {
        Date date = (Date) ObjectHelper.convertToType(Date.class,"2018-06-08");
        MysqlSelect2Model testTable = new MysqlSelect2Model();
        testTable.setUserName("testSelective");
        testTable.setSex(1);
        testTable.setCreateDate(date);
        int result = swjDao.insertSelective(testTable);
        MysqlSelect2Model addEntity = swjDao.select(MysqlSelect2Model.class, ObjectHelper.getParameter("id", result));
        Assert.assertNotNull(addEntity);
        Assert.assertEquals("testSelective", addEntity.getUserName());
        Assert.assertEquals(1,addEntity.getSex().intValue());
        Assert.assertEquals(date,addEntity.getCreateDate());
    }


    @Test
    public void testUnderline() throws Exception{
        //记录新增之前的记录数
        List<MysqlUnderlineModel> beforeList = swjDao.selectAll(MysqlUnderlineModel.class);
        //传带有下划线的参数(新增)
        MysqlUnderlineModel addEntity = new MysqlUnderlineModel();
        addEntity.setUserName("addName1");
        addEntity.setResource_("resource_");
        addEntity.set_userRole("_super");
        addEntity.setUser_pwd("pwd_123");
        addEntity.setSex(1);
        int result = swjDao.insertSelective(addEntity);
        Assert.assertTrue(result != 0);
        //记录新增之后的记录数
        List<MysqlUnderlineModel> afterList = swjDao.selectAll(MysqlUnderlineModel.class);
        //断言记录数
        Assert.assertEquals(beforeList.size() + 1, afterList.size());
        //查询新增记录
        MysqlUnderlineModel selectEntity = swjDao.select(MysqlUnderlineModel.class, result);
        //断言新增实体
        Assert.assertNotNull(selectEntity);
        Assert.assertEquals("addName1", selectEntity.getUserName());
        Assert.assertEquals("resource_", selectEntity.getResource_());
        Assert.assertEquals("_super", selectEntity.get_userRole());
        Assert.assertEquals("pwd_123", selectEntity.getUser_pwd());
    }

    @Test
    public void transactionWithIinsertSelective () throws Exception {
        Date date = (Date) ObjectHelper.convertToType(Date.class,"2018-06-08");
        MysqlSelect2Model entity2=new MysqlSelect2Model();
        entity2.setUserName("userSwlective");
        entity2.setCreateDate(date);
        entity2.setSex(0);
        TransactionManager.open();
        int result=swjDao.insertSelective(entity2);

        Assert.assertTrue(result!=0&&result>99);

        try {
            TransactionManager.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MysqlSelect2Model addModel = swjDao.select(MysqlSelect2Model.class,result);
        Assert.assertEquals("userSwlective",addModel.getUserName());
        Assert.assertEquals(date,addModel.getCreateDate());
    }

    /**
     * 测试是否可以解析实体中集合对象中的对象
     * todo 暂时不支持
     */
    @Test
    public void hasListModel() {
        List<MysqlSelectModel> list = swjDao.queryEntities(MysqlSelectModel.class, "mysql.hasListModel", null);
        Assert.assertNotNull(list);
    }

    /**
     * 批量更新实体的部分字段（更新部分字段）
     * @date 2018/06/08
     */
    @Test
    public void updateFieldsBatch() throws Exception {
        List<MysqlUpdateModel> trueList=new ArrayList();
        List<MysqlUpdateModel> falseList=new ArrayList();
        Date date = (Date)ObjectHelper.convertToType(Date.class,"2017-06-08");
        PageResult pageResult = swjDao.queryEntitiesByPage(MysqlUpdateModel.class,"select * from framework_test_table3","id",50,10,null);
        for (int i = 0; i < pageResult.getResult().size(); i++) {
            MysqlUpdateModel model = (MysqlUpdateModel)pageResult.getResult().get(i);
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

        pageResult = swjDao.queryEntitiesByPage(MysqlUpdateModel.class,"select * from framework_test_table3","id",50,10,null);
        for (int i = 50; i < pageResult.getResult().size(); i++) {
            MysqlUpdateModel updateModel = (MysqlUpdateModel)pageResult.getResult().get(i);
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

        MysqlUpdateModel unUpdateModel = swjDao.select(MysqlUpdateModel.class,ObjectHelper.getParameter("id","U0070"));
        Assert.assertNotNull(unUpdateModel);
        Assert.assertEquals("user70",unUpdateModel.getUserName());
        Assert.assertEquals("a_othName70",unUpdateModel.getOtherName());

    }

    @AfterClass
    public static void destroty() throws Exception{
        String sql = "drop table framework_test_table";
        String sql1 = "drop table framework_test_table2";
        String sql2 = "drop table framework_test_table3";
        String sql3 = "drop table framework_test_table4";
        String doublePkSql = "drop table doublePk";
        String testDate = "drop table test_date";

        int result = swjDao.execute(sql, null);
        int result2 = swjDao.execute(sql2, null);
        int result1 = swjDao.execute(sql1, null);
        int result3 = swjDao.execute(sql3, null);
        int result4 = swjDao.execute(doublePkSql, null);
        int result5 = swjDao.execute(testDate, null);
    }


}
