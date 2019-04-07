$('#form').submit(function() {
	resetTable();
	return true;
});
    		
function newstep() {
	var newRowContent = "<tr style=\"width: 240px;\">"
			+ "<td><input type=\"text\" style=\"width: 100%\" class=\"title\" value=\"\" placeholder=\"Descreva aqui o pedido\" onclick=\"this.select()\" accesskey=\"0\" /></td>"
			+ "<td><button id=\"removerItem\" type=\"button\" class=\"btn btn-danger btn-circle\" onclick=\"$(this).closest('tr').remove();setTimeout(function() { resetTable() }, 500); \">"
			+ "<i class=\"fa fa-times\"></i></button></td></tr>";
	$("#stepsTable tbody").append(newRowContent);
	setTimeout(function() {
		resetTable()
	}, 500);
}

function resetTable() {
	var inputContent = [];
	var content;
	var obj;
	var i = 0;
	$('#stepsTable tr').each(function() {
		if (i > 0) {
			var row = $(this);
			obj = new Object();
			obj.id = row.find('.title').attr('accesskey');
			obj.title = row.find('.title').val();
			obj.position = row.find('.position').val();
			inputContent.push(obj);
		}
		i++;
	});
	JSON.stringify(inputContent);
	$("#steps").val(JSON.stringify(inputContent));
}

