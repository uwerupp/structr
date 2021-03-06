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
package org.structr.rest.entity;

import java.util.Date;
import org.structr.common.PropertyView;
import org.structr.common.View;
import org.structr.core.EntityContext;
import org.structr.core.property.Property;
import org.structr.core.entity.AbstractNode;
import org.structr.core.graph.NodeService.NodeIndex;
import org.structr.core.property.*;

/**
 *
 * @author Christian Morgner
 */
public class TestThree extends AbstractNode {
	
	public static final Property<String[]>      stringArrayProperty = new ArrayProperty<String>("stringArrayProperty", String.class);
	public static final Property<Boolean>       booleanProperty     = new BooleanProperty("booleanProperty");
	public static final Property<Double>        doubleProperty      = new DoubleProperty("doubleProperty");
	public static final Property<Integer>       integerProperty     = new IntProperty("integerProperty");
	public static final Property<Long>          longProperty        = new LongProperty("longProperty");
	public static final Property<String>        stringProperty      = new StringProperty("stringProperty");
	public static final Property<Date>          dateProperty        = new ISO8601DateProperty("dateProperty");
	
	public static final View publicView = new View(TestThree.class, PropertyView.Public,
		stringArrayProperty, booleanProperty, doubleProperty, integerProperty, longProperty, stringProperty, dateProperty
	);
	
	static {
		
		EntityContext.registerSearchablePropertySet(TestThree.class, NodeIndex.fulltext.name(), publicView.properties());
		EntityContext.registerSearchablePropertySet(TestThree.class, NodeIndex.keyword.name(),  publicView.properties());
	}
}
