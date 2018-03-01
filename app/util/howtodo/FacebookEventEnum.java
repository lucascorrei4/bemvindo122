package util.howtodo;

public enum FacebookEventEnum {

	Purchase("Purchase - Para página de confirmação do pedido ou página para completar compras.", "Purchase"),
	Lead("Lead - Para página de formulários/captura de leads.", "Lead"),
	CompleteRegistration("Complete Registration - Para página de cadastro concluído com sucesso.", "CompleteRegistration"),
	AddPaymentInfo("Add Payment Info - Para página 'sucesso' de adicionar pagamento em um fluxo de finalização de compra, ou dentro de uma seção de configurações da conta.","AddPaymentInfo"),
	AddToCart("Add To Cart - Para página de adicionar ao carrinho ou rastreie esse evento em uma ação embutida.","AddToCart"),
	AddToWishlist("Add To Wish List - Para página de lista de desejos ou rastreie esse evento em uma ação embutida.","AddToWishlist"),
	InitiateCheckout("InitiateCheckout - Para primeira página do processo de finalização da compra.","InitiateCheckout"),
	Search("Search - Para página de resultados da pesquisa.","Search"),
	ViewContent("ViewContent - Para página de detalhes ou páginas de conteúdo.","ViewContent");
	
	String label;
	String value;

	FacebookEventEnum(String label, String value) {
		this.value = value;
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public String getValue() {
		return this.value;
	}

	public String getLabel() {
		return label;
	}
}
