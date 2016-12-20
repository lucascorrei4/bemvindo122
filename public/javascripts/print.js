$('#create_pdf').click(function () {
	 var divContents = $("#orderInvoice").html();
     var printWindow = window.open('', '', 'height=auto,width=auto');
     printWindow.document.write('<html><head><title></title>');
     printWindow.document.write('</head><body>');
     printWindow.document.write(divContents);
     printWindow.document.write('</body></html>');
     printWindow.document.close();
     setTimeout(function(){ printWindow.print(); }, 3000);
});