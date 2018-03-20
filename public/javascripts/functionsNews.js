function newsletterFreePageBootstrap() {
	var name = document.getElementsByName('mailList.name1')[0].value;
	var mail = document.getElementsByName('mailList.mail1')[0].value;
	var phone = document.getElementsByName('mailList.phone').length > 0 ? document.getElementsByName('mailList.phone')[0].value : '';
	var typeContentPage = document.getElementsByName('abTestVideoOfText')[0].value;
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
	data.origin = 'newsletterfreepagebootstrap';
	data.typeContentPage = typeContentPage;
	var url = window.location.href;
	data.url = url;
	data.phone = phone;
	$('#newsletterFreePage').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					if ($("#redirectTo").val() !== '') {
						$('#newsletterFreePage').unbind('load');
						window.location.href = $("#redirectTo").val();
					}
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

function newsletterFreePage() {
	var name = document.getElementsByName('mailList.name1')[0].value;
	var mail = document.getElementsByName('mailList.mail1')[0].value;
	var typeContentPage = document.getElementsByName('abTestVideoOfText')[0].value;
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
	data.origin = 'newsletterfreepage';
	data.typeContentPage = typeContentPage;
	var url = [location.protocol, '//', location.host, location.pathname].join('');
	data.url = url;
	$('#newsletterFreePage').load('/application/savemaillist', data,
			function(response, status, xhr) {
		var status = $("#status").val();
		if ('SUCCESS' === status) {
			if ($("#redirectTo").val() !== '') {
				$('#newsletterFreePage').unbind('load');
				window.location.href = $("#redirectTo").val();
			}
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
	data.typeContentPage = "nd";
	var url = [location.protocol, '//', location.host, location.pathname].join('');
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
	data.typeContentPage = "nd";
	var url = [location.protocol, '//', location.host, location.pathname].join('');
	data.url = url;
	$('#newsletterTheSystemTop').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					if ($("#redirectTo").val() !== '') {
						$('#newsletterTheSystemTop').unbind('load');
						window.location.href = $("#redirectTo").val();
					}
					$("#message").fadeIn();
					$("#message").css("color", "gray");
					$("#message").html($("#response").val());
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
	data.typeContentPage = "nd";
	var url = [location.protocol, '//', location.host, location.pathname].join('');
	data.url = url;
	$('#newsletterTheSystemBottom').load('/application/savemaillist', data,
			function(response, status, xhr) {
				var status = $("#status").val();
				if ('SUCCESS' === status) {
					if ($("#redirectTo").val() !== '') {
						$('#newsletterTheSystemBottom').unbind('load');
						window.location.href = $("#redirectTo").val();
					}
					$("#message2").fadeIn();
					$("#message2").css("color", "gray");
					$("#message2").html($("#response2").val());
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


function getGeoIp() {
	$.getJSON("application/cors", function(ok) {
        console.log(ok);
    });
	var geoIp;
	$.getJSON('//ipapi.co/json/', function(data) {
		geoIp = JSON.stringify(data, null, 2);
	});
	return geoIp;
}