package br.com.alura.financialmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.financialmanagement.model.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

	@Query("SELECT i FROM Income i WHERE i.description = :description AND MONTH(i.date) = :month")
	List<Income> findByDescriptionOnMonth(@Param("description") String description, @Param("month") int month);

}
