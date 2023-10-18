package br.gov.cesarschool.poo.bonusvendas.negocio;

import java.time.LocalDate;

import br.gov.cesarschool.poo.bonusvendas.dao.VendedorDAO;
import br.gov.cesarschool.poo.bonusvendas.entidade.Vendedor;
import br.gov.cesarschool.poo.bonusvendas.negocio.geral.ValidadorCPF;

public class VendedorMediator {
	private static VendedorMediator instance;
	private VendedorDAO repositorioVendedor;
	private AcumuloResgateMediator caixaDeBonusMediator;
	
	private VendedorMediator() {
		repositorioVendedor = new VendedorDAO();
		caixaDeBonusMediator = AcumuloResgateMediator.getInstancia();
	}
	
	public static VendedorMediator getInstance() {
		if (instance == null) {
			instance = new VendedorMediator();
		}
		return instance;
	}
	
	public ResultadoInclusaoVendedor validarVendedor(Vendedor vendedor) {
	    if (vendedor.getCpf() == null || vendedor.getCpf().trim().isEmpty()) {
	      return new ResultadoInclusaoVendedor(0, "CPF nao informado");
	    }
	    if (!ValidadorCPF.ehCpfValido(vendedor.getCpf())) {
	      return new ResultadoInclusaoVendedor(0, "CPF invalido");
	    }
	    if (vendedor.getNomeCompleto() == null || vendedor.getNomeCompleto().trim().isEmpty()) {
	      return new ResultadoInclusaoVendedor(0, "Nome completo nao informado");
	    }
	    if (vendedor.getSexo() == null) {
	      return new ResultadoInclusaoVendedor(0, "Sexo nao informado");
	    }
	    if (vendedor.getDataNascimento() == null) {
	      return new ResultadoInclusaoVendedor(0, "Data de nascimento nao informada");
	    }
	    if (vendedor.getDataNascimento().isAfter(LocalDate.now().minusYears(17))) {
	      return new ResultadoInclusaoVendedor(0, "Data de nascimento invalida");
	    }
	    if (vendedor.getRenda() < 0) {
	      return new ResultadoInclusaoVendedor(0, "Renda menor que zero");
	    }
	    if (vendedor.getEndereco() == null) {
	      return new ResultadoInclusaoVendedor(0, "Endereco nao informado");
	    }
	    if (vendedor.getEndereco().getLogradouro() == null || vendedor.getEndereco().getLogradouro().trim().isEmpty()) {
	      return new ResultadoInclusaoVendedor(0, "Logradouro nao informado");
	    }
	    if (vendedor.getEndereco().getLogradouro().length() < 4) {
	      return new ResultadoInclusaoVendedor(0, "Logradouro tem menos de 04 caracteres");
	    }
	    if (vendedor.getEndereco().getNumero() < 0) {
	      return new ResultadoInclusaoVendedor(0, "Numero menor que zero");
	    }
	    if (vendedor.getEndereco().getCidade() == null || vendedor.getEndereco().getCidade().trim().isEmpty()) {
	      return new ResultadoInclusaoVendedor(0, "Cidade nao informada");
	    }
	    if (vendedor.getEndereco().getEstado() == null || vendedor.getEndereco().getEstado().trim().isEmpty()) {
	      return new ResultadoInclusaoVendedor(0, "Estado nao informado");
	    }
	    if (vendedor.getEndereco().getPais() == null || vendedor.getEndereco().getPais().trim().isEmpty()) {
	      return new ResultadoInclusaoVendedor(0, "Pais nao informado");
	    }
	    return new ResultadoInclusaoVendedor(0, null);
	  }

	  public ResultadoInclusaoVendedor incluir(Vendedor vendedor) {
	    ResultadoInclusaoVendedor resultado = validarVendedor(vendedor);
	    if (resultado.getMensagemErroValidacao() != null) {
	      return resultado;
	    }
	    
	    long numeroCaixaDeBonus = caixaDeBonusMediator.gerarCaixaDeBonus(vendedor);
	    if (numeroCaixaDeBonus == 0) {
	    	return new ResultadoInclusaoVendedor(0, "Caixa de bonus nao foi gerada");
	    }

	    boolean incluir = repositorioVendedor.incluir(vendedor);
	    if (incluir == false) {
	      return new ResultadoInclusaoVendedor(0, "Vendedor ja existente");
	    }
	    
	    return new ResultadoInclusaoVendedor(numeroCaixaDeBonus, null);
	  }

	  public String alterar(Vendedor vendedor) {
	    ResultadoInclusaoVendedor resultado = validarVendedor(vendedor);
	    if (resultado.getMensagemErroValidacao() != null) {
	      return resultado.getMensagemErroValidacao();
	    }
	    if (repositorioVendedor.buscar(vendedor.getCpf()) == null) {
	      return "Vendedor inexistente";
	    }
	    ;
	    repositorioVendedor.alterar(vendedor);
	    return null;
	  }

	  public Vendedor buscar(String cpf) {
	    return repositorioVendedor.buscar(cpf);
	  }
	
}
