package wrx.xing.hand.of.god.util;

import wrx.xing.hand.of.god.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 请填写类的描述
 *
 * @author wangruxing
 * @date 2019-05-28 17:12
 */
@Slf4j
public class DataSourceUtil {
	/**
	 * 数据源
	 */
	private static DataSource dataSource;

	/**
	 * 是否有数据源
	 * @param servletContext Servlet容器
	 * @return 返回
	 */
	public static boolean haveDataSource(ServletContext servletContext) {
		if (null == dataSource) {
			WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			dataSource = webApplicationContext.getBean(DataSource.class);
			return null != dataSource;
		}
		return true;
	}

	/**
	 * 数据库操作
	 * @param request request
	 * @return 返回结果
	 */
	public static Result dbOption(HttpServletRequest request) {
		String sqlText = request.getParameter("sqlText");
		String msg = checkSql(sqlText);
		if (null != msg) {
			return Result.buildFail(msg);
		}
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.execute(sqlText);
			return Result.buildSuccess(statement.getUpdateCount());
		} catch (SQLException e) {
			log.error("DataSourceUtil->dbOption,error",e);
			return Result.buildFail(e.getMessage());
		}finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("DataSourceUtil->dbOption,error",e);
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("DataSourceUtil->dbOption,error",e);
				}
			}
		}
	}

	/**
	 * 检查sql
	 * @param sqlText sql
	 * @return 检查结果
	 */
	private static String checkSql(String sqlText) {
		String empty = "null";
		String where = "where";
		String select = "select";
		String insert = "insert";
		if (null == sqlText || "".equals(sqlText) || empty.equalsIgnoreCase(sqlText)) {
			return "sql不能为空";
		}
		if (!sqlText.toLowerCase().contains(where) && !sqlText.toLowerCase().contains(insert)) {
			return "sql必须要where条件";
		}
		if (sqlText.toLowerCase().contains(select)) {
			return "不支持查询";
		}
		return null;
	}
}
