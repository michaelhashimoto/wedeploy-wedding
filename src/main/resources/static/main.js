var update_data = function(guests) {
	var guestsElement = document.getElementById("guests");

	guestsElement.innerHTML = "";

	guests.forEach(guest => {
		var guestElement = document.createElement("tr");

		var guestNameElement = document.createElement("td");
		guestNameElement.innerHTML = guest.guest_name;

		var relatedGuestNamesElement = document.createElement("td");
		relatedGuestNamesElement.innerHTML = guest.related_guest_names;

		var guestCheckInElement = document.createElement("td");

		var guestCheckBoxElement = document.createElement("input");
		guestCheckBoxElement.setAttribute("type", "checkbox");
		guestCheckBoxElement.setAttribute("name", "check_in");

		if (guest.checked_in) {
			guestCheckBoxElement.setAttribute("checked", "");
		}

		var guestTableNumElement = document.createElement("td");
		guestTableNumElement.innerHTML = guest.table_num

		guestsElement.appendChild(guestElement);

		guestElement.appendChild(guestNameElement);
		guestElement.appendChild(relatedGuestNamesElement);
		guestElement.appendChild(guestCheckInElement);
		guestElement.appendChild(guestTableNumElement);

		guestCheckInElement.appendChild(guestCheckBoxElement);
	});
}

var update_data_on_node = function() {
	$.ajax({
		type: 'GET',
		url: '/guests',
		crossDomain: false,
		dataType: 'json',
		success: function() {
			var guests = arguments[0];

			update_data(guests);
		},
		error: function() {
			console.log(arguments);
		}
	});
}

$.ready(update_data_on_node());

$("#update").click(update_data_on_node());

$("#search").keyup(function() {
	var $cells = $("td");

	var searchValue = $.trim(this.value).toUpperCase();

	if (searchValue === "") {
		$cells.parent().show();
	}
	else {
		$cells.parent().hide();

		$cells.filter(function() {
			var tdValue = $(this).text().toUpperCase();

			if (tdValue.includes(searchValue)) {
				return true;
			}
			else {
				return false;
			}
		}).parent().show();
	}
});