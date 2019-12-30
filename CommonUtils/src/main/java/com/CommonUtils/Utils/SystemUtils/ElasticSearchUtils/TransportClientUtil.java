package com.CommonUtils.Utils.SystemUtils.ElasticSearchUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

public final class TransportClientUtil 
{
	private TransportClientUtil() {}
	
	/**创建索引*/
	public static boolean createIndex(final TransportClient client, final String index) 
	{ return isIndexExist(client, index) ? false : client.admin().indices().prepareCreate(index).execute().actionGet().isAcknowledged(); }
	
	/**删除索引*/
	public static boolean deleteIndex(final TransportClient client, final String index) 
	{ return isIndexExist(client, index) ? client.admin().indices().prepareDelete(index).execute().actionGet().isAcknowledged() : false; }
	
	/**判断索引是否存在, true为存在，false为不存在*/
    public static boolean isIndexExist(final TransportClient client, final String index) 
    { return client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet().isExists(); }
    
    /**判断inde下指定type是否存在*/
    public static boolean isTypeExist(final TransportClient client, final String index, final String type) 
    { return isIndexExist(client, index) ? client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists() : false; }
    
    /**
     * 数据添加，正定ID
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static String addData(final TransportClient client, 
    							 final JSONObject jsonObject, 
    							 final String index, 
    							 final String type, 
    							 final String id) 
    {
        IndexResponse response = client.prepareIndex(index, type, id).setSource(jsonObject).get();
        //LOGGER.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getId();
    }
    
    /**
     * 数据添加
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @return
     */
    public static String addData(final TransportClient client, 
    							 final JSONObject jsonObject, 
    							 final String index, 
    							 final String type) 
    { return addData(client, jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase()); }
    
    /**
     * 通过ID删除数据
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public static void deleteDataById(final TransportClient client, 
    								  final String index, 
    								  final String type, 
    								  final String id) 
    {
        client.prepareDelete(index, type, id).execute().actionGet();
        //LOGGER.info("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
    }
    
    /**
     * 通过ID 更新数据
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static void updateDataById(final TransportClient client,
    								  final JSONObject jsonObject, 
    								  final String index, 
    								  final String type, 
    								  final String id) 
    { client.update(new UpdateRequest().index(index).type(type).id(id).doc(jsonObject)); }
    
    /**
     * 通过ID获取数据
     *
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public static Map<String, Object> searchDataById(final TransportClient client, 
    												 final String index, 
    												 final String type, 
    												 final String id, 
    												 final String fields) 
    {

        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);

        if (!StrUtil.isEmpty(fields)) 
        { getRequestBuilder.setFetchSource(fields.split(","), null); }

        return getRequestBuilder.execute().actionGet().getSource();
    }
    
    /**
     * 使用分词查询,并分页
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param startPage      当前页
     * @param pageSize       每页显示条数
     * @param query          查询条件
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return
     */
    public static Optional<ElasticSearchPage> searchDataPage(final TransportClient client, 
    											   			 final String index, 
    											   			 final String type, 
    											   			 final int startPage, 
    											   			 final int pageSize, 
    											   			 final QueryBuilder query, 
    											   			 final String fields, 
    											   			 final String sortField, 
    											   			 final String highlightField) 
    {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index)
        												  .setSearchType(SearchType.QUERY_THEN_FETCH)
        												  
        												  //searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        												  .setQuery(query)
        												  
        												  //分页应用
        												  .setFrom(startPage).setSize(pageSize)
        												  
        												  //设置是否按查询匹配度排序
        												  .setExplain(true)
        												  ;
        
        if (!StrUtil.isEmpty(type)) 
        { searchRequestBuilder.setTypes(type.split(",")); }

        // 需要显示的字段，逗号分隔（缺省为全部字段）
        if (!StrUtil.isEmpty(fields)) 
        { searchRequestBuilder.setFetchSource(fields.split(","), null); }

		//排序字段
        if (!StrUtil.isEmpty(sortField)) 
        { searchRequestBuilder.addSort(sortField, SortOrder.DESC); }

		// 高亮（xxx=111,aaa=222）
        if (!StrUtil.isEmpty(highlightField)) 
        {
            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
            //highlightBuilder.postTags("</span>");//设置后缀

        	//设置高亮字段
            searchRequestBuilder.highlighter(new HighlightBuilder().field(highlightField));
        }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        //LOGGER.info("\n{}", searchRequestBuilder);

        // 执行搜索,返回搜索响应信息
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        //long length = searchResponse.getHits().getHits().length;

        //LOGGER.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        ElasticSearchPage result = null;
        if (searchResponse.status().getStatus() == 200) 
        {
			// 解析对象
            result = new ElasticSearchPage(startPage, pageSize, (int) searchResponse.getHits().totalHits, setSearchResponse(searchResponse, highlightField));
        }

        return Optional.ofNullable(result);
    }
    
    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param query          查询条件
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return
     */
    public static List<Map<String, Object>> searchListData(final TransportClient client,
            											   final String index, 
            											   final String type, 
            											   final QueryBuilder query, 
            											   final int size,
            											   final String fields, 
            											   final String sortField, 
            											   final String highlightField) 
    {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setQuery(query).setFetchSource(true);
        if (!StrUtil.isEmpty(type)) 
        { searchRequestBuilder.setTypes(type.split(",")); }

        if (!StrUtil.isEmpty(highlightField)) 
        {
        	// 设置高亮字段
            searchRequestBuilder.highlighter(new HighlightBuilder().field(highlightField));
        }

        if (!StrUtil.isEmpty(fields)) 
        { searchRequestBuilder.setFetchSource(fields.split(","), null); }

        if (!StrUtil.isEmpty(sortField)) 
        { searchRequestBuilder.addSort(sortField, SortOrder.DESC); }

        if (size > 0) 
        { searchRequestBuilder.setSize(size); }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        //LOGGER.info("\n{}", searchRequestBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        //long totalHits = searchResponse.getHits().totalHits;
        //long length = searchResponse.getHits().getHits().length;

        //LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) 
        {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }
        
        return Collections.emptyList();

    }
    
    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(final SearchResponse searchResponse, final String highlightField) 
    {
        List<Map<String, Object>> sourceList = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) 
        {
            searchHit.getSourceAsMap().put("id", searchHit.getId());

            if (!StrUtil.isEmpty(highlightField)) 
            {
                //System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSourceAsMap());
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();

                if (text != null) 
                {
                    for (Text str : text) 
                    { stringBuffer.append(str.string()); }
                    
					//遍历 高亮结果集，覆盖 正常结果集
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }
}