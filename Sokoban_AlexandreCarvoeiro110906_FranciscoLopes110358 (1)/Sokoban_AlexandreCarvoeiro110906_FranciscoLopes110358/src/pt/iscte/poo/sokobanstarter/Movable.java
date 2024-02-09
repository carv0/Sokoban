package pt.iscte.poo.sokobanstarter;

import java.util.List;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public interface Movable {

	void move(Direction direction, List<GameElement> gameElements);

	void verificarTeleporte(Point2D position, List<GameElement> gameElements);

	default public boolean isPositionOccupied(Point2D position, List<GameElement> gameElements) {
		// Verifica se a posição esta ocupada por outra coisa que não seja chao
		List<GameElement> elementsNaPosicao = GameElement.select(gameElements,
				element -> !(element instanceof Chao) && !(element instanceof Alvo) && !(element instanceof Buraco)
						&& !(element instanceof Teleporte) && element.getPosition().equals(position));

		// Retorna true se a posicao estiver ocupada
		return !elementsNaPosicao.isEmpty();
	}

}
