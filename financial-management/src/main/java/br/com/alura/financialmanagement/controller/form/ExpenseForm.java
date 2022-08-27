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

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.model.Category;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

public class ExpenseForm {

	@NotBlank
	private String description;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal value;

	@NotNull
	private LocalDate date;

	private Category category = Category.OUTRAS;

	public ExpenseForm() {
	}

	public ExpenseForm(String description, BigDecimal value, LocalDate date, Category category) {
		this.description = description;
		this.value = value;
		this.date = date;
		this.category = category;
	}

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

	public Category getCategory() {
		return category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	private boolean isValid(ExpenseRepository expenseRepository) {

		// Checando dados para podemos salvar no banco de dados

		// ---- Checando se a Descrição já não foi usada dentro do mês selecionado ----

		// Primeiro é realizada uma consulta no banco de dados procurando uma lista de
		// descrições que sejam identicas a utilizada
		// dentro do mesmo mês
		List<Expense> expenseList = expenseRepository.findByDescriptionOnMonth(this.description,
				this.date.getMonthValue());

		// Caso a lista não esteja vazia temos alguma descrição identica cadastrada,
		// então retornamos false
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
		expense.setCategory(this.category);

		return expense;
	}

	public ExpenseDto parseToDto() {
		return new ExpenseDto(this.description, this.value, this.date, this.category);
	}
}
