package com.swj.basic;

import com.swj.basic.annotation.Column;
import com.swj.basic.annotation.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * com.swj.framework.dao
 *
 * @DESCRIPTION
 * @AUTHOR Chenjw
 * @DATE 2018/3/13
 * @EMAIL chenjw@3vjia.com
 **/
public class TypeColumn {

    private static final Logger logger = LoggerFactory.getLogger(TypeColumn.class);

    private static Map<Class<?>,TypeColumnMapping> typeColumnMapping=new HashMap<>();

    /**
     * @DESCRIPTION 查询删除使用的typecolumn
     * @AUTHOR Chenjw
     * @DATE 2018/3/13
     **/
    public static <T> TypeColumnMapping getTypeColumnMapping(Class<T> value)  {
        if(!typeColumnMapping.containsKey(value)) {
            synchronized (typeColumnMapping)
            {
                if(!typeColumnMapping.containsKey(value))
                {
                    TypeColumnMapping temp = new TypeColumnMapping();
                    Table table = value.getAnnotation(Table.class);
                    if(table==null)
                    {
                        temp.setTableName(value.getName());
                    }
                    else {
                        temp.setTableName(table.value());
                    }
                    TableColumn tableColumn;
                    Class tempClass = value ;
                    while (tempClass !=null ){
                        Field[] fields = tempClass.getDeclaredFields();
                        for (Field field : fields) {
                            Column column = field.getAnnotation(Column.class);
                            if(column!=null && !column.isDataKey())
                            {
                                continue;
                            }
                            field.setAccessible(true);
                            tableColumn = new TableColumn();
                            tableColumn.setField(field);
                            if (column != null) {
                                tableColumn.setName(column.name());
                                if (tableColumn.getName() == null || tableColumn.getName().equals(""))
                                {
                                    tableColumn.setName(field.getName());
                                }
                                tableColumn.setPK(column.isPK());
                                tableColumn.setSelect(column.isSelect());
                                tableColumn.setInsert(column.isInsert());
                                tableColumn.setUpdate(column.isUpdate());
                                tableColumn.setFieldName(field.getName());
                                tableColumn.setAutoIncrement(column.isAutoIncrement());
                            }
                            else {
                                tableColumn.setName(field.getName());
                                tableColumn.setPK(false);
                                tableColumn.setSelect(true);
                                tableColumn.setInsert(true);
                                tableColumn.setUpdate(true);
                                tableColumn.setAutoIncrement(false);
                                tableColumn.setFieldName(field.getName());
                            }
                            temp.addColumns(tableColumn);
                        }
                        tempClass = tempClass.getSuperclass();
                    }
                    typeColumnMapping.put(value, temp);
                }
            }
        }
        return typeColumnMapping.get(value);
    }
}
