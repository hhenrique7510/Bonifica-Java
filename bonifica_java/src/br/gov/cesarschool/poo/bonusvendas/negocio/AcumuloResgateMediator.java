package br.gov.cesarschool.poo.bonusvendas.negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import br.gov.cesarschool.poo.bonusvendas.dao.CaixaDeBonusDAO;
import br.gov.cesarschool.poo.bonusvendas.dao.LancamentoBonusDAO;
import br.gov.cesarschool.poo.bonusvendas.entidade.CaixaDeBonus;
import br.gov.cesarschool.poo.bonusvendas.entidade.LancamentoBonusCredito;
import br.gov.cesarschool.poo.bonusvendas.entidade.LancamentoBonusDebito;
import br.gov.cesarschool.poo.bonusvendas.entidade.TipoResgate;
import br.gov.cesarschool.poo.bonusvendas.entidade.Vendedor;

public class AcumuloResgateMediator {
	  private static AcumuloResgateMediator instancia;

	  public static AcumuloResgateMediator getInstancia() {
		  if (instancia == null) {
			  instancia = new AcumuloResgateMediator();
		  }
		  return instancia;
	  }

	  private CaixaDeBonusDAO repositorioCaixaBonus;
	  private LancamentoBonusDAO repositorioLancamento;

	  private AcumuloResgateMediator() {
		  this.repositorioCaixaBonus = new CaixaDeBonusDAO();
		  this.repositorioLancamento = new LancamentoBonusDAO();
	  }
	  
	  public CaixaDeBonus buscarCaixaDeBonus(long numeroCaixaDeBonus) {
		  CaixaDeBonus caixa = repositorioCaixaBonus.buscar(numeroCaixaDeBonus);
		  if (caixa == null) {
			  return null;
		  }
		  return caixa;
	  }

	  public long gerarCaixaDeBonus(Vendedor vendedor) {
		  String cpf = vendedor.getCpf();
		  LocalDate dataAtual = LocalDate.now();
		  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		  cpf = cpf.replaceAll("[^0-9]", "");

		  cpf = cpf.substring(0, cpf.length() - 2);

		  long cpfNumerico = Long.parseLong(cpf);

		  String numCaixaDeBonusStr = String.valueOf(cpfNumerico) + dataAtual.format(formatter);

		  long numCaixaDeBonus = Long.parseLong(numCaixaDeBonusStr);

		  CaixaDeBonus caixa = new CaixaDeBonus(numCaixaDeBonus);

		  if (repositorioCaixaBonus.buscar(numCaixaDeBonus) == null) {
			  repositorioCaixaBonus.incluir(caixa);
			  return numCaixaDeBonus;
		  } else {
			  return 0;
		  }
	  }

	  public String acumularBonus(long numCaixaDeBonus, double valor) {
		  if (valor <= 0) {
			  return "Valor menor ou igual a zero";
		  }
		  CaixaDeBonus caixa = repositorioCaixaBonus.buscar(numCaixaDeBonus);
		  if (caixa != null) {
			  caixa.creditar(valor);
			  repositorioCaixaBonus.alterar(caixa);
			  LancamentoBonusCredito lancamento = new LancamentoBonusCredito(numCaixaDeBonus, valor, LocalDate.now());
			  repositorioLancamento.incluir(lancamento);
			  return null;
		  } else {
			  return "Caixa de bonus inexistente";
		  }
	  }

	  public String resgatar(long numeroCaixaDeBonus, double valor, TipoResgate tipoResgate) {
		  if (valor <= 0) {
			  return "Valor menor ou igual a zero";
		  }
		  CaixaDeBonus caixa = repositorioCaixaBonus.buscar(numeroCaixaDeBonus);

		  if (caixa != null) {
			  if (caixa.getSaldo() >= valor) {
				  caixa.debitar(valor);
				  repositorioCaixaBonus.alterar(caixa);
				  LancamentoBonusDebito lancamento = new LancamentoBonusDebito (numeroCaixaDeBonus, valor, LocalDate.now(), tipoResgate);
				  repositorioLancamento.incluir(lancamento);
				  return null;
			  } else {
				  return "Saldo insuficiente";
			  }
		  } else {
			  return "Caixa de bonus inexistente";
		  }
	  }
}
