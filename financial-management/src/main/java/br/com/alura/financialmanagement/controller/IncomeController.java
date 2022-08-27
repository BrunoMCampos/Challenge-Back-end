package br.com.alura.financialmanagement.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.financialmanagement.controller.dto.IncomeDto;
import br.com.alura.financialmanagement.controller.form.IncomeForm;
import br.com.alura.financialmanagement.controller.form.IncomeFormUpdate;
import br.com.alura.financialmanagement.model.Income;
import br.com.alura.financialmanagement.repository.IncomeRepository;

@RestController
@RequestMapping("/receitas")
public class IncomeController {

	// Injeção de dependências da interface "IncomeRepository" que é utilizada para
	// persistência

	@Autowired
	private IncomeRepository incomeRepository;

	// Método utilizado para listar todas as "Entradas"
	// Caso seja utilizado um parametro ele irá listar apenas as "entradas" com
	// aquele trecho de texto da descrição, caso contrário listará todas as
	// "entradas"
	@GetMapping
	public List<IncomeDto> listAll(String descricao) {

		// Realiza a verificação se foi enviado um parametro na url com um if
		// Caso o retorno seja FALSE, ou seja, o valor de descição seja NULO OU VAZIO
		// retornamos a lista de valores por completo

		if (descricao == null || descricao.isBlank()) {
			return IncomeDto.parse(incomeRepository.findAll()); // Lista completa sem filtros
		}

		// Caso o parametro tenha sido utilizado iremos retornar apenas o que contém o
		// parametro em questão

		// Precisamos adicionar o caractere '%' para realizar uma pesquisa LIKE no MYSQL

		descricao = "%" + descricao + "%";

		List<Income> listIncome = incomeRepository.findByDescriptionWithLike(descricao);
		
		if (listIncome.size() > 0) {
			return IncomeDto.parse(listIncome);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
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

	// Endpoint criado para retornar as receitas de determinado mês utilizando a
	// formatação "/receitas/ano/mes" ---Exemplo--> "/receitas/2022/08"

	@GetMapping("/{ano}/{mes}")
	public List<IncomeDto> listByDate(@PathVariable("ano") int year, @PathVariable("mes") int month) {
		// Utilizaremos o método findByDate do repositório de INCOME, utilizando os
		// parametros enviados via PATH

		// Ao realizarmos a busca já retornaremos os dados para o endpoint como um DTO
		// utilizando o método estático PARSE da classe IncomeDto

		return IncomeDto.parse(incomeRepository.findByDate(year, month));
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
