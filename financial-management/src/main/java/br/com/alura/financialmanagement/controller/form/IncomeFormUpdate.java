package br.com.alura.financialmanagement.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import br.com.alura.financialmanagement.controller.dto.IncomeDto;
import br.com.alura.financialmanagement.model.Income;
import br.com.alura.financialmanagement.repository.IncomeRepository;

public class IncomeFormUpdate {

	private String description;
	private BigDecimal value;
	private LocalDate date;

	public IncomeFormUpdate(String description, BigDecimal value, LocalDate date) {
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

	public ResponseEntity<IncomeDto> update(IncomeRepository incomeRepository, Long id) {
		Optional<Income> optionalIncome = incomeRepository.findById(id);

		if (!optionalIncome.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			Income income = optionalIncome.get();

			this.validade(incomeRepository, id, income);

			income.setDate(this.date);
			income.setDescription(this.description);
			income.setValue(this.value);

			return ResponseEntity.ok(new IncomeDto(income));
		}
	}

	private void validade(IncomeRepository incomeRepository, Long id, Income income) {

		if (this.date == null) {
			this.date = income.getDate();
		}

		List<Income> incomeList = incomeRepository.findByDescriptionOnMonthDiferentId(this.description,
				this.date.getMonthValue(), id);

		if (incomeList.size() > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição já cadastrada dentro do mês atual");
		}

		if (this.description == null || this.description.isBlank()) {
			this.description = income.getDescription();
		}

		if (this.value == null) {
			this.value = income.getValue();
		}

	}

}
