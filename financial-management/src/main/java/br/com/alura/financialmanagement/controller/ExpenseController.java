package br.com.alura.financialmanagement.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.controller.form.ExpenseForm;
import br.com.alura.financialmanagement.controller.form.ExpenseFormUpdate;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

@RestController
@RequestMapping("/despesas")
public class ExpenseController {

	@Autowired
	private ExpenseRepository expenseRepository;

	@GetMapping
	public List<ExpenseDto> listAll() {
		return ExpenseDto.parse(expenseRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExpenseDto> detailAnExpense(@PathVariable Long id) {
		Optional<Expense> optionalExpense = expenseRepository.findById(id);

		if (!optionalExpense.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(new ExpenseDto(optionalExpense.get()));
		}
	}

	@PostMapping
	public ResponseEntity<ExpenseDto> add(@RequestBody @Valid ExpenseForm form, UriComponentsBuilder uriBuilder) {
		return form.save(expenseRepository, uriBuilder);
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<ExpenseDto> update(@RequestBody @Valid ExpenseFormUpdate form, @PathVariable Long id) {
		return form.update(expenseRepository, id);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<Expense> optionalExpense = expenseRepository.findById(id);

		if (!optionalExpense.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			Expense expense = optionalExpense.get();
			expenseRepository.delete(expense);
			return ResponseEntity.ok().build();
		}
	}

}
