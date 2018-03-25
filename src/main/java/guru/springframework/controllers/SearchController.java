package guru.springframework.controllers;

import com.alibaba.fastjson.JSON;
import guru.springframework.domain.Page;
import guru.springframework.util.ESIndexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {
    @Autowired
    private ESIndexUtil esIndexUtil;

    @RequestMapping("/search")
    @ResponseBody
    public Page search(String wd){
        Page result = esIndexUtil.search(wd, 0, true, 0);
        System.out.println(JSON.toJSONString(result));
        return result;
    }
}
