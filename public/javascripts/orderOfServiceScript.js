function openModalOrderOfService(element) {
	var orderOfServicedId = $(element).data('order-id');
	document.getElementById("osid").setAttribute('value', orderOfServicedId);
	$('#orderServiceModal').modal('show');
	$('#orderInvoice').load(
			'/OrderOfServiceCRUD/orderofservice?id=' + orderOfServicedId);
}
