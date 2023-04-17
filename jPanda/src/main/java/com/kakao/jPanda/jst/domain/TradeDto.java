package com.kakao.jPanda.jst.domain;

import lombok.Data;

@Data
public class TradeDto {
	//talent
	private String talentNo;
	private String sellerId;
	private String upperCategory_no;
	private String lowerCategory_no;
	private String mainImg;
	private String title;
	private String content;
	private Long bamboo;
	private Long saleBamboo;
	private String summary;
	private String talentStatus; //status
	private Long viewCount;
	private String regDate;
	
	//member
	private String memberId; //id
	private String password;
	private String name;
	private String tel;
	private String email;
	private String birth;
	private String gender;
	private String memberStatus; //status
	private String memberGrade; //grade
	
	//talent_refund
	private String refundPurchaseNo; //purchaseNo
	private String refundStatus; //status
	private String issue;
	private String refundSubmitDate;
	private String refundApproveDate;
	private Long refundBamboo;
	
	//member_grade
	private String grade;
	private Long gradeRatio; //ratio
	
	//exchange
	private String exchangeNo;
	private String exchangeId; //id
	private String exchangeTalentNo; //talentNo
	private Long sales;
	private String exchangeSubmitDate;
	private String exchangeApproveDate;
	private Long total;
	private String exchangeGrade; //grade
	private Long exchangeRatio; //ratio
	private String exchangeStatus; //status
	private Long paymentAmount;
	
	//bamboo_use
	private String bambooUsePurchaseNo; //purchaseNo
	private String buyerId;
	private String bambooUsetalentNo; //talentNo
	private String purchaseDate;
	private String useBamboo;
	
	//ExtraValues
		//TradeMapper.xml selectTradeListByParaMap
	private String listType;
		//TradeMapper.xml selectTradeListByParaMap
	private Long sellCount;
		//TradeMapper.xml selectTradeListByParaMap
	private String refundableDate;
	
	
}
