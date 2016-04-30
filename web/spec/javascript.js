/*  Common JavaScript functions
    @(#) $Id$
    2016-04-30: mailToLink with up to 3 parameters
    2016-02-09: code.jquery.com commented out; telLink
    2015-04-25: loadPage
    2014-11-10: showImage
    2010-11-26: Dr. Georg Fischer
*/

function upper() {
} // raise

function lower(field) {
    var result = this.form1.prefix.value.toLowerCase();
    this.form1.prefix.value = result;
} // raise

function showImage(imagename, width) {
    document.write('<img src="' + imagename + '" width="' + width + '" title="' + imagename + '" />');
} // showImage

function mailtoLink(mailAddr, subject, body) { // surround a mail address by an <a href="mailto:..."> tag
    if (mailAddr != "undefined" && mailAddr.length > 0) { // non-empty
        document.write('<a href="mailto:' + mailAddr);
        if (subject != "undefined") {
            document.write("?subject=" + subject);
            if (body != "undefined") {
                document.write("\&body=" + body);
            } // with body
        } // with subject
        document.write('">' + mailAddr + '</a>');
    } // with mailAddr
} // mailtoLink, <a href="mailto:...">

function facebookLink(fbName) { // surround a Facebook name by an <a href="https://www.facebook.com/..."> tag
    if (fbName != "undefined" && fbName.length > 0) { // non-empty
        document.write('<a target="_blank" href="https://www.facebook.com/' + fbName + '">' + fbName + '</a>');
    } // non-empty
} // facebookLink, <a href="https://www.facebook.com/:...">

function telLink(localPrefix, telNo) { // surround a telephone number by an <a href="tel:..."> tag
    var intlTelNo = new String(telNo);
    intlTelNo = intlTelNo.replace(/[ \.\-]/g, "");
    if (intlTelNo != "undefined" && intlTelNo.length > 0) { // non-empty
        if (! intlTelNo.match(/^[0\+]/)) {
            intlTelNo = localPrefix + intlTelNo;
        } else if (intlTelNo.match(/^[0]/)){
            intlTelNo = intlTelNo.replace(/^0/, "+49");
        }
        document.write('<a href="tel:' + intlTelNo + '">' + telNo + '</a>');
    } // non-empty
} // telLink, <a href="tel:...">

/*
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
function loadPage(pageRef, ref, breakAfter) {
    jQuery(document).ready(function(){
        jQuery("#ref" + ref).load("servlet?spec=fibu.kontenblatt&amp;fibukto=" + ref);
    });
    document.write('<p id="ref' + ref + '"></p>');
} // loadPage
*/
