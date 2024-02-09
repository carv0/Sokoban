package pt.iscte.poo.sokobanstarter;

import pt.iscte.poo.utils.Point2D;

public class Chao extends GameElement {

	public Chao(Point2D Point2D, String imageName) {
		super(Point2D, imageName);
	}

	@Override
	public int getLayer() {
		return 0;
	}

}
