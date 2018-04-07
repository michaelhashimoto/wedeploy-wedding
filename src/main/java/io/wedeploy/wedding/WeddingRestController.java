package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	@RequestMapping(value="/string", method = RequestMethod.POST)
	public synchronized String string(@RequestParam("data") String data) {
		JSONObject jsonObject = new JSONObject(data);

		System.out.println(jsonObject);

		String guestName = jsonObject.getString("guest_name");
		Boolean checkedIn = jsonObject.getBoolean("checked_in");

		Guest guest = Guest.getGuest(guestName);

		guest.setCheckedIn(checkedIn);

		GoogleSheetsUtil.writeTableAssignmentsGoogleSheet("Table Assignments!A:F");

		return "{\"message\":\"received\"}";
	}

	@GetMapping("/guests")
	public String guests() throws Exception {
		String accessToken = GoogleSheetsUtil.getAccessToken();

		if (accessToken == null) {
			return "[]";
		}

		GuestUpdaterJob.start();

		JSONArray jsonArray = Guest.getGuestsJSONArray();

		return jsonArray.toString();
	}

	@GetMapping("/guests_by_table")
	public String guestsByTable() throws Exception {
		String accessToken = GoogleSheetsUtil.getAccessToken();

		if (accessToken == null) {
			return "[]";
		}

		Map<Integer, List<Guest>> guestsMap = new TreeMap<>();

		for (int i = 0; i <= 28; i++) {
			guestsMap.put(i, new ArrayList<Guest>());
		}

		for (Guest guest : Guest.getGuests()) {
			String tableNumber = guest.getTableNumber();

			if (tableNumber.equals("")) {
				tableNumber = "0";
			}

			Integer tableNumberKey = Integer.valueOf(tableNumber);

			List<Guest> guests = guestsMap.get(tableNumberKey);

			guests.add(guest);
		}

		JSONArray guestsByTableJSONArray = new JSONArray();

		for (Integer tableNumber : guestsMap.keySet()) {
			JSONArray guestsJSONArray = new JSONArray();

			JSONObject tableJSONObject = new JSONObject()
				.put("table_num", tableNumber)
				.put("guests", guestsJSONArray);

			for (Guest guest : guestsMap.get(tableNumber)) {
				guestsJSONArray.put(guest.getJSONObject());
			}

			guestsByTableJSONArray.put(tableJSONObject);
		}

		return guestsByTableJSONArray.toString();
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