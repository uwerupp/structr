/*
 *  Copyright (C) 2011 Axel Morgner
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

package org.structr.rest.resource;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.Services;
import org.structr.core.entity.AbstractNode;
import org.structr.core.node.search.Search;
import org.structr.core.node.search.SearchAttribute;
import org.structr.core.node.search.SearchNodeCommand;
import org.structr.rest.exception.NotFoundException;

/**
 * Represents an exact UUID match and behaves like an {@see IdResource} otherwise.
 *
 * @author Christian Morgner
 */
public class UuidResource extends IdResource {

	private static final Logger logger = Logger.getLogger(UuidResource.class.getName());

	private String uuid = null;

	@Override
	public AbstractNode getNode() throws FrameworkException {

		List<SearchAttribute> attrs = new LinkedList<SearchAttribute>();
		attrs.add(Search.andExactUuid(uuid));

		List<AbstractNode> results = (List<AbstractNode>)Services.command(securityContext, SearchNodeCommand.class).execute(
			null, false, false, attrs
		);

		int size = results.size();

		switch(size) {
			case 0:
				throw new NotFoundException();

			case 1:
				return results.get(0);

			default:
				logger.log(Level.WARNING, "Got more than one result for UUID {0}, this is very likely to be a UUID collision!", uuid);
				return results.get(0);
		}
	}

	@Override
	public boolean checkAndConfigure(String part, SecurityContext securityContext, HttpServletRequest request) {

		this.securityContext = securityContext;

		this.setUuid(part);
		return true;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getUriPart() {
		return uuid;
	}
}