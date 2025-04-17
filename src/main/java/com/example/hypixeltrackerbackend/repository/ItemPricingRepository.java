package com.example.hypixeltrackerbackend.repository;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemPricingRepository extends JpaRepository<ItemPricing, ItemPricingId> {

    List<ItemPricing> findAllByItemIdAndTimeBetweenOrderByTime(String itemId, LocalDateTime lowerDateTime, LocalDateTime upperDateTime);

    @Query("""
            select new com.example.hypixeltrackerbackend.data.bazaar.ItemPricing(p.itemId,avg(p.sellPrice),avg(p.buyPrice),:before)
            from ItemPricing p
            where p.time between :before and :after
            group by p.itemId""")
    List<ItemPricing> groupAllByTimestampBetween(@Param("before") LocalDateTime before,
                                                 @Param("after") LocalDateTime after);

    void deleteAllInBatchByTimeBetween(LocalDateTime before, LocalDateTime after);

    void deleteAllByTimeBefore(LocalDateTime before);

    List<ItemPricing> findAllByItemId(String itemId);

}
