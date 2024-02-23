// API call and display response
function create_bucket() {
	name = document.getElementById("bucketName").value;
	if (name.length == 0) {
		alert("Bucket name cannot be empty");
		return false;
	}
	var http = new XMLHttpRequest();
	http.open("POST", "/api/create", true);
	http.setRequestHeader("Content-type", "application/json");
	http.setRequestHeader("X-API-KEY", "somik123");
	http.send(JSON.stringify({ "name": name }));
	http.onload = function () {
		if (http.responseText.length > 0) {
			var api_reply = JSON.parse(http.responseText);
			if (api_reply["status"] == "OK") {
				if (api_reply['content']['name'] == name) {
					location.reload();
					return true;
				}
				alert("Error:\n" + api_reply['error']);
			}
		}
		else {
			alert("Empty response from server.");
		}
	}
	return true;
}


function delete_bucket(sl) {
	id = document.getElementById("id_" + sl).innerHTML;
	if (id == null || id.length == 0) {
		alert("Bucket ID cannot be empty");
		return false;
	}
	if (confirm("Delete bucket with id:\n" + id) != true)
		return false;
	var http = new XMLHttpRequest();
	http.open("GET", "/api/delete/" + id, true);
	http.setRequestHeader("Content-type", "application/json");
	http.setRequestHeader("X-API-KEY", "somik123");
	http.send();
	http.onload = function () {
		if (http.responseText.length > 0) {
			var api_reply = JSON.parse(http.responseText);
			if (api_reply["status"] == "OK") {
				location.reload();
				return true;
			}
			alert("Error:\n" + api_reply['error']);
		}
		else {
			alert("Failed to delete bucket.");
		}
	}
	return false;
}


function generate_new_key(type, sl) {
	el = null;
	id = document.getElementById("id_" + sl).innerHTML;
	if (id.length == 0) {
		alert("Bucket ID cannot be empty");
		return false;
	}
	if (type == "up") {
		key_type = "upload key";
		url = "resetUploadKey/" + id;
		el = document.getElementById("ulk_" + sl);
	}
	else {
		key_type = "download key";
		url = "resetDownloadKey/" + id;
		el = document.getElementById("dlk_" + sl);
	}
	if (confirm("Regenerate " + key_type + " with id:\n" + id) != true)
		return false;

	var http = new XMLHttpRequest();
	http.open("GET", "/api/" + url, true);
	http.setRequestHeader("Content-type", "application/json");
	http.setRequestHeader("X-API-KEY", "somik123");
	http.send();
	http.onload = function () {
		if (http.responseText.length > 0) {
			var api_reply = JSON.parse(http.responseText);
			if (api_reply["status"] == "OK") {
				el.innerHTML = api_reply["content"];
				return true;
			}
			alert("Error:\n" + api_reply['error']);
		}
		else {
			alert("Failed to generate " + key_type + " for id:\n" + id);
		}
	}
	return false;
}


function generate_link(type, sl) {
	url = "";
	id = document.getElementById("id_" + sl).innerHTML;
	if (id.length == 0) {
		alert("Bucket ID cannot be empty");
		return false;
	}
	if (type == "up") {
		key_type = "upload";
		key = document.getElementById("ulk_" + sl).innerHTML;
		url = location.protocol + '//' + location.host + "/" + key_type + "/" + id + "/" + key;
	}
	else {
		key_type = "download";
		key = document.getElementById("dlk_" + sl).innerHTML;
		url = location.protocol + '//' + location.host + "/" + key_type + "/" + id + "/" + key;
	}

	let el = document.getElementById("modal_" + sl);
	copyTextToClipboard(url, el);
}


// Fallback function to copy text to clipboard
function fallbackCopyTextToClipboard(text, el = document.body) {
	var textArea = document.createElement("textarea");
	textArea.value = text;

	// Avoid scrolling to bottom
	textArea.style.top = "0";
	textArea.style.left = "0";
	textArea.style.position = "fixed";

	el.appendChild(textArea);
	textArea.focus();
	textArea.select();
	textArea.setSelectionRange(0, 99999);

	try {
		var successful = document.execCommand('copy');
		var msg = successful ? 'successful' : 'unsuccessful';
		console.log('Fallback: Copying text command was ' + msg);
		console.log(text);
	} catch (err) {
		console.error('Fallback: Oops, unable to copy', err);
	}

	el.removeChild(textArea);
}


// Main function to copy text to clipboard
function copyTextToClipboard(text, el = document.body) {
	if (!navigator.clipboard) {
		fallbackCopyTextToClipboard(text, el);
		return;
	}
	navigator.clipboard.writeText(text).then(
		function () {
			console.log('Async: Copying to clipboard was successful!');
		}, function (err) {
			console.error('Async: Could not copy text: ', err);
		}
	);
}


function upload_file() {
	let form = document.querySelector("form");
	let form_action = document.getElementById("form_action").value;
	let folder_name = document.getElementById("folder_name").value;
	if (folder_name.length > 0) {
		form_action += "/" + folder_name;
	}
	let percent = document.getElementById("upload_btn");
	let http = new XMLHttpRequest();
	http.open("POST", form_action);
	//xhr.setRequestHeader("Content-type", "application/json");
	http.upload.addEventListener("progress", ({ loaded, total }) => {
		let fileLoaded = Math.floor((loaded / total) * 100);
		let fileTotal = Math.floor(total / 1000);
		let fileSize;
		(fileTotal < 1024) ? fileSize = fileTotal + " KB" : fileSize = (loaded / (1024 * 1024)).toFixed(2) + " MB";
		percent.innerHTML = fileLoaded + "%";
		if (loaded == total) {
			percent.innerHTML = fileLoaded + "%";
		}
	});
	let data = new FormData(form);
	http.send(data);
	http.onload = function () {
		if (http.responseText.length > 0) {
			var api_reply = JSON.parse(http.responseText);
			if (api_reply["status"] == "OK") {
				location.reload();
				return true;
			}
			alert("Error:\n" + api_reply['error']);
		}
		else {
			alert("Failed to delete bucket.");
		}
	}
	return false;
}


function delete_file(sl) {
	let file_name = document.getElementById("name_" + sl).innerHTML;
	if (file_name == null || file_name.length == 0) {
		alert("File name cannot be empty");
		return false;
	}
	if (confirm("Delete file:\n" + file_name) != true)
		return false;
	let url = location.href.replace("/bucket/", "/delete/") + "/" + file_name;
	let http = new XMLHttpRequest();
	http.open("GET", url, true);
	http.setRequestHeader("Content-type", "application/json");
	http.send();
	http.onload = function () {
		if (http.responseText.length > 0) {
			let api_reply = JSON.parse(http.responseText);
			if (api_reply["status"] == "OK") {
				location.reload();
				return true;
			}
			alert("Error:\n" + api_reply['error']);
		}
		else {
			alert("Failed to delete file.");
		}
	}
	return false;
}



function disable_key(type, sl) {
	el = null;
	id = document.getElementById("id_" + sl).innerHTML;
	if (id.length == 0) {
		alert("Bucket ID cannot be empty");
		return false;
	}
	if (type == "up") {
		key_type = "upload key";
		url = "disableUploadKey/" + id;
		el = document.getElementById("ulk_" + sl);
	}
	else {
		key_type = "download key";
		url = "disableDownloadKey/" + id;
		el = document.getElementById("dlk_" + sl);
	}
	if (confirm("Disable " + key_type + " with id:\n" + id) != true)
		return false;

	var http = new XMLHttpRequest();
	http.open("GET", "/api/" + url, true);
	http.setRequestHeader("Content-type", "application/json");
	http.setRequestHeader("X-API-KEY", "somik123");
	http.send();
	http.onload = function () {
		if (http.responseText.length > 0) {
			var api_reply = JSON.parse(http.responseText);
			if (api_reply["status"] == "OK") {
				el.innerHTML = api_reply["content"];
				return true;
			}
			alert("Error:\n" + api_reply['error']);
		}
		else {
			alert("Failed to disable " + key_type + " for id:\n" + id);
		}
	}
	return false;
}


function open_bucket(sl) {
	let id = document.getElementById("id_" + sl).innerHTML;
	if (id.length == 0) {
		alert("Bucket ID cannot be empty");
		return false;
	}
	let up_key = document.getElementById("ulk_" + sl).innerHTML;
	let down_key = document.getElementById("dlk_" + sl).innerHTML;
	if (up_key == "DISABLED" || down_key == "DISABLED") {
		alert("Bucket [" + id + "] browsing is disabled.");
	}
	else {
		let url = "/bucket/" + id + "/" + up_key + "/" + down_key;
		window.open(url);
	}
	return false;
}


