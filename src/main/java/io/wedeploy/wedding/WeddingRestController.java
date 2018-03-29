package io.wedeploy.wedding;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeddingRestController {

	@GetMapping("/hello")
	public String counts() throws Exception {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("hello", "world");

		return jsonObject.toString();
	}

}