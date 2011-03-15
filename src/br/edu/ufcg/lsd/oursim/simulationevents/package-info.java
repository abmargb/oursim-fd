/**
 * Contains the events that drive the simulation. 
 * 
 * The method {@link TimedEvent#action()} of each event is this package and its subpackages is intended to perform 
 * some action specifically related with its equivalent class in package {@link br.edu.ufcg.lsd.oursim.entities} 
 * and dispatch the appropriate event of the package {@link br.edu.ufcg.lsd.oursim.dispatchableevents} that will
 * be treaded by all the registered listeners. 
 */
package br.edu.ufcg.lsd.oursim.simulationevents;