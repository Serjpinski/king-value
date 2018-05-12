package kingvalue.search;

import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private Piece[][] pieces;
	private int[] pieceCountsByType;
	private int[] pieceCountsBySide;
	private Side sideToMove;

	public Board () {

		pieces = new Piece[8][8];
		pieceCountsByType = new int[Piece.Type.values().length];

		pieceCountsBySide = new int[Side.values().length];
		for (int i = 0; i < pieceCountsBySide.length; i++) {
			pieceCountsBySide[i] = 16;
		}

		sideToMove = Side.WHITE;
	}

	public Piece getPiece(Position position) {
		return pieces[position.col][position.row];
	}

	public int[] getPieceCountsByType() {
		return pieceCountsByType;
	}

	public int[] getPieceCountsBySide() {
		return pieceCountsBySide;
	}

	public Side getSideToMove() {
		return sideToMove;
	}

	public void move (Move move) {

		if (move.captured != null) {
			pieceCountsByType[move.captured.type.ordinal()] -= move.captured.side == Side.WHITE ? 1 : -1;
			pieceCountsBySide[move.captured.side.ordinal()]--;
		}

		pieces[move.to.col][move.to.row] = pieces[move.from.col][move.from.row];
		pieces[move.from.col][move.from.row] = null;

		if (move.promotion != null) {
			pieces[move.to.col][move.to.row].type = move.promotion.type;
			pieceCountsByType[Piece.Type.PAWN.ordinal()] -= move.promotion.side == Side.WHITE ? 1 : -1;
			pieceCountsByType[move.promotion.type.ordinal()] += move.promotion.side == Side.WHITE ? 1 : -1;
		}
	}

	public void undoMove (Move move) {

		if (move.promotion != null) {
			pieces[move.to.col][move.to.row].type = Piece.Type.PAWN;
			pieceCountsByType[Piece.Type.PAWN.ordinal()] += move.promotion.side == Side.WHITE ? 1 : -1;
			pieceCountsByType[move.promotion.type.ordinal()] -= move.promotion.side == Side.WHITE ? 1 : -1;
		}

		pieces[move.from.col][move.from.row] = pieces[move.to.col][move.to.row];
		pieces[move.to.col][move.to.row] = move.captured;

		if (move.captured != null) {
			pieceCountsByType[move.captured.type.ordinal()] += move.captured.side == Side.WHITE ? 1 : -1;
			pieceCountsBySide[move.captured.side.ordinal()]++;
		}
	}

	public List<Move> getLegalMovements () {
				
		List<Move> legalMoves = new ArrayList<>();
		
		// TODO
		
		return legalMoves;
	}

	public enum Side {
		WHITE,
		BLACK
	}

	public static class Piece {

		public enum Type {
			PAWN,
			KNIGHT,
			BISHOP,
			ROOK,
			QUEEN,
			KING
		}

		public Type type;
		public Side side;
	}
}