package br.com.alura.financialmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.financialmanagement.controller.dto.SummaryDto;
import br.com.alura.financialmanagement.model.Expense;
import br.com.alura.financialmanagement.model.Income;
import br.com.alura.financialmanagement.repository.ExpenseRepository;
import br.com.alura.financialmanagement.repository.IncomeRepository;

@RestController
@RequestMapping("/resumo")
public class SummaryController {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	// Endpoint para a geração do relatório de saídas e entradas dentro de um mês
	// específico utilizando o mapeamento
	// "/resumo/{ano}/{mes} ---Exemplo--> "/resumo/2022/08"

	@GetMapping("/{ano}/{mes}")
	public SummaryDto monthSummary(@PathVariable("ano") int year, @PathVariable("mes") int month) {

		// Realizamos a pesquisa via repository com o método findByDate que já utiliza
		// ano e mês vindos do PATH

		List<Expense> expensesList = expenseRepository.findByDate(year, month);
		List<Income> incomesList = incomeRepository.findByDate(year, month);

		// Com essas duas listas realizamos a conversão para SummaryDto com o método
		// parse e retornamos para o endpoint
		
		return new SummaryDto(expensesList, incomesList);

	}

}
