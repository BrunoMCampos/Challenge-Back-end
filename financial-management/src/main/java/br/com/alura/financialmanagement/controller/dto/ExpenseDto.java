package br.com.alura.financialmanagement.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import br.com.alura.financialmanagement.model.Expense;

public class ExpenseDto {

	private String description;
	private BigDecimal value;
	private LocalDate date;

	public ExpenseDto() {
	}

	public ExpenseDto(Expense expense) {
		this.description = expense.getDescription();
		this.value = expense.getValue();
		this.date = expense.getDate();
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getValue() {
		return value;
	}

	public LocalDate getDate() {
		return date;
	}

	public static List<ExpenseDto> parse(List<Expense> expensesList) {
		return expensesList.stream().map(ExpenseDto::new).collect(Collectors.toList());
	}

}
