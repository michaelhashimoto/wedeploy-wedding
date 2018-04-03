var update_data = function(guests) {
	var guestsElement = document.getElementById("guests");

	guestsElement.innerHTML = "";

	guests.forEach(guest => {
		var guestElement = document.createElement("tr");

		var guestNameElement = document.createElement("td");
		var guestNameDivElement = document.createElement("div");
		guestNameDivElement.innerHTML = guest.guest_name;
		guestNameElement.appendChild(guestNameDivElement);

		if (guest.related_guest_names != "") {
			var relatedGuestNamesElement = document.createElement("small");

			var relatedGuestNamesWrapperElement = document.createElement("i");

			var relatedGuestNamesTitleElement = document.createElement("b");
			relatedGuestNamesTitleElement.innerHTML = "Related:" + `<br />`

			var relatedGuestNamesBodyElement = document.createElement("i");
			relatedGuestNamesBodyElement.innerHTML = guest.related_guest_names;

			relatedGuestNamesWrapperElement.appendChild(relatedGuestNamesTitleElement);
			relatedGuestNamesWrapperElement.appendChild(relatedGuestNamesBodyElement);

			relatedGuestNamesElement.appendChild(relatedGuestNamesWrapperElement);

			guestNameElement.appendChild(relatedGuestNamesElement);
		}

		var guestPartyElement = document.createElement("td");
		guestPartyElement.innerHTML = guest.category

		var guestMenuChoiceElement = document.createElement("td");
		guestMenuChoiceElement.innerHTML = guest.menu_choice

		var guestCheckInElement = document.createElement("td");

		var guestCheckBoxElement = document.createElement("input");
		guestCheckBoxElement.setAttribute("type", "checkbox");
		guestCheckBoxElement.setAttribute("name", "check_in");

		if (guest.checked_in == "TRUE") {
			guestCheckBoxElement.setAttribute("checked", "");
		}

		var guestTableNumElement = document.createElement("td");
		guestTableNumElement.innerHTML = guest.table_num

		guestsElement.appendChild(guestElement);

		guestElement.appendChild(guestNameElement);
		guestElement.appendChild(guestTableNumElement);
		guestElement.appendChild(guestPartyElement);
		guestElement.appendChild(guestMenuChoiceElement);
		guestElement.appendChild(guestCheckInElement);

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