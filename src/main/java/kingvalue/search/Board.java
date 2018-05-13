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

		sideToMove = sideToMove == Side.WHITE ? Side.BLACK : Side.WHITE;
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

		sideToMove = sideToMove == Side.WHITE ? Side.BLACK : Side.WHITE;
	}

	public List<Move> getLegalMoves() {
				
		List<Move> legalMoves = new ArrayList<>();
		int side = sideToMove == Side.WHITE ? 1 : -1;
		
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[0].length; j++) {

				Piece piece = pieces[i][j];

				if (piece != null && sideToMove == piece.side) {

					Position position = new Position(i, j);

					if (piece.type == Piece.Type.PAWN) {

						int j2 = j + side;

						if (pieces[i][j2] == null) {
							if (j == 6) {
								for (Piece.Type type : Piece.Type.values()) {
									if (type != Piece.Type.PAWN) {
										legalMoves.add(new Move(position, new Position(i, j2), new Piece(type, sideToMove), null));
									}
								}
							}
							else {
								legalMoves.add(new Move(position, new Position(i, j2), null, null));
							}
						}

						if (i > 0 && pieces[i - 1][j2] != null && pieces[i - 1][j2].side != sideToMove) {
							if (j == 6) {
								for (Piece.Type type : Piece.Type.values()) {
									if (type != Piece.Type.PAWN) {
										legalMoves.add(new Move(position, new Position(i - 1, j2), new Piece(type, sideToMove), pieces[i - 1][j2]));
									}
								}
							}
							else {
								legalMoves.add(new Move(position, new Position(i - 1, j2), null, pieces[i - 1][j2]));
							}
						}

						if (i < 7 && pieces[i + 1][j2] != null && pieces[i + 1][j2].side != sideToMove) {
							if (j == 6) {
								for (Piece.Type type : Piece.Type.values()) {
									if (type != Piece.Type.PAWN) {
										legalMoves.add(new Move(position, new Position(i + 1, j2), new Piece(type, sideToMove), pieces[i + 1][j2]));
									}
								}
							}
							else {
								legalMoves.add(new Move(position, new Position(i + 1, j2), null, pieces[i + 1][j2]));
							}
						}
					}
					else if (piece.type == Piece.Type.KNIGHT) {
						
					}
				}
			}
		}
		
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

		public Piece(Type type, Side side) {
			this.type = type;
			this.side = side;
		}
	}
}