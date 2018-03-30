var request = new XMLHttpRequest();

request.open('GET', '/guests', true);

request.onload = function () {
	var data = JSON.parse(this.response);
	var guestsElement = document.getElementById("guests");

	data.forEach(guest => {
		var guestElement = document.createElement("tr");

		var guestFullNameElement = document.createElement("td");
		guestFullNameElement.innerHTML = guest.first_name + " " + guest.last_name;

		var guestFirstNameElement = document.createElement("td");
		guestFirstNameElement.innerHTML = guest.first_name;

		var guestLastNameElement = document.createElement("td");
		guestLastNameElement.innerHTML = guest.last_name;

		var guestCheckedInElement = document.createElement("td");

		var guestCheckBoxElement = document.createElement("input");
		guestCheckBoxElement.setAttribute("type", "checkbox");
		guestCheckBoxElement.setAttribute("name", "checked_in");

		if (guest.checked_in) {
			guestCheckBoxElement.setAttribute("checked", "");
		}

		var guestTableNumElement = document.createElement("td");
		guestTableNumElement.innerHTML = guest.table

		guestsElement.appendChild(guestElement);

		guestElement.appendChild(guestFullNameElement);
		guestElement.appendChild(guestFirstNameElement);
		guestElement.appendChild(guestLastNameElement);
		guestElement.appendChild(guestCheckedInElement);
		guestElement.appendChild(guestTableNumElement);

		guestCheckedInElement.appendChild(guestCheckBoxElement);
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
