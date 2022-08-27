package br.com.alura.financialmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;
	private BigDecimal value;
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	private Category category;

	public Expense() {
	}

	public Expense(Long id, String description, BigDecimal value, LocalDate date) {
		this.id = id;
		this.description = description;
		this.value = value;
		this.date = date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, date, description, id, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expense other = (Expense) obj;
		if (this.category.equals(other.getCategory()) && this.date.equals(other.getDate())
				&& this.description.equals(other.getDescription()) && this.value.compareTo(other.getValue()) == 0
				&& this.id == other.getId())
			return true;
		else
			return false;
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

	public Long getId() {
		return id;
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

	public void setCategory(Category category) {
		this.category = category;
	}

}
