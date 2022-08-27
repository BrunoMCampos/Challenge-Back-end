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

import br.com.alura.financialmanagement.controller.dto.IncomeDto;
import br.com.alura.financialmanagement.controller.form.IncomeForm;
import br.com.alura.financialmanagement.repository.IncomeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class IncomeControllerTest {

	// Injeção de dependencia do MockMvc para realizar os testes no "controller"
	@Autowired
	private MockMvc mockMvc;

	// Injeção de dependência do repository para podermos realizar algumas buscas no
	// banco de dados
	@Autowired
	private IncomeRepository repository;

	// Preparamos o mapper usando uma configuração para aceitar LocalDate, já que
	// este será utilizado em todos os testes para a conversão de JSON
	private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	// Como estaremos sempre criando as tabelas ao iniciar o ambiente de testes
	// vamos começar os testes com o cadastro de algumas "receitas" no banco de
	// dados
	//
	// Dados de uma receitas:
	//
	// Id(id) -> Long (gerado automaticamente)
	//
	// Data (date) -> LocalDate
	// Descrição (description) -> String
	// Valor(value) -> BigDecimal
	//
	// Classes utilizadas para exibir os dados necessários e realizar os cadastros
	// das informações(Data, Descrição e Valor):
	//
	// IncomeDto
	// IncomeForm
	//
	// Regras de negócio a serem verificadas:
	// Todas as informações são obrigatórias
	// Não deve ser permitido o cadastro de Receitas com a mesma descrição dentro de
	// um mesmo mês
	//
	// Os dados aqui cadastrados serão reutilizados nos demais testes, sendo assim,
	// segue abaixo uma lista dos registros inseridos pelos testes (lembrando que a
	// ordem varia de run para run):
	//
	// "Salário" - R$ 960,00 - 2022-08-05;
	// "Aluguel de Chácara na Figueira" - R$ 1.800,00 - 2022-08-05;
	//
	// 1º Teste - Cadastro de Receita com todos os dados corretos
	@Test
	void shouldReturnCreatedAndInsertAnIncomeInTheDatabase() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/receitas");

		// Criamos o objeto a ser cadastrado no banco de dados com a categoria
		IncomeForm form = new IncomeForm("Salário", new BigDecimal(960), LocalDate.of(2022, Month.AUGUST, 05));
		
		System.out.println(mapper.writeValueAsString(form));

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
		IncomeDto incomeDto = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				IncomeDto.class);

		// Convertemos o "form" para uma "dto" e verificamos com "assertEquals"
		assertEquals(form.parseToDto(), incomeDto);
	}

	// 2º Teste - Cadastro de Receita com um dos dados incorretos
	@Test
	void shouldntInsertAnIncomeWithoutOneOrMoreOfTheMandatoryFields() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/receitas");

		// Criamos os objetos a serem cadastrados no banco de dados:

		// 1 - Com a descrição em branco
		IncomeForm formDescriptionNull = new IncomeForm("", new BigDecimal(200), LocalDate.of(2022, Month.AUGUST, 05));

		// 2 - Com a data em branco
		IncomeForm formDataNull = new IncomeForm("Venda de Roupas", new BigDecimal(100), null);

		// 3 - Com o valor em branco
		IncomeForm formValueNull = new IncomeForm("Aulas", null, LocalDate.of(2022, Month.AUGUST, 01));

		// 4 - Com o valor e descrição em branco
		IncomeForm formValueAndDescriptionNull = new IncomeForm("", null, LocalDate.of(2022, Month.AUGUST, 01));

		// 5 - Com o valor e data em branco
		IncomeForm formValueAndDateNull = new IncomeForm("Saque de FGTS", null, null);

		// 6 - Com o valor, data e descrição em branco
		IncomeForm formValueDateAndDescriptionNull = new IncomeForm("", null, null);

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

	// 3º Teste - Cadastro de Receita com descrição já utilizada dentro do mesmo mês
	@Test
	void shouldntInsertAnIncomeWithTheSameDescriptionInTheSameMonth() throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		URI uri = new URI("/receitas");

		// Criamos os objetos a serem cadastrados no banco de dados:

		// 1 - Primeiro aluguel a ser inserido dentro do mês de agosto
		IncomeForm formAluguel = new IncomeForm("Aluguel da Chácara na Figueira", new BigDecimal(1800),
				LocalDate.of(2022, Month.AUGUST, 05));

		// 2 - Segundo aluguel a ser inserido dentro do mês de agosto com todos os
		// demais dados diferentes
		IncomeForm formAluguelSameMonth = new IncomeForm("Aluguel da Chácara na Figueira", new BigDecimal(1800),
				LocalDate.of(2022, Month.AUGUST, 15));

		// Com o MockMvc enviamos uma requisição do tipo "post" com os dados corretos
		// para o cadastro, verificamos se o retorno foi "Created", depois solicitamos
		// uma nova inserção com a mesma descrição e esperamos o retorno "BadRequest"

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formAluguel))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formAluguelSameMonth))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	// Método para testar a verificação do banco de dados, verificando se os 2
	// registros inseridos anteriormente pelos demais testes se encontram presentes
	@Test
	void shouldListAllIncomesInTheDatabaseWithoutFilter() throws Exception {
		// Utilizamos a uri "/receitas" para acessar o endpoint em questão
		URI uri = new URI("/receitas");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Convertemos o corpo do retorno para uma lista de "IncomeDto" usando
		// "Jackson"
		List<IncomeDto> expensesList = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				new TypeReference<List<IncomeDto>>() {
				});

		// Realizamos a verificação da quantidade de registros no banco de dados pelo
		// repository, já que não podemos garantir a ordem das runs dos testes
		int expectedNumberOfRows = repository.findAll().size();

		// Realizamos o "assertEquals"
		assertEquals(expectedNumberOfRows, expensesList.size());
	}

	// Método para testar a verificação do banco de dados, verificando se serão
	// retornados 2 registros ao se utilizar o termo "alll" como parametro e apenas
	// um
	// resultado ao utilizar a expressão "alu"
	@Test
	void shouldListOnlyTwoOfTheExpensesBecauseOfTheParameterAndAfterThisShouldListOnlyOneWithOtherParameter()
			throws Exception {
		// Criamos a variável "uri" para armazenar o caminho para qual será enviado a
		// requisição
		//
		// Para este teste estaremos utilizando duas URIs, pois vamos inserir os dados e
		// depois realizar a verificação, uma vez que os testes não tem garantia de
		// serem rodados sempre na mesma ordem em que foram criados
		//
		// Parte 1 - Inserção dos dois dados com o termo "alll" para pesquisa
		URI uri = new URI("/receitas");

		// Criamos o objeto a ser cadastrado no banco de dados
		IncomeForm formAlll1 = new IncomeForm("Vale alll1", new BigDecimal(960), LocalDate.of(2022, Month.AUGUST, 05));

		// Criamos o objeto a ser cadastrado no banco de dados
		IncomeForm formAlll2 = new IncomeForm("Venda alll2", new BigDecimal(500), LocalDate.of(2022, Month.AUGUST, 11));

		// Com o MockMvc enviamos uma requisição do tipo "post" com os dados corretos
		// para o cadastro, verificamos se o retorno foi "Created" e armazenamos este
		// retorno em uma variável para podemos analizar seu conteúdo
		MvcResult resultAlll1 = mockMvc
				.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formAlll1))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

		MvcResult resultAlll2 = mockMvc
				.perform(MockMvcRequestBuilders.post(uri).content(mapper.writeValueAsString(formAlll2))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

		// Pegamos o "content" do resultado e verificamos se as informações
		// do JSON correspondem às informações cadastradas
		//
		// Para isso usamos "jackson" para a conversão e analizamos os objetos
		// com um "assertEquals"

		// Utilizamos o mapper para a conversão
		IncomeDto incomeDtoAlll1 = mapper
				.readValue(resultAlll1.getResponse().getContentAsString(StandardCharsets.UTF_8), IncomeDto.class);

		IncomeDto incomeDtoAlll2 = mapper
				.readValue(resultAlll2.getResponse().getContentAsString(StandardCharsets.UTF_8), IncomeDto.class);

		// Convertemos o "form" para uma "dto" e verificamos com "assertEquals"
		assertEquals(formAlll1.parseToDto(), incomeDtoAlll1);
		assertEquals(formAlll2.parseToDto(), incomeDtoAlll2);

		// Parte 2 - Consulta dos dados para verificação da funcionalidade de pesquisa
		// com like

		// Utilizamos a uri "/receitas" para acessar o endpoint em questão com o
		// parametro "al" para retornar 2 registros
		uri = new URI("/receitas?descricao=alll");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Conversão dos dados para uma lista de "IncomeDto"
		List<IncomeDto> expensesList = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				new TypeReference<List<IncomeDto>>() {
				});

		// Criamos a lista de dados que esperamos que tenham sido retornados pela
		// consulta, para isso realizamos a consulta diretamente pelo repository, sem o
		// auxilio do controller
		List<IncomeDto> expectedIncomeList = IncomeDto.parse(repository.findByDescriptionWithLike("%alll%"));

		// Realizamos o "assertEquals"
		for (int i = 0; i < 2; i++) {
			assertEquals(expectedIncomeList.get(i), expensesList.get(i));
		}

		// Parte 3 - Pesquisa do termo "alll2" para retornar apenas 1 dado

		// Repetimos o processo, agora com o termo "alll2"
		// Utilizamos a uri "/receitas" para acessar o endpoint em questão com o
		// parametro "alll2" para retornar 1 registros
		uri = new URI("/receitas?descricao=alll2");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Conversão dos dados para uma lista de "IncomeDto"
		expensesList = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				new TypeReference<List<IncomeDto>>() {
				});

		// Criamos a lista de dados que esperamos que tenham sido retornados pela
		// consulta, para isso realizamos a consulta diretamente pelo repository, sem o
		// auxilio do controller
		expectedIncomeList = IncomeDto.parse(repository.findByDescriptionWithLike("%alll2%"));

		// Realizamos o "assertEquals"
		for (int i = 0; i < 1; i++) {
			assertEquals(expectedIncomeList.get(i), expensesList.get(i));
		}
	}

	// Método para retornar um array vazio no json ao se pesquisar um termo não
	// cadastrado como "abcdef"
	@Test
	void shouldReturnAnEmptyArrayForASearchWithANonExistentParameterOnTheDescription() throws Exception {
		// Utilizamos a uri "/receitas" para acessar o endpoint em questão e utilizamos
		// o parametro "abcdef" para a descrição, que não existe no banco de dados
		URI uri = new URI("/receitas?descricao=abcdef");

		// Verificarmos se o "statusCode" retornado foi o de "NotFound"
		mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	// Método para retornar uma única "Receita" detalhada.
	// Utilizamos uma instância para realizar a verificação dos dados e confrontar a
	// sua veracidade

	@Test
	void shouldReturnOnlyOneIncomeWithCorrectData() throws Exception {
		// Utilizamos a uri "/receitas/{id}" para acessar o endpoint em questão
		URI uri = new URI("/receitas/1");

		// Armazenamos a resposta do servidor dentro de uma variável MvcResult após
		// verificarmos se o "statusCode" retornado foi o de "ok"
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Conversão dos dados para uma "IncomeDto"
		IncomeDto expenseDto = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				IncomeDto.class);

		// Criamos a "ExpenseDto" que esperamos que tenha sido retornada pela
		// consulta, para isso vamos realizar a pesquisa diretamente, sem a utilização
		// do controller, por isso teremos aqui o repository
		IncomeDto expectedIncomeDto = new IncomeDto(repository.findById(1l).get());

		// Realizamos o "assertEquals"
		assertEquals(expectedIncomeDto, expenseDto);
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
