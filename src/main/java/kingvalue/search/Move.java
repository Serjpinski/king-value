package kingvalue.search;

public class Move {

	public Position from;
	public Position to;
	public Board.Piece promotion;
	public Board.Piece captured;

	public Move(Position from, Position to, Board.Piece promotion, Board.Piece captured) {
		
		this.from = from;
		this.to = to;
		this.promotion = promotion;
		this.captured = captured;
	}
}
