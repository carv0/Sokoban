package pt.iscte.poo.sokobanstarter;

import java.util.List;
import pt.iscte.poo.utils.Point2D;

public class Bateria extends GameElement implements Interactable {

	public Bateria(Point2D Point2D, String imageName) {
		super(Point2D, imageName);
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void interactWithEmpilhadora(Empilhadora empilhadora, List<GameElement> gameElements) {
		empilhadora.ganharEnergia(50);
		GameEngine.getInstance().removeElementImage(this);
	}

}
