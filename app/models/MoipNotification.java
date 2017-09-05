package models;

public class MoipNotification {
	/*
	 * Identificador da transação informado por você para controle em seu site
	 * Alfanumérico 32 abcd1234
	 */
	private String id_transacao;

	/*
	 * Valor do pagamento, sem vírgulas, com casas decimais (veja exemplo para
	 * R$20,00) Numérico (inteiro) 9 2000
	 */
	private int valor;

	/*
	 * Codigo informando o status atual da transação (veja Anexo A) Numérico
	 * inteiro 2 3
	 */
	private int status_pagamento;

	/*
	 * Código da transação no ambiente MoIP. Valor único gerado pelo MoIP.
	 * Numérico 20 12341234
	 */
	/*
	 * [autorizado 1] 
	 * Pagamento já foi realizado porém ainda não foi creditado na
	 * Carteira MoIP recebedora (devido ao floating da forma de pagamento)
	 * 
	 * [iniciado 2]
	 * Pagamento está sendo realizado ou janela do navegador foi
	 * fechada (pagamento abandonado) boleto 
	 * 
	 * [impresso 3] 
	 * Boleto foi impresso e
	 * ainda não foi pago 
	 * 
	 * [concluido 4] Pagamento já foi realizado e dinheiro já
	 * foi creditado na Carteira MoIP recebedora 
	 * 
	 * [cancelado 5] Pagamento foi
	 * cancelado pelo pagador, instituição de pagamento, MoIP ou recebedor antes
	 * de ser concluído 
	 * 
	 * [em análise 6] 
	 * Pagamento foi realizado com cartão de
	 * crédito e autorizado, porém está em análise pela Equipe MoIP. Não existe
	 * garantia de que será concluído 
	 * 
	 * [estornado 7] 
	 * Pagamento foi estornado pelo
	 * pagador, recebedor, instituição de pagamento ou MoIP
	 */
	private long cod_moip;

	/*
	 * Codigo informando a forma de pagamento escolhida pelo pagador (veja Anexo
	 * B) Numérico inteiro 2 1
	 */
	private int forma_pagamento;

	/*
	 * Tipo de pagamento utilizado, descritivo, em formato de texto (veja Anexo
	 * C) Alfanumérico 32 CartaoDeCredito
	 */
	private String tipo_pagamento;

	/*
	 * E-mail informado pelo pagador, no MoIP Alfanumérico 45
	 * pagador@email.com.br
	 */
	private String email_consumidor;

	public String getId_transacao() {
		return id_transacao;
	}

	public void setId_transacao(String id_transacao) {
		this.id_transacao = id_transacao;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public int getStatus_pagamento() {
		return status_pagamento;
	}

	public void setStatus_pagamento(int status_pagamento) {
		this.status_pagamento = status_pagamento;
	}

	public long getCod_moip() {
		return cod_moip;
	}

	public void setCod_moip(long cod_moip) {
		this.cod_moip = cod_moip;
	}

	public int getForma_pagamento() {
		return forma_pagamento;
	}

	public void setForma_pagamento(int forma_pagamento) {
		this.forma_pagamento = forma_pagamento;
	}

	public String getTipo_pagamento() {
		return tipo_pagamento;
	}

	public void setTipo_pagamento(String tipo_pagamento) {
		this.tipo_pagamento = tipo_pagamento;
	}

	public String getEmail_consumidor() {
		return email_consumidor;
	}

	public void setEmail_consumidor(String email_consumidor) {
		this.email_consumidor = email_consumidor;
	}
}
