package guru.springframework.util;


import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            log.error("创建index/type失败",e);
        }
    }

    public void docIndex(String json){
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

    // not_analyzed no my_index_analyzer
    private void documentType(XContentBuilder builder){
        BookIndexUtil.config().forEach(bookIndexConfig -> {
            try {
                builder.startObject(bookIndexConfig.getIndexName()).field("type", "string").endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



//    public static BooksPage searchBook(Book query, int pageNum, boolean highLight, int pageSize) {
//        if (pageSize == 0){
//            pageSize = BooksPage.pageSize;
//        }
//        List<Book> books = Lists.newArrayList();
//        BooksPage result = new BooksPage();
//        result.setCurrentPage(pageNum);
//        result.setList(books);
//
//        int from = (pageNum - 1) * pageSize;
//        if (from < 0){
//            from = 0;
//        }
//
//        // or 查询
//        BoolQueryBuilder orgQueryBuilder = QueryBuilders.boolQuery();
//        if (StringUtils.isNotBlank(query.getBianHao())) {
//            orgQueryBuilder.should(QueryBuilders.matchQuery("bianHao", query.getBianHao()));
//        }
//        if (StringUtils.isNotBlank(query.getXingShi())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("xingShi", query.getXingShi()));
//        }
//        if (StringUtils.isNotBlank(query.getJuanNum())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("juanNum", query.getJuanNum()));
//        }
//        if (StringUtils.isNotBlank(query.getPuJidi())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("puJidi", query.getPuJidi()));
//        }
//        if (StringUtils.isNotBlank(query.getShiZu())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("shiZu", query.getShiZu()));
//        }
//        if (StringUtils.isNotBlank(query.getShiQianZu())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("shiQianZu", query.getShiQianZu()));
//        }
//        if (StringUtils.isNotBlank(query.getTiMing())) {
//            orgQueryBuilder.should(QueryBuilders.matchQuery("tiMing", query.getTiMing()));
//        }
//        if (StringUtils.isNotBlank(query.getXiuZhuanZhe())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("xiuZhuanZhe", query.getXiuZhuanZhe()));
//        }
//        if (StringUtils.isNotBlank(query.getBanBenNian())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("banBenNian", query.getBanBenNian()));
//        }
//        if (StringUtils.isNotBlank(query.getTangHao())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("tangHao", query.getTangHao()));
//        }
//        if (StringUtils.isNotBlank(query.getZiBei())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("ziBei", query.getZiBei()));
//        }
//        if (StringUtils.isNotBlank(query.getBeiZhu())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("beiZhu", query.getBeiZhu()));
//        }
//        if (StringUtils.isNotBlank(query.getTxt())){
//            orgQueryBuilder.should(QueryBuilders.matchQuery("txt", query.getTxt()));
//        }
//
//        if (log.isInfoEnabled())
//            log.info("book-query: " + orgQueryBuilder);
//        SearchResponse searchResponse = client.prepareSearch(INDEX).setTypes(BOOK_TYPE)
//                .setQuery(orgQueryBuilder)
//                //.addHighlightedField("bianHao")
//                //.addHighlightedField("xingShi")
//                .addHighlightedField("tiMing")
//                //.addHighlightedField("juanNum")
//                .addHighlightedField("puJidi")
//                //.addHighlightedField("shiZu")
//                //.addHighlightedField("shiQianZu")
//                .addHighlightedField("xiuZhuanZhe")
//                .addHighlightedField("banBenNian")
//                .addHighlightedField("tangHao")
//                //.addHighlightedField("ziBei")
//                .addHighlightedField("beiZhu")
//                .addHighlightedField("txt")
//                .setHighlighterPreTags("<em style='color:red'>")
//                .setHighlighterPostTags("</em>")
//                .setFrom(from)
//                .setSize(pageSize)
//                .execute()
//                .actionGet();
//
//        long total = searchResponse.getHits().totalHits();
//        result.setTotalNum((int) total);
//        if (log.isInfoEnabled())
//            log.info("book search result-total: " + total);
//        for (SearchHit hit : searchResponse.getHits()) {
//            Book book = JSON.parseObject(hit.getSourceAsString(), Book.class);
//            book.setImageAltForTiMing(book.getTiMing());
//            if (highLight){
//                Map<String, HighlightField> highlightFieldMap = hit.highlightFields();
//                if (log.isInfoEnabled())
//                    log.info("book search high-light: " + highlightFieldMap);
//                highlightFieldMap.forEach((k,v) ->{
//                    String newV = Arrays.stream(v.fragments()).map(Text::toString).reduce("", (s1, s2) -> s1 + s2);
//                    try {
//                        BeanUtils.setProperty(book,k,newV);
//                    } catch (Exception e) {
//                        log.error("error copy property",e);
//                    }
//                });
//            }
//            books.add(book);
//
//        }
//        if (log.isDebugEnabled())
//            log.debug(JSON.toJSONString(books, true));
//        return result;
//    }



}
