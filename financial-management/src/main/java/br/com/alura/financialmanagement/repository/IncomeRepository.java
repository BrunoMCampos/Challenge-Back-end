package br.com.alura.financialmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.financialmanagement.model.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

	@Query("SELECT i FROM Income i WHERE i.description = :description AND MONTH(i.date) = :month")
	List<Income> findByDescriptionOnMonth(@Param("description") String description, @Param("month") int month);

	@Query("SELECT i FROM Income i WHERE i.description = :description AND MONTH(i.date) = :month AND i.id <> :id")
	List<Income> findByDescriptionOnMonthDiferentId(@Param("description") String description, @Param("month") int month,
			@Param("id") Long id);

	@Query("SELECT i FROM Income i WHERE i.description LIKE :description")
	List<Income> findByDescriptionWithLike(@Param("description") String description);

	@Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month")
	List<Income> findByDate(@Param("year") int year, @Param("month") int month);

}
