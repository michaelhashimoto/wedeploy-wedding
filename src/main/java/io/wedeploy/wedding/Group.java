package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {

	protected Group(
		String primaryGuestName, List<String> guestNames,
		Guest.Category category) {

		if (_groups.containsKey(primaryGuestName)) {
			throw new RuntimeException("Duplicate group " + primaryGuestName);
		}

		_primaryGuestName = primaryGuestName;
		_category = category;

		_groups.put(primaryGuestName, this);

		for (String guestName : guestNames) {
			List<String> relatedGuestNames = new ArrayList<>(guestNames);

			relatedGuestNames.remove(guestName);

			_guests.add(new Guest(guestName, relatedGuestNames, category));
		}
	}

	private Guest.Category _category;
	private List<Guest> _guests = new ArrayList<>();
	private String _primaryGuestName;

	private static Map<String, Group> _groups = new HashMap<>();

}