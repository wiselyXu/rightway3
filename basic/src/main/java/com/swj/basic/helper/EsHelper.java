//package com.swj.basic.helper;
//
//import io.searchbox.client.JestClientFactory;
//import io.searchbox.client.JestResult;
//import io.searchbox.client.config.HttpClientConfig;
//import io.searchbox.client.http.JestHttpClient;
//import io.searchbox.core.*;
//import io.searchbox.indices.CreateIndex;
//import io.searchbox.indices.DeleteIndex;
//import io.searchbox.indices.mapping.PutMapping;
//import io.searchbox.params.Parameters;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//
///**
// * ElasticSearch工具类
// *
// * @author zhangxh
// * @since Create in 2018/5/30
// **/
//public class EsHelper {
//    private static Logger logger = LoggerFactory.getLogger(EsHelper.class);
//    //    private static final String ELASTIC_SEARCH_SERVER_URI="http://10.101.17.82:9200";
//    private static final String ELASTIC_SEARCH_SERVER_URI = "http://localhost:9200";
//    private static JestHttpClient client;
//
//    private EsHelper() {
//    }
//
//    /**
//     * 初始化jest客户端
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    static {
////        try {
////            JestClientFactory factory = new JestClientFactory();
////            factory.setHttpClientConfig(new HttpClientConfig.Builder(ELASTIC_SEARCH_SERVER_URI).multiThreaded(true).build());
////            client = (JestHttpClient) factory.getObject();
////        } catch (Exception ex) {
////            logger.error("jest initClient fail");
////        }
//    }
//
//
//    /**
//     * 创建es索引
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    public static Boolean createIndex(String indexName) {
//        try {
//            JestResult jr = client.execute(new CreateIndex.Builder(indexName).build());
//            return jr.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest createIndex fail");
//        }
//        return false;
//    }
//
//    /**
//     * 创建索引及文档
//     *
//     * @param jsonDocument json格式字符串
//     * @author zhangxh - 2018/5/31
//     */
//    public static Boolean createIndex(String indexName, String typeName, String id, String jsonDocument) {
//        Index index = new Index.Builder(jsonDocument).index(indexName).type(typeName).id(id).build();
//        try {
//            DocumentResult result = client.execute(index);
//            return result.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest createType fail");
//        }
//        return false;
//    }
//
//    /**
//     * 创建索引及文档
//     *
//     * @param document 对象类型数据
//     * @author zhangxh - 2018/5/31
//     */
//    public static Boolean createIndex(String indexName, String typeName, String id, Object document) {
//        Index index = new Index.Builder(JsonHelper.toJsonString(document)).index(indexName).type(typeName).id(id).build();
//        try {
//            DocumentResult result = client.execute(index);
//            return result.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest createType fail");
//        }
//        return false;
//    }
//
//    /**
//     * 全文搜索
//     *
//     * @param query 查询条件
//     * @author zhangxh - 2018/5/31
//     */
//    public static SearchResult search(String indexName, String typeName, String query) {
//        Search search = new Search.Builder(query).addIndex(indexName).addType(typeName).build();
//        try {
//            return client.execute(search);
//        } catch (IOException ex) {
//            logger.error("jest search fail");
//        }
//        return null;
//    }
//
//    /**
//     * 根据scroll 分页方式搜索
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    public static SearchResult searchWithScroll(String indexName, String typeName, String query, String scroll) {
//        Search search = new Search.Builder(query).addIndex(indexName).addType(typeName).setParameter(Parameters.SCROLL, scroll).build();
//        try {
//            return client.execute(search);
//        } catch (IOException ex) {
//            logger.error("jest searchWithScroll fail");
//        }
//        return null;
//    }
//
//    /**
//     * 根据分页游标获取结果
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    public static JestResult searchByScrollId(String scroll, String scrollId) {
//        SearchScroll searchScroll = new SearchScroll.Builder(scrollId, scroll).build();
//        try {
//            return client.execute(searchScroll);
//
//        } catch (IOException e) {
//            logger.error("jest searchByScrollId fail");
//        }
//        return null;
//    }
//
//    public static SearchResult sear(String scroll, String scrollId) {
//        String query = "{\"scroll_id\":\"" + scrollId + "\",\"scroll\":\"" + scroll + "\"}";
//        Search search = new Search.Builder(query).build();
//        SearchResult execute = null;
//        try {
//            execute = client.execute(search);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return execute;
//    }
//
//    /**
//     * 删除scroll
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    public static boolean deleteScroll(String scrollId) {
//        String query = String.format("_search/scroll/%s", scrollId);
//        DeleteByQuery deleteScroll = new DeleteByQuery.Builder(query).build();
//        try {
//            JestResult jr = client.execute(deleteScroll);
//            return jr.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest delete fail");
//        }
//        return false;
//    }
//
//    /**
//     * 删除索引
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    public static boolean deleteIndex(String indexName) {
//        try {
//            JestResult jr = client.execute(new DeleteIndex.Builder(indexName).build());
//            return jr.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest deleteIndex fail");
//        }
//        return false;
//    }
//
//    /**
//     * 删除文档
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    public static boolean deleteDoc(String indexName, String typeName, String id) {
//        try {
//            DocumentResult dr = client.execute(new Delete.Builder(id).index(indexName).type(typeName).build());
//            return dr.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest delete fail");
//        }
//        return false;
//    }
//
//    /**
//     * 创建es 映射
//     * TODO 暂未用到 未验证
//     *
//     * @author zhangxh - 2018/5/31
//     */
//    @Deprecated
//    public static Boolean createIndexMapping(String indexName, String typeName, String queryMapping) {
//        PutMapping.Builder builder = new PutMapping.Builder(indexName, typeName, queryMapping);
//        try {
//            JestResult jr = client.execute(builder.build());
//            return jr.isSucceeded();
//        } catch (IOException ex) {
//            logger.error("jest createIndexMapping fail");
//        }
//        return false;
//    }
//
//}
