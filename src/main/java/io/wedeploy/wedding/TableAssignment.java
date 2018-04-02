package io.wedeploy.wedding;

//import io.wedeploy.wedding.CurlUtil.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class TableAssignment {

	public TableAssignment(Integer id, Guest guest) {
		_id = id;
		_guest = guest;

		if (_tableAssignments.containsKey(id)) {
			throw new RuntimeException(
				"Duplicate table assignment guest: " + guest.getGuestName());
		}

		_tableAssignments.put(_id, _guest);

		_category = _guest.getCategory();

		_range = "A1" + id + ":" + "B" + id;
	}

	public String getRange() {
		return _range;
	}

	public static void update() {
		JSONArray valuesJSONArray = new JSONArray();

		for (int i = 1; i < _tableAssignments.size(); i++) {
			Guest guest = _tableAssignments.get(i);

			String guestName = guest.getGuestName();

			valuesJSONArray.put(new JSONArray()
				.put(guest.getGuestName())
				.put(guest.getRelatedGuestNames())
			);
		}

		JSONObject requestJSONObject = new JSONObject()
			.put("valueInputOption","USER_ENTERED")
			.put("data", new JSONObject()
				.put("range", "Table Assignments!A2:B")
				.put("majorDimension", "ROWS")
				.put("values", valuesJSONArray)
			)
			.put("includeValuesInResponse", true);

		StringBuilder sb = new StringBuilder();

		sb.append("https://sheets.googleapis.com/v4/spreadsheets/");
		sb.append(GoogleSheetsUtil.getSheetID());
		sb.append("/values:batchUpdate");

		System.out.println(requestJSONObject.toString());

		System.out.println(CurlUtil.curl(
			sb.toString(), requestJSONObject.toString()));
	}

	private Integer _id;
	private Guest.Category _category;
	private Guest _guest;
	private String _range;

	private static Map<Integer, Guest> _tableAssignments = new HashMap<>();

}