package kingvalue.search;

public class SelectedMovement {

	private Move move;
	private double score;
	private SelectedMovement nextPVMove;

	public SelectedMovement (Move move, double score,
                             SelectedMovement nextPVMove) {
		
		this.move = move;
		this.score = score;
		this.nextPVMove = nextPVMove;
	}

	public Move getMove() {
		
		return move;
	}

	public double getScore () {
		
		return score;
	}

	public SelectedMovement getNextPVMove () {
		
		return nextPVMove;
	}

	public void setMove(Move move) {
		
		this.move = move;
	}

	public void setScore (double score) {
		
		this.score = score;
	}

	public void setNextPVMove (SelectedMovement nextPVMove) {
		
		this.nextPVMove = nextPVMove;
	}
}
