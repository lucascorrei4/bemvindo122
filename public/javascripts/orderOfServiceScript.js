function openModalOrderOfService(element) {
	var orderOfServicedId = $(element).data('order-id');
	document.getElementById("osid").setAttribute('value', orderOfServicedId);
	$('#orderServiceModal').modal('show');
	$('#orderInvoice').load(
			'/orderofservicecontroller/orderofservice?id=' + orderOfServicedId);
}
