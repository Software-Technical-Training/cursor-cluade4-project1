package com.groceryautomation.repository;

import com.groceryautomation.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.read = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId")
    List<Notification> findByRelatedEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :cutoffDate")
    List<Notification> findOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
} 