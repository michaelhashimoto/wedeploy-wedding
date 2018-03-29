var request = new XMLHttpRequest();

request.open('GET', '/guests', true);

request.onload = function () {
	var data = JSON.parse(this.response);
	var container = document.getElementById("container");

	var guestListElement = document.createElement("ul");

	container.appendChild(guestListElement);

	data.forEach(guest => {
		console.log(guest);

		var guestElement = document.createElement("li");
		guestElement.innerHTML = guest.name;

		var guestInfoElement = document.createElement("ul");

		var guestFirstNameElement = document.createElement("li");
		guestFirstNameElement.innerHTML = "First Name: " + guest.first_name;

		var guestLastNameElement = document.createElement("li");
		guestLastNameElement.innerHTML = "Last Name: " + guest.last_name;

		var guestCheckedInElement = document.createElement("li");
		guestCheckedInElement.innerHTML = "Checked In: " + guest.checked_in;

		var guestTableElement = document.createElement("li");
		guestTableElement.innerHTML = "Table #: " + guest.table

		guestElement.appendChild(guestInfoElement);

		guestListElement.appendChild(guestElement);

		guestInfoElement.appendChild(guestFirstNameElement);
		guestInfoElement.appendChild(guestLastNameElement);
		guestInfoElement.appendChild(guestCheckedInElement);
		guestInfoElement.appendChild(guestTableElement);
	});
}

request.send();