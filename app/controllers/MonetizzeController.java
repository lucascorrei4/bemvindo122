package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import org.apache.ivy.Main;

import models.BodyMail;
import models.City;
import models.MonetizzeTransaction;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.Service;
import models.StatusMail;
import models.User;
import play.mvc.Controller;
import util.ApplicationConfiguration;
import util.Utils;

public class MonetizzeController extends Controller {

	public static void postBack() throws UnsupportedEncodingException {
		String uniqueKey = "52a7196c41cb79b2e7539713270cc02f";
		String productCode = "40605";
		String productKey = "10f0468c26647f43d73aac2d4b829eaa";
		//String body = params.get("body", String.class);
		String body = "chave_unica=52a7196c41cb79b2e7539713270cc02f&produto[codigo]=42&produto[nome]=X-Wing&produto[chave]=b1a4e746c913ac058575403c6408622b&venda[codigo]=1&venda[dataInicio]=2017-06-03 17:32:27&venda[dataFinalizada]=2017-06-03 17:32:27&venda[meioPagamento]=Monetizze&venda[formaPagamento]=Boleto&venda[garantiaRestante]=7&venda[status]=Finalizada&venda[valor]=197.00&venda[quantidade]=1&venda[tipo_frete]=1&venda[frete]=15.00&venda[valorRecebido]=150.00&venda[plano]=&venda[cupom]=&venda[linkBoleto]=&venda[linha_digitavel]=&venda[src]=Origem&comissoes[0][nome]=Afiliado&comissoes[0][tipo_comissao]=Último Clique&comissoes[0][valor]=27,50&comissoes[0][porcentagem]=15,50&comprador[nome]=Comprador Teste&comprador[email]=teste@teste.com.br&comprador[data_nascimento]=1990-01-01&comprador[cnpj_cpf]=111.111.111-11&comprador[telefone]=(11) 1111-1111&comprador[cep]=11111-111&comprador[endereco]=Rua Sem fim&comprador[numero]=42&comprador[complemento]=casa 7&comprador[bairro]=Esperança&comprador[cidade]=Terra do nunca&comprador[estado]=AA&comprador[pais]=Maravilha";
		String[] params = URLDecoder.decode(body, "UTF-8").split("&");
		String uniqueKeyFromPost = Utils.getValueParamByKey(params, "chave_unica");
		if (uniqueKeyFromPost.equals(uniqueKey)) {
			String sellCodeFromPost = Utils.getValueParamByKey(params, "venda[codigo]");
			String sellStatusFromPost = Utils.getValueParamByKey(params, "venda[status]");
			MonetizzeTransaction monetizzeTransaction = MonetizzeTransaction.find("sellCode = '" + sellCodeFromPost + "'").first();
			if (monetizzeTransaction == null) {
				monetizzeTransaction = new MonetizzeTransaction();
				monetizzeTransaction.id = 0l;
			}
			for (int i = 0; i < params.length; i++) {
				if ("produto[codigo]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setProductCode(Utils.getValueFromUrlParam(params[i]));
				if ("produto[nome]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setProductName(Utils.getValueFromUrlParam(params[i]));
				if ("produto[chave]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setProductKey(Utils.getValueFromUrlParam(params[i]));
				if ("venda[codigo]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellCode(Utils.getValueFromUrlParam(params[i]));
				if ("venda[plano]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellPlan(Utils.getValueFromUrlParam(params[i]));
				if ("venda[dataInicio]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellStartDate(Utils.getValueFromUrlParam(params[i]));
				if ("venda[dataFinalizada]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellFinishDate(Utils.getValueFromUrlParam(params[i]));
				if ("venda[meioPagamento]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellMeansOfPayment(Utils.getValueFromUrlParam(params[i]));
				if ("venda[formaPagamento]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellFormOfPayment(Utils.getValueFromUrlParam(params[i]));
				if ("venda[garantiaRestante]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellRemainingWarranty(Utils.getValueFromUrlParam(params[i]));
				if ("venda[status]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellStatus(Utils.getValueFromUrlParam(params[i]));
				if ("venda[valor]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellValue(Utils.getValueFromUrlParam(params[i]));
				if ("venda[quantidade]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellQuantity(Utils.getValueFromUrlParam(params[i]));
				if ("venda[tipo_frete]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellFreightType(Utils.getValueFromUrlParam(params[i]));
				if ("venda[frete]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellFreight(Utils.getValueFromUrlParam(params[i]));
				if ("venda[valorRecebido]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellReceivedValue(Utils.getValueFromUrlParam(params[i]));
				if ("venda[plano]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellPalan(Utils.getValueFromUrlParam(params[i]));
				if ("venda[cupom]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellCoupon(Utils.getValueFromUrlParam(params[i]));
				if ("venda[linkBoleto]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellBillet(Utils.getValueFromUrlParam(params[i]));
				if ("venda[linha_digitavel]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellDigitableLine(Utils.getValueFromUrlParam(params[i]));
				if ("venda[src]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellSource(Utils.getValueFromUrlParam(params[i]));
				if ("venda[utm_source]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellUtmSource(Utils.getValueFromUrlParam(params[i]));
				if ("venda[utm_medium]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellUtmMedium(Utils.getValueFromUrlParam(params[i]));
				if ("venda[utm_content]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellUtmContent(Utils.getValueFromUrlParam(params[i]));
				if ("venda[utm_campaign]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSellUtmCampaing(Utils.getValueFromUrlParam(params[i]));
				if ("comissoes[0][nome]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setComissionName(Utils.getValueFromUrlParam(params[i]));
				if ("comissoes[0][tipo_comissao]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setComissionType(Utils.getValueFromUrlParam(params[i]));
				if ("comissoes[0][valor]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setComissionValue(Utils.getValueFromUrlParam(params[i]));
				if ("comissoes[0][porcentagem]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setComissionPercent(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[nome]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerName(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[email]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerMail(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[data_nascimento]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerBirthDate(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[cnpj_cpf]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerCGC(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[telefone]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerPhone(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[cep]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerCEP(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[endereco]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerAddress(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[numero]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerAddressNumber(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[complemento]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerAddressComplement(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[bairro]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerNeighborhood(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[cidade]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerCity(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[estado]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerState(Utils.getValueFromUrlParam(params[i]));
				if ("comprador[pais]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setCustomerCountry(Utils.getValueFromUrlParam(params[i]));
				if ("assinatura[codigo]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSignatureCode(Utils.getValueFromUrlParam(params[i]));
				if ("assinatura[status]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSignatureStatus(Utils.getValueFromUrlParam(params[i]));
				if ("assinatura[data_assinatura]".equals(params[i].split("=")[0]))
					monetizzeTransaction.setSignatureDate(Utils.getValueFromUrlParam(params[i]));
			}
			monetizzeTransaction.willBeSaved = true;
			monetizzeTransaction.setPostedAt(Utils.getCurrentDateTime());
			monetizzeTransaction.merge();
			if ("Finalizada".equals(sellStatusFromPost)) {
				createNewUserCustomer(monetizzeTransaction);
				User newUser = User.verifyByEmail(monetizzeTransaction.getCustomerMail());
				if (newUser != null) {
					sendEmailToCustomer(newUser);
				} else {
					Admin.sendMailToMeWithCustomInfo("Houve um problema para cadastrar o cliente!", "Nome: " + monetizzeTransaction.getComissionName().concat(" - E-mail: ").concat(monetizzeTransaction.getCustomerMail().concat(" - Fone: ").concat(monetizzeTransaction.getCustomerPhone())));
				}
			}
		}
	}

	private static void createNewUserCustomer(MonetizzeTransaction monetizzeTransaction) {
		User user = new User();
		user.setName(Utils.getNameByWholeName(monetizzeTransaction.getCustomerName()));
		user.setLastName(Utils.getLastNameByWholeName(monetizzeTransaction.getCustomerName()));
		user.setAddress(monetizzeTransaction.getCustomerAddress() + " " + monetizzeTransaction.getCustomerAddressNumber());
		user.setComplement(monetizzeTransaction.getCustomerAddressComplement());
		user.setBirthdate(monetizzeTransaction.getCustomerBirthDate());
		user.setNeighborhood(monetizzeTransaction.getCustomerNeighborhood());
		user.setCep(monetizzeTransaction.getCustomerCEP());
		user.setCpf(monetizzeTransaction.getCustomerCGC());
		user.setCityId(1l);
		user.setCountryId(1l);
		user.setStateId(1l);
		user.setEmail(monetizzeTransaction.getCustomerMail());
		user.setPhone1(monetizzeTransaction.getCustomerPhone());
		user.setPassword(Utils.encode("123456"));
		user.setAdmin(true);
		user.setPostedAt(Utils.getCurrentDateTime());
		user.setInstitutionId(0l);
		user.setActive(true);
		user.setFromMonetizze(true);
		user.merge();
	}

	private static void sendEmailToCustomer(User user) {
		if (!Utils.isNullOrEmpty(user.getEmail()) && Utils.validateEmail(user.getEmail())) {
			Parameter parameter = Parameter.all().first();
			MailController mailController = new MailController();
			/* SendTo object */
			SendTo sendTo = new SendTo();
			sendTo.setDestination(user.getEmail());
			sendTo.setName(user.getName());
			sendTo.setSex("");
			sendTo.setStatus(new StatusMail());
			/* Sender object */
			Sender sender = new Sender();
			sender.setCompany(parameter.getSiteTitle());
			sender.setFrom(parameter.getSiteMail());
			sender.setKey("resetpass");
			/* SendTo object */
			BodyMail bodyMail = new BodyMail();
			bodyMail.setTitle1("É um prazer te servir, " + user.getName()
					+ "! Faltam duas (2) etapas simples: <br />A primeira (1ª) é criar uma senha segura de acesso ao sistema. <br />[Basta clicar no link azul abaixo]. <br />A segunda (2ª) e última etapa é acessar o sistema e informar os dados da sua empresa/seu negócio. <br /><br />A gente se vê logo mais! :)");
			bodyMail.setTitle2(user.getName() + ", recebemos seu cadastro com sucesso.");
			bodyMail.setFooter1(parameter.getSiteDomain() + "/nova-senha/" + Utils.encode(user.getEmail()));
			bodyMail.setImage1(parameter.getSiteDomain() + "/logo");
			bodyMail.setBodyHTML(MailController.getHTMLTemplateResetPass(bodyMail, parameter));
			if (mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter)) {
				Admin.sendMailToMeWithCustomInfo("Venda realizada e cliente cadastrado automaticamente!", "Nome: " + user.getName().concat(" - E-mail: ").concat(user.getEmail().concat(" - Fone: ").concat(user.getPhone1())));
			}
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String body = "chave_unica=52a7196c41cb79b2e7539713270cc02f&produto[codigo]=42&produto[nome]=X-Wing&produto[chave]=b1a4e746c913ac058575403c6408622b&venda[codigo]=1&venda[dataInicio]=2017-06-03 17:32:27&venda[dataFinalizada]=2017-06-03 17:32:27&venda[meioPagamento]=Monetizze&venda[formaPagamento]=Boleto&venda[garantiaRestante]=7&venda[status]=Finalizada&venda[valor]=197.00&venda[quantidade]=1&venda[tipo_frete]=1&venda[frete]=15.00&venda[valorRecebido]=150.00&venda[plano]=&venda[cupom]=&venda[linkBoleto]=&venda[linha_digitavel]=&venda[src]=Origem&comissoes[0][nome]=Afiliado&comissoes[0][tipo_comissao]=Último Clique&comissoes[0][valor]=27,50&comissoes[0][porcentagem]=15,50&comprador[nome]=Comprador Teste&comprador[email]=teste@teste.com.br&comprador[data_nascimento]=1990-01-01&comprador[cnpj_cpf]=111.111.111-11&comprador[telefone]=(11) 1111-1111&comprador[cep]=11111-111&comprador[endereco]=Rua Sem fim&comprador[numero]=42&comprador[complemento]=casa 7&comprador[bairro]=Esperança&comprador[cidade]=Terra do nunca&comprador[estado]=AA&comprador[pais]=Maravilha";
		postBack();
	}

}
