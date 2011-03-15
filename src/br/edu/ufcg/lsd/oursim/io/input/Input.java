package br.edu.ufcg.lsd.oursim.io.input;

import java.io.Closeable;

/**
 * 
 * Represents an input to the simulation.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 * @param <T>
 *            The type of input to be managed.
 */
public interface Input<T> extends Closeable {

	/**
	 * Retrieves, but does not remove, the head (first element) of this Input.
	 * 
	 * @return the head of this list, or <tt>null</tt> if this Input is empty.
	 */
	T peek();

	/**
	 * Retrieves and removes the head (first element) of this Input.
	 * 
	 * @return the head of this list, or <tt>null</tt> if this Input is empty
	 */
	T poll();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	void close();
	
	void stop();

}
