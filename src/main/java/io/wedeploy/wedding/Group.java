package io.wedeploy.wedding;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Group {

	public Group(
		String primaryGuestName, List<String> guestNames,
		Guest.Category category) {

		if (_primaryGuestNames.contains(primaryGuestName)) {
			throw new RuntimeException("Duplicate guest " + primaryGuestName);
		}

		_primaryGuestName = primaryGuestName;
		_category = category;

		_primaryGuestNames.add(primaryGuestName);

		for (String guestName : guestNames) {
			List<String> relatedGuestNames = new ArrayList<>(guestNames);

			relatedGuestNames.remove(guestName);

			_guests.add(new Guest(guestName, relatedGuestNames, category));
		}
	}

	private Guest.Category _category;
	private List<Guest> _guests = new ArrayList<>();
	private String _primaryGuestName;

	private static List<String> _primaryGuestNames = new ArrayList<>();

}