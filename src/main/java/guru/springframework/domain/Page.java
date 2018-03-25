package guru.springframework.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Page {

    /**
     * 定义数据总条数
     */

    private int totalNum;

    /**
     * 定义每页显示条数
     */
    public static int pageSize = 10;

    /**
     * 定义数据总页数
     */
    private int totalPage;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 数据集合
     */
    private List<Map<String, String>> list;

    public int getTotalPage() {
        return totalNum / pageSize + 1;
    }
}
