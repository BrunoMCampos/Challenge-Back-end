package br.com.alura.financialmanagement.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.alura.financialmanagement.model.Category;
import br.com.alura.financialmanagement.model.Expense;

public class ExpenseDto {

	private String description;
	private BigDecimal value;
	private LocalDate date;

	private Category category;

	public ExpenseDto() {
	}

	public ExpenseDto(String description, BigDecimal value, LocalDate date, Category category) {
		this.description = description;
		this.value = value;
		this.date = date;
		this.category = category;
	}

	public ExpenseDto(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
	}

	public ExpenseDto(Expense expense) {
		this.description = expense.getDescription();
		this.value = expense.getValue();
		this.date = expense.getDate();
		this.category = expense.getCategory();
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, date, description, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpenseDto other = (ExpenseDto) obj;
		if (this.category.equals(other.getCategory()) && this.date.equals(other.getDate())
				&& this.description.equals(other.getDescription()) && this.value.compareTo(other.getValue()) == 0)
			return true;
		else
			return false;
	}

	public Category getCategory() {
		return category;
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
