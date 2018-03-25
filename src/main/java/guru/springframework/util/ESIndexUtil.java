package guru.springframework.util;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import guru.springframework.domain.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * es 索引工具类
 */
@Slf4j
@Component
public class ESIndexUtil {
    public static final String INDEX = "fei-books";
    public static final String BOOK_TYPE = "books";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    public void initIndexAndType() {
        log.warn("重置es数据库");
        // table 是否存在
        delIndex();
        // 创建
        CreateIndexRequestBuilder createIndexRequestBuilder = elasticsearchTemplate.getClient().admin().indices().prepareCreate(INDEX);
        final XContentBuilder booksBuilder;
        try {
            booksBuilder = XContentFactory.jsonBuilder().startObject().startObject(BOOK_TYPE).startObject("properties");
            documentType(booksBuilder);
            booksBuilder.endObject().endObject().endObject();
            XContentBuilder setting = XContentFactory.jsonBuilder().startObject().startObject("analysis").startObject("analyzer").startObject("my_index_analyzer")
                    .field("type", "custom")
                    .field("tokenizer", "index_ansj")
                    .field("char_filter", "tsconvert")
                    .endObject().endObject().endObject().endObject();

            createIndexRequestBuilder.setSettings(setting);
            createIndexRequestBuilder.addMapping(BOOK_TYPE, booksBuilder);


            createIndexRequestBuilder.execute().actionGet();
        } catch (Exception e) {
            log.error("创建index/type失败", e);
        }
    }

    public void docIndex(String json) {
        elasticsearchTemplate.getClient().prepareIndex(INDEX, BOOK_TYPE)
                .setSource(json).get();
    }

    private void delIndex() {
        IndicesExistsResponse res = elasticsearchTemplate.getClient().admin().indices().prepareExists(INDEX).execute().actionGet();
        if (res.isExists()) {
            // 删除
            DeleteIndexRequestBuilder delIdx = elasticsearchTemplate.getClient().admin().indices().prepareDelete(INDEX);
            delIdx.execute().actionGet();
        }

    }


    private void documentType(XContentBuilder builder) {
        BookIndexUtil.config().forEach(bookIndexConfig -> {
            try {
                builder.startObject(bookIndexConfig.getIndexName()).field("type", "string").endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public Page search(String query, int pageNum, boolean highLight, int pageSize) {
        if (pageSize == 0) {
            pageSize = Page.pageSize;
        }
        List<Map<String, String>> books = Lists.newArrayList();
        Page result = new Page();
        result.setCurrentPage(pageNum);
        result.setList(books);

        int from = (pageNum - 1) * pageSize;
        if (from < 0) {
            from = 0;
        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.should(QueryBuilders.matchQuery("_all", query));


        SearchResponse searchResponse = elasticsearchTemplate.getClient().prepareSearch(INDEX).setTypes(BOOK_TYPE)
                .setQuery(queryBuilder)
                .addHighlightedField("title")
                .setHighlighterPreTags("<em style='color:red'>")
                .setHighlighterPostTags("</em>")
                .setFrom(from)
                .setSize(pageSize)
                .execute()
                .actionGet();

        long total = searchResponse.getHits().totalHits();
        result.setTotalNum((int) total);
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, String> map = JSON.parseObject(hit.getSourceAsString(), Map.class);
            if (highLight) {
                Map<String, HighlightField> highlightFieldMap = hit.highlightFields();
                highlightFieldMap.forEach((k, v) -> {
                    String newV = Arrays.stream(v.fragments()).map(Text::toString).reduce("", (s1, s2) -> s1 + s2);
                    try {
                        BeanUtils.setProperty(map, k, newV);
                    } catch (Exception e) {
                        log.error("error copy property", e);
                    }
                });
            }
            books.add(map);
        }
        return result;
    }


}
