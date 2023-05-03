package com.kakao.jPanda.jst.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.jPanda.jst.domain.TradeDto;
import com.kakao.jPanda.jst.service.TradeService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class TradeWebSocketHandler extends TextWebSocketHandler{
	
	private final TradeService tradeService;
	
	@Autowired
	public TradeWebSocketHandler(TradeService tradeService) {
		this.tradeService = tradeService;
	}
	
	//Session 관리를 위한 Map
	private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
	
	//ObjectWrapper
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String memberId = (String) session.getAttributes().get("memberId");
		log.info("afterConnectionEstablished memberId : " +  memberId);
		if (memberId != null) {
			sessionMap.put(memberId, session);
		}
		
		sessionMap.forEach((key, value)->{
			log.info("session map>> id : {} session : {}", key, value);
		});
		
	}
	
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		List<TradeDto> messageTradeDtoList = objectMapper.readValue((String)message.getPayload(), new TypeReference<List<TradeDto>>() {});
		log.info("handleMessage before messageTradeDtoList : " + messageTradeDtoList);
		
		List<TradeDto> tradeDtoList = messageTradeDtoList.stream().map(tradeDto -> {
																	TradeDto resultTradeDto = findTradeDto(tradeDto);
																	combineTradeDtoValues(resultTradeDto);
																	return resultTradeDto;
																   }).collect(Collectors.toList());
		
		log.info("handleMessage after tradeDtoList : " + tradeDtoList);
		
		Map<String, List<TradeDto>> groupedTradeDtoMap = tradeDtoList.stream().collect(Collectors.groupingBy(TradeDto::getMemberId));
		groupedTradeDtoMap.forEach((memberId, trades) -> {
			WebSocketSession receiverSession = sessionMap.get(memberId);
			
			if (receiverSession != null) {
				try {
					receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(trades)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("afterConnectionClosed removed memberId : {} session : {}", (String) session.getAttributes().get("memberId"),sessionMap.values().remove(session));
		sessionMap.values().remove(session);
		
	}
	
	/**
	 * param을 통해 talent TB, exchange TB, talent_refund TB의 PK로 row 조회
	 * @param tradeDto
	 * @return 조회된 TradeDto
	 */
	private TradeDto findTradeDto(TradeDto tradeDto) {
		if (tradeDto.getTalentNo() != null) {
	        return tradeService.findTradeByTalentNo(tradeDto.getTalentNo());
	        
		} else if(tradeDto.getExchangeNo() != null) {
			return tradeService.findExchangeByExchangeNo(tradeDto.getExchangeNo());
			
		} else if(tradeDto.getRefundPurchaseNo() != null) {
			return tradeService.findRefundByRefundPurchaseNo(tradeDto.getRefundPurchaseNo());
			
		} else {
			return null;
		}
		
	}
	
	/**
	 * TradeDto의 sellerId, getBuyerId, getExchangeId를 memberId로 combine
	 * 해당 dto의 type에 따라 listType 세팅
	 * @param tradeDto
	 */
	private void combineTradeDtoValues(TradeDto tradeDto) {
		if (tradeDto.getSellerId() != null) {
			tradeDto.setListType("sell");
			tradeDto.setMemberId(tradeDto.getSellerId());
		} else if (tradeDto.getBuyerId() != null) {
			tradeDto.setListType("refund");
			tradeDto.setMemberId(tradeDto.getBuyerId());
		} else if (tradeDto.getExchangeId() != null) {
			tradeDto.setListType("sell");
			tradeDto.setMemberId(tradeDto.getExchangeId());
		}
		
	}
	
}//end class