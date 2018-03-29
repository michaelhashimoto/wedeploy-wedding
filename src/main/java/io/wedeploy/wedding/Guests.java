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
			jsonObject.put("first_name", valueJSONArray.getString(1));
			jsonObject.put("last_name", valueJSONArray.getString(2));
			jsonObject.put(
				"checked_in", Boolean.valueOf(valueJSONArray.getString(3)));
			jsonObject.put("table", valueJSONArray.getInt(4));

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

	private String _range = "A:E";
	private List<Guest> _guests = new ArrayList<>();
	private String _id = "1S-upsjmEjzzJ4JI4G55qhdSAoqshIccQewjaErbQwmY";
	private JSONObject _jsonObject;

}