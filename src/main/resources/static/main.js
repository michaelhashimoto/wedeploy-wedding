var update_search = function(guests) {
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

var update_tables = function(guests_by_table) {
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

		console.log("table_num: " + table_num);
		console.log("row_num: " + row_num);
		console.log("col_num: " + col_num);

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
		tableDiv.setAttribute("class", "col-sm-" + columns_size);

		var tableHeader = document.createElement("h4");
		tableHeader.innerHTML = "Table #" + table_num;

		var tableBody = document.createElement("ol");

		tableDiv.appendChild(tableHeader);
		tableDiv.appendChild(tableBody);

		update_table(guests_by_table[table_num], tableBody);

		rowDiv.appendChild(tableDiv);
	}

	var unassignedRowDiv = document.createElement("div");
	unassignedRowDiv.setAttribute("class", "row");

	var tableDiv = document.createElement("div");
	tableDiv.setAttribute("class", "col-sm-12");

	var tableHeader = document.createElement("h4");
	tableHeader.innerHTML = "Unassigned";

	var tableBody = document.createElement("ol");

	update_table(guests_by_table[0], tableBody, true);

	tableDiv.appendChild(tableHeader);
	tableDiv.appendChild(tableBody);

	unassignedRowDiv.appendChild(tableDiv);

	guestsByTablesElement.appendChild(unassignedRowDiv);
}

var update_table = function(table_of_guests, tableBody, isUnassigned) {
	var guests = table_of_guests.guests;

	if (guests != null) {
		var min_num_guests = 10;

		if (isUnassigned) {
			min_num_guests = guests.length;
		}

		for (var i = 0; (i < min_num_guests); i++) {
			var guest = guests[i];

			var guest_name = "";

			if (guest != null) {
				guest_name = guest.guest_name;
			}

			var tableGuest = document.createElement("li");
			tableGuest.innerHTML = guest_name;

			tableBody.appendChild(tableGuest);
		}
	}
}

var update_tables_on_node = function() {
	$.ajax({
		type: 'GET',
		url: '/guests_by_table',
		crossDomain: false,
		dataType: 'json',
		success: function() {
			var guests = arguments[0];

			update_tables(guests);
		},
		error: function() {
			console.log(arguments);
		}
	});
}

$.ready(update_search_on_node());
$.ready(update_tables_on_node());

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