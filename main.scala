object ProgrammingAssignment {
  case class Tile(x: Int, y: Int) {
    override def toString() = s"(${x},${y})"
  }
  type Path = Seq[Tile]
  case class Move(x: Int, y: Int)
  type Direction = String
  type Rotation = String

  val Moves: Array[(Direction, Move)] = Array(
      ("N", Move(0, -3)),
      ("NE", Move(2, -2)),
      ("E", Move(3, 0)),
      ("SE", Move(2, 2)),
      ("S", Move(0, 3)),
      ("SW", Move(-2, 2)),
      ("W", Move(-3, 0)),
      ("NW", Move(-2, -2))
  )

  val DefaultDirection = "N"
  val DefaultRotation = "clockwise"
  val MaxIterations = 1000000

  def main(args: Array[String]) {
    val argTile = if (args.size >= 1) args(0) else null
    for (tile <- startTiles(argTile)) {
      val direction = if (args.size >= 2) args(1).toUpperCase() else DefaultDirection
      val rotation = if (args.size >= 3) args(2) else DefaultRotation
      print(s"${tile} ${direction}:${rotation}")
      val moves = movesInDirection(Moves, direction)
      val selectMoves = (path: Path) => {
        validMoves(path, continueRotateMoves(path, moves, rotation))
      }
      val (iterations, path) = findPath(Array(tile), selectMoves)
      println(s" iterations=${iterations} path.size=${path.size} path=${path.mkString(", ")}")
    }
  }

  def findPath(previousPath: Path, selectMoves: Path => Seq[Move], previousIterations: Int = 0): (Int, Path) = {
    var path = previousPath
    var iterations = previousIterations + 1
    if (iterations < MaxIterations) {
      for (move <- selectMoves(path)) {
        val nextPath = previousPath ++ Array(nextTile(previousPath.last, move))
        val (foundIterations, foundPath) = findPath(nextPath, selectMoves, iterations)
        if (foundPath.size == 100) return (foundIterations, foundPath)
        iterations = foundIterations
        if (foundPath.size > path.size) path = foundPath
      }
    }
    (iterations, path)
  }

  def movesInDirection(moves: Seq[(Direction, Move)], direction: Direction): Seq[Move] = {
    var result = moves
    if (direction != 'N') {
     val index = moves.indexWhere(t => t._1 == direction)
     result = moves.slice(index, moves.size) ++ moves.slice(0, index)
    }
    result.map { m => m._2 }
  }

  def nextTile(currentTile: Tile, move: Move): Tile = {
    Tile(
      (currentTile.x + move.x),
      (currentTile.y + move.y)
    )
  }

  def validMoves(path: Path, moves: Seq[Move]): Seq[Move] = {
    moves.filter { move => moveIsValid(path, move) }
  }

  def continueRotateMoves(path: Path, moves: Seq[Move], rotation: Rotation): Seq[Move] = {
    val currentTile = path.last
    val previousTile = if (path.size > 1) path(path.size - 2) else null
    if (previousTile != null) {
      val previousMove = moveBetweenTiles(previousTile, currentTile)
      val previousMoveIndex = moves.indexWhere { m => m == previousMove }
      if (rotation == "clockwise") {
        val clockwise = moves.slice(previousMoveIndex, moves.size)
        val antiClockwise = moves.slice(0, previousMoveIndex)
        clockwise ++ antiClockwise
      } else if (rotation == "anticlockwise") {
        val antiClockwise = moves.slice(0, previousMoveIndex + 1).reverse
        val clockwise = moves.slice(previousMoveIndex + 1, moves.size).reverse
        antiClockwise ++ clockwise
      } else {
        throw new IllegalArgumentException(s"Unsupported rotation ${rotation}")
      }
    } else {
      // First move
      moves
    }
  }

  def moveBetweenTiles(fromTile: Tile, toTile: Tile): Move = {
    Move(
      (toTile.x - fromTile.x),
      (toTile.y - fromTile.y)
    )
  }

  def moveIsValid(path: Path, move: Move): Boolean = {
    val tile = nextTile(path.last, move)
    tileIsOnBoard(tile) && !tileVisited(path, tile)
  }

  def tileIsOnBoard(tile: Tile): Boolean = {
    tile.x >= 0 && tile.x <= 9 && tile.y >= 0 && tile.y <= 9
  }

  def tileVisited(path: Path, tile: Tile): Boolean = {
    path.exists { t => t.x == tile.x && t.y == tile.y }
  }

  def startTiles(tileString: String): Path = {
    if (tileString != null && tileString != "all") {
      return Array(parseTile(tileString))
    } else {
      return allTiles()
    }
  }

  def parseTile(tileString: String): Tile = {
    val Array(x,y) = tileString.split(",").map(Integer.parseInt)
    Tile(x, y)
  }

  def allTiles(): Path = {
    return (0 to 99).map { n =>
      Tile(
        x = n % 10,
        y = n / 10
      )
    }
  }
}
