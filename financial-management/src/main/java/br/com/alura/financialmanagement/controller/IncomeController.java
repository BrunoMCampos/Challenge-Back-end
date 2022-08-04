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

import br.com.alura.financialmanagement.controller.dto.IncomeDto;
import br.com.alura.financialmanagement.controller.form.IncomeForm;
import br.com.alura.financialmanagement.controller.form.IncomeFormUpdate;
import br.com.alura.financialmanagement.model.Income;
import br.com.alura.financialmanagement.repository.IncomeRepository;

@RestController
@RequestMapping("/receitas")
public class IncomeController {

	@Autowired
	private IncomeRepository incomeRepository;

	@GetMapping
	public List<IncomeDto> listAll() {
		return IncomeDto.parse(incomeRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<IncomeDto> detailAnIncome(@PathVariable Long id) {
		Optional<Income> optionalIncome = incomeRepository.findById(id);

		if (!optionalIncome.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(new IncomeDto(optionalIncome.get()));
		}
	}

	@PostMapping
	public ResponseEntity<IncomeDto> add(@RequestBody @Valid IncomeForm form, UriComponentsBuilder uriBuilder) {
		return form.save(incomeRepository, uriBuilder);
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<IncomeDto> update(@RequestBody @Valid IncomeFormUpdate form, @PathVariable Long id) {
		return form.update(incomeRepository, id);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<Income> optionalIncome = incomeRepository.findById(id);

		if (!optionalIncome.isPresent()) {
			return ResponseEntity.notFound().build();
		} else {
			Income income = optionalIncome.get();
			incomeRepository.delete(income);
			return ResponseEntity.ok().build();
		}
	}

}
