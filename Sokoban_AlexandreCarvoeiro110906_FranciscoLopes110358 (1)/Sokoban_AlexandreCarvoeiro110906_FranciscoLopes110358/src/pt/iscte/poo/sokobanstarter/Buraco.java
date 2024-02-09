package pt.iscte.poo.sokobanstarter;

import java.util.List;

import pt.iscte.poo.utils.Point2D;

public class Buraco extends GameElement implements Interactable {

	public Buraco(Point2D Point2D, String imageName) {
		super(Point2D, imageName);
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public void interactWithEmpilhadora(Empilhadora empilhadora, List<GameElement> gameElements) {
		GameEngine.getInstance().reiniciarNivel();
	}

}
