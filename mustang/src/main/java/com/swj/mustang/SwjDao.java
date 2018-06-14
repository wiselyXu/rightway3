package com.swj.mustang;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.swj.basic.*;
import com.swj.basic.helper.ListHelper;
import com.swj.basic.helper.ObjectHelper;
import com.swj.basic.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据访问帮助类
 *
 * @author liuhf
 * @since 2018/3/12-11:27
 **/
public class SwjDao {

    private final static Logger logger = LoggerFactory.getLogger(SwjDao.class);

    private String defaultKey;

    SwjDao(String name) {
        defaultKey = name;
    }

    /**
     * constructor
     *
     * @param name
     * @return SwjDao
     */
    public static SwjDao get(String name) {
        return new SwjDao(name);
    }

    /**
     * constructor with default argument : "default"
     *
     * @return SwjDao
     */
    public static SwjDao get() {
        return new SwjDao("oracle-pmc");
    }

    /**
     * 执行指定的sql语句
     *
     * @param sql       要执行的sql语句（或xml文件中的key）
     * @param parameter 参数
     * @return 返回受影响的行数
     */
    public int execute(String sql, Object parameter) throws Exception {
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = SqlSource.findSqlByMsId(sql, isOracle(false), map);
        return executeInner(sql, map, false, new SwjDaoExecCallback());
    }


    /**
     * 查询单条记（map）
     *
     * @param sql       要查询的sql语句
     * @param parameter 参数
     * @return 结果集（key均为小写）
     */
    public Map<String, Object> queryMap(String sql, Object parameter) {
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = SqlSource.findSqlByMsId(sql, isOracle(true), map);
        try {
            return executeInner(sql, map, true, new SwjDaoQueryCallback());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询多条记（map）
     *
     * @param sql       要查询的sql语句
     * @param parameter 参数
     * @return 结果集（key均为小写）
     */
    public List<Map<String, Object>> queryMaps(String sql, Object parameter) {
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = SqlSource.findSqlByMsId(sql, isOracle(true), map);
        try {
            return executeInner(sql, map, true, new SwjDaoQueryListCallback());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询单条记录（实体）
     *
     * @param tClass    实体类型
     * @param sql       要查询的sql语句
     * @param parameter 参数
     * @return 实体
     */
    public <T> T queryEntity(Class<T> tClass, String sql, Object parameter) {
        return ObjectHelper.toEntity(queryMap(sql, parameter), tClass);
    }

    /**
     * 查询多条记录（实体）
     *
     * @param tClass    实体类型
     * @param sql       要查询的sql语句
     * @param parameter 参数
     * @return 实体结果集
     */
    public <T> List<T> queryEntities(Class<T> tClass, String sql, Object parameter) {
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = SqlSource.findSqlByMsId(sql, isOracle(true), map);
        try {
            return executeInner(sql, map, true, new SwjDaoQueryEntitiesCallback<>(tClass));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询多条记录（实体），可附加排序表达式
     *
     * @param tClass      实体类型
     * @param sql         要查询的sql语句
     * @param orderClause 排序表达式
     * @param parameter   参数
     * @return 实体结果集
     */
    public <T> List<T> queryEntities(Class<T> tClass, String sql, String orderClause, Object parameter) {
        if (StringHelper.isNullOrEmpty(orderClause)) {
            return queryEntities(tClass, sql, parameter);
        } else {
            if (!orderClause.toLowerCase().contains("order by")) {
                orderClause = " order by " + orderClause;
            }
        }
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = String.format("%s %s", SqlSource.findSqlByMsId(sql, isOracle(true), map), orderClause);
        try {
            return executeInner(sql, map, true, new SwjDaoQueryEntitiesCallback<>(tClass));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 按指定的起始行获取指定条数的实体记录
     *
     * @param tClass      实体类型
     * @param sql         要查询的sql语句
     * @param orderClause 排序表达式
     * @param startIndex  起始行
     * @param rowCount    要返回的记录数
     * @param parameter   要查询的参数
     */
    public <T> PageResult<T> queryEntitiesByPage(Class<T> tClass, String sql, String orderClause, int startIndex, int rowCount, Object parameter) {
        boolean isOracle = isOracle(true);
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        if (map == null) {
            map = new HashMap<>();
        }
        sql = SqlSource.findSqlByMsId(sql, isOracle, map);
        PageResult<T> result = new PageResult<>();
        try {
            int recordCount = executeInner(wrapCountingSql(sql), map, true, new SwjDaoQueryOneCallback<>(Integer.class));
            result.setRecordCount(recordCount);

            sql = wrapPagingSql(sql, orderClause, startIndex, rowCount, map, isOracle);
            result.setResult(executeInner(sql, map, true, new SwjDaoQueryEntitiesCallback<>(tClass)));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * 按指定的页码获取指定条数的实体记录
     *
     * @param tClass      实体类型
     * @param sql         要查询的sql语句
     * @param orderClause 排序表达式
     * @param pageIndex   页码
     * @param pageSize    每页记录数
     * @param parameter   要查询的参数
     */
    public <T> PageResult<T> queryEntitiesByPageIndex(Class<T> tClass, String sql, String orderClause, int pageIndex, int pageSize, Object parameter) {
        int startIndex = (pageIndex - 1) * pageSize + 1;
        return queryEntitiesByPage(tClass, sql, orderClause, startIndex, pageSize, parameter);
    }

    /**
     * 按指定的页码获取指定条数的map记录
     *
     * @param sql         要查询的sql语句
     * @param orderClause 排序表达式
     * @param pageIndex   页码
     * @param pageSize    每页记录数
     * @param parameter   要查询的参数
     */
    public PageResult<Map<String, Object>> queryMapsByPageIndex(String sql, String orderClause, int pageIndex, int pageSize, Object parameter) {
        int startIndex = (pageIndex - 1) * pageSize + 1;
        return queryMapsByPage(sql, orderClause, startIndex, pageSize, parameter);
    }

    /**
     * 按指定的起始行获取指定条数的map记录
     *
     * @param sql         要查询的sql语句
     * @param orderClause 排序表达式
     * @param startIndex  起始行
     * @param rowCount    要返回的记录数
     * @param parameter   要查询的参数
     */
    public PageResult<Map<String, Object>> queryMapsByPage(String sql, String orderClause, int startIndex, int rowCount, Object parameter) {
        boolean isOracle = isOracle(true);
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        if (map == null) {
            map = new HashMap<>();
        }
        sql = SqlSource.findSqlByMsId(sql, isOracle, map);
        PageResult<Map<String, Object>> result = new PageResult<>();
        try {
            int recordCount = executeInner(wrapCountingSql(sql), map, true, new SwjDaoQueryOneCallback<>(Integer.class));
            result.setRecordCount(recordCount);
            sql = wrapPagingSql(sql, orderClause, startIndex, rowCount, map, isOracle);
            result.setResult(executeInner(sql, map, true, new SwjDaoQueryListCallback()));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    private String wrapCountingSql(String sql) {
        return String.format("SELECT COUNT(0) FROM ( %s ) b", sql);
    }

    private String wrapPagingSql(String sql, String orderClause, int startIndex, int rowCount, Map<String, Object> parameter, boolean isOracle) {
        String rowBegin = "_ROW_BEGIN_";
        String rowEnd = "_ROW_END_";
        if (StringHelper.isNullOrEmpty(orderClause)) {
            orderClause = "";
        } else {
            if (!orderClause.toLowerCase().contains("order by")) {
                orderClause = " order by " + orderClause;
            }
        }
        if (isOracle) {
            parameter.put(rowBegin, startIndex);
            parameter.put(rowEnd, startIndex + rowCount - 1);
            sql = String.format("SELECT * FROM (SELECT * FROM (SELECT ROW_.*,rownum rownum_ from ( %s %s ) ROW_) where rownum_ <= #%s#) where rownum_>=#%s#",
                    sql, orderClause, rowEnd, rowBegin);
        } else {
            parameter.put(rowBegin, startIndex - 1);
            parameter.put(rowEnd, rowCount);
            sql = String.format(" %s %s LIMIT #%s#,#%s#", sql, orderClause, rowBegin, rowEnd);
        }
        return sql;
    }

    /**
     * 插入实体记录
     *
     * @param model 实体对象
     * @return 受影响的行数
     */
    public <T> int insert(T model) throws Exception {
        String sql = SqlSource.getSqlKey(model.getClass(), SqlSource.INSERT_KEY);
        return execute(sql, model);
    }

    /**
     * 批量插入实体
     *
     * @param models 实体对象
     * @return 受影响的行数（结果未必准确，oracle有时候执行成功会返回-2，所以不要依赖受影响的行数来判断结果）
     */
    public <T> int insertBatch(List<T> models) throws SQLException {
        if (ListHelper.isNullOrEmpty(models)) {
            throw new IllegalArgumentException("models is required!");
        }
        String sql = SqlSource.getSqlKey(models.get(0).getClass(), SqlSource.INSERT_KEY);
        return executeBatch(sql, models);
    }

    /**
     * 插入实体记录（仅适用于mysql）
     *
     * @param model 实体对象
     * @return 新增记录的主键
     */
    public <T> int insertSelective(T model) throws Exception {
        int result = insert(model);
        if (result == 1) {
            return queryScalar("SELECT @@IDENTITY", null, Integer.class);
        }
        return 0;
    }

    /**
     * 根据主健更新全部字段
     *
     * @param model 实体对象
     * @return 受影响的行数
     */
    public <T> int update(T model) throws Exception {
        String sql = SqlSource.getSqlKey(model.getClass(), SqlSource.UPDATE_KEY);
        return execute(sql, model);
    }

    /**
     * 批量更新实体（只支持更新相同的字段）
     *
     * @param models 实体对象
     * @return 受影响的行数（结果未必准确，oracle有时候执行成功会返回-2，所以不要依赖受影响的行数来判断结果）
     */
    public <T> int updateBatch(List<T> models) throws SQLException {
        if (ListHelper.isNullOrEmpty(models)) {
            throw new IllegalArgumentException("models is required!");
        }
        String sql = SqlSource.getSqlKey(models.get(0).getClass(), SqlSource.UPDATE_KEY);
        return executeBatch(sql, models);
    }

    /**
     * 批量更新实体的部分字段（更新部分字段）
     *
     * @param models  要update的实体对象集合
     * @param include true:包含，false:排除
     * @param fields  要（包含/排除）的字段名，大小写不敏感
     * @return 受影响的行数（结果未必准确，oracle有时候执行成功会返回-2，所以不要依赖受影响的行数来判断结果）
     */
    public <T> int updateFieldsBatch(List<T> models, boolean include, String... fields) throws SQLException {
        if (ListHelper.isNullOrEmpty(models)) {
            return 0;
        }
        if (fields == null || fields.length <= 0) {
            return updateBatch(models);
        }
        String sql;
        Map<String, Object> parameter = ObjectHelper.toMap(models.get(0));
        if (include) {
            sql = new SqlProvider(isOracle(false) ? SqlSource.ORACLE : SqlSource.MYSQL).updateWithSepcColumn(models.get(0).getClass(), fields);
        } else {
            sql = new SqlProvider(isOracle(false) ? SqlSource.ORACLE : SqlSource.MYSQL).updateExcludeColumn(models.get(0).getClass(), fields);
        }
        return executeBatch(sql, models);
    }

    /**
     * update（更新部分字段）
     *
     * @param model   要update的实体对象
     * @param include true:包含，false:排除
     * @param fields  要（包含/排除）的字段名，大小写不敏感
     * @return 受影响的行数
     */
    public <T> int updateFields(T model, boolean include, String... fields) throws Exception {
        if (model == null) {
            return 0;
        }
        if (fields == null || fields.length <= 0) {
            return update(model);
        }
        String sql;
        Map<String, Object> parameter = ObjectHelper.toMap(model);
        if (include) {
            sql = new SqlProvider(isOracle(false) ? SqlSource.ORACLE : SqlSource.MYSQL).updateWithSepcColumn(model.getClass(), fields);
        } else {
            sql = new SqlProvider(isOracle(false) ? SqlSource.ORACLE : SqlSource.MYSQL).updateExcludeColumn(model.getClass(), fields);
        }
        return executeInner(sql, parameter, false, new SwjDaoExecCallback());
    }

    /**
     * 按主健删除记录
     *
     * @param tClass 实体类
     * @param id     主健
     * @return 受影响的行数
     */
    public <T> int delete(Class<T> tClass, Object id) throws Exception {
        String sql = SqlSource.getSqlKey(tClass, SqlSource.DELETE_KEY);
        Map<String, Object> parameter = SqlProvider.getPkFromClass(tClass, id);
        return execute(sql, parameter);
    }

    /**
     * 按主健获取指定记录
     *
     * @param tClass 实体类
     * @param id     主健
     * @return 符合主健的记录实体
     */
    public <T> T select(Class<T> tClass, Object id) {
        String sql = SqlSource.getSqlKey(tClass, SqlSource.SELECT_ONE_KEY);
        Map<String, Object> parameter = SqlProvider.getPkFromClass(tClass, id);
        return queryEntity(tClass, sql, parameter);
    }

    /**
     * select-all
     *
     * @param tClass 类型
     * @return
     */
    public <T> List<T> selectAll(Class<T> tClass) {
        String sql = SqlSource.getSqlKey(tClass, SqlSource.SELECT_ALL_KEY);
        return queryEntities(tClass, sql, null);
    }

    /**
     * queryScaler
     *
     * @param sql       待执行的sql语句
     * @param parameter 参数
     * @param tClass    类型
     * @return 指定类型的值
     */
    public <T> T queryScalar(String sql, Object parameter, Class<T> tClass) {
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = SqlSource.findSqlByMsId(sql, isOracle(true), map);
        try {
            return executeInner(sql, map, true, new SwjDaoQueryOneCallback<T>(tClass));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * queryScalerList
     *
     * @param sql       待执行的sql语句
     * @param parameter 参数
     * @param tClass    类型
     * @return 指定类型的集合
     */
    public <T> List<T> queryScalarList(String sql, Object parameter, Class<T> tClass) {
        Map<String, Object> map = ObjectHelper.toMap(parameter);
        sql = SqlSource.findSqlByMsId(sql, isOracle(true), map);
        try {
            return executeInner(sql, map, true, new SwjDaoQueryScalarListCallback<T>(tClass));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isOracle(boolean isQuery) {
        return DataSourceManager.createDataSource(defaultKey, isQuery).isOracle();
    }

    /**
     * the actual execute method..every other method is redirect to here.
     *
     * @param sql       待执行的sql语句
     * @param parameter 参数
     * @param callback  回调方法
     */
    private <T> T executeInner(String sql, Map<String, Object> parameter, boolean isQuery, SwjDaoCallback<NamedParameterStatement, T> callback) throws Exception {
        DruidPooledConnection connection = null;
        NamedParameterStatement prepStatement = null;
        try {
            connection = TransactionManager.get(defaultKey, isQuery);
            if (connection == null) {
                throw new SQLException("get connection fail!");
            }
            prepStatement = new NamedParameterStatement(connection, sql);
            if (parameter != null && parameter.size() > 0) {
                for (String key : parameter.keySet()) {
                    prepStatement.setObject(key, parameter.get(key));
                }
            }
            StopWatch stopWatch = null;
            if (logger.isDebugEnabled()) {
                stopWatch = new StopWatch();
                stopWatch.start(sql);
            }
            T result = callback.call(prepStatement);
            if (logger.isDebugEnabled()) {
                stopWatch.stop();
                logger.debug(stopWatch.prettyPrint());
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            close(prepStatement, connection);
        }
    }

    private void close(NamedParameterStatement parameterStatement, DruidPooledConnection connection) {
        try {
            if (parameterStatement != null) {
                parameterStatement.close();
            }
            if (connection != null) {
                TransactionManager.closeConnection(connection);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 执行指定的sql语句
     *
     * @param sql        要执行的sql语句（或xml文件中的key）
     * @param parameters 参数
     * @return 返回受影响的行数（结果未必准确，oracle有时候执行成功会返回-2，所以不要依赖受影响的行数来判断结果）
     */
    public <T> int executeBatch(String sql, List<T> parameters) throws SQLException {
        if (parameters == null || parameters.size() == 0) {
            throw new IllegalArgumentException("parameters is required!");
        }
        DruidPooledConnection connection = null;
        NamedParameterStatement prepStatement = null;
        try {
            connection = TransactionManager.get(defaultKey, false);
            if (connection == null) {
                throw new SQLException("get connection fail!");
            }
            sql = SqlSource.findSqlByMsId(sql, isOracle(false), ObjectHelper.toMap(parameters.get(0)));
            prepStatement = new NamedParameterStatement(connection, sql);
            for (T t : parameters) {
                Map<String, Object> parameter = ObjectHelper.toMap(t);
                if (parameter != null && parameter.size() > 0) {
                    for (String key : parameter.keySet()) {
                        prepStatement.setObject(key, parameter.get(key));
                    }
                    prepStatement.addBatch();
                }
            }
            StopWatch stopWatch = null;
            if (logger.isDebugEnabled()) {
                stopWatch = new StopWatch();
                stopWatch.start(sql);
            }
            int[] result = prepStatement.executeBatch();
            if (logger.isDebugEnabled()) {
                stopWatch.stop();
                logger.debug(stopWatch.prettyPrint());
            }
            int effectCount = 0;
            for (int r : result) {
                if (r < 0) r = 0;//oracle有时候会返回-2
                effectCount += r;
            }
            return effectCount;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            close(prepStatement, connection);
        }
    }


    private class SwjDaoQueryOneCallback<T> implements SwjDaoCallback<NamedParameterStatement, T> {

        private Class<T> tClass;

        public SwjDaoQueryOneCallback(Class<T> c) {
            tClass = c;
        }

        @Override
        public T call(NamedParameterStatement param) throws Exception {
            T result = null;
            ResultSet resultSet = param.executeQuery();
            if (resultSet.next()) {
                result = (T) ObjectHelper.convertToType(tClass, resultSet.getObject(1));
            }
            resultSet.close();
            return result;
        }
    }

    private class SwjDaoQueryScalarListCallback<T> implements SwjDaoCallback<NamedParameterStatement, List<T>> {

        private Class<T> tClass;

        private SwjDaoQueryScalarListCallback(Class<T> c) {
            tClass = c;
        }

        @Override
        public List<T> call(NamedParameterStatement param) throws Exception {
            List<T> result = new ArrayList<>();
            ResultSet resultSet = param.executeQuery();
            while (resultSet.next()) {
                result.add((T) ObjectHelper.convertToType(tClass, resultSet.getObject(1)));
            }
            resultSet.close();
            return result;
        }
    }

    private class SwjDaoQueryEntitiesCallback<T> implements SwjDaoCallback<NamedParameterStatement, List<T>> {

        private Class<T> tClass;

        private SwjDaoQueryEntitiesCallback(Class<T> c) {
            tClass = c;
        }

        @Override
        public List<T> call(NamedParameterStatement param) throws Exception {
            List<T> result = new ArrayList<>();
            ResultSet resultSet = param.executeQuery();
            if (resultSet.next()) {
                List<PropertyMapping> propertyMappings = getPropertyMapping(resultSet);
                do {
                    T instance = tClass.newInstance();
                    for (PropertyMapping propertyMapping : propertyMappings) {
                        propertyMapping.getTableColumn().setValue(instance, resultSet.getObject(propertyMapping.getResultSetIndex()));
                    }
                    result.add(instance);
                }
                while (resultSet.next());
            }
            resultSet.close();
            return result;
        }

        private List<PropertyMapping> getPropertyMapping(ResultSet resultSet) throws SQLException {
            TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(tClass);
            List<PropertyMapping> propertyMappings = new ArrayList<>(typeColumnMapping.getColumns().size());
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String tempName = metaData.getColumnLabel(i + 1);
                for (TableColumn column : typeColumnMapping.getColumns()) {
                    if (column.matchColumnName(tempName)) {
                        PropertyMapping propertyMapping = new PropertyMapping();
                        propertyMapping.setTableColumn(column);
                        propertyMapping.setResultSetIndex(i + 1);
                        propertyMappings.add(propertyMapping);
                        break;
                    }
                }
            }
            return propertyMappings;
        }
    }

    private class SwjDaoQueryListCallback implements SwjDaoCallback<NamedParameterStatement, List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> call(NamedParameterStatement param) throws Exception {
            ResultSet resultSet = param.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Map<String, Object>> result = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> temp = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    temp.put(metaData.getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
                }
                result.add(temp);
            }
            resultSet.close();
            return result;
        }
    }

    private class SwjDaoQueryCallback implements SwjDaoCallback<NamedParameterStatement, Map<String, Object>> {

        @Override
        public Map<String, Object> call(NamedParameterStatement param) throws Exception {
            ResultSet resultSet = param.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> result = new HashMap<>();
            if (resultSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    result.put(metaData.getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
                }
            }
            resultSet.close();
            return result;
        }
    }

    private class SwjDaoExecCallback implements SwjDaoCallback<NamedParameterStatement, Integer> {

        @Override
        public Integer call(NamedParameterStatement statement) throws Exception {
            return statement.executeUpdate();
        }
    }
}
