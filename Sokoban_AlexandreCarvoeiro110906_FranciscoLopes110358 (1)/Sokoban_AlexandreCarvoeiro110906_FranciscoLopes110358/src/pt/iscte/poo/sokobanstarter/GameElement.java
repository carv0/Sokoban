package pt.iscte.poo.sokobanstarter;

import java.util.List;
import java.util.function.Predicate;
import java.util.ArrayList;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public abstract class GameElement implements ImageTile {

	private Point2D Point2D; // Posicao do GameElement
	private String imageName; // Nome do GameElement

	public GameElement(Point2D Point2D, String imageName) {
		this.Point2D = Point2D;
		this.imageName = imageName;
	}

	@Override
	public String getName() {
		return imageName;
	}

	@Override
	public Point2D getPosition() {
		return Point2D;
	}

	@Override
	public abstract int getLayer();

	public void setPosition(Point2D newPosition) {
		this.Point2D = newPosition;
	}

	public static GameElement criar(String tipo, Point2D point2d) {

		switch (tipo) {
		case "Caixote":
			return new Caixote(point2d, tipo);
		case "Chao":
			return new Chao(point2d, tipo);
		case "Empilhadora":
			return new Empilhadora(point2d, "Empilhadora_T");
		case "Parede":
			return new Parede(point2d, tipo);
		case "Bateria":
			return new Bateria(point2d, tipo);
		case "Vazio":
			return new Vazio(point2d, tipo);
		case "Alvo":
			return new Alvo(point2d, tipo);
		case "Teleporte":
			return new Teleporte(point2d, tipo);
		case "Palete":
			return new Palete(point2d, tipo);
		case "ParedeRachada":
			return new ParedeRachada(point2d, tipo);
		case "Buraco":
			return new Buraco(point2d, tipo);
		case "Martelo":
			return new Martelo(point2d, tipo);

		default:
			throw new IllegalArgumentException();
		}
	}

	public boolean isValidPosition(Point2D position) {
		return position.getX() >= 0 && position.getX() < 10 && position.getY() >= 0 && position.getY() < 10;
	}

	// Uso da interface Predicate
	public static List<GameElement> select(List<GameElement> gameElements, Predicate<GameElement> filtro) {
		List<GameElement> selecao = new ArrayList<>();
		for (GameElement element : gameElements) {
			if (filtro.test(element)) {
				selecao.add(element);
			}
		}
		return selecao;
	}

}
