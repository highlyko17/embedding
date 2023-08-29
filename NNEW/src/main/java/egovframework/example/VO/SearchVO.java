package egovframework.example.VO;

import java.util.List;

public class SearchVO {
	List<String> input; // 검색창에 입력된 검색어

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}
}
