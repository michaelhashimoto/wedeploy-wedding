var request = new XMLHttpRequest();
request.open('GET', '/guests_by_table', false);  // `false` makes the request synchronous
request.send(null);

if (request.status === 200) {
	var guest_tables = JSON.parse(request.response);
}

var init_search = function(guests) {
	var guestsElement = document.getElementById("guests");

	guestsElement.innerHTML = "";

	guests.forEach(guest => {
		var guestElement = document.createElement("tr");
		guestElement.setAttribute("id", "search-" + guest.guest_id);
		guestElement.setAttribute("class", getClassValue(guest));

		var guestNameElement = document.createElement("td");
		var guestNameDivElement = document.createElement("div");
		guestNameDivElement.setAttribute("class", "guest-name");
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

		var guestMenuChoiceElement = document.createElement("td");
		guestMenuChoiceElement.setAttribute("class", "menu-choice")
		guestMenuChoiceElement.innerHTML = guest.menu_choice

		var guestCheckInElement = document.createElement("td");

		var guestCheckBoxElement = document.createElement("input");
		guestCheckBoxElement.setAttribute("class", "check-in");
		guestCheckBoxElement.setAttribute("type", "checkbox");
		guestCheckBoxElement.setAttribute("name", "check_in");
		guestCheckBoxElement.setAttribute("id", "check-in-" + guest.guest_id);
		guestCheckBoxElement.setAttribute("onclick", "update_user_check_in(\"" + guest.guest_id + "\", \"" + guest.guest_name + "\")");

		if (guest.checked_in == "TRUE") {
			guestCheckBoxElement.setAttribute("checked", "");
		}

		var guestTableNumElement = document.createElement("td");
		guestTableNumElement.setAttribute("class", "table-number");

		var guestTableNumSelectElement = get_table_number_select();
		guestTableNumSelectElement.setAttribute("id", "table-number-" + guest.guest_id);
		guestTableNumSelectElement.setAttribute("onchange", "update_user_table_num(\"" + guest.guest_id + "\", \"" + guest.guest_name + "\")");

		if (guest.table_num != "") {
			guestTableNumSelectElement.selectedIndex = parseInt(guest.table_num);
		}
		else {
			guestTableNumSelectElement.selectedIndex = 0;
		}

		guestTableNumElement.appendChild(guestTableNumSelectElement);

		guestsElement.appendChild(guestElement);

		guestElement.appendChild(guestNameElement);
		guestElement.appendChild(guestTableNumElement);
		guestElement.appendChild(guestMenuChoiceElement);
		guestElement.appendChild(guestCheckInElement);

		guestCheckInElement.appendChild(guestCheckBoxElement);
	});
}

var get_table_number_select = function() {
	var guestTableNumSelectElement = document.createElement("select");
	guestTableNumSelectElement.setAttribute("class", "form-control");

	var guestTableNumOptionElement = document.createElement("option");
	guestTableNumOptionElement.innerHTML = "---";

	guestTableNumSelectElement.appendChild(guestTableNumOptionElement);

	for (var i = 0; i < guest_tables.length; i++) {
		var guestTableNumOptionElement = document.createElement("option");
		guestTableNumOptionElement.innerHTML = "Table #" + (i + 1);

		guestTableNumSelectElement.appendChild(guestTableNumOptionElement);
	}

	return guestTableNumSelectElement;
}

var init_search_on_node = function() {
	$.ajax({
		type: 'GET',
		url: '/guests',
		crossDomain: false,
		dataType: 'json',
		success: function() {
			var guests = arguments[0];

			init_search(guests);
		},
		error: function() {
			console.log(arguments);
		}
	});
}

var init_tables = function(guests_by_table) {
	var guestsByTablesElement = document.getElementById("guestsByTables");

	guestsByTablesElement.innerHTML = "";

	var columns_per_row = 4;
	var columns_size = 3;

	for (i = 0; i < (guests_by_table.length - 1); i++) {
		var rowDiv = null;
		var rowBody = null;

		var row_num = parseInt((i / columns_per_row) + 1);
		var col_num = parseInt(i % columns_per_row);
		var table_num = parseInt(i) + 1;

		if ((i % columns_per_row) == 0) {
			rowDiv = document.createElement("div");
			rowDiv.setAttribute("class", "row");
			rowDiv.setAttribute("id", "row" + row_num);

			guestsByTablesElement.appendChild(rowDiv);
		}
		else {
			rowDiv = document.getElementById("row" + row_num);
		}

		var tableDiv = document.createElement("div");
		tableDiv.setAttribute("class", "col-sm-" + columns_size + " ");

		var tableHeader = document.createElement("h4");
		tableHeader.setAttribute("class", "table-header");
		tableHeader.innerHTML = "Table #" + table_num;

		var tableBody = document.createElement("div");

		tableDiv.appendChild(tableHeader);
		tableDiv.appendChild(tableBody);

		update_table(guests_by_table[table_num], tableBody, tableHeader);

		rowDiv.appendChild(tableDiv);
	}

	var unassignedRowDiv = document.createElement("div");
	unassignedRowDiv.setAttribute("class", "row");

	var tableDiv = document.createElement("div");
	tableDiv.setAttribute("class", "col-sm-" + columns_size);

	var tableHeader = document.createElement("h4");
	tableHeader.setAttribute("class", "table-header");
	tableHeader.innerHTML = "Unassigned";

	var tableBody = document.createElement("div");

	update_table(guests_by_table[0], tableBody, tableHeader, true);

	tableDiv.appendChild(tableHeader);
	tableDiv.appendChild(tableBody);

	unassignedRowDiv.appendChild(tableDiv);

	guestsByTablesElement.appendChild(unassignedRowDiv);
}

var update_table = function(table_of_guests, tableBody, tableHeader, isUnassigned) {
	var guests = table_of_guests.guests;

	if (guests != null) {
		var min_num_guests = 10;

		if (isUnassigned) {
			min_num_guests = guests.length;
		}

		console.log(table_of_guests);
		console.log(min_num_guests);
		console.log(guests.length);

		var i = 0;

		while (true) {
			var hasMinimumNumberOfGuests = false;

			if (i >= min_num_guests) {
				hasMinimumNumberOfGuests = true;
			}

			var usedAllGuests = false;

			if (i >= guests.length) {
				usedAllGuests = true;
			}

			if (hasMinimumNumberOfGuests && usedAllGuests) {
				break;
			}

			var guest = guests[i];

			var guest_name = (i + 1) + ". ---";

			if (guest != null) {
				guest_name = (i + 1) + ". " + guest.guest_name;
			}

			var tableGuest = document.createElement("div");

			if (guest != null) {
				if (guest.menu_choice == "Vegetarian") {
					guest_name += " &#9752;";

					var tableHeaderValue = tableHeader.innerHTML;

					tableHeaderValue += " &#9752;";

					tableHeader.innerHTML = tableHeaderValue;
				}
			}

			tableGuest.innerHTML = guest_name;

			var classValue = "table-entry";

			if (guest != null) {
				classValue += " " + getClassValue(guest);
			}

			tableGuest.setAttribute("class", classValue);

			tableBody.appendChild(tableGuest);

			i++;
		}
	}
}

var getClassValue = function(guest) {
	if (guest.category == "EN's Guests" && guest.checked_in == "TRUE") {
		return "en-arrived";
	}
	else if (guest.category == "EN's Guests") {
		return "en-absent";
	}
	else if (guest.category == "Bride's Parent's Guests" && guest.checked_in == "TRUE") {
		return "bride-arrived";
	}
	else if (guest.category == "Bride's Parent's Guests") {
		return "bride-absent";
	}
	else if (guest.category == "Groom's Parent's Guests" && guest.checked_in == "TRUE") {
		return "groom-arrived";
	}
	else if (guest.category == "Groom's Parent's Guests") {
		return "groom-absent";
	}
}

var init_tables_on_node = function() {
	$.ajax({
		type: 'GET',
		url: '/guests_by_table',
		crossDomain: false,
		dataType: 'json',
		success: function() {
			var guests = arguments[0];

			init_tables(guests);
		},
		error: function() {
			console.log(arguments);
		}
	});
}

var remove_login = function() {
	$.ajax({
		type: 'GET',
		url: '/is_logged_in',
		crossDomain: false,
		dataType: 'text',
		success: function() {
			var isLoggedIn = arguments[0];

			if (isLoggedIn == "true") {
				var loginButton = document.getElementById("loginButton");

				loginButton.innerHTML = "";
			}
		},
		error: function() {
			console.log(arguments);
		}
	});
}

$.ready(remove_login());
$.ready(init_search_on_node());
$.ready(init_tables_on_node());

$("#search").keyup(function() {
	var $cells = $("td");

	var searchValue = $.trim(this.value).toUpperCase();

	if (searchValue === "") {
		var $parents = $cells.parent();

		$parents.show();

		var searchResultsElement = document.getElementById("search-results");

		searchResultsElement.innerHTML = "Showing " + $parents.length + " results.";
	}
	else {
		$cells.parent().hide();

		var $parents = $cells.filter(function() {
			var tdValue = $(this).text().toUpperCase();

			if (tdValue.includes(searchValue)) {
				return true;
			}
			else {
				return false;
			}
		}).parent();

		$parents.show();

		var searchResultsElement = document.getElementById("search-results");

		searchResultsElement.innerHTML = "Showing " + $parents.length + " results.";
	}
});

$("#simple").click(function() {
    var hashi = {data:"lkas"};

    $.ajax({
        type: 'POST',
        url: '/string',
        crossDomain: false,
        dataType: 'json',
        success: function() {
            console.log(arguments);
        },
        error: function() {
            console.log(arguments);
        },
        data: hashi
    });
});

$("#fancy").click(function() {
	var data = {"first":"michael","last":"hashimoto"};

    postJSONData("/string", data);
});

var update_user_check_in = function(id, guest_name) {
	var $checkIn = $("#check-in-" + id);

	if ($checkIn[0].checked) {
		var data = {"guest_name":guest_name, "checked_in":true};

	    postJSONData("/string", data);
	}
	else {
		var data = {"guest_name":guest_name, "checked_in":false};

	    postJSONData("/string", data);
	}
}

var update_user_table_num = function(id, guest_name) {
	var tableNumSelectElement = document.getElementById("table-number-" + id);

	console.log(tableNumSelectElement);

	var tableNumber = tableNumSelectElement.selectedIndex;

	var data = {"guest_name":guest_name, "table_num":tableNumber};

	postJSONData("/string", data);
}

var postJSONData = function(url, data) {
    var request = {data:JSON.stringify(data)};

    $.ajax({
        type: 'POST',
        url: url,
        crossDomain: false,
        dataType: 'json',
        success: function() {
            console.log(arguments);
        },
        error: function() {
            console.log(arguments);
        },
        data: request
    });
}

var update_search = function(guests) {
	guests.forEach(guest => {
		var guestElement = document.getElementById("search-" + guest.guest_id);
		guestElement.setAttribute("class", getClassValue(guest));

		var tdElements = guestElement.childNodes;

		var selectElement = tdElements[1].getElementsByTagName("select");

		if (guest.table_num != "") {
			selectElement[0].selectedIndex = parseInt(guest.table_num);
		}
		else {
			selectElement[0].selectedIndex = 0;
		}

		var checkedInElements = tdElements[3].getElementsByTagName("input");

		if (guest.checked_in == "TRUE") {
			checkedInElements[0].setAttribute("checked", "");
		}
		else {
			checkedInElements[0].removeAttribute("checked");
		}
	});
}

var update_search_on_node = function() {
	$.ajax({
		type: 'GET',
		url: '/guests',
		crossDomain: false,
		dataType: 'json',
		success: function() {
			var guests = arguments[0];

			update_search(guests);
		},
		error: function() {
			console.log(arguments);
		}
	});
}

var intervalID = setInterval(function(){
	update_search_on_node();
	init_tables_on_node();
}, 5000);

