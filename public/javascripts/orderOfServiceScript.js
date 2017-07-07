function openModalOrderOfService(element) {
	var orderOfServicedId = $(element).data('order-id');
	document.getElementById("osid").setAttribute('value', orderOfServicedId);
	$('#orderServiceModal').modal('show');
	$('#orderInvoice').load(
			'/OrderOfServiceCRUD/orderofservice?id=' + orderOfServicedId);
}

function openModalCustomerNotification(element) {
	var orderOfServicedId = $(element).data('order-id');
	document.getElementById("osid2").setAttribute('value', orderOfServicedId);
	$('#customerNotificationModal').modal('show');
	$('#notificationArea').load(
			'/OrderOfServiceCRUD/customerNotificationModal?id=' + orderOfServicedId);
}
