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
		$('#formQuickAccount').load(
				'/savequickaccount',
				formDataJSON,
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
		$("#message").show();
		$("#message").html('Favor, insira o código do seu pedido!');
		setTimeout('$("#message").hide()', 5000);
		return;
	} else {
		var formData = $('#formFollow').serializeArray();
		$('#formFollow').load(
				'/follow',
				formData,
				function(response, status) {
					var status = $("#status").val();
					if ('SUCCESS' === status) {
						$("#message").css("color", "gray");
						$("#message").show();
						$("#message").html($("#response").val());
						$('#small-dialog1').magnificPopup('/acompanhe', 3);
					} else {
						$("#message").css("color", "red");
						$("#message").show();
						$("#message").html($("#response").val());
					}
				});
	}
}