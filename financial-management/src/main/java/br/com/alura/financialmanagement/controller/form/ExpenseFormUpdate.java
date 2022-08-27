package br.com.alura.financialmanagement.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.model.Category;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

public class ExpenseFormUpdate {

	private String description;
	private BigDecimal value;
	private LocalDate date;
	private Category category;

	public ExpenseFormUpdate(String description, BigDecimal value, LocalDate date, Category category) {
		this.description = description;
		this.value = value;
		this.date = date;
		this.category = category;
	}

	public String getDescription() {
		return this.description;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public Category getCategory() {
		return this.category;
	}

	public ResponseEntity<ExpenseDto> update(ExpenseRepository expenseRepository, Long id) {

		// Para atualizarmos o banco de dados começamos encontrando o registro pelo ID

		Optional<Expense> optionalExpense = expenseRepository.findById(id);

		// Verificamos a sua existência e, caso o ID informado não exista retornamos 404

		if (!optionalExpense.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {

			// Caso o ID exista entramos no else

			// Transformamos o Optional<Expense> em um Expense pelo método get

			Expense expense = optionalExpense.get();

			// Realizamos a validação para verificar a integridade dos dados e regras de
			// negócio

			this.validate(expenseRepository, id, expense);

			// Transformamos o form em um expense

			expense.setDate(this.date);
			expense.setDescription(this.description);
			expense.setValue(this.value);
			expense.setCategory(this.category);

			return ResponseEntity.ok(new ExpenseDto(expense));
		}
	}

	private void validate(ExpenseRepository expenseRepository, Long id, Expense expense) {

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

		if (this.category == null) {
			this.category = expense.getCategory();
		}

	}

}
