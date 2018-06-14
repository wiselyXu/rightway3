package com.swj.mustang;


import com.swj.basic.annotation.Table;
import com.swj.basic.helper.PackageHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlSource
 * 1.加载xml文件中的sql语句，并通过sqlparser格式化好后保存在静态map中
 * 2.扫描指定的jar包，为jar包里包含table标识的entity生成增删改查sql语句
 *
 * @author liuhf
 * @since 2018-03-12
 */
public class SqlSource {

    private static final String PRE_KEY = "auto";//自动生成的语句标识

    public static final String SELECT_ALL_KEY = "selectAll";

    public static final String INSERT_KEY = "insert";

    public static final String UPDATE_KEY = "update";

    public static final String DELETE_KEY = "delete";

    public static final String SELECT_ONE_KEY = "select";

    public static final String MYSQL = "mysql";

    public static final String ORACLE = "oracle";

    private static final String SCAN_PACKAGE = "scanPackage";

    private static Map<String, SqlStatement> SqlStatements = new HashMap<>();

    private final static Logger logger = LoggerFactory.getLogger(SqlSource.class);

    static {
        loadSqlFromXml();
        loadSqlFromEntity(getScanClassPackageName());
    }

    /**
     * load all sql statement from specified xml file.
     * xug
     */
    private static void loadSqlFromXml() {
        SAXReader saxReader = new SAXReader();
        String sqlId;
        try {
            List<URL> list = getSQLFileURL();
            for (URL url : list) {
                org.dom4j.Document doc = saxReader.read(url);
                List<Node> commandNodeList = doc.selectNodes("//command");
                for (Node command : commandNodeList) {
                    Element commandElem = (Element) command;
                    String sqlStatement = commandElem.getText();
                    sqlId = commandElem.attribute("id").getValue();
                    SqlStatement statement = new SqlStatement();
                    statement.setClauses(SqlParser.parse(sqlStatement));
                    SqlStatements.put(sqlId, statement);
                }
            }
        } catch (IllegalSqlStatementException e) {
            e.printStackTrace();//todo  throw exception,show which sqlid fail to parse.
        } catch (Exception e) {
            logger.error("loadSqlFromXml error", e);
        }
    }

    /**
     * find sql by msid
     *
     * @param sqlId     sqlId
     * @param isOracle  是否oracle
     * @param parameter 参数
     * @return sql statement
     */
    public static String findSqlByMsId(String sqlId, boolean isOracle, Map<String, Object> parameter) {
        String key = "%s$%s";
        SqlStatement sqlStatement = null;
        if (isOracle) {
            key = String.format(key, sqlId, SqlSource.ORACLE);
            if (SqlStatements.containsKey(key)) {
                sqlStatement = SqlStatements.get(key);
            }
        } else {
            key = String.format(key, sqlId, SqlSource.MYSQL);
            if (SqlStatements.containsKey(key)) {
                sqlStatement = SqlStatements.get(key);
            }
        }
        if (sqlStatement == null) {
            if (SqlStatements.containsKey(sqlId)) {
                sqlStatement = SqlStatements.get(sqlId);
            }
        }
        if (sqlStatement != null) {
            if (logger.isDebugEnabled()) {
                StringBuilder stringBuilder = new StringBuilder("\n");
                stringBuilder.append(sqlStatement.toString());
                stringBuilder.append("\n");
                if (parameter != null && !parameter.isEmpty()) {
                    for (String parameterKey : parameter.keySet()) {
                        stringBuilder.append(String.format("    %s = '%s'\n", parameterKey, parameter.get(parameterKey)));
                    }
                }
                logger.debug(stringBuilder.toString());
            }
            sqlId = sqlStatement.getStatement(parameter);
        } else {
            logger.error(String.format("sql id not found:%s", sqlId));
        }
        return sqlId;
    }


    /**
     * 获取所有模块下的sqlxml 文件
     *
     * @author Chenjw
     * @since 2018/4/4
     **/
    private static List<URL> getSQLFileURL() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:/sqlSource/*.xml");
        List<URL> list = new ArrayList<>();
        for (Resource resource : resources) {
            logger.info("Loading xml from :" + resource.getURI().toString());
            list.add(resource.getURL());
        }
        return list;
    }

    /**
     * xml配置完毕后，加载指定包的entiy类，为其增加实体的select update delete 语句
     *
     * @author Chenjw
     * @since 2018/3/15
     **/
    private static void loadSqlFromEntity(String packageName) {
        List<String> classNames;
        try {
            classNames = PackageHelper.getClassName(packageName);
            //2 反射获取类型的相关数据生成sql语句 生成sqlId
            String[] keys = new String[]{SELECT_ALL_KEY, SELECT_ONE_KEY, INSERT_KEY, UPDATE_KEY, DELETE_KEY};
            String[] dbs = new String[]{MYSQL, ORACLE};
            String tempKey;
            if (classNames != null && classNames.size() > 0) {
                for (String className : classNames) {
                    if (!className.contains("test-class") && !className.contains("com.swj.basic")) {
                        try {
                            Class cls = Class.forName(className);
                            if (cls.getAnnotation(Table.class) == null) {
                                continue;
                            }
                            logger.info("generate sql for class :{}", className);
                            for (int j = 0; j < dbs.length; j++) {
                                SqlProvider provider = new SqlProvider(dbs[j]);
                                for (int i = 0; i < keys.length; i++) {
                                    tempKey = getSqlKey(className, keys[i], dbs[j]);
                                    if (!SqlStatements.containsKey(tempKey.toLowerCase())) {
                                        SqlStatements.put(tempKey.toLowerCase(), new SqlStatement(SqlParser.parse(provider.getKey(cls, keys[i]))));
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            logger.error("loading class:{} error",className,e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("loadSqlFromEntity error", e);
        }
    }


    public static <T> String getSqlKey(Class<T> tClass, String keyType) {
        return String.format("%s.%s.%s", PRE_KEY, tClass.getName(), keyType).toLowerCase();
    }

    public static String getSqlKey(String className, String keyType, String dbType) {
        return String.format("%s.%s.%s$%s", PRE_KEY, className, keyType, dbType).toLowerCase();
    }

    public static String getScanClassPackageName() {
        String pageName = "com.swj.api";
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:/application.yml");
        Map map = new HashMap();
        if (resource.exists()) {
            Yaml yaml = new Yaml();
            try {
                map = (Map) yaml.load(new FileInputStream(resource.getURL().getFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pageName = map.get(SCAN_PACKAGE)!=null? map.get(SCAN_PACKAGE).toString():pageName ;
        return pageName;
    }

}
