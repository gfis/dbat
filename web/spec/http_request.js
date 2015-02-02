/*	Activation of a Dbat specification with an Ajax request
 *	@(#) $Id$
 *	2012-05-21, Georg Fischer: reformatted.
 *	The code was adapted from an online example file for the book
 *	"Ajax Hacks" by Bruce Perry. Copyright 2006 O'Reilly Media, Inc., 0-596-10169-4.
 */	
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var request = null;

/** Show some value in an alert box.
 *	Used to prove that the JavaScript is accessible.
 *	@param value the string to be displayed
 */
function showValue(value) {
	alert(value);
} // showValue

/** Send a request for a Dbat specification.
 *	@param link part of the URL behind "servlet?spec="
 */
function ajaxRequest(link) {
	var dbatUrl   = "servlet";
	var dbatQuery = "spec=" + link + "&mode=json";
	// httpRequest("post", dbatUrl				, true, handleResponse2, dbatQuery); 
	httpRequest("get", dbatUrl + "?" + dbatQuery, true, handleResponse2); 
} // showValue

/** Receive and process the response of httpRequest.
 *	The elements <em>thead</em> and <em>tbody</em> returned by the request
 *	must contain the object references to the properties to be set.
 *	Only the first table row of <em>tbody</em> is taken; it should be
 *	the error configuration if the primary SQL select did not find the row.
 *	The regular expression before 'eval' was taken from
 *	{@link http://en.wikipedia.org/wiki/JSON#JavaScript_eval.28.29}
 */
function handleResponse2() {
	if (request.readyState == 4) {
		if (request.status == 200) {
			var text = request.responseText; // should return JSON format
			// alert(text);
			try {
				var obj = !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(text.replace(/"(\\.|[^"\\])*"/g, ''))) &&
						eval('(' + text + ')'); // turn JSON into an object
				if (obj.tables[0].table.tbody.length > 0) {
					// alert("location = " + obj.tables[0].table.tbody[0][1]);
				}
				var ncol = obj.tables[0].table.thead.length;
				var icol = 1; // skip over 'sort' column
				while (icol < ncol) {
					var heads = obj.tables[0].table.thead[icol].split("\.");
					var nhead = heads.length;
					var ihead = 0;
					var id = heads[ihead ++];
					var elem = document.getElementById(id);
					if (0) {
					} else if (nhead == 2) {
						elem[heads[1]] = obj.tables[0].table.tbody[0][icol];
					} else if (nhead == 3) {
						elem[heads[1]]
							[heads[2]] = obj.tables[0].table.tbody[0][icol];
					} else if (nhead == 4) {
						elem[heads[1]]
							[heads[2]]
							[heads[3]] = obj.tables[0].table.tbody[0][icol];
					} else if (nhead == 5) {
						elem[heads[1]]
							[heads[2]]
							[heads[3]]
							[heads[4]] = obj.tables[0].table.tbody[0][icol];
					}
				/*
				*/
					icol ++;
				} // while icol
			} catch(exc) {
				alert("exception catched: " + exc);
			} // try
		} else {
			alert("Problem with XMLHttpRequest communication");
		} // status = 200
	} // readyState = 4
} // handleResponse2

/** Receive and process the response of httpRequest.
 *	The regular expression before 'eval' was taken from
 *	{@link http://en.wikipedia.org/wiki/JSON#JavaScript_eval.28.29}
 */
function handleResponse1() {
	if (request.readyState == 4) {
		if (request.status == 200) {
			var text = request.responseText; // should return JSON format
			// alert(text);
			try {
				var obj = !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(text.replace(/"(\\.|[^"\\])*"/g, ''))) &&
						eval('(' + text + ')'); // turn JSON into an object
				if (obj.tables[0].table.tbody.length > 0) {
					// alert("location = " + obj.tables[0].table.tbody[0][1]);
				}
				with (document.getElementById("aj1")) {
					if (obj.tables[0].table.tbody.length > 0) {
						className = 'valid';
						// value = value + ", " + obj.tables[0].table.tbody[0][1];
						nextSibling.data  = " " + obj.tables[0].table.tbody[0][1];
					} else { 
						className = 'invalid';
						// value = value + ", " + obj.tables[0].table.tbody[0][1];
						nextSibling.data = " ?";
					}
				} // with
			} catch(exc) {
				document.getElementById("aj1").className = 'invalid';
				nextSibling.data = "?exception?";
			} // try
		} else {
			alert("Problem with XMLHttpRequest communication");
		} // status = 200
	} // readyState = 4
} // handleResponse1

/** Initialize a Request object that is already constructed 
 *	@param reqType The HTTP request type such as GET or POST.
 *	@param url The URL of the server program.
 *	@param asynch Whether to send the request asynchronously or not.
 *	@param respHandle The name of the function that will handle the response.
 *	Any fifth parameters represented as arguments[4] are the data a POST request is designed to send. 
 */
function initReq(reqType, url, bool, respHandle) {
    try {
        // Specify the function that will handle the HTTP response 
        request.onreadystatechange = respHandle;
        request.open(reqType, url, bool);
        // if the reqType parameter is POST, then the 5th argument to the function is the POSTed data
        if (reqType.toLowerCase() == "post") {
        	request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            request.send(arguments[4]);
        }   else {
            request.send(null);
        }
    } catch(exc) {
        alert("The application cannot contact the server at the moment. Please try again in a few seconds.\n"
	        	+  "Error detail: " + exc.message);
    }
} // initReq

/** Wrapper function for constructing a Request object.
 *	@param reqType The HTTP request type such as GET or POST.
 *	@param url The URL of the server program.
 *	@param asynch Whether to send the request asynchronously or not.
 *	@param respHandle The name of the function that will handle the response.
 *	Any fifth parameters represented as arguments[4] are the data a POST request is designed to send. 
 */
function httpRequest(reqType, url, asynch, respHandle) {
    if (window.XMLHttpRequest) { // Mozilla-based browsers
        request = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        request = new ActiveXObject("Msxml2.XMLHTTP");
        if (! request) {
            request = new ActiveXObject("Microsoft.XMLHTTP");
        }
    }
    // Very unlikely, but we test for a null request if neither ActiveXObject was initialized
    if (request) {
        // if the reqType parameter is POST, then the 5th argument to the function is the POSTed data
        if (reqType.toLowerCase() != "post") {
            initReq(reqType, url, asynch, respHandle);
        }  else { // the POSTed data
            var args = arguments[4];
            if (args != null && args.length > 0) {
                initReq(reqType, url, asynch, respHandle, args);
            }
        }
    } else {
        alert("Your browser does not permit the use of all of this application's features!");
    }
} // httpRequest
