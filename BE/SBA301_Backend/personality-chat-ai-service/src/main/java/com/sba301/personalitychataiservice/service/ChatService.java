package com.sba301.personalitychataiservice.service;

import com.sba301.personalitychataiservice.dto.ChatRequestDTO;
import com.sba301.personalitychataiservice.dto.ChatResponseDTO;
import com.sba301.personalitychataiservice.entity.ChatMessage;

import java.util.List;

public interface ChatService {

    String createNewSession(String userId);

    ChatResponseDTO processMessage(String userId, ChatRequestDTO request);

    List<ChatMessage> getChatHistory(String userId, String sessionId);

    List<ChatMessage> getChatHistory(String sessionId);

    List<String> getSessionIdsForUser(String userId);

    void deleteSession(String userId, String sessionId);
}