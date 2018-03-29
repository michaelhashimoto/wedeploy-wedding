package io.wedeploy.wedding;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

@Controller
public class WeddingBootController {

	@RequestMapping("/")
	public String index(Map<String, Object> model) throws Exception {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("michael","hashimoto");
		jsonObject.put("crystal","son");

		model.put("jsonObject", jsonObject);

		return "index";
	}

}