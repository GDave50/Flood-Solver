/**
 * Flood is a game in which the player is given a grid of randomly-
 * colored squares. The goal is to get the entire board to be one
 * color. This is done by flood-filling the top left corner with a
 * color, in which every square orthogonally connected by the same
 * color gets turned to the new color.
 * 
 * This program is an extension of the game in that it features a
 * parallel state space problem solver which attempts to find the
 * best solution for a given board.
 * 
 * Additionally, this program features a cluster-parallel state
 * space problem solver, which means some number of remote computers
 * help find a best solution. This is certainly more effective than
 * simply using one computer.
 * 
 * @author Gage Davidson
 */
