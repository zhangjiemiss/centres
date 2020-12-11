package org.origin.centres.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjie
 * @version 2019-07-13
 * @apiNote 物理分页
 */
public interface IPagination {

    /**
     * 分页
     *
     * @param page     当前页
     * @param pageSize 每页大小
     * @param list     分页的数据
     * @return 分页的结果
     */
    default Object buildPage(Integer page, Integer pageSize, List<?> list) {
        return buildPage(page, pageSize, 0, 0, list);
    }

    /**
     * 分页
     *
     * @param page     当前页
     * @param pageSize 每页大小
     * @param list     分页的数据
     * @return 分页的结果
     */
    default Object buildPage(Integer page, Integer pageSize, Integer totalPage, Integer totalSize, List<?> list) {
        return buildPage(page, pageSize, totalPage, totalSize, "current", "size", "pages", "total", "records", list);
    }

    /**
     * 分页
     *
     * @param page     当前页
     * @param pageSize 每页大小
     * @param list     分页的数据
     * @return 分页的结果
     */
    default Object buildPage(Integer page, Integer pageSize, Integer totalPage, Integer totalSize, String pageKey, String pageSizeKey, String totalPageKey, String totalSizeKey, String dataKey, List<?> list) {
        if (list != null && list.size() > 0) {
            if (page == null || page <= 0) page = 1;
            if (pageSize == null || pageSize <= 0) pageSize = 10;
            if (totalPage == null || totalPage <= 0) totalPage = 0;
            if (totalSize == null || totalSize <= 0) totalSize = 0;
            if (list.size() <= totalSize || totalPage == 0) totalSize = list.size();
            int totalPageTemp = (int) Math.ceil(totalSize * 1d / pageSize);
            if (totalPageTemp <= totalPage || totalPage == 0) totalPage = totalPageTemp;
            if (page >= totalPage) page = totalPage;
            int offsetBegin = (page - 1) * pageSize;
            int offsetEnd = page * pageSize;
            offsetEnd = offsetEnd > totalSize ? totalSize : offsetEnd;
            List<?> mList = list.subList(offsetBegin, offsetEnd);
            Map<String, Object> result = new HashMap<>();
            result.put(pageKey, page);
            result.put(pageSizeKey, pageSize);
            result.put(totalPageKey, totalPage);
            result.put(totalSizeKey, totalSize);
            result.put(dataKey, mList);
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put(pageKey, page);
            result.put(pageSizeKey, pageSize);
            result.put(totalPageKey, 0);
            result.put(totalSizeKey, 0);
            result.put(dataKey, list);
            return result;
        }
    }

}
