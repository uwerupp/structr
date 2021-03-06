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
package org.structr.core.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.auth.exception.AuthenticationException;
import org.structr.core.entity.Principal;
import org.structr.core.entity.ResourceAccess;

/**
 * An authenticator interface that defines how the system can obtain a principal
 * from a HttpServletRequest.
 *
 * @author Christian Morgner
 * @author Axel Morgner
 */
public interface Authenticator {
	
	/*
	 * Indicate that the authenticator has already examined the request
	 */
	public boolean hasExaminedRequest();

	/**
	 * 
	 * @param securityContext the security context
	 * @param request
	 * @throws FrameworkException 
	 */
	public void initializeAndExamineRequest(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) throws FrameworkException;
	
	/**
	 * 
	 * @param securityContext the security context
	 * @param request
	 * @throws FrameworkException 
	 */
	public void examineRequest(SecurityContext securityContext, HttpServletRequest request, final String resourceSignature, final ResourceAccess resourceAccess, final String propertyView) throws FrameworkException;
	
	/**
	 *
	 * Tries to authenticate the given HttpServletRequest.
	 *
	 * @param securityContext the security context
	 * @param request the request to authenticate
	 * @param response the response
	 * @param emailOrUsername the (optional) email/username
	 * @param password the (optional) password
	 * 
	 * @return the user that was just logged in
	 * @throws AuthenticationException
	 */
	public Principal doLogin(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response, final String emailOrUsername, final String password) throws AuthenticationException;

	/**
	 * Logs the given request out.
	 *
	 * @param securityContext the security context
	 * @param request the request to log out
	 * @param response the response
	 */
	public void doLogout(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Returns the user that is currently logged into the system,
	 * or null if the session is not authenticated.
	 *
	 * @param securityContext the security context
	 * @param request the request
	 * @param response the response
	 * @param tryLogin if true, try to login the user
	 * @return the logged-in user or null
	 * @throws FrameworkException
	 */
	public Principal getUser(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response, final boolean tryLogin) throws FrameworkException;
}
