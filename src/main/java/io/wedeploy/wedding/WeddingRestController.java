package io.wedeploy.wedding;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeddingRestController {

	@GetMapping("/guests")
	public String guests() throws Exception {
		Guests guests = new Guests();

		return guests.toJSONString();
	}

}