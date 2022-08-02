package br.com.alura.financialmanagement.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.financialmanagement.controller.dto.IncomeDto;
import br.com.alura.financialmanagement.controller.form.IncomeForm;
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

	@PostMapping
	public ResponseEntity<IncomeDto> register(@RequestBody @Valid IncomeForm form, UriComponentsBuilder uriBuilder) {
		List<Income> incomeList = incomeRepository.findByDescriptionOnMonth(form.getDescription(),
				form.getDate().getMonthValue());

		if (incomeList.size() == 0) {
			Income income = form.parse();
			incomeRepository.save(income);

			URI uri = uriBuilder.path("/receitas/{id}").buildAndExpand(income.getId()).toUri();

			return ResponseEntity.created(uri).body(new IncomeDto(income));
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição já cadastrada dentro do mês atual");
		}
	}

}
