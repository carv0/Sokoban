package pt.iscte.poo.sokobanstarter;

import java.util.List;

import pt.iscte.poo.utils.Point2D;

public class ParedeRachada extends Parede implements Interactable {

	public ParedeRachada(Point2D Point2D, String imageName) {
		super(Point2D, imageName);
	}

	@Override
	public void interactWithEmpilhadora(Empilhadora empilhadora, List<GameElement> gameElements) {
		if (empilhadora.getPossuiMartelo() == true) {
			GameEngine.getInstance().removeElementImage(this);
			empilhadora.perderEnergia(5);
		}
	}

}
