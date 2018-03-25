package guru.springframework.controllers;

import com.alibaba.fastjson.JSON;
import guru.springframework.domain.Page;
import guru.springframework.util.ESIndexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {
    @Autowired
    private ESIndexUtil esIndexUtil;

    @RequestMapping("/search")
    public String search(String wd, @RequestParam(value="pageNum",required = false,defaultValue = "0") Integer pageNum, Model model){
        Page result = esIndexUtil.search(wd, pageNum, false);
        model.addAttribute("page",result);
        model.addAttribute("wd",wd);
        return "search/list";
    }

    @RequestMapping("/search/json")
    @ResponseBody
    public Page searchJson(String wd){
        Page result = esIndexUtil.search(wd, 0, false);
        System.out.println(JSON.toJSONString(result));
        return result;
    }
}
