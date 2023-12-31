package com.kakao.jPanda.board.controller;


import java.util.HashMap;
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.jPanda.board.domain.CategoryDto;
import com.kakao.jPanda.board.domain.FiltersDto;
import com.kakao.jPanda.board.service.BoardService;
import com.kakao.jPanda.common.annotation.NoLoginCheck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
	
	private final BoardService boardService;
	
	
	// 재능 리스트 불러오기 (필터 통합)
	@NoLoginCheck
	@ResponseBody
	@GetMapping("/talents")
	public HashMap<String, Object> talentList(FiltersDto filters){
		log.info("TalentList Controller Start");
		
		HashMap<String, Object> map = boardService.findTalentListByFilter(filters);

		return map;
	}
	
	
	// 대분류 카테고리 리스트 불러오기
	@NoLoginCheck
	@GetMapping("")
	public String upperCategoryList(@RequestParam(required=false) String search, Model model) {
		log.info("UpperCategoryList Controller Start");
		
		List<CategoryDto> upperCategoryList = boardService.findUpperCategoryList();
		
		model.addAttribute("upperCategoryList", upperCategoryList);
		if(search == null) {
	         search = "";
	      }
	    model.addAttribute("search", search);
		
		return "board/board";
	}
	
	
	// 중분류 카테고리를 불러오는 리스트
	@NoLoginCheck
	@ResponseBody
	@GetMapping("/categorys/lower-category-nos") 
	public List<CategoryDto> lowerCategoryListByUpperCategoryNo(@RequestParam Long upperCategoryNo) {
		log.info("LowerCategoryList Controller Start");
		
		List<CategoryDto> lowerCategotyList = boardService.findLowerCategoryListByUpperCategoryNo(upperCategoryNo);
		
		return lowerCategotyList;
	}
}
