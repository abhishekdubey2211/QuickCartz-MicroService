package com.shopify.orderservice.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class ResponseApi {

	private String responseDateTime;
	private int status;
	private String statusDescription;
	private String revisionId;
	private Object data;
    private String message;
    
	public static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ResponseApi(int status, String statusdescription, Object data) {
		this.responseDateTime = sf.format(new Date());
		this.status = status;
		this.statusDescription = statusdescription;
//		this.revisionId = LoggingInterceptor.requestId;
		this.data = ensureDataIsList(data);
	}

	private Object ensureDataIsList(Object data) {
		if (data instanceof List<?>) {
			return data;
		} else if (data instanceof String) {
			return data;
		} else if (data == null) {
			return null;
		} else {
			List<Object> singleItemList = new ArrayList<>();
			singleItemList.add(data);
			return singleItemList;
		}
	}

	public static ResponseApi createResponse(int status, Object data) {
		String statusDescription = (status == 1) ? "Success" : "Fail";
		return new ResponseApi(status, statusDescription, data);
	}
}
