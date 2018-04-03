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

		GoogleSheetsUtil.init();

		return new RedirectView("/");
	}

	@GetMapping("/guests")
	public String guests() throws Exception {
		String accessToken = GoogleSheetsUtil.getAccessToken();

		if (accessToken == null) {
			return "[]";
		}

		JSONArray jsonArray = Guest.getGuestsJSONArray();

		return jsonArray.toString();
	}

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public RedirectView login() {
		return new RedirectView(GoogleSheetsUtil.getLoginURL());
	}

	/*
		sb = new StringBuilder();

		sb.append("https://www.googleapis.com/drive/v2/files?");

		System.out.println(sb.toString());

		String accessToken = GoogleSheetsUtil.getAccessToken()

		System.out.println(CurlUtil.curl(sb.toString(), null, accessToken));*/

}