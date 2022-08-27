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

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.controller.form.ExpenseForm;
import br.com.alura.financialmanagement.controller.form.ExpenseFormUpdate;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

@RestController
@RequestMapping("/despesas")
public class ExpenseController {

	// Injeção de dependência do repository necessário, referente a entidade Expense

	@Autowired
	private ExpenseRepository expenseRepository;

	// Métodos GET para listar todas as despesas com ou sem filtro

	@GetMapping
	public List<ExpenseDto> listAll(String descricao) {

		// Verificamos a existência do parametro com um "IF"
		// Caso o retorno seja FALSE já retornamos todos os dados do banco de dados,
		// caso contrário realizamos a busca dos dados inseridos.

		if (descricao == null || descricao.isBlank()) {
			return ExpenseDto.parse(expenseRepository.findAll());
		}

		// Caso chegue até aqui quer dizer que o parametro foi inserido

		// Realizamos uma pesquisa do tipo LIKE no banco de dados e para isso
		// adicionamos o caracter '%' na variável 'descricao'.

		descricao = "%" + descricao + "%";

		// Utilizamos o repository e já fazemos a sua conversão via ExpenseDto com o
		// método estático parse e armazenamos em uma variável
		List<ExpenseDto> expensesDtoList = ExpenseDto.parse(expenseRepository.findByDescriptionWithLike(descricao));

		// Verificamos se os dados estão presentes
		// Caso a lista esteja vazia retornamos um "Not Found"
		// Caso esteja com dados o retornamos ao usuário

		if (expensesDtoList.size() > 0) {
			return expensesDtoList;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
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

	// Endpoint criado para retornar as despesas de determinado mês utilizando a
	// formatação "/despesas/ano/mes" ---Exemplo--> "/despesas/2022/08"

	@GetMapping("/{ano}/{mes}")
	public List<ExpenseDto> listByDate(@PathVariable("ano") int year, @PathVariable("mes") int month) {
		// Utilizaremos o método findByDate do repositório de EXPENSE, utilizando os
		// parametros enviados via PATH

		// Ao realizarmos a busca já retornaremos os dados para o endpoint como um DTO
		// utilizando o método estático PARSE da classe ExpenseDto

		return ExpenseDto.parse(expenseRepository.findByDate(year, month));
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
