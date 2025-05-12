package com.example.erv.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActionCount {
	private Long pauseStartCount;
	private Long pauseEndCount;
	private Long businessStartCount;
	private Long businessEndCount;
	private Long checkInCount;
	private Long checkOutCount;
	public ActionCount(Long pauseStartCount, Long pauseEndCount, Long businessStartCount, Long businessEndCount, Long checkInCount, Long checkOutCount) {
		super();
		this.pauseStartCount = pauseStartCount == null ? 0 :pauseStartCount;
		this.pauseEndCount = pauseEndCount == null ? 0 :pauseEndCount;
		this.businessStartCount = businessStartCount == null ? 0 :businessStartCount;
		this.businessEndCount = businessEndCount == null ? 0 :businessEndCount;
		this.checkInCount = checkInCount == null ? 0 :checkInCount;
		this.checkOutCount = checkOutCount == null ? 0 :checkOutCount;
	}
}
