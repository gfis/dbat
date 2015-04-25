/*
	Common JavaScript functions
	@(#) $Id$
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

