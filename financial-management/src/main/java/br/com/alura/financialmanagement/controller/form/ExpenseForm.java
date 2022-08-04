package br.com.alura.financialmanagement.controller.form;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

public class ExpenseForm {

	@NotBlank
	private String description;

	@NotNull
	private BigDecimal value;

	@NotNull
	private LocalDate date;

	public ExpenseForm(String description, BigDecimal value, LocalDate date) {
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

	private boolean isValid(ExpenseRepository expenseRepository) {
		List<Expense> expenseList = expenseRepository.findByDescriptionOnMonth(this.description,
				this.date.getMonthValue());

		if (expenseList.size() > 0) {
			return false;
		}

		return true;
	}

	public ResponseEntity<ExpenseDto> save(ExpenseRepository expenseRepository, UriComponentsBuilder uriBuilder) {
		if (this.isValid(expenseRepository)) {
			Expense expense = this.parse();

			expenseRepository.save(expense);

			URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();

			return ResponseEntity.created(uri).body(new ExpenseDto(expense));
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição já cadastrada dentro do mês atual");
	}

	private Expense parse() {
		Expense expense = new Expense();

		expense.setDescription(this.description);
		expense.setDate(this.date);
		expense.setValue(this.value);

		return expense;
	}

}
