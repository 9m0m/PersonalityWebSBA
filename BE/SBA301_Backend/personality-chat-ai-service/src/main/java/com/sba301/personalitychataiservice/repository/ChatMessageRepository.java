package com.sba301.personalitychataiservice.repository;

import com.sba301.personalitychataiservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    long countBySessionIdAndSender(String sessionId, String sender);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMessage m WHERE m.sessionId = :sessionId")
    int deleteBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId AND cm.sender = 'user' ORDER BY cm.createdAt ASC")
    List<ChatMessage> findUserMessagesBySessionId(@Param("sessionId") String sessionId);

     //Lấy danh sách các session ID của một người dùng, sắp xếp theo thời gian mới nhất trước
    @Query("SELECT c.sessionId FROM ChatMessage c WHERE c.userId = :userId GROUP BY c.sessionId ORDER BY MAX(c.createdAt) DESC")
    List<String> findSessionIdsByUserId(@Param("userId") String userId);

     //dùng để kiểm tra quyền sở hữu session
    boolean existsByUserIdAndSessionId(String userId, String sessionId);
}