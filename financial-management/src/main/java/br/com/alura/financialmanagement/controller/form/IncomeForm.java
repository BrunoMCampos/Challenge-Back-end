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

import br.com.alura.financialmanagement.controller.dto.IncomeDto;
import br.com.alura.financialmanagement.model.Income;
import br.com.alura.financialmanagement.repository.IncomeRepository;

public class IncomeForm {

	@NotBlank
	private String description;
	@NotNull
	private BigDecimal value;
	@NotNull
	private LocalDate date;

	public IncomeForm() {
	}

	public IncomeForm(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public IncomeDto atualizar(IncomeRepository incomeRepository, Long id) {
		Income income = incomeRepository.findById(id).get();

		income.setDescription(this.description);
		income.setDate(this.date);
		income.setValue(this.value);

		return new IncomeDto(income);
	}

	public ResponseEntity<IncomeDto> save(IncomeRepository incomeRepository, UriComponentsBuilder uriBuilder) {
		if (this.isValid(incomeRepository)) {
			Income income = this.parse();

			incomeRepository.save(income);

			URI uri = uriBuilder.path("/receitas/{id}").buildAndExpand(income.getId()).toUri();

			return ResponseEntity.created(uri).body(new IncomeDto(income));
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição já cadastrada dentro do mês atual");
	}

	private boolean isValid(IncomeRepository incomeRepository) {
		List<Income> incomeList = incomeRepository.findByDescriptionOnMonth(this.description,
				this.date.getMonthValue());

		if (incomeList.size() > 0) {
			return false;
		}

		return true;
	}

	private Income parse() {
		Income income = new Income();

		income.setDescription(this.description);
		income.setDate(this.date);
		income.setValue(this.value);

		return income;
	}

	public IncomeDto parseToDto() {
		return new IncomeDto(this.parse());
	}

}
