package pt.iscte.poo.sokobanstarter;

import java.util.List;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Palete extends GameElement implements Movable {

	private boolean teleportado = false;

	public Palete(Point2D Point2D, String imageName) {
		super(Point2D, imageName);
	}

	@Override
	public int getLayer() {
		return 2;
	}

	public boolean foiTeleportado() {
		return teleportado;
	}

	public void setTeleportado(boolean teleportado) {
		this.teleportado = teleportado;

	}

	@Override
	public void move(Direction direction, List<GameElement> gameElements) {

		Point2D newPosition = getPosition().plus(direction.asVector());

		// Verifica se a nova posicao e valida e nao esta ocupada por um elemento solido
		if (super.isValidPosition(newPosition) && !isPositionOccupied(newPosition, gameElements)) {
			// Move a Palete para a nova posicao
			setPosition(newPosition);
		}

	}

	@Override
	public void verificarTeleporte(Point2D position, List<GameElement> gameElements) {
		if (teleportado == false) {

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
						setTeleportado(true);
					}
				}
			}
		}

	}

}
