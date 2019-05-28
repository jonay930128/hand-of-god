package wrx.xing.hand.of.god.servlet;

import com.alibaba.fastjson.JSON;
import wrx.xing.hand.of.god.constant.Constant;
import wrx.xing.hand.of.god.response.Result;
import wrx.xing.hand.of.god.util.DataSourceUtil;
import wrx.xing.hand.of.god.util.RedisUtil;
import wrx.xing.hand.of.god.util.Utils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 请填写类的描述
 *
 * @author wangruxing
 * @date 2019-05-15 19:56
 */
@Slf4j
public class HandServlet extends HttpServlet {
	/**
	 * session key
	 */
	private static final String SESSION_USER_KEY = "hand_of_god";
	/**
	 * 用户名key
	 */
	private static final String PARAM_NAME_USERNAME = "loginUsername";
	/**
	 * 密码key
	 */
	private static final String PARAM_NAME_PASSWORD = "loginPassword";
	/**
	 * 账号
	 */
	private String username = null;
	/**
	 * 密码
	 */
	private String password = null;
	/**
	 * servletContext
	 */
	private ServletContext servletContext;

	@Override
	public void init(ServletConfig config)  {
		log.info("HandServlet->init,start");
		username = config.getInitParameter(PARAM_NAME_USERNAME);
		password = config.getInitParameter(PARAM_NAME_PASSWORD);

		servletContext = config.getServletContext();
		log.info("HandServlet->init finish");
	}


	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		String requestURI = request.getRequestURI();

		response.setCharacterEncoding("utf-8");

		// root context
		if (contextPath == null) {
			contextPath = "";
		}
		String uri = contextPath + servletPath;
		String path = requestURI.substring(contextPath.length() + servletPath.length());

		// 登录请求
		if (Objects.equals(Constant.LOGIN_PATH,path)) {
			String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
			String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
			if (username.equals(usernameParam) && password.equals(passwordParam)) {
				request.getSession().setAttribute(SESSION_USER_KEY, username);
				response.getWriter().print(JSON.toJSONString(Result.buildSuccess("success")));
			} else {
				response.getWriter().print(JSON.toJSONString(Result.buildFail("用户名或密码错误")));
			}
			return;
		}

		// 是否需要登录
		if (needLogin(request,path)) {
			if ("".equals(contextPath) || Constant.ROOT_PATH.equals(contextPath)) {
				response.sendRedirect(Constant.LOGIN_HTML_PATH);
			} else {
				if ("".equals(path)) {
					response.sendRedirect(Constant.LOGIN_HTML_PATH);
				} else {
					response.sendRedirect("login.html");
				}
			}
			return;
		}

		// 请求处理
		Result result = requestHandler(path,request);

		if (null != result) {
			response.getWriter().print(JSON.toJSONString(result));
		}else {
			// find file in resources path
			returnResourceFile(path, uri, response);
		}
	}

	/**
	 * 自定义请求处理
	 * @param path 请求路径
	 * @param request request
	 * @return 返回处理结果
	 */
	private Result requestHandler(String path, HttpServletRequest request) {
		String initMenu = "/initMenu";
		String dbOption = "/dbOption";
		if (initMenu.equals(path)) {
			Map<String,Boolean> map = new HashMap<>(2);
			map.put("haveDataSource",DataSourceUtil.haveDataSource(servletContext));
			map.put("haveRedis",RedisUtil.haveRedis(servletContext));
			return Result.buildSuccess(map);
		}else if (dbOption.equals(path)) {
			return DataSourceUtil.dbOption(request);
		}
		return null;
	}

	/**
	 * 是否需要登录
	 * @return true/false
	 */
	private boolean needLogin(HttpServletRequest request,String path) {
		return isRequireAuth()
				&& !alreadyLogin(request)
				&& !checkLoginParam(request)
				&& !("/login.html".equals(path)
				|| path.startsWith("/css")
				|| path.startsWith("/js")
				|| path.startsWith("/img"));
	}

	/**
	 * 登录参数校验
	 * @param request request
	 * @return true/false
	 */
	private boolean checkLoginParam(HttpServletRequest request) {
		String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
		String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
		if(null == username || null == password){
			return false;
		}
		return username.equals(usernameParam) && password.equals(passwordParam);
	}

	/**
	 * 是否需要权限校验
	 * @return true/false
	 */
	private boolean isRequireAuth() {
		return this.username != null;
	}

	/**
	 * 是否已经登录
	 * @param request request
	 * @return true/false
	 */
	private boolean alreadyLogin(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null && session.getAttribute(SESSION_USER_KEY) != null;
	}

	/**
	 * 解析文件
	 * @param fileName 视图文件名
	 * @param uri 路径
	 * @param response 响应
	 * @throws IOException IO异常
	 */
	private void returnResourceFile(String fileName, String uri, HttpServletResponse response) throws IOException {

		String filePath = getFilePath(fileName);

		if (filePath.endsWith(Constant.HTML_SUFFIX)) {
			response.setContentType("text/html; charset=utf-8");
		}
		if (fileName.endsWith(Constant.JPG_SUFFIX)) {
			byte[] bytes = Utils.readByteArrayFromResource(filePath);
			if (bytes != null) {
				response.getOutputStream().write(bytes);
			}

			return;
		}

		String text = Utils.readFromResource(filePath);
		if (text == null) {
			response.sendRedirect(uri + "/login.html");
			return;
		}
		if (fileName.endsWith(Constant.CSS_SUFFIX)) {
			response.setContentType("text/css;charset=utf-8");
		} else if (fileName.endsWith(Constant.JS_SUFFIX)) {
			response.setContentType("text/javascript;charset=utf-8");
		}
		response.getWriter().write(text);
	}

	/**
	 * 拼接全路径
	 * @param fileName 视图文件名
	 * @return 返回全路径
	 */
	private String getFilePath(String fileName) {
		return Constant.RESOURCE_PATH + fileName;
	}

}
