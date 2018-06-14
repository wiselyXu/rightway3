package com.swj.mustang;

import com.swj.basic.TableColumn;
import com.swj.basic.TypeColumn;
import com.swj.basic.TypeColumnMapping;
import com.swj.basic.helper.ListHelper;
import com.swj.basic.helper.ObjectHelper;
import com.swj.basic.helper.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql语句生成器，根据实体及数据库类型生成增删改查语句
 * @author liuhf
 * @since 2018/3/9-11:48
 **/
public class SqlProvider {

    private static final String SELECT_CONDITION = "select %s from %s where %s";
    private static final String SELECT = "select %s from %s";
    private static final String INSERT = "insert into %s (%s) values (%s)";
    private static final String UPDATE = "update %s set %s where %s";
    private static final String DELETE = "delete from %s where %s";

    private String excapeIdentity(String filedName) {
        if (dbType.toLowerCase().equals(SqlSource.MYSQL)) {
            return String.format("`%s`", filedName);
        } else {
            return String.format("\"%s\"", filedName.toUpperCase());
        }
    }

    private String dbType;

    public SqlProvider(String dbType) {
        this.dbType = dbType;
    }

    public <T> String getKey(Class<T> tClass, String keyType) {
        String result = null;
        switch (keyType) {
            case SqlSource.SELECT_ALL_KEY:
                result = selectAll(tClass);
                break;
            case SqlSource.SELECT_ONE_KEY:
                result = selectOne(tClass);
                break;
            case SqlSource.INSERT_KEY:
                result = insert(tClass);
                break;
            case SqlSource.UPDATE_KEY:
                result = update(tClass);
                break;
            case SqlSource.DELETE_KEY:
                result = delete(tClass);
                break;
        }
        return result;
    }

    private String getSelectColumn(List<TableColumn> columns) {
        List<String> list = new ArrayList<>();
        columns.forEach(column -> {
            if (column.isSelect()) {
                list.add(excapeIdentity(column.getName()));
            }
        });
        return String.join(",", list);
    }

    private String setStatement(TableColumn tableColumn) {
        return String.format("%s = #%s#", excapeIdentity(tableColumn.getName()), tableColumn.getFieldName());
    }

    private String setDynamicStatement(TableColumn tableColumn) {
        return String.format("{?, %s }", setStatement(tableColumn));
    }

    private String getWhereCondition(List<TableColumn> columns) {
        List<String> list = new ArrayList<>();
        columns.forEach(column -> {
            if (column.isPK()) {
                list.add(setStatement(column));
            }
        });
        return String.join(" and ", list);
    }

    /**
     * 查询单条
     * @author Chenjw
     * @since 2018/3/14
     **/
    public <T> String selectOne(Class<T> value) {
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(value);
        List<TableColumn> columns = typeColumnMapping.getColumns();
        String selectContent = getSelectColumn(columns);
        String selectWhere = getWhereCondition(columns);
        return String.format(SELECT_CONDITION, selectContent, excapeIdentity(typeColumnMapping.getTableName()), selectWhere);
    }

    /**
     * 查询多条
     * @author Chenjw
     * @since 2018/3/14
     **/
    public <T> String selectAll(Class<T> value) {
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(value);
        List<TableColumn> list = typeColumnMapping.getColumns();
        String selectContent = getSelectColumn(list);
        return String.format(SELECT, selectContent, excapeIdentity(typeColumnMapping.getTableName()));
    }

    /**
     * 插入数据
     * @author Chenjw
     * @since 2018/3/14
     **/
    public <T> String insert(Class<T> tClass) {
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(tClass);
        List<TableColumn> list = typeColumnMapping.getColumns();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        list.forEach(column -> {
            if (column.isInsert() && !column.isAutoIncrement()) {
                list2.add(excapeIdentity(column.getName()));
                list3.add(String.format("#%s#", column.getFieldName()));
            }
        });
        String setContent = String.join(",", list2);
        String setWhere = String.join(",", list3);
        return String.format(INSERT, excapeIdentity(typeColumnMapping.getTableName()), setContent, setWhere);
    }


    /**
     * 更新数据
     * @author Chenjw
     * @since 2018/3/14
     **/
    public <T> String update(Class<T> tClass)  {
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(tClass);
        List<TableColumn> columns = typeColumnMapping.getColumns();
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        columns.forEach(column -> {
            if (!column.isPK() && !column.isAutoIncrement() && column.isUpdate()) {
                if (list.size() == 0) {
                    list.add(setStatement(column));
                } else {
                    list2.add(setDynamicStatement(column));
                }
            }
        });
        String setContent = String.format("%s %s", String.join(",", list), String.join("", list2));
        return String.format(UPDATE, excapeIdentity(typeColumnMapping.getTableName()), setContent, getWhereCondition(columns));
    }

    /**
     * 更新特定列的update语句
     * @author liuhf
     * @since 2018/5/14
     **/
    public <T> String updateWithSepcColumn(Class<T> tClass,String ...columnNames) throws IllegalArgumentException
    {
        if(columnNames==null || columnNames.length==0)
        {
            throw new IllegalArgumentException("columnNames 不能为空");
        }
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(tClass);
        List<TableColumn> columns = typeColumnMapping.getColumns();
        List<String> list = new ArrayList<>();
        for(String columnName : columnNames)
        {
            TableColumn column = typeColumnMapping.getColumn(columnName);
            if(column==null)
            {
                throw new IllegalArgumentException(String.format("columnName:'%s' 不存在",columnName));
            }
            if(column.isPK() || column.isAutoIncrement())
            {
                throw new IllegalArgumentException(String.format("不允许更新主健或自增列:'%s'",columnName));
            }
            list.add(setStatement(column));
        }
        String setContent = String.join(",", list);
        return String.format(UPDATE, excapeIdentity(typeColumnMapping.getTableName()), setContent, getWhereCondition(columns));
    }

    /**
     * 更新特定列的update语句
     * @author liuhf
     * @since 2018/5/14
     **/
    public <T> String updateExcludeColumn(Class<T> tClass,String ...columnNames) throws IllegalArgumentException
    {
        if(columnNames==null || columnNames.length==0)
        {
            throw new IllegalArgumentException("columnNames 不能为空");
        }
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(tClass);
        List<TableColumn> columns = typeColumnMapping.getColumns();
        List<String> list = new ArrayList<>();
        boolean exclude;
        for(TableColumn column : columns)
        {
            if(column.isPK() || column.isAutoIncrement() || !column.isUpdate())
            {
                continue;
            }
            exclude=false;
            for(String columnName : columnNames)
            {
                if(column.matchColumnName(columnName))
                {
                    exclude=true;
                    break;
                }
            }
            if(exclude)
            {
                continue;
            }
            list.add(setStatement(column));
        }
        if(ListHelper.isNullOrEmpty(list))
        {
            throw new IllegalArgumentException("update column is empty");
        }
        String setContent = String.join(",", list);
        return String.format(UPDATE, excapeIdentity(typeColumnMapping.getTableName()), setContent, getWhereCondition(columns));
    }

    /**
     * 删除数据
     * @author Chenjw
     * @since 2018/3/14
     **/
    public <T> String delete(Class<T> tClass) {
        TypeColumnMapping typeColumnMapping = TypeColumn.getTypeColumnMapping(tClass);
        List<TableColumn> list = typeColumnMapping.getColumns();
        return String.format(DELETE, excapeIdentity(typeColumnMapping.getTableName()), getWhereCondition(list));
    }

    /**
     * 返回类的主键
     * @author Chenjw
     * @since 2018/3/14
     **/
    public static <T> Map<String, Object> getPkFromClass(Class<T> className, Object id) {
        Map<String, Object> map = new HashMap<>();
        TypeColumnMapping mapping = TypeColumn.getTypeColumnMapping(className);
        if(id instanceof Boolean || id instanceof String || id instanceof Integer || id.getClass().isPrimitive()) {
            String keyName = "";
            for (TableColumn column : mapping.getColumns()) {
                if (column.isPK()) {
                    keyName = column.getName();
                    break;
                }
            }
            if (StringHelper.isNullOrEmpty(keyName)) {
                return null;
            }
            map.put(keyName, id);
        }
        else
        {
            Map<String,Object> temp= ObjectHelper.toMap(id);
            for (TableColumn column : mapping.getColumns()) {
                if (column.isPK()) {
                    if (temp.containsKey(column.getName()))
                    {
                        map.put(column.getName(),temp.get(column.getName()));
                    }
                    else
                    {
                        //throw new Exception("参数中没有找到主健%s的值");
                    }
                }
            }
        }
        return map;
    }
}
