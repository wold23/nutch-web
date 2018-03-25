package guru.springframework.domain;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class BookIndexConfig {
    /**
     * 索引名称
     */
    private String indexName;
    /**
     * 中文释义
     */
    private String viewName;
    /**
     * 最优key
     */
    private String firstKey;
    /**
     * 扩展key
     */
    private List<String> extKeys = Lists.newArrayList();

    public boolean hasExtKey() {
        return !CollectionUtils.isEmpty(extKeys);
    }

    public BookIndexConfig(String indexName, String viewName, String firstKey) {
        this.indexName = indexName;
        this.viewName = viewName;
        this.firstKey = firstKey;
    }


}
