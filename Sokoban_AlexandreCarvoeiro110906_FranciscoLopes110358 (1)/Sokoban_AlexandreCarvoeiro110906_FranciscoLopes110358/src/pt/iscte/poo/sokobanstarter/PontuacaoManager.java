package pt.iscte.poo.sokobanstarter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PontuacaoManager {

	// Mapeia o nome do jogador(chave) para suas pontuações por nivel (valor)
	private Map<String, Map<Integer, PontuacaoJogador>> pontuacoes;

	public PontuacaoManager() {
		pontuacoes = new HashMap<>();
	}

	public void registarPontuacao(String player, int nivel, int numMovimentos, int numEmpurroes, int energia) {
		// Obtem as pontuacoes do jogador para todos os niveis
		Map<Integer, PontuacaoJogador> pontuacoesPorJogador = pontuacoes.get(player);

		// Se ainda nao houver pontuacoes para esse jogador, cria o mapa
		if (pontuacoesPorJogador == null) {
			pontuacoesPorJogador = new HashMap<>();
			pontuacoes.put(player, pontuacoesPorJogador);
		}

		// Obtem as pontuacoes do jogador para o nivel atual
		PontuacaoJogador pontuacaoAtual = pontuacoesPorJogador.get(nivel);

		// Se ainda nao houver pontuacoes para esse nivel, cria se uma nova instancia de
		// PontuacaoJogador
		if (pontuacaoAtual == null) {
			pontuacaoAtual = new PontuacaoJogador();
			pontuacoesPorJogador.put(nivel, pontuacaoAtual);
		}

		// Adiciona as pontuacoes para o nível atual
		pontuacaoAtual.adicionarPontuacao(numMovimentos, numEmpurroes, energia);
	}

	public void gravarPontuacoes(String ficheiro) {
		try (PrintWriter filewriter = new PrintWriter(new FileWriter(ficheiro, true))) {
			for (Map.Entry<String, Map<Integer, PontuacaoJogador>> entry : pontuacoes.entrySet()) {
				String jogador = entry.getKey();
				Map<Integer, PontuacaoJogador> pontuacoesPorNivel = entry.getValue();

				// Encontrar o nivel maximo atingido pelo jogador
				int nivelMaximo = Collections.max(pontuacoesPorNivel.keySet());

				// Gravar a pontuacao apenas para o nivel maximo
				PontuacaoJogador pontuacaoMaxima = pontuacoesPorNivel.get(nivelMaximo);
				filewriter.println(jogador + " - Nivel " + nivelMaximo + " - " + pontuacaoMaxima);
			}
		} catch (IOException e) {
			System.err.println("Erro na criacao ou escrita do ficheiro");
		}
	}

	private static class PontuacaoJogador {
		private int totalMovimentos;
		private int totalEmpurroes;
		private int totalEnergia;

		public void adicionarPontuacao(int movimentos, int empurroes, int energia) {
			totalMovimentos += movimentos;
			totalEmpurroes += empurroes;
			totalEnergia += energia;
		}

		@Override
		public String toString() {
			return "Movimentos: " + totalMovimentos + ", Empurroes: " + totalEmpurroes + ", Energia: " + totalEnergia;
		}

	}

}
