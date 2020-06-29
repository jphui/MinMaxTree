import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyAI
  implements AI
{
  public String getName()
  {
    return "MinMaxTree";
  }
  
  //Return the best move myPiece can make using a MinMaxTree
  public Point getNextMove(TicTacToe game, TicTacToePiece myPiece)
  {
  	//if game is over, return null
    if (game.isGameOver()) { return null; }
  	
  	//Generate a MinMax tree using the TicTacToe game parameter
    TNode<MinMaxNode> root = generateMinMaxTree(game, null);  // Start with move=null in the root

  	//Score the MinMax tree
  	scoreMinMaxTree(root, myPiece);

  	//Create a list of MinMaxNodes from the children of the root of the MinMax tree you just generated
  	//if the root node has no children, return null
    List<MinMaxNode> rootDirectChildren = root.getChildrenData();
    if (rootDirectChildren.isEmpty()) { return null; }
  	
  	//Determine what the best (highest) score is among all of the MinMax nodes in the list your just created
    int max = -10;  // By default, the max will be the lowest possible value
    for (MinMaxNode checkMe : rootDirectChildren)
    {
      if (checkMe.getScore() > max)
        max = checkMe.getScore();
    }
  	
  	//Create a list to hold all of the moves (Point objects) that are rated with the best score
  	//loop through all of the children of the root of hte MinMax tree and add the Point from the ones with the "best score" to the list of Points you just created
    List<Point> good = new LinkedList<>();
    for (MinMaxNode checkMe : rootDirectChildren)
    {
      if (checkMe.getScore() == max)
        good.add(checkMe.getMove());
    }

    //return a random point from the list of Points
    return good.get((int) (Math.random()*good.size()));
  }
  
  //Generate a MinMaxTree consisting of a root node containing game, and children nodes 
  //containing all possible moves the current player can make
  private TNode<MinMaxNode> generateMinMaxTree(TicTacToe game, Point move)
  {
  	//make a copy of game (so you can modify the copy without changing game)
    TicTacToe copy = game.copy();
  	//if move is not null
  	//	apply move to the copy (addPiece and nextPlayer)
    if (move != null)
    {
      copy.addPiece(move.getRow(), move.getCol());
      copy.nextPlayer();
    }
  	
  	//Make a MinMaxNode with the copy and move
  	//Make a TNode with the MinMaxNode you just created
    MinMaxNode current = new MinMaxNode(copy, move);
    TNode<MinMaxNode> newNode = new TNode<MinMaxNode>(current);
  	
  	//recursively call generateMinMaxTree for each legal move that the new current player can make on copy (every empty space is a legal move)
  	//	add the TNode returned by the recursive generateMinMaxTree calls as a child to the TNode you created above
    for (Point empty : copy.getEmptySpaces()) { newNode.addChild(generateMinMaxTree(copy, empty)); }
  	
  	//return the TNode you created above
    return newNode;
  }
  
  //Generate a score for every node in the MinMaxTree (from the point of view of player)
  private void scoreMinMaxTree(TNode<MinMaxNode> root, TicTacToePiece player)
  {
  	//get the MinMaxNode out of the root node
  	//get the game out of the MinMaxNode
    MinMaxNode current = root.getData();
    TicTacToe currentGame = current.getGame();
  	
  	//if the game is over
  	//	use the setScore method to score the MinMaxNode based on who one the game
  	//		if player is the winner -> 	10 points
  	//		if the game is tied -> 		0 points
  	//		if player is the loser ->	-10 points
    if (currentGame.isGameOver())
    {
      TicTacToePiece winner = currentGame.getWinner();
      if (player == winner) { current.setScore(10); }
      else if (winner == null) { current.setScore(0); } // Tie since GameOver&&null
      else { current.setScore(-10); } // Last case: null=/=player=/=winner
    }
  	
  	//if the game is not over
  	//	recursively call scoreMinMaxTree on all of the root node's children
  	//	determine the lowest and highest scores among all of the root node's children
  	//	if it is player's turn
  	//		set this MinMaxNode's score to the highest score
  	//	if it is NOT player's turn
  	//		set this MinMaxNode's score to the lowest score
    else
    {
      for (TNode<MinMaxNode> child : root.getChildren()) { scoreMinMaxTree(child, player); }

      int min = 0;
      int max = 0;
      for (MinMaxNode checkMe : root.getChildrenData())
      {
        if (checkMe.getScore() < min)
          min = checkMe.getScore();
        if (checkMe.getScore() > max)
          max = checkMe.getScore();
      }

      if (player == currentGame.getCurrentPlayer())
        current.setScore(max);
      else
        current.setScore(min);
    }
  }
  
}
