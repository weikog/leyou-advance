package com.leyou.search.utils;

import com.leyou.common.utils.JsonUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索高亮工具类
 */
public class HighlightUtils {

    public static void highlightField(NativeSearchQueryBuilder queryBuilder, String field) {
        // 创建高亮字段
        HighlightBuilder.Field titleField = new HighlightBuilder.Field(field);
        // 设置高亮前缀
        titleField.preTags("<font color='red'>");
        // 设置高亮后缀
        titleField.postTags("</font>");
        // 设置文本截断
        titleField.fragmentSize(50);
        // 设置高亮字段(哪些字段需要高亮)
        queryBuilder.withHighlightFields(titleField);
    }

    public static SearchResultMapper highlightBody(Class clazz, String field) {
        return new SearchResultMapper() {
            // 实现搜索结果映射的接口
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse sr,
                                                    Class<T> aClass,
                                                    Pageable pageable) {
                // 第一个参数：List<T> content: 分页数据
                List<T> content = new ArrayList<T>();
                // 获取搜索命中的文档
                SearchHits hits = sr.getHits();
                // SearchHit：代表命中的一篇文档
                for (SearchHit hit : hits) {
                    // 获取该文档对应的json字符串
                    String jsonStr = hit.getSourceAsString();
                    // 把json字符串解析成EsItem对象
                    Object object = JsonUtils.toBean(jsonStr, clazz);
                    // 获取标题高亮字段
                    // key : field的名称(title)
                    HighlightField titleHighlightField = hit.getHighlightFields().get(field);
                    // 判断标题高亮字段是否为空
                    if (titleHighlightField != null) {
                        // 获取标题的高亮内容
                        String title = titleHighlightField.getFragments()[0].toString();
                        // 设置标题高亮内容
                        try {
                            Method method = object.getClass().getMethod("set" + captureName(field), String.class);
                            method.invoke(object, title);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // 添加到集合中
                    content.add((T) object);
                }
                // 第二个参数：Pageable pageable: 分页对象
                // 第三个参数：long total: 总记录数
                return new AggregatedPageImpl<T>(content, pageable,
                        sr.getHits().getTotalHits());
            }
        };
    }

    private static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

}
