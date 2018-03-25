package guru.springframework.controllers;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import guru.springframework.domain.BookIndexConfig;
import guru.springframework.util.BookIndexUtil;
import guru.springframework.util.ESIndexUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private String filePath = "D:\\work\\books\\test";


    @Autowired
    private ESIndexUtil esIndexUtil;

    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("filePath", filePath);
        return "/admin/index";
    }

    @RequestMapping("/init")
    @ResponseBody
    public void init() {
        esIndexUtil.initIndexAndType();
    }

    @RequestMapping("/change")
    @ResponseBody
    public void change(String path) {
        this.filePath = path;
    }

    @RequestMapping("/start")
    @ResponseBody
    public void start() {
        Collection<File> files = FileUtils.listFiles(new File(filePath), new String[]{"txt"}, true);
        files.forEach(file -> {
            try {
                List<String> lines = FileUtils.readLines(file, "utf-8");
                Map<String, String> map = Maps.newHashMap();
                lines.forEach(line -> {
                    if (StringUtils.isNotBlank(line)) {
                        String[] array = line.split("=");
                        if (array.length == 2) {
                            String key = array[0].trim();
                            String value = array[1].trim();
                            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                                // 查找是否有对应的配置项
                                Optional<BookIndexConfig> first = BookIndexUtil.config().stream()
                                        .filter(bookIndexConfig -> key.equalsIgnoreCase(bookIndexConfig.getFirstKey()) || bookIndexConfig.hasExtKey()
                                                && bookIndexConfig.getExtKeys().stream().anyMatch(key::equalsIgnoreCase))
                                        .findFirst();

                                if (first.isPresent()) {
                                    BookIndexConfig config = first.get();
                                    map.put(config.getIndexName(), value);
                                }
                            }
                        }
                    }
                });
                if (!map.isEmpty()) {
                    // 做索引
                    esIndexUtil.docIndex(JSON.toJSONString(map));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @RequestMapping("/doc")
    @ResponseBody
    public void doc() {
        Collection<File> files = FileUtils.listFiles(new File(filePath), new String[]{"txt"}, true);
        files.forEach(file -> {
            try {
                String doc = FileUtils.readFileToString(file, "utf-8");
                Map<String,String> docMap= Maps.newHashMap();
                docMap.put("doc",doc);
                esIndexUtil.docIndex(JSON.toJSONString(docMap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
