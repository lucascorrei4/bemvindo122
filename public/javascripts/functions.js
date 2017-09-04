function saveQuickAccount() {
	if ($('#name').val() == '') {
		$("#message").show();
		$("#message").html('Favor, preencha todos os campos!');
		setTimeout('$("#message").hide()', 5000);
		return;
	} else {
		var formDataJSON = {};
		var formData = $('#formQuickAccount').serializeArray();
		decodeURIComponent(formData);
		$.each(formData, function() {
			if (formDataJSON[this.name] !== undefined) {
				if (!formDataJSON[this.name].push) {
					formDataJSON[this.name] = [ formDataJSON[this.name] ];
				}
				formDataJSON[this.name].push(this.value || '');
			} else {
				formDataJSON[this.name] = this.value || '';
			}
		});
		$('#formQuickAccount').load('/savequickaccount', formDataJSON,
				function(response, status) {
					var status = $("#status").val();
					if ('SUCCESS' === status) {
						$("#message").css("color", "gray");
						$("#message").show();
						$("#message").html($("#response").val());
						$("#saveUser").fadeOut();
						$("#btnLogin").fadeIn();
					} else {
						$("#message").css("color", "red");
						$("#message").show();
						$("#message").html($("#response").val());
						setTimeout('$("#message").hide()', 10000);
					}
				});
	}
}

function followordercode() {
	if ($('#orderCode').val() == '') {
		$("#message2").show();
		$("#message2").html('Favor, insira o código do seu pedido!');
		setTimeout('$("#message").hide()', 5000);
		return;
	} else {
		var formData = $('#formFollow').serializeArray();
		$('#formFollow').load('/follow', formData, function(response, status) {
			var status = $("#status").val();
			if ('SUCCESS' === status) {
				$("#message2").css("color", "gray");
				$("#message2").show();
				$("#message2").html($("#response2").val());
				$('#formFollow').reset();
			} else {
				$("#message2").css("color", "red");
				$("#message2").show();
				$("#message2").html($("#response2").val());
				$('#formFollow').reset();
			}
		});
	}
}

function updateRadioValue(name, value) {
	var data = jQuery.param({
		name : name,
		value : value
	});
	$('#accordion').load('/OrderOfServiceCRUD/updateradiovalue', data,
			function(response, status) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					$("#message-" + name).css("color", "gray");
					$("#message-" + name).show();
					$("#message-" + name).html($("#response").val());
					$("#message-" + name).fadeIn();
					var spplittedName = name.split('-');
					$("#collapse" + spplittedName[1]).collapse('show');
					console.log("#collapse" + spplittedName[1])
				} else {
					$("#message-" + name).css("color", "red");
					$("#message-" + name).show();
					$("#message-" + name).html($("#response").val());
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function updateObsOrderStep(name, obs) {
	var data = jQuery.param({
		name : name,
		obs : obs
	});
	$('#accordion').load('/OrderOfServiceCRUD/updateobsorderstep', data,
			function(response, status) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					$("#message-" + name).css("color", "gray");
					$("#message-" + name).show();
					$("#message-" + name).html($("#response").val());
					$("#message-" + name).fadeIn();
					var spplittedName = name.split('-');
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + name).css("color", "red");
					$("#message-" + name).show();
					$("#message-" + name).html($("#response").val());
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function sendSMS(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendsms', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function sendSMSThankful(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendSMSThankful', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					var message = '#message-' + id;
					setTimeout($(message).hide(), 10000);
				}
			});
}

function sendPUSH(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendpush', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function sendPUSHThankful(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendPUSHThankful', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function sendEmail(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendemail', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function sendEmailNotificationToCustomer(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load(
			'/OrderOfServiceCRUD/sendEmailNotificationToCustomer', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function sendEmailNotificationThankful(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendEmailNotificationThankful',
			data, function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "gray");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function closeModal() {
	$('#orderServiceModal').modal('hide');
}

function newsletterTop() {
	var name = document.getElementsByName('mailList.name1')[0].value;
	var mail = document.getElementsByName('mailList.mail1')[0].value;
	if (name === '') {
		$('#message').css('color', 'red');
		$('#message').show();
		$('#message').html('Favor, insira seu nome.');
		setTimeout('$("#message").hide()', 10000);
		return;
	}
	if (mail === '') {
		$('#message').css('color', 'red');
		$('#message').show();
		$('#message').html(
				'Favor, insira seu e-mail no formato nome@provedor.com');
		setTimeout(function() {
			$('#message').hide()
		}, 10000);
		return;
	}
	var data = new Object();
	data.name = name;
	data.mail = mail;
	data.origin = 'homepagetop';
	data.url = window.location.href;
	$('#mailListTop').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					$('#mailListTop').unbind('load');
					window.location.href = "/gratidao";
				} else {
					$("#message").fadeIn();
					$("#message").css("color", "red");
					$("#message").html($("#response").val());
					setTimeout(function() {
						$('#message').hide()
					}, 8000);
				}
			});
}

function newsletterBottom() {
	var name = document.getElementsByName('mailList.name2')[0].value;
	var mail = document.getElementsByName('mailList.mail2')[0].value;
	if (name === '') {
		$('#message2').css('color', 'red');
		$('#message2').show();
		$('#message2').html('Favor, insira seu nome.');
		setTimeout('$("#message2").hide()', 10000);
		return;
	}
	if (mail === '') {
		$('#message2').css('color', 'red');
		$('#message2').show();
		$('#message2').html(
				'Favor, insira seu e-mail no formato nome@provedor.com');
		setTimeout(function() {
			$('#message2').hide()
		}, 10000);
		return;
	}
	var data = new Object();
	data.name = name;
	data.mail = mail;
	data.origin = 'homepagebottom';
	data.url = window.location.href;
	$('#mailListBottom').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status2").val();
				if ('SUCCESS' === status) {
					$('#mailListBottom').unbind('load');
					window.location.href = "/gratidao";
				} else {
					$("#message2").fadeIn();
					$("#message2").css("color", "red");
					$("#message2").html($("#response2").val());
					setTimeout(function() {
						$('#message2').hide()
					}, 8000);
				}
			});
}

function getQueryVariable(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return pair[1];
		}
	}
}

function openModalCustomerThankful(element) {
	var orderOfServicedId = $(element).data('order-id');
	document.getElementById("osid").setAttribute('value', orderOfServicedId);
	$('#notificationArea').load(
			'/OrderOfServiceCRUD/thankfulNotificationModal?id='
					+ orderOfServicedId);
}