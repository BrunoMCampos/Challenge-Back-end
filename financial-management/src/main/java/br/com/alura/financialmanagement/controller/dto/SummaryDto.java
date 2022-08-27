package br.com.alura.financialmanagement.controller.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.financialmanagement.model.Category;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.model.Income;

// Classe criada para retornar um JSON referente aos resumos do mês contendo:
//
// totalIncomes -> Valor total das entradas do mês
// totalExpenses -> Valor total das saídas do mês
// balance -> Valor da diferença das entradas e saídas do mês
//
// Ainda é necessário retornar a quantidade gasta por categoria, porém este ainda será implementado 

public class SummaryDto {

	private BigDecimal totalIncomes = new BigDecimal(0);
	private BigDecimal totalExpenses = new BigDecimal(0);

	private BigDecimal balance = new BigDecimal(0);

	private Map<Category, BigDecimal> expensesByCategory = new HashMap<>();

	// Construtor principal
	// -------------------------------------------------------------
	// Construtor usado para gerar a classe completa já com o resumo de acordo com
	// as listas fornecidas

	public SummaryDto(List<Expense> expensesList, List<Income> incomesList) {
		// Utilizamos o método "sum" para realizar a soma dos valores de entradas e
		// saídas e setalos nos atributos da classe

		this.sum(incomesList, expensesList);

		// Realizamos a verificação da diferença e atribuimos ao atributo de balance
		// Para isso utilizamos o método "add" adicionando as entradas e adicionando às
		// entradas o totalExpenses como negativo usando o método "negate"
		this.balance = this.balance.add(totalIncomes.add(totalExpenses.negate()));
		// Para criarmos uma lista com os gastos por categoria utilizaremos a classe map
		// que pode ser refatorada no futuro com a criação de uma nova classe
		//
		// Para popular a lista utilizamos um novo método privado, chamado
		// setExpensesByCategory
		this.setExpensesByCategory(expensesList);
	}

	// Método utilizado dentro do método PARSE para realizar a soma dos valores das
	// despesas e das receitas
	private void sum(List<Income> incomesList, List<Expense> expensesList) {
		// Utilizamos o "stream" para realizar um "foreach" e somar os valores dentro de
		// cada registro

		// Somando entradas
		incomesList.stream().forEach(i -> totalIncomes = totalIncomes.add(i.getValue()));
		// Somando saídas
		expensesList.stream().forEach(e -> totalExpenses = totalExpenses.add(e.getValue()));
	}

	// Método utilizado dentro do método PARSE para realizar a separação dos gastos
	// mensais por categorias
	private void setExpensesByCategory(List<Expense> expensesList) {
		// Iremos utilizar o método "stream" da classe "list", iterando dentro das
		// categorias por meio de um "for"
		for (Category category : Category.values()) {

			// A cada iteração teremos uma nova variável para realizar a soma do
			// total da categoria iterada

			BigDecimal sumOfThisCategory = new BigDecimal(0);

			// Agora entramos em cada "despesa" da lista e realizamos a verificação
			// para somarmos apenas aquelas que contem a mesma categoria que estamos
			// iterando
			for (Expense expense : expensesList) {
				if (expense.getCategory() == category) {
					sumOfThisCategory = sumOfThisCategory.add(expense.getValue());
				}
			}

			// Por fim, se o valor for diferente de zero,
			// adicionamos ao "map" a categoria e o valor para termos o registro.
			//
			// Utilizamos o método "compareTo" para verificar os valores ignorando
			// a "escala"
			if (sumOfThisCategory.compareTo(new BigDecimal(0)) > 0) {
				expensesByCategory.put(category, sumOfThisCategory);
			}
		}

	}

	public BigDecimal getTotalIncomes() {
		return totalIncomes;
	}

	public BigDecimal getTotalExpenses() {
		return totalExpenses;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public Map<Category, BigDecimal> getExpensesByCategory() {
		return expensesByCategory;
	}

}
