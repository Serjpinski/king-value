package kingvalue.search;

import java.util.List;

public class Engine {

	private static final double MAX_SCORE = 1000000;

	private final double[] evalWeights;

	public Engine(double[] evalWeights) {
	    this.evalWeights = evalWeights;
    }

	public Move search(Board board, int depthLimit, int timeLimit) {
		
		int timeZero = (int) (System.currentTimeMillis() / 1000);
		
		// Iterative deepening search
		SelectedMovement last = new SelectedMovement(null, -MAX_SCORE, null);
		
		int i = 1;
		
		while (last.getScore() != MAX_SCORE // Avoids random moves when the game is won
				&& (depthLimit < 1 || i <= depthLimit)
				&& !timeExpired(timeLimit, timeZero)) {
			
			// Recursive search to a fixed depth
			SelectedMovement move = searchRec(board, i,
					-MAX_SCORE, MAX_SCORE, last, timeLimit, timeZero);
			
			if (!timeExpired(timeLimit, timeZero) // Discards uncompleted searches
					&& move.getScore() != -MAX_SCORE) // Avoids random moves when the game is lost
				last = move;
			
			i++;
		}
		
		return last.getMove();
	}

	private SelectedMovement searchRec(Board board,
			int depth, double alpha, double beta, SelectedMovement nextPVMove,
			int timeLimit, int timeZero) {

        // If game is lost, returns the minimum score
        if (board.getPieceCountsBySide()[board.getSideToMove().ordinal()] == 0) {
            return new SelectedMovement(null, alpha, null);
        }

        // If maximum depth is reached, returns an estimated score
		if (depth ==  0) return new SelectedMovement(null, eval(board), null);

	    // If time expired, returns the minimum score (this is arbitrary)
		if (timeExpired(timeLimit, timeZero)) return new SelectedMovement(null, alpha, null);
		
	    List<Move> moves = board.getLegalMoves();
		
	    if (nextPVMove != null && nextPVMove.getMove() != null) {
	    	
			// Puts the principal variation move first
			boolean found = false;
			for (int i = 0; !found && i < moves.size(); i++) {

				if (moves.get(i).equals(nextPVMove.getMove())) {

					Move pv = moves.remove(i);
					moves.add(0, pv);
					found = true;
				}
			}
		}
	    
	    SelectedMovement best = new SelectedMovement(null, alpha, null);
	    
	    // Expands the children
	    for (int i = 0; i < moves.size(); i++) {
	    	
	    	Move move = moves.get(i);
	    	
//	    	// Checks for capture-in-spring victory condition
//	    	if (Terrain.SPRING.equals(board.getTerrain(move.to()))
//	    			&& board.getOrder(move.to()) == color.opposite())
//	    		return new SelectedMovement(move, beta, null);

            Board.Piece pieceTo = board.getPiece(move.to);
            if (pieceTo != null && Board.Piece.Type.KING.equals(pieceTo.type)) {
                return new SelectedMovement(move, beta, null);
            }
	    	
	    	board.move(move);
	    	
	    	// Obtains the score for this movement
	    	SelectedMovement result = searchRec(board,
	    				depth - 1, -beta, -best.getScore(),
	    				(nextPVMove != null && i == 0) ? nextPVMove.getNextPVMove() : null,
	    				timeLimit, timeZero);
	    	
	    	result.setScore(-result.getScore());
	    	
	    	board.undoMove(move);
	    	
	    	// Pruning
	    	if (result.getScore() >= beta)
	    		return new SelectedMovement(move, beta, result);
	    	
	    	// Saves the best score so far
	        if (result.getScore() > best.getScore()) {
	        	
	        	best.setScore(result.getScore());
	        	best.setMove(move);
	        	best.setNextPVMove(result);
	        }
	    }
	    
	    return best;
	}
	
	private double eval(Board board) {

	    double eval = 0;
	    int[] pieceCounts = board.getPieceCountsByType();

	    for (int i = 0; i < evalWeights.length; i++) {
	        eval += evalWeights[i] * pieceCounts[i];
        }

		return eval;
	}

	private static boolean timeExpired (int timeLimit, int timeZero) {
		
		return timeLimit > 0 && System.currentTimeMillis() / 1000 - timeZero > timeLimit;
	}
}
