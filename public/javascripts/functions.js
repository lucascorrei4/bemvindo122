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

function updateRadioValue(name, value, href) {
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
					$(href).collapse('toggle');
				} else {
					$("#message-" + name).css("color", "red");
					$("#message-" + name).show();
					$("#message-" + name).html($("#response").val());
					setTimeout('$("#message").hide()', 10000);
				}
			});
}

function updateObsOrderStep(name, obs, href) {
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
					$(href).collapse('toggle');
				} else {
					$("#message-" + name).css("color", "red");
					$("#message-" + name).show();
					$("#message-" + name).html($("#response").val());
					setTimeout('$("#message").hide()', 10000);
					$("#collapse" + spplittedName[1]).collapse('show');
				}
			});
}

function sendSMS(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendsms', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function sendSMSThankful(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendSMSThankful', data,
			function(response, status) {
				var status = $("#statusThankful").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#responseThankful").val());
					$("#message-" + id).fadeIn();
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#responseThankful").val());
					setTimeout(function() { $('#message-' + id).hide(); }, 10000);
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function sendWhatsAppThankful(id, value, idUpdate) {
	var isMobile = checkIsMobile();
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate,
		isMobile : isMobile
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendWhatsAppThankful', data,
			function(response, status) {
		var status = $("#statusThankful").val();
		if ('SUCCESS' === status) {
			$("#message-" + id).css("color", "cornflowerblue");
			$("#message-" + id).show();
			window.open($("#responseThankful").val(), '_blank');
			$("#message-" + id).fadeIn();
		} else {
			$("#message-" + id).css("color", "red");
			$("#message-" + id).show();
			$("#message-" + id).html($("#responseThankful").val());
			setTimeout(function() { $('#message-' + id).hide(); }, 10000);
		}
		$("#" + idUpdate + "*").prop("disabled", false);
	});
}

function sendWhatsAppOS(id, value, idUpdate) {
	var isMobile = checkIsMobile();
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate,
		isMobile : isMobile
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendWhatsAppOS', data,
			function(response, status) {
		var status = $("#status-wpp-" + id).val();
		if ('SUCCESS' === status) {
			$("#message-" + id).css("color", "cornflowerblue");
			$("#message-" + id).show();
			window.open($("#response-wpp-" + id).val(), '_blank');
			$("#message-" + id).fadeIn();
		} else {
			$("#message-" + id).css("color", "red");
			$("#message-" + id).show();
			$("#message-" + id).html($("#response-wpp-" + id).val());
			setTimeout(function() { $('#message-' + id).hide(); }, 10000);
		}
		$("#" + idUpdate + "*").prop("disabled", false);
	});
}

function checkIsMobile()  {
	var isMobile = false;
	if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent) 
	    || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0,4))) isMobile = true;
	return isMobile;
}

function sendPUSH(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendpush', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout(function() { $('#message-' + id).hide(); }, 10000);
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function sendPUSHThankful(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendPUSHThankful', data,
			function(response, status) {
				var status = $("#statusThankful").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#responseThankful").val());
					$("#message-" + id).fadeIn();
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#responseThankful").val());
					setTimeout(function() { $('#message-' + id).hide(); }, 10000);
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function sendEmail(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendemail', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout(function() { $('#message-' + id).hide(); }, 10000);
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function sendEmailNotificationToCustomer(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load(
			'/OrderOfServiceCRUD/sendEmailNotificationToCustomer', data,
			function(response, status) {
				var status = $("#status").val();
				var spplittedName = id.split('-');
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#message-" + id).fadeIn();
					$("#collapse" + spplittedName[1]).collapse('show');
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#response").val());
					$("#collapse" + spplittedName[1]).collapse('show');
					setTimeout(function() { $('#message-' + id).hide(); }, 10000);
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function sendEmailNotificationThankful(id, value, idUpdate) {
	var data = jQuery.param({
		id : id,
		value : value,
		idUpdate : idUpdate
	});
	messageActionOnClick(id, idUpdate);
	$('#' + idUpdate).load('/OrderOfServiceCRUD/sendEmailNotificationThankful',
			data, function(response, status) {
				var status = $("#statusThankful").val();
				if ('SUCCESS' === status) {
					$("#message-" + id).css("color", "cornflowerblue");
					$("#message-" + id).show();
					$("#message-" + id).html($("#responseThankful").val());
					$("#message-" + id).fadeIn();
				} else {
					$("#message-" + id).css("color", "red");
					$("#message-" + id).show();
					$("#message-" + id).html($("#responseThankful").val());
					setTimeout(function() { $('#message-' + id).hide(); }, 10000);
				}
				$("#" + idUpdate + "*").prop("disabled", false);
			});
}

function messageActionOnClick(id, idUpdate) {
	$("#" + idUpdate + " *").prop("disabled", true);
	$("#message-" + id).css("color", "gray");
	$("#message-" + id).show();
	$("#message-" + id).html('Enviando. Por favor, aguarde...');
	$("#message-" + id).fadeIn();
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
	$('#thankfulNotificationArea').load(
			'/OrderOfServiceCRUD/thankfulNotificationModal?id='
					+ orderOfServicedId);
}

function openPopup(url,w,h) {
	var newW = w + 100;
	var newH = h + 100;
	var left = (screen.width-newW)/2;
	var top = (screen.height-newH)/2;
	var newwindow = window.open(url, 'name', 'width='+newW+',height='+newH+',left='+left+',top='+top);
	newwindow.resizeTo(newW, newH);
	newwindow.moveTo(left, top);
	newwindow.focus();
	return false;
}