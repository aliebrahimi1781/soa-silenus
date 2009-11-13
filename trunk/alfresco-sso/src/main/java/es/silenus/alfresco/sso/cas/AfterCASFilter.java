/*
 * Copyright (c) 2009. Silenus Consultoria, S.L.
 */
package es.silenus.alfresco.sso.cas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.config.ConfigService;
import org.alfresco.web.config.LanguagesConfigElement;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.AuthenticationHelper;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.LoginBean;
import org.alfresco.model.ContentModel;
import org.alfresco.i18n.I18NUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.util.*;


/**
 * Filter specific for Alfresco to perform after CAS filter operations.
 *
 * @author Mariano Alonso
 * @since 19-sep-2009 20:53:12
 */
public class AfterCASFilter implements Filter {
	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AfterCASFilter.class);

	/**
	 * The locale.
	 */
	public static final String LOCALE = "locale";

	/**
	 * The message bundle.
	 */
	public static final String MESSAGE_BUNDLE = "alfresco.messages.webclient";

	/**
	 * CAS Filter user.
	 */
	public final static String CAS_FILTER_USER = "edu.yale.its.tp.cas.client.filter.user";

	/**
	 * Servlet context.
	 */
	private ServletContext context;

	/**
	 * The alfresco login url.
	 */
	private String[] loginURL;

	/**
	 * URL of the application where the user lands once successfully logged in.
	 */
	private String initialURL;

	/**
	 * URL of the application to perform logout.
	 */
	private String[] logoutURL;

	/**
	 * CAS logout URL.
	 */
	private String casLogoutURL;

	/**
	 * The authentication component.
	 */
	private AuthenticationComponent authComponent;

	/**
	 * The authentication service.
	 */
	private AuthenticationService authService;

	/**
	 * The transaction service.
	 */
	private TransactionService transactionService;

	/**
	 * The person service.
	 */
	private PersonService personService;

	/**
	 * The node service.
	 */
	private NodeService nodeService;

	/**
	 * The locales.
	 */
	private Map<String, Locale> locales;

	/**
	 * Initializes the filter.
	 *
	 * @param config the configuration.
	 * @throws ServletException if something goes wrong.
	 */
	public void init(final FilterConfig config) throws ServletException {
		this.context = config.getServletContext();

		// Initialize parameters
		String temp = config.getInitParameter("loginURL");
		this.loginURL = temp != null ? temp.split(",") : new String[0];

		this.initialURL = config.getInitParameter("initialURL");

		temp = config.getInitParameter("logoutURL");
		this.logoutURL = temp != null ? temp.split(",") : new String[0];

		this.casLogoutURL = config.getInitParameter("casLogoutURL");

		if(initialURL == null) {
			throw new ServletException("initialURL parameter cannot be null");
		}
		if(casLogoutURL == null) {
			throw new ServletException("casLogoutURL parameter cannot be null");
		}

		// Fetch spring dependencies
		final WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

		ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);

		transactionService = serviceRegistry.getTransactionService();
		nodeService = serviceRegistry.getNodeService();
		authComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");
		authService = serviceRegistry.getAuthenticationService();
		personService = serviceRegistry.getPersonService();

		// Get a list of the available locales
		ConfigService configServiceService = (ConfigService) ctx.getBean("webClientConfigService");
		LanguagesConfigElement configElement = (LanguagesConfigElement) configServiceService.getConfig("Languages").getConfigElement(LanguagesConfigElement.CONFIG_ELEMENT_ID);

		locales = new HashMap<String, Locale>();
		Locale locale;
		for(String language: configElement.getLanguages()) {
			locale = parseLocale(language);
			if(locale != null) {
				locales.put(locale.getLanguage(), locale);
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Initialized filter %s", toString()));
		}
	}

	private static Locale parseLocale(final String language) {
		String[] parts = language.split("_");

		switch(parts.length) {
			case 1:
				return new Locale(parts[0]);

			case 2:
				return new Locale(parts[0], parts[1]);

			case 3:
				return new Locale(parts[0], parts[1], parts[2]);
		}

		return null;
	}


	/**
	 * Filters the request.
	 *
	 * @param request the request.
	 * @param response the response.
	 * @param chain the filter chain.
	 * @throws ServletException if something goes wrong.
	 * @throws IOException if an I/O error occurs.
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}


	/**
	 * Filters the request.
	 *
	 * @param request the request.
	 * @param response the response.
	 * @param chain the filter chain.
	 * @throws ServletException if something goes wrong.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
			throws ServletException, IOException {

		final HttpSession session = request.getSession(true);

		final String contextPath = request.getContextPath();
		final String requestURI = request.getRequestURI();

		final String path = requestURI.substring(contextPath.length());


		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Filtering request [%s] - path [%s] ...", requestURI, path));
		}


		// Detect logout link
		final String actParam = getActParameter(request);
		if(actParam != null) {
			final String actValue = request.getParameter(actParam);
			if(actValue.endsWith(":logout")) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Detected logout link pressed. We will disconnect the internal session and perform a CAS logout");
				}
				onLogout(request, response);
				return;
			}
		}

		// If request is for logout...
		if(isLogoutURL(path)) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Detected logout URL. We will disconnect the internal session and perform a CAS logout");
			}
			onLogout(request, response);
			return;
		}


		// If request is for login
		if (isLoginURL(path)) {
			onLogin(request, response);
			return;
		}


		final String userName = (String)session.getAttribute(CAS_FILTER_USER);
		User user = (User) session.getAttribute(AuthenticationHelper.AUTHENTICATION_USER);

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("After internal CAS processing for user [%s]", userName));
		}

		if(userName != null) {

			// See if there is a user in the session and test if it matches
			if(user != null && !user.getUserName().equals(userName)) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(String.format("Alfresco session user is [%s] but CAS says [%s]... invalidating session", user.getUserName(), userName));
				}
				onLogout(request, response);
				return;
			}


			user = setAuthenticatedUser(request, response, userName);

			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Authenticated user [%s]", user.getUserName()));
			}


			final FakeTicketRequest fakeRequest = new FakeTicketRequest(request, user);
			fakeRequest.setExposeTicket(false);
			// Continue
			chain.doFilter(fakeRequest, response);

		} else {

			if(LOG.isDebugEnabled()) {
				LOG.debug("No user detected, redirecting to CAS...");
			}
			onLogout(request, response);

		}

	}

	private String getActParameter(final HttpServletRequest request) {
		Enumeration params = request.getParameterNames();
		String param;
		while(params.hasMoreElements()) {
			param = (String)params.nextElement();
			if(param.endsWith(":act")) {
				return param;
			}
		}
		return null;
	}


	private boolean isLogoutURL(final String url) {
		if(url != null) {
			for(String aLogoutURL: logoutURL) {
				if(url.endsWith(aLogoutURL)) {
					return true;
				}
			}
		}
		return false;
	}


	private boolean isLoginURL(final String url) {
		if(url != null) {
			for(String aLoginURL: loginURL) {
				if(url.endsWith(aLoginURL)) {
					return true;
				}
			}
		}
		return false;
	}

	private void onLogout(final HttpServletRequest request,
												final HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);

		session.invalidate();

		response.sendRedirect(casLogoutURL);
	}


	private void onLogin(final HttpServletRequest request,
											 final HttpServletResponse response) throws IOException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Detected login URL. We will redirect the user to the initial URL.");
		}

		final String contextPath = request.getContextPath();

		response.sendRedirect(contextPath + initialURL);
	}


	/**
	 * Set the authenticated user.
	 *
	 * It does not check that the user exists at the moment.
	 *
	 * @param request the request.
	 * @param response the response.
	 * @param userName the user name.
	 * @return the user.
	 */
	private User setAuthenticatedUser(final HttpServletRequest request,
																		final HttpServletResponse response,
																		final String userName) {
		// Ensure a faces context exists to be able to set the locale later
		FacesHelper.getFacesContext(request, response, context);

		final HttpSession session = request.getSession(true);

		// Set the authentication
		authComponent.clearCurrentSecurityContext();
		authComponent.setCurrentUser(userName);


		// Set up the user information
		UserTransaction tx = transactionService.getUserTransaction();
		NodeRef homeSpaceRef;
		User user;

		try {
			tx.begin();

			user = new User(userName, authService.getCurrentTicket(), personService.getPerson(userName));

			homeSpaceRef = (NodeRef) nodeService.getProperty(personService.getPerson(userName), ContentModel.PROP_HOMEFOLDER);
			if(homeSpaceRef == null) {
				LOG.warn(String.format("Home Folder is null for user '%s', using company_home.", userName));
				homeSpaceRef = nodeService.getRootNode(Repository.getStoreRef());
			}

			user.setHomeSpaceId(homeSpaceRef.getId());

			tx.commit();
		} catch (Throwable ex) {
			LOG.error(String.format("Error authenticating user: \"%s\"", userName), ex);

			try {
				tx.rollback();
			} catch (Exception ex2) {
				LOG.error("Failed to rollback transaction", ex2);
			}

			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			} else {
				throw new RuntimeException(String.format("Failed to set authenticated user: \"%s\"", userName), ex);
			}
		}

		// Store the user
		session.setAttribute(AuthenticationHelper.AUTHENTICATION_USER, user);
		session.setAttribute(LoginBean.LOGIN_EXTERNAL_AUTH, Boolean.TRUE);

		// Determine locale
		Locale userLocale = null;

		// First obey the request parameter "lang"
		String language = request.getParameter("lang");
		if(language != null && language.length() > 0) {
			userLocale = parseLocale(language);
		}

		// Then retrieve user language
		if(userLocale == null) {
			userLocale = Application.getLanguage(session);
		}
		// Fallback to browser locale
		if(userLocale == null) {

			Locale locale = request.getLocale();
			if(locale != null) {
				userLocale = locales.get(locale.getLanguage());
			}
			if(userLocale == null) {
				userLocale = Locale.getDefault();
			}
		}

		// Set the locale using the session
		I18NUtil.setLocale(userLocale);
		session.setAttribute(LOCALE, userLocale);
		session.removeAttribute(MESSAGE_BUNDLE);

		return user;
	}

	/**
	 * Destroys the filter.
	 */
	public void destroy() {
	}

	/**
	 * Retrieves a string representation of this object.
	 *
	 * @return a string representation of this object.
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder("AfterCASFilter { ");

		builder.append("loginURL=").append(loginURL != null ? Arrays.asList(loginURL) : "");
		builder.append(", initialURL=").append(initialURL);
		builder.append(", logoutURL=").append(logoutURL);
		builder.append(", casLogoutURL=").append(casLogoutURL);

		builder.append("}");

		return builder.toString();
	}

	/**
	 * Fake ticket request.
	 */
	private static class FakeTicketRequest extends HttpServletRequestWrapper {

		/**
		 * The user.
		 */
		private final User user;

		/**
		 * Expose ticket flag.
		 */
		private boolean exposeTicket;

		/**
		 * Constructor.
		 *
		 * @param httpServletRequest the http servlet request.
		 * @param user the user.
		 */
		public FakeTicketRequest(final HttpServletRequest httpServletRequest, final User user) {
			super(httpServletRequest);
			this.user = user;
			exposeTicket = true;
		}

		/**
		 * Sets the expose ticket flag.
		 *
		 * @param exposeTicket the expose ticket flag.
		 */
		public void setExposeTicket(boolean exposeTicket) {
			this.exposeTicket = exposeTicket;
		}

		/**
		 * Returns the value of a request parameter as a <code>String</code>,
		 * or <code>null</code> if the parameter does not exist. Request parameters
		 * are extra information sent with the request.  For HTTP servlets,
		 * parameters are contained in the query string or posted form data.
		 *
		 * <p>You should only use this method when you are sure the
		 * parameter has only one value. If the parameter might have
		 * more than one value, use {@link #getParameterValues}.
		 *
		 * <p>If you use this method with a multivalued
		 * parameter, the value returned is equal to the first value
		 * in the array returned by <code>getParameterValues</code>.
		 *
		 * <p>If the parameter data was sent in the request body, such as occurs
		 * with an HTTP POST request, then reading the body directly via {@link
		 * #getInputStream} or {@link #getReader} can interfere
		 * with the execution of this method.
		 *
		 * @param name a <code>String</code> specifying the
		 * name of the parameter
		 *
		 * @return a <code>String</code> representing the
		 * single value of the parameter
		 *
		 * @see #getParameterValues
		 */
		public java.lang.String getParameter(final String name) {
			if("ticket".equals(name)) {
				if(exposeTicket) {
					return user.getTicket();
				} else {
					return null;
				}
			}
			return super.getParameter(name);
		}

		/**
		 * Returns a java.util.Map of the parameters of this request.
		 * Request parameters are extra information sent with the request.
		 * For HTTP servlets, parameters are contained in the query
		 * string or posted form data.
		 *
		 * @return an immutable java.util.Map containing parameter names as
		 * keys and parameter values as map values. The keys in the parameter
		 * map are of type String. The values in the parameter map are of type
		 * String array.
		 */
		@SuppressWarnings({"unchecked"})
		public Map getParameterMap() {
			Map paramMap = new HashMap();
			paramMap.putAll(super.getParameterMap());
			if(exposeTicket) {
				paramMap.put("ticket", user.getTicket());
			} else {
				paramMap.remove("ticket");
			}
			return paramMap;
		}

		/**
		 * Returns an array of <code>String</code> objects containing
		 * all of the values the given request parameter has, or
		 * <code>null</code> if the parameter does not exist.
		 *
		 * <p>If the parameter has a single value, the array has a length
		 * of 1.
		 *
		 * @param name a <code>String</code> containing the name of
		 * the parameter whose value is requested
		 *
		 * @return an array of <code>String</code> objects
		 * containing the parameter's values
		 *
		 * @see #getParameter
		 */
		public String[] getParameterValues(final String name) {
			if("ticket".equals(name)) {
				if(exposeTicket) {
					return new String[] { user.getTicket() };
				} else {
					return null;
				}
			}
			return super.getParameterValues(name);
		}


	}
}
