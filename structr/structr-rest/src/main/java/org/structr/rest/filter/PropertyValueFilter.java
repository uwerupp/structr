/*
 *  Copyright (C) 2012 Axel Morgner
 * 
 *  This file is part of structr <http://structr.org>.
 * 
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.structr.rest.filter;

import org.structr.core.BinaryPredicate;
import org.structr.core.GraphObject;
import org.structr.core.Value;

/**
 *
 * @author Christian Morgner
 */
public class PropertyValueFilter<T> implements Filter {

	private BinaryPredicate<T> predicate = null;
	private String propertyKey = null;
	private Value<T> value = null;

	public PropertyValueFilter(String propertyKey, BinaryPredicate<T> predicate, Value<T> value) {
		this.propertyKey = propertyKey;
		this.predicate = predicate;
		this.value = value;
	}

	@Override
	public boolean includeInResultSet(GraphObject object) {
		T t = (T)object.getProperty(propertyKey);
		return predicate.evaluate(t, value.get());
	}
}