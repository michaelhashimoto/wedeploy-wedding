var request = new XMLHttpRequest();

request.open('GET', '/guests', true);

request.onload = function () {
	var data = JSON.parse(this.response);
	var guestsElement = document.getElementById("guests");

	data.forEach(guest => {
		var guestElement = document.createElement("tr");

		var guestNameElement = document.createElement("td");
		guestNameElement.innerHTML = guest.name;

		var guestPlusOnesElement = document.createElement("td");
		guestPlusOnesElement.innerHTML = guest.guests;

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
		guestElement.appendChild(guestPlusOnesElement);
		guestElement.appendChild(guestCheckInElement);
		guestElement.appendChild(guestTableNumElement);

		guestCheckInElement.appendChild(guestCheckBoxElement);
	});
}

request.send();

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
