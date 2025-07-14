package com.groceryautomation.repository;

import com.groceryautomation.entity.UserStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStoreRepository extends JpaRepository<UserStore, Long> {
    
    List<UserStore> findByUserIdOrderByPriorityAsc(Long userId);
    
    List<UserStore> findByUserIdAndIsActiveOrderByPriorityAsc(Long userId, boolean isActive);
    
    Optional<UserStore> findByUserIdAndStoreId(Long userId, Long storeId);
    
    Optional<UserStore> findByUserIdAndPriority(Long userId, Integer priority);
    
    boolean existsByUserIdAndStoreId(Long userId, Long storeId);
    
    @Query("SELECT COALESCE(MAX(us.priority), 0) FROM UserStore us WHERE us.user.id = :userId")
    Integer findMaxPriorityByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE UserStore us SET us.priority = us.priority + 1 WHERE us.user.id = :userId AND us.priority >= :fromPriority")
    void incrementPrioritiesFrom(@Param("userId") Long userId, @Param("fromPriority") Integer fromPriority);
    
    @Modifying
    @Query("UPDATE UserStore us SET us.priority = us.priority - 1 WHERE us.user.id = :userId AND us.priority > :fromPriority")
    void decrementPrioritiesAfter(@Param("userId") Long userId, @Param("fromPriority") Integer fromPriority);
    
    void deleteByUserIdAndStoreId(Long userId, Long storeId);
} 