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


package org.structr.rest.resource;

import org.structr.core.Result;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.Services;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.AbstractRelationship;
import org.structr.core.graph.search.Search;
import org.structr.core.graph.search.SearchAttribute;
import org.structr.core.graph.search.SearchNodeCommand;
import org.structr.core.graph.search.SearchRelationshipCommand;
import org.structr.rest.exception.NotFoundException;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import org.structr.core.property.PropertyKey;
import org.structr.rest.exception.IllegalPathException;
import org.structr.rest.exception.NotAllowedException;

//~--- classes ----------------------------------------------------------------

/**
 * Represents an exact UUID match.
 *
 * @author Christian Morgner
 */
public class UuidResource extends FilterableResource {

	private static final Logger logger = Logger.getLogger(UuidResource.class.getName());

	//~--- fields ---------------------------------------------------------

	private String uuid = null;

	//~--- methods --------------------------------------------------------

	@Override
	public Result doGet(PropertyKey sortKey, boolean sortDescending, int pageSize, int page, String offsetId) throws FrameworkException {

		GraphObject obj = null;

		// search for node first, then fall back to relationship
		try {

			obj = getNode();

		} catch (NotFoundException nfex1) {

			try {

				obj = getRelationship();

			} catch (NotFoundException nfex2) {}

		}

		if (obj != null) {

			List<GraphObject> results = new LinkedList<GraphObject>();

			results.add(obj);

			return new Result(results, null, isCollectionResource(), isPrimitiveArray());

		}

		throw new NotFoundException();

	}

	@Override
	public boolean checkAndConfigure(String part, SecurityContext securityContext, HttpServletRequest request) {

		this.securityContext = securityContext;

		this.setUuid(part);

		return true;

	}

	@Override
	public Resource tryCombineWith(Resource next) throws FrameworkException {

		// do not allow nesting of "bare" uuid resource with type resource
		// as this will not do what the user expects to do. 
		if(next instanceof TypeResource) {
			
			throw new IllegalPathException();
		}

		return super.tryCombineWith(next);
	}

	//~--- get methods ----------------------------------------------------

	public AbstractNode getNode() throws FrameworkException {

		List<SearchAttribute> attrs = new LinkedList<SearchAttribute>();

		attrs.add(Search.andExactUuid(uuid));

		Result results    = Services.command(SecurityContext.getSuperUserInstance(), SearchNodeCommand.class).execute(attrs);
		int size          = results.size();
		AbstractNode node = null;

		switch (size) {

			case 0 :
				throw new NotFoundException();

			case 1 :
				
				node = (AbstractNode) results.get(0);
				
				if (!securityContext.isReadable(node, true, false)) {
					throw new NotAllowedException();
				}
				
				node.setSecurityContext(securityContext);
				
				return node;

			default :
				logger.log(Level.WARNING, "Got more than one result for UUID {0}, this is very likely to be a UUID collision!", uuid);

				node = (AbstractNode)results.get(0);
				
				node.setSecurityContext(securityContext);
				
				return node;

		}

	}

	public AbstractRelationship getRelationship() throws FrameworkException {

		List<SearchAttribute> attrs = new LinkedList<SearchAttribute>();

		attrs.add(Search.andExactUuid(uuid));

		List<AbstractRelationship> results = Services.command(securityContext, SearchRelationshipCommand.class).execute(attrs);
		int size                           = results.size();

		switch (size) {

			case 0 :
				throw new NotFoundException();

			case 1 :
				return results.get(0);

			default :
				logger.log(Level.WARNING, "Got more than one result for UUID {0}, this is very likely to be a UUID collision!", uuid);

				return results.get(0);

		}

	}

	public String getUuid() {

		return uuid;

	}

	@Override
	public String getUriPart() {

		return uuid;

	}

	@Override
	public String getResourceSignature() {

		return "/";

	}

	@Override
	public boolean isCollectionResource() {

		return false;

	}

	//~--- set methods ----------------------------------------------------

	public void setUuid(String uuid) {

		this.uuid = uuid;

	}

}
