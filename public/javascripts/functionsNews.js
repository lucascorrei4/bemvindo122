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
		$('#message').html('Favor, insira seu e-mail no formato nome@provedor.com');
		setTimeout(function() { $('#message').hide() }, 10000);
		return;
	}
	var data = new Object();
	data.name = name;
	data.mail = mail;
	data.origin = 'newspage';
	data.url = window.location.href;
	$('#newsletterTips').load('/application/savemaillist', data,
			function(response, status, xhr) {
		var status = $("#status").val();
		if ('SUCCESS' === status) {
			$('#newsletterTips').unbind('load');
			window.location.href = "/gratidao";
		} else {
			$("#message").fadeIn();
			$("#message").css("color", "red");
			$("#message").html($("#response").val());
			setTimeout(function() { $('#message').hide() }, 8000);
		}
	});
}