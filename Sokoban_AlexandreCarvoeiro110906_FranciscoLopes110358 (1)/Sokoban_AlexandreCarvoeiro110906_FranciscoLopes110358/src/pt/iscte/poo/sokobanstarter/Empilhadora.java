package pt.iscte.poo.sokobanstarter;

import java.util.List;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Empilhadora extends GameElement implements Movable {

	private Direction lastDirection;
	private int energia;
	private int numeroMovimentos;
	private int numeroEmpurroes;
	private boolean possuiMartelo = false;

	public Empilhadora(Point2D initialPosition, String imageName) {
		super(initialPosition, imageName);
		lastDirection = Direction.UP; // direcao padrao inicial
		energia = 100;

		numeroMovimentos = 0;
		numeroEmpurroes = 0;
	}

	@Override
	public String getName() {
		// Atualiza o nome da imagem com base na ultima direção
		return "Empilhadora_" + lastDirection.name().charAt(0);
	}

	@Override
	public int getLayer() {
		return 3;
	}

	public int getEnergia() {
		return energia;
	}

	public void perderEnergia(int quantidade) {
		energia -= quantidade;
	}

	public void ganharEnergia(int quantidade) {
		energia += quantidade;
	}

	public int getNumeroMovimentos() {
		return numeroMovimentos;
	}

	public int getNumeroEmpurroes() {
		return numeroEmpurroes;
	}

	public boolean getPossuiMartelo() {
		return possuiMartelo;
	}

	public void setPossuiMartelo(boolean possuiMartelo) {
		this.possuiMartelo = possuiMartelo;
	}

	// Metodo para verificar se uma posicao esta ocupada por uma parede
	public boolean isPositionOccupiedByParede(Point2D position, List<GameElement> gameElements) {
		// Verifica se a nova posicao e uma parede
		List<GameElement> paredesNaPosicao = GameElement.select(gameElements,
				element -> element instanceof Parede && element.getPosition().equals(position));

		// Retorna true se houver uma parede na posicao
		return !paredesNaPosicao.isEmpty();
	}

	private void verificarElemento(Point2D position, List<GameElement> gameElements) {

		// Itera sobre os elementos interativos na posição
		List<GameElement> elementosInterativosNaPosicao = GameElement.select(gameElements,
				element -> element instanceof Interactable && element.getPosition().equals(position));

		for (GameElement elementoInterativo : elementosInterativosNaPosicao) {
			Interactable interactiveElement = (Interactable) elementoInterativo;
			interactiveElement.interactWithEmpilhadora(this, gameElements);
		}

	}

	public void verificarElementoNoBuraco(GameElement element, List<GameElement> gameElements) {
		Point2D position = element.getPosition();
		// Verifica se a nova posicao e um buraco
		List<GameElement> buracosNaPosicao = GameElement.select(gameElements,
				e -> e instanceof Buraco && e.getPosition().equals(position));

		if (!buracosNaPosicao.isEmpty()) {

			if (element instanceof Caixote) {
				GameEngine.getInstance().removeElementImage(element);
			} else if (element instanceof Palete) {
				Buraco buraco = (Buraco) buracosNaPosicao.get(0);
				GameEngine.getInstance().removeElementImage(buraco);
				GameEngine.getInstance().removeElementImage(element);
			}
		}
	}

	public void verificarTeleporte(Point2D position, List<GameElement> gameElements) {

		// Verifica se a nova posicao e um portal de teleporte
		List<GameElement> teleportesNaPosicao = GameElement.select(gameElements,
				element -> element instanceof Teleporte && element.getPosition().equals(position));

		if (!teleportesNaPosicao.isEmpty()) {
			// Encontrar o outro portal de teleporte
			Teleporte teleporte = (Teleporte) teleportesNaPosicao.get(0);
			List<GameElement> outrosTeleportes = GameElement.select(gameElements,
					element -> element instanceof Teleporte && element != teleporte);

			if (!outrosTeleportes.isEmpty()) {
				Teleporte outroTeleporte = (Teleporte) outrosTeleportes.get(0);

				// Define a posicao da empilhadora para a posicao do outro portal
				if (super.isValidPosition(outroTeleporte.getPosition())
						&& !isPositionOccupied(outroTeleporte.getPosition(), gameElements)) {
					setPosition(outroTeleporte.getPosition());
				}
			}
		}

	}

	@Override
	public void move(Direction direction, List<GameElement> gameElements) {
		if (energia > 0) {
			Point2D newPosition = getPosition().plus(direction.asVector());
			verificarElemento(newPosition, gameElements);

			// Verifica se a nova posicao é valida e não está ocupada por uma parede
			if (super.isValidPosition(newPosition) && !isPositionOccupiedByParede(newPosition, gameElements)) {
				moverEmpilhadora(newPosition, direction, gameElements);
			}
		} else {
			return;
		}
	}

	private void moverEmpilhadora(Point2D newPosition, Direction direction, List<GameElement> gameElements) {
		List<GameElement> movableElements = GameElement.select(gameElements,
				element -> element instanceof Movable && element.getPosition().equals(newPosition));

		// Verifica se ha um elemento movable na nova posicao. Se sim tenta mover-los
		if (!movableElements.isEmpty()) {
			boolean canMoveElement = true;

			for (GameElement movableElement : movableElements) {
				Point2D elementNewPosition = movableElement.getPosition().plus(direction.asVector());
				if (!super.isValidPosition(elementNewPosition)
						|| isPositionOccupied(elementNewPosition, gameElements)) {
					// Se a nova posicao do elemento movable nao for valida ou estiver ocupada, nao
					// pode mover
					canMoveElement = false;
					break;
				}
			}

			// Se for possivel mover um elemento Movable, move tambem a empilhadora
			if (canMoveElement) {
				// Move a empilhadora para a nova posicao
				setPosition(newPosition);
				lastDirection = direction; // Atualiza a ultima direcao

				// Move os elementos Movable
				for (GameElement movableElement : movableElements) {
					Movable movable = (Movable) movableElement;
					movable.move(direction, gameElements);
					perderEnergia(2);

					numeroEmpurroes++;
					verificarElementoNoBuraco((GameElement) movable, gameElements);
				}
				numeroMovimentos++;

			}
		} else {
			// Se nao ha caixotes nem paletes na nova posicao, apenas move a empilhadora
			setPosition(newPosition);
			lastDirection = direction; // Atualiza a ultima direcao
			perderEnergia(1); // Perde 1 ponto ao mover

			numeroMovimentos++;
		}
	}

}