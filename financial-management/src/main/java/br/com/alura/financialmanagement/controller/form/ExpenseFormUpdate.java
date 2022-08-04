package br.com.alura.financialmanagement.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

public class ExpenseFormUpdate {

	private String description;
	private BigDecimal value;
	private LocalDate date;

	public ExpenseFormUpdate(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
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

	public ResponseEntity<ExpenseDto> update(ExpenseRepository expenseRepository, Long id) {
		Optional<Expense> optionalExpense = expenseRepository.findById(id);

		if (!optionalExpense.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			Expense expense = optionalExpense.get();

			this.validade(expenseRepository, id, expense);

			expense.setDate(this.date);
			expense.setDescription(this.description);
			expense.setValue(this.value);

			return ResponseEntity.ok(new ExpenseDto(expense));
		}
	}

	private void validade(ExpenseRepository expenseRepository, Long id, Expense expense) {

		if (this.date == null) {
			this.date = expense.getDate();
		}

		List<Expense> expenseList = expenseRepository.findByDescriptionOnMonthDiferentId(this.description,
				this.date.getMonthValue(), id);

		if (expenseList.size() > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição já cadastrada dentro do mês atual");
		}

		if (this.description == null || this.description.isBlank()) {
			this.description = expense.getDescription();
		}

		if (this.value == null) {
			this.value = expense.getValue();
		}

	}

}
