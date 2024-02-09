package pt.iscte.poo.sokobanstarter;

import pt.iscte.poo.utils.Point2D;

public class Teleporte extends GameElement {

	public Teleporte(Point2D Point2D, String imageName) {
		super(Point2D, imageName);
	}

	@Override
	public int getLayer() {
		return 1;
	}

}
