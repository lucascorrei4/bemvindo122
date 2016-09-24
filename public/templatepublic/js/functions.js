$(window).load(function() {
	var hiddenModel = document.getElementById('model');
	if (hiddenModel.value) {
		var idSelectedModel = hiddenModel.value;
		var selectedModel = document.getElementById(idSelectedModel);
		highlightModel(selectedModel);
		selectedModel.classList;
		selectedModel.classList.add('active');
	}
	$('#auxSeeMore').val('1a3');
	seeMore();
});

function highlightModel(element) {
	element.style.background = '#F6F6F6';
}

function showModels() {
	var aux = $('#auxSeeMore').val();
	var list;
	if ('1a3' === aux) {
		list = [1, 2, 3];
		list.forEach(function(id) {
			$("#model" + id).fadeOut();
		});
		list = [4, 5, 6];
		list.forEach(function(id) {
			$("#model" + id).fadeIn();
		});
	} else {
		list = [4, 5, 6];
		list.forEach(function(id) {
			$("#model" + id).fadeOut();
		});
		list = [1, 2, 3];
		list.forEach(function(id) {
			$("#model" + id).fadeIn();
		});
	}
}

function seeMore() {
	var aux = $('#auxSeeMore').val();
	if ('1a3' === aux) {
		$('#auxSeeMore').val('4a6');
	} else {
		$('#auxSeeMore').val('1a3');
	}
	showModels();
}

function unHighlightModel(element) {
	var classes = element.className.split(" ");
	var isActive = false;
	for (i = 0; i < classes.length; i++) {
		if (classes[i] == 'active') {
			isActive = true;
		}
	}
	if (!isActive) {
		element.style.background = 'white';
	}
}

function chooseModel(element) {
	element.style.background = '#F6F6F6';
	element.classList;
	element.classList.add('active');
	var currentElement = element.id;
	var hiddenModel = document.getElementById('model');
	hiddenModel.value = currentElement;
	var models = $('.pricing-bottom').find('.pricing-left');
	for (i = 0; i < models.length; i++) {
		var model = models[i].id;
		if (currentElement != model) {
			var m = document.getElementById(model);
			m.classList.remove('active');
			m.style.background = 'white';
		}
	}
}

function saveChildrenChalkBoard() {
	if ($('#model').val() == '') {
		$("#message").show();
		$("#message").html('Favor, escolha um modelo!');
		setTimeout('$("#message").hide()', 5000);
		anchorToPayment();
		return;
	} else {
		var jsonChalkBoard = getJsonChalkBoardChildren();
		if (JSON.stringify(jsonChalkBoard) === '{}') {
			$("#message").show();
			$("#message").html('Favor, preencha os campos do ChalkBoard!');
			setTimeout('$("#message").hide()', 5000);
			anchorToPayment();
			return;
		}
		$
				.ajax({
					type : 'POST',
					url : '/save-new-children-chalkboard',
					dataType : 'json',
					contentType : "application/json",
					data : jsonChalkBoard,
					success : function(data) {
						$("#message").show();
						$("#message")
								.html(
										'Pedido salvo com sucesso! Efetue o pagamento para efetivação!');
						setTimeout('$("#message").hide()', 5000);
						location.reload();
						setTimeout(function() {
							anchorToPayment();
						}, 1000);
					},
					error : function(data) {
						location.reload();
						anchorToMessage();
						$("#message").show()
						$("#message")
								.html(
										'Ocorreu um erro ao salvar. Por favor, tente novamente. :(');
						setTimeout('$("#message").hide()', 5000);
					}
				});
		return false;
	}
}

function saveChildrenChalkBoardObj() {
	if ($('#model').val() == '') {
		$("#message").show();
		$("#message").html('Favor, escolha um modelo!');
		setTimeout('$("#message").hide()', 5000);
		anchorToPayment();
		return;
	} else {
		var formDataJSON = {};
		var formData = $('#formChalkBoardChildren').serializeArray();
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
		$('#formChalkBoardChildren').load(
				'/chalkboardchildrencontroller/savechalkboardchildren',
				formDataJSON,
				function(response, status) {
					anchorToPayment();
					var status = $("#status").val();
					if ('SUCCESS' === status) {
						$("[name='id_transacao']").val($("#orderCode").val());
						$("[name='pagador_nome']").val($("#userName").val());
						$("[name='pagador_email']").val($("#mail").val());
						$("#message").css("color", "gray");
						$("#message").show();
						$("#message").html(
								$("#response").val()
										+ '<br /> Código do Pedido: '
										+ $("#orderCode").val() + '.');
						$("#formChalkBoardChildren :input").attr("disabled",
								true);
					} else {
						$("#message").css("color", "red");
						$("#message").show();
						$("#message").html($("#response").val());
						setTimeout('$("#message").hide()', 10000);
					}
				});
	}
}

function getJsonChalkBoardChildren() {
	var jsonChalkChildren = new Object();
	jsonChalkChildren.model = $('#model').val();
	jsonChalkChildren.name = $('#name').val();
	jsonChalkChildren.birthDay = $('#birthDay').val();
	jsonChalkChildren.age = $('#age').val();
	jsonChalkChildren.bornHeight = $('#bornHeight').val();
	jsonChalkChildren.bornWeight = $('#bornWeight').val();
	jsonChalkChildren.heightNow = $('#heightNow').val();
	jsonChalkChildren.weightNow = $('#weightNow').val();
	jsonChalkChildren.teeth = $('#teeth').val();
	jsonChalkChildren.likeToDo1 = $('#likeToDo1').val();
	jsonChalkChildren.likeToDo2 = $('#likeToDo2').val();
	jsonChalkChildren.likeToDo3 = $('#likeToDo3').val();
	jsonChalkChildren.likeToDo4 = $('#likeToDo4').val();
	jsonChalkChildren.likeToWatch1 = $('#likeToWatch1').val();
	jsonChalkChildren.likeToWatch2 = $('#likeToWatch2').val();
	jsonChalkChildren.know1 = $('#know1').val();
	jsonChalkChildren.know2 = $('#know2').val();
	jsonChalkChildren.know3 = $('#know3').val();
	jsonChalkChildren.likeToEat1 = $('#likeToEat1').val();
	jsonChalkChildren.likeToEat2 = $('#likeToEat2').val();
	jsonChalkChildren.firstWord1 = $('#firstWord1').val();
	jsonChalkChildren.firstWord2 = $('#firstWord2').val();
	jsonChalkChildren.firstWord3 = $('#firstWord3').val();
	jsonChalkChildren.nickName1 = $('#nickName1').val();
	jsonChalkChildren.nickName2 = $('#nickName2').val();
	jsonChalkChildren.nickName3 = $('#nickName3').val();
	var outputJson = JSON.stringify(jsonChalkChildren);
	return outputJson;
}

function anchorToPayment() {
	document.getElementById('toPayment').click();
}

function anchorToMessage() {
	document.getElementById('toMessage').click();
}

function reloadWithMessageAnchor() {
	var anchor = "/#message";
	window.location = anchor;
}

function reloadWithMessageAnchor(reload) {
	window.location.hash = '#message';
	window.location.reload(true);
}
