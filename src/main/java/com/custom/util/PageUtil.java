package com.custom.util;

import com.custom.entity.common.PageEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageUtil {

    public static PageEntity getPage(int pageOrder, int pageNum){//0开始
        PageEntity pg = new PageEntity();
        int offset = pageOrder * pageNum;
        pg.setOffset(offset);
        pg.setLimit(pageNum + 1);
        return pg;
    }

    static public Map next(List list, PageEntity pg){
        Map map = new HashMap();
        boolean hasNext = false;
        if(list.size() == pg.getLimit()){
            // 移除多查出来的这个，并且返回有下一页
            list.remove(list.size()-1);
            hasNext = true;
        }
        map.put("list",list);
        map.put("hasNext",hasNext);
        return map;
    }
}
