package com.example.demo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.ChargingSuccess;
import com.example.demo.mapper.ChargingSuccessMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ChargingSuccessController {
	@Autowired
	private ChargingSuccessMapper chargingSuccessMapper;
	
	@PostMapping("/chargingSuccess/add")
	public void add(@RequestBody ChargingSuccess chargingSuccess) {
		chargingSuccessMapper.save(chargingSuccess);
	}
	
	@GetMapping("/chargingSuccess/successList")
	public List<ChargingSuccess> findAll(){
		List<ChargingSuccess> successList=chargingSuccessMapper.findAll();
		return successList;
	}
	
	@GetMapping("/extract-backend-success-rate")
    public String extractBackendSuccessRate() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://134.176.134.80:30048/eop/restful/memory/cache/api/day?apiIds=1674614694499393536";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-app-id", "8fd07da5b0364f9aa891db319d92776d");
        headers.set("x-app-key", "4885548cd52247c6b9d5f3bc14c2aac4");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                // 解析JSON响应
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                double backendSuccessRate = rootNode.path("data").path("items").get(0).path("backendSuccessRate").asDouble();
                
                BigDecimal bigDecimal = BigDecimal.valueOf(backendSuccessRate).multiply(BigDecimal.valueOf(100));
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
                double formattedBackendSuccessRate = bigDecimal.doubleValue();

		String backendSuccessRatePercent = String.format("%.2f%%", backendSuccessRate * 100);
		    
                // 创建并保存 ChargingSuccess 对象
                ChargingSuccess chargingSuccess = new ChargingSuccess();
                chargingSuccess.setBackendSuccessRate(formattedBackendSuccessRate);
                chargingSuccessMapper.save(chargingSuccess);
                
                return "Extracted Backend Success Rate: " + backendSuccessRate;
            } catch (Exception e) {
                return "Failed to parse JSON response: " + e.getMessage();
            }
        } else {
            return "Failed to fetch data, status code: " + response.getStatusCodeValue();
        }
    }
}
