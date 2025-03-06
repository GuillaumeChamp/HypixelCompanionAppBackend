package com.example.hypixeltrackerbackend.repository;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemPricingRepository extends CrudRepository<ItemPricing, Long>{

    List<ItemPricing> findAllByItemIdAndLastUpdateBetweenOrderByLastUpdate(String itemId, LocalDateTime lastUpdate, LocalDateTime lastUpdate2);
    @Query("""
         select new com.example.hypixeltrackerbackend.data.bazaar.ItemPricing(p.itemId,avg(p.sellPrice),avg(p.buyPrice),:before)
         from ItemPricing p
         where p.lastUpdate between :before and :after
         group by p.itemId""")
    List<ItemPricing> groupAllByTimestampBetween(@Param("before") LocalDateTime before,
                                                 @Param("after") LocalDateTime after);

    void deleteAllByLastUpdateBetween(LocalDateTime before, LocalDateTime after);
}
