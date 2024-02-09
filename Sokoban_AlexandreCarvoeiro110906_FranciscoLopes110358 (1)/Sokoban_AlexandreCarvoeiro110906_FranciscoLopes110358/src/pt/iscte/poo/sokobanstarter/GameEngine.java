package pt.iscte.poo.sokobanstarter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {

	// Dimensoes da grelha de jogo
	public static final int GRID_HEIGHT = 10;
	public static final int GRID_WIDTH = 10;

	private static GameEngine INSTANCE; // Referencia para o unico objeto GameEngine (singleton)
	private ImageMatrixGUI gui; // Referencia para ImageMatrixGUI (janela de interface com o utilizador)

	private List<GameElement> gameElements;
	private Empilhadora bobcat;
	private int nivelAtual = 0;
	private String player;
	private PontuacaoManager pontuacaoManager;

	private GameEngine() {
		gameElements = new ArrayList<>();
		pontuacaoManager = new PontuacaoManager();
	}

	// Implementacao do singleton para o GameEngine
	public static GameEngine getInstance() {
		if (INSTANCE == null)
			return INSTANCE = new GameEngine();
		return INSTANCE;
	}

	// Inicio
	public void start() {

		// Setup inicial da janela que faz a interface com o utilizador

		gui = ImageMatrixGUI.getInstance(); // 1. obter instancia ativa de ImageMatrixGUI
		gui.setSize(GRID_HEIGHT, GRID_WIDTH); // 2. configurar as dimensoes
		gui.registerObserver(this); // 3. registar o objeto ativo GameEngine como observador da GUI
		gui.go(); // 4. lancar a GUI

		// Criar o cenario de jogo
		createWarehouse(); // criar o armazem
		createLevel("Level0.txt");
		sendImagesToGUI(); // enviar as imagens para a GUI

		player = solicitarNomeJogador();

	}

	// O metodo update() e' invocado automaticamente sempre que o utilizador carrega
	// numa tecla
	// no argumento do metodo e' passada uma referencia para o objeto observado
	// (neste caso a GUI)
	@Override
	public void update(Observed source) {

		try {

			int key = gui.keyPressed(); // obtem o codigo da tecla pressionada
			Direction direction = Direction.directionFor(key);
			bobcat.move(direction, gameElements);

			for (GameElement element : gameElements) {
				if (element instanceof Movable) {
					Movable movableElement = (Movable) element;
					movableElement.verificarTeleporte(element.getPosition(), gameElements);
				}
			}

			gui.update(); // redesenha a lista de ImageTiles na GUI,
							// tendo em conta as novas posicoes dos objetos

			if (bobcat.getEnergia() <= 0) {
				System.out.println("Game Over! Reiniciando nivel...");
				reiniciarNivel();
				return; // Nao executa o restante do codigo de atualizacao apos reiniciar o nivel
			}

			// Verifica a arrumacao apos cada movimento

			if (checkArrumacao()) {
				System.out.println("Arrumacao completa! Parabens!");
				pontuacaoManager.registarPontuacao(player, nivelAtual, bobcat.getNumeroMovimentos(),
						bobcat.getNumeroEmpurroes(), bobcat.getEnergia());
				pontuacaoManager.gravarPontuacoes("pontuacoes.txt");
				proximoNivel(); // Passa para o próximo nível
			}

			// Verifica se ha menos caixotes do que alvos e reinicia o nível, se necessário
			verificarCaixotesEAlvos();

			// Atualiza a mensagem de status com as informações desejadas
			String statusMessage = String.format("Level: %d - Player: %s - Moves: %d - Pushes: %d - Energy: %d",
					nivelAtual, player, bobcat.getNumeroMovimentos(), bobcat.getNumeroEmpurroes(), bobcat.getEnergia());

			gui.setStatusMessage(statusMessage);

		} catch (IllegalArgumentException e) {
			System.out.println("Erro: comando invalido");
		}

	}

	// Criacao da planta do armazem - so' chao neste exemplo
	private void createWarehouse() {

		for (int y = 0; y < GRID_HEIGHT; y++)
			for (int x = 0; x < GRID_HEIGHT; x++)
				gameElements.add(new Chao(new Point2D(x, y), "Chao"));
	}

	private String obterTipoElemento(char caracter) {
		switch (caracter) {
		case 'E':
			return "Empilhadora";
		case 'C':
			return "Caixote";
		case 'X':
			return "Alvo";
		case 'B':
			return "Bateria";
		case '#':
			return "Parede";
		case ' ':
			return "Chao";
		case '=':
			return "Vazio";
		case 'T':
			return "Teleporte";
		case 'O':
			return "Buraco";
		case 'P':
			return "Palete";
		case 'M':
			return "Martelo";
		case '%':
			return "ParedeRachada";
		default:
			throw new IllegalArgumentException("Tipo de elemento desconhecido: " + caracter);
		}
	}

	private void createLevel(String ficheiro) {
		try {
			Scanner scanner = new Scanner(new File(ficheiro));

			int numeroLinha = 0;
			while (scanner.hasNextLine()) {
				String linha = scanner.nextLine();

				for (int coluna = 0; coluna != linha.length(); coluna++) {
					char caracter = linha.charAt(coluna);
					Point2D coordenadas = new Point2D(coluna, numeroLinha);

					GameElement elemento = GameElement.criar(obterTipoElemento(caracter), coordenadas);
					if (elemento instanceof Empilhadora) {

						bobcat = (Empilhadora) elemento;
					}
					gameElements.add(elemento);
				}
				numeroLinha++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("Ficheiro " + ficheiro + " nao encontrado");
		}
	}

	// Nao e' suposto re-enviar os objetos se a unica coisa que muda sao as posicoes
	private void sendImagesToGUI() {
		for (GameElement element : gameElements) {
			gui.addImage(element);
		}

	}

	public void removeElementImage(GameElement element) {
		gui.removeImage(element); // Remove a imagem do GameElement da GUI
		gameElements.remove(element); // Remove o GameElement da lista gameElements
	}

	private boolean checkArrumacao() {
		// Para cada elemento do jogo, verifica se e um Alvo
		List<GameElement> alvos = GameElement.select(gameElements, element -> element instanceof Alvo);

		for (GameElement alvo : alvos) {
			Point2D alvoPosition = alvo.getPosition();

			// Verifica se ha um caixote na posicao do alvo
			boolean caixoteNoAlvo = GameElement
					.select(gameElements,
							element -> element instanceof Caixote && element.getPosition().equals(alvoPosition))
					.size() > 0;

			// Se nao houver caixote na posição do alvo, a arrumação não esta completa
			if (!caixoteNoAlvo) {
				return false;
			}
		}

		// Se todos os alvos tiverem caixotes, a arrumação esta completa
		return true;
	}

	public void reiniciarNivel() {
		// Limpa as imagens antigas da GUI
		gui.clearImages();

		// Limpa a lista de elementos do jogo
		gameElements.clear();

		// Cria novamente o armazem
		createWarehouse();

		// Carrega novamente o nivel atual
		String nivelAtualStr = "level" + nivelAtual + ".txt";
		createLevel(nivelAtualStr);

		// Envia as imagens do novo nivel para a GUI
		sendImagesToGUI();

		// Atualiza a mensagem de status
		gui.setStatusMessage("Nível " + nivelAtual);
	}

	private void proximoNivel() {
		nivelAtual++;

		// Limpa as imagens antigas da GUI
		gui.clearImages();

		// Limpa a lista de elementos do jogo
		gameElements.clear();

		createWarehouse();

		// Carrega o próximo nível
		String proximoNivel = "level" + nivelAtual + ".txt";
		createLevel(proximoNivel);

		// Envia as imagens do novo nível para a GUI
		sendImagesToGUI();

		// Atualiza a mensagem de status
		gui.setStatusMessage("Nível " + nivelAtual);

	}

	// Metodo para solicitar o nome do jogador
	private String solicitarNomeJogador() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Digite seu nome: ");
		String nome = sc.nextLine();
		sc.close();
		return nome;
	}

	// Metodo para verificar se ha menos caixotes do que alvos e reiniciar o nivel,
	// se necessario
	private void verificarCaixotesEAlvos() {
		// Seleciona todos os alvos no nivel
		List<GameElement> alvos = GameElement.select(gameElements, element -> element instanceof Alvo);

		// Seleciona todos os caixotes no nivel
		List<GameElement> caixotes = GameElement.select(gameElements, element -> element instanceof Caixote);

		// Se houver menos caixotes do que alvos, reinicia o nivel
		if (caixotes.size() < alvos.size()) {
			reiniciarNivel();
		}
	}

}
