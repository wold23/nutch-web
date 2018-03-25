package guru.springframework.util;

import com.google.common.collect.Lists;
import guru.springframework.domain.BookIndexConfig;

import java.util.List;

public class BookIndexUtil {
    public static List<BookIndexConfig> config(){
        List<BookIndexConfig> indexConfigs = Lists.newArrayList();
        BookIndexConfig title = new BookIndexConfig("title","书名","title");
        title.getExtKeys().add("书名");
        indexConfigs.add(title);

        BookIndexConfig responsiblePerson = new BookIndexConfig("responsible_person","责任者","责任者");
        indexConfigs.add(responsiblePerson);

        BookIndexConfig decade = new BookIndexConfig("decade","年代","年代");
        indexConfigs.add(decade);

        BookIndexConfig address = new BookIndexConfig("address","地点","地点");
        indexConfigs.add(address);

        BookIndexConfig gong_bu_dan_wei = new BookIndexConfig("gong_bu_dan_wei","公布单位","公布单位");
        indexConfigs.add(gong_bu_dan_wei);

        BookIndexConfig wen_xian_chu_chu = new BookIndexConfig("wen_xian_chu_chu","文献出处","文献出处");
        indexConfigs.add(wen_xian_chu_chu);

        BookIndexConfig keyword = new BookIndexConfig("keyword","关键词","关键词");
        indexConfigs.add(keyword);

        BookIndexConfig topic_word = new BookIndexConfig("topic_word","主题词","主题词");
        indexConfigs.add(topic_word);

        BookIndexConfig ti_ba_yin_ji = new BookIndexConfig("ti_ba_yin_ji","题跋印记","题跋印记");
        indexConfigs.add(ti_ba_yin_ji);

        BookIndexConfig chu_ban_zhe = new BookIndexConfig("chu_ban_zhe","出版者","出版者");
        indexConfigs.add(chu_ban_zhe);

        BookIndexConfig chu_ban_shijian = new BookIndexConfig("chu_ban_shijian","出版时间","出版时间");
        indexConfigs.add(chu_ban_shijian);

        BookIndexConfig chu_ban_di_dian = new BookIndexConfig("chu_ban_di_dian","出版地点","出版地点");
        indexConfigs.add(chu_ban_di_dian);

        BookIndexConfig fuzhu_shuoming = new BookIndexConfig("fuzhu_shuoming","附注说明","附注说明");
        indexConfigs.add(fuzhu_shuoming);

        BookIndexConfig zhu_ti_ci = new BookIndexConfig("zhu_ti_ci","主题词","主题词");
        indexConfigs.add(zhu_ti_ci);

        BookIndexConfig zhai_yao = new BookIndexConfig("zhai_yao","摘要","摘要");
        indexConfigs.add(zhai_yao);

        BookIndexConfig pinyin_timing = new BookIndexConfig("pinyin_timing","拼音题名","拼音题名");
        indexConfigs.add(pinyin_timing);

        return indexConfigs;
    }
}
