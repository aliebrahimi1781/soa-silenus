package es.silenus.soa.jbpm4;

import org.jbpm.pvm.internal.type.Converter;

/**
 * String to character converter.
 *
 * @author Mariano Alonso
 * @since 11-nov-2009 12:15:10
 */
public class StringToCharacterConverter implements Converter {
	/**
	 * is true if this converter supports the given type, false otherwise.
	 */
	@Override
	public boolean supports(Object value) {
		return value != null && (value instanceof String);
	}

	/**
	 * converts a given object to its persistable format.
	 */
	@Override
	public Object convert(Object o) {
		if(o != null) {
			return ((String)o).toCharArray();
		}
		return null;
	}

	/**
	 * reverts a persisted object to its original formResourceName.
	 */
	@Override
	public Object revert(Object o) {
		if(o != null) {
			return new String((char[])o);
		}
		return null;
	}
}
