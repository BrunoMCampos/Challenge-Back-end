package br.com.alura.financialmanagement.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.alura.financialmanagement.model.Income;

public class IncomeDto {

	private String description;
	private BigDecimal value;
	private LocalDate date;

	public IncomeDto() {
	}

	public IncomeDto(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
	}

	public IncomeDto(Income income) {
		this.description = income.getDescription();
		this.value = income.getValue();
		this.date = income.getDate();
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

	public static List<IncomeDto> parse(List<Income> list) {
		return list.stream().map(IncomeDto::new).collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, description, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncomeDto other = (IncomeDto) obj;
		return date.equals(other.date) && description.equals(other.description)
				&& value.compareTo(other.value) == 0;
	}

}
