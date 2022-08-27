package br.com.alura.financialmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.alura.financialmanagement.controller.dto.ExpenseDto;
import br.com.alura.financialmanagement.controller.form.ExpenseForm;
import br.com.alura.financialmanagement.model.Category;
import br.com.alura.financialmanagement.repository.ExpenseRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class ExpenseControllerTest {

	// Injeção de dependencia do MockMvc para realizar os testes no "controller"
	@Autowired
	private MockMvc mockMvc;

	// Injeção de dependência do repository para podermos realizar algumas buscas no
	// banco de dados
	@Autowired
	private ExpenseRepository repository;

	// Preparamos o mapper usando uma configuração para aceitar LocalDate, já que
	// este será utilizado em todos os testes para a conversão de JSON
	private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	// Como estaremos sempre criando as tabelas ao iniciar o ambiente de testes
	// vamos começar os testes com o cadastro de algumas despesas no banco de dados
	//
	// Dados de uma despesa:
	//
	// Id(id) -> Long (gerado automaticamente)
	//
	// Categoria (category) -> Category (Enum)
	// Data (date) -> LocalDate
	// Descrição (description) -> String
	// Valor(value) -> BigDecimal
	//
	// Classes utilizadas para exibir os dados necessários e realizar os cadastros
	// das informações(Categoria, Data, Descrição
	// e Valor):
	//
	// ExpenseDto
	// ExpenseForm
	//
	// Regras de negócio a serem verificadas:
	// Todas as informações são obrigatórias (Exceto categoria)
	// Não deve ser permitido o cadastro de Despesas com a mesma descrição dentro de
	// um mesmo mês
	//
	// Os dados aqui cadastrados serão reutilizados nos demais testes, sendo assim,
	// segue abaixo uma lista dos registros inseridos pelos testes (lembrando que a
	// ordem varia de run para run):
	//
	// "Restaurante" - R$ 150,00 - 2022-08-14 - ALIMENTACAO;
	// "Pneu Furado" - R$ 20,00 - 2022-08-11 - OUTRAS;
	// "Aluguel" - R$ 1.800,00 - 2022-08-05 - MORADIA;
	//
	// 1º Teste - Cadastro de Despesa com todos os dados corretos e com a categoria
	@Test
	void shouldReturnCreatedAndInsertAnExpenseInTheDatabaseWithTheCategorySelected() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/despesas");

		// Criamos o objeto a ser cadastrado no banco de dados com a categoria
		ExpenseForm form = new ExpenseForm("Restaurante", new BigDecimal(150), LocalDate.of(2022, Month.AUGUST, 14),
				Category.ALIMENTACAO);

		System.out.println("Teste: " + mapper.writeValueAsString(form));

		// Com o MockMvc enviamos uma requisição do tipo "post" com os dados corretos
		// para o cadastro, verificamos se o retorno foi "Created" e armazenamos este
		// retorno em uma variável para podemos analizar seu conteúdo
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(form))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

		// Pegamos o "content" do resultado e verificamos se as informações
		// do JSON correspondem às informações cadastradas
		//
		// Para isso usamos "jackson" para a conversão e analizamos os objetos
		// com um "assertEquals"

		// Utilizamos o mapper para a conversão
		ExpenseDto expenseDto = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				ExpenseDto.class);

		// Convertemos o "form" para uma "dto" e verificamos com "assertEquals"
		assertEquals(form.parseToDto(), expenseDto);
	}

	// 2º Teste - Cadastro de Despesa com todos os dados corretos e sem a categoria
	@Test
	void shouldReturnCreatedAndInsertAnExpenseInTheDatabaseWithoutTheCategorySelected() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/despesas");

		// Criamos o objeto a ser cadastrado no banco de dados sem a categoria
		ExpenseForm form = new ExpenseForm("Pneu Furado", new BigDecimal(20), LocalDate.of(2022, Month.AUGUST, 11));

		// Com o MockMvc enviamos uma requisição do tipo "post" com os dados corretos
		// para o cadastro, verificamos se o retorno foi "Created" e armazenamos este
		// retorno em uma variável para podemos analizar seu conteúdo
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(form))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

		// Pegamos o "content" do resultado e verificamos se as informações
		// do JSON correspondem às informações cadastradas
		//
		// Para isso usamos "jackson" para a conversão e analizamos os objetos
		// com um "assertEquals"

		// Utilizamos o mapper para a conversão
		ExpenseDto expenseDto = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				ExpenseDto.class);

		// Convertemos o "form" para uma "dto" e verificamos com "assertEquals"
		assertEquals(form.parseToDto(), expenseDto);

		// Como a categoria não foi inserida o padrão para a categoria deveria ser
		// "OUTRAS", então vamos realizar a verificação específica deste campo
		// manualmente
		assertEquals(Category.OUTRAS, expenseDto.getCategory());
	}

	// 3º Teste - Cadastro de Despesa com um dos dados incorretos e com a categoria
	@Test
	void shouldntInsertAnExpenseWithoutOneOrMoreOfTheMandatoryFieldsEvenWithCategory() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/despesas");

		// Criamos os objetos a serem cadastrados no banco de dados:

		// 1 - Com a descrição em branco
		ExpenseForm formDescriptionNull = new ExpenseForm("", new BigDecimal(200), LocalDate.of(2022, Month.AUGUST, 05),
				Category.OUTRAS);

		// 2 - Com a data em branco
		ExpenseForm formDataNull = new ExpenseForm("Aluguel", new BigDecimal(1800), null, Category.MORADIA);

		// 3 - Com o valor em branco
		ExpenseForm formValueNull = new ExpenseForm("Inglês", null, LocalDate.of(2022, Month.AUGUST, 01),
				Category.EDUCACAO);

		// 4 - Com o valor e descrição em branco
		ExpenseForm formValueAndDescriptionNull = new ExpenseForm("", null, LocalDate.of(2022, Month.AUGUST, 01),
				Category.EDUCACAO);

		// 5 - Com o valor e data em branco
		ExpenseForm formValueAndDateNull = new ExpenseForm("Inglês", null, null, Category.EDUCACAO);

		// 6 - Com o valor, data e descrição em branco
		ExpenseForm formValueDateAndDescriptionNull = new ExpenseForm("Inglês", null, null, Category.EDUCACAO);

		// Com o MockMvc enviamos uma requisição do tipo "post" com os dados incorretos
		// para o cadastro, verificamos se o retorno foi "BadRequest" e finalizamos
		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formDescriptionNull))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formDataNull))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formValueNull))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formValueAndDateNull))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formValueAndDescriptionNull))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
		mockMvc.perform(
				MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formValueDateAndDescriptionNull))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	// 4º Teste - Cadastro de Despesa com descrição já utilizada dentro do mesmo mês
	@Test
	void shouldntInsertAnExpenseWithTheSameDescriptionInTheSameMonth() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/despesas");

		// Criamos os objetos a serem cadastrados no banco de dados:

		// 1 - Primeiro aluguel a ser inserido dentro do mês de agosto
		ExpenseForm formAluguel = new ExpenseForm("Aluguel", new BigDecimal(1800), LocalDate.of(2022, Month.AUGUST, 05),
				Category.MORADIA);

		// 2 - Segundo aluguel a ser inserido dentro do mês de agosto com todos os
		// demais dados diferentes
		ExpenseForm formAluguelSameMonth = new ExpenseForm("Aluguel", new BigDecimal(1800),
				LocalDate.of(2022, Month.AUGUST, 15), Category.OUTRAS);

		// Com o MockMvc enviamos uma requisição do tipo "post" com os dados corretos
		// para o cadastro, verificamos se o retorno foi "Created", depois solicitamos
		// uma nova inserção com a mesma descrição e esperamos o retorno "BadRequest"

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formAluguel))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formAluguelSameMonth))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	// Método para testar a verificação do banco de dados, verificando se os 3
	// registros inseridos anteriormente pelos demais testes se encontram presentes
	@Test
	void shouldListAllExpensesInTheDatabaseWithoutFilter() throws Exception {
		// Utilizamos a uri "/despesas" para acessar o endpoint em questão
		URI uri = new URI("/despesas");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Convertemos o corpo do retorno para uma lista de "ExpenseDto" usando
		// "Jackson"
		List<ExpenseDto> expensesList = mapper.readValue(
				result.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<ExpenseDto>>() {
				});

		// Realizamos a verificação da quantidade de registros no banco de dados pelo
		// repository, já que não podemos garantir a ordem das runs dos testes
		int expectedNumberOfRows = repository.findAll().size();

		// Realizamos o "assertEquals"
		assertEquals(expectedNumberOfRows, expensesList.size());
	}

	// Método para testar a verificação do banco de dados, verificando se serão
	// retornados 2 registros ao se utilizar o termo "ur" como parametro
	@Test
	void shouldListOnlyTwoOfTheExpensesBecauseOfTheParameter() throws Exception {
		// Utilizamos a uri "/despesas" para acessar o endpoint em questão
		URI uri = new URI("/despesas?descricao=ur");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Conversão dos dados para uma lista de "ExpenseDto"
		List<ExpenseDto> expensesList = mapper.readValue(
				result.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<ExpenseDto>>() {
				});

		// Criamos a lista de dados que esperamos que tenham sido retornados pela
		// consulta, para isso realizamos a consulta diretamente pelo repository, sem o
		// auxilio do controller
		List<ExpenseDto> expectedExpensesList = ExpenseDto.parse(repository.findByDescriptionWithLike("%ur%"));

		// Realizamos o "assertEquals"
		for (int i = 0; i < 2; i++) {
			assertEquals(expectedExpensesList.get(i), expensesList.get(i));
		}
	}

	// Método para retornar um array vazio no json ao se pesquisar um termo não
	// cadastrado como "abcdef"
	@Test
	void shouldReturnAnEmptyArrayForASearchWithANonExistentParameterOnTheDescription() throws Exception {
		// Utilizamos a uri "/despesas" para acessar o endpoint em questão e utilizamos
		// o parametro "abcdef" para a descrição, que não existe no banco de dados
		URI uri = new URI("/despesas?descricao=abcdef");

		// Verificarmos se o "statusCode" retornado foi o de "NotFound"
		mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	// Método para retornar uma única despesa detalhada.
	// Utilizamos uma instância para realizar a verificação dos dados e confrontar a
	// sua veracidade

	@Test
	void shouldReturnOnlyOneExpenseWithCorrectData() throws Exception {
		// Utilizamos a uri "/despesas/{id}" para acessar o endpoint em questão
		URI uri = new URI("/despesas/1");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Conversão dos dados para uma "ExpenseDto"
		ExpenseDto expenseDto = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				ExpenseDto.class);

		// Criamos a "ExpenseDto" que esperamos que tenha sido retornada pela
		// consulta, para isso vamos realizar a pesquisa diretamente, sem a utilização
		// do controller, por isso teremos aqui o repository
		ExpenseDto expectedExpensesDto = new ExpenseDto(repository.findById(1l).get());

		// Realizamos o "assertEquals"
		assertEquals(expectedExpensesDto, expenseDto);
	}

	// Método para verificar se o detalhamento de um código que não existe retorna
	// 404

	@Test
	void shouldntReturnANotFoundCodeWhenAnExpenseWhichTheCodeDoesntExistsIsSearched() throws Exception {
		// Utilizamos a uri "/despesas" para acessar o endpoint em questão
		URI uri = new URI("/despesas/9999");

		// Verificarmos se o "statusCode" retornado foi o de "NotFound"
		mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
