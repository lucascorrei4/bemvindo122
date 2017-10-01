function newsletterTips() {
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
	data.origin = 'newspage';
	var url = [location.protocol, '//', location.host, location.pathname].join('');console.log(url);
	data.url = url;
	$('#newsletterTips').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					$("#message").fadeIn();
					$("#message").css("color", "gray");
					$("#message").html($("#response").val());
					setTimeout(function() {
						$('#message').hide()
					}, 8000);
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

function newsletterTheSystemTop() {
	var name = document.getElementsByName('mailList.name1')[0].value;
	var mail = document.getElementsByName('mailList.mail1')[0].value;
	var action = document.getElementById('action').value;
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
	data.origin = 'capturepagetop';
	var url = [location.protocol, '//', location.host, location.pathname].join('');console.log(url);
	data.url = url;
	$('#newsletterTheSystemTop').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					$('#newsletterTheSystemTop').unbind('load');
					window.location.href = action;
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

function newsletterTheSystemBottom() {
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
	data.origin = 'capturepagebottom';
	var url = [location.protocol, '//', location.host, location.pathname].join('');console.log(url);
	data.url = url;
	$('#newsletterTheSystemBottom').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					$('#newsletterTheSystemBottom').unbind('load');
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