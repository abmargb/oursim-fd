package br.edu.ufcg.lsd.oursim.entities;

/**
 * 
 * A file to be used as input, output or executable in a task.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class File {

	/**
	 * The name of the file, actually this could represent an path.
	 */
	private String name;

	/**
	 * Size in bytes of this File.
	 */
	private long size;

	/**
	 * An ordinary constructor for a file.
	 * 
	 * @param name
	 *            The name of the file, actually this could represent an path.
	 * @param size
	 *            Size in bytes of this File.
	 */
	public File(String name, long size) {
		this.name = name;
		this.size = size;
	}

	/**
	 * @return The name of the file, actually this could represent an path.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the size in bytes of this File.
	 */
	public long getSize() {
		return size;
	}
	
	@Override
	public String toString(){
		return name;
	}

}
