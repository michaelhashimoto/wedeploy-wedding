package io.wedeploy.wedding;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class WeddingRestController {

	@RequestMapping(value="/code", method = RequestMethod.GET)
	public RedirectView code(@RequestParam("code") String code) {
		GoogleSheetsUtil.storeAccessToken(code);

		return new RedirectView("/");
	}

	@GetMapping("/guests")
	public String guests() throws Exception {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("name1","value1");
		jsonObject.put("name2","value2");
		jsonObject.put("name3","value3");

		//GoogleSheetsUtil.init();

		return jsonObject.toString();
	}

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public RedirectView login() {
		return new RedirectView(GoogleSheetsUtil.getLoginURL());
	}

}