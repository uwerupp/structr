/**
 * Copyright (C) 2010-2013 Axel Morgner, structr <structr@structr.org>
 *
 * This file is part of structr <http://structr.org>.
 *
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.structr.core.notion;

import java.util.Collections;
import org.structr.core.property.PropertyKey;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.Adapter;
import org.structr.core.EntityContext;
import org.structr.core.GraphObject;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.structr.core.converter.PropertyConverter;

//~--- classes ----------------------------------------------------------------

/**
 * Combines a serialization strategy and a deserialization strategy to form a
 * notion of an object. A notion in this context is a viewer-specific selection
 * of properties that can be configured separately for each entity via
 * {@link EntityContext}. You can for example configure the User entity to
 * return only its name when referenced from a Folder entity, but to return the
 * whole object when referenced from a Group entity.
 *
 * @author Christian Morgner
 */
public abstract class Notion<S extends GraphObject, T> {

	private static final Logger logger = Logger.getLogger(Notion.class.getName());
	
	protected DeserializationStrategy<T, S> deserializationStrategy = null;
	protected SerializationStrategy<S, T> serializationStrategy     = null;
	protected SecurityContext securityContext                       = null;
	protected String idProperty                                     = null;
	protected Class<S> type                                         = null;

	//~--- constructors ---------------------------------------------------

	public Notion(SerializationStrategy serializationStrategy, DeserializationStrategy deserializationStrategy) {
		
		this.serializationStrategy   = serializationStrategy;
		this.deserializationStrategy = deserializationStrategy;
	}

	//~--- get methods ----------------------------------------------------

	/**
	 * Returns the property key that will be used to de-serialize objects
	 * with this notion, or null if this notion can not deserialize objects
	 * with a single key.
	 *
	 * @return the primary key property
	 */
	public abstract PropertyKey getPrimaryPropertyKey();

	public Adapter<S, T> getAdapterForGetter(final SecurityContext securityContext) {

		this.securityContext = securityContext;

		return new Adapter<S, T>() {

			@Override
			public T adapt(S s) throws FrameworkException {
				return serializationStrategy.serialize(securityContext, type, s);
			}
		};
	}

	public Adapter<T, S> getAdapterForSetter(final SecurityContext securityContext) {

		return new Adapter<T, S>() {

			@Override
			public S adapt(T s) throws FrameworkException {
				return deserializationStrategy.deserialize(securityContext, type, s);
			}
		};
	}

	public Adapter<List<S>, List<T>> getCollectionAdapterForGetter(final SecurityContext securityContext) {

		return new Adapter<List<S>, List<T>>() {

			@Override
			public List<T> adapt(List<S> s) throws FrameworkException {

				List<T> list = new LinkedList<T>();

				for (S o : s) {

					list.add(serializationStrategy.serialize(securityContext, type, o));

				}

				return list;
			}
		};
	}

	public Adapter<List<T>, List<S>> getCollectionAdapterForSetter(final SecurityContext securityContext) {

		return new Adapter<List<T>, List<S>>() {

			@Override
			public List<S> adapt(List<T> s) throws FrameworkException {
				
				if (s == null) {
					return Collections.EMPTY_LIST;
				}

				List<S> list = new LinkedList<S>();
				for (T t : s) {

					list.add(deserializationStrategy.deserialize(securityContext, type, t));

				}

				return list;
			}
		};
	}

	public PropertyConverter<T, S> getEntityConverter(SecurityContext securityContext) {
		
		return new PropertyConverter<T, S>(securityContext, null) {

			@Override
			public T revert(S source) throws FrameworkException {
				return getAdapterForGetter(securityContext).adapt(source);
			}

			@Override
			public S convert(T source) throws FrameworkException {
				return getAdapterForSetter(securityContext).adapt(source);
			}
		};
		
	}

	public PropertyConverter<List<T>, List<S>> getCollectionConverter(SecurityContext securityContext) {
		
		return new PropertyConverter<List<T>, List<S>>(securityContext, null) {

			@Override
			public List<T> revert(List<S> source) throws FrameworkException {
				return getCollectionAdapterForGetter(securityContext).adapt(source);
			}

			@Override
			public List<S> convert(List<T> source) throws FrameworkException {
				return getCollectionAdapterForSetter(securityContext).adapt(source);
			}
		};
		
	}
	
	//~--- set methods ----------------------------------------------------

	public void setType(Class<S> type) {
		this.type = type;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}
	
	public static <S, T> List<T> convertList(List<S> source, Adapter<S, T> adapter) {
		
		List<T> result = new LinkedList<T>();
		for(S s : source) {
	
			try {
				result.add(adapter.adapt(s));
				
			} catch(FrameworkException fex) {
				logger.log(Level.WARNING, "Error in iterable adapter", fex);
			}
	}
		
		return result;
	}
}
