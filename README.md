# Programming Assignment - Visit All Tiles on the Board

This is the solution to an interesting programming assignment
I was given when applying for a programming job.

## Problem Description

You have a 10x10 chequerboard (like a chess board but slightly larger).
A pawn can move in the following way on the board:

* 3 tiles North (N), West (W), South (S), and East (E)
* 2 tiles NE, SE, SW, NW (diagonal moves)

A move is only valid/allowed if:

* The destination tile is on the board
* The tile has not been visited before

Starting from any tile on the board, design a program that will find
a path for the pawn so that it visits all tiles on the board.

Example: suppose we have a coordinate system for the tiles in x horizonal axis, 0-9, and y vertical axis, 0-9. Further suppose
that the pawn starts at tile x=0, y=0 (upper left corner, i.e. coordinate 0,0). Valid moves in that position would then be South (to tile 0,3), West (to tile 3,0), and South West (to tile 2,2).

## Algorithm - Continue and Rotate

The algorithm accepts three inputs:

* Start tile - x,y coordinate where x and y are between 0 and 9 (x is the horizontal axis, y is the vertical)
* Start direction (optional). Which direction to attempt the first move in. One of
  the moves N, NE, E, SE, S, SW, W, NW. Defaults to N.
* Rotation (optional). Defaults to clockwise but can be set to anticlockwise. This
  refers to the direction in which we will progress through the sequence of moves (the compass) where
  clockwise means traversing right and anticlockwise is traversing left. When we reach the end of
  the sequence we continue at the beginning, i.e. after NW comes N.

If a start direction and rotation are not given as input to the algorithm then the algorithm will pick
the direction/rotation combination from previous runs that has the fewest iterations for the given
tile. Once a start direction and rotation has been chosen for the tile the algorithm proceeds as follows:

1. Make a first move in the start direction if that move is valid. If that move is not
   valid then pick the first valid move in the sequence of moves in the given rotation (clockwise/anticlockwise).
2. After the first move, continue in the same direction as the previous move if that move
   is valid. If that move is not valid then pick the first valid move from the sequence
   in the rotation order.

A move is valid (allowed) if the destination tile is on the board and has not been visited before.

We keep track of visited tiles as an array of tiles where each tile has an x and a y coordinate.
If the number of tiles reaches 100 then we are done. If we don't find a 100 tiles and thenumber of moves (iterations) reaches a certain limit (i.e. 1 million) then we give up and the algorithm terminates.

Here is an example to illustrate how the algorithm works. Suppose that we start at
the tile 0,0 (x: 0, y: 0) and that we have chosen direction N and rotation clockwise. In the first
move we will attempt to go north, since that move is not valid we try the next move in the
sequence which is NE (traversing right, i.e. clockwise). Since that move is also not valid
we will move east (E). In the next move we will attempt to continue with the previous move.
When that move is no longer valid we will try SE, S, SW, and so on...

## How to run

JavaScript implementation:

```
bin/run-js
```

Scala implementation:

```
bin/run all N clockwise
```

The scala implementation does not have the ability to select direction/rotation
based on previous results so those have to be given as input.

The results from the JavaScript implementation are saved in `results.json`.
There is a separate script to sanity check those results:

```
bin/validate-results
```

## Visualization

You can see an animated visualization of a successful path in [index.html](index.html)

## Results

### First Attempt - North Clockwise (35 failures)

```
bin/run-js all n clockwise
```

The north:clockwise algorithm yields the result that 65 tiles terminate within 3
million iterations and 35 do not.

### Start Direction and Symmetry (8 failures)

By using symmetry and varying the direction of moves between north, west, south,
and east, we can find a solution for all tiles except 8. The tiles that don't
terminate with any direction in the first quadrant are the upper-right corner (4,0)
and the tile one north of the lower-left corner (0,3). For the other three quadrants
there are also two tiles that don't terminate that correspond to the tiles in the
first quadrant when you rotate the board 90, 180, and 270 degrees respectively.

### Rotation (0 failures)

By changing the rotation from `clockwise` to `anticlockwise` and choosing
the right start direction we can solve the 8 remaining tiles that were not
solved with clockwise rotation:

```
bin/run-js 4,0 se anticlockwise # => iterations=7235
bin/run-js 0,3 e anticlockwise # => iterations=613890

bin/run-js 9,4 sw anticlockwise # => iterations=7235
bin/run-js 6,0 s anticlockwise # => iterations=613890

bin/run-js 5,9 nw anticlockwise # => iterations=7235
bin/run-js 9,6 w anticlockwise # => iterations=613890

bin/run-js 0,5 ne anticlockwise # => iterations=7235
bin/run-js 3,9 n anticlockwise # => iterations=613890
```
