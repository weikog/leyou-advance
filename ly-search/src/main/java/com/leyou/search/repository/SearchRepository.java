package com.leyou.search.repository;

import com.leyou.search.entity.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchRepository extends ElasticsearchRepository<Goods, Long> {
}
