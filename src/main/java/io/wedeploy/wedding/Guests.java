package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Guests {

	public Guests() {
		StringBuilder sb = new StringBuilder();

		sb.append("https://sheets.googleapis.com/v4/spreadsheets/");
		sb.append(_id);
		sb.append("/values/");
		sb.append(_range.replace(":", "%3A"));

		_jsonObject = new JSONObject(CurlUtil.curl(sb.toString()));

		JSONArray valuesJSONArray = _jsonObject.getJSONArray("values");

		for (int i = 1; i < valuesJSONArray.length(); i++) {
			JSONArray valueJSONArray = valuesJSONArray.getJSONArray(i);

			JSONObject jsonObject = new JSONObject();

			jsonObject.put("name", valueJSONArray.getString(0));
			jsonObject.put("guests", valueJSONArray.getString(1));
			jsonObject.put("type", valueJSONArray.getString(2));
			jsonObject.put("menu", valueJSONArray.getString(3));
			jsonObject.put("adult_count", valueJSONArray.getInt(4));
			jsonObject.put("children_count", valueJSONArray.getInt(5));
			jsonObject.put("table_num", valueJSONArray.getInt(6));

			String checkIn = valueJSONArray.getString(7);

			if (checkIn.equals("arrived")) {
				jsonObject.put("check_in", true);
			}
			else if (checkIn.equals("absent")) {
				jsonObject.put("check_in", false);
			}

			_guests.add(new Guest(jsonObject));
		}
	}

	public List<Guest> getGuests() {
		return _guests;
	}

	public String toJSONString() {
		JSONArray jsonArray = new JSONArray();

		for (Guest guest : _guests) {
			jsonArray.put(guest.getJSONObject());
		}

		return jsonArray.toString();
	}

	private String _range = "Check-in!A:H";
	private List<Guest> _guests = new ArrayList<>();
	private String _id = "1S-upsjmEjzzJ4JI4G55qhdSAoqshIccQewjaErbQwmY";
	private JSONObject _jsonObject;

}