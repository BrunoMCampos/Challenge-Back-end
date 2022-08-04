package br.com.alura.financialmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.financialmanagement.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	@Query("SELECT e FROM Expense e WHERE e.description = :description AND MONTH(e.date) = :month")
	List<Expense> findByDescriptionOnMonth(@Param("description") String description, @Param("month") int month);
	
	@Query("SELECT e FROM Expense e WHERE e.description = :description AND MONTH(e.date) = :month AND e.id <> :id")
	List<Expense> findByDescriptionOnMonthDiferentId(@Param("description") String description,
			@Param("month") int month, @Param("id") Long id);

}
