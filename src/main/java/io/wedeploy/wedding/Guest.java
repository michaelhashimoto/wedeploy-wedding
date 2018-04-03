package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class Guest {

	public Guest(
		String guestName, List<String> relatedGuestNames, Category category) {

		if (_guests.containsKey(guestName)) {
			throw new RuntimeException("Duplicate guest " + guestName);
		}

		_guests.put(guestName, this);

		_guestName = guestName;
		_relatedGuestNames = relatedGuestNames;
		_category = category;
	}

	public String getGuestName() {
		return _guestName;
	}

	public String getRelatedGuestNames() {
		return StringUtils.join(_relatedGuestNames, ",");
	}

	public Category getCategory() {
		return _category;
	}

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject()
			.put("checked_in", false)
			.put("guest_name", _guestName)
			.put("related_guest_names", StringUtils.join(_relatedGuestNames, ","))
			.put("table_number", 1);

		return jsonObject;
	}

	public static Guest getGuest(String guestName) {
		if (_guests.containsKey(guestName)) {
			return _guests.get(guestName);
		}

		return null;
	}

	public static List<Guest> getGuests() {
		List<Guest> guests = new ArrayList<>(_guests.values());

		return guests;
	}

	public static JSONArray getGuestsJSONArray() {
		JSONArray jsonArray = new JSONArray();

		for (Guest guest : _guests.values()) {
			jsonArray.put(guest.getJSONObject());
		}

		return jsonArray;
	}

	public static Boolean exists(String guestName) {
		if (_guests.containsKey(guestName)) {
			return true;
		}

		return false;
	}

	private Category _category;
	private String _guestName;
	private List<String> _relatedGuestNames;

	public static enum Category {
		BRIDES_FAMILY("Bride's Family"), FRIENDS("Friends"),
		GROOMS_FAMILY("Groom's Family");

		public String toString() {
			return _value;
		}

		private Category(String value) {
			_value = value;
		}

		private String _value;
	};

	private static Map<String, Guest> _guests = new TreeMap<>();
}